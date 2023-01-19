package com.wuliang.lib

import android.content.Context
import org.zeroturnaround.zip.ZipException
import java.io.File

/**
 * <pre>
 *     author : wuliang
 *     time   : 2019/09/27
 * </pre>
 */
abstract class XapkInstaller(val xapkPath: String, val xapkUnzipOutputDir: File) {

    fun installXapk(context: Context) {
        install(xapkPath,context)
    }

    internal abstract fun install(xapkPath: String,context: Context)

    abstract fun getUnzipPath():String?

}