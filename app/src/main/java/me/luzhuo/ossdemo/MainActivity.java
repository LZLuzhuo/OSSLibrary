package me.luzhuo.ossdemo;

import androidx.appcompat.app.AppCompatActivity;
import me.luzhuo.lib_image_select.ImageSelectManager;
import me.luzhuo.lib_image_select.bean.FileBean;
import me.luzhuo.lib_image_select.callback.SelectCallBack;
import me.luzhuo.lib_oss.bean.OSSFileBean;
import me.luzhuo.lib_oss.callback.IOSSFileCallback;
import me.luzhuo.lib_oss.callback.IOSSFilesCallback;
import me.luzhuo.lib_oss.callback.IProgress;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private OSSUtils ossUtils;
    private String FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ossUtils = OSSUtils.getInstance(this);
    }

    /**
     * 上传单个文件
     */
    public void uploadFile(View view) {
        OSSFileBean fileBean = new OSSFileBean(FILE_DIR + "wangwang.zip", UUID.randomUUID().toString().replace("-",""));

        ossUtils.uploadFileByAliOSS(fileBean, new IProgress() {
            @Override
            public void onProgress(int progress, long currentSize, long totalSize) {
                Log.e(TAG, "" + progress + " : " + currentSize + " : " + totalSize);
            }
        }, new IOSSFileCallback() {
            @Override
            public void onSuccess(OSSFileBean fileBean) {
                Log.e(TAG, "" + fileBean.getPath());
            }

            @Override
            public void onError(String s) {
                Log.e(TAG, "" + s);
            }
        });
    }

    /**
     * 上传多个文件
     */
    public void uploadFiles(View view) {
        List files = new ArrayList<OSSFileBean>();
        for (int i = 0; i <= 5; i++){
            files.add(new OSSFileBean(FILE_DIR + "wangwang.zip", UUID.randomUUID().toString().replace("-", "")));
        }

        Log.e(TAG, "开始上传多个文件");
        ossUtils.uploadFilesByAliOSS(files, new IProgress() {
            @Override
            public void onProgress(int progress, long currentSize, long totalSize) {
                Log.e(TAG, "" + progress + " : " + currentSize + " : " + totalSize);
            }
        }, new IOSSFilesCallback() {
            @Override
            public void onSuccess(List<OSSFileBean> fileBeans) {
                for (OSSFileBean fileBean : fileBeans) {
                    Log.e(TAG, "" + fileBean.getPath());
                }
            }

            @Override
            public void onError(String s) {
                Log.e(TAG, "" + s);
            }
        });
    }

    public void uploadImage(View view) {
        new ImageSelectManager(this).onSetCallbackListener(new SelectCallBack() {
            @Override
            public void onSelect(List<FileBean> list) {

                OSSFileBean fileBean = new OSSFileBean(list.get(0).getPath(), UUID.randomUUID().toString().replace("-",""));

                ossUtils.uploadFileByAliOSS(fileBean, new IProgress() {
                    @Override
                    public void onProgress(int progress, long currentSize, long totalSize) {
                        Log.e(TAG, "" + progress + " : " + currentSize + " : " + totalSize);
                    }
                }, new IOSSFileCallback() {
                    @Override
                    public void onSuccess(OSSFileBean fileBean) {
                        Log.e(TAG, "" + fileBean.getPath());
                    }

                    @Override
                    public void onError(String s) {
                        Log.e(TAG, "" + s);
                    }
                });
            }

            @Override
            public void onCancel() {

            }
        }).openImage(1);
    }
}