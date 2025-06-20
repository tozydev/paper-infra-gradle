package vn.id.tozydev.paperinfra.paper

import kotlin.test.Test
import kotlin.test.assertContains

class DynamicLibrariesLoaderClassBuilderTest {
    @Test
    fun `should build class string`() {
        val classBuilder = DynamicLibrariesLoaderClassBuilder()
        classBuilder.dependencies =
            listOf(
                "com.google.guava:guava:31.1-jre",
                "org.apache.commons:commons-lang3:3.12.0",
            )

        classBuilder.repositories =
            mapOf(
                "mavenCentral" to "https://repo.maven.apache.org/maven2",
                "jitpack" to "https://jitpack.io",
            )

        classBuilder.className = "DynamicLibrariesPluginLoader"
        classBuilder.packageName = "vn.id.tozydev.paperinfra.paper"

        val classString = classBuilder.buildString()

        assertContains(classString, "package vn.id.tozydev.paperinfra.paper")
        assertContains(classString, "public class DynamicLibrariesPluginLoader")
        assertContains(classString, "private static final Map<String, String> repositories = new LinkedHashMap<>()")
        assertContains(classString, "private static final List<String> dependencies = new ArrayList<>()")
        assertContains(classString, "repositories.put(\"mavenCentral\", \"https://repo.maven.apache.org/maven2\")")
        assertContains(classString, "repositories.put(\"jitpack\", \"https://jitpack.io\")")
        assertContains(classString, "dependencies.add(\"com.google.guava:guava:31.1-jre\")")
        assertContains(classString, "dependencies.add(\"org.apache.commons:commons-lang3:3.12.0\")")
        assertContains(classString, "public void classloader(@NotNull PluginClasspathBuilder pluginClasspathBuilder) {")
    }
}
