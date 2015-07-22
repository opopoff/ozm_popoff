# OZM

## Сборка

Собрать  debug  сборку:
```
./gradlew installInternalDebug
```
Собрать  stage сборку:
```
./gradlew installStageRelease
```
Собрать  release сборку:
```
./gradlew installProductionRelease
```

## Crashlitics Beta

Залить  stage сборку на Crashlitics Beta:
```
./gradlew assembleStageRelease crashlyticsUploadDistributionStageRelease
```

Номера сборки и кода задаются в файле `./gradle.properties`:
```
# Version build and code indexes;
VERSION_MAJOR=0
VERSION_MINOR=0
VERSION_PATCH=0
VERSION_BUILD=0
```
и формируются по правилу:
```
versionCode = versionMajor * 10000 + versionMinor * 1000 + versionPatch + 100 * versionBuild
versionName = "${versionMajor}.${versionMinor}.${versionPatch}"
```

## Подпись приложения

Файлы подписи приложения хранятся в каталоге `./disribution/*`:
* `release.keystore` - релизный ключ;
* `debug.keystore` - дебажный ключ;
* `README` - Подробное описание о содании ключа (alias, password and etc.);