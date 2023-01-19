package com.wuliang.lib

import android.os.Environment
import android.util.Log
import org.zeroturnaround.zip.NameMapper
import org.zeroturnaround.zip.ZipException
import org.zeroturnaround.zip.ZipUtil
import java.io.File

/**
 * <pre>
 *     author : wuliang
 *     time   : 2019/09/27
 * </pre>
 */

internal const val INSTALL_OPEN_APK_TAG = "install_open_apk_tag"

fun createXapkInstaller(xapkFilePath: String?): XapkInstaller? {
    if (xapkFilePath.isNullOrEmpty()) {
        return null
    }

    val xapkFile = File(xapkFilePath)

    val unzipOutputDirPath = createUnzipOutputDir(xapkFile)
    if (unzipOutputDirPath.isNullOrEmpty()) {
        return null
    }

    val unzipOutputDir = File(unzipOutputDirPath)
    try {
        //只保留apk文件和Android/obb下的文件,以及json文件用于获取主包（当有多个apk时）
        ZipUtil.unpack(xapkFile, unzipOutputDir, NameMapper { name ->
            when {
                name.endsWith(".apk") -> return@NameMapper name
                else -> return@NameMapper null
            }
        })
    } catch (e: ZipException) {
        e.printStackTrace()
        return null
    }

    val files = unzipOutputDir.listFiles()
    val apkSize = files.count { file ->
        file.isFile && file.name.endsWith(".apk")
    }

    if (!unzipObbToAndroidObbDir(xapkFile, File(getMobileAndroidObbDir()))) {
        return null
    }

    return if (apkSize > 1) {
        MultiApkXapkInstaller(xapkFilePath, unzipOutputDir)
    } else {
        SingleApkXapkInstaller(xapkFilePath, unzipOutputDir)
    }
}

private fun createUnzipOutputDir(file: File): String? {
    val filePathPex = file.parent + File.separator
    val unzipOutputDir = filePathPex + getFileNameNoExtension(file)
    val result = createOrExistsDir(unzipOutputDir)

    return if (result)
        unzipOutputDir
    else
        null
}

private fun unzipObbToAndroidObbDir(xapkFile: File, unzipOutputDir: File): Boolean {
    val prefix = "Android/obb"

    try {
        //只保留apk文件和Android/obb下的文件,以及json文件用于获取主包（当有多个apk时）
        ZipUtil.unpack(xapkFile, unzipOutputDir, NameMapper { name ->
            when {
                name.startsWith(prefix) -> return@NameMapper name.substring(prefix.length)
                else -> return@NameMapper null
            }
        })

        Log.d(INSTALL_OPEN_APK_TAG, "unzip obb to Android/obb succeed")
        return true
    } catch (e: ZipException) {
        e.printStackTrace()
        return false
    }
}

/**
 * 得到手机的Android/obb目录
 *
 * @return
 */
fun getMobileAndroidObbDir(): String {
    val path: String = if (isSDCardEnableByEnvironment()) {
        Environment.getExternalStorageDirectory().path + File.separator + "Android" + File.separator + "obb"
    } else {
        Environment.getDataDirectory().parentFile.toString() + File.separator + "Android" + File.separator + "obb"
    }
    createOrExistsDir(path)
    return path
}

/**
 * Return whether sdcard is enabled by environment.
 *
 * @return `true`: enabled<br></br>`false`: disabled
 */
fun isSDCardEnableByEnvironment(): Boolean {
    return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
}