plugins {
    id("compile")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}
