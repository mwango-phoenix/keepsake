# Keepsake

Keepsake is a simple Android photo widget. Pick photos from your device, place
the widget on your home screen, and browse them with the previous and next
buttons. The widget can also rotate through the selected photos automatically.



[![Watch the video](https://github.com/user-attachments/assets/97051c35-d08f-4964-907b-a00d2c8bc787)](https://github.com/user-attachments/assets/97051c35-d08f-4964-907b-a00d2c8bc787)

<img width="300" height="657" alt="image" src="https://github.com/user-attachments/assets/44c634fb-3437-4dc6-813b-16fee0a8dcf7" />
<img width="300" height="657" alt="image" src="https://github.com/user-attachments/assets/eb350f55-392f-49ba-ae0e-66ff7638a1f1" />

## Download

[Download Keepsake 1.0 (debug APK)](download/Keepsake-v1.0-debug.apk)

This repository currently includes a debug-signed build for testing. Android
may warn that the app is from an unknown source. 

Requirements:

- Android 7.0 (API 24) or newer
- A launcher that supports Android home-screen widgets

## User guide

### 1. Install Keepsake

1. Download the APK above on your Android device.
2. Open the downloaded file.
3. If Android blocks the installation, open the displayed settings page and
   allow **Install unknown apps** for the browser or file manager you used.
4. Return to the installer and tap **Install**.
5. Open **Keepsake**.

On Android 12 and newer, Keepsake may open the **Alarms & reminders** settings.
Allowing this access allows automatic photo change. The widget
can still work without exact-alarm access, but Android may delay rotations.

### 2. Choose photos

1. Open Keepsake and select the **Photos** tab.
2. Tap the photo picker.
3. Select one or more images and confirm.
4. Use **Add more** to add to the slideshow, or remove photos you no longer
   want displayed.


### 3. Add the widget


1. Return to the home screen.
2. Touch and hold an empty area, then choose **Widgets**.
3. Find **Keepsake**.
4. Touch and hold the Keepsake widget, then drag it onto the home screen.
5. Resize it if desired.


### 4. Use the slideshow

- Tap **‹** to show the previous photo.
- Tap **›** to show the next photo.
- Tap the photo itself to open Keepsake.
- Photos also advance automatically.

### 5. Change the frame

1. Open Keepsake.
2. Select the **Widget Settings** tab.
3. Choose **Rectangle**, **Circle**, or **Polaroid**.

The home-screen widget updates as soon as a frame is selected.


## Build from source

Prerequisites:

- Android Studio with Android SDK 36 or newer
- JDK 11 or newer

Clone the repository, open it in Android Studio, let Gradle sync, and run the
`app` configuration on a device or emulator.

To build a debug APK from a terminal:

```powershell
.\gradlew.bat assembleDebug
```

The generated APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Technology

- Kotlin
- Jetpack Compose and Material 3
- Jetpack Glance App Widgets
- WorkManager and Android alarms
