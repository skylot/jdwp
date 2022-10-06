plugins {
    `java-library`
	`maven-publish`
	signing
}

repositories {
	mavenLocal()
	mavenCentral()
}

dependencies {

}

group = "io.github.skylot"
version = System.getenv("JDWP_VERSION") ?: "dev"

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
	targetCompatibility = JavaVersion.VERSION_1_8

	withJavadocJar()
	withSourcesJar()
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = "jdwp"
			from(components["java"])
			versionMapping {
				usage("java-api") {
					fromResolutionOf("runtimeClasspath")
				}
				usage("java-runtime") {
					fromResolutionResult()
				}
			}
			pom {
				name.set("jdwp")
				description.set("A library for encoding & decoding JDWP packets. ")
				url.set("https://github.com/skylot/jdwp")
				licenses {
					license {
						name.set("MIT License")
						url.set("http://www.opensource.org/licenses/mit-license.php")
					}
				}
				developers {
					developer {
						id.set("skylot")
						name.set("Skylot")
						email.set("skylot@gmail.com")
						url.set("https://github.com/skylot")
					}
				}
				scm {
					connection.set("scm:git:git://github.com/skylot/jdwp.git")
					developerConnection.set("scm:git:ssh://github.com:skylot/jdwp.git")
					url.set("https://github.com/skylot/jdwp")
				}
			}
		}
	}
	repositories {
		maven {
			val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
			val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
			url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
			credentials {
				username = project.properties["ossrhUser"].toString()
				password = project.properties["ossrhPassword"].toString()
			}
		}
	}
}

signing {
	isRequired = gradle.taskGraph.hasTask("publish")
	sign(publishing.publications["mavenJava"])
}

tasks.javadoc {
	val stdOptions = options as StandardJavadocDocletOptions
	if (JavaVersion.current().isJava9Compatible) {
		stdOptions.addBooleanOption("html5", true)
	}
	// disable 'missing' warnings
	stdOptions.addStringOption("Xdoclint:all,-missing", "-quiet")
}
