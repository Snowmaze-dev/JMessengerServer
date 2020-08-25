package jmessenger.jlanguage.messages

class UnknownError(requestId: Int): ErrorMessage(requestId) {

    override var code = UNKNOWN

}