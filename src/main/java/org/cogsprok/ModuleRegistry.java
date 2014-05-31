package org.cogsprok.gribeauval;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;


/** Central Registry of Module Attributes and Information
 * <p>
 * Intended to be used as a central source to retrieve any necessary information
 * about Modules found in the Modules Directory. Consists mainly of HashMaps storing
 * various information, generally keyed with each Module's jar filename. 
 * <p>
 * The {@link ModuleScanner#scan() ModuleScanner.scan} method invokes the {@link #register(HashMap, HashMap) register} method
 * and passes it HashMaps of JarFile objects and URLs of Modules found. From this the <code>register</code> 
 * method compiles various information.
 * <p>
 * Specific information such as the classes in a particular module, the headers and values of a module's manifest,
 * and the Display-Names of the modules can be retrieved. The URL location object can also be retrieved for all or
 * a specific Module, which is useful for creating a {@link ModuleLoader} instance for a Module. 
 * 
 * @version 1.0
 * @author John Wilson
 *
 */
public final class ModuleRegistry {
	
	private static HashMap<String, String> titleMap = new HashMap<>();
	private static HashMap<String, ArrayList<String>> classMap = new HashMap<>();
	private static HashMap<String, HashMap<String, String>> headerMap = new HashMap<>();
	private static HashMap<String, URL> urlMap;
	private static Manifest man = null;
	
	
	/** Collects Basic Information about Module Jar Files
	 * 
	 * @param jarm HashMap<String, JarFile>
	 * @param urls HashMap<String, URL> 
	 */
	static void register(HashMap<String, JarFile> jarm, HashMap<String, URL> urls) {
		urlMap = urls;
		//Step through each jar's entrySet 
		for(Map.Entry<String, JarFile> jar:jarm.entrySet()) {
			classMap.put(jar.getKey(), new ArrayList<String>());
			Enumeration<JarEntry> enumerator = jar.getValue().entries();
			ArrayList<String> hsc = new ArrayList<>();
			HashSet<String> hse = new HashSet<>();
			
			//Populate HashSet with list of jar's class files.
			while(enumerator.hasMoreElements()) {
				JarEntry entry = enumerator.nextElement();
				hse.add(entry.getName());
				if(entry.getName().endsWith(".class")) {
					String s = entry.getName();
					String[] split = s.split("/");
					for(String sp:split) {
						if(sp.endsWith(".class"))
						hsc.add(sp);
					}
				}
			}
			//Add key: jar name, value: HashSet of class files to classes map
			classMap.put(jar.getKey(), hsc);
			
			//Get Manifest and MainAttributes
			try {
				man = jar.getValue().getManifest();	
			} catch (IOException io) {
				io.printStackTrace();
			}
			Attributes a = man.getMainAttributes();
		    //Create TreeMap of Attribute String pairs, 
			Set<Map.Entry<Object,Object>> attrib = a.entrySet();
			HashMap<String, String> tma = new HashMap<>();
			for(Map.Entry<Object, Object> attr:attrib) {
				tma.put(attr.getKey().toString(), attr.getValue().toString());
			}
			//Add Map of Attribute header pairs to attributes map with jar name key
			headerMap.put(jar.getKey(), tma);	
		}	
	}
	
	/** Get TreeSet of Module Display-Names.
	 *
	 * @return TreeSet<String>
	 */
	public static TreeSet<String> getTitleSet() {
		TreeSet<String> titles = new TreeSet<>();
		if(titleMap.isEmpty()) {
			for(Map.Entry<String, HashMap<String, String>> entry: headerMap.entrySet()) {
				titleMap.put(entry.getKey(), entry.getValue().get("Display-Name"));
			}
		}
		for(Map.Entry<String, String> name:titleMap.entrySet()) {
			titles.add(name.getValue());
		}
	    	return titles;   
	}
	
	/** Get Map of Module filenames with associated Display-Name
	 * 
	 * @return HashMap<String, String> Module filename, Display-Name header value
	 */
	public static HashMap<String, String> getTitleMap() {
		if(titleMap.isEmpty()) {
			for(Map.Entry<String, HashMap<String, String>> entry: headerMap.entrySet()) {
				titleMap.put(entry.getKey(), entry.getValue().get("Display-Name"));
			}
		}
		return titleMap;
	}
	
	/** Get Manifest Display-Name Header value as String
	 * 
	 * @param jarName String Name jar file name of module
	 * @return String Value of Display-Name Header of Manifest
	 */
	public static String getTitle(String jarName) {
		return headerMap.get(jarName).get("Display-Name");
	}
	
	/** Get list of classes for specific jar file
	 * 
	 * @param jarName Filename of jar file.
	 * @return ArrayList<String> List of class files in Jar
	 */
	public static ArrayList<String> getClassList(String jarName) {
		
		return classMap.get(jarName);
	}
	
	/** Get URL object for location of Module.
	 * 
	 * @param name String for Module filename.jar
	 * @return URL absolute URL of Module location.
	 */
	public static URL getUrl(String name) {
		if(urlMap.containsKey(name)) {
			return urlMap.get(name);
		} else {
			String id = null;
			for(Map.Entry<String, HashMap<String, String>> entry:headerMap.entrySet()) {
				if(entry.getValue().containsValue(name)) {
					id = entry.getKey();
				}
			}
			return urlMap.get(id);
		}
	}
	
	/** Get Map of jar filenames with associated URL Object
	 * 
	 * @return HashMap<String, URL> 
	 */
	public static HashMap<String, URL> getUrlMap() {
		return urlMap;
	}
}
