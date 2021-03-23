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
package me.luzhuo.lib_oss.callback;

import java.util.Map;

/**
 * Description:
 *
 * @Author: Luzhuo
 * @Creation Date: 2020/5/3 21:30
 * @Copyright: Copyright 2020 Luzhuo. All rights reserved.
 **/
public interface IFileResult {
    /**
     * Successfully uploaded to the network
     * the net file absolute path is <pre> http:// + filePath + fileName. </pre>
     * the local file absolute path is <pre> filePath + fileName </pre>
     *
     * Note: This callback is executed in the child thread.
     *
     * @param filePath File path from the network.
     *                 case (put): luzhuo-data.oss-cn-hangzhou.aliyuncs.com/
     *                 case (get): /storage/emulated/0/
     * @param fileName File name from the network.
     *                 case: qinauzi/666.jpg
     * @param bundleData Bundled data.
     */
    public void onSucess(String filePath, String fileName, Map<String, String> bundleData);

    /**
     * error
     *
     * Note: This callback is executed in the child thread.
     *
     * @param errMessage err message
     */
    public void onError(String errMessage);
}
