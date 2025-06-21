plugins {
    id("vn.id.tozydev.paper-infra.kotlin")
    id("vn.id.tozydev.paper-infra.paper")
}

dependencies {
    library(kotlin("stdlib"))
}

paperInfra {
    paper {
        minecraftVersion = "1.21.6"
        plugin {
            apiVersion = "1.21"
            main = "vn.id.tozydev.paperinfra.examples.kotlin.ExamplePlugin"
        }
        runServer {
            acceptEula = true
        }
    }
}
