# README

# XAPKInstaller

[![](https://jitpack.io/v/andywu91/XAPKInstaller.svg)](https://jitpack.io/#andywu91/XAPKInstaller)

实现安装xapk(单apk加上obb资源文件)或者xapk（多apk）

## 开始使用

在你的根`build.gradle`中添加jitpack存储库

```groovy
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

在`build.gradle`添加如下代码:

```groovy
dependencies {
  implementation 'com.github.andywu91:XAPKInstaller:1.0.0'
}
```

## 使用说明

你可以将app代码里的downloadUrl替换成自己的xapk的downloadUrl,即可体验此项目

## 感谢

使用 [zt-zip](<https://github.com/zeroturnaround/zt-zip>) 来解压xapk,感谢

## 更新

因为魅族和vivo对系统安装相关的类进行了修改，导致split apk的xapk无法安装成功，故进行检查，如果是flyme或者funtouch os则不做处理。

另外可以参考文章：https://www.jianshu.com/p/580b61ee7aee