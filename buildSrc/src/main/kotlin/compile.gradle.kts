// basic compile settings for project and subprojects

plugins {
    groovy
}

dependencies {
    compileOnly("io.openapiprocessor:openapi-processor-api:2024.2")
    implementation(localGroovy())
}
