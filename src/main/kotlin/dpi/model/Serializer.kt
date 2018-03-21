package dpi.model

import com.google.gson.Gson
import java.nio.charset.Charset

val charset = Charset.forName("UTF-8")

fun Any.serialize() = Gson().toJson(this).toByteArray(charset)

inline fun <reified T> deserialize(bytes: ByteArray) = Gson().fromJson(bytes.toString(charset), T::class.java)
