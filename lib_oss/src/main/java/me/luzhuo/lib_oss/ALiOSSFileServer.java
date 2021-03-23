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
package me.luzhuo.lib_oss;

import android.content.Context;

import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import me.luzhuo.lib_oss.alioss.AliOSSManager;
import me.luzhuo.lib_oss.alioss.bean.HttpType;
import me.luzhuo.lib_oss.bean.OSSFileBean;
import me.luzhuo.lib_oss.bean.OSSSTSBean;
import me.luzhuo.lib_oss.callback.IFileResult;
import me.luzhuo.lib_oss.callback.IOSSFileCallback;
import me.luzhuo.lib_oss.callback.IOSSFilesCallback;
import me.luzhuo.lib_oss.callback.IProgress;


/**
 * Description:
 *
 * @Author: Luzhuo
 * @Creation Date: 2020/5/4 20:17
 * @Copyright: Copyright 2020 Luzhuo. All rights reserved.
 **/
public abstract class ALiOSSFileServer implements IALiOSSFileServer{
    // --- Please set this information ---
    private String STSServer = "http://192.168.10.124:7080/doGet";
    private String EndPoint = "oss-cn-hangzhou.aliyuncs.com";
    private String BucketName = "luzhuo-data";
    private HttpType httpType = HttpType.Http;

    /**
     * The default is to use OSSAuthCredentialsProvider to automatically send a Get request to obtain the Token,
     * otherwise use a your own method through the callback to obtain the Token
     *
     * The default true is for testing only.
     */
    private boolean isdefaultGetrequest;

    /**
     * case: qinzui/
     */
    private String Prefix = "";

    private AliOSSManager aliOSSManager;

    /**
     * The default option is for testing. GetTokenFromUser returns null.
     * @param context
     */
    public ALiOSSFileServer(Context context){
        isdefaultGetrequest = true;
        init(context);
    }

    /**
     * Example:
     * <pre>
     * private static final String FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
     * public void onclick(View view) {
     *     String STSServer = "https://192.168.10.124:7080/getAliToekn";
     *     String EndPoint = "cj-oss.save.xyz";
     *     String BucketName = "databucket";
     *     String prefix = "666/";
     *
     *     ALiOSSFileServer aLiOSSFileServer = new ALiOSSFileServer(this, STSServer, EndPoint, BucketName, prefix){
     *         @Override
     *         public OSSFederationToken getTokenFromUser(String STSServer) {
     *             try {
     *                 // Get temporary token.
     *                 OKHttpManager manager = new OKHttpManager(new TokenInterceptor() {
     *                     @Override
     *                     public String getToken() {
     *                         return "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIzMzE2MyIsImlhdCI6MTU4OTYxNDczNSwiZXhwIjoxNTkwMjE5NTM1fQ.DyuUllLfRAmv85OY2e9pjME1jQ2o44tsSu4cr4iRVwYljg6y9gml_f3h0ztEsrtBAw_y3ztk3QhjzuiIpAIUmw";
     *                     }
     *                 });
     *                 String message = manager.get(STSServer);
     *
     *                 Gson gson = new Gson();
     *                 STSBean bean = gson.fromJson(message, STSBean.class);
     *                 Log.e(TAG, "" + bean);
     *                 if(bean.code != 200){
     *                     return null;
     *                 }
     *                 STSBean.DataBean dataBean = bean.data;
     *                 Log.e(TAG, "" + dataBean);
     *
     *                 return new OSSFederationToken(dataBean.AccessKeyId, dataBean.AccessKeySecret, dataBean.SecurityToken, dataBean.Expiration);
     *             } catch (IOException e) {
     *                 e.printStackTrace();
     *             } catch (NoSuchAlgorithmException e) {
     *                 e.printStackTrace();
     *             } catch (KeyManagementException e) {
     *                 e.printStackTrace();
     *             } catch (NetErrorException e) {
     *                 e.printStackTrace();
     *             }
     *             return null;
     *         }
     *     };
     *
     *     // Single file upload
     *     FileBean fileBean = new FileBean();
     *     fileBean.setPath2AliOSS(FILE_DIR + "wangwang.zip", UUID.randomUUID().toString().replace("-",""));
     *     aLiOSSFileServer.uploadFileByAliOSS(fileBean, new IProgress() {
     *         @Override
     *         public void onProgress(int progress, long currentSize, long totalSize) {
     *             Log.e(TAG, "" + progress + " : " + currentSize + " : " + totalSize);
     *         }
     *     }, new FileWorkCallbackImpl() {
     *         @Override
     *         public void onSuccess(FileBean fileBean) {
     *             Log.e(TAG, "" + fileBean);
     *         }
     *
     *         @Override
     *         public void onError(String errorMessage) {
     *             Log.e(TAG, "" + errorMessage);
     *         }
     *     });
     *
     *     // Multi-file upload
     *     List<FileBean> files = new ArrayList<>();
     *     for (int i = 1; i < 10; i++) {
     *         FileBean fileBean2 = new FileBean();
     *         fileBean2.setPath2AliOSS(FILE_DIR + "wangwang.zip", UUID.randomUUID().toString().replace("-",""));
     *         files.add(fileBean2);
     *     }
     *     aLiOSSFileServer.uploadFilesByAliOSS(files, new IProgress() {
     *         @Override
     *         public void onProgress(int progress, long currentSize, long totalSize) {
     *             Log.e(TAG, "" + progress);
     *         }
     *     }, new FileWorkCallbackImpl(){
     *         @Override
     *         public void onSuccess(List<FileBean> networkUrls) {
     *             for (FileBean fileBean : networkUrls) {
     *                 Log.e(TAG, "" + fileBean.getPath2AliOSS());
     *             }
     *         }
     *
     *         @Override
     *         public void onError(String errorMessage) {
     *             Log.e(TAG, "" + errorMessage);
     *         }
     *     });
     * }
     * </pre>
     *
     * @param context
     * @param STSServer
     * @param EndPoint
     * @param BucketName
     * @param prefix
     */
    public ALiOSSFileServer(Context context, String STSServer, String EndPoint, String BucketName, String prefix){
        isdefaultGetrequest = false;
        this.STSServer = STSServer;
        this.EndPoint = EndPoint;
        this.BucketName = BucketName;
        this.Prefix = prefix;
        init(context);
    }

