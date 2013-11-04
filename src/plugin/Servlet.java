/*
 * Servlet.java
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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public abstract class Servlet{

	public abstract HttpResponse handleRequest(HttpRequest request);
	
	public abstract boolean handlesRequestType(String requestType);
	
	protected HttpResponse handleGet(HttpRequest request)
	{
		// Handling GET request here
		// Get relative URI path from request
		String uri = request.getUri();
		// Get root directory path from server
		String rootDirectory = request.getDirectoryPath();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri);
		HttpResponse response;
		// Check if the file exists
		if(file.exists()) {
			if(file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = rootDirectory + uri + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				if(file.exists()) {
					// Lets create 200 OK response
					response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
				}
				else {
					// File does not exist so lets create 404 file not found code
					response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
				}
			}
			else { // Its a file
				// Lets create 200 OK response
				response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
			}
		}
		else {
			// File does not exist so lets create 404 file not found code
			response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		}
		return response;
	}
	
	protected HttpResponse handlePut(HttpRequest request) throws IOException
	{
		// Get relative URI path from request
		String uri = request.getUri();
		// Get root directory path from server
		String rootDirectory = request.getDirectoryPath();
		String body = request.getBody();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri);
		HttpResponse response = null;
		if(file.exists()) {
			if(file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = rootDirectory + uri + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				//add to the file
				if(file.exists()) {
					 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
					 out.println(body);
					 out.close();
					 response  = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
				}
				//the file was not found
				else {
		
					 response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
				}
			}
			else { // Its a file
				// Lets create 200 OK response
				 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
				 out.println(body);
				 out.close();
				 response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
			}
		}//The file is not a file and cannot be added to
		else {
			 response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		}
		return response;
	}
	
	protected HttpResponse handlePost(HttpRequest request) throws IOException
	{
		// Get relative URI path from request
		String uri = request.getUri();
		String body = request.getBody();
		// Get root directory path from server
		String rootDirectory = request.getDirectoryPath();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri);
		HttpResponse response = null;
		if(file.exists()) {
			if(file.isDirectory()) {
				// Look for default index.html file in a directory
				String location = rootDirectory + uri + System.getProperty("file.separator") + Protocol.DEFAULT_FILE;
				file = new File(location);
				if(file.exists()) {
					 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
					 out.println(body);
					 out.close();
					 response  = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
				}
				else {
					 file.createNewFile();
					 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
					 out.println(body);
					 out.close();
					 response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
				}
			}
			else { // Its a file
				// Lets create 200 OK response
				 file.createNewFile();
				 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
				 out.println(body);
				 out.close();
				 response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
			}
		}//create all the needed directories and then create the file. Then add anything in the body to the file
		else {
			 uri = uri.substring(0, uri.indexOf(file.getName()));
			 new File(rootDirectory + uri).mkdir();
			 file.createNewFile();
			 PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
			 out.println(body);
			 out.close();
			 response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
		}
		return response;
	}
	
	protected HttpResponse handleDelete(HttpRequest request)
	{
		// Handling DELETE request here
		// Get relative URI path from request
		String uri = request.getUri();
		// Get root directory path from server
		String rootDirectory = request.getDirectoryPath();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + uri);
		HttpResponse response;
		// Check if the file exists
		if(file.exists()) {
			if(file.isDirectory()) {
				// Lets delete and create 204 no content response
				deleteDirectory(file);
				response = HttpResponseFactory.create204NoContent(Protocol.CLOSE);
			}
			else { // Its a file
				// Lets delete and create 204 no content response
				file.delete();
				response = HttpResponseFactory.create204NoContent(Protocol.CLOSE);
			}
		}
		else {
			// File does not exist so lets create 404 file not found code
			response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		}
		return response;
	}
	/**
	 * Force deletion of directory
	 * @param path
	 * @return
	 */
	private static boolean deleteDirectory(File path) {
	    if (path.exists()) {
	        File[] files = path.listFiles();
	        for (int i = 0; i < files.length; i++) {
	            if (files[i].isDirectory()) {
	                deleteDirectory(files[i]);
	            } else {
	                files[i].delete();
	            }
	        }
	    }
	    return (path.delete());
	}
}
