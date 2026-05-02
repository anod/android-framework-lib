# android-framework-lib

Android standard framework extensions

## AppLog - Wrapper on top of android.util.Log class:
 - Formats messages with method name
 - Add support for exception listeners

## Animations
 - RevealAnimatorCompat
 - ResizeAnimator
 - AnimatorCollection - performs animation with list of Animators

## Images
 - Images MemoryCache
 - ImageLoader using MemoryCache

## Binary Clock Glance Widget

The `binaryclock` module provides a minimal Android home screen widget built with Jetpack Glance. It exposes `BinaryClockGlanceWidget` through `BinaryClockWidgetReceiver`, so an Android app can include the module and merge the receiver metadata into its manifest.

The widget renders the current 24-hour time as six columns for `HHMMSS`. Each decimal digit is shown as four vertical bits with values `8`, `4`, `2`, and `1` from top to bottom. Bright dots are active bits and muted dots are inactive bits, using a dark monochrome style.

Widget metadata lives in `binaryclock/src/androidMain/res/xml/binary_clock_widget_info.xml`. Android launchers and the OS throttle app widget updates, so the widget supports seconds in the display but should not be treated as a guaranteed per-second clock surface. The metadata uses Android's periodic app widget update mechanism at the platform minimum cadence.

Build and test this module from the consuming Gradle project that includes this repository, for example with the module path configured by that project:

```sh
./gradlew :lib:binaryclock:check
```
