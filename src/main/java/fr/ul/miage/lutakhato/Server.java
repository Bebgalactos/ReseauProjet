package fr.ul.miage.lutakhato;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class Server {


    private static final Logger LOG = Logger.getLogger(Server.class.getName());
    public static HashMap<String, ServerObject> database = new HashMap<String, ServerObject>();
    public static void main(String[] args){

    }

    public static String set(String key, Object value, String[] options) {

        String toReturn = "";
        options = purgeBlanks(options);
        int expireMillis = 0;

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
                        if (database.containsKey(key)) {
                            LOG.warning("key already exists");
                            return toReturn;
                        }
                        break;

                    case "XX":
                        if (!database.containsKey(key)) {
                            LOG.warning("key does not already exists");
                            return toReturn;
                        }
                        break;

                    case "GET":
                        if (database.containsKey(key)) {
                            toReturn = String.valueOf(database.get(key).getValue());
                        } else {
                            toReturn = "nil";
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
         /* Cette méthode prends en entrée une Map et un tableau de String, et retourne un int qui est le nombre
    de clés qu'on a supprimé
    */
    public static int Del(Map<String, String> map, String[] parameters) {
        int res = 0; //res est initialisée à 0.

        // on itére sur le tableau de string
        for (String key : parameters) {
            /*
            Si la clé existe dans la Map,
            elle est supprimée à l'aide de la méthode remove(),
            et la variable res est incrémentée
             */
            if (map.containsKey(key)) {
                map.remove(key);
                res++;
            }
        }
        return res; }
    
    /* Cette méthode prends en entrée une Map et un tableau de String, et retourne un int qui est le nombre
    de clés existants dans la map donné en entrée
    */
    public static int Exist(Map<String, String> map, String[] parameters) {
        int res = 0; //res est initialisée à 0 et c'est le résultat de la méthode

        // on itére sur le tableau de string
        for (String key : parameters) {
             /*
            Si la clé existe dans la Map,
            la variable res est incrémentée
             */
            if (map.containsKey(key)) {
                res++;
            }
        }
        return res; }
    
    
    
}
