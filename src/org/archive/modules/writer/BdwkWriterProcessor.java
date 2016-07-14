package org.archive.modules.writer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.archive.io.ReadSource;
import org.archive.io.RecordingInputStream;
import org.archive.io.ReplayInputStream;
import org.archive.modules.CrawlURI;
import org.archive.modules.Processor;
import org.archive.spring.ConfigPath;
import org.archive.util.Recorder;
import org.archive.util.SurtPrefixSet;

import com.archive.xhentity.WenkuEntity;
import com.archive.xhutil.BaiduBaikeHtmlParser;
import com.archive.xhutil.JsonFileUtil;

public class BdwkWriterProcessor  extends Processor {

	protected SurtPrefixSet surtPrefixes = new SurtPrefixSet();
	private static final Logger logger =
	        Logger.getLogger(BdwkWriterProcessor.class.getName());
	
	/** 初始化URL过滤规则 **/
	protected void buildSurtPrefixSet() {
        if (getSurtsSource() != null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("reading surt prefixes from " + getSurtsSource());
            }
            Reader reader = getSurtsSource().obtainReader();
            try {
                surtPrefixes.importFromMixed(reader, true);
            } finally {
                IOUtils.closeQuietly(reader);
            }
        }
    }
	
	protected ReadSource surtsSource = null;
    public ReadSource getSurtsSource() {
        return surtsSource;
    }
    public void setSurtsSource(ReadSource surtsSource) {
        this.surtsSource = surtsSource;
    }
	
	@Override
	protected boolean shouldProcess(CrawlURI curi) {
		
		String candidateSurt = SurtPrefixSet.getCandidateSurt(curi.getUURI());
        if (surtPrefixes != null && !surtPrefixes.containsPrefixOf(candidateSurt)) {
            return false;
        }
        if(curi.getContentType().equals("text/html")) {
        	return true;
        }
        return false;
        
	}

	@Override
	protected void innerProcess(CrawlURI curi) throws InterruptedException {
		Recorder recorder = curi.getRecorder();
		RecordingInputStream recis = recorder.getRecordedInput();
		if (0L == recis.getResponseContentLength()) {
            return;
        }
		try {
			writeToPath(recis, curi.getUURI().toString());
		} catch (IOException e) {
			System.out.println(curi.getUURI() + " - save failed ..");
		}
	}
	
	private void writeToPath(RecordingInputStream recis, String uuri)
	        throws IOException {
		
        ReplayInputStream replayis = null;
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            replayis = recis.getMessageBodyReplayInputStream();

            /**
             * 将流中字节转为字符串
             */
            br = new BufferedReader(new InputStreamReader(replayis, "utf-8"));
            StringBuffer sb = new StringBuffer();
            String line = new String("");
            while ((line = br.readLine()) != null){
            	sb.append(line);
            }
            
            WenkuEntity wenkuEntity = null;
			try {
				wenkuEntity = BaiduBaikeHtmlParser.parse(sb.toString(), uuri);
				System.out.println(uuri + " - save ok ..");
			} catch (Exception e) {
				System.out.println(uuri + " - save failed ..");
				return ;
			}
            
			String baseDir = getPath().getFile().getAbsolutePath();
			File parentFile = new File(baseDir + File.separator + "json_data" + File.separator);
			File destFile = new File(parentFile, wenkuEntity.getTitle() + ".json");
			
			if(! parentFile.exists()) {
				parentFile.mkdirs();
			}
			
			if(! destFile.exists()) {
				destFile.createNewFile();
			}
			
	        JsonFileUtil.writeToJsonFile(wenkuEntity, destFile);
			
        } finally {
            IOUtils.closeQuietly(replayis);
            IOUtils.closeQuietly(br);
            IOUtils.closeQuietly(bw);
        }

	}
	
	/**
     * Top-level directory for mirror files.
     */
    protected ConfigPath path = new ConfigPath("mirror writer top level directory", "${launchId}/mirror");
    public ConfigPath getPath() {
        return this.path;
    }
    public void setPath(ConfigPath s) {
        this.path = s;
    }

}
