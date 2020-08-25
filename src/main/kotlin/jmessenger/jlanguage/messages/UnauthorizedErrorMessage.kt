package jmessenger.jlanguage.messages

class UnauthorizedErrorMessage: ErrorMessage() {

    override var code = UNAUTHORIZED

    override var message = "Unauthorized action"

}