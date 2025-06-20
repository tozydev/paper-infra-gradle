package vn.id.tozydev.paperinfra.java

import org.gradle.api.Project
import org.gradle.api.artifacts.ArtifactRepositoryContainer

fun Project.configureMavenCentralRepository() {
    if (repositories.findByName(ArtifactRepositoryContainer.DEFAULT_MAVEN_CENTRAL_REPO_NAME) == null) {
        repositories.mavenCentral()
    }
}
