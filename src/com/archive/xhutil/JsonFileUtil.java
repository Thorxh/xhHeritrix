package com.archive.xhutil;

import java.io.File;
import java.io.IOException;

import com.archive.xhentity.WenkuEntity;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 将数据以 json 格式保存
 * 
 * @author thor
 *
 */
public class JsonFileUtil {
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static void writeToJsonFile(WenkuEntity wenkuEntity, File output) {
		try {
			mapper.writeValue(output, wenkuEntity);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
