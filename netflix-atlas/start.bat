@echo off
SET ATLAS_VERSION=1.6.0
SET JAR_FILE=atlas-%ATLAS_VERSION%-standalone.jar

IF EXIST %JAR_FILE% (
  java -jar %JAR_FILE% memory.conf
) ELSE (
  ECHO Downloading Netflix Atlas Distribution %ATLAS_VERSION%"
  curl --insecure -LO https://github.com/Netflix/atlas/releases/download/v1.4.2/%JAR_FILE%
  curl --insecure -LO https://raw.githubusercontent.com/Netflix/atlas/v1.6.x/conf/memory.conf
  java -jar %JAR_FILE% memory.conf
)


