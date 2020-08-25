package jmessenger.storages.jdbc

import java.sql.Connection
import java.sql.DriverManager

class MySQLStorage(host: String, port: Int, database: String, login: String, password: String) :
    JDBCStorage(host, port, database, login, password) {

    override fun init() {
        super.init()
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                "id int NOT NULL AUTO_INCREMENT," +
                "login text NOT NULL," +
                "password text NOT NULL," +
                "PRIMARY KEY (id)" +
                ") AUTO_INCREMENT=11 CHARSET=utf8") // MySQL
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS dialogs (" +
                "id int NOT NULL AUTO_INCREMENT," +
                "from_user int NOT NULL," +
                "to_user int NOT NULL," +
                "PRIMARY KEY (id)" +
                ") AUTO_INCREMENT=12 CHARSET=utf8")
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS messages (" +
                "id int NOT NULL AUTO_INCREMENT," +
                "message text NOT NULL," +
                "dialog_id int NOT NULL," +
                "time datetime NOT NULL," +
                "from_user int NOT NULL," +
                "to_user int NOT NULL," +
                "PRIMARY KEY (id)" +
                ") AUTO_INCREMENT=593 CHARSET=utf8")
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS documents (" +
                "id int NOT NULL AUTO_INCREMENT," +
                "name text NOT NULL," +
                "type int NOT NULL," +
                "parent_id int NOT NULL," +
                "PRIMARY KEY (id)" +
                ") AUTO_INCREMENT=17 CHARSET=utf8")
        connection.createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS messages_attachments (" +
                "message_id int NOT NULL," +
                "attachment_id int NOT NULL," +
                "attachment_type int NOT NULL" +
                ") CHARSET=utf8")
    }

    override fun openConnection(host: String, port: Int, database: String, login: String, password: String): Connection {
        synchronized(Object()) {
            Class.forName("com.mysql.cj.jdbc.Driver")
            return DriverManager.getConnection("jdbc:mysql://$host:$port/$database?serverTimezone=UTC", login, password)
        }
    }

}