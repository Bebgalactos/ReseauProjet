package fr.ul.miage.lutakhato;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.logging.Logger;

public class Server {


    private static final Logger LOG = Logger.getLogger(Server.class.getName());
    private static Map<String, ServerObject> database = new HashMap<String, ServerObject>();
    public static String[] keyWords = new String[]{"APPEND", "DECR", "DEL", "EXISTS", "EXPIRE", "GET", "INCR", "SET"};

    public static void main(String[] args){

    }

    public static boolean syntaxCheck(String entry) {
        boolean result = true;

        // Récupération des mots clés entrés
        String[] parts = entry.split(" ");
        parts = purgeBlanks(parts);

        // Vérification de l'existence de la requête
        boolean requestExists = false;
        int i;
        for(i = 0; i < keyWords.length; i++) {
            if(keyWords[i].equals(parts[0])){
                requestExists = true;
                break;
            }
        }
        if(!requestExists) {
            result = false;
        } else {

            // Vérification des paramètres
            List<String> exceptFirst = new ArrayList<String>();
            for (int n = 1; n < parts.length; n++){
                exceptFirst.add(parts[n]);
            }
            switch(keyWords[i]){
                case "APPEND":
                    result = syntaxCheckAppend(exceptFirst);
                    break;
                case "INCR":
                case "DECR":
                case "GET":
                    result = syntaxCheckIncrDecrGet(exceptFirst);
                    break;
                case "DEL":
                case "EXISTS":
                    result = syntaxCheckDelExists(exceptFirst);
                    break;
                case "SET":
                    result = syntaxCheckSet(exceptFirst);
                    break;
                default:
                    result = false;
                    break;
            }
        }

        return result;
    }



    private static boolean syntaxCheckIncrDecrGet(List<String> array) {
        // Paramètres : String key
        boolean result = false;
        if(array.size() == 1) {
            result = true;
        }
        return result;
    }

    private static boolean syntaxCheckAppend(List<String> array) {
        // Paramètres : String key, String value
        boolean result = false;
        if(array.size() == 2) {
            result = true;
        }
        return result;
    }

    private static boolean syntaxCheckSet(List<String> array) {
        // Paramètres : String key, Object value, String[] options
        boolean result = false;
        if(array.size() > 1) {
            result = true;
        }
        return result;
    }

    private static boolean syntaxCheckDelExists(List<String> array) {
        // Paramètres : String[] keys
        boolean result = false;
        if(array.size() > 0) {
            result = true;
        }
        return result;
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

        ServerObject serverObject = new ServerObject((int) System.currentTimeMillis(), value);
        database.put(key, serverObject);
        expire(key, expireMillis, new int[]{});

        return toReturn;
    }

    /*
    Cette méthode prend en entrée une Map et un tableau de String, et retourne un int qui est le nombre
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
            if (database.containsKey(key)) {
                if(database.get(key).getExpire() != -1){
                    if ((System.currentTimeMillis() - database.get(key).getCreationMillis()) < database.get(key).getExpire()) {
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

    /**
     * Fonction permettant de récupérer une valeur dans la base de données
     * @param key nom de la valeur à récupérer dans la base de données
     * @return la valeur associée à la clé dans la base de données (retourne null si la valeur n'existe pas)
     */
    public Object get(String key){
        Object value = null;
        if(exists(new String[]{key}) > 0){
            value = database.get(key).getValue();
        }
        return value;
    }

    /**
     * Fonction permettant de mettre une "date limite" sur une variable de la base de données
     * @param key nom de la valeur où il faut mettre la "date limite"
     * @param seconds durée avant laquelle la variable sera expirée
     * @param options option de la commande
     *               - 0 : applique la "date limite" seulement si la variable n'en possède pas (option NX)
     *               - 1 : applique la "date limite" uniquement si la variable en possède une (option XX)
     *               - 2 : applique la "date limite" uniquement si la nouvelle est plus grande que celle qui existe
     *               (option GR)
     *               - 3 : applique la "date limite" seulement si la nouvelle est plus petite que celle qui existe
     *               (option LT)
     *               - si le tableau options est vide, se lance sans options
     * @return 1 si l'ajout se passe correctement, 0 sinon
     */
    public static int expire(String key, int seconds, int[] options){
        if(exists(new String[]{key}) > 0){
            int expireMillis = seconds * 1000;
            boolean noProblems = true;
            for (int option : options) {
                switch (option) {
                    case 0:
                        if (!(database.get(key).getExpire() == -1)) {
                            noProblems = false;
                        }
                        break;
                    case 1:
                        if (!(database.get(key).getExpire() != -1)) {
                            noProblems = false;
                        }
                        break;
                    case 2:
                        if (!(database.get(key).getExpire() < expireMillis)) {
                            noProblems = false;
                        }
                        break;
                    case 3:
                        if (!(database.get(key).getExpire() > expireMillis)) {
                            noProblems = false;
                        }
                        break;
                    default:
                        break;
                }
            }
            if(!(expireMillis == -1 || expireMillis > 0)) {
                noProblems = false;
            }
            if(noProblems) {
                database.get(key).setExpire(expireMillis);
                return 1;
            }
        }
        return 0;
    }

    // Getters et Setters
    public Map<String, ServerObject> getDatabase(){
        return database;
    }

    public void setDatabase(Map<String, ServerObject> database){
        this.database = database;
    }

}
