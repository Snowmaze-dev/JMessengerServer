package jlanguage.messages

import jlanguage.Ignore

open class JMessage(@Ignore var type: Short, var requestId: Int = 0)