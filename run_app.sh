#!/bin/bash

# Mental Fitness App Runner Script

echo "Mental Fitness App - Android Project"
echo "===================================="

# Set up environment
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
export PATH=$PATH:~/Library/Android/sdk/platform-tools:~/Library/Android/sdk/emulator

echo "1. Checking if emulator is running..."
if adb devices | grep -q "emulator"; then
    echo "✓ Emulator is running"
else
    echo "⚠ Starting emulator..."
    emulator -avd Medium_Phone_API_36 &
    echo "Waiting for emulator to start..."
    sleep 30
fi

echo "2. Opening project in Android Studio..."
open -a "Android Studio" "/Users/affanlaptops/Downloads/project 4"

echo ""
echo "Instructions:"
echo "1. Android Studio should now be open with your project"
echo "2. Wait for Gradle sync to complete"
echo "3. Click the 'Run' button (green play icon) or press Ctrl+R"
echo "4. Select your emulator device if prompted"
echo "5. The Mental Fitness app should install and launch on the emulator"
echo ""
echo "App Features:"
echo "- Login screen with username/password (accepts any input)"
echo "- Home screen with mental fitness features:"
echo "  • Workout Recommendations"
echo "  • Facial Expression Analysis"
echo "  • Weather-Based Suggestions"
echo "  • Journaling"
echo "  • Charts"
echo "  • Music Health Recommendations"
echo "  • Assessment"
echo "  • Music"
echo ""
echo "Note: This is a UI prototype - clicking feature cards shows toast messages" 