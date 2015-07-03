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

Залить  stage сборку на Crasjliticst Beta:
```
./gradlew assembleStageRelease crashlyticsUploadDistributionStageRelease
```

Номера сборки и кода задаются в файле `./build.gradle` в разделе `ext`:
```
// Version build and code indexes;
versionMajor = 0
versionMinor = 0
versionBuild = 0
```
и формируются по правилу:
```
versionCode = versionMajor * 1000 + versionMinor * 100 + versionBuild
versionName = "${versionMajor}.${versionMinor}"
```

## Подпись приложения

Файлы подписи приложения хранятся в каталоге `./disribution/*`:
* `release.keystore` - релизный ключ;
* `debug.keystore` - дебажный ключ;
* `README` - Подробное описание о содании ключа (alias, password and etc.);