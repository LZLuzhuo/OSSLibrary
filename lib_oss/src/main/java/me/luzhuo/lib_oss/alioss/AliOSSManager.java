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
package me.luzhuo.lib_oss.alioss;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSAuthCredentialsProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.CompleteMultipartUploadResult;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.MultipartUploadRequest;
import com.alibaba.sdk.android.oss.model.OSSObjectSummary;
import com.alibaba.sdk.android.oss.model.OSSRequest;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.alibaba.sdk.android.oss.model.ResumableUploadRequest;
import com.alibaba.sdk.android.oss.model.ResumableUploadResult;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.luzhuo.lib_core.data.file.FileManager;
import me.luzhuo.lib_oss.alioss.callback.IAliOSSResult;
import me.luzhuo.lib_oss.callback.IFileResult;
import me.luzhuo.lib_oss.callback.IProgress;


/**
 * Description: Aliyun OSS
 *
 * @Author: Luzhuo
 * @Creation Date: 2020/4/30 16:48
 * @Copyright: Copyright 2020 Luzhuo. All rights reserved.
 **/
public class AliOSSManager implements IAliOSSAsyncTask, IAliOSSSyncTask{
    /**
     * 1. Log in to the Aliyun, activate the OSS service, and create The Bucket.
     * 2. Open STS service, configure it on your own server.
     * 3. The mobile terminal requests a token of default 15-minute, and uses this token to upload and download files to OSS.
     * 4. Please use Get request.
     *
     * // return success
     * {
     *     "StatusCode":200,
     *     "AccessKeyId":"STS.3p***dgagdasdg",
     *     "AccessKeySecret":"rpnwO9***tGdrddgsR2YrTtI",
     *    "SecurityToken":"CAES+wMIARKAAZhjH0EUOIhJMQBMjRywXq7MQ/cjLYg80Aho1ek0Jm63XMhr9Oc5s˙∂˙∂3qaPer8p1YaX1NTDiCFZWFkvlHf1pQhuxfKBc+mRR9KAbHUefqH+rdjZqjTF7p2m1wJXP8S6k+G2MpHrUe6TYBkJ43GhhTVFMuM3BZajY3VjZWOXBIODRIR1FKZjIiEjMzMzE0MjY0NzM5MTE4NjkxMSoLY2xpZGSSDgSDGAGESGTETqOio6c2RrLWRlbW8vKgoUYWNzOm9zczoqOio6c2RrLWRlbW9KEDExNDg5MzAxMDcyNDY4MThSBTI2ODQyWg9Bc3N1bWVkUm9sZVVzZXJgAGoSMzMzMTQyNjQ3MzkxMTg2OTExcglzZGstZGVtbzI=",
     *    "Expiration":"2017-12-12T07:49:09Z",
     * }
     * // return err
     * {
     *     "StatusCode":500,
     *     "ErrorCode":"InvalidAccessKeyId.NotFound",
     *     "ErrorMessage":"Specified access key is not found."
     * }
     * This STSServer is your own server with STS.
     */
    private String STSServer = "http://192.168.2.101:7080/doGet";
    /**
     * Go to Aliyun to create a Bucket.
     */
    private String BucketName = "luzhuo-data";
    /**
     * OSS endpoint, check out:http://help.aliyun.com/document_detail/oss/user_guide/endpoint_region.html
     * Hangzhou OSS endpoint.
     *
     * If you use a registered domain name, this domain name is also used here.
     *
     * You can access resources directly through http://bucketName[1].oss-cn-hangzhou.aliyuncs.com/resourceName[2].mp4
     */
    private String Endpoint = "oss-cn-hangzhou.aliyuncs.com";
    /**
     * callback url (Optional)
     */
    private String OSSCallBackUrl = null;
    /**
     * The default is to use OSSAuthCredentialsProvider to automatically send a Get request to obtain the Token,
     * otherwise use a your own method through the callback to obtain the Token
     *
     * The default true is for testing only.
     */
    private boolean isdefaultGetrequest = true;

    private Context context;
    private OSS oss;
    private FileManager fileManager;

