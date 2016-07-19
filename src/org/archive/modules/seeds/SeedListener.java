/*
 *  This file is part of the Heritrix web crawler (crawler.archive.org).
 *
 *  Licensed to the Internet Archive (IA) by one or more individual 
 *  contributors. 
 *
 *  The IA licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.archive.modules.seeds;

import org.archive.modules.CrawlURI;

/**
 * Implemented by components which want notifications of 
 * seed list changes.
 * 
 * <br><br>
 * 
 * 那些想在种子列表改变后获得通知的类实现这个接口(成为观察者)
 * 
 * @author gojomo
 */
public interface SeedListener {
    void addedSeed(final CrawlURI uuri);
    boolean nonseedLine(String line);
    void concludedSeedBatch();
}
