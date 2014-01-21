/**
 * 
 */
package com.millet;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import com.millet.Vehicle;

/**
 * @author remillet
 * 
 */
public class InterfaceRunner {

	private static String PLUGINS = "plugins";
	private static String GET_NAME_METHOD = "getName";

	/**
	 * @param args - command line arguments
	 * 
	 * We look in the current directory for a directory named 'plugins'.  If that directory
	 * contains any .class files, we dynamically load them and test them to see if they implement the com.millet.Vehicle inteface. If
	 * they do, we create an instance of the class and invoke the "getName" method.
	 * 
	 */
	public static void main(String[] args) {
		// Get the current directory
		File currentDir = new File(System.getProperty("user.dir"));
		System.out.println("The current directory is: " + currentDir.getAbsolutePath());
		
		// Look for the "plugins" directory and get a list of the class files
		File pluginsDir = new File(currentDir + "/" + PLUGINS);
		if (pluginsDir.exists() && pluginsDir.isDirectory()) {
			// First, get a list of all the Java class files
			File[] classFileList = getClassFiles(pluginsDir);
			
			// Next, from the list of class files, create a list of Class objects
			ArrayList<Class<?>> classList = getClasses(pluginsDir, classFileList);
			
			// Test the Class object to see if it implementents the "com.millet.Vehicle" Inteface
			// and invoke the "getName" method if it does.
			for (Class<?> theClass : classList) {
				if (Vehicle.class.isAssignableFrom(theClass) == true) {
					invokeMethod(theClass, GET_NAME_METHOD);
				}
			}
		} else {
			System.err.println(String.format("The directory '%s' is missing.", pluginsDir.getAbsolutePath()));
		}
	}
	
	/*
	 * Returns an ArrayList of Java Class objects that exist in the directory "pluginsDir"
	 */
	private static ArrayList<Class<?>> getClasses(File pluginsDir, File[] classFileList) {
		ArrayList<Class<?>> result = null;

		try {
			URL[] urls = { pluginsDir.toURI().toURL() };
			URLClassLoader classLoader = new URLClassLoader(urls, InterfaceRunner.class.getClassLoader());

			result = new ArrayList<Class<?>>();
			for (File classFile : classFileList) {
				ClassParser classParser = new ClassParser(classFile.getAbsolutePath());
				JavaClass javaClass = classParser.parse();
				String className = javaClass.getClassName();
				System.out.println(String.format("The class name is '%s'.", className));
				Class<?> theClass = classLoader.loadClass(className);
				result.add(theClass);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	/*
	 * Creates an instance of the class 'theClass' and invokes the method 'theMethod'
	 */
	private static void invokeMethod(Class<?> theClass, String theMethod) {
		try {
			Object instance = theClass.newInstance();
			Method getNameMethod = theClass.getMethod(theMethod);
			String result = (String) getNameMethod.invoke(instance);
			System.out.println(String.format("The result of calling 'getName()' on '%s' is '%s'.", theClass.getName(), result));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Returns the list of files in the 'pluginsDir' that end with ".class"
	 */
	private static File[] getClassFiles(File pluginsDir) {
		File[] result = null;

		// create new filename filter for ".class" files
		FilenameFilter fileNameFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				boolean result = false;
				if (name.lastIndexOf('.') > 0) {
					// get last index for '.' char
					int lastIndex = name.lastIndexOf('.');

					// get extension
					String str = name.substring(lastIndex);

					// match path name extension
					if (str.equals(".class")) {
						result = true;
					}
				}
				return result;
			}
		};

		//
		// Use the FilenameFilter we created "in-line" above to get a list of all files
		// ending with ".class".
		//
		result = pluginsDir.listFiles(fileNameFilter);

		return result;
	}

}
