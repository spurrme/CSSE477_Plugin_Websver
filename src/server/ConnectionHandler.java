/*
 * ConnectionHandler.java
 * Oct 7, 2012
 *
 * Simple Web Server (SWS) for CSSE 477
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
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
 */
 
package server;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;
import protocol.ProtocolException;

/**
 * This class is responsible for handling a incoming request
 * by creating a {@link HttpRequest} object and sending the appropriate
 * response be creating a {@link HttpResponse} object. It implements
 * {@link Runnable} to be used in multi-threaded environment.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 */
public class ConnectionHandler implements Runnable {
	private Server server;
	private Socket socket;
	private long startTime;
	private InputStream inStream;
	private OutputStream outStream;
	
	public ConnectionHandler(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
	}
	
	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}


	/**
	 * The entry point for connection handler. It first parses
	 * incoming request and creates a {@link HttpRequest} object,
	 * then it creates an appropriate {@link HttpResponse} object
	 * and sends the response back to the client (web browser).
	 */
	public void run() {
		// Get the start time
		this.startTime = System.currentTimeMillis();
		
		this.inStream = null;
		this.outStream = null;
		
		try {
			this.inStream = this.socket.getInputStream();
			this.outStream = this.socket.getOutputStream();
		}
		catch(Exception e) {
			// Cannot do anything if we have exception reading input or output stream
			// May be have text to log this for further analysis?
			e.printStackTrace();
			
			// Increment number of connections by 1
			this.server.incrementConnections(1);
			// Get the end time
			long endTime = System.currentTimeMillis();
			this.server.incrementServiceTime(endTime - this.startTime);
			return;
		}
		
		// At this point we have the input and output stream of the socket
		// Now lets create a HttpRequest object
		HttpRequest request = null;
		HttpResponse response = null;
		try {
			request = HttpRequest.read(this.inStream);
		}
		catch(ProtocolException pe) {
			// We have some sort of protocol exception. Get its status code and create response
			// We know only two kind of exception is possible inside fromInputStream
			// Protocol.BAD_REQUEST_CODE and Protocol.NOT_SUPPORTED_CODE
			int status = pe.getStatus();
			if(status == Protocol.BAD_REQUEST_CODE) {
				response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
			}
			else if(status == Protocol.NOT_SUPPORTED_CODE) {
				response = HttpResponseFactory.create505NotSupported(Protocol.CLOSE);
			}
			
			this.sendResponse(response);
			return;
		}
		catch(Exception e) {
			e.printStackTrace();
			// For any other error, we will create bad request response as well
			response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
			this.sendResponse(response);
			return;
		}
		
		if(!request.getVersion().equalsIgnoreCase(Protocol.VERSION)) {
			response = HttpResponseFactory.create505NotSupported(Protocol.CLOSE);
			this.sendResponse(response);
		}
		else {
			this.delegateRequestToServlet(request);
		}
	}
	
	/**
	 * Sends an HttpResponse to the client
	 * 
	 * @param response
	 */
	private void sendResponse(HttpResponse response)
	{
		try {
			response.write(this.outStream);
			this.socket.close();
		}
		catch(Exception e){
			// We will ignore this exception
			e.printStackTrace();
			
			response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
			this.sendResponse(response);
		}

		// Increment number of connections by 1
		this.server.incrementConnections(1);
		// Get the end time
		long endTime = System.currentTimeMillis();
		this.server.incrementServiceTime(endTime - this.startTime);
	}
	
	/**
	 * Delegates a request to the appropriate servlet to handle it
	 * 
	 * @param request
	 */
	private void delegateRequestToServlet(HttpRequest request)
	{
		HttpResponse response = null;
		
		try {
			if(request.getMethod().equalsIgnoreCase(Protocol.GET)) {
				// Handling GET request here
				// Get relative URI path from request
				String uri = request.getUri();
				// Get root directory path from server
				String rootDirectory = this.server.getRootDirectory();
				// Combine them together to form absolute file path
				File file = new File(rootDirectory + uri);
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
			}
			else if(request.getMethod().equalsIgnoreCase(Protocol.POST))
			{
				// Handling POST request here
				// Get relative URI path from request
				String uri = request.getUri();
				// Get root directory path from server
				String rootDirectory = this.server.getRootDirectory();
				// Combine them together to form absolute file path
				File file = new File(rootDirectory + uri);
				
				FileWriter writer = new FileWriter(file, true);
				writer.write(request.getBody());
				writer.close();
				response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
			}
			else if(request.getMethod().equalsIgnoreCase(Protocol.PUT))
			{
				// Handling PUT request here
				// Get relative URI path from request
				String uri = request.getUri();
				// Get root directory path from server
				String rootDirectory = this.server.getRootDirectory();
				// Combine them together to form absolute file path
				File file = new File(rootDirectory + uri);
				
				FileWriter writer = new FileWriter(file, false);
				writer.write(request.getBody());
				writer.close();
				response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
			}
			else if(request.getMethod().equalsIgnoreCase(Protocol.DELETE))
			{
				// Handling DELETE request here
				// Get relative URI path from request
				String uri = request.getUri();
				// Get root directory path from server
				String rootDirectory = this.server.getRootDirectory();
				// Combine them together to form absolute file path
				File file = new File(rootDirectory + uri);
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
			}
			
			this.sendResponse(response);
		}
		catch(Exception e) {
			e.printStackTrace();
			
			// Increment number of connections by 1
			this.server.incrementConnections(1);
			// Get the end time
			long endTime = System.currentTimeMillis();
			this.server.incrementServiceTime(endTime - this.startTime);
		}
	}
	
	/**
	 * Force deletion of directory
	 * @param path
	 * @return
	 */
	static public boolean deleteDirectory(File path) {
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
