package org.cogsprok.gribeauval;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.jar.JarFile;
/** Class for scanning and creating registry of Modules directory
 *  
 * @author John Wilson
 * @version 1.0
 *
 */
public final class ModuleScanner {
	
	/** Default Constructor. Calls <code>scan</code> method. */
	public ModuleScanner() throws IOException {
		super();
		scan();
	}
	
		/** Scan Modules directory, pass jar filenames, URLs, and JarFile objects to ModuleRegistry
		 * <p>
		 * Finds all .jar files in Modules Directory, ignores any non .jar files.
		 * Creates a File array of the jar files, which is iterated over to create a HashMap of
		 * URLs of the jar file, keyed by filename, and a HashMap of JarFile objects, again keyed
		 * by filename. Both HashMaps are passed to the {@link ModuleRegistry#register ModuleRegistry.register} method.
		 * 
		 * @throws IOException
		 */
		public static void scan() throws IOException {
			File modules = new File("Modules");
			if(modules.isDirectory()) {
				File [] jars = modules.listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".jar");	
					}
				});
				HashMap<String, URL> installed = new HashMap<>();
				HashMap<String, JarFile> jarMap = new HashMap<>();
				for(File f:jars) {
					URL u = f.toURI().toURL();
					installed.put(f.getName(), u);
					try {
						jarMap.put(f.getName(), new JarFile(f));
					} catch (IOException io) {
						io.printStackTrace();
					}
				}
				ModuleRegistry.register(jarMap, installed);
			
			} else {
				throw new IOException("Missing Modules Directory");
			}
		
		}
}
