package fr.ul.miage.lutakhato;

public class ServerObject {
    //
    private int creationMillis = 0;
    private int expire = -1;
    private Object value = 0;
    public ServerObject(int creationMillis, int expire, Object value) {
        setCreationMillis(creationMillis);
        setExpire(expire);
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
    public void setExpire(int expire) {
        this.expire = expire;
    }
    public int getExpire(){
        return this.expire;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public Object getValue(){
        return this.value;
    }
}
