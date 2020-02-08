package com.wuliang.lib;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.wuliang.lib.FileUtilsKt.getFileName;

/**
 * @author Jason Ran
 * @date 2019/9/26
 */
public class InstallActivity extends AppCompatActivity {

    private static final String TAG = "InstallActivity";

    private static final String PACKAGE_INSTALLED_ACTION =
            "com.wuliang.common.SESSION_API_PACKAGE_INSTALLED";

    public static final String KEY_XAPK_PATH = "xapk_path";
    public static final String KEY_APK_PATHS = "apk_path";

    private String xapkPath;
    private List<String> apkPaths;
    private ExecutorService installXapkExectuor;

    private PackageInstaller.Session mSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_install);

        initData();

        installXapk();
    }

    public void initData() {
        xapkPath = getIntent().getStringExtra(KEY_XAPK_PATH);
        apkPaths = getIntent().getStringArrayListExtra(KEY_APK_PATHS);
    }

    private void installXapk() {
        if (Build.VERSION.SDK_INT < 21) {
            Toast.makeText(this, "暂时不支持安装,请更新到Android 5.0及以上版本", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (apkPaths == null || apkPaths.isEmpty()) {
            Toast.makeText(this, "解析apk出错或已取消", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (RomUtils.isMeizu() || RomUtils.isVivo()) {
            Toast.makeText(this,"魅族或VIVO系统用户如遇安装被中止或者安装失败的情况，请尝试联系手机平台客服，或者更换系统内置包安装器再重试",
                    Toast.LENGTH_SHORT).show();
            finish();
        }

        installXapkExectuor = Executors.newSingleThreadExecutor();
        installXapkExectuor.execute(() -> {

            try {

                mSession = initSession();

                for (String apkPath : apkPaths) {
                    addApkToInstallSession(apkPath, mSession);
                }

                commitSession(mSession);

            } catch (IOException e) {
                e.printStackTrace();
                abandonSession();
            }

        });


    }

    @TargetApi(21)
    private PackageInstaller.Session initSession() throws IOException {
        PackageInstaller.Session session = null;
        PackageInstaller packageInstaller = getPackageManager().getPackageInstaller();
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                PackageInstaller.SessionParams.MODE_FULL_INSTALL);
        int sessionId = 0;
        sessionId = packageInstaller.createSession(params);
        session = packageInstaller.openSession(sessionId);

        return session;
    }

    @TargetApi(21)
    private void addApkToInstallSession(String filePath, PackageInstaller.Session session)
            throws IOException {
        // It's recommended to pass the file size to openWrite(). Otherwise installation may fail
        // if the disk is almost full.
        try (OutputStream packageInSession = session.openWrite(getFileName(filePath), 0, new File(filePath).length());
             InputStream is = new BufferedInputStream(new FileInputStream(filePath))) {
            byte[] buffer = new byte[16384];
            int n;
            while ((n = is.read(buffer)) >= 0) {
                packageInSession.write(buffer, 0, n);
            }
        }
    }

    @TargetApi(21)
    private void commitSession(PackageInstaller.Session session) {

        // Create an install status receiver.

        Intent intent = new Intent(this, InstallActivity.class);
        intent.setAction(PACKAGE_INSTALLED_ACTION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentSender statusReceiver = pendingIntent.getIntentSender();

        // Commit the session (this will start the installation workflow).
        session.commit(statusReceiver);

    }

    @TargetApi(21)
    private void abandonSession() {
        if (mSession != null) {
            mSession.abandon();
            mSession.close();
        }
    }

    @TargetApi(21)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (PACKAGE_INSTALLED_ACTION.equals(intent.getAction())) {
            int status = -100;
            String message = "";
            if (extras != null) {
                status = extras.getInt(PackageInstaller.EXTRA_STATUS);
                message = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE);
            }
            switch (status) {
                case PackageInstaller.STATUS_PENDING_USER_ACTION:
                    // This test app isn't privileged, so the user has to confirm the install.
                    Intent confirmIntent = (Intent) extras.get(Intent.EXTRA_INTENT);
                    startActivity(confirmIntent);
                    break;
                case PackageInstaller.STATUS_SUCCESS:
                    Toast.makeText(this, "安装成功!", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case PackageInstaller.STATUS_FAILURE:
                case PackageInstaller.STATUS_FAILURE_ABORTED:
                case PackageInstaller.STATUS_FAILURE_BLOCKED:
                case PackageInstaller.STATUS_FAILURE_CONFLICT:
                case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
                case PackageInstaller.STATUS_FAILURE_INVALID:
                case PackageInstaller.STATUS_FAILURE_STORAGE:
                    Toast.makeText(this, "安装失败,请重试",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    Log.d(TAG, "Install failed! " + status + ", " + message);
                    break;
                default:
                    Toast.makeText(this, "安装失败,解压文件可能已丢失或损坏，请重试",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    Log.d(TAG, "Unrecognized status received from installer: " + status);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (installXapkExectuor != null && !installXapkExectuor.isShutdown()) {
            installXapkExectuor.shutdown();
        }

        abandonSession();

    }
}
