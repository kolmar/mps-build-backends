import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

group = "de.itemis.mps"

plugins {
    kotlin("jvm")
    `maven-publish`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    maven {
        url = URI("https://projects.itemis.de/nexus/content/repositories/mbeddr")
    }
}

val nexusUsername: String? by project
val nexusPassword: String? by project

val kotlinArgParserVersion: String by project
val mpsVersion: String by project

val kotlinApiVersion: String by project
val kotlinVersion: String by project

val pluginVersion = "2"

version = if (project.hasProperty("forceCI") || project.hasProperty("teamcity")) {
    de.itemis.mps.gradle.GitBasedVersioning.getVersion(mpsVersion, pluginVersion)
} else {
    "$mpsVersion.$pluginVersion-SNAPSHOT"
}


val mpsConfiguration = configurations.create("mps")

dependencies {
    implementation(kotlin("stdlib-jdk8", version = kotlinVersion))
    implementation("com.xenomachina:kotlin-argparser:$kotlinArgParserVersion")
    mpsConfiguration("com.jetbrains:mps:$mpsVersion")
    compileOnly(mpsConfiguration.resolve().map { zipTree(it) }.first().matching { include("lib/*.jar") })
    implementation(project(":project-loader"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.apiVersion = kotlinApiVersion
    kotlinOptions.allWarningsAsErrors = true
}

publishing {
    repositories {
        maven {
            name = "itemis"
            url = uri("https://projects.itemis.de/nexus/content/repositories/mbeddr")
            credentials {
                username = nexusUsername
                password = nexusPassword
            }
        }
    }
}