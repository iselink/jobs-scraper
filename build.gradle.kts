plugins {
    id("java")
}

group = "net.iselink"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.jsoup:jsoup:1.15.4")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("org.mozilla:rhino:1.7.14")
    implementation("org.mozilla:rhino-engine:1.7.14")
    implementation("org.mozilla:rhino-runtime:1.7.14")
}

//https://stackoverflow.com/questions/75715341/toolchain-from-executable-property-does-not-match-toolchain-from-javalauncher
//or worth mentioning separately
//https://youtrack.jetbrains.com/issue/IDEA-316081/Gradle-8-toolchain-error-Toolchain-from-executable-property-does-not-match-toolchain-from-javaLauncher-property-when-different
gradle.taskGraph.whenReady {
    val task = this.allTasks.find { it.name.endsWith(".main()") } as? JavaExec // or whatever other method your Main class runs
    task?.let {
        it.setExecutable(it.javaLauncher.get().executablePath.asFile.absolutePath)
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "net.iselink.jobsscraper.Main"
    }
}

val fatJar = task("fatJar", type = Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "net.iselink.jobsscraper.Main"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}