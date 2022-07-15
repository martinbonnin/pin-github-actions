package pga

import kotlinx.serialization.json.*

fun Any?.toJsonElement(): JsonElement = when (this) {
  is Map<*, *> -> JsonObject(this.asMap.mapValues { it.value.toJsonElement() })
  is List<*> -> JsonArray(map { it.toJsonElement() })
  is Boolean -> JsonPrimitive(this)
  is Number -> JsonPrimitive(this)
  is String -> JsonPrimitive(this)
  null -> JsonNull
  else -> error("cannot convert $this to JsonElement")
}

fun JsonElement.toAny(): Any? = when (this) {
  is JsonObject -> this.mapValues { it.value.toAny() }
  is JsonArray -> this.map { it.toAny() }
  is JsonPrimitive -> {
    when {
      isString -> this.content
      this is JsonNull -> null
      else -> booleanOrNull ?: intOrNull ?: longOrNull ?: doubleOrNull ?: error("cannot decode $this")
    }
  }

  else -> error("cannot convert $this to Any")
}

inline fun <reified T> Any?.cast() = this as T

val Any?.asMap: Map<String, Any?>
  get() = this.cast()
val Any?.asList: List<Any?>
  get() = this.cast()
val Any?.asString: String
  get() = this.cast()
val Any?.asBoolean: String
  get() = this.cast()
val Any?.asNumber: Number
  get() = this.cast()