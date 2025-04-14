import java.net.URL

plugins {
    kotlin("jvm") version "2.1.10"
    id("application")
}

repositories {
    mavenCentral()
}

val osName = System.getProperty("os.name")
val targetOs = when {
    osName == "Mac OS X" -> "macos"
    osName.startsWith("Win") -> "windows"
    osName.startsWith("Linux") -> "linux"
    else -> error("Unsupported OS: $osName")
}

val osArch = System.getProperty("os.arch")
val targetArch = when (osArch) {
    "x86_64", "amd64" -> "x64"
    "aarch64" -> "arm64"
    else -> error("Unsupported arch: $osArch")
}

val target = "${targetOs}-${targetArch}"

dependencies {
    implementation("org.jetbrains.skiko:skiko-awt-runtime-$target:0.9.4")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(17)
}

tasks.register<JavaExec>("runTestApp") {
    group = "verification"
    dependsOn("build")
    description = "Run the application during test phase"
    mainClass.set("org.jetbrains.skiko.java2d.TestAppKt")
    jvmArgs("-Djava.library.path=${nativeLibDir.absolutePath}")
    classpath = sourceSets["test"].runtimeClasspath
}

val nativeDir = file("native")
val buildNativeDir = file("native/build")

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("-h", nativeDir.absolutePath))
}

val buildNative by tasks.registering {
    group = "build"
    dependsOn("unzipSkiaNative")
    doLast {
        exec {
            commandLine("cmake", "-S", nativeDir.absolutePath, "-B", buildNativeDir.absolutePath)
//            commandLine("cmake", "-S", nativeDir.absolutePath, "-B", buildNativeDir.absolutePath,
//                "-DCMAKE_BUILD_TYPE=Debug", "-DCMAKE_C_FLAGS=-g", "-DCMAKE_CXX_FLAGS=-g"
//            )
        }
        exec {
            commandLine("cmake", "--build", buildNativeDir.absolutePath, "--config", "Release")
        }
    }
}

tasks.named("build") {
    dependsOn(buildNative)
}

var nativeLibDir = file("$buildNativeDir")
if (System.getProperty("os.name").startsWith("Win")) {
    nativeLibDir = nativeLibDir.resolve("Release")
}
tasks.withType<JavaExec> {
    jvmArgs("-Djava.library.path=${nativeLibDir.absolutePath}")
}

val skiaVersion = "m132-9b3c42e2f9-3"
val skiaBaseUrl = "https://github.com/JetBrains/skia-pack/releases/download/${skiaVersion}"
val skiaNativeDir = file("native/skia")
val skiaDownloadArchive = file("${layout.buildDirectory.get()}/skia-native.zip")

tasks.register("downloadSkiaNative") {
    group = "native"
    description = "Download the Skia native libraries for $target"

    doLast {
        val skiaUrl = "$skiaBaseUrl/Skia-$skiaVersion-$targetOs-Release-$targetArch.zip"

        if (!skiaDownloadArchive.exists()) {
            println("Downloading Skia native library from: $skiaUrl")
            URL(skiaUrl).openStream().use { input ->
                skiaDownloadArchive.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            println("Downloaded Skia archive to: ${skiaDownloadArchive.absolutePath}")
        } else {
            println("Skia archive already downloaded at: ${skiaDownloadArchive.absolutePath}")
        }
    }
}

tasks.register<Copy>("unzipSkiaNative") {
    group = "native"
    description = "Unzip the Skia native libraries into the native directory"

    dependsOn("downloadSkiaNative")

    from(zipTree(skiaDownloadArchive))
    into(skiaNativeDir)

    doLast {
        println("Unzipped Skia native library to: ${skiaNativeDir.absolutePath}")
    }
}
