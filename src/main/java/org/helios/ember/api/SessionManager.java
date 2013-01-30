/**
 * Helios, OpenSource Monitoring
 * Brought to you by the Helios Development Group
 *
 * Copyright 2007, Helios Development Group and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org. 
 *
 */
package org.helios.ember.api;

import java.security.Principal;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;
import org.helios.ember.auth.SSHLoginService.SessionLogin;



/**
 * <p>Title: SessionManager</p>
 * <p>Description: Provides session services through a json api</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.ember.api.SessionManager</code></p>
 */
@Path("/session")
public class SessionManager {
	/** Instance logger */
	protected final Logger log = Logger.getLogger(getClass());
	/** The injected security context */
	@Context
	protected SecurityContext securityContext = null;

	@GET
	@Produces({"text/plain"})
	@Path("/id")
	public String getSessionId(@Context HttpServletRequest request) {
		SessionLogin sessionLogin = (SessionLogin)securityContext.getUserPrincipal();
		log.info("Principal [" + sessionLogin.getName() + "] class:" + sessionLogin.getClass().getName());
		// request.isUserInRole("GSX")
		HttpSession session = request.getSession();
		if(session.isNew()) {
			session.setAttribute("session", sessionLogin);
		}
		StringBuilder b = new StringBuilder("Session:").append(request.getSession().getId());
		b.append("\n\tSession Attributes:");
		for(Enumeration<String> attrNames = session.getAttributeNames(); attrNames.hasMoreElements();) {
			String key = attrNames.nextElement();
			b.append("\n\t\t").append(key).append(":").append(session.getAttribute(key));
		}
		b.append("\n\tContext Attributes:");
		for(Enumeration<String> attrNames = session.getServletContext().getAttributeNames(); attrNames.hasMoreElements();) {
			String key = attrNames.nextElement();
			b.append("\n\t\t").append(key).append(":").append(session.getServletContext().getAttribute(key));
		}		
		return b.toString();
	}
	@GET
	@Produces({"text/plain"})
	@Path("/end")
	public String termSession(@Context HttpServletRequest request) {
		request.getSession().invalidate();
		return  "OK";
	}

}
