package fr.ul.miage.lutakhato;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ServerThread extends Thread {

    private final Client client;
    private Map<String, ServerObject> database = new HashMap<>();

    public ServerThread(Client client) {
        this.client = client;
    }

    public void run() {
        try {
            InputStream in = this.client.getInputStream();
            OutputStream out = this.client.getOutputStream();

            while (true) {
                byte[] buffer = new byte[2048];
                int numRead = in.read(buffer);
                if (numRead == -1) {
                    this.client.close();
                    break;
                }

                String received = new String(buffer, 0, numRead, StandardCharsets.UTF_8);
                System.out.println("> reçu :  " + received);

                String toReturn = "";
                if(received.contains("|")) {
                    String[] allCommands = received.split("\\|");
                    if(pipelineSyntax(allCommands)){
                        for(String command : allCommands) {
                            if(toReturn.equals("")) {
                                toReturn += callFunction(command) + "\n";
                            } else {
                                toReturn += "Server> " + callFunction(command) + "\n";
                            }
                        }
                        toReturn = toReturn.substring(0, toReturn.length()-1); //On enlève le dernier retour à la ligne
                    } else {
                        toReturn = "Syntax error";
                    }
                } else {
                    if(syntaxCheck(received)){
                        toReturn = callFunction(received);
                    } else {
                        toReturn = "Syntax error";
                    }
                }
                out.write(toReturn.getBytes());
            }
        } catch (IOException var6) {
            var6.printStackTrace();
        }
        /*
        String received = "SET 0 0";
        String toReturn = "";
        if (syntaxCheck(received)) {
            toReturn = callFunction(received);
        } else {
            // Retourne au client : (sysout ne revient pas au même/ à refaire)
            toReturn = "Entry error";
        }
        System.out.println(toReturn);*/
    }

    public boolean pipelineSyntax(String[] allCommands) {
        for (String command : allCommands) {
            if (!syntaxCheck(command)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fonction permettant de vérifier la syntaxe d'une entrée
     *
     * @param entry entrée dont on veut vérifier la syntaxe
     * @return true si la syntaxe est bonne, false sinon
     */
    public boolean syntaxCheck(String entry) {
        if (entry != null) {
            // Récupération des mots clés entrés
            String[] entryParts = purgeBlanksNormalizeStrings(entry);

            if (entryParts.length != 0) {
                // Vérification des paramètres
                List<String> exceptFirst = Arrays.stream(entryParts, 1, entryParts.length).collect(Collectors.toList());

                // Le premier argument correspond à la fonction appelée
                switch (entryParts[0].toUpperCase()) {
                    case "SUBSCRIBE":
                        return syntaxCheckSubscribe(exceptFirst);
                    case "PUBLISH":
                    case "APPEND":
                        return syntaxCheckAppendPublish(exceptFirst);
                    case "STRLEN":
                    case "INCR":
                    case "DECR":
                    case "GET":
                        return syntaxCheckIncrDecrGetStrlen(exceptFirst);
                    case "DEL":
                    case "EXISTS":
                        return syntaxCheckDelExists(exceptFirst);
                    case "EXPIRE":
                        return syntaxCheckExpire(exceptFirst);
                    case "SET":
                        return syntaxCheckSet(exceptFirst);
                    default:
                        // Si l'on appele une fonction qui n'existe pas
                        return false;
                }
            }
        }
        return false;
    }

    /**
     * Appel la bonne fonction en fonction de la syntaxe
     *
     * @param entry Entrée de l'utilisateur
     * @return Retour de la fonction
     */
    public String callFunction(String entry) {
        String toReturn = "";
        String[] entryParts = purgeBlanksNormalizeStrings(entry);
        List<String> exceptFirst = Arrays.stream(entryParts, 1, entryParts.length).collect(Collectors.toList());
        switch (entryParts[0].toUpperCase()) {
            case "PUBLISH":
                int reply = publishToChannel(entryParts[1].trim(), entryParts[2].trim());
                if(reply != -1){
                    toReturn = String.valueOf(reply);
                } else {
                    toReturn = "Error";
                }
                break;
            case "SUBSCRIBE":
                subscribeToChannel(entryParts[1].trim());
                break;
            case "APPEND":
                toReturn = String.valueOf(append(exceptFirst.get(0), exceptFirst.get(1)));
                break;
            case "STRLEN":
                toReturn = String.valueOf(strlen(exceptFirst.get(0)));
                break;
            case "INCR":
                toReturn = String.valueOf(incr(exceptFirst.get(0)));
                break;
            case "DECR":
                toReturn = String.valueOf(decr(exceptFirst.get(0)));
                break;
            case "GET":
                toReturn = String.valueOf(get(exceptFirst.get(0)));
                break;
            case "DEL":
                toReturn = String.valueOf(del(exceptFirst.toArray(new String[0])));
                break;
            case "EXISTS":
                toReturn = String.valueOf(exists(exceptFirst.toArray(new String[0])));
                break;
            case "EXPIRE":
                toReturn = String.valueOf(expire(exceptFirst.get(0), Integer.parseInt(exceptFirst.get(1)), exceptFirst.subList(2, exceptFirst.size()).toArray(new String[0])));
                break;
            case "SET":
                Object value;
                if (containsOnlyDigits(exceptFirst.get(1))) {
                    value = Integer.parseInt(exceptFirst.get(1));
                } else {
                    value = exceptFirst.get(1);
                }
                toReturn = String.valueOf(set(exceptFirst.get(0), value, exceptFirst.subList(2, exceptFirst.size()).toArray(new String[0])));

                break;
        }
        return toReturn;
    }

    public boolean containsOnlyDigits(String str) {
        Pattern pattern = Pattern.compile("^[0-9]+$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    /**
     * Fonction permettant de mettre une paire clé/valeur dans la base de données
     * Options :
     * EX seconds -- Set the specified expire time, in seconds.
     * PX milliseconds -- Set the specified expire time, in milliseconds.
     * NX -- Only set the key if it does not already exist.
     * XX -- Only set the key if it already exists.
     * GET -- Return the old string stored at key, or nil if key did not exist. An error is returned and SET aborted if the value stored at key is not a string.
     *
     * @param key     clé de la paire
     * @param value   valeur de la paire
     * @param options tableau contenant les différentes options - une option + valeur = 2 entrées dans le tableau
     * @return un String en fonction des options
     */
    public String set(String key, Object value, String[] options) {

        String toReturn = "OK";
        long expireMillis = -1;

        for (int i = 0; i < options.length; i++) {
            String option = options[i].toUpperCase();
            switch (option) {
                case "EX":
                    expireMillis = Long.parseLong(String.valueOf(1000 * Integer.parseInt(options[i + 1])));
                    i++;
                    break;

                case "PX":
                    expireMillis = Integer.parseInt(options[i + 1]);
                    i++;
                    break;

                case "NX":
                    if (exists(new String[]{key}) > 0) {
                        return "nil";
                    }
                    break;

                case "XX":
                    if (!(exists(new String[]{key}) > 0)) {
                        return "nil";
                    }
                    break;

                case "GET":
                    if (exists(new String[]{key}) > 0) {
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

        ServerObject serverObject;
        if (value instanceof Integer) {
            serverObject = new ServerObject(expireMillis, value);
        } else if (value instanceof String) {
            serverObject = new ServerObject(expireMillis, value);
        } else if (value == null) {
            serverObject = new ServerObject(expireMillis, null);
        } else {
            System.out.println("Le type de l'objet est inconnu et n'a pas été inséré");
            return null;
        }
        database.put(key, serverObject);

        return toReturn;
    }

    /**
     * Cette méthode supprime plusieurs valeurs stockées dans la base de données
     *
     * @param keys tableau contenant les clés à supprimer de la base de données
     * @return le nombre de valeurs supprimées avec succès
     */
    public int del(String[] keys) {
        // nbSuccess est initialisée à 0 et c'est le résultat de la méthode
        int nbSuccess = 0;

        // on itère sur le tableau de clés
        for (String key : keys) {
            /*
            Si la clé existe dans la Map,
            elle est supprimée à l'aide de la méthode remove(),
            et la variable res est incrémentée
             */
            if (database.containsKey(key)) {
                database.remove(key);
                nbSuccess++;
            }
        }
        return nbSuccess;
    }

    /**
     * Cette méthode donne le nombre de clés existantes dans la base de donnée parmi une liste de clés
     *
     * @param keys tableau de String représentant une liste de clés
     * @return retourne le nombre de clés existantes dans la base de donnée
     */
    public int exists(String[] keys) {
        int nbSuccess = 0;

        for (String key : keys) {
            if (database.containsKey(key)) {
                ServerObject serverObject = database.get(key);
                if (serverObject.getExpireMillis() >= 0) {
                    if (serverObject.isExpired()) {
                        // La clé a expiré, nous la supprimons
                        del(new String[]{key});
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

    /**
     * Fonction qui incrémente de 1 une valeur dans la base de donnée si la valeur est du bon type, si l'objet n'existe pas, il le crée dans la base de donnée
     *
     * @param key La clé correspondant à la position de l'objet à incrémenter dans la base de donnée
     * @return retourne la nouvelle valeur de l'objet après l'incrémentation, 0 si l'objet n'est pas du bon type ou si la clé n'existe pas dans la base de donnée
     */
    public int incr(String key) throws NumberFormatException {
        int newValue = 0;
        if (exists(new String[]{key}) > 0) {
            int oldValue = Integer.parseInt(database.get(key).getValue().toString());
            if (Integer.MAX_VALUE == oldValue) {
                newValue = Integer.MAX_VALUE;
            } else {
                newValue = oldValue + 1;
            }
            database.get(key).setValue(newValue);
        } else {
            set(key, newValue, new String[0]);
        }
        return newValue;
    }

    /**
     * Fonction qui fait -1 sur la valeur de la clé passée en paramètre
     *
     * @param key clé dont on veut décrémenter la valeur
     * @return la nouvelle valeur (0 si le décrément n'a pas pu être fait)
     */
    public int decr(String key) throws NumberFormatException {
        int newValue = 0;
        if (exists(new String[]{key}) > 0) {
            int oldValue = Integer.parseInt(database.get(key).getValue().toString());
            if (Integer.MIN_VALUE == oldValue) {
                newValue = Integer.MIN_VALUE;
            } else {
                newValue = oldValue - 1;
            }
            database.get(key).setValue(newValue);
        } else {
            set(key, newValue, new String[0]);
        }
        return newValue;
    }

    /**
     * Fonction permettant d'ajouter une chaîne de caractères à la suite d'une autre
     *
     * @param key   clé où l'on veut ajouter une chaine de caractères
     * @param value chaine de caractères à ajouter à la fin de la précédente valeur
     * @return la longueur de la nouvelle chaine
     */
    public int append(String key, String value) {
        int length = value.length();
        try {
            if (exists(new String[]{key}) > 0) {
                String oldValue = String.valueOf(database.get(key).getValue());
                String newValue = oldValue + value;
                database.get(key).setValue(newValue);
                length = strlen(key);
            } else {
                ServerObject serverObject = new ServerObject(-1, value);
                set(key, serverObject, new String[0]);
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
     * @return la valeur associée à la clé dans la base de données (retourne nil si la valeur n'existe pas)
     */
    public Object get(String key) {
        if (exists(new String[]{key}) > 0) {
            return database.get(key).getValue();
        }
        return "nil";
    }

    /**
     * Fonction permettant de mettre une "date limite" sur une variable de la base de données
     *
     * @param key     nom de la valeur où il faut mettre la "date limite"
     * @param seconds durée avant laquelle la variable sera expirée
     * @param options option de la commande
     *                - NX : applique la "date limite" uniquement si la variable n'en possède pas
     *                - XX : applique la "date limite" uniquement si la variable en possède une
     *                - GT : applique la "date limite" uniquement si la nouvelle est plus grande que celle qui existe (on considère -1 comme l'infini et infini > x)
     *                - LT : applique la "date limite" uniquement si la nouvelle est plus petite que celle qui existe (on considère -1 comme l'infini et infini > x)
     *                - si le tableau options est vide, se lance sans options
     * @return 1 si l'ajout se passe correctement, 0 sinon
     */
    public int expire(String key, int seconds, String[] options) {
        if (exists(new String[]{key}) > 0) {
            int expireMillis = seconds * 1000;
            for (String option : options) {
                switch (option.toUpperCase()) {
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
                    case "GT":
                        if (database.get(key).getExpireMillis() == -1 || !(database.get(key).getExpireMillis() < expireMillis)) {
                            return 0;
                        }
                        break;
                    case "LT":
                        if (database.get(key).getExpireMillis() != -1 && !(database.get(key).getExpireMillis() > expireMillis)) {
                            return 0;
                        }
                        break;
                    default:
                        break;
                }
            }
            database.get(key).setExpireMillis(expireMillis);
            return 1;
        }
        return 0;
    }

    /**
     * Fonction permettant de récupérer la taille d'un objet String
     *
     * @param key la clé du String dans la base
     * @return la taille du String ou le nombre de chiffres de l'Integer, retourne une -1 et affiche une erreur si l'objet n'est pas d'un type géré
     */
    public int strlen(String key) {
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

    /**
     * Fonction permettant de vérifier la syntaxe des fonctions incr, decr et get
     *
     * @param array tableau représentant ce qui est après l'appel de la fonction
     * @return si la syntaxe est bonne ou non
     */
    private boolean syntaxCheckIncrDecrGetStrlen(List<String> array) {
        // Paramètres : String key
        return array.size() == 1;
    }

    /**
     * Fonction permettant de vérifier la syntaxe de la fonction append
     *
     * @param array tableau représentant ce qui est après l'appel de la fonction
     * @return si la syntaxe est bonne ou non
     */
    private boolean syntaxCheckAppendPublish(List<String> array) {
        // Paramètres : String key, String value
        return array.size() == 2;
    }

    /**
     * Fonction permettant de vérifier la syntaxe de la fonction set
     *
     * @param array tableau représentant ce qui est après l'appel de la fonction
     * @return si la syntaxe est bonne ou non
     */
    private boolean syntaxCheckSet(List<String> array) {
        // Paramètres : String key, String value, String[] options
        if (array.size() > 1) {
            return syntaxCheckSetOptions(array.subList(2, array.size()));
        }
        return false;
    }

    /**
     * Fonction permettant de vérifier la syntaxe de la fonction set
     *
     * @param array tableau représentant ce qui est après l'appel de la fonction
     * @return si la syntaxe est bonne ou non
     */
    private boolean syntaxCheckSubscribe(List<String> array) {
        // Paramètres : String[] channelNames
        return array.size() > 0;
    }

    /**
     * Check si la syntaxe de la requête set est correcte
     * Empêche l'insertion de deux fois le même argument pour éviter les requêtes gigantesques
     * Empêche l'insertion de combinaisons d'arguments rendant la requête inutile (Exemple : doit être inférieure et supérieure à la valeur précédente)
     *
     * @param array Liste des arguments en référence à la fonction set
     * @return true si la syntaxe de la requête set est correcte, false sinon
     */
    private boolean syntaxCheckSetOptions(List<String> array) {
        Map<String, Boolean> options = new HashMap<>() {{
            put("EX", false);
            put("PX", false);
            put("NX", false);
            put("XX", false);
            put("GET", false);
        }};
        if (array.size() > 0) {
            for (int i = 0; i < array.size(); i++) {
                switch (array.get(i).toUpperCase()) {
                    case "EX":
                        if (!options.get("EX") && !options.get("PX")) {
                            options.replace("EX", true);
                        } else {
                            return false;
                        }
                        if (array.size() - 1 > i) {
                            try {
                                Integer.parseInt(array.get(i + 1));
                            } catch (Exception e) {
                                return false;
                            }
                            i++;
                        }
                        break;
                    case "PX":
                        if (!options.get("EX") && !options.get("PX")) {
                            options.replace("PX", true);
                        } else {
                            return false;
                        }
                        if (array.size() - 1 > i) {
                            try {
                                Integer.parseInt(array.get(i + 1));
                            } catch (Exception e) {
                                return false;
                            }
                            i++;
                        }
                        break;
                    case "NX":
                        if (!options.get("NX") && !options.get("XX")) {
                            options.replace("NX", true);
                        } else {
                            return false;
                        }
                        break;
                    case "XX":
                        if (!options.get("NX") && !options.get("XX")) {
                            options.replace("XX", true);
                        } else {
                            return false;
                        }
                        break;
                    case "GET":
                        if (!options.get("GET")) {
                            options.replace("GET", true);
                        } else {
                            return false;
                        }
                        break;

                    default:
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * Fonction permettant de vérifier la syntaxe des chaines de caractères entrées par l'utilisateur
     *
     * @param input la chaine de caractères entrée par l'utilisateur
     * @return un tableau contenant la fonction découpée
     */
    public String[] purgeBlanksNormalizeStrings(String input) {
        List<String> extractedStrings = new ArrayList<>();

        StringBuilder currentString = new StringBuilder();
        boolean insideQuotes = false;
        boolean escapeBackSlash = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            if (escapeBackSlash) {
                // Guillemet échappé (\"), ajouter un guillemet dans la chaîne courante
                escapeBackSlash = false;

            } else if (c == '"') {
                // Début ou fin des guillemets englobants
                insideQuotes = !insideQuotes;
                continue;
            } else if (c == '\\' && i + 1 < input.length() && String.valueOf(input.charAt(i + 1)).equals("\"")) {
                // Guillemet non échappé (\") sous la forme \"
                escapeBackSlash = true;
                continue;
            } else if (c == ' ' && !insideQuotes) {
                if (currentString.length() > 0) {
                    extractedStrings.add(currentString.toString());
                    currentString.setLength(0);
                }
                continue;
            }

            currentString.append(c);
        }

        if (currentString.length() > 0) {
            extractedStrings.add(currentString.toString());
        }

        return extractedStrings.toArray(new String[0]);
    }

    /**
     * Fonction permettant de vérifier la syntaxe de la fonction expire
     *
     * @param array ce qui est entré après l'appel de la fonction
     * @return si la syntaxe est bonne ou pas
     */
    private boolean syntaxCheckExpire(List<String> array) {
        // Paramètres : String key, int expireMillis, String[] options
        if (array.size() > 1) {
            try {
                int isInt = Integer.parseInt(array.get(1));
                if (isInt <= 0) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
            return syntaxCheckExpireOptions(array.subList(2, array.size()));
        }
        return false;
    }


    /**
     * Fonction permettant de vérifier la syntaxe de la fonction expire avec des options
     *
     * @param array ce qui est entré après l'appel de la fonction
     * @return si la syntaxe est bonne ou pas
     */
    private boolean syntaxCheckExpireOptions(List<String> array) {
        Map<String, Boolean> options = new HashMap<>() {{
            put("NX", false);
            put("XX", false);
            put("GT", false);
            put("LT", false);
        }};

        if (array.size() > 0) {
            for (String s : array) {
                switch (s.toUpperCase()) {
                    case "NX":
                        if (!options.get(s.toUpperCase()) && !options.get("XX")) {
                            options.replace(s.toUpperCase(), true);
                        } else {
                            return false;
                        }
                        break;
                    case "XX":
                        if (!options.get(s.toUpperCase()) && !options.get("NX")) {
                            options.replace(s.toUpperCase(), true);
                        } else {
                            return false;
                        }
                        break;
                    case "GT":
                        if (!options.get(s.toUpperCase()) && !options.get("LT")) {
                            options.replace(s.toUpperCase(), true);
                        } else {
                            return false;
                        }
                        break;
                    case "LT":
                        if (!options.get(s.toUpperCase()) && !options.get("GT")) {
                            options.replace(s.toUpperCase(), true);
                        } else {
                            return false;
                        }
                        break;

                    default:
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * Fonction permettant de vérifier la syntaxe de la fonction expire si elle implique un appel à la fonction del
     *
     * @param array ce qui est entré après l'appel de la fonction
     * @return si la syntaxe est bonne ou pas
     */
    private boolean syntaxCheckDelExists(List<String> array) {
        // Paramètres : String[] keys
        return array.size() > 0;
    }


    private void subscribeToChannel(String channelName) {
        Channel c = Channel.findChannelInList(Server.channels, channelName);
        String output = "";
        if (c != null) {
            c.subscribe(this);
            output = "Subscribed to " + channelName + " Channel";
        } else {
            output = "Channel " + channelName + " not found !";
        }
        try {
            this.client.getOutputStream().write((output).toUpperCase().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {

        }

    }

    public int publishToChannel(String channelName, String message) {
        int reply = 0;
        Channel c = Channel.findChannelInList(Server.channels, channelName);
        if (c != null) {
            try {
                c.publish(message);
                reply = c.getNbSubscribers();
            } catch (Exception e) {
                return -1;
            }
        } else {
            try {
                this.client.getOutputStream().write("Channel not found !".toUpperCase().getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                return -1;
            }
        }
        return reply;
    }

    public void NewMessage(String message) throws IOException {
        this.client.getOutputStream().write(message.toUpperCase().getBytes(StandardCharsets.UTF_8));
    }

    // Getters et Setters ----------------------------------------------------------------
    public Map<String, ServerObject> getDatabase() {
        return database;
    }

    public void setDatabase(Map<String, ServerObject> database) {
        this.database = database;
    }
}