    private void init(Context context){
        aliOSSManager = new AliOSSManager(context, STSServer, EndPoint, BucketName, isdefaultGetrequest){
            @Override
            public OSSFederationToken getToken(String STSServer) {
                OSSSTSBean sts = getTokenFromUser(STSServer);
                if(sts == null) return null;

                return new OSSFederationToken(sts.AccessKeyId, sts.AccessKeySecret, sts.SecurityToken, sts.Expiration);
            }
        };
    }

    public void uploadFileByAliOSS(final OSSFileBean fileBean, IProgress progress, final IOSSFileCallback callback) {
        aliOSSManager.asyncPutFile(fileBean.getFilePath(), Prefix, fileBean.getFileNameKey(), progress, new IFileResult() {
            @Override
            public void onSucess(String filePath, String fileName, Map<String, String> bundleData) {
                // Run on child thread
                String fileUrl = httpType.value() + filePath + fileName;
                fileBean.setNetworkPath(fileUrl);
                if(callback != null) callback.onSuccess(fileBean);
            }

            @Override
            public void onError(String errMessage) {
                // Run on child thread
                if(callback != null) callback.onError(errMessage);
            }
        });
    }

    public void uploadFilesByAliOSS(List<OSSFileBean> fileBeans, final IProgress iProgress, final IOSSFilesCallback callback) {
        final AtomicInteger currentSize = new AtomicInteger(0);
        final int totalSize = fileBeans.size();

        final List<OSSFileBean> netWorkUrl = new ArrayList<>();
        final boolean[] isNormal = {true};

        for (final OSSFileBean fileBean : fileBeans) {
            OSSAsyncTask task = aliOSSManager.asyncPutFile(fileBean.getFilePath(), Prefix, fileBean.getFileNameKey(), null, new IFileResult() {
                @Override
                public void onSucess(String filePath, String fileName, Map<String, String> bundleData) {
                    // Run on child thread
                    String fileUrl = httpType.value() + filePath + fileName;
                    fileBean.setNetworkPath(fileUrl);
                    netWorkUrl.add(fileBean);

                    int progress = (int) (100 * currentSize.addAndGet(1) / totalSize);
                    if(iProgress != null) iProgress.onProgress(progress, currentSize.get(), totalSize);
                }

                @Override
                public void onError(String errMessage) {
                    // Run on child thread
                    isNormal[0] = false;
                    if(callback != null) callback.onError(errMessage);
                }
            });

            // if an exception occurs, the task will be null
            if(task != null) task.waitUntilFinished();
            // If an exception occurs, it will not continue execution.
            if(!isNormal[0]) {
                break;
            }
        }

        if(isNormal[0] && callback != null) callback.onSuccess(netWorkUrl);
    }

    /**
     * Please override this method, if the acquisition fails, please return null
     *
     * Please execute in this thread, this thread is not on the Main thread,
     * please do not execute in a new thread
     *
     * example:
     * <pre>
     * public OSSFederationToken getTokenFromUser(String STSServer) {
     *     try {
     *         OkHttpClient client = new OkHttpClient();
     *         Request request = new Request.Builder()
     *                 .post(new FormBody.Builder().build())
     *                 .url(STSServer)
     *                 .addHeader("token", "f786c148bcb98ea4d81913f0871fdf6c")
     *                 .build();
     *         Response response = client.newCall(request).execute();
     *         String json = response.body().string();
     *         Log.e(TAG, "" + json);
     *
     *         Gson gson = new Gson();
     *         STSBean bean = gson.fromJson(json, STSBean.class);
     *         if(bean.code != 200){
     *             return null;
     *         }
     *         STSBean.DataBean dataBean = bean.data;
     *
     *         return new OSSFederationToken(dataBean.AccessKeyId, dataBean.AccessKeySecret, dataBean.SecurityToken, dataBean.Expiration);
     *     } catch (IOException e) {
     *         e.printStackTrace();
     *         return null;
     *     }
     * }
     * </pre>
     *
     * @return OSSFederationToken or null
     */
    protected abstract OSSSTSBean getTokenFromUser(String STSServer);
}
