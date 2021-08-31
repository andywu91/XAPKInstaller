package com.wuliang.lib

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/08/02
 *     desc  : App相关工具类
 * </pre>
 */

/**
 * Install the app.
 *
 * Target APIs greater than 25 must hold
 * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
 *
 * @param filePath The path of file.
 */
fun installApp(filePath: String,context: Context) {
    installApp(getFileByPath(filePath),context)
}

/**
 * Install the app.
 *
 * Target APIs greater than 25 must hold
 * `<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />`
 *
 * @param file The file.
 */
fun installApp(file: File?,context: Context) {
    try {

        if(file != null && file.exists()){
            context.startActivity(getInstallAppIntent(file, true,context))
        }

    } catch (e: SecurityException) {
        e.printStackTrace()
    }

}

private fun getInstallAppIntent(file: File, isNewTask: Boolean,context: Context): Intent {
    val intent = Intent(Intent.ACTION_VIEW)
    val data: Uri
    val type = "application/vnd.android.package-archive"
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        data = Uri.fromFile(file)
    } else {
        val authority = context.packageName + ".provider"
        data = FileProvider.getUriForFile(context, authority, file)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
    context.grantUriPermission(
        context.packageName,
        data,
        Intent.FLAG_GRANT_READ_URI_PERMISSION
    )
    intent.setDataAndType(data, type)
    return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
}