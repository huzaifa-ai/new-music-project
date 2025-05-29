#!/bin/bash

# Direct build and run script for Mental Fitness App

echo "Building and running Mental Fitness App..."

# Set environment
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export PATH=$PATH:~/Library/Android/sdk/platform-tools:~/Library/Android/sdk/emulator

# Direct java command to run gradle
echo "Building APK..."
"$JAVA_HOME/bin/java" \
    -Xmx2048m \
    -Dfile.encoding=UTF-8 \
    -Dorg.gradle.appname=gradlew \
    -classpath gradle/wrapper/gradle-wrapper.jar \
    org.gradle.wrapper.GradleWrapperMain \
    app:assembleDebug

if [ $? -eq 0 ]; then
    echo "Build successful! Installing to emulator..."
    
    # Install the APK
    adb install -r app/build/outputs/apk/debug/app-debug.apk
    
    if [ $? -eq 0 ]; then
        echo "Installation successful! Launching app..."
        
        # Launch the app
        adb shell am start -n com.example.mentalfitness/.MainActivity
        
        echo "App should now be running on your emulator!"
    else
        echo "Installation failed!"
    fi
else
    echo "Build failed!"
fi 