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

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import plugin.Servlet;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;
import protocol.ProtocolException;

/**
 * This class is responsible for handling a incoming request by creating a
 * {@link HttpRequest} object and sending the appropriate response be creating a
 * {@link HttpResponse} object. It implements {@link Runnable} to be used in
 * multi-threaded environment.
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
	 * The entry point for connection handler. It first parses incoming request
	 * and creates a {@link HttpRequest} object, then it creates an appropriate
	 * {@link HttpResponse} object and sends the response back to the client
	 * (web browser).
	 */
	public void run() {
		// Get the start time
		this.startTime = System.currentTimeMillis();

		this.inStream = null;
		this.outStream = null;

		try {
			this.inStream = this.socket.getInputStream();
			this.outStream = this.socket.getOutputStream();
		} catch (Exception e) {
			// Cannot do anything if we have exception reading input or output
			// stream
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
		} catch (ProtocolException pe) {
			// We have some sort of protocol exception. Get its status code and
			// create response
			// We know only two kind of exception is possible inside
			// fromInputStream
			// Protocol.BAD_REQUEST_CODE and Protocol.NOT_SUPPORTED_CODE
			int status = pe.getStatus();
			if (status == Protocol.BAD_REQUEST_CODE) {
				response = HttpResponseFactory
						.create400BadRequest(Protocol.CLOSE);
			} else if (status == Protocol.NOT_SUPPORTED_CODE) {
				response = HttpResponseFactory
						.create505NotSupported(Protocol.CLOSE);
			}

			this.sendResponse(response);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			// For any other error, we will create bad request response as well
			response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
			this.sendResponse(response);
			return;
		}

		if (!request.getVersion().equalsIgnoreCase(Protocol.VERSION)) {
			response = HttpResponseFactory
					.create505NotSupported(Protocol.CLOSE);
			this.sendResponse(response);
		} else {
			request.setDirectoryPath(this.server.getRootDirectory());
			sendResponse(this.delegateRequestToServlet(request));
		}
	}

	/**
	 * Sends an HttpResponse to the client
	 * 
	 * @param response
	 */
	private void sendResponse(HttpResponse response) {
		try {
			response.write(this.outStream);
			this.socket.close();
		} catch (Exception e) {
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
	 * @throws Exception
	 */
	private HttpResponse delegateRequestToServlet(HttpRequest request) {
		/*
		 * String configkey = request.getMethod() + request.getUri(); String
		 * pluginName = request.getPluginName(); HttpResponse response =
		 * HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		 * if(pluginName != null){ ArrayList<Class<?>> servlets =
		 * this.server.getPlugins().get(pluginName); for (Class<?> class1 :
		 * servlets) {
		 * if(class1.getName().equalsIgnoreCase(this.server.getMapping
		 * (configkey))){ try { Servlet servlet = (Servlet)
		 * class1.newInstance();
		 * if(servlet.handlesRequestType(request.getMethod())){ response =
		 * servlet.handleRequest(request); }else{ response =
		 * HttpResponseFactory.create501NotImplemented(Protocol.CLOSE); } }
		 * catch (InstantiationException e) { e.printStackTrace(); } catch
		 * (IllegalAccessException e) { e.printStackTrace(); } }else{ response =
		 * HttpResponseFactory.create501NotImplemented(Protocol.CLOSE); } }
		 * }else{ StaticServlet staticResourceServlet = new StaticServlet();
		 * if(staticResourceServlet.handlesRequestType(request.getMethod())){
		 * response = staticResourceServlet.handleRequest(request); } } return
		 * response;
		 */
		String uri = request.getUri();
		String method = request.getMethod();
		String pluginName = request.getPluginName();
		String servletName = this.server.getServletClassName(uri, method);
		HttpResponse response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);

		if (servletName != null) {
			HashMap<String, ArrayList<Class<?>>> plugins = this.server.getPlugins();
			if (plugins.containsKey(pluginName)) {
				ArrayList<Class<?>> servlets = plugins.get(pluginName);

				for (Class<?> servletClass : servlets) {
					if (servletClass.getName().equalsIgnoreCase(servletName)) {
						try {
							Servlet servlet = (Servlet) servletClass.newInstance();

							if (servlet.handlesRequestType(method)) {
								response = servlet.handleRequest(request);
							} else {
								response = HttpResponseFactory.create501NotImplemented(Protocol.CLOSE);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		} else {
			StaticServlet staticServlet = new StaticServlet();

			if (staticServlet.handlesRequestType(method)) {
				response = staticServlet.handleRequest(request);
			} else {
				response = HttpResponseFactory.create501NotImplemented(Protocol.CLOSE);
			}

		}
		return response;
	}
}
