package jmessenger.jlanguage.messages

class PermissionDeniedMessage: ErrorMessage() {

    override var code = PERMISSION_DENIED

    override var message = "Permission denied"

}