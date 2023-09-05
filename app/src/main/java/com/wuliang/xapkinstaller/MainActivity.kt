package com.wuliang.xapkinstaller

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import com.liulishuo.okdownload.core.listener.DownloadListener1
import com.liulishuo.okdownload.core.listener.DownloadListener2
import com.liulishuo.okdownload.core.listener.assist.Listener1Assist
import com.wuliang.lib.createXapkInstaller
import java.io.File
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

private const val PERMISSION_REQUEST_CODE = 99
private const val ACTION_MANAGE_UNKNOWN_APP_SOURCES_REQUEST_CODE = 120

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.install_tv).setOnClickListener {
            checkAndInstall()
        }
    }

    private fun checkAndInstall() {
        val writeExternalStorageAllowed = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED

        if (Build.VERSION.SDK_INT >= 26) {//8.0
            //判断应用是否有权限安装apk
            val installAllowed = packageManager.canRequestPackageInstalls()

            //有权限
            if (installAllowed && writeExternalStorageAllowed) {
                //安装apk
                install()
            } else {
                //无权限 申请权限
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.REQUEST_INSTALL_PACKAGES
                    ),
                    PERMISSION_REQUEST_CODE
                )
            }
        } else {//8.0以下
            if (!writeExternalStorageAllowed) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    PERMISSION_REQUEST_CODE
                )
            } else {
                //安装apk
                install()
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != PERMISSION_REQUEST_CODE)
            return

        grantResults.forEachIndexed { index, grantResult ->
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                if (Manifest.permission.WRITE_EXTERNAL_STORAGE == permissions[index]) {
                    Toast.makeText(this, "权限不足,请授予存储权限！", Toast.LENGTH_SHORT).show();
                } else if (Manifest.permission.REQUEST_INSTALL_PACKAGES == permissions[index]) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        //引导用户去手动开启权限
                        val uri = Uri.parse("package:" + this.packageName)
                        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri)
                        startActivityForResult(
                            intent,
                            ACTION_MANAGE_UNKNOWN_APP_SOURCES_REQUEST_CODE
                        )
                    }else{
                        Toast.makeText(this, "权限不足，请打开安装应用权限！", Toast.LENGTH_SHORT).show();
                    }
                }
                return
            }
        }

        install()
    }

    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ACTION_MANAGE_UNKNOWN_APP_SOURCES_REQUEST_CODE) {
            if (packageManager.canRequestPackageInstalls()) {
                install()
            } else {
                Toast.makeText(this, "权限不足，请打开安装应用权限！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun install() {
        downloadXapkAndInstall()
    }

    private fun downloadXapkAndInstall() {
        val outputFileDirectory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath +
                    File.separator + "xapk"
        )
        if (!outputFileDirectory.exists())
            outputFileDirectory.mkdir()

        val outputFileName = "LEGO_CITY.xapk"
        val outputFile = File(outputFileDirectory.absolutePath + File.separator + outputFileName)

        if (outputFile.exists()) {
            doInstall(outputFile.absolutePath)
        }

        //TODO this is xapk download url
        val downloadUrl = ""

        val task = DownloadTask.Builder(
            downloadUrl,
            outputFileDirectory
        )
            .setFilename(outputFileName)
            .build()

        val dialog = ProgressDialog(this)
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        dialog.isIndeterminate = false
        dialog.setCancelable(false)
        dialog.max = 100
        dialog.setTitle("正在下载中...")

        task.enqueue(object : DownloadListener1() {
            override fun taskStart(task: DownloadTask, model: Listener1Assist.Listener1Model) {
                dialog.show()
            }

            override fun progress(task: DownloadTask, currentOffset: Long, totalLength: Long) {
                dialog.progress = ((currentOffset / totalLength) * dialog.max).toInt()
            }

            override fun taskEnd(
                task: DownloadTask,
                cause: EndCause,
                realCause: Exception?,
                model: Listener1Assist.Listener1Model
            ) {
                dialog.dismiss()
                Toast.makeText(this@MainActivity, "下载完成，开始安装！", Toast.LENGTH_SHORT).show();

                doInstall(outputFile.absolutePath)
            }

            override fun retry(task: DownloadTask, cause: ResumeFailedCause) {
            }

            override fun connected(
                task: DownloadTask,
                blockCount: Int,
                currentOffset: Long,
                totalLength: Long
            ) {
            }

        })
    }

    private fun doInstall(xapkFilePath: String) {
        val xapkInstaller = createXapkInstaller(xapkFilePath)

        if (xapkInstaller == null) {
            Toast.makeText(this, "安装xapk失败！", Toast.LENGTH_SHORT).show();
        } else {
            val installExecutor = Executors.newSingleThreadExecutor()
            installExecutor.execute {
                xapkInstaller.installXapk(this@MainActivity)
            }
        }
    }

}
