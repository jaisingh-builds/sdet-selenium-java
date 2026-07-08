# W6D2 Gradle Setup For Existing Maven Selenium Project

This zip adds Gradle support beside the existing Maven build.

Do not delete `pom.xml`. Do not run `gradle init`.

## Copy Files

Extract this zip into the root of your Selenium Java project, where `pom.xml` already exists.

After extraction, these files should be visible:

```text
build.gradle.kts
settings.gradle.kts
gradle.properties
gradlew
gradlew.bat
gradle/wrapper/gradle-wrapper.jar
gradle/wrapper/gradle-wrapper.properties
scripts/day2-gradle-demo.ps1
scripts/day2-gradle-demo.sh
```

## Windows Commands

Open PowerShell in the project folder.

Check Java:

```powershell
java -version
```

Java 17 or above is fine.

Check Gradle wrapper:

```powershell
.\gradlew.bat --version
```

Run the Gradle build gate:

```powershell
.\gradlew.bat clean build
```

Run the Week 6 Day 2 structure check:

```powershell
.\gradlew.bat w6d1StructureTest
```

Run the parallel demo:

```powershell
.\gradlew.bat parallelStructureTest
```

Run the demo script:

```powershell
.\scripts\day2-gradle-demo.ps1
```

## Optional Browser Proof

Start the retail app first, then run:

```powershell
.\gradlew.bat w6d1CheckoutTest -Pheadless=true -PbaseUrl=http://localhost:5173
```

## Troubleshooting

If Gradle behaves strangely:

```powershell
.\gradlew.bat --stop
Remove-Item -Recurse -Force .gradle, build -ErrorAction SilentlyContinue
.\gradlew.bat clean build
```

If `.\gradlew.bat --version` cannot download Gradle, it is usually a proxy/Zscaler/certificate issue. Share the exact error screenshot with the trainer.
