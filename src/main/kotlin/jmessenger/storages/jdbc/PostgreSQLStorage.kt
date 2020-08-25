package jmessenger.storages.jdbc

import java.sql.Connection
import java.sql.DriverManager

class PostgreSQLStorage(host: String, port: Int, database: String, login: String, password: String) :
    JDBCStorage(host, port, database, login, password) {

    override fun init() {
        super.init()
        connection.createStatement().executeUpdate("CREATE OR REPLACE FUNCTION create_if_not_exists (table_name text, create_stmt text)" +
                " RETURNS text AS \$_\$" +
                " BEGIN IF EXISTS (" +
                "    SELECT * FROM   pg_catalog.pg_tables " +
                "    WHERE    tablename  = table_name) THEN" +
                "   RETURN 'TABLE ' || '''' || table_name || '''' || ' ALREADY EXISTS';" +
                " ELSE" +
                "   EXECUTE create_stmt;" +
                "   RETURN 'CREATED';" +
                "END IF;" +
                " END;" +
                "\$_\$ LANGUAGE plpgsql;")
        createTable("users", "(id SERIAL PRIMARY KEY," +
                    "login text NOT NULL," +
                    "password text NOT NULL)")
        createTable("dialogs", "(id SERIAL PRIMARY KEY," +
                    "from_user int NOT NULL," +
                    "to_user int NOT NULL)")
        createTable("messages","(id SERIAL PRIMARY KEY," +
                "message text NOT NULL," +
                "dialog_id int NOT NULL," +
                "time timestamp NOT NULL," +
                "from_user int NOT NULL," +
                "to_user int NOT NULL)")
        createTable("documents",  "(id SERIAL PRIMARY KEY," +
                "name text NOT NULL," +
                "type int NOT NULL," +
                "parent_id int NOT NULL)")
        createTable("messages_attachments", "(message_id int NOT NULL," +
                "attachment_id int NOT NULL," +
                "attachment_type int NOT NULL)")
    }

    private fun createTable(tableName: String, fieldsStatement: String) {
        connection.createStatement().executeQuery("SELECT create_if_not_exists('$tableName', 'CREATE TABLE $tableName $fieldsStatement')")
    }

    override fun openConnection(host: String, port: Int, database: String, login: String, password: String): Connection {
        synchronized(Object()) {
            Class.forName("org.postgresql.Driver")
            return DriverManager.getConnection("jdbc:postgresql://$host:$port/$database?serverTimezone=UTC", login, password)
        }
    }

}