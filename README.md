[kotlin-gradle-plugin]: https://kotlinlang.org/docs/gradle-configure-project.html "Kotlin Gradle Plugin documentation"

[paper]: https://papermc.io/ "PaperMC website"

[paperweight-userdev]: https://docs.papermc.io/paper/dev/userdev/ "paperweight-userdev documentation"

[run-task]: https://github.com/jpenilla/run-task "run-task repository"

[resource-factory]: https://github.com/jpenilla/resource-factory/ "resource-factory repository"

[shadow]: https://gradleup.com/shadow/ "Shadow plugin documentation"

# Paper Infrastructure Gradle Plugin

A collection of Gradle plugins designed to streamline the development of [Paper][paper] plugins for
Minecraft. This project provides a robust infrastructure setup, allowing you to focus on writing your plugin's logic
instead of wrestling with build configurations.

It automates common tasks like setting up Paperweight, generating `paper-plugin.yml`, running a test server, and
managing
dependencies with advanced features like dynamic library loading.

## ✨ Features

- **Unified Configuration:** A simple `paperInfra { ... }` block to manage your project.
- **Pre-configured Environment:** Sets up Java/Kotlin, repositories, and toolchains automatically.
- **Paperweight Integration:** Seamlessly integrates with [`paperweight-userdev`][paperweight-userdev] to provide the
  Paper API and internal classes for development.
- **Automatic `paper-plugin.yml`:** Generates your `paper-plugin.yml` file from the build script, powered by
  [`resource-factory`][resource-factory].
- **Dynamic Library Loading:** Automatically download and load dependencies at runtime without shading them. This keeps
  your plugin JAR small and avoids dependency conflicts.
- **Test Server Support:** Easily run a Paper server for testing with [`run-paper`][run-task], including EULA
  acceptance, plugin downloads, and HotSwap support.
- **Shadow Plugin Ready:** A dedicated plugin to easily configure dependency shading with the [`shadow`][shadow] plugin
  when needed.

## 📚 Available Plugins

This project provides a suite of plugins. You'll typically use `paper-infra.paper` and optionally add
`paper-infra.kotlin` or `paper-infra.shadow`.

| Plugin ID                          | Description                                                                      |
|------------------------------------|----------------------------------------------------------------------------------|
| `vn.id.tozydev.paper-infra.java`   | Sets up a base Java project. (Applied automatically by other plugins)            |
| `vn.id.tozydev.paper-infra.kotlin` | Adds Kotlin support on top of the Java setup.                                    |
| `vn.id.tozydev.paper-infra.paper`  | The core plugin for Paper development. Configures Paperweight, `run-paper`, etc. |
| `vn.id.tozydev.paper-infra.shadow` | A simple convenience plugin to apply the `com.gradleup.shadow` plugin.           |

## 🚀 Quick Start

Follow these steps to set up your project.

### 1. `settings.gradle.kts`

In your `settings.gradle.kts`, configure the Gradle Plugin Portal.

```kotlin
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
```

> [!IMPORTANT]
> `mavenCentral()` is required for the `paper-infra` plugins to resolve their dependencies. I will shadow dependencies
> which are not available on Gradle Plugin Portal, so you can use them without adding additional repositories.

### 2. `build.gradle.kts`

Apply the desired plugins and configure your project inside the `paperInfra` block.

Here's a minimal example for a **Kotlin** plugin:

```kotlin
plugins {
    id("vn.id.tozydev.paper-infra.kotlin")
    id("vn.id.tozydev.paper-infra.paper")
}

// All configuration is handled within this block
paperInfra {
    paper {
        // The Minecraft version to develop against
        minecraftVersion = "1.21.6"

        // Configure your plugin.yml
        plugin {
            name = "ExamplePlugin"
            version = "1.0.0"
            main = "com.example.ExamplePlugin"
            apiVersion = "1.21"
            author = "YourName"
        }

        // Configure the runServer task
        runServer {
            acceptEula = true
        }
    }
}
```

For a **Java** plugin, simply omit the `paper-infra.kotlin` plugin.

## 📖 Usage & Configuration

### Dynamic Library Loading (`library` configuration)

To keep your plugin JAR small, you can declare dependencies that your plugin needs but that you don't want to shade.
These will be automatically downloaded and loaded by the server at runtime.

Just add them to the `library` configuration:

```kotlin
dependencies {
    // This will be downloaded and loaded at runtime
    library(kotlin("stdlib"))
    library("org.xerial:sqlite-jdbc:3.45.1.0")
}
```

