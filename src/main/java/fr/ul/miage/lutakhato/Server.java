package fr.ul.miage.lutakhato;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Server {


    private static final Logger LOG = Logger.getLogger(Server.class.getName());
    private static Map<String, ServerObject> database = new HashMap<String, ServerObject>();
    public static String[] keyWords = new String[]{"APPEND", "DECR", "DEL", "EXISTS", "EXPIRE", "GET", "INCR", "SET"};

    public static void main(String[] args) {

    }

    public static boolean syntaxCheck(String entry) {
        // Récupération des mots clés entrés
        String[] entryParts = purgeBlanks(entry.split(" "));
        if (entryParts.length == 0) {
            return false;
        }

        // Vérification de l'existence de la requête
        String firstKeyword = entryParts[0];
        boolean requestExists = Arrays.stream(keyWords).anyMatch(keyword -> keyword.equals(firstKeyword));
        if (!requestExists) {
            return false;
        }

        // Vérification des paramètres
        List<String> exceptFirst = Arrays.stream(entryParts, 1, entryParts.length).collect(Collectors.toList());
        switch (firstKeyword) {
            case "APPEND":
                return syntaxCheckAppend(exceptFirst);
            case "INCR":
            case "DECR":
            case "GET":
                return syntaxCheckIncrDecrGet(exceptFirst);
            case "DEL":
            case "EXISTS":
                return syntaxCheckDelExists(exceptFirst);
            case "EXPIRE":
                return syntaxCheckExpire(exceptFirst);
            case "SET":
                return syntaxCheckSet(exceptFirst);
            default:
                return false;
        }
    }


    private static boolean syntaxCheckIncrDecrGet(List<String> array) {
        // Paramètres : String key
        boolean result = false;
        if (array.size() == 1) {
            result = true;
        }
        return result;
    }

    private static boolean syntaxCheckAppend(List<String> array) {
        // Paramètres : String key, String value
        boolean result = false;
        if (array.size() == 2) {
            result = true;
        }
        return result;
    }

    private static boolean syntaxCheckSet(List<String> array) {
        // Paramètres : String key, String value, String[] options
        boolean result = false;
        if (array.size() > 1) {
            result = true;
        }
        return result;
    }

    private static boolean syntaxCheckExpire(List<String> array) {
        // Paramètres : String key, int expireMillis, String[] options
        if (array.size() > 1) {
            try {
                int isInt = Integer.valueOf(array.get(1));
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    private static boolean syntaxCheckDelExists(List<String> array) {
        // Paramètres : String[] keys
        boolean result = false;
        if (array.size() > 0) {
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

    public String set(String key, Object value, String[] options) {

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

        ServerObject serverObject = null;
        if(value instanceof Integer) {
            serverObject = new ServerObject((int) System.currentTimeMillis(), (int) value);
        } else if (value instanceof String) {
            serverObject = new ServerObject((int) System.currentTimeMillis(), (String) value);
        } else if (value == null) {
            serverObject = new ServerObject((int) System.currentTimeMillis(), value);
        } else {
            System.out.println("Le type de l'objet est inconnu");
        }
        database.put(key, serverObject);
        expire(key, expireMillis, new String[]{});

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
        int nbSuccess = 0;

        for (String key : keys) {
            if (database.containsKey(key)) {
                if (database.get(key).getExpireMillis() != -1) {
                    if ((System.currentTimeMillis() - database.get(key).getCreationMillis()) > database.get(key).getExpireMillis()) {
                        // La clé a expiré, nous la supprimons
                        database.remove(key);
                    } else {
                        // La clé existe et n'a pas expiré
                        nbSuccess++;
                    }
                } else {
                    // La clé existe sans expiration
                    nbSuccess++;
                }
            }
        }

        return nbSuccess;
    }

    public static int incr(String key) {
        int newValue = 0;
        if (exists(new String[]{key}) > 0) {
            try {
                int oldValue = Integer.valueOf(database.get(key).getValue().toString());
                if (Integer.MAX_VALUE >= oldValue + 1) {
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
                if (Integer.MIN_VALUE <= oldValue - 1) {
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
            if (exists(new String[]{key}) > 0) {
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
     *
     * @param key nom de la valeur à récupérer dans la base de données
     * @return la valeur associée à la clé dans la base de données (retourne null si la valeur n'existe pas)
     */
    public Object get(String key) {
        if (exists(new String[]{key}) > 0) {
            return database.get(key).getValue();
        }
        return null;
    }

    /**
     * Fonction permettant de mettre une "date limite" sur une variable de la base de données
     *
     * @param key     nom de la valeur où il faut mettre la "date limite"
     * @param seconds durée avant laquelle la variable sera expirée
     * @param options option de la commande
     *                - NX : applique la "date limite" uniquement si la variable n'en possède pas
     *                - XX : applique la "date limite" uniquement si la variable en possède une
     *                - GR : applique la "date limite" uniquement si la nouvelle est plus grande que celle qui existe
     *                - LT : applique la "date limite" uniquement si la nouvelle est plus petite que celle qui existe
     *                - si le tableau options est vide, se lance sans options
     * @return 1 si l'ajout se passe correctement, 0 sinon
     */
    public int expire(String key, int seconds, String[] options) {
        if (exists(new String[]{key}) > 0) {
            int expireMillis = seconds * 1000;
            for (int i = 0; i < options.length; i++) {
                switch (options[i]) {
                    case "NX":
                        if (!(database.get(key).getExpireMillis() == -1)) {
                            return 0;
                        }
                        break;
                    case "XX":
                        if (database.get(key).getExpireMillis() == -1) {
                            return 0;
                        }
                        break;
                    case "GR":
                        if (!(database.get(key).getExpireMillis() < expireMillis)) {
                            return 0;
                        }
                        break;
                    case "LT":
                        if (!(database.get(key).getExpireMillis() > expireMillis)) {
                            return 0;
                        }
                        break;
                    default:
                        break;
                }
            }
            if (expireMillis == -1 || expireMillis > 0) {
                database.get(key).setExpireMillis(expireMillis);
                return 1;
            } else {
                return 0;
            }
        }
        return 0;
    }

    /**
     * Fonction permettant de récupérer la taille d'un objet String
     *
     * @param key la clé du String dans la base
     * @return la taille du String ou le nombre de chiffres de l'Integer, retourne une -1 et affiche une erreur si l'objet n'est pas d'un type géré
     */
    public static int strlen(String key) {
        int dataLength = -1;
        try {
            if (!database.containsKey(key)) {
                dataLength = 0;
            }
            String value = String.valueOf(database.get(key).getValue());
            if (value == null) {
                dataLength = 0;
            } else {
                dataLength = value.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataLength;
    }

    // Getters et Setters
    public static Map<String, ServerObject> getDatabase() {
        return database;
    }

    public void setDatabase(Map<String, ServerObject> database) {
        this.database = database;
    }

}
