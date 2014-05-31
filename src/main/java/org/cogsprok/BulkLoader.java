
package org.cogsprok.gribeauval;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/** Load and access all installed Modules
 * 
 * @author John Wilson
 * @version 1.0
 *
 */
public class BulkLoader {
	
	/** Default Constructor. 
	 * <p>
	 * Instantiating <code>BulkLoader</code> automatically runs {@link #start() start} method
	 */
	public BulkLoader() {
		super();
		start();
	}
	
	private static HashMap<String, URL> jars = new HashMap<>();
	private static HashMap<String, ModuleLoader> modules = new HashMap<>();
	private static TreeMap<String, String> displayNames = new TreeMap<>();
	
	/** Creates a ModuleLoader instance for every installed Module
	 * 
	 */
	public static void start() {
		if(jars.isEmpty()) {
			jars = ModuleRegistry.getUrlMap();
		}
		for(Map.Entry<String, URL> entry:jars.entrySet()) {
			String s = entry.getKey().substring(0, entry.getKey().lastIndexOf('.'));
			System.out.println(s);
			try {
			modules.put(s, new ModuleLoader(entry.getValue()));
			} catch (IllegalStateException is) {
				is.printStackTrace();
			} catch (MalformedURLException mf) {
				mf.printStackTrace();
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}
	
	/** Get Map containing Module names and Loader instances.
	 * 
	 * @return HashMap<String, ModuleLoader>
	 */
	public static HashMap<String, ModuleLoader> getModules() {
		return modules;
	}
	
	/** Get ArrayList of Module names Loaded by BulkLoader
	 * 
	 * @return ArrayList<String>
	 */
	public static ArrayList<String> getNames() {
		ArrayList<String> names = new ArrayList<String>();
		for(Map.Entry<String, ModuleLoader> entry:modules.entrySet()) {
			names.add(entry.getKey());
		}
		return names;
	}
	
	/** Get Map of Module Display-Names and Names for Loader instance access.
	 * 
	 * @return TreeMap<String,String>
	 */
	public static TreeMap<String, String> getDisplayNames() {
		for(Map.Entry<String, ModuleLoader> entry:modules.entrySet()) {
			displayNames.put(entry.getValue().getDisplayName(), entry.getKey());
		}
		return displayNames;
	}
	
	/** Get individual ModuleLoader instance 
	 * 
	 * @param name String name of module
	 * @return ModuleLoader instance of module
	 */
	public static ModuleLoader getModule(String name) {
		if(modules.containsKey(name)) {
		return modules.get(name);
		}
		else {
			String id = null;
			if(displayNames.isEmpty()) {
				@SuppressWarnings("unused")
				TreeMap<String,String> tempMap = getDisplayNames();
			}
			for(Map.Entry<String, String> entry:displayNames.entrySet()) {
				if(entry.getValue().equals(name)) {
					id = entry.getKey();
				}
			}
			return modules.get(id);
		}
	}
	
	/** Check isConnected status of Module
	 * 
	 * @param name String name of module
	 * @return boolean
	 */
	public static boolean isConnected(String name) {
		return modules.get(name).isConnected;
	}
	
	/** Close an individual Module by name.
	 * 
	 * @param name String name of module
	 * @return boolean success or failure. 
	 */
	public static boolean closeModule(String name) {
		if(modules.get(name).isConnected);
		try {
			modules.get(name).close();
		} catch (IOException fail) {
			return false;
		}
		return true;
	}
	
	/** Close (if open) Module, and remove entry from modules Map
	 * 
	 * @param name String name of module
	 * @return boolean success or failure
	 */
	public static boolean removeModule(String name) {
		if(modules.get(name).isConnected) {
		try {
			modules.get(name).close();
		} catch (IOException fail) {
			return false;
		}
		modules.remove(name);
		return true;
		} else {
			modules.remove(name);
			return true;
		}
	}
	
	/** Reestablish connection on Module previously closed.
	 * 
	 * @param name String name of module
	 * @return boolean success or failure
	 */
	public static boolean connectModule(String name) {
		if(!modules.get(name).isConnected) {
			if(modules.get(name).connect()) {
				return true;
			} else { return false; }
		}else { return true; }	
	}
}