    @Deprecated
    private AliOSSManager(Context context){
        this.context = context;

        initOSS();
    }

    public AliOSSManager(Context context, String STSServer, String endPoint, String bucketName, boolean isdefaultGetrequest){
        this(context, STSServer, endPoint, bucketName, null, isdefaultGetrequest);
    }

    /**
     * Create AliOSSManager
     * @param context Context
     * @param STSServer Get the Token by your own server address
     * @param endPoint OSS endpoint, check out:http://help.aliyun.com/document_detail/oss/user_guide/endpoint_region.html.
     *                 You can use your own registered domain name
     * @param bucketName BucketName, The name of the bucket has no `oss://` and prefix
     * @param ossCallBackUrl Optional, Set the server's callback url.
     */
    public AliOSSManager(Context context, String STSServer, String endPoint, String bucketName, String ossCallBackUrl, boolean isdefaultGetrequest){
        if(context == null || STSServer == null || endPoint == null || bucketName == null) return;
        if(STSServer.isEmpty() || endPoint.isEmpty() || bucketName.isEmpty()) return;

        this.context = context;
        this.STSServer = STSServer;
        this.Endpoint = endPoint;
        this.BucketName = bucketName;
        this.OSSCallBackUrl = ossCallBackUrl;
        this.isdefaultGetrequest = isdefaultGetrequest;

        fileManager = new FileManager();
        initOSS();
    }

    private void initOSS() {
        // -- Token --
        /**
         * OSSAuthCredentialsProvider, the token will automatically refresh after it expires.
         *
         * The actual application generally uses the OSSFederationCredentialProvider.
         */
        OSSCredentialProvider credentialProvider;
        if(isdefaultGetrequest){
            credentialProvider = new OSSAuthCredentialsProvider(STSServer);
        }else{
            credentialProvider = new OSSFederationCredentialProvider() {
                @Override
                public OSSFederationToken getFederationToken() {
                    return getToken(STSServer);
                }
            };
        }

        // -- Config --
        ClientConfiguration config = new ClientConfiguration();
        // Connection timeout, default 15 seconds
        config.setConnectionTimeout(15 * 1000);
        // Socket timeout time, default 15 seconds
        config.setSocketTimeout(15 * 1000);
        // Maximum number of concurrent requests, default 5
        config.setMaxConcurrentRequest(5);
        // Maximum number of retries after failure, default 2 times
        config.setMaxErrorRetry(2);
        oss = new OSSClient(context.getApplicationContext(), Endpoint, credentialProvider, config);
    }

    /**
     * Please override this method, if the acquisition fails, please return null
     * @return OSSFederationToken or null
     */
    public OSSFederationToken getToken(String url){
        return null;
    }

