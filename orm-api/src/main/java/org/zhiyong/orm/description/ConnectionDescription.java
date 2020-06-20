package org.zhiyong.orm.description;

public class ConnectionDescription {
    private final String driver;
    private final String protocol;
    private final String host;
    private final int port;
    private final String name;
    private final String password;
    private final String db;

    public ConnectionDescription(String driver, String protocol, String host, int port, String name, String password, String db) {
        this.driver = driver;
        this.host = host;
        this.port = port;
        this.name = name;
        this.password = password;
        this.db = db;
        this.protocol = protocol;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getDriver() {
        return driver;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getDb() {
        return db;
    }

    public String toUri(){
        return String.format("jdbc:%s://%s:%d/%s?%s",
                protocol,
                host,
                port,
                db,
                String.format("user=%s&password=%s", name, password));
    }

    public static ConnectionDescription asMysql(String driver, String host, int port, String name, String password, String db){
        return new ConnectionDescription(driver, "mysql", host, port, name, password, db);
    }

    public static ConnectionDescription asMysql(String driver, String name, String password, String db){
        return new ConnectionDescription(driver, "mysql", "localhost", 3306, name, password, db);
    }

    public static ConnectionDescription asMysql(String driver, int port,  String name, String password, String db){
        return new ConnectionDescription(driver, "mysql", "localhost", port, name, password, db);
    }


}
