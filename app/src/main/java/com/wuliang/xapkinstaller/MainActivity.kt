package com.wuliang.xapkinstaller

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.wuliang.lib.createXapkInstaller
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var installExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.install_tv).setOnClickListener {
            install()
        }

        if (Build.VERSION.SDK_INT >= 26) {//8.0
            //来判断应用是否有权限安装apk
            val installAllowed = packageManager.canRequestPackageInstalls()
            //有权限
            if (installAllowed) {
                //安装apk

            } else {
                //无权限 申请权限
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.REQUEST_INSTALL_PACKAGES, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    99)
            }
        } else {//8.0以下
            //安装apk

        }

    }

    private fun install() {

        installExecutor = Executors.newSingleThreadExecutor()
        installExecutor.execute {
            createXapkInstaller(getXapkFilePath())?.installXapk(this@MainActivity)
        }

    }

    private fun getXapkFilePath(): String {
        return ""
    }

}
