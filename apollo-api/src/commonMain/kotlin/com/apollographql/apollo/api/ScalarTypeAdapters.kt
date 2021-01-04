package com.apollographql.apollo.api

class ScalarTypeAdapters(val customScalarAdapters: Map<ScalarType, CustomScalarAdapter<*>>) {

  private val adapterByGraphQLName = customScalarAdapters.mapKeys { it.key.graphqlName }

  @Suppress("UNCHECKED_CAST")
  fun <T : Any> adapterFor(scalarType: ScalarType): CustomScalarAdapter<T> {
    /**
     * Look in user-registered adapters by scalar type name first
     */
    var customScalarAdapter: CustomScalarAdapter<*>? = adapterByGraphQLName[scalarType.graphqlName]
    if (customScalarAdapter == null) {
      /**
       * If none is found, provide a default adapter based on the implementation class name
       * This saves the user the hassle of registering a scalar adapter for mapping to widespread such as Long, Map, etc...
       * The ScalarType must still be declared in the Gradle plugin configuration.
       */
      customScalarAdapter = adapterByClassName[scalarType.className]
    }
    return requireNotNull(customScalarAdapter) {
      "Can't map GraphQL type: `${scalarType.graphqlName}` to: `${scalarType.className}`. Did you forget to add a CustomScalarAdapter?"
    } as CustomScalarAdapter<T>
  }

  companion object {
    val DEFAULT = ScalarTypeAdapters(emptyMap())

    private val adapterByClassName = mapOf(
        "java.lang.String" to BuiltinCustomScalarAdapters.STRING_ADAPTER,
        "kotlin.String" to  BuiltinCustomScalarAdapters.STRING_ADAPTER,

        "java.lang.Boolean" to BuiltinCustomScalarAdapters.BOOLEAN_ADAPTER,
        "boolean" to  BuiltinCustomScalarAdapters.BOOLEAN_ADAPTER,
        "kotlin.Boolean" to  BuiltinCustomScalarAdapters.BOOLEAN_ADAPTER,

        "java.lang.Integer" to BuiltinCustomScalarAdapters.INT_ADAPTER,
        "int" to BuiltinCustomScalarAdapters.INT_ADAPTER,
        "kotlin.Int" to  BuiltinCustomScalarAdapters.INT_ADAPTER,

        "java.lang.Long" to BuiltinCustomScalarAdapters.LONG_ADAPTER,
        "long" to BuiltinCustomScalarAdapters.LONG_ADAPTER,
        "kotlin.Long" to  BuiltinCustomScalarAdapters.LONG_ADAPTER,

        "java.lang.Float" to BuiltinCustomScalarAdapters.FLOAT_ADAPTER,
        "float" to BuiltinCustomScalarAdapters.FLOAT_ADAPTER,
        "kotlin.Float" to  BuiltinCustomScalarAdapters.FLOAT_ADAPTER,

        "java.lang.Double" to BuiltinCustomScalarAdapters.DOUBLE_ADAPTER,
        "double" to BuiltinCustomScalarAdapters.DOUBLE_ADAPTER,
        "kotlin.Double" to  BuiltinCustomScalarAdapters.DOUBLE_ADAPTER,

        "java.util.Map" to BuiltinCustomScalarAdapters.MAP_ADAPTER,
        "kotlin.collections.Map" to  BuiltinCustomScalarAdapters.MAP_ADAPTER,

        "java.util.List" to BuiltinCustomScalarAdapters.LIST_ADAPTER,
        "kotlin.collections.List" to  BuiltinCustomScalarAdapters.LIST_ADAPTER,

        "com.apollographql.apollo.api.FileUpload" to  BuiltinCustomScalarAdapters.FILE_UPLOAD_ADAPTER,

        "java.lang.Object" to  BuiltinCustomScalarAdapters.FALLBACK_ADAPTER,
        "kotlin.Any" to  BuiltinCustomScalarAdapters.FALLBACK_ADAPTER,
    )
  }
}
