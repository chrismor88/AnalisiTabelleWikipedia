package util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

public class ReaderFileConfig {
	
	private final String propFileName = "config_chris.properties";
	private final Map<String,String> keyToValue;
	
	
	
	public static void main(String[] args) {
		ReaderFileConfig readerFileConfig = new ReaderFileConfig();
		try {
			String result = readerFileConfig.getValueOf("path_dump_statistics");
			System.out.println(result);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public ReaderFileConfig() {
		keyToValue= new HashMap<String,String>();
		populateMap();
		
	}
	
	
	private void populateMap() {
		InputStream inputStream = null;

		Properties prop = new Properties();

		try {
			inputStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	
		for(Entry<Object, Object> e: prop.entrySet()){
			keyToValue.put((String)e.getKey(),(String)e.getValue());
		}
	}


	public String getValueOf(String key) throws FileNotFoundException{
		if(!keyToValue.containsKey(key))
			throw new FileNotFoundException("property '" + key + "' not found in file property");
		
		else
			return keyToValue.get(key);
	}
	
}
