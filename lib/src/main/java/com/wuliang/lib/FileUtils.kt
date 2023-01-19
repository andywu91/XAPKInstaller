package com.wuliang.lib

import java.io.File

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/05/03
 *     desc  : utils about file,just copy from https://github.com/Blankj/AndroidUtilCode
 * </pre>
 */

/**
 * Create a directory if it doesn't exist, otherwise do nothing.
 *
 * @param dirPath The path of directory.
 * @return `true`: exists or creates successfully<br></br>`false`: otherwise
 */
fun createOrExistsDir(dirPath: String): Boolean {
    return createOrExistsDir(getFileByPath(dirPath))
}

/**
 * Create a directory if it doesn't exist, otherwise do nothing.
 *
 * @param file The file.
 * @return `true`: exists or creates successfully<br></br>`false`: otherwise
 */
fun createOrExistsDir(file: File?): Boolean {
    return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
}

/**
 * Return the file by path.
 *
 * @param filePath The path of file.
 * @return the file
 */
fun getFileByPath(filePath: String): File? {
    return if (isSpace(filePath)) null else File(filePath)
}

/**
 * Return the name of file.
 *
 * @param file The file.
 * @return the name of file
 */
fun getFileName(file: File?): String {
    return if (file == null) "" else getFileName(file.absolutePath)
}

/**
 * Return the name of file.
 *
 * @param filePath The path of file.
 * @return the name of file
 */
fun getFileName(filePath: String): String {
    if (isSpace(filePath)) return ""
    val lastSep = filePath.lastIndexOf(File.separator)
    return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
}

/**
 * Return the name of file without extension.
 *
 * @param file The file.
 * @return the name of file without extension
 */
fun getFileNameNoExtension(file: File?): String {
    return if (file == null) "" else getFileNameNoExtension(file.path)
}

/**
 * Return the name of file without extension.
 *
 * @param filePath The path of file.
 * @return the name of file without extension
 */
fun getFileNameNoExtension(filePath: String): String {
    if (isSpace(filePath)) return ""
    val lastPoi = filePath.lastIndexOf('.')
    val lastSep = filePath.lastIndexOf(File.separator)
    if (lastSep == -1) {
        return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
    }
    return if (lastPoi == -1 || lastSep > lastPoi) {
        filePath.substring(lastSep + 1)
    } else filePath.substring(lastSep + 1, lastPoi)
}

private fun isSpace(s: String?): Boolean {
    if (s == null) return true
    var i = 0
    val len = s.length
    while (i < len) {
        if (!Character.isWhitespace(s[i])) {
            return false
        }
        ++i
    }
    return true
}

