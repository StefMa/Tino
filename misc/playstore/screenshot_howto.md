# Screenshot How To
We make screenshots for the Play Store entry with Android Studio.
For this we choose the first generation **Pixel** device as a **Frame** and check **Screen Glare**.
Beside of that we make use of Androids [Demo Mode](https://android.googlesource.com/platform/frameworks/base/+/master/packages/SystemUI/docs/demo_mode.md) to make a nice looking statusbar.
The following settings should be used:
```
adb shell am broadcast -a com.android.systemui.demo -e command enter
adb shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm 1000
adb shell am broadcast -a com.android.systemui.demo -e command network -e mobile show -e datatype lte
adb shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false
adb shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false
adb shell am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 4 -e fully true
adb shell am broadcast -a com.android.systemui.demo -e command exit
```

Beside of this we are using [GIMP](https://www.gimp.org/) to manipulate these screenshots in a nice way.
See for example this [Screenshot](screenshot0.png).
The `.xcf` files should be added to this repository.