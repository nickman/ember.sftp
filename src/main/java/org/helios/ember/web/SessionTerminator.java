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
package org.helios.ember.web;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;
import org.helios.ember.auth.SSHLoginService.SessionLogin;

/**
 * <p>Title: SessionTerminator</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.ember.web.SessionTerminator</code></p>
 */

public class SessionTerminator implements HttpSessionListener {
	/** Instance logger */
	protected final Logger log = Logger.getLogger(getClass());

	/**
	 * Creates a new SessionTerminator
	 */
	public SessionTerminator() {
		log.info("Created Session Terminator");
	}

	/**
	 * {@inheritDoc}
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionCreated(HttpSessionEvent se) {		
		log.info("Created Session: [" + se.getSession().getId() + "]");
		
	}

	/**
	 * {@inheritDoc}
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		SessionLogin sessionLogin = (SessionLogin)se.getSession().getAttribute("auth");
		if(sessionLogin!=null) {
			log.info("Destroying active session for [" + sessionLogin.getName() + "]");
			try { sessionLogin.getSession().disconnect(); } catch (Exception ex) {}
		} else {
			log.info("Destroying inactive session"); 
		}
		
	}


}
