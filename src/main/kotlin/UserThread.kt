import jlanguage.JLanguageInputStream
import jlanguage.JLanguageOutputStream
import jlanguage.messages.*
import java.net.Socket

class UserThread(private val socket: Socket, private val callback: UserCallback): Thread() {

    private lateinit var inp: JLanguageInputStream
    private lateinit var out: JLanguageOutputStream
    var user: User? = null

    override fun run() {
        inp = JLanguageInputStream(socket.getInputStream())
        out = JLanguageOutputStream(socket.getOutputStream())
        while(true) {
            if(inp.available() > 0) {
                val message = inp.parseLastMessage()
                println("received message $message with id " + message.requestId)
                if(message is AuthMessage) {
                    callback.onAuthorize(this, message)
                }
                if(message is NewTextMessage) {
                    callback.onTextMessage(this, message.textMessage.apply {
                        requestId = message.requestId
                    })
                }
                if(message is RequestDialogs) {
                    callback.onRequestDialogs(this, message)
                }
            }
            if(socket.isClosed) break
            sleep(1)
        }
        callback.onDisconnect(this)
    }

    fun sendMessage(message: JMessage, requestId: Int) {
        message.requestId = requestId
        println("sendMessage with id $requestId to " + (user?.login ?: "unauthorized user") + ": $message")
        out.sendMessage(message)
    }

    fun onNewMessage(textMessage: TextMessage) {
        out.sendMessage(NewMessageNotification().apply {
            message = textMessage
        })
    }

    interface UserCallback {

        fun onAuthorize(userThread: UserThread, authMessage: AuthMessage)

        fun onTextMessage(userThread: UserThread, message: TextMessage)

        fun onRequestDialogs(userThread: UserThread, message: RequestDialogs)

        fun onDisconnect(userThread: UserThread)

    }
}