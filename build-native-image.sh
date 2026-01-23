#!/bin/sh
mvn clean package
#java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image \
#  -jar target/check-language-version.jar test2.java
mvn package -Pnative