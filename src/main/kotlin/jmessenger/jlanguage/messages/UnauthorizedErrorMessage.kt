package jmessenger.jlanguage.messages

class UnauthorizedErrorMessage(requestId: Int): ErrorMessage(requestId = requestId) {

    override var code = UNAUTHORIZED

    override var message = "Unauthorized action"

}