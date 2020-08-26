package jmessenger.storages.jdbc

import jmessenger.jlanguage.messages.Dialog
import jmessenger.jlanguage.messages.Document
import jmessenger.jlanguage.messages.TextMessage
import jmessenger.jmessengerserver.LoggedUser
import jmessenger.storages.Message
import jmessenger.storages.Storage
import jmessenger.storages.exceptions.NoSuchUserException
import jmessenger.storages.exceptions.UserAlreadyExistException
import jmessenger.utils.LogsManager
import jmessenger.utils.startThread
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement

abstract class JDBCStorage(private val host: String, private val port: Int,
    private val database: String, private val login: String, private val password: String) : Storage {

    internal lateinit var connection: Connection

    override fun init() {
        connection = openConnection(host, port, database, login, password)
        startThread {
            val sleepTime: Long = 12 * 60 * 60 * 1000
            while (true) {
                Thread.sleep(sleepTime)
                LogsManager.log("Reopen connection to JDBC DB")
                connection.close()
                connection = openConnection(host, port, database, login, password)
            }
        }
    }

    override fun addUser(login: String, password: String): Int {
        val statement = prepareStatement()
        val result = statement.executeQuery("SELECT * FROM users WHERE login = '$login'")
        if (getSetSize(result) > 0) {
            throw UserAlreadyExistException()
        }
        statement.executeUpdate(
            "INSERT INTO users (login, password) VALUES ('$login', 'password')",
            Statement.RETURN_GENERATED_KEYS
        )
        val id = getInsertId(statement)
        statement.close()
        return id
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

    override fun getUser(login: String): LoggedUser {
        val statement = prepareStatement()
        val result = statement.executeQuery("SELECT * FROM users WHERE LOWER(login) = '${login.toLowerCase()}'")
        if (getSetSize(result) == 0) throw NoSuchUserException()
        val user = LoggedUser(
            result.getInt("id"),
            result.getString("login"),
            result.getString("password")
        )
        result.close()
        statement.close()
        return user
    }

    override fun getUserLogin(id: Int): String {
        val statement = prepareStatement()
        val result = statement.executeQuery("SELECT login FROM users WHERE id = $id")
        if (getSetSize(result) == 0) throw NoSuchUserException()
        val userLogin = result.getString("login")
        result.close()
        statement.close()
        return userLogin
    }

    override fun getUserId(login: String) = getUser(login).id

    private fun getMessage(set: ResultSet) = TextMessage().apply {
        id = set.getInt("id")
        dialogId = set.getInt("dialog_id")
        message = set.getString("message")
        fromUser = set.getInt("from_user")
        toUser = set.getInt("to_user")
        time = set.getTimestamp("time").time
        val statement = prepareStatement()
        val result = statement.executeQuery("SELECT attachment_id FROM messages_attachments WHERE message_id = $id")
        while (result.next()) {
            attachments.add(getDocument(result.getInt("attachment_id")))
        }
        result.close()
        statement.close()
    }

    override fun getDialogs(userId: Int, page: Int): List<Dialog> {
        val statement = prepareStatement()
        val result =
            statement.executeQuery("WITH messages AS ( SELECT m.*, ROW_NUMBER() OVER (PARTITION BY dialog_id ORDER BY id DESC) AS rn FROM messages AS m ) SELECT * FROM messages WHERE rn = 1 AND (from_user = $userId OR to_user = $userId) ORDER BY id ASC LIMIT " + (page + 1) * 15 + " OFFSET " + page * 15)
        val dialogs = mutableListOf<Dialog>()
        while (result.next()) {
            dialogs.add(Dialog().apply {
                lastMessage = getMessage(result)
                login =
                    if (lastMessage.fromUser == userId) getUserLogin(lastMessage.toUser) else getUserLogin(lastMessage.fromUser)
            })
        }
        result.close()
        statement.close()
        return dialogs
    }

    private fun prepareStatement() =
        connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)

    private fun getDialogsSet(statement: Statement, fromUser: Int, toUser: Int) =
        statement.executeQuery("SELECT * FROM dialogs WHERE (from_user = $fromUser AND to_user = $toUser) OR (from_user = $toUser AND to_user = $fromUser)")

    override fun addMessage(textMessage: TextMessage): Message {
        val statement = prepareStatement()
        val dialogs = getDialogsSet(statement, textMessage.fromUser, textMessage.toUser)
        val dialogId = if (getSetSize(dialogs) > 0) {
            dialogs.getInt("id")
        } else {
            val result = statement.executeQuery("SELECT * FROM users WHERE id = ${textMessage.toUser}")
            if (getSetSize(result) == 0) {
                throw NoSuchUserException()
            }
            statement.executeUpdate(
                "INSERT INTO dialogs (from_user, to_user) VALUES (${textMessage.fromUser}, ${textMessage.toUser})",
                Statement.RETURN_GENERATED_KEYS
            )
            getInsertId(statement)
        }
        val messageStatement = connection.prepareStatement(
            "INSERT INTO messages (message, dialog_id, from_user, to_user, time) " +
                    "VALUES (?, $dialogId, ${textMessage.fromUser}, ${textMessage.toUser}, now())",
            Statement.RETURN_GENERATED_KEYS
        )
        messageStatement.setString(1, textMessage.message)
        messageStatement.executeUpdate()
        val messageId = getInsertId(messageStatement)
        messageStatement.close()
        for (attachment in textMessage.attachments) {
            if (attachment is Document) {
                addDocumentToMessage(messageId, attachment.id, attachment.documentType)
            }
        }
        statement.close()
        return Message(messageId, dialogId)
    }

    override fun getMessages(dialogId: Int, fromId: Int): List<TextMessage> {
        val statement = prepareStatement()
        val result =
            statement.executeQuery("SELECT * FROM messages WHERE id < $fromId AND dialog_id = $dialogId ORDER BY id DESC FETCH FIRST 10 ROWS ONLY")
        val messages = mutableListOf<TextMessage>()
        while (result.next()) {
            messages.add(getMessage(result))
        }
        messages.reverse()
        result.close()
        statement.close()
        return messages.toList()
    }

    override fun getDialog(userId: Int, otherUser: Int): Dialog? {
        val statement = prepareStatement()
        val dialogs = getDialogsSet(statement, userId, otherUser)
        val id = if (getSetSize(dialogs) > 0) {
            dialogs.getInt("id")
        } else return null
        val set = statement.executeQuery("SELECT * FROM messages WHERE dialog_id = $id ORDER BY id DESC LIMIT 1")
        set.next()
        val message = try {
            getMessage(set)
        } catch (e: SQLException) {
            return null;
        }
        set.close()
        statement.close()
        return Dialog().apply {
            lastMessage = message
        }

    }

    override fun getMessage(id: Int): TextMessage {
        val statement = prepareStatement()
        val set = statement.executeQuery("SELECT * FROM messages WHERE id = $id")
        set.first()
        val message = getMessage(set)
        set.close()
        statement.close()
        return message
    }

    override fun editMessage(id: Int, message: String) {
        val statement = prepareStatement()
        statement.executeUpdate("UPDATE messages SET message = '$message' WHERE id = $id")
        statement.close()
    }

    override fun deleteMessage(id: Int) {
        val statement = prepareStatement()
        statement.executeUpdate("DELETE FROM messages WHERE id = $id")
        statement.close()
    }

    override fun addFile(name: String, type: Int, parentId: Int): Int {
        val statement = prepareStatement()
        statement.executeUpdate(
            "INSERT INTO documents (name, type, parent_id) VALUES ('$name', $type, $parentId)",
            Statement.RETURN_GENERATED_KEYS
        )
        val id = getInsertId(statement)
        statement.close()
        return id
    }

    override fun getDocumentName(id: Int): String {
        val statement = prepareStatement()
        val query = statement.executeQuery("SELECT name FROM documents WHERE id = $id")
        query.first()
        val documentName = query.getString("name")
        query.close()
        statement.close()
        return documentName
    }

    override fun getDocument(id: Int): Document {
        val statement = prepareStatement()
        val query = statement.executeQuery("SELECT name, type FROM documents WHERE id = $id")
        query.first()
        val document = Document(id, query.getString("name"), query.getInt("type"))
        query.close()
        statement.close()
        return document
    }

    override fun addDocumentToMessage(messageId: Int, documentId: Int, type: Int) {
        val statement = prepareStatement()
        statement.executeUpdate("INSERT INTO messages_attachments VALUES ($messageId, $documentId, $type)")
        statement.closeOnCompletion()
    }

    override fun removeAttachmentFromMessage(messageId: Int, documentId: Int) {
        val statement = prepareStatement()
        statement.executeUpdate("DELETE FROM messages_attachments WHERE message_id = $messageId AND attachment_id = $documentId")
    }

    abstract fun openConnection(host: String, port: Int, database: String, login: String, password: String): Connection

}