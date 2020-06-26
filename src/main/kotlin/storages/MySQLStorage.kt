package storages

import User
import jlanguage.messages.Dialog
import jlanguage.messages.NewTextMessage
import jlanguage.messages.TextMessage
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class MySQLStorage : Storage {

    private lateinit var connection: Connection

    private val host = "127.0.0.1"
    private val port = 3306
    private val database = "jmessenger"
    private val login = "root"
    private val password = "password"

    init {
        openConnection()
    }

    override fun addUser(login: String, password: String): Int {
        val st = connection.prepareStatement("SELECT * FROM users WHERE login = ?", Statement.RETURN_GENERATED_KEYS)
        st.setString(1, login)
        val result = st.executeQuery()
        if (getSetSize(result) > 0) {
            throw UserAlreadyExistException()
        }
        val statement = connection.prepareStatement(
            "INSERT INTO users (login, password) VALUES (?, ?)",
            Statement.RETURN_GENERATED_KEYS
        )
        statement.setString(1, login)
        statement.setString(2, password)
        statement.executeUpdate()
        return getInsertId(statement)
    }

    private fun getSetSize(resultSet: ResultSet): Int {
        resultSet.last()
        val size = resultSet.row
        resultSet.first()
        return size
    }

    private fun getInsertId(statement: Statement): Int {
        val generatedKeys = statement.generatedKeys
        generatedKeys.next()
        return generatedKeys.getInt(1)
    }

    override fun getUser(login: String): User {
        val statement = connection.prepareStatement("SELECT * FROM users WHERE login = ?")
        statement.setString(1, login)
        val result = statement.executeQuery()
        if (getSetSize(result) == 0) throw UserNotFoundException()
        val user = User(result.getInt("id"), result.getString("login"), result.getString("password"))
        result.close()
        return user
    }

    override fun getUserLogin(id: Int): String {
        println("get user login $id")
        val statement = connection.prepareStatement("SELECT login FROM users WHERE id = ?")
        statement.setInt(1, id)
        val result = statement.executeQuery()
        if (getSetSize(result) == 0) throw UserNotFoundException()
        val userLogin = result.getString("login")
        result.close()
        return userLogin
    }

    override fun getDialogs(userId: Int, page: Int): List<Dialog> {
        val statement =
            connection.prepareStatement("WITH messages AS ( SELECT m.*, ROW_NUMBER() OVER (PARTITION BY dialog_id ORDER BY id DESC) AS rn FROM messages AS m ) SELECT * FROM messages WHERE rn = 1 AND (from_user = ? OR to_user = ?) ORDER BY id ASC LIMIT " + page * 15 + ", " + (page + 1) * 15)
        statement.setInt(1, userId)
        statement.setInt(2, userId)
        val result = statement.executeQuery()
        val dialogs = mutableListOf<Dialog>()
        while (result.next()) {
            dialogs.add(Dialog().apply {
                lastMessage = TextMessage().apply {
                    id = result.getInt("id")
                    dialogId = result.getInt("dialog_id")
                    message = result.getString("message")
                    fromUser = result.getInt("from_user")
                    toUser = result.getInt("to_user")
                    date = result.getDate("date").time
                }
                login = if(lastMessage.fromUser == userId) getUserLogin(lastMessage.toUser) else getUserLogin(lastMessage.fromUser)
            })
        }
        result.close()
        return dialogs
    }

    override fun addMessage(textMessage: TextMessage): Message {
        val dialogsStatement =
            connection.prepareStatement("SELECT * FROM dialogs WHERE (from_user = ? AND to_user = ?) OR (from_user = ? AND to_user = ?)")
        dialogsStatement.setInt(1, textMessage.fromUser)
        dialogsStatement.setInt(2, textMessage.toUser)
        dialogsStatement.setInt(3, textMessage.toUser)
        dialogsStatement.setInt(4, textMessage.fromUser)
        val dialogs = dialogsStatement.executeQuery()
        val dialogId = if (getSetSize(dialogs) > 0) {
            dialogs.getInt("id")
        } else {
            val st = connection.prepareStatement("SELECT * FROM users WHERE id = ?")
            st.setInt(1, textMessage.toUser)
            val result = st.executeQuery()
            if (getSetSize(result) == 0) {
                throw UserNotFoundException()
            }
            val createDialogStatement = connection.prepareStatement(
                "INSERT INTO dialogs (from_user, to_user) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
            )
            createDialogStatement.setInt(1, textMessage.fromUser)
            createDialogStatement.setInt(2, textMessage.toUser)
            createDialogStatement.executeUpdate()
            getInsertId(createDialogStatement)
        }
        val statement = connection.prepareStatement(
            "INSERT INTO messages (message, dialog_id, from_user, to_user, date) VALUES (?, ?, ?, ?, now())",
            Statement.RETURN_GENERATED_KEYS
        )
        statement.setString(1, textMessage.message)
        statement.setInt(2, dialogId)
        statement.setInt(3, textMessage.fromUser)
        statement.setInt(4, textMessage.toUser)
        statement.executeUpdate()
        return Message(getInsertId(statement), dialogId)
    }

    private fun openConnection() {
        synchronized(Object()) {
            Class.forName("com.mysql.cj.jdbc.Driver")
            connection = DriverManager.getConnection("jdbc:mysql://$host:$port/$database?serverTimezone=UTC", login, password)
        }
    }

    private fun reOpenConnection() {
        if (!connection.isClosed) {
            return
        }
        openConnection()
    }


}