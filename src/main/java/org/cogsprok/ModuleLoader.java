package org.cogsprok.gribeauval;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/** Facilitates access to Module jar files.
 * <p>
 * Instantiating the class with a URL of a jar file opens a JARUrlConnection
 * with the jar file, allowing access to the classes and attributes in the jar.
 * Provides basic methods for getting main attributes, or invoking the <code>main</code>
 * method of the jar module. 
 * <p> 
 * Intended to server as a basic connection and class loader for an individual module,
 * can be extended and functionality added when more complexity is required for interaction
 * with a module. 
 * <p>
 * Superclass URLClassLoader provides extensive functionality through inherited methods
 * for finding and instantiating individual classes, and should be taken advantage as 
 * means for core application logic to interact with modules. 
 * 
 * @author John Wilson
 * @version 1.0
 *
 */
public class ModuleLoader extends URLClassLoader {
	
	private URL url;
	private Attributes attributes;
	private String displayName;
	private String mainClass;
	protected Boolean isConnected = false;
	
	/** Constructor. Creates JARUrlConnection with Module.
	 * 
	 * @param url absolute URL of Module location.
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public ModuleLoader(URL url) throws IOException, MalformedURLException {
		super(new URL[] {url});
		this.url = url;
		try {
		open();
		}
		catch (MalformedURLException mue) {
			throw new MalformedURLException(
			"Malformed URL, Cannot Construct Object");
		}
		catch (IOException io) { 
			io.printStackTrace();
			throw new IOException(
				"IOException, Cannot Construct Object");
		}
		
		
	}
	
	/**Establish connection with Module, set Fields with information from Manifest.
	 * 
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private void open() throws IOException, MalformedURLException {
	URL u = new URL("jar", "", url + "!/");
	try {
	JarURLConnection moduleConnection = (JarURLConnection)u.openConnection();
	this.attributes = moduleConnection.getMainAttributes();
	} catch(MalformedURLException m) {
		throw new MalformedURLException();
	} catch(IOException i) {
		throw new IOException();
	}
	this.isConnected = true;
	this.displayName = attributes.getValue("Display-Name");
	this.mainClass = attributes.getValue("Main-Class");
	}
	
	/**Calls Superclass close(), sets instance isConnected field to false on success.
	 * 
	 */
	@Override
	public void close() throws IOException {
		try {
			super.close();
		} catch (IOException io) {
			throw new IOException();
		}
		this.isConnected = false;
	}
	
	/** Used to reconnect to module if connection has been closed.
	 * 
	 * @return boolean for success/failure
	 */
	public boolean connect() {
		if(!isConnected) 
			try {
				open(); 
			} catch (Exception e) {
				return false;
			}
		return true;
		
		
	}
	
	/** Get Module Display-Name as declared in Manifest Header
	 * 
	 * @return String value of Display-Name Manifest Header.
	 */
	public String getDisplayName() {
		return this.displayName;
	}
	
	/** Get Module Main Class 
	 * 
	 * @return String value of Main-Class Manifest Header.
	 */
	public String getMainClass() {
		return this.mainClass;
	}
	
	/** Get complete attributes of Module
	 * 
	 * @return Attributes
	 */
	public Attributes getAttributes() {
		return this.attributes;
	}
	
	public Manifest getManifest() {
		return this.getManifest();
	}
	
	/** Invokes main method of connected module.
	 *  
	 * @param args String[] of arguments to pass to module main method.
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	public void invoke(String[] args)
		    throws ClassNotFoundException,
		           NoSuchMethodException,
		           InvocationTargetException
		{
		    Class<?> c = loadClass(mainClass);
		    Method m = c.getMethod("main", new Class<?>[] { args.getClass() });
		    m.setAccessible(true);
		    int mods = m.getModifiers();
		    if (m.getReturnType() != void.class || !Modifier.isStatic(mods) ||
		        !Modifier.isPublic(mods)) {
		        throw new NoSuchMethodException("main");
		    }
		    try {
		        m.invoke(null, new Object[] { args });
		    } catch (IllegalAccessException e) {
		       e.printStackTrace();
		    }
		}
	
}
