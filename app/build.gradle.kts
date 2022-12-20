/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Java application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.3/userguide/building_java_projects.html
 */

plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
    id("org.openjfx.javafxplugin") version "0.0.13"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

javafx {
    version = "19"
    modules("javafx.controls", "javafx.fxml")
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
}

application {
    // Define the main class for the application.
    mainClass.set("projet.cpoo.App")
}

tasks {
    register("serveur") {
        group = "application"
        application {
            mainClass.set("projet.cpoo.Serveur")
        }
        dependsOn(run)
    }
}