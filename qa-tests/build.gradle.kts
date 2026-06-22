plugins {
    java
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.cucumber:cucumber-java:7.18.1")
    testImplementation("io.cucumber:cucumber-junit-platform-engine:7.18.1")
    testImplementation("org.junit.platform:junit-platform-suite:1.10.2")
    testImplementation("io.rest-assured:rest-assured:5.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}

tasks.test {
    useJUnitPlatform()
    systemProperty("qacopilot.dryRun", System.getProperty("qacopilot.dryRun", "false"))
    systemProperty("api.base.url", System.getProperty("api.base.url", "http://localhost:8080"))
    val tagFilter = System.getProperty("cucumber.filter.tags", "").trim()
    if (tagFilter.isNotEmpty()) {
        systemProperty("cucumber.filter.tags", tagFilter)
    }
}
