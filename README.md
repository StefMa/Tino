# Tino - A Digital Wellbeing Experiment
Tino is a digital wellbeing experiment by give you a deep overview how many notifications your phone arrives.

<img src="misc/images/motivation.png" />

The main idea is that you can see how long a notification stays in average in the NotificationBar.
But you get also deep insights in other statistics like
* How many notification you received in overall
* Which App sends you the most notification and how many
* How long stayed the longest notification in the NotificationBar
* The sum of how long all of the notifications stayed in the NotificationBar since App installation

You could also checkout the same statistics of your friends.
Simple search for your friends name and see their notification statistics.

The App should help you to get an overview about how many notifications you receive on your device.
These information could then be used to reflect your Smartphone usage and if it would make sense
to disable a few App notifications so you don't get distracted from your real life anymore!

## Development
### Local vs Cloud (debug vs release)
Tino has at the time of writing two different build types: `debug` and `release`.
While its obvious that the `release` type uses a different keystore (cause its published to the Play Store)
and uses R8 (more about this below) there is also a difference how the app behave.
If you run the `debug` type we store everything locally in a local `database` while
the `release` type uses Firebase Firestore to store data.

### Firebase
We make use of Firebase **Authentication** and **Firestore** for the `release` build type.
Therefore, if you build this App with the `release` type by your own, you have to do the following:
* Setup a new Firebase project (or use an existing)
* Enable **Firebase Authentication** and enable **Anonymous** users
* Enable **Firestore** (if required to enable it) and set the following rules:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth.uid != null;
    }
  }
}
```
* Setup the App in the Firebase console and place the `google-services.json` into [`app/`](app/).

### App signing
You know that you have to sign the App before you can publish it to the Play Store.

The `debug` build type is, even if it is possible, not intended to be published.
Therefore this version will always be signed with the "default" Android Debug Keystore
which is part of this repository.

If you want to sign the `release` App you have to add a file called `release.keystore` inside [`app`](app/).
Additionally you have to provide the following [Gradle properties](https://docs.gradle.org/6.2.2/userguide/build_environment.html#sec:gradle_configuration_properties)
to successfully sign the `release` build:
```
Tino.Keystore.Password
Tino.Keystore.KeyAlias
Tino.KeyStore.KeyPassword
```
The build script will automatically use the `release` `signingConfig` if everything is provided.
Otherwise it will use the `debug` `signingConfig` which is the same configuration as for the `debug` build type.

### R8 and deobfuscating
We are using R8 on `release` builds to obfuscate and shrink our code.

To deobfuscate stacktrace you can use the [`retrace.jar` tool](https://www.guardsquare.com/en/products/proguard/manual/retrace) which comes togehter with the Android SDK.
Put the stracktrace in a file (name doesn't matter) and run the following:
```
java -jar /path/to/android/sdk/tools/proguard/lib/retrace.jar -verbose /path/to/tino/app/build/outputs/mapping/release/mapping.txt /path/to/stacktrace
```

Because of this the `mapping.txt` file should be also added as a **assett* to the **releases**.

## Image credits
I found the dinosaur at [flaticon.com](https://www.flaticon.com/free-icon/diplodocus_1939441).

To respect their attribution:
```
"Icon made by [Darius Dan](https://www.flaticon.com/authors/darius-dan) from www.flaticon.com"
```

According to their [attribution page](https://support.flaticon.com/hc/en-us/articles/207248209-How-I-must-insert-the-attribution-)
I also have to add this information to the Play Store description.