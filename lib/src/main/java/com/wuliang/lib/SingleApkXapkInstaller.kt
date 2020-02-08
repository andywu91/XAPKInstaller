package com.wuliang.lib

import android.content.Context
import android.util.Log
import java.io.File

/**
 * <pre>
 *     author : wuliang
 *     e-mail : l_wu@mingboent.com
 *     time   : 2019/09/27
 *     desc   :
 * </pre>
 */
class SingleApkXapkInstaller(xapkPath: String, xapkUnzipOutputDir: File)
    : XapkInstaller(xapkPath, xapkUnzipOutputDir) {

    override fun install(xapkPath: String, context: Context) {

        val files:Array<File>? = xapkUnzipOutputDir.listFiles()

        files?.forEach { file ->
            if ((file.isFile && file.name.endsWith(".apk"))) {

                val filePath = file.absolutePath

                if (!filePath.isNullOrEmpty()) {

                    Log.d(INSTALL_OPEN_APK_TAG, "single apk xapk installer,openDownloadApk")

                    installApp(filePath,context)
                }

            }
        }

    }

    override fun getUnzipPath(): String? {
        return xapkUnzipOutputDir.listFiles().find { file ->
            file.isFile && file.name.endsWith(".apk")
        }?.absolutePath
    }

}