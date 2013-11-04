/*
 * PluginManager.java
 * Oct 30, 2013
 *
 * Simple Web Server (SWS) for EE407/507 and CS455/555
 * 
 * Copyright (C) 2011 Chandan Raj Rupakheti, Clarkson University
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 * Contact Us:
 * Chandan Raj Rupakheti (rupakhcr@clarkson.edu)
 * Department of Electrical and Computer Engineering
 * Clarkson University
 * Potsdam
 * NY 13699-5722
 * http://clarkson.edu/~rupakhcr
 */
 
package plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import server.Server;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */

public class PluginManager extends Thread{
	private File dirLocation;
	private Server caller;

	public PluginManager(String defaultLocation, Server caller){
		this.dirLocation = new File(defaultLocation);
		this.caller = caller;
		start();
	}
	
	public HashMap<String, ArrayList<Class<?>>> readInPlugins() throws Exception{
		HashMap<String, ArrayList<Class<?>>> plugins = new HashMap<String, ArrayList<Class<?>>>();
		File[] files = this.dirLocation.listFiles();
		
		for (File file : files) {
			String fileName = file.getName();
			if (fileName.endsWith(".jar")) {
				String pluginName = fileName.replaceAll(".jar", "");
				ArrayList<Class<?>> classes = getClassesFromJar(file.getAbsolutePath());
				plugins.put(pluginName, classes);	
			} else if (fileName.endsWith(".txt")) {
				updateConfigurationMap(file);
			}
		}
		
		return plugins;
	}
	
	/**
	 * Updates the server's map of Request Method + Plugin Name + Servet URI -> Servlet Class Name
	 * 
	 * @param plugin File
	 */
	private void updateConfigurationMap(File configFile) {
		
		try 
		{
			//config file should be named config.txt to work
			String[] pluginMappings = openFile(configFile.getAbsolutePath());
	    	
		    for (int i = 0; i < pluginMappings.length; i++)
		    {
				try {
					String[] parsedMapping = pluginMappings[i].split("\\s+");
					String requestMethod = parsedMapping[0];
					String pluginServletURI = parsedMapping[1];
					String servletClass = parsedMapping[2];

					if (!caller.servletMappings.containsKey(pluginServletURI)) {
						HashMap<String, String> newMapping = new HashMap<String, String>();
						newMapping.put(requestMethod, servletClass);
						caller.servletMappings
								.put(pluginServletURI, newMapping);
					} else {
						if (!caller.servletMappings.get(pluginServletURI)
								.containsKey(requestMethod)) {
							caller.servletMappings.get(pluginServletURI).put(
									requestMethod, servletClass);
						}
					}
				} catch (NullPointerException e)
		    	{
		    		e.printStackTrace();
		    	}
				
		    }
			
		} catch (IOException e) 
		{
			e.printStackTrace();
		} 
	}
	
	
	/**
	 * @param path = directory of the file to be read
	 * @return textData  = array of strings in the file
	 */
	private String[] openFile(String path) throws IOException{

		FileReader fr = new FileReader(path);
		BufferedReader textReader = new BufferedReader(fr);
		
		int numberOfLines = readLines(path);
		String[] textData = new String[numberOfLines];
		
		for (int i = 0; i < numberOfLines; i++) {
			textData[i] = textReader.readLine();
		}
		
		fr.close();
		textReader.close();
		
		return textData;
	}
	
	/**
	 * @param path = directory of the file to be read
	 * @return numberOfLines
	 */
	int readLines(String path) throws IOException{
		FileReader file_to_read = new FileReader(path);
		BufferedReader bf = new BufferedReader(file_to_read);
		
		String line;
		int numberOfLines = 0;
		
		while((line = bf.readLine()) != null) {
			numberOfLines++;
		}
		bf.close();
		file_to_read.close();
		
		return numberOfLines;
	}
    
    private static ArrayList<Class<?>> getClassesFromJar(String jarName) {
    	ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
    	URL url = null;
    	try {
			url = new URL("file:///" + jarName);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    	
		URLClassLoader urlLoader = new URLClassLoader(new URL[] {url});
    	
    	ArrayList<String> resources = getResourceNamesFromJar(jarName);
    	for (String resource : resources) {
    		if (resource.endsWith(".class")) {
    			resource = resource.substring(0, resource.lastIndexOf('.'));
	    		Class<?> c = null;
				try {
					c = urlLoader.loadClass(resource);
					classes.add(c);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
    		}
    	}
    	
    	try {
			urlLoader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	return classes;
    }
    
    private static ArrayList<String> getResourceNamesFromJar(String jarName) {
    	ArrayList<String> classes = new ArrayList<String>();

		try{
		    JarInputStream jarFile = new JarInputStream(new FileInputStream(jarName));
		    JarEntry jarEntry;
		
		    while(true) {
		    	jarEntry = jarFile.getNextJarEntry();
		    	
		    	if(jarEntry == null){
		    		break;
		    	}
		    	
		    	if(jarEntry.getName().endsWith(".class") || jarEntry.getName().endsWith(".txt")) {
		    		classes.add(jarEntry.getName().replaceAll("/", "\\."));
		    	}
		    }
		    jarFile.close();
		} catch(Exception e){
		    e.printStackTrace ();
		}
		return classes;
    }

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Path dir = Paths.get(dirLocation.getPath());
		try {
			caller.setPlugins(readInPlugins());
			
			WatchService watcher = FileSystems.getDefault().newWatchService();
			dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			while(true){
				WatchKey key = watcher.take();
				List<WatchEvent<?>> events = key.pollEvents();
					if (!events.isEmpty()){
						try {
							System.out.println("Loaded Plugins");
							caller.setPlugins(readInPlugins());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				events.clear();
				key.reset();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
