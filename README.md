# XAudioCapture

Record any audio stream you want.

Since Android 10, the system introduced the AudioPlaybackCapture API
for capturing internal audio playback. It also implemented policies that
allow applications to decide whether to permit their audio output to be recorded by
other applications or the system. 

This Xposed module let you to capture any audio stream you desire, bypassing these restrictions.

## Features

- Reasonable UI
- Hooks **both** `ApplicationInfo.privateFlags` **and** 
  `AudioAttributes.Builder#setAllowedCapturePolicy(int)`.
- May Works with multi-user (not tested yet)

## How it works?
This module inject hooks to the `android` system package, and add
`PRIVATE_FLAG_ALLOW_AUDIO_PLAYBACK_CAPTURE` into Application's privateFlags.

## Usage
1. Enable `XAudioCapture` (this module) in your LSPosed framework setting
2. Hook the `android` system package in scope setting
3. Launch `XAudioCapture` UI, select the app you want to record
4. kill and restart the victim app
5. Enjoy!

## Notes

1. Only tested on LSPosed.
2. If recording still didn't work, also hook the target application
   in scope setting. This will hook `AudioAttributes.Builder#setAllowedCapturePolicy(int)`
   while the target application calls it.
3. For users with Work profile, install this all in **both** your Main profile
   and Work profile. Attach to `android` package in your **Main** profile,
   and tick the apps you want to record in the according profile.

## Special Thanks

All the UI is forked from `XAppDebug`

- [XAppDebug](https://github.com/Palatis/XAppDebug)
