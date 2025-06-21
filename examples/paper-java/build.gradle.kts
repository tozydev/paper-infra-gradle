plugins {
    id("vn.id.tozydev.paper-infra.paper")
}

dependencies {
    library(kotlin("stdlib", libs.versions.kotlin.get()))
}

paperInfra {
    paper {
        minecraftVersion = "1.21.6"
        plugin {
            apiVersion = "1.21"
            main = "vn.id.tozydev.paperinfra.examples.java.ExamplePlugin"
        }
        runServer {
            acceptEula = true
        }
    }
}
