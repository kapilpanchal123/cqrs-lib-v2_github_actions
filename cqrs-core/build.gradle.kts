/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
version = "0.2.3"
group = "io.github.kapil-panchal"

plugins {
  `java-library`
  alias(libs.plugins.publish.to.maven)
  alias(libs.plugins.osgipublish)
  jacoco
}

repositories {
  mavenCentral()
}

dependencies {
  api(libs.jacksonjsr310)
  api(libs.jacksondatabind)
  api(libs.slf4j)
  implementation(libs.jackson)
  compileOnly(libs.jakarta.persistence)
  testImplementation(libs.junit.jupiter)
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation(libs.mockito.core)
  testImplementation(libs.mockito.junit.jupiter)
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

tasks.test {
  finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.required = false
    csv.required = false
    html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
  }
}

mavenPublishing {
  coordinates(group.toString(), name.toString(), version.toString())

  pom {
    name.set("CQRS Core Library")
    description.set("Command Query Responsibility Segregation - Library")
    inceptionYear.set("2025")
    url.set("https://github.com/CQRS-Library-Architecture/cqrs-lib-v2")
    licenses {
      license {
        name.set("The Apache License, Version 2.0")
        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
        distribution.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
      }
    }
    developers {
      developer {
        id.set("Kapil Panchal")
        name.set("Kapil Panchal")
        url.set("https://github.com/CQRS-Library-Architecture/cqrs-lib-v2")
      }
    }
    scm {
      url.set("https://github.com/CQRS-Library-Architecture/cqrs-lib-v2")
      connection.set("scm:git:git://github.com/CQRS-Library-Architecture/cqrs-lib-v2.git")
      developerConnection.set("scm:git:ssh://git@github.com:CQRS-Library-Architecture/cqrs-lib-v2.git")
    }
  }
}