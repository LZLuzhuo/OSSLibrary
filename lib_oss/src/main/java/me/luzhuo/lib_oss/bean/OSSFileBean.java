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
package me.luzhuo.lib_oss.bean;

/**
 * Description: Common file objects
 *
 * @Author: Luzhuo
 * @Creation Date: 2020/5/6 3:21
 * @Copyright: Copyright 2020 Luzhuo. All rights reserved.
 **/
public class OSSFileBean {
    /**
     * Original file path
     */
    private String filePath;

    /**
     * network file path
     */
    private String networkPath;

    /**
     * file name
     */
    private String fileNameKey;

    /**
     * This tag will not be processed in any way
     */
    public Object tag;

    // ===== System use =====
    public String getFilePath(){
        return filePath;
    }

    public String getFileNameKey(){
        return fileNameKey;
    }

    public void setNetworkPath(String networkPath){
        this.networkPath = networkPath;
    }
    // ===== System use =====

    public OSSFileBean(String filePath, String fileNameKey){
        this(filePath, fileNameKey, null);
    }

    public OSSFileBean(String filePath, String fileNameKey, Object tag){
        this.filePath = filePath;
        this.fileNameKey = fileNameKey;
        this.tag = tag;
    }

    public String getPath(){
        return networkPath;
    }

    @Override
    public String toString() {
        return "OSSFileBean{" +
                "filePath='" + filePath + '\'' +
                ", networkPath='" + networkPath + '\'' +
                ", fileNameKey='" + fileNameKey + '\'' +
                ", tag=" + tag +
                '}';
    }
}
