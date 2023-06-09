/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.3/userguide/building_java_projects.html
 */
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Input

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

javafx {
    version = "19"
    modules("javafx.controls", "javafx.fxml","javafx.graphics")
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.2")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:30.1.1-jre")

    // https://mvnrepository.com/artifact/org.openjfx/javafx-controls
    implementation("org.openjfx:javafx-controls:20-ea+9")

    // https://mvnrepository.com/artifact/org.openjfx/javafx-fxml
    implementation("org.openjfx:javafx-fxml:20-ea+9")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10")

   // https://mvnrepository.com/artifact/org.openjfx/javafx-graphics
    implementation("org.openjfx:javafx-graphics:20-ea+11")
}

application {
    // Define the main class for the application.
    mainClass.set("projet.cpoo.Main")
}

tasks.register("serveur", JavaExec::class) {
    val port = project.findProperty("port") as String? ?: "5000"
    args = listOf(port)
    main = "projet.cpoo.Serveur"
    classpath = sourceSets["main"].runtimeClasspath
}

tasks {
    
    shadowJar {
        archiveBaseName.set("DactyloGame")
        archiveClassifier.set("")
    }

}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}
