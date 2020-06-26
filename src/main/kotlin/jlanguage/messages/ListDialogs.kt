package jlanguage.messages

import jlanguage.MessagesUtils.LIST_DIALOGS

class ListDialogs(var page: Int = 0): JMessage(LIST_DIALOGS) {

    var list = mutableListOf<Dialog>()

    override fun toString() = "ListDialogs(page=$page, list=$list)"

}