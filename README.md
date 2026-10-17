# IP Tracker

Aplicație Android în Java pentru căutarea informațiilor despre adrese IP, cu autentificare și istoric local.

## Funcționalități

- autentificare cu e-mail și parolă sau cu Google (Firebase Authentication), înregistrare cont
- căutarea informațiilor despre un IP și despre țara asociată, de la [ipapi.co](https://ipapi.co)
- istoricul căutărilor salvat local cu Room
- operații asincrone (Executor + Handler)
- procesarea datelor JSON
- preferințe și temă

## Tehnologii

Java, Room, Firebase Authentication și Firestore, Google Sign-In, Material Components

## Cerințe

Android Studio, Android SDK 34, dispozitiv sau emulator cu Android 8.0 (API 26) sau mai nou.

## Configurare Firebase

Fișierul `google-services.json` nu este inclus în repository.

1. Se creează un proiect în [Firebase Console](https://console.firebase.google.com).
2. Se adaugă o aplicație Android cu pachetul `com.iptracker.app`.
3. Se activează metodele de autentificare Email/Password și Google.
4. Se descarcă `google-services.json` și se pune în `app/`.

## Rulare

Se deschide folderul în Android Studio, se așteaptă sincronizarea Gradle și se rulează configurația `app`.
