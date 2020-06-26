package jlanguage.messages

class UnAuthorizedErrorMessage: ErrorMessage() {

    override var code = UNAUTHORIZED

    override var message = "Unauthorized action"

}