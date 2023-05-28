package fr.ul.miage.lutakhato;

public class ServerObject {

    // Variables
    private long creationMillis = System.currentTimeMillis();
    private long expireMillis = -1;
    private Object value = 0;
    public ServerObject(long expire, Object value) {
        setExpireMillis(expire);
        setValue(value);
    }
    public ServerObject(Object value) {
        setValue(value);
    }

    // Getters et Setters
    public void setCreationMillis(int creationMillis) {
        this.creationMillis = creationMillis;
    }
    public long getCreationMillis(){
        return this.creationMillis;
    }
    public void setExpireMillis(long expire) {
        this.expireMillis = expire;
    }
    public long getExpireMillis(){
        return this.expireMillis;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public Object getValue(){
        return this.value;
    }
    public boolean isExpired() {
        if(this.expireMillis > 0){
            return (System.currentTimeMillis() > (this.creationMillis + this.expireMillis));
        }
        return false;
    }
}