    /**
     * Upload files asynchronously from local to oss.
     *
     * example:
     * <pre>
     * AliOSSManager aliOSSManager = new AliOSSManager(this, STSServer, endPoint, bucketName);
     * aliOSSManager.asyncPutFile(FILE_PATH, "qianzui/", "666.jpg", new IProgress() {
     *     public void onProgress(int progress, long currentSize, long totalSize) {
     *         // Run on child thread
     *         Log.e(TAG, "" + progress);
     *     }
     * }, new IFileResult() {
     *     public void onSucess(String filePath, String fileName, Map<String, String> bundleData) {
     *         // Run on child thread
     *         String fileUrl = "http://" + filePath + fileName;
     *         Log.e(TAG, "" + fileUrl);
     *     }
     *
     *     public void onError(String errMessage) {
     *         // Run on child thread
     *         Log.e(TAG, "" + errMessage);
     *     }
     * });
     * </pre>
     *
     * @param filePath local file path
     * @param PutDirectory case: qianzui/
     * @param PutFileName case: xxx.jpg
     * @param iProgress progress, Can be null
     * @param iFileResult result callback, not be null.
     * @return OSSAysncTask
     * <pre>
     * task.cancel(); // cancel task, stop execute.
     * task.waitUntilFinished(); // current thread waiting, Until the task is finished. Can be used with cancel()
     * GetObjectResult result = task.getResult(); // current thread blocking, until the result to return
     * </pre>
     */
    public OSSAsyncTask asyncPutFile(String filePath, String PutDirectory /* "qinauzi/" */, String PutFileName /* xxx.jpg */, final IProgress iProgress, final IFileResult iFileResult){
        File file = new File(filePath);
        if (!file.exists() || PutDirectory == null || PutFileName == null || PutFileName.isEmpty() || iFileResult == null) {
            if(iFileResult != null) iFileResult.onError("IllegalArgumentException in AliOSSManager");
            return null;
        }

        String putOSSFileName = PutDirectory + PutFileName; // Automatically create PutDirectory directory
        PutObjectRequest put = new PutObjectRequest(BucketName, putOSSFileName, filePath);
        put.setCRC64(OSSRequest.CRC64Config.YES); // If you enable CRC64 check will reduce the transmission speed

        // Set the server's callback url
        if(OSSCallBackUrl != null && !OSSCallBackUrl.isEmpty()){
            Map<String, String> map = new HashMap<String, String>();
            map.put(IAliOSSResult.CallbackUrl, OSSCallBackUrl);
            map.put(IAliOSSResult.CallbackBody, "filename=" + PutFileName);
            put.setCallbackParam(map);
        }

        // asyc progress
        if(iProgress != null) {
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                    int progress = (int) (100 * currentSize / totalSize);
                    iProgress.onProgress(progress, currentSize, totalSize);
                }
            });
        }

        // start push
        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                if(iFileResult == null) return;

                Map<String, String> map = new HashMap<>();
                map.put(IAliOSSResult.ETag, result.getETag()); // Tag: 420C0592576D4CC69461EDDF43096392
                map.put(IAliOSSResult.RequestId, result.getRequestId()); // RequestId: 5EAEB19A227FE637399136B9
                map.put(IAliOSSResult.FileName, request.getObjectKey()); // ObjectKey: qinauzi/image.jpg
                map.put(IAliOSSResult.BucketName, request.getBucketName()); // BucketName: luzhuo-data
                map.put(IAliOSSResult.LocalFilePath, request.getUploadFilePath()); // LocalFilePath: /storage/emulated/0/image.jpg

                /**
                 * oss-cn-hangzhou.aliyuncs.com
                 *
                 * If you use ali Endpoint, you need to add the BucketName,
                 * else You don't need to add the BucketName.
                 */
                String filePathUrl;
                if(Endpoint.startsWith("oss-")) filePathUrl = BucketName + "." + Endpoint + "/";
                else filePathUrl = Endpoint + "/";

                iFileResult.onSucess(filePathUrl, request.getObjectKey(), map);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if(iFileResult == null) return;

                if (clientExcepion != null){
                    iFileResult.onError(clientExcepion.toString());
                }
                if(serviceException != null){
                    iFileResult.onError(serviceException.toString());
                }
            }
        });
        return task;
    }

    /**
     * Download files asynchronously from OSS to local.
     *
     * @param localDirectory local directory.
     *                       case: /storage/emulated/0/
     * @param objectFileName File name with prefix.
     *                       case: qinauzi/xxx.jpg
     * @param iProgress progress, can be null.
     * @param iFileResult callback, can't be null.
     * @return OSSAsyncTask
     * <pre>
     * task.cancel(); // cancel task, stop execute.
     * task.waitUntilFinished(); // current thread waiting, Until the task is finished. Can be used with cancel()
     * GetObjectResult result = task.getResult(); // current thread blocking, until the result to return
     * </pre>
     */
    public OSSAsyncTask asyncGetFile(final String localDirectory /* /storage/emulated/0/ */, String objectFileName /* qinauzi/xxx.jpg */, final IProgress iProgress, final IFileResult iFileResult){
        if (objectFileName == null || objectFileName.isEmpty() || iFileResult == null) {
            if(iFileResult != null) iFileResult.onError("IllegalArgumentException in AliOSSManager");
            return null;
        }

        GetObjectRequest get = new GetObjectRequest(BucketName, objectFileName);
        get.setCRC64(OSSRequest.CRC64Config.YES);

        if(iProgress != null) {
            get.setProgressListener(new OSSProgressCallback<GetObjectRequest>() {
                @Override
                public void onProgress(GetObjectRequest request, long currentSize, long totalSize) {
                    int progress = (int) (100 * currentSize / totalSize);
                    iProgress.onProgress(progress, currentSize, totalSize);
                }
            });
        }

        OSSAsyncTask task = oss.asyncGetObject(get, new OSSCompletedCallback<GetObjectRequest, GetObjectResult>() {
            @Override
            public void onSuccess(GetObjectRequest request, GetObjectResult result) {
                if(iFileResult == null) return;

                try {
                    InputStream inputStream = result.getObjectContent();
                    String localPath = localDirectory + request.getObjectKey();
                    // save file
                    fileManager.Stream2File(inputStream, localPath);
                    if(inputStream != null) inputStream.close();

                    Map<String, String> map = new HashMap<>();
                    map.put(IAliOSSResult.RequestId, result.getRequestId()); // requestId: 5EAF05E0C3F72236306D77AC
                    map.put(IAliOSSResult.FileName, request.getObjectKey()); // objectKey: 666.jpg
                    map.put(IAliOSSResult.BucketName, request.getBucketName()); // bucketName: luzhuo-data
                    iFileResult.onSucess(localDirectory, request.getObjectKey(), map);

                }catch (IOException e) {
                    iFileResult.onError("Serialization to local failed");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(GetObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if(iFileResult == null) return;

                if (clientExcepion != null){
                    iFileResult.onError(clientExcepion.toString());
                }
                if(serviceException != null){
                    iFileResult.onError(serviceException.toString());
                }
            }
        });
        return task;
    }

    /**
     * get file list.
     *
     * @param prefix prefix.
     *               case: qianzui/
     * @param iFileResult callback
     * @return OSSAsyncTask
     * <pre>
     * task.cancel(); // cancel task, stop execute.
     * task.waitUntilFinished(); // current thread waiting, Until the task is finished. Can be used with cancel()
     * GetObjectResult result = task.getResult(); // current thread blocking, until the result to return
     * </pre>
     */
    public OSSAsyncTask asyncGetList(String prefix /* qianzui/ */, final IAliOSSResult iFileResult){
        ListObjectsRequest listObjects = new ListObjectsRequest(BucketName);
        // Sets the prefix
        if(prefix != null && !prefix.isEmpty()) {
            listObjects.setPrefix(prefix);
            listObjects.setDelimiter("/");
        }

        OSSAsyncTask task = oss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                if(iFileResult == null) return;

                List<Map<String, String>> fileInfoList = new ArrayList<>();
                for (int i = 1; i < result.getObjectSummaries().size(); i++) {
                    OSSObjectSummary object = result.getObjectSummaries().get(i);

                    Map<String, String> map = new HashMap<>();
                    map.put(IAliOSSResult.BucketName, object.getBucketName());
                    map.put(IAliOSSResult.ETag, object.getETag());
                    map.put(IAliOSSResult.FileName, object.getKey());
                    map.put(IAliOSSResult.FilePath, object.getBucketName() + "." + Endpoint + "/");
                    map.put(IAliOSSResult.LastModified, object.getLastModified().getTime() + "");

                    fileInfoList.add(map);
                }

                String filePathUrl;
                if(Endpoint.startsWith("oss-")) filePathUrl = BucketName + "." + Endpoint + "/";
                else filePathUrl = Endpoint + "/";

                iFileResult.onFileList(filePathUrl, fileInfoList);
            }

            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
                if(iFileResult == null) return;

                if (clientExcepion != null){
                    iFileResult.onError(clientExcepion.toString());
                }
                if(serviceException != null){
                    iFileResult.onError(serviceException.toString());
                }
            }
        });
        return task;
    }

    /**
     * async multipart put file.
     *
     * @param filePath local file path
     * @param PutDirectory oss prefix
     * @param PutFileName oss filename
     * @param iProgress progress
     * @param iFileResult callback
     * @return OSSAsyncTask
     * <pre>
     * task.cancel(); // cancel task, stop execute.
     * task.waitUntilFinished(); // current thread waiting, Until the task is finished. Can be used with cancel()
     * GetObjectResult result = task.getResult(); // current thread blocking, until the result to return
     * </pre>
     */
    @Deprecated
    private OSSAsyncTask asyncMultipartPutFile(String filePath, String PutDirectory /* "qinauzi/" */, String PutFileName /* xxx.jpg */, final IProgress iProgress, final IFileResult iFileResult){
        File file = new File(filePath);
        if (!file.exists() || PutDirectory == null || PutFileName == null || PutFileName.isEmpty() || iFileResult == null) {
            if(iFileResult != null) iFileResult.onError("IllegalArgumentException in AliOSSManager");
            return null;
        }

        MultipartUploadRequest request = new MultipartUploadRequest(BucketName, PutDirectory + PutFileName, filePath);
        request.setCRC64(OSSRequest.CRC64Config.YES);

        if(iProgress != null) {
            request.setProgressCallback(new OSSProgressCallback<MultipartUploadRequest>() {
                @Override
                public void onProgress(MultipartUploadRequest request, long currentSize, long totalSize) {
                    int progress = (int) (100 * currentSize / totalSize);
                    iProgress.onProgress(progress, currentSize, totalSize);
                }
            });
        }

        OSSAsyncTask task = oss.asyncMultipartUpload(request, new OSSCompletedCallback<MultipartUploadRequest, CompleteMultipartUploadResult>() {
            @Override
            public void onSuccess(MultipartUploadRequest request, CompleteMultipartUploadResult result) {
                if(iFileResult == null) return;

                Map<String, String> map = new HashMap<>();
                map.put(IAliOSSResult.BucketName, result.getBucketName());
                map.put(IAliOSSResult.ETag, result.getETag());
                map.put(IAliOSSResult.FileName, result.getObjectKey());
                map.put(IAliOSSResult.RequestId, result.getRequestId());
                map.put(IAliOSSResult.LocalFilePath, request.getUploadFilePath());

                String filePathUrl;
                if(Endpoint.startsWith("oss-")) filePathUrl = BucketName + "." + Endpoint + "/";
                else filePathUrl = Endpoint + "/";

                iFileResult.onSucess(filePathUrl, result.getObjectKey(), map);
            }

            @Override
            public void onFailure(MultipartUploadRequest request, ClientException clientException, ServiceException serviceException) {
                if(iFileResult == null) return;

                if (clientException != null){
                    iFileResult.onError(clientException.toString());
                }
                if(serviceException != null){
                    iFileResult.onError(serviceException.toString());
                }
            }
        });
        return task;
    }

    /**
     * async resumable put file.
     * He is based on asyncMultipartPutFile.
     *
     * It is only when uploading large files to using asyncResumablePutFile, because it will take several seconds to initialize.
     *
     * @param filePath local file path
     * @param PutDirectory oss prefix
     * @param PutFileName oss file name
     * @param iProgress progress
     * @param iFileResult callback
     * @return OSSAsyncTask
     * <pre>
     * task.cancel(); // cancel task, stop execute.
     * task.waitUntilFinished(); // current thread waiting, Until the task is finished. Can be used with cancel()
     * GetObjectResult result = task.getResult(); // current thread blocking, until the result to return
     * </pre>
     */
    public OSSAsyncTask asyncResumablePutFile(String filePath, String PutDirectory /* "qinauzi/" */, String PutFileName /* xxx.jpg */, final IProgress iProgress, final IFileResult iFileResult){

        // Record recording info on the internal disk of the phone
        final String recordFilePath = context.getCacheDir().getAbsolutePath() + PutDirectory + PutFileName + "_record";
        File recordFile = new File(recordFilePath);
        if(!recordFile.exists()){
            recordFile.mkdirs();
        }

        ResumableUploadRequest request = new ResumableUploadRequest(BucketName, PutDirectory + PutFileName, filePath, recordFilePath);
        request.setCRC64(OSSRequest.CRC64Config.YES);

        if(iProgress != null) {
            request.setProgressCallback(new OSSProgressCallback<MultipartUploadRequest>() {
                @Override
                public void onProgress(MultipartUploadRequest request, long currentSize, long totalSize) {
                    int progress = (int) (100 * currentSize / totalSize);
                    iProgress.onProgress(progress, currentSize, totalSize);
                }
            });
        }

        OSSAsyncTask task = oss.asyncResumableUpload(request, new OSSCompletedCallback<ResumableUploadRequest, ResumableUploadResult>() {
            @Override
            public void onSuccess(ResumableUploadRequest request, ResumableUploadResult result) {
                if(iFileResult == null) return;

                Map<String, String> map = new HashMap<>();
                map.put(IAliOSSResult.BucketName, result.getBucketName());
                map.put(IAliOSSResult.ETag, result.getETag());
                map.put(IAliOSSResult.NetFileUrl, result.getLocation());
                map.put(IAliOSSResult.FileName, result.getObjectKey());
                map.put(IAliOSSResult.RequestId, result.getRequestId());
                map.put(IAliOSSResult.LocalFilePath, request.getUploadFilePath());
                map.put(IAliOSSResult.RecordFile, request.getRecordDirectory());

                String filePathUrl;
                if(Endpoint.startsWith("oss-")) filePathUrl = BucketName + "." + Endpoint + "/";
                else filePathUrl = Endpoint + "/";

                iFileResult.onSucess(filePathUrl, result.getObjectKey(), map);
            }

            @Override
            public void onFailure(ResumableUploadRequest request, ClientException clientException, ServiceException serviceException) {
                if(iFileResult == null) return;

                if (clientException != null){
                    iFileResult.onError(clientException.toString());
                }
                if(serviceException != null){
                    iFileResult.onError(serviceException.toString());
                }
            }
        });
        return task;
    }

    /**
     * sync put file
     *
     * @param filePath local file path
     * @param PutDirectory oss prefix
     * @param PutFileName oss file name
     * @param iProgress progress
     * @return file network url by oss
     * @throws ClientException app -> your own server
     * @throws ServiceException your own server -> oss server
     */
    public String syncPutFile(String filePath, String PutDirectory /* "qinauzi/" */, String PutFileName /* xxx.jpg */, final IProgress iProgress) throws ClientException, ServiceException {
        File file = new File(filePath);
        if (!file.exists() || PutDirectory == null || PutFileName == null || PutFileName.isEmpty()) {
            return null;
        }

        PutObjectRequest put = new PutObjectRequest(BucketName, PutDirectory + PutFileName, filePath);
        put.setCRC64(OSSRequest.CRC64Config.YES); // If you enable CRC64 check will reduce the transmission speed

        // Set the server's callback url
        if(OSSCallBackUrl != null && !OSSCallBackUrl.isEmpty()){
            Map<String, String> map = new HashMap<String, String>();
            map.put(IAliOSSResult.CallbackUrl, OSSCallBackUrl);
            map.put(IAliOSSResult.CallbackBody, "filename=" + PutFileName);
            put.setCallbackParam(map);
        }

        // asyc progress
        if(iProgress != null) {
            put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                    int progress = (int) (100 * currentSize / totalSize);
                    iProgress.onProgress(progress, currentSize, totalSize);
                }
            });
        }

        // start push
        PutObjectResult result = oss.putObject(put);

        /*
        Map<String, String> map = new HashMap<>();
        map.put(IAliOSSResult.ETag, result.getETag()); // Tag: 420C0592576D4CC69461EDDF43096392
        map.put(IAliOSSResult.RequestId, result.getRequestId()); // RequestId: 5EAEB19A227FE637399136B9
        map.put(IAliOSSResult.FileName, put.getObjectKey()); // ObjectKey: qinauzi/image.jpg
        map.put(IAliOSSResult.BucketName, put.getBucketName()); // BucketName: luzhuo-data
        map.put(IAliOSSResult.LocalFilePath, put.getUploadFilePath()); // LocalFilePath: /storage/emulated/0/image.jpg
        */

        String filePathUrl;
        if(Endpoint.startsWith("oss-")) filePathUrl = BucketName + "." + Endpoint + "/";
        else filePathUrl = Endpoint + "/";

        return filePathUrl + put.getObjectKey();
    }
}
