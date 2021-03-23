/* Copyright 2020 Luzhuo. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.luzhuo.ossdemo;

import android.content.Context;

import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.luzhuo.lib_okhttp.IOKHttpManager;
import me.luzhuo.lib_okhttp.OKHttpManager;
import me.luzhuo.lib_okhttp.interceptor.TokenInterceptor;
import me.luzhuo.lib_oss.ALiOSSFileServer;
import me.luzhuo.lib_oss.IALiOSSFileServer;
import me.luzhuo.lib_oss.bean.OSSFileBean;
import me.luzhuo.lib_oss.bean.OSSSTSBean;
import me.luzhuo.lib_oss.callback.IOSSFileCallback;
import me.luzhuo.lib_oss.callback.IOSSFilesCallback;
import me.luzhuo.lib_oss.callback.IProgress;

/**
 * Description:
 *
 * @Author: Luzhuo
 * @Creation Date: 2020/8/2 11:21
 * @Copyright: Copyright 2020 Luzhuo. All rights reserved.
 **/
public class OSSUtils {
    private static OSSUtils instance;
    private IALiOSSFileServer ossFileServer;
    private IOKHttpManager okHttpManager;
    private ExecutorService threadpool = Executors.newCachedThreadPool();

    private static final String STSServer = "http://api-chongjia.jincaishuizu.com/find/app/v1/pic/getAliCloudSupposeAcc";
    private static final String EndPoint = "cj-oss.jcprod.xyz";
    private static final String BucketName = "chongjia";
    private static final String prefix = "0001/";

    public static OSSUtils getInstance(Context context) {
        if (instance == null){
            synchronized (OSSUtils.class){
                if (instance == null){
                    instance = new OSSUtils(context);
                }
            }
        }
        return instance;
    }

    private OSSUtils(Context context) {

        try {
            okHttpManager = new OKHttpManager(
                new TokenInterceptor() {
                    @Override
                    public String getToken() {
                        return "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzMzE2MyIsImlhdCI6MTYwNzc2NjMwMSwiZXhwIjoxNjA4MzcxMTAxfQ.P1-KMLC4xFhjQng9RxiujRazubv6SlxdU3KRT6OhcRrRGE7w4q2yR7VFANGkj7EieBUNXpZP_Pnf861UVPOH3w";
                    }
                });
        }catch (Exception e){
            e.printStackTrace();
        }

        ossFileServer = new ALiOSSFileServer(context.getApplicationContext(), STSServer, EndPoint, BucketName, prefix) {
            @Override
            protected OSSSTSBean getTokenFromUser(String s) {
                try {
                    String message = okHttpManager.get(STSServer);

                    Gson gson = new Gson();
                    STSBean bean = gson.fromJson(message, STSBean.class);
                    if(bean.code != 200){
                        return null;
                    }
                    STSBean.DataBean dataBean = bean.data;

                    return new OSSSTSBean(dataBean.AccessKeyId, dataBean.AccessKeySecret, dataBean.SecurityToken, dataBean.Expiration);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }

    /**
     * 使用案例
     *
     * <pre>
     * ManagerUser.getInstance(this).setToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzMzE2MyIsImlhdCI6MTU4OTcyODc4NCwiZXhwIjoxNTkwMzMzNTg0fQ.FRHKWh-siG40NqG3qrtGl3Gb_vS3HjBFRrSRqNfEtzUA9ckoCDcwwi11wiB0WTQCrMhGACqZZfg26jeItIrO4g");
     * String FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
     * FileBean fileBean = new FileBean();
     * fileBean.setPath2AliOSS(FILE_DIR + "wangwang.zip", UUID.randomUUID().toString().replace("-",""));
     * ManagetNetWork.getInstance(this).uploadFileByAliOSS(fileBean, new IProgress() {
     *     @Override
     *     public void onProgress(int i, long l, long l1) {
     *         Log.e(TAG, "" + i + " : " + l + " : " + l1);
     *     }
     * }, new FileWorkCallbackImpl() {
     *     @Override
     *     public void onSuccess(FileBean fileBean) {
     *         Log.e(TAG, "" + fileBean.getPath2AliOSS());
     *     }
     *
     *     @Override
     *     public void onError(String s) {
     *         Log.e(TAG, "" + s);
     *     }
     * });
     * </pre>
     *
     * @param fileBean
     * @param progress
     * @param callback
     */
    public void uploadFileByAliOSS(final OSSFileBean fileBean, final IProgress progress, final IOSSFileCallback callback) {
        threadpool.execute(new Runnable() {
            @Override
            public void run() {
                ossFileServer.uploadFileByAliOSS(fileBean, progress, callback);
            }
        });
    }

    public void uploadFilesByAliOSS(final List<OSSFileBean> fileBeans, final IProgress iProgress, final IOSSFilesCallback callback) {
        threadpool.execute(new Runnable() {
            @Override
            public void run() {
                ossFileServer.uploadFilesByAliOSS(fileBeans, iProgress, callback);
            }
        });
    }
}