**How it works:** The `paper-infra.paper` plugin generates a custom `PluginLoader` that resolves these dependencies from
Maven Central (and any other repositories in your project) before your plugin is enabled. The `loader` property in your
`paper-plugin.yml` is set automatically.

> [!IMPORTANT]
> For Kotlin multi-platform libraries like `kotlix-datetime`, you should use the jvm-specific artifact (e.g.,
> `kotlinx-datetime-jvm`) to ensure loader resolution works correctly.

### Shading Dependencies (`implementation` configuration)

If you need to bundle a library directly into your plugin JAR (e.g., a library not available on Maven Central or one you
need to relocate), use the `shadow` plugin.

1. Apply the `paper-infra.shadow` plugin.
2. Add dependencies to the `implementation` configuration.

```kotlin
plugins {
    id("vn.id.tozydev.paper-infra.paper")
    id("vn.id.tozydev.paper-infra.shadow") // Add the shadow plugin
}

paperInfra {
    // ... your standard configuration
}

dependencies {
    // This will be shaded into your final JAR
    implementation("com.example.mylib:my-custom-library:1.0.0")
}
```

### Running a Test Server

You can run a development server with your plugin installed using the `runDevBundleServer` task.

```bash
./gradlew runDevBundleServer
```

The server can be configured in the `runServer` block:

```kotlin
paperInfra {
    paper {
        // ...
        runServer {
            // Automatically accept the EULA
            acceptEula = true

            // Enable agent-based HotSwap with a JetBrains Runtime for debugging
            hotswap = true

            // Download other plugins for your test environment
            downloadPlugins {
                hangar("EssentialsX", "2.21.0")
                modrinth("luckperms", "5.4.127")
            }
        }
    }
}
```

> [!TIP]
> Use the `runDevBundleServer` task to run a test server provided by `paperweight-userdev`.
> Use the `runServer` task to run a server downloaded from the Paper API repository.

## ⚙️ Configuration Reference

<details>
<summary>Click to expand the full configuration reference</summary>

```kotlin
paperInfra {
    // Configures the Java toolchain version. Default: 21
    java {
        version = 21
    }

    paper {
        // The Minecraft version for the Paper API. Default: "1.21.6"
        minecraftVersion = "1.21.6"

        // See https://docs.papermc.io/paper/dev/getting-started/paper-plugins/ for more details
        plugin {
            name = "MyPlugin"
            version = "1.0-SNAPSHOT"
            main = "com.example.MyPlugin"
            apiVersion = "1.21"

            // Optional properties
            description = "A great plugin."
            author = "YourName"
            authors = listOf("You", "And-A-Friend")
            website = "https://example.com"
            // ... and many more
        }

        // Configure the runServer task
        runServer {
            // Accept Mojang's EULA. Default: false
            acceptEula = true

            // Directory for the server files. Default: .run-server/
            directory = layout.projectDirectory.dir(".run")

            // Enable HotSwap agent for JetBrains Runtime. Default: true
            hotswap = true

            // Download other plugins for the test server.
            // See: https://github.com/jpenilla/run-task?tab=readme-ov-file#plugin-downloads
            downloadPlugins {
                url("https://example.com/my-plugin.jar")
                github("user/repo", "tag", "asset.jar")
                hangar("EssentialsX", "2.21.0")
                modrinth("luckperms", "5.4.127")
                // ... and more
            }
        }
    }
}
```

</details>

## 📂 Examples

You can find complete, working examples in the [`/examples`](./examples) directory of this repository:

- [`paper-java`](./examples/paper-java): A basic Java plugin using dynamic library loading.
- [`paper-kotlin`](./examples/paper-kotlin): A basic Kotlin plugin.
- [`paper-shadow`](./examples/paper-shadow): A Java plugin demonstrating how to shade dependencies.

## 🔌 Used plugins

This project uses several Gradle plugins as dependencies to provide its functionality:

- [kotlin-gradle-plugin][kotlin-gradle-plugin]: For Kotlin support.
- [paperweight-userdev][paperweight-userdev]: For development setup with Paper API and internal classes.
- [run-task][run-task]: For running a test server with your plugin.
- [resource-factory][resource-factory]: For generating `paper-plugin.yml` from the build script.
- [shadow][shadow]: For shading dependencies when needed.

## 🤝 Contributing

Contributions are welcome! If you have a feature request, bug report, or pull request, please feel free to open an issue
or PR on this repository.

## 📜 License

This project is licensed under the ... License. See the [LICENSE](LICENSE) file for details.
