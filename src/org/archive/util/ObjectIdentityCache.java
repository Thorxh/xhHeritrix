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

package org.archive.util;

import java.io.Closeable;
import java.util.Set;

/**
 * An object cache for create-once-by-name-and-then-reuse objects. 
 * 
 * Objects are added, but never removed. Subsequent(后来的,随后的) get()s using the 
 * same key will return the exact(准确的) same object, UNLESS all such objects
 * have been forgotten, in which case a new object MAY be returned. 
 * 
 * This allows implementors (such as ObjectIdentityBdbCache or 
 * CachedBdbMap) to page out (aka(又叫做 also known as) 'expunge'(擦去,删掉)) instances to
 * persistent storage while they're not being used. However, as long as
 * they are used (referenced), all requests for the same-named object
 * will share a reference to the same object, and the object may be
 * mutated(改变) in place without concern for(关心) explicitly(明确地,明白地) persisting its
 * state to disk.  
 * 
 * <p>create-once-by-name-and-then-reuse 对象缓存
 * 
 * @param <V>
 */
public interface ObjectIdentityCache<V extends IdentityCacheable> extends Closeable {
    /** get the object under the given key/name -- but should not mutate 
     * object state
     * 
     * <p>返回给定 key/name 对应的对象
     * 
     */
    public abstract V get(final String key);
    
    /** get the object under the given key/name, using (and remembering)
     * the object supplied(提供) by the supplier if no prior(优先的,在前的) mapping exists 
     * -- but should not mutate object state
     * 
     * <p>返回给定 key/name 对应的对象，如果没有已存在的 mapping ，则在 supplierOrNull 中查找
     * 
     */
    public abstract V getOrUse(final String key, Supplier<V> supplierOrNull);

    /** force the persistent backend, if any, to be updated with all 
     * live object state
     *
     * <p>强制持久化后端更新所有 live object 的状态
     * 
     */ 
    public abstract void sync();
    
    /** force the persistent backend, if any, to eventually be updated with 
     * live object state for the given key 
     * 
     * <p>强制持久化后端更新给定key对应的 live object 的状态
     * 
     */ 
    public abstract void dirtyKey(final String key);

    /** close/release any associated resources */ 
    public abstract void close();
    
    /** count of name-to-object contained */ 
    public abstract int size();

    /** set of all keys */ 
    public abstract Set<String> keySet();
}