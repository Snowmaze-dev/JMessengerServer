package jmessenger.jlanguage.messages

class NoSuchUserError(requestId: Int = 0) : ErrorMessage(requestId) {

    override var code = NO_SUCH_USER

    override var message = "No such user"

}