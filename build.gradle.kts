plugins {
    java
}

group = "com.ust.sdet"
version = "0.1.0"

val seleniumVersion = "4.45.0"
val selenideVersion = "7.16.2"
val restAssuredVersion = "6.0.0"
val assertjVersion = "3.27.6"
val junitVersion = "5.14.4"
val cucumberVersion = "7.34.3"
val allureVersion = "2.33.0"
val extentVersion = "5.1.2"
val extentCucumberAdapterVersion = "1.14.0"
val slf4jVersion = "2.0.17"
val testcontainersVersion = "2.0.5"
val flywayVersion = "10.22.0"
val postgresqlVersion = "42.7.4"
val mysqlVersion = "9.4.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation(platform("io.cucumber:cucumber-bom:$cucumberVersion"))
    testImplementation(platform("io.qameta.allure:allure-bom:$allureVersion"))
    testImplementation(platform("org.testcontainers:testcontainers-bom:$testcontainersVersion"))

    testImplementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("io.rest-assured:json-schema-validator:$restAssuredVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    testImplementation("com.codeborne:selenide:$selenideVersion")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.cucumber:cucumber-java")
    testImplementation("io.cucumber:cucumber-junit-platform-engine")
    testImplementation("io.cucumber:cucumber-picocontainer")
    testImplementation("org.junit.platform:junit-platform-suite")
    testImplementation("io.qameta.allure:allure-cucumber7-jvm")
    testImplementation("io.qameta.allure:allure-junit5")
    testImplementation("com.aventstack:extentreports:$extentVersion")
    testImplementation("tech.grasshopper:extentreports-cucumber7-adapter:$extentCucumberAdapterVersion")
    testImplementation("org.slf4j:slf4j-simple:$slf4jVersion")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:testcontainers-postgresql:$testcontainersVersion")
    testImplementation("org.flywaydb:flyway-core:$flywayVersion")
    testImplementation("org.flywaydb:flyway-database-postgresql:$flywayVersion")
    testImplementation("org.postgresql:postgresql:$postgresqlVersion")
    testImplementation("com.mysql:mysql-connector-j:$mysqlVersion")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(17)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    systemProperty("baseUrl", providers.gradleProperty("baseUrl").orElse("http://localhost:5173").get())
    systemProperty("headless", providers.gradleProperty("headless").orElse("false").get())
    systemProperty("browser", providers.gradleProperty("browser").orElse("chrome").get())
    systemProperty("build.label", providers.gradleProperty("buildLabel").orElse("gradle-local").get())
    systemProperty("cucumber.publish.quiet", "true")
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT
    }
}

fun Test.useProjectTestClasses() {
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
}

tasks.test {
    description = "Runs the default classroom-safe Gradle checks without launching a browser."
    include("**/Day4SolidStructureTest.class")
    include("**/W6D1RefactoringStructureTest.class")
    include("**/W6D3TestDataBuilderStructureTest.class")
    include("**/W6D4ReportingInsightsTest.class")
    maxParallelForks = 1
}

val w6d1StructureTest by tasks.registering(Test::class) {
    description = "Runs the safe no-browser Week 6 Day 1 structure checks."
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/W6D1RefactoringStructureTest.class")
}

val w6d1CheckoutTest by tasks.registering(Test::class) {
    description = "Runs the optional browser Week 6 Day 1 checkout proof."
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/W6D1RefactoredCheckoutFlowTest.class")
    maxParallelForks = 1
}

val cucumberSmoke by tasks.registering(Test::class) {
    description = "Runs Cucumber smoke scenarios through the Gradle JUnit Platform."
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/RunCucumberTest.class")
    systemProperty("cucumber.filter.tags", "@smoke")
    maxParallelForks = 1
}

val parallelStructureTest by tasks.registering(Test::class) {
    description = "Demonstrates Gradle test forks with no-browser checks."
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/W6D1RefactoringStructureTest.class")
    maxParallelForks = Runtime.getRuntime().availableProcessors().coerceAtMost(2)
}

val w6d3BuilderStructureTest by tasks.registering(Test::class) {
    description = "Runs no-Docker Week 6 Day 3 builder and reset-strategy checks."
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/W6D3TestDataBuilderStructureTest.class")
}

val w6d3DataStrategyTest by tasks.registering(Test::class) {
    description = "Runs Week 6 Day 3 Testcontainers/Flyway isolated Postgres tests."
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/OrdersDataIT.class")
    maxParallelForks = 1
}

val w6d4ReportingInsightsTest by tasks.registering(Test::class) {
    description = "Runs no-browser Week 6 Day 4 Allure reporting checks."
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/W6D4ReportingInsightsTest.class")
}

val w6d4FailureReportDemo by tasks.registering(Test::class) {
    description = "Runs intentional Week 6 Day 4 failures so Allure categories have visible buckets."
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/W6D4IntentionalFailureDemo.class")
    ignoreFailures = true
    maxParallelForks = 1
}

val w7d1E2E by tasks.registering(Test::class) {
    description = "Runs the Week 7 Day 1 UI, API, database and contract journeys."
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/RunW7D1E2ETest.class")
    systemProperty("w7d1.runtime", providers.gradleProperty("w7d1Runtime").orElse("container").get())
    systemProperty("shopkart.root", providers.gradleProperty("shopkartRoot").orElse("../sdet-retail-app").get())
    maxParallelForks = 1
}

val w7d1AiReview by tasks.registering(Test::class) {
    description = "Checks that the AI draft contains the five teaching defects and the hardened flow removes them."
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/W7D1AiHardeningReviewTest.class")
}

tasks.register("w6d2BuildSummary") {
    description = "Prints the Week 6 Day 2 Maven to Gradle command map."
    group = "help"
    doLast {
        println(
            """
            W6D2 Build Tooling Summary
            Maven compile: mvn clean test-compile
            Gradle compile: ./gradlew clean testClasses
            Maven structure: mvn clean -Dtest=W6D1RefactoringStructureTest test
            Gradle structure: ./gradlew clean w6d1StructureTest
            Gradle smoke: ./gradlew cucumberSmoke -Pheadless=true
            Gradle W6D3 builder: ./gradlew w6d3BuilderStructureTest
            Gradle W6D3 data IT: ./gradlew w6d3DataStrategyTest
            Gradle W6D4 reporting: ./gradlew w6d4ReportingInsightsTest
            Gradle W6D4 failure demo: ./gradlew w6d4FailureReportDemo
            Gradle W7D1 E2E: ./gradlew w7d1E2E -Pw7d1Runtime=container -Pheadless=true
            Gradle W7D1 AI review: ./gradlew w7d1AiReview
            Gradle scan: ./gradlew w6d1StructureTest --scan
            """.trimIndent()
        )
    }
}
