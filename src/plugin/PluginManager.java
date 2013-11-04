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

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashMap;
import server.Server;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class PluginManager extends ClassLoader implements Runnable{
	private File dirLocation;
	private Server caller;

	public PluginManager(String defaultLocation, Server caller){
		this.dirLocation = new File(defaultLocation);
		this.caller = caller;
	}
	
	public HashMap<String, ArrayList<Class<?>>> readInPlugins() throws Exception{
		HashMap<String, ArrayList<Class<?>>> plugins = new HashMap<String, ArrayList<Class<?>>>();
		FilenameFilter folderFilter = new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				if (dir.isDirectory() == true){
					return true;
				}
				return false;
			}
		};
		File[] listOfPluginFolders = dirLocation.listFiles(folderFilter);
		
		FilenameFilter servletClassFilter = new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				int j = name.lastIndexOf('.');
				String extension = "";
				if (j > 0) {
				   extension = name.substring(j+1);
				}
				if(extension == "class"){
					return true;
				}
				return false;
			}
		};
		for (int i = 0; i < listOfPluginFolders.length; i++) {
			File currentPlugin = listOfPluginFolders[i];
			File[] listOfServlets = currentPlugin.listFiles(servletClassFilter);
			for (int j = 0; j < listOfServlets.length; j++) {
				File currentServlet = listOfServlets[j];
				Class<?> servletClass;
				try {
					servletClass = loadClass(currentServlet.getName());
					if (servletClass.getSuperclass().equals(Servlet.class)){
						String plugin = currentPlugin.getName();
						
						if (!plugins.containsKey(plugin)){
							plugins.put(plugin, new ArrayList<Class<?>>());
						}
						plugins.get(plugin).add(servletClass);
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return plugins;
	}
	
	public Class<?> loadClass (String name) throws ClassNotFoundException { 
        return loadClass(name, true); 
      }
    public Class<?> loadClass (String classname, boolean resolve) throws ClassNotFoundException {
        try {
          Class<?> c = findLoadedClass(classname);
          if (c == null) {
            try { c = findSystemClass(classname); }
            catch (Exception ex) {}
          }
          if (c == null) {
            String filename = classname.replace('.',File.separatorChar)+".class";
            File f = new File(dirLocation, filename);
            int length = (int) f.length();
            byte[] classbytes = new byte[length];
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            in.readFully(classbytes);
            in.close();
            c = defineClass(classname, classbytes, 0, length);
          }
          if (resolve) resolveClass(c);
          return c;
        }
        catch (Exception ex) { throw new ClassNotFoundException(ex.toString()); }
      }

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Path dir = Paths.get(dirLocation.getPath());
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			while(true){
				WatchKey event = watcher.poll();
				if (event.isValid()){
					try {
						caller.setPlugins(readInPlugins());
						event.cancel();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
