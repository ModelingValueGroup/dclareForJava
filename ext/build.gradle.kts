plugins {
    java
    `java-test-fixtures`
}
dependencies {
    implementation(rootProject)
    implementation("org.modelingvalue:immutable-collections:1.5.0-BRANCHED")
    implementation("org.modelingvalue:dclare:1.5.0-BRANCHED")

    testFixturesImplementation(rootProject)
    testFixturesImplementation("org.modelingvalue:immutable-collections:1.5.0-BRANCHED")
    testFixturesImplementation("org.modelingvalue:dclare:1.5.0-BRANCHED")
}
