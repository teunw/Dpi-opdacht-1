package dpi.model

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

fun Any.serialize(): ByteArray {
    val byteOutputStream = ByteOutputStream()
    val oos = ObjectOutputStream(byteOutputStream)
    oos.writeObject(this)
    return byteOutputStream.bytes
}

inline fun <reified T> deserialize(bytes: ByteArray): T {
    val ois = ObjectInputStream(bytes.inputStream())
    val readObj = ois.readObject()
    if (readObj is T) {
        return readObj
    }
    throw Exception("Wrong type")
}
