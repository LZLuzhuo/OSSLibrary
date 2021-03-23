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
 * Description:
 *
 * @Author: Luzhuo
 * @Creation Date: 2020/8/2 13:31
 * @Copyright: Copyright 2020 Luzhuo. All rights reserved.
 **/
public class OSSSTSBean {
    // OSSFederationToken(dataBean.AccessKeyId, dataBean.AccessKeySecret, dataBean.SecurityToken, dataBean.Expiration);
    public String AccessKeyId;
    public String AccessKeySecret;
    public String SecurityToken;
    public String Expiration;

    public OSSSTSBean(String accessKeyId, String accessKeySecret, String securityToken, String expiration) {
        this.AccessKeyId = accessKeyId;
        this.AccessKeySecret = accessKeySecret;
        this.SecurityToken = securityToken;
        this.Expiration = expiration;
    }
}
