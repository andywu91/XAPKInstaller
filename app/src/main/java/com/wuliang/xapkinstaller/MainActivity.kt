package com.wuliang.xapkinstaller

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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
