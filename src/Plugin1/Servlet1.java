package Plugin1;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import plugin.Servlet;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/*
 * Servlet1.java
 * Nov 3, 2013
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

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class Servlet1 extends Servlet {
	private static int count = 0;

	/* (non-Javadoc)
	 * @see plugin.Servlet#handleRequest(protocol.HttpRequest)
	 */
	@Override
	public HttpResponse handleRequest(HttpRequest request) {
		if (request.getMethod().equalsIgnoreCase("GET")) {
			return handleGet(request);
		} else if (request.getMethod().equalsIgnoreCase("PUT")) {
			return handlePut(request);
		} else if (request.getMethod().equalsIgnoreCase("POST")) {
			return handlePost(request);
		} else if (request.getMethod().equalsIgnoreCase("DELETE")) {
			return handleDelete(request);
		}
		else return null;
	}

	/* (non-Javadoc)
	 * @see plugin.Servlet#handlesRequestType(java.lang.String)
	 */
	@Override
	public boolean handlesRequestType(String requestType) {
		if (requestType.equalsIgnoreCase("GET") || requestType.equalsIgnoreCase("PUT") ||
				requestType.equalsIgnoreCase("POST") || requestType.equalsIgnoreCase("DELETE")) {
			return true;
		}
		else return false;
	}
	
	@Override
	protected HttpResponse handleGet(HttpRequest request)
	{
		// Handling GET request here
		// Get root directory path from server
		String rootDirectory = request.getDirectoryPath();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + "\\test\\Plugin1\\name.txt");
		HttpResponse response;
		// Check if the file exists
		if(file.exists()) {
			String body = request.getBody();
			if (body.trim().equals("")) {
				body = "I don't know you.";
			} else {
				body = "Hello, " + body.trim() + "!";
			}
			
			try {
				PrintWriter out;
				out = new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
				out.println(body);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			 
			// Lets create 200 OK response
			response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
		}
		else {
			// File does not exist so lets create 404 file not found code
			response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		}
		return response;
	}
	
	@Override
	protected HttpResponse handlePut(HttpRequest request) {
		String rootDirectory = request.getDirectoryPath();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + "\\test\\Plugin1\\count.txt");
		HttpResponse response;
		// Check if the file exists
		if(file.exists()) {
			try {
				PrintWriter out;
				out = new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
				
				String body = request.getBody();
				if (!body.trim().equals("")) {
					body = body.trim();
					count += Integer.parseInt(body);
				} else {
					count++;
				}
				
				out.println(count);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			 
			// Lets create 200 OK response
			response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
		}
		else {
			// File does not exist so lets create 404 file not found code
			response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		}
		
		return response;
	}
	
	@Override
	protected HttpResponse handlePost(HttpRequest request) {
		String rootDirectory = request.getDirectoryPath();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + "\\test\\Plugin1\\count.txt");
		HttpResponse response;
		// Check if the file exists
		if(file.exists()) {
			try {
				PrintWriter out;
				out = new PrintWriter(new BufferedWriter(new FileWriter(file, false)));
				
				String body = request.getBody();
				if (!body.trim().equals("")) {
					body = body.trim();
					count = Integer.parseInt(body);
				} else {
					count = 1;
				}
				
				out.println(count);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			 
			// Lets create 200 OK response
			response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
		}
		else {
			try {
				file.createNewFile();
				PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
				
				String body = request.getBody();
				if (!body.trim().equals("")) {
					body = body.trim();
					count = Integer.parseInt(body);
				} else {
					count = 1;
				}
				
				out.println(count);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			response = HttpResponseFactory.create200OK(file, Protocol.CLOSE);
		}
		
		return response;
	}
	
	@Override
	protected HttpResponse handleDelete(HttpRequest request)
	{
		String rootDirectory = request.getDirectoryPath();
		// Combine them together to form absolute file path
		File file = new File(rootDirectory + "\\test\\Plugin1\\count.txt");
		HttpResponse response;
		// Check if the file exists
		if(file.exists()) {
			// Lets delete and create 204 no content response
			file.delete();
			response = HttpResponseFactory.create204NoContent(Protocol.CLOSE);
		}
		else {
			// File does not exist so lets create 404 file not found code
			response = HttpResponseFactory.create404NotFound(Protocol.CLOSE);
		}
		return response;
	}

}
