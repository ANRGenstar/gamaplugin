#!/bin/sh
echo "genstarpassw=xMY2d63UAUixj3m/AXuCTg\=\=" >> ~/.gradle/gradle.6346B4DDA13C97C4C0BE190A1D1F519E.encrypted.properties
cat ~/.gradle/gradle.6346B4DDA13C97C4C0BE190A1D1F519E.encrypted.properties
gradle clean build -i
gradle deploy -d -PcredentialsPassphrase=$CREDENTIALS_ENV