# OZM

## Builds

Build debug apk:
```
./gradlew assembleDebug
```
Build internal apk:
```
./gradlew assembleInternal
```
Build stage apk:
```
./gradlew assembleStage
```
Build release apk:
```
./gradlew assembleProduction
```
All build stored in catalog:
```
./app/build/outputs/apk/
```

## Crashlitics Beta

Deploy internal build on Crashlitics Beta:
```
./gradlew assembleInternal crashlyticsUploadDistributionInternal
```

Deploy stage build on Crashlitics Beta:
```
./gradlew assembleStage crashlyticsUploadDistributionStage
```

Version code and version build places at file `./gradle.properties`:
```
# Version build and code indexes;
VERSION_MAJOR=0
VERSION_MINOR=0
VERSION_PATCH=0
VERSION_BUILD=0
```
and creates by rule:
```
versionCode = versionMajor * 10000 + versionMinor * 1000 + versionPatch + 100 * versionBuild
versionName = "${versionMajor}.${versionMinor}.${versionPatch}"
```

Testers emails:
```
./app/fabric_beta_distribution_emails.txt
```

Release notes:
```
./app/fabric_beta_release_notes.txt
```

## Keystore

Keystores places in catalog `./disribution/*`:
* `release.keystore` - release key;
* `debug.keystore` - debug key;
* `README` - description about created keys (alias, password and etc.);