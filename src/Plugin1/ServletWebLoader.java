/*
 * Servlet2.java
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
 
package Plugin1;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import plugin.Servlet;
import protocol.HttpRequest;
import protocol.HttpResponse;
import protocol.HttpResponseFactory;
import protocol.Protocol;

/**
 * 
 * @author Chandan R. Rupakheti (rupakhcr@clarkson.edu)
 */
public class ServletWebLoader extends Servlet {

	/* (non-Javadoc)
	 * @see plugin.Servlet#handleRequest(protocol.HttpRequest)
	 */
	@Override
	public HttpResponse handleRequest(HttpRequest request) {
			HttpResponse response = HttpResponseFactory.create400BadRequest(Protocol.CLOSE);
		if (request.getMethod().equalsIgnoreCase("LOAD")){
			response = HttpResponseFactory.create204NoContent(Protocol.CLOSE);
			if(Desktop.isDesktopSupported()){
				try {
					Desktop.getDesktop().browse(new URI(request.getBody()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
		return response;
	}

	/* (non-Javadoc)
	 * @see plugin.Servlet#handlesRequestType(java.lang.String)
	 */
	@Override
	public boolean handlesRequestType(String requestType) {
		if (requestType.equalsIgnoreCase("LOAD")) {
			return true;
		}
		else return false;
	}

}
