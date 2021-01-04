package com.apollographql.apollo.gradle.api

import com.apollographql.apollo.api.ApolloExperimental
import com.apollographql.apollo.compiler.OperationIdGenerator
import com.apollographql.apollo.compiler.OperationOutputGenerator
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.TaskProvider

/**
 * A [Service] represents a GraphQL schema and associated queries.
 *
 * The queries will be compiled and verified against the schema to generate the models.
 */
interface Service  {
  val name: String

  /**
   * The folder containing the GraphQL operation files. They are the files that are used to define queries.
   *
   * Files will be searched in "src/$sourceSet/graphql/$sourceFolder"
   */
  val sourceFolder: Property<String>

  /**
   * Files to include as in [org.gradle.api.tasks.util.PatternFilterable]
   *
   * Default: ["**&#47;*.graphql", "**&#47;*.gql"]
   */
  val include: ListProperty<String>

  /**
   * Files to exclude as in [org.gradle.api.tasks.util.PatternFilterable]
   *
   * The empty list by default
   */
  val exclude: ListProperty<String>

  /**
   * Adds the given directory as a GraphQL source root
   *
   * By default, the plugin will use [sourceFolder] to search for GraphQL files under "src/$sourceSet/graphql/$sourceFolder".
   *
   * Use [addGraphqlDirectory] if your files are outside of "src/main/graphql" or to have them in multiple folders.
   *
   */
  fun addGraphqlDirectory(directory: Any)

  /**
   * The schema file as either a ".json" introspection schema or a ".sdl" SDL schema. You might come across schemas named "schema.graphql",
   * these are SDL schemas most of the time that need to be renamed to "schema.sdl" to be recognized properly.
   *
   * By default, the plugin looks for a "schema.[json|sdl]" file in sourceDirectory
   */
  val schemaFile: RegularFileProperty

  /**
   * Warn if using a deprecated field
   *
   * Default value: true
   */
  val warnOnDeprecatedUsages: Property<Boolean>

  /**
   * Fail the build if there are warnings. This is not named `allWarningAsErrors` to avoid nameclashes with the Kotlin options
   *
   * Default value: false
   */
  val failOnWarnings: Property<Boolean>

  /**
   * For custom scalar types like Date, map from the GraphQL type to the java/kotlin type.
   *
   * Default value: the empty map
   */
  val customScalarsMapping: MapProperty<String, String>

  /**
   * By default, Apollo uses `Sha256` hashing algorithm to generate an ID for the query.
   * To provide a custom ID generation logic, pass an `instance` that implements the [OperationIdGenerator]. How the ID is generated is
   * indifferent to the compiler. It can be an hashing algorithm or generated by a backend.
   *
   * Example Md5 hash generator:
   * ```groovy
   * import com.apollographql.apollo.compiler.OperationIdGenerator
   *
   * apollo {
   *   operationIdGenerator = new OperationIdGenerator() {
   *     String apply(String operationDocument, String operationFilepath) {
   *       return operationDocument.md5()
   *     }
   *
   *     /**
   *      * Use this version override to indicate an update to the implementation.
   *      * This invalidates the current cache.
   *      */
   *     String version = "v1"
   *   }
   * }
   * ```
   *
   * Default value: [OperationIdGenerator.Sha256]
   */
  val operationIdGenerator: Property<OperationIdGenerator>

  /**
   * A generator to generate the operation output from a list of operations.
   * OperationOutputGenerator is similar to [OperationIdGenerator] but can work on lists. This is useful if you need
   * to register/whitelist your operations on your server all at once.
   *
   * Example Md5 hash generator:
   * ```groovy
   * import com.apollographql.apollo.compiler.OperationIdGenerator
   *
   * apollo {
   *   operationOutputGenerator = new OperationIdGenerator() {
   *     String apply(List<operation operationDocument, String operationFilepath) {
   *       return operationDocument.md5()
   *     }
   *
   *     /**
   *      * Use this version override to indicate an update to the implementation.
   *      * This invalidates the current cache.
   *      */
   *     String version = "v1"
   *   }
   * }
   * ```
   *
   * Default value: [OperationIdGenerator.Sha256]
   */
  val operationOutputGenerator: Property<OperationOutputGenerator>

  /**
   * When true, the generated classes names will end with 'Query' or 'Mutation'.
   * If you write `query droid { ... }`, the generated class will be named 'DroidQuery'.
   *
   * Default value: true
   */
  val useSemanticNaming: Property<Boolean>

  /**
   * The package name of the models is computed from their folder hierarchy like for java sources.
   *
   * If you want, you can prepend a custom package name here to namespace your models.
   *
   * Default value: the empty string
   */
  val rootPackageName: Property<String>

  /**
   * Whether to generate Kotlin models with `internal` visibility modifier.
   *
   * Default value: false
   */
  val generateAsInternal: Property<Boolean>

  /**
   * A list of [Regex] patterns for GraphQL enums that should be generated as Kotlin sealed classes instead of the default Kotlin enums.
   *
   * Use this if you want your client to have access to the rawValue of the enum. This can be useful if new GraphQL enums are added but
   * the client was compiled against an older schema that doesn't have knowledge of the new enums.
   */
  val sealedClassesForEnumsMatching: ListProperty<String>

  /**
   * Whether or not to generate Apollo metadata. Apollo metadata is used for multi-module support. Set this to true if you want other
   * modules to be able to re-use fragments and types from this module.
   *
   * This is currently experimental and this API might change in the future.
   *
   * Default value: false
   */
  @ApolloExperimental
  val generateApolloMetadata: Property<Boolean>

  /**
   * A list of [Regex] patterns for input/scalar/enum types that should be generated whether or not they are used by queries/fragments
   * in this module. When using multiple modules, Apollo Android will generate all the types by default in the root module
   * because the root module doesn't know what types are going to be used by dependent modules. This can be prohibitive in terms
   * of compilation speed for large projects. If that's the case, opt-in the types that are used by multiple dependent modules here.
   * You don't need to add types that are used by a single dependent module.
   *
   * This is currently experimental and this API might change in the future.
   *
   * Default value: if (generateApolloMetadata) listOf(".*") else listOf()
   */
  @ApolloExperimental
  val alwaysGenerateTypesMatching: SetProperty<String>

  /**
   * Configures the [Introspection]
   */
  fun introspection(configure: Action<in Introspection>)

  /**
   * Configures the [Registry]
   */
  fun registry(configure: Action<in Registry>)

  /**
   * overrides the way operationOutput is wired. Use this if you want to wire the generated operationOutput. By default, oeprationOutput
   * is not generated and therefore not wired.
   */
  fun withOperationOutput(action: Action<in OperationOutputWire>)

  class OperationOutputWire(
      /**
       * The task that produces operationOutput
       */
      val task: TaskProvider<out Task>,

      /**
       * A json file containing a [Map]<[String], [com.apollographql.apollo.compiler.operationoutput.OperationDescriptor]>
       *
       * This file can be used to upload the queries exact content and their matching operation ID to a server for whitelisting
       * or persisted queries.
       */
      val operationOutputFile: Provider<RegularFile>
  )

  /**
   * overrides the way the task is wired. Use this if you want to wire the generated sources to another task than the default destination:
   * - main sourceSet for Kotlin projects
   * - commonMain sourceSet for Kotlin multiplatform projects
   * - all variants for Android projects
   */
  fun withOutputDir(action: Action<in OutputDirWire>)

  class OutputDirWire(
      /**
       * The task that produces outputDir
       */
      val task: TaskProvider<out Task>,
      /**
       * The directory where the generated models will be written
       */
      val outputDir: Provider<Directory>
  )
}
