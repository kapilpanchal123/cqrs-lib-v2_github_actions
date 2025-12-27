version = "0.1.1-SNAPSHOT"
group = "io.github.kapil-panchal"

plugins {
    `java-library`
    alias(libs.plugins.publish.to.maven)
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.jdbc)
    implementation(libs.jackson)
    compileOnly(libs.jakarta.persistence)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(libs.guava)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

//tasks.jar {
//    manifest {
//        attributes(mapOf("Library-Title" to project.name,
//            "Library-Version" to project.version))
//    }
//}

mavenPublishing {
    coordinates(group.toString(), name.toString(), version.toString())

    pom {
        name.set("CQRS Library")
        description.set("Command Query Responsibility Segregation - Library.")
        inceptionYear.set("2025")
        url.set("https://github.com/kapil-panchal")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("kapil-panchal")
                name.set("Kapil Panchal")
                url.set("https://github.com/kapil-panchal")
            }
        }
        scm {
            url.set("https://github.com/CQRS-Library-Architecture/cqrs-lib-v2")
            connection.set("scm:git:git://github.com/CQRS-Library-Architecture/cqrs-lib-v2.git")
            developerConnection.set("scm:git:ssh://git@github.com:CQRS-Library-Architecture/cqrs-lib-v2.git")
        }
    }
}