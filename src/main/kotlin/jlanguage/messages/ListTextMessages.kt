package jlanguage.messages

import jlanguage.MessagesUtils.LIST_TEXT_MESSAGES

class ListTextMessages(var page: Int = 0): JMessage(LIST_TEXT_MESSAGES) {

    var list = mutableListOf<TextMessage>()

    override fun toString() = "ListTextMessages(page=$page, list=$list)"

}