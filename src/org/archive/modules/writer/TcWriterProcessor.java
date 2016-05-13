package org.archive.modules.writer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.archive.io.RecordingInputStream;
import org.archive.io.ReplayInputStream;
import org.archive.modules.CrawlURI;
import org.archive.modules.Processor;
import org.archive.net.UURI;

import com.archive.xhentity.HtmlSaver;
import com.archive.xhutil.HtmlParser;

public class TcWriterProcessor extends Processor {

	@Override
	protected boolean shouldProcess(CrawlURI curi) {
		// TODO Auto-generated method stub
		if(curi.getContentType().equals("text/html")) {
			return true;
		}
		return false;
	}

	@Override
	protected void innerProcess(CrawlURI curi) throws InterruptedException {
		// TODO Auto-generated method stub
		UURI uuri = curi.getUURI(); // Current URI.

        // Only http and https schemes are supported.
        String scheme = uuri.getScheme();
        if (!"http".equalsIgnoreCase(scheme)
                && !"https".equalsIgnoreCase(scheme)) {
            return;
        }
        RecordingInputStream recis = curi.getRecorder().getRecordedInput();
        if (0L == recis.getResponseContentLength()) {
            return;
        }
        String baseDir = "F:\\Crawl\\ithome.txt";
        File dest = new File(baseDir);
        try {
			writeToPath(recis, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeToPath(RecordingInputStream recis, File dest) throws IOException {
		File tf = new File (dest.getPath() + "N");
        ReplayInputStream replayis = null;
        FileOutputStream fos = null;
        try {
            replayis = recis.getMessageBodyReplayInputStream();
            fos = new FileOutputStream(tf);

            BufferedReader br = new BufferedReader(new InputStreamReader(replayis, "gb2312"));
            StringBuffer sb = new StringBuffer();
            String line = new String("");
            while ((line = br.readLine()) != null){
            	sb.append(line);
            }
            
            HtmlSaver htmlSaver = new HtmlParser().getTitleAndContent(sb.toString());
            if(htmlSaver != null) {
            	System.err.println(htmlSaver.getTitle());
            	System.out.println(htmlSaver.getContent());
            }
            
//            replayis.readFullyTo(fos);
        } finally {
            IOUtils.closeQuietly(replayis);
            IOUtils.closeQuietly(fos);
        }
//        if (!tf.renameTo(dest)) {
//            throw new IOException("Can not rename " + tf.getAbsolutePath()
//                                  + " to " + dest.getAbsolutePath());
//        }
	}
	
}
