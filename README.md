# OZM

## Сборка

Собрать  debug  сборку:
```
./gradlew assembleInternalDebug
```
Собрать  stage сборку:
```
./gradlew assembleStageRelease
```
Собрать  release сборку:
```
./gradlew assembleProductionRelease
```
Сборки хранятся в каталоге
```
./app/build/outputs/apk/
```

## Crashlitics Beta

Залить stage сборку на Crashlitics Beta:
```
./gradlew assembleStageRelease crashlyticsUploadDistributionStageRelease
```

Залить debug сборку на Crashlitics Beta:
```
./gradlew assembleInternalDebug crashlyticsUploadDistributionInternalDebug
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

Список emails тестировщиков задается в файле:
```
./app/fabric_beta_distribution_emails.txt
```

Информация по текущей сборке вручную заночится в файл:
```
./app/fabric_beta_release_notes.txt
```

## Подпись приложения

Файлы подписи приложения хранятся в каталоге `./disribution/*`:
* `release.keystore` - релизный ключ;
* `debug.keystore` - дебажный ключ;
* `README` - Подробное описание о содании ключа (alias, password and etc.);