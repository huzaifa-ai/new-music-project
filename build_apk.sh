#!/bin/bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"

echo "Listing available tasks..."
./gradlew tasks

echo "Building APK..."
./gradlew assembleDebug 