@file:Suppress("DEPRECATION")

package com.apollographql.apollo3.api

import kotlin.jvm.JvmStatic

/**
 * A replica of Apollo Android v2's CustomTypeAdapter, to ease migration from v2 to v3.
 *
 * Make your CustomTypeAdapters implement this interface by updating the imports
 * from `com.apollographql.apollo.api` to `com.apollographql.apollo3.api`.
 */
@Deprecated("Used for backward compatibility with 2.x, use Adapter instead")
interface CustomTypeAdapter<T> {
  fun decode(value: CustomTypeValue<*>): T
  fun encode(value: T): CustomTypeValue<*>
}

/**
 * A replica of Apollo Android v2's CustomTypeValue, to ease migration from v2 to v3.
 *
 * In your [CustomTypeAdapter], update the imports from `com.apollographql.apollo.api` to
 * `com.apollographql.apollo3.api` to use this version.
 */
@Deprecated("Used for backward compatibility with 2.x, use Adapter instead")
open class CustomTypeValue<T>(val value: T) {
  object GraphQLNull : CustomTypeValue<Unit>(Unit)
  class GraphQLString(value: String) : CustomTypeValue<String>(value)
  class GraphQLBoolean(value: Boolean) : CustomTypeValue<Boolean>(value)
  class GraphQLNumber(value: Number) : CustomTypeValue<Number>(value)
  class GraphQLJsonObject(value: Map<String, Any>) : CustomTypeValue<Map<String, Any>>(value)
  class GraphQLJsonList(value: List<Any>) : CustomTypeValue<List<Any>>(value)

  companion object {
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun fromRawValue(value: Any): CustomTypeValue<*> {
      return when (value) {
        is Map<*, *> -> GraphQLJsonObject(value as Map<String, Any>)
        is List<*> -> GraphQLJsonList(value as List<Any>)
        is Boolean -> GraphQLBoolean(value)
        // Not supported as we are in common code here
        /* is BigDecimal -> GraphQLNumber(value.toNumber()) */
        is Number -> GraphQLNumber(value)
        else -> GraphQLString(value.toString())
      }
    }
  }
}