package jmessenger.jlanguage.messages

import jmessenger.jlanguage.messages.requests.RequestMessage
import jmessenger.jlanguage.utils.MessagesUtils.LIST_TEXT_MESSAGES

class ListTextMessages(var messages: List<TextMessage> = listOf()): RequestMessage(LIST_TEXT_MESSAGES) {

    override fun toString() = "ListTextMessages(list=$messages)"

}