
plugins {
	id("fabric-loom") version "1.10-SNAPSHOT" // Fabric Loom
	id("io.github.p03w.machete") version "1.1.4" // Build jar compression
	id("me.modmuss50.mod-publish-plugin") version "0.4.5" // Mod publishing

	id("maven-publish") // Maven publishing
	id("java")
}

//////
fun property(name: String): String = project.properties[name].toString()
fun fabricApiModule(name: String, version: String? = null): Dependency {
	if (version == null) {
		return fabricApi.module(name, (project.findProperty("deps.fabricApi") ?: throw IllegalArgumentException("Fabric API version (deps.fabricApi) is not set")).toString())
    }
	return fabricApi.module(name, version)
}
//////
val javaVersion = JavaVersion.forClassVersion(44 + property("mod.java").toInt())
val minecraftVersion = property("mod.minecraft")
val loaderVersion = property("mod.loader")

val modVersion: String = file("VERSION").readText().trim()
val modGroup = property("mod.group")
val modId = property("mod.id")
val modName = property("mod.name")
val modDescription = property("mod.description")

val group = modGroup
version = modVersion
//////

base {
	archivesName = "$modId-$minecraftVersion"
}

repositories {
	maven("https://maven.shedaniel.me/")
	maven("https://maven.terraformersmc.com/releases/")
}

loom {
	// Check if the access widener exists
	accessWidenerPath = file("src/main/resources/${modId}.accesswidener").takeIf { it.exists() }
}


dependencies {
	minecraft("com.mojang:minecraft:${minecraftVersion}")
	// There should be an error if both Yarn and Parchment mappings are specified
	if (project.findProperty("mod.yarn") != null) {
		mappings("net.fabricmc:yarn:${property("mod.yarn")}:v2")
	} else if (project.findProperty("mod.parchment") != null) {
		val parchmentVersion = property("mod.parchment")
		@Suppress("UnstableApiUsage")
		mappings(loom.layered {
			officialMojangMappings()
			if (parchmentVersion.contains(":"))
				parchment("org.parchmentmc.data:parchment-${parchmentVersion}@zip") // Use exact version
			else
				parchment("org.parchmentmc.data:parchment-${minecraftVersion}:${parchmentVersion}@zip") // Use minecraft version + given date
		})
	} else {
		mappings(loom.officialMojangMappings())
	}

	modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")

	// Fabric API
	modImplementation(fabricApiModule("fabric-api-base"))
	modImplementation(fabricApiModule("fabric-networking-api-v1"))
	modImplementation(fabricApiModule("fabric-rendering-v1"))

	// Other mods might need different modules
	// modRuntimeOnly(fabricApiModule("fabric-api"))
	modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:${property("deps.fabricApi")}")

	modApi("com.terraformersmc:modmenu:${property("deps.modMenu")}")

	modApi("me.shedaniel.cloth:cloth-config-fabric:${property("deps.clothConfig")}") {
		exclude("net.fabricmc.fabric-api")
	}
}

tasks {
	processResources {
		inputs.property("modLoader", loaderVersion)
		inputs.property("modJava", java.targetCompatibility.majorVersion)
		inputs.property("modName", modName)
		inputs.property("modVersion", modVersion)
		inputs.property("modDescription", modDescription)
		project.findProperty("deps.fabricApi") ?: inputs.property("depsFabricApi", property("deps.fabricApi"))

		filteringCharset = "UTF-8"

		filesMatching("fabric.mod.json") {
			expand("modLoader" to loaderVersion,
				"modJava" to java.targetCompatibility.majorVersion,
				"modName" to modName,
				"modVersion" to modVersion,
				"modDescription" to modDescription
			)
			project.findProperty("deps.fabricApi") ?: expand("depsFabricApi" to property("deps.fabricApi"))
		}
	}

	java {
		sourceCompatibility = javaVersion
		targetCompatibility = javaVersion

		withSourcesJar()
	}

	jar {
		from("LICENSE") {
			rename { "${it}_${base.archivesName.get()}"}
		}
	}

	publishMods {
		file = remapJar.get().archiveFile
		changelog = providers.environmentVariable("CHANGELOG").getOrElse("No changelog provided")
		type = BETA
		displayName = "$modName $minecraftVersion $modVersion"
		modLoaders.add("fabric")
		dryRun = providers.environmentVariable("CI").getOrNull() == null

		curseforge {
			accessToken = providers.environmentVariable("CURSEFORGE_API_KEY")
			projectId = "852036"
			minecraftVersions.add(property("minecraft_version"))
			requires("fabric-api")
			requires("cloth-config")
		}
		modrinth {
			accessToken = providers.environmentVariable("MODRINTH_TOKEN")
			projectId = "HfZKWsjM"
			minecraftVersions.add(property("minecraft_version"))
			requires("fabric-api")
			requires("cloth-config")
		}
	}

	publishing {
		if (System.getenv("MAVEN_URL") != null) {
			publications {
				create<MavenPublication>("jar") {
					repositories {
						maven(System.getenv("MAVEN_URL")) {         // Maven repository URL
							credentials {
								username=System.getenv("MAVEN_USER")
								password=System.getenv("MAVEN_PASSWORD")
							}
						}
					}
					groupId = group
					artifactId = modId

					// Includes jar, sources and dependencies
					from(project.components["java"])
				}
			}
		} else {
			logger.warn("[!] Maven URL is not set, skipped setting up Maven publishing.")
		}
	}

	configureEach {
		if (name.startsWith("publish") || name == "generateMetadataFileForJarPublication") {
			dependsOn("optimizeOutputsOfRemapJar")
		}
	}
}