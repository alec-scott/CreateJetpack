import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("net.fabricmc.fabric-loom") version "1.17.20"
    kotlin("jvm") version "2.4.21"
}

val p = { key: String -> providers.gradleProperty(key).get() }
val javaVersion = p("java_version").toInt()

version = "${p("mod_version")}+mc${p("minecraft_version")}-fabric.${p("port_build")}"
group = p("maven_group")
base { archivesName = "create_jetpack-fabric" }

repositories {
    // Flight Lib 26.1 port: publishToMavenLocal from ../FlightLib (branch 26.1-fabric)
    mavenLocal {
        content { includeGroup("com.possible-triangle") }
    }
    mavenCentral()
    maven("https://maven.fabricmc.net/")
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
}

sourceSets.main {
    // Upstream's committed datagen output (Registrate datagen is not run in this port; see PORTING-26.1.md)
    resources.srcDir("src/generated/resources")
    resources.exclude(".cache/**", "META-INF/neoforge.mods.toml", "pack.mcmeta", "**/*.xcf")
}

dependencies {
    // 26.1 is unobfuscated: no mappings, and Loom no longer remaps.
    minecraft("com.mojang:minecraft:${p("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${p("fabric_loader_version")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${p("fabric_api_version")}")
    implementation("net.fabricmc:fabric-language-kotlin:${p("fabric_kotlin_version")}")
    implementation("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:${p("forgeconfigapiport_version")}")

    implementation(files(p("create_fly_jar")))

    implementation("com.possible-triangle:flightlib-fabric:${p("flightlib_version")}")
    include("com.possible-triangle:flightlib-fabric:${p("flightlib_version")}")
}

tasks.processResources {
    val props = mapOf(
        "version" to project.version,
        "minecraft_version" to p("minecraft_version"),
        "fabric_loader_version" to p("fabric_loader_version"),
        "fabric_kotlin_version" to p("fabric_kotlin_version"),
        "forgeconfigapiport_version" to p("forgeconfigapiport_version"),
        "create_fly_version_range" to p("create_fly_version_range"),
    )
    inputs.properties(props)
    filesMatching("fabric.mod.json") { expand(props) }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

kotlin {
    jvmToolchain(javaVersion)
    compilerOptions.jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
}

tasks.withType<JavaCompile>().configureEach {
    options.release = javaVersion
    options.encoding = "UTF-8"
}

tasks.jar {
    from("LICENSE.md") { rename { "LICENSE_create_jetpack.md" } }
}
