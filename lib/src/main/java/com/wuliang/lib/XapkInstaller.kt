package com.wuliang.lib

import android.content.Context
import org.zeroturnaround.zip.ZipException
import java.io.File

/**
 * <pre>
 *     author : wuliang
 *     e-mail : l_wu@mingboent.com
 *     time   : 2019/09/27
 *     desc   :
 * </pre>
 */
abstract class XapkInstaller(val xapkPath: String, val xapkUnzipOutputDir: File) {

    /**
     * 返回apk
     */
    fun installXapk(context: Context) {

        try {

            install(xapkPath,context)

        } catch (e: ZipException) {
        }

    }

    internal abstract fun install(xapkPath: String,context: Context)

    abstract fun getUnzipPath():String?

}