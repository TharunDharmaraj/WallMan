# Building project for Android

## Requirements

- Gradle 8 compatible IDE(e.g. `Android Studio Canary`)

## Creating local files

Create file `local.properties` with `sdk.dir` property(your path to `Android Sdk`).
Sample `local.properties`:

```
sdk.dir=/home/user/Android/Sdk
```

Make sure that your Gradle java version is 18(e.g. coretto-18). In `Android Studio` it's located
at `Settings -> Build, Execution, Deployment -> Build tools -> Gradle -> Gradle JDK`

To generate baseline profile, there is pre-defined run configuration for `IntelliJ Idea`
called `Generate Baseline Profile`. Before running, make sure Android device is connected and it's
API >= 28. You will need to call `adb root` to enter adb root mode(note that it won't work on most
physical devices, so test on emulator).
If device API >= 33, no extra work needed and generating profiles can be done on physical device
(also note that generating profiles won't work on security-hardened Android versions, e.g.
GrapheneOS).

To run in release build variant, create `key.jks` file. Also fill `storePassword`, `keyAlias`
and `keyPassword` with real ones
in [AppDefaultPlugin](../buildSrc/src/main/kotlin/com/colorata/wallman/buildSrc/AppDefaultPlugin.kt)

## Running & packaging

To run on device, just run play button in `Intellij Idea` or `Android Studio`(or run gradle
task  `Gradle(Task bar) -> WallMan -> app -> Tasks -> install -> installRelease`)

To package apk, go to `Gradle(Task bar) -> WallMan -> app -> Tasks -> other -> packageRelease`. Apk
is located at `app/build/outputs/apk/release/`

## Troubleshooting

If you encounter issues when building `buildSrc` folder, remove weird import
lines [Ext.kt](../buildSrc/src/main/kotlin/Ext.kt):

```kotlin
import gradle.kotlin.dsl.accessors._fb1e36eb481082a315bbec520f7f46ed.android
import gradle.kotlin.dsl.accessors._fb1e36eb481082a315bbec520f7f46ed.kotlin
import gradle.kotlin.dsl.accessors._fb1e36eb481082a315bbec520f7f46ed.sourceSets
```

Then invalidate caches in `IntelliJ Idea` and import these functions again with autocomplete
in [Ext.kt](../buildSrc/src/main/kotlin/Ext.kt)
