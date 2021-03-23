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

/**
 * Description: Progress
 *
 * @Author: Luzhuo
 * @Creation Date: 2020/5/3 21:11
 * @Copyright: Copyright 2020 Luzhuo. All rights reserved.
 **/
public interface IProgress {
    /**
     * Progress
     *
     * Note: This callback is executed in the child thread.
     * But non-sub-threads can update the progress bar.
     *
     * @param progress [0,1], int progress = (int) (100 * currentSize / totalSize);
     * @param currentSize current Size
     * @param totalSize total size
     */
    public void onProgress(int progress, long currentSize, long totalSize);
}
