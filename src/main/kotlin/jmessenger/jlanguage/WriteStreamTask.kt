package jmessenger.jlanguage

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream

class WriteStreamTask(private val inputStream: InputStream, private val stream: DataOutputStream, private val bufferSizeValue: Int = 8,
                      resultCallback: () -> Unit = {}): Task(resultCallback) {
    override fun run() {
        val inputStreamToSend = DataInputStream(inputStream)
        var bytesCount = inputStream.available()
        val bufferSize = bufferSizeValue*1024
        val buffer = ByteArray(bufferSize)
        var bytesToWrite = bufferSize.coerceAtMost(bytesCount)
        stream.writeInt(bufferSizeValue)
        stream.writeInt(bytesCount)
        while (bytesToWrite >= 0) {
            inputStreamToSend.read(buffer, 0, bytesToWrite)
            stream.write(buffer, 0, bytesToWrite)
            bytesCount -= bufferSize
            bytesToWrite = bufferSize.coerceAtMost(bytesCount)
            if(cancelled) {
                break
            }
        }
        stream.flush()
    }

}