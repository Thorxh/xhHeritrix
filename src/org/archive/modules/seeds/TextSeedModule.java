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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.io.IOUtils;
import org.archive.io.ReadSource;
import org.archive.modules.CrawlURI;
import org.archive.modules.SchedulingConstants;
import org.archive.net.UURI;
import org.archive.net.UURIFactory;
import org.archive.spring.WriteTarget;
import org.archive.util.ArchiveUtils;
import org.archive.util.DevUtils;
import org.archive.util.iterator.LineReadingIterator;
import org.archive.util.iterator.RegexLineIterator;
import org.springframework.beans.factory.annotation.Required;

/**
 * Module that announces a list of seeds from a text source (such
 * as a ConfigFile or ConfigString), and provides a mechanism for
 * adding seeds after a crawl has begun.
 *
 * <br><br>
 * 
 * 该模块从一个文本源获得种子，并且提供了在爬取开始之后添加种子的机制
 *
 * <br><br>
 * 过程：从配置或者文件中读取种子，通过发送通知将种子加入到BdbFrontier(猜测，待验证)
 * 
 * @contributor gojomo
 */
public class TextSeedModule extends SeedModule 
implements ReadSource {
    private static final long serialVersionUID = 3L;

    private static final Logger logger =
        Logger.getLogger(TextSeedModule.class.getName());

    /**
     * Text from which to extract seeds
     * 
     * <br><br>
     * 
     * 种子来源
     */
    protected ReadSource textSource = null;
    public ReadSource getTextSource() {
        return textSource;
    }
    @Required
    public void setTextSource(ReadSource seedsSource) {
        this.textSource = seedsSource;
    }
    
    /**
     * Number of lines of seeds-source to read on initial load before proceeding
     * with crawl. Default is -1, meaning all. Any other value will cause that
     * number of lines to be loaded before fetching begins, while all extra
     * lines continue to be processed in the background. Generally, this should
     * only be changed when working with very large seed lists, and scopes that
     * do *not* depend on reading all seeds. 
     * 
     * <br><br>
     * 
     * 在抓取之前，最初从seeds-source加载进来的行数。默认值是-1，意味着加载所有。任何其它值 
     * 将会导致那个数量的行在抓取开始之前被加载进来，然而其余的行在后台被处理。通常情况下，这个 
     * 值只有在有很多种子或者不依赖于读取所有种子的情况下才需要改变。 
     */
    protected int blockAwaitingSeedLines = -1;
    public int getBlockAwaitingSeedLines() {
        return blockAwaitingSeedLines;
    }
    public void setBlockAwaitingSeedLines(int blockAwaitingSeedLines) {
        this.blockAwaitingSeedLines = blockAwaitingSeedLines;
    }

    public TextSeedModule() {
    }

    /**
     * Announce all seeds from configured source to SeedListeners 
     * (including nonseed lines mixed in). 
     * @see org.archive.modules.seeds.SeedModule#announceSeeds()
     */
    public void announceSeeds() {
        if(getBlockAwaitingSeedLines()>-1) {
        	// CountDownLatch 的使用意义何在?
            final CountDownLatch latch = new CountDownLatch(getBlockAwaitingSeedLines());
            new Thread(){
                @Override
                public void run() {
                    announceSeeds(latch); 
                    // 为什么要有这段代码???
                    while(latch.getCount()>0) {
                        latch.countDown();
                    }
                }
            }.start();
            try {
                latch.await();
            } catch (InterruptedException e) {
                // do nothing
            } 
        } else {
            announceSeeds(null); 
        }
    }
    
    protected void announceSeeds(CountDownLatch latchOrNull) {
        BufferedReader reader = new BufferedReader(textSource.obtainReader());       
        try {
            announceSeedsFromReader(reader,latchOrNull);    
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }
            
    /**
     * Announce all seeds (and nonseed possible-directive lines) from
     * the given Reader
     * @param reader source of seed/directive lines
     * @param latchOrNull if non-null, sent countDown after each line, allowing 
     * another thread to proceed after a configurable number of lines processed
     */
    protected void announceSeedsFromReader(BufferedReader reader, CountDownLatch latchOrNull) {
        String s;
        Iterator<String> iter = 
            new RegexLineIterator(
                    new LineReadingIterator(reader),
                    RegexLineIterator.COMMENT_LINE,
                    RegexLineIterator.NONWHITESPACE_ENTRY_TRAILING_COMMENT,
                    RegexLineIterator.ENTRY);

        int count = 0; 
        while (iter.hasNext()) {
            s = (String) iter.next();
            if(Character.isLetterOrDigit(s.charAt(0))) {
                // consider a likely URI
                seedLine(s);
                count++;
                if(count%20000==0) {
                	// 强制调用已经失去引用的对象的finalize方法
                    System.runFinalization();
                }
            } else {
                // report just in case it's a useful directive
                nonseedLine(s);
            }
            if(latchOrNull!=null) {
                latchOrNull.countDown(); 
            }
        }
        publishConcludedSeedBatch(); 
    }
    
    /**
     * Handle a read line that is probably a seed.
     * 
     * <br><br>
     * 
     * 处理可能是种子的读入行 
     * 
     * @param uri String seed-containing line
     */
    protected void seedLine(String uri) {
        if (!uri.matches("[a-zA-Z][\\w+\\-]+:.*")) { // Rfc2396 s3.1 scheme,
                                                     // minus '.'
            // Does not begin with scheme, so try http://
            uri = "http://" + uri;
        }
        try {
            UURI uuri = UURIFactory.getInstance(uri);
            CrawlURI curi = new CrawlURI(uuri);
            curi.setSeed(true);
            curi.setSchedulingDirective(SchedulingConstants.MEDIUM);
            if (getSourceTagSeeds()) {
                curi.setSourceTag(curi.toString());
            }
            publishAddedSeed(curi);
        } catch (URIException e) {
            // try as nonseed line as fallback
            nonseedLine(uri);
        }
    }
    
    /**
     * Handle a read line that is not a seed, but may still have
     * meaning to seed-consumers (such as scoping beans). 
     * 
     * <br><br>
     * 
     * 处理一个不是种子但是对种子消费者还是有意义的的读入行 
     * 
     * @param uri String seed-containing line
     */
    protected void nonseedLine(String line) {
        publishNonSeedLine(line);
    }
    
    /**
     * Treat the given file as a source of additional seeds,
     * announcing to SeedListeners.
     * 
     * <br><br>
     * 
     * 从一个给定文件读取种子，并告知给监听者
     * 
     * @see org.archive.modules.seeds.SeedModule#actOn(java.io.File)
     */
    public void actOn(File f) {
        BufferedReader reader = null;
        try {
            reader = ArchiveUtils.getBufferedReader(f);
            announceSeedsFromReader(reader, null);    
        } catch (IOException ioe) {
            logger.log(Level.SEVERE,"problem reading seed file "+f,ioe);
        } finally {
            IOUtils.closeQuietly(reader);
        }
    }

    /**
     * Add a new seed to scope. By default, simply appends
     * to seeds file, though subclasses may handle differently.
     *
     * <p>This method is *not* sufficient to get the new seed 
     * scheduled in the Frontier for crawling -- it only 
     * affects the Scope's seed record (and decisions which
     * flow from seeds). 
     *
     *<br><br>
     *
     * 添加一个新的种子。默认情况下，只是把种子放到种子文件后面，子类 
     * 可以有不同的处理方式。 
     *  
     * 这个方法不能使新的种子被Frontier调度。也就是说，新的种子被当做普通 
     * 的种子对待。
     *
     * @param curi CandidateUri to add
     * @return true if successful, false if add failed for any reason
     */
    @Override
    public synchronized void addSeed(final CrawlURI curi) {
        if(!(textSource instanceof WriteTarget)) {
            // TODO: do something else to log seed update
            logger.warning("nowhere to log added seed: "+curi);
        } else {
            // TODO: determine if this modification to seeds file means
            // TextSeedModule should (again) be Checkpointable
            try {
                Writer fw = ((WriteTarget)textSource).obtainWriter(true);
                // Write to new (last) line the URL.
                fw.write("\n");
                fw.write("# Heritrix added seed " +
                    ((curi.getVia() != null) ? "redirect from " + curi.getVia():
                        "(JMX)") + ".\n");
                fw.write(curi.toString());
                fw.flush();
                fw.close();
            } catch (IOException e) {
                DevUtils.warnHandle(e, "problem writing new seed");
            }
        }
        publishAddedSeed(curi); 
    }

    public Reader obtainReader() {
        return textSource.obtainReader();
    }
}
