package com.wuliang.lib

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import java.io.File

/**
 * <pre>
 *     author : wuliang
 *     time   : 2019/09/27
 * </pre>
 */
class MultiApkXapkInstaller(xapkPath: String, xapkUnzipOutputDir: File)
    : XapkInstaller(xapkPath, xapkUnzipOutputDir) {

    override fun getUnzipPath(): String? {
        return xapkUnzipOutputDir.absolutePath
    }

    override fun install(xapkPath: String, context: Context) {
        val files = xapkUnzipOutputDir.listFiles()

        val apkFilePaths = files.filter { file ->
            file.isFile && file.name.endsWith(".apk")
        }.map { it.absolutePath }

        enterInstallActivity(xapkPath, ArrayList(apkFilePaths), context)
    }

    private fun enterInstallActivity(xapkPath: String, apkFilePaths: ArrayList<String>, context: Context) {
        Log.d(INSTALL_OPEN_APK_TAG, "multi apk xapk installer,enter InstallActivity,xapkPath:$xapkPath," +
                "apkFilePaths:$apkFilePaths")

        val intent = Intent(context, InstallActivity::class.java)
        intent.putStringArrayListExtra(InstallActivity.KEY_APK_PATHS, apkFilePaths)
        if (context !is Activity) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

}