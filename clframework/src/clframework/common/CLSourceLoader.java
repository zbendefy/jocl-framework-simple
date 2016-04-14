package clframework.common;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

public class CLSourceLoader {

	private static String readLocalFile(String fname) {
		InputStream input = CLSourceLoader.class.getResourceAsStream(fname);

		java.util.Scanner s = new java.util.Scanner(input);
		s.useDelimiter("\\A");
		String ret = s.hasNext() ? s.next() : "";
		s.close();
		return ret;
	}
	
	/**
	 * 
	 * @param fname
	 * @return
	 * @throws IOException
	 */
	private static String readFile(String fname) throws IOException
	{
		byte[] encoded = Files.readAllBytes(Paths.get(fname));
		  return new String(encoded);
	}
	
	/**
	 * Reads a local file
	 * @param localpath
	 * @param dictionary For each key in this hashmap, each occurence will be replaced with it's value pair in the source file.
	 * @return
	 */
	public static String getLocalSource(String localpath, Map<String, String> dictionary)
	{
		String file = readLocalFile(localpath);
		return processFile(file, dictionary);
	}
	
	public static String getLocalSource(String localpath)
	{
		return getLocalSource(localpath, null);
	}
	
	/**
	 * 
	 * @param filepath
	 * @param dictionary For each key in this hashmap, each occurence will be replaced with it's value pair in the source file.
	 * @return
	 * @throws IOException
	 */
	public static String getFileSource(String filepath, Map<String, String> dictionary) throws IOException
	{
		String file = readFile(filepath);
		return processFile(file, dictionary);
	}
	
	/**
	 * 
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public static String getFileSource(String filepath) throws IOException
	{
		return getFileSource(filepath, null);
	}
	
	/**
	 * Replaces a set of keys with the corresponding values
	 * @param content The file content
	 * @param dictionary A map containing keywords to replace
	 * @return The processed file content
	 */
	public static String processFile(String content, Map<String, String> dictionary)
	{
		if (dictionary == null)
			return content;
		
		for (String key : dictionary.keySet()) {
			content = content.replace(key, dictionary.get(key));
		}
		
		return content;
	}
}
