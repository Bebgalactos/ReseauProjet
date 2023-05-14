package fr.ul.miage.lutakhato;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;

public class Server {


    private static final Logger LOG = Logger.getLogger(Server.class.getName());
    public static Map<String, ServerObject> database = new HashMap<String, ServerObject>();
    public static void main(String[] args){

    }

    public static String set(String key, Object value, String[] options) {

        String toReturn = null;
        options = purgeBlanks(options);
        int expireMillis = -1;

        try {
            for (int i = 0; i < options.length; i = i + 1) {
                // Options :
                // EX seconds -- Set the specified expire time, in seconds.
                // PX milliseconds -- Set the specified expire time, in milliseconds.
                // NX -- Only set the key if it does not already exist.
                // XX -- Only set the key if it already exists.
                // GET -- Return the old string stored at key, or nil if key did not exist. An error is returned and SET aborted if the value stored at key is not a string.
                String option = options[i];
                switch (option) {
                    case "EX":
                        expireMillis = 1000 * Integer.valueOf(options[i + 1]);
                        break;

                    case "PX":
                        expireMillis = Integer.valueOf(options[i + 1]);
                        break;

                    case "NX":
                        if (exists(new String[]{key}) > 0) {
                            LOG.warning("key already exists");
                            return toReturn;
                        }
                        break;

                    case "XX":
                        if (!(exists(new String[]{key}) > 0)) {
                            LOG.warning("key does not already exists");
                            return toReturn;
                        }
                        break;

                    case "GET":
                        if (exists(new String[]{key}) > 0) {
                            toReturn = String.valueOf(database.get(key).getValue());
                        } else {
                            toReturn = null;
                        }
                        break;

                    default:
                        // do nothing
                        break;
                }
            }
        } catch (Exception e) {
            LOG.warning("malformed expression");
            return toReturn;
        }

        ServerObject serverObject = new ServerObject((int) System.currentTimeMillis(), expireMillis, value);
        database.put(key, serverObject);

        return toReturn;
    }

    public static String[] purgeBlanks(String[] strings) {
        // Créer une liste pour stocker les éléments non vides
        List<String> nonEmptyStrings = new ArrayList<String>();

        // Parcourir chaque élément du tableau
        for (String str : strings) {
            // Vérifier si l'élément est différent de la chaîne de caractères vide
            if (!str.equals("")) {
                // Ajouter l'élément non vide à la liste
                nonEmptyStrings.add(str);
            }
        }

        // Convertir la liste en un nouveau tableau
        String[] result = nonEmptyStrings.toArray(new String[0]);
        return result;
    }

    /* Cette méthode prend en entrée une Map et un tableau de String, et retourne un int qui est le nombre
    de clés qu'on a supprimé
    */
    public static int del(String[] keys) {
        // nbSuccess est initialisée à 0 et c'est le résultat de la méthode
        int nbSuccess = 0;

        // on itère sur le tableau de clés
        for (String key : keys) {
            /*
            Si la clé existe dans la Map,
            elle est supprimée à l'aide de la méthode remove(),
            et la variable res est incrémentée
             */
            if (exists(new String[]{key}) > 0) {
                database.remove(key);
                nbSuccess++;
            }
        }
        return nbSuccess;
    }
    
    /* Cette méthode prend en entrée une Map et un tableau de String, et retourne un int qui est le nombre
    de clés existantes dans la map donné en entrée
    */
    public static int exists(String[] keys) {
        // nbSuccess est initialisée à 0 et c'est le résultat de la méthode
        int nbSuccess = 0;
        boolean found = false;

        // on itère sur le tableau de clés
        for (String key : keys) {
             /*
            Si la clé existe dans la Map et n'est pas expirée,
            la variable nbSuccess est incrémentée
             */
            if (exists(new String[]{key}) > 0) {
                if(database.get(key).getExpire() != -1){
                    if ((System.currentTimeMillis() - database.get(key).getCreationMillis()) > database.get(key).getExpire()) {
                        found = true;
                    } else {
                        database.remove(key);
                    }
                } else {
                    found = true;
                }
            }
            if (found) {
                nbSuccess++;
            }
        }
        return nbSuccess;
    }

    public static int incr(String key) {
        int newValue = 0;
        if (exists(new String[]{key}) > 0) {
            try {
                int oldValue = Integer.valueOf(database.get(key).getValue().toString());
                if(Integer.MAX_VALUE >= oldValue + 1){
                    newValue = oldValue + 1;
                } else {
                    newValue = Integer.MAX_VALUE;
                }
                database.get(key).setValue(newValue);
            } catch (Exception e) {
                System.out.println("On ne peut pas incrémenter sur un String");
            }
        } else {
            ServerObject serverObject = new ServerObject((int) System.currentTimeMillis(), -1, newValue);
            database.put(key, serverObject);
        }
        return newValue;
    }

    public static int decr(String key) {
        int newValue = 0;
        if (exists(new String[]{key}) > 0) {
            try {
                int oldValue = Integer.valueOf(database.get(key).getValue().toString());
                if(Integer.MIN_VALUE <= oldValue - 1){
                    newValue = oldValue - 1;
                } else {
                    newValue = Integer.MIN_VALUE;
                }
                database.get(key).setValue(newValue);
            } catch (Exception e) {
                System.out.println("On ne peut pas incrementer sur une chaine de caracteres");
            }
        } else {
            ServerObject serverObject = new ServerObject((int) System.currentTimeMillis(), -1, newValue);
            database.put(key, serverObject);
        }
        return newValue;
    }

    public static int append(String key, String value) {
        int length = value.length();
        try {
            if(exists(new String[]{key}) > 0){
                String oldValue = String.valueOf(database.get(key).getValue());
                String newValue = oldValue + value;
                database.get(key).setValue(newValue);
            } else {
                length = 0;
                ServerObject serverObject = new ServerObject((int) System.currentTimeMillis(), -1, "");
                database.put(key, serverObject);
            }
        } catch (Exception e) {
            System.out.println("On ne peut pas append sur un nombre entier");
        }
        return length;
    }

}
