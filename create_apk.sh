#!/bin/bash

echo "Mental Fitness App - APK Builder"
echo "================================"

# Set up environment
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export PATH=$PATH:~/Library/Android/sdk/platform-tools

echo "1. Opening Android Studio with the project..."
open -a "Android Studio" "/Users/affanlaptops/Downloads/project 4"

echo ""
echo "Instructions to create APK:"
echo "1. Wait for Android Studio to load and Gradle sync to complete"
echo "2. Go to 'Build' menu → 'Build Bundle(s) / APK(s)' → 'Build APK(s)'"
echo "3. Wait for the build to complete"
echo "4. Click 'locate' when the build finishes to find the APK file"
echo "5. The APK will be in: app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "Alternative method:"
echo "1. Open Terminal tab in Android Studio (View → Tool Windows → Terminal)"
echo "2. Run: ./gradlew assembleDebug"
echo "3. APK will be created in app/build/outputs/apk/debug/"
echo ""
echo "Transfer to your phone:"
echo "1. Copy the APK to your phone via USB, email, or cloud storage"
echo "2. On your phone, enable 'Install from unknown sources' in Security settings"
echo "3. Tap the APK file to install"
echo ""
echo "Waiting for you to build the APK... Press any key when done."
read -n 1 -s

# Check if APK was created
if [ -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
    echo "✓ APK found! Opening the folder..."
    open app/build/outputs/apk/debug/
    echo "APK location: $(pwd)/app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ APK not found. Please follow the build instructions above."
fi 