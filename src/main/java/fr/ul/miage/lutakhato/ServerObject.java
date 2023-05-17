package fr.ul.miage.lutakhato;

public class ServerObject {

    // Variables
    private int creationMillis = 0;
    private int expireMillis = -1;
    private Object value = 0;
    public ServerObject(int creationMillis, int expire, Object value) {
        setCreationMillis(creationMillis);
        setExpireMillis(expire);
        setValue(value);
    }
    public ServerObject(int creationMillis, Object value) {
        setCreationMillis(creationMillis);
        setValue(value);
    }

    // Getters et Setters
    public void setCreationMillis(int creationMillis) {
        this.creationMillis = creationMillis;
    }
    public int getCreationMillis(){
        return this.creationMillis;
    }
    public void setExpireMillis(int expire) {
        this.expireMillis = expire;
    }
    public int getExpireMillis(){
        return this.expireMillis;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public Object getValue(){
        return this.value;
    }
    public boolean isExpired() {
        if(this.expireMillis != -1){
            return System.currentTimeMillis() > (this.creationMillis + this.expireMillis);
        }
        return false;
    }
}
