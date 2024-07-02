plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "MusicAnalyzer"
include("app")
include("core:api")
include("libs:analyzer")
findProject(":libs:analyzer")?.name = "analyzer"
