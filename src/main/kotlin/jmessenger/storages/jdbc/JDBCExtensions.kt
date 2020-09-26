package jmessenger.storages.jdbc

import java.sql.PreparedStatement

fun PreparedStatement.setArgument(argument: String, position: Int = 1): PreparedStatement {
    setString(position, argument)
    return this
}