package fr.ul.miage.lutakhato;

import java.util.HashMap;
import java.util.Map;

public class Server {

    Map<String, Object[]> database = new HashMap<String, Object[]>();
    public static void main(String[] args){

    }

    /**
     * Fonction permettant de récupérer une valeur dans la base de données
     * @param key nom de la valeur à récupérer dans la base de données
     * @return la valeur associée à la clé dans la base de données (retourne null si la valeur n'existe pas)
     */
    public Object[] get(String key){
        return database.get(key);
    }

    /**
     * Fonction permettant de mettre une "date limite" sur une variable de la base de données
     * @param key nom de la valeur où il faut mettre la "date limite"
     * @param seconds durée avant laquelle la variable sera expirée
     * @param option option de la commande
     *               - 0 : comportement normal de la commande
     *               - 1 : applique la "date limite" seulement si la variable n'en possède pas (option NX)
     *               - 2 : applique la "date limite" uniquement si la variable en possède une (option XX)
     *               - 3 : applique la "date limite" uniquement si la nouvelle est plus grande que celle qui existe
     *               (option GR)
     *               - 4 : applique la "date limite" seulement si la nouvelle est plus petite que celle qui existe
     *               (option LT)
     * @return 1 si l'ajout se passe correctement, 0 sinon
     */
    public int expire(String key, int seconds, int option){
        Object aModifier = database.get(key)[1];
        if (aModifier == null)
            return 0;
        switch (option) {
            case 0:
                aModifier = seconds;
                break;
            case 1:
                if (aModifier == null) {
                    aModifier = seconds;
                }
                break;
            case 2:
                if (aModifier != null) {
                    aModifier = seconds;
                }
                break;
            case 3:
                if ((int) aModifier < seconds) {
                    aModifier = seconds;
                }
                break;
            case 4:
                if ((int) aModifier > seconds) {
                    aModifier = seconds;
                }
                break;
            default:
                return 0;
        }
        return 1;
    }
}
