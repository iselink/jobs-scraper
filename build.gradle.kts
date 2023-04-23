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