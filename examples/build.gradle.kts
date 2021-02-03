plugins {
    java
}
dependencies {
    testImplementation(rootProject)
    testImplementation(testFixtures(project(":ext")))
    testImplementation("org.modelingvalue:dclare:1.5.0-BRANCHED")
    testImplementation("org.modelingvalue:immutable-collections:1.5.0-BRANCHED")
    testImplementation("com.intellij:forms_rt:7.0.3")
}
