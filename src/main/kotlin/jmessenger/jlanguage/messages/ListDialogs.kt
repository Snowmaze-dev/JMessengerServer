package jmessenger.jlanguage.messages

import jmessenger.jlanguage.messages.requests.RequestMessage
import jmessenger.jlanguage.utils.MessagesUtils.LIST_DIALOGS

class ListDialogs(var page: Int = 0): RequestMessage(LIST_DIALOGS) {

    var list = mutableListOf<Dialog>()

    override fun toString() = "ListDialogs(page=$page, list=$list)"

}