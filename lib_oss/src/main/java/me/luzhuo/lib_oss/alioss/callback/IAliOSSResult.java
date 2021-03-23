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
package me.luzhuo.lib_oss.alioss.callback;

import java.util.List;
import java.util.Map;

/**
 * Description: ALiOSS specific callback
 *
 * @Author: Luzhuo
 * @Creation Date: 2020/5/4 9:54
 * @Copyright: Copyright 2020 Luzhuo. All rights reserved.
 **/
public interface IAliOSSResult {
    /**
     * Unified key name
     */
    public static final String BucketName = "BucketName", Prefix = "Prefix", FilePath = "FilePath", FileName = "FileName",
            RequestId = "RequestId", LocalFilePath = "LocalFilePath", ETag = "ETag",
            LastModified = "LastModified", NetFileUrl = "NetFileUrl", RecordFile = "RecordFile";
    /**
     * the server's callback url.
     */
    public static final String CallbackUrl = "callbackUrl", CallbackBody = "callbackBody";

    /**
     * get file list by bucket.
     *
     * the net file absolute path is <pre> http:// + filePath + fileName. </pre>
     *
     * @param filePath file path.
     * @param fileLists file info list
     */
    public void onFileList(String filePath, List<Map<String, String>> fileLists);

    /**
     * error
     *
     * Note: This callback is executed in the child thread.
     *
     * @param errMessage err message
     */
    public void onError(String errMessage);
}
