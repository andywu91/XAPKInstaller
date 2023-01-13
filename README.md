# README #

# XAPKInstaller

[![](https://jitpack.io/v/andywu91/XAPKInstaller.svg)](https://jitpack.io/#andywu91/XAPKInstaller)

A library for install xapk(single apk with obb) or xapk(multiple apk with obb).

## Getting started

In your `build.gradle`:

```groovy
dependencies {
  implementation 'com.github.andywu91:XAPKInstaller:1.0.0'
}

## Usage
you can just check code in app which replace downloadUrl to your own xapk downloadUrl,and then experience it

use [zt-zip](<https://github.com/zeroturnaround/zt-zip>) for unzip xapk,thanks

[中文说明](./README_zh.md)

### update ###

Because Meizu and vivo have modified the system installation-related classes,xapk(split apk) cannot be successfully installed, so check it and do nothing if os is flyme or funtouch os.

In addition, please refer to the article：https://www.jianshu.com/p/580b61ee7aee
