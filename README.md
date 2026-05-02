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

Widget metadata lives in `binaryclock/src/androidMain/res/xml/binary_clock_widget_info.xml`. Android launchers and the OS throttle app widget updates, so the widget schedules minute refreshes through its receiver instead of relying on the platform periodic app widget update mechanism. The module declares `android.permission.SCHEDULE_EXACT_ALARM` for these clock refresh alarms and falls back to inexact alarm windows if exact alarms are not allowed, which can reduce refresh accuracy.

Build and test this module from the consuming Gradle project that includes this repository, for example with the module path configured by that project:

```sh
./gradlew :binaryclock:check
```

## APK build and publish workflow

`.github/workflows/build-publish-apk.yml` builds a release APK for an Android application module on pull requests, manual runs, and `v*` tags, then uploads it as a workflow artifact. Pull request builds use a debug signature and do not publish releases. When `publish_release` is enabled for a manual run, or when a `v*` tag is pushed, it signs the APK and uploads it to a GitHub Release.

The workflow reuses the same signing key between builds through repository secrets. Generate the release keystore once, base64-encode it, and store these secrets in GitHub:

- `ANDROID_SIGNING_KEYSTORE_BASE64`
- `ANDROID_SIGNING_KEYSTORE_PASSWORD`
- `ANDROID_SIGNING_KEY_ALIAS`
- `ANDROID_SIGNING_KEY_PASSWORD`

The APK version code is automatically set to `github.run_number`, so each workflow run increments the version code. Tagged builds use the tag name without a leading `v` as the version name; non-tag builds use `0.1.<run_number>`. Manual release runs can publish to a specific GitHub Release by setting `release_tag`; otherwise the workflow uses `apk-<versionCode>`.

Manual runs accept an Android application module path, defaulting to `app`. If this repository snapshot does not contain a root Gradle wrapper or Android application module, the workflow still publishes a minimal Binary Clock demo APK built directly with Android SDK tools.
