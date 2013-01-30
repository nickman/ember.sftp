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
package org.helios.ember.web.spring;

import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.eclipse.jetty.util.Attributes;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * <p>Title: ServletContextAttributes</p>
 * <p>Description: </p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.ember.web.spring.ServletContextAttributes</code></p>
 */

public class ServletContextAttributes implements ApplicationContextAware, Attributes {
	protected ApplicationContext applicationContext;
	protected final Map<String, Object> attrs = new ConcurrentHashMap<String, Object>();
	protected final Logger log = Logger.getLogger(getClass());
	/**
	 * Creates a new ServletContextAttributes
	 */
	public ServletContextAttributes() {
		log.info("Created ServletContextAttributes");
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext; 
		GenericWebApplicationContext gwa = new GenericWebApplicationContext() ;
		gwa.setParent(applicationContext);
		attrs.put(WebApplicationContext.class.getName() + ".ROOT", gwa);

	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jetty.util.Attributes#removeAttribute(java.lang.String)
	 */
	@Override
	public void removeAttribute(String name) {
		attrs.remove(name);
		
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jetty.util.Attributes#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(String name, Object attribute) {
		attrs.put(name, attribute);
		
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jetty.util.Attributes#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String name) {
		return attrs.get(name);
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jetty.util.Attributes#getAttributeNames()
	 */
	@Override
	public Enumeration<String> getAttributeNames() {
		Vector<String> v = new Vector<String>(attrs.keySet());
		return v.elements();
	}

	/**
	 * {@inheritDoc}
	 * @see org.eclipse.jetty.util.Attributes#clearAttributes()
	 */
	@Override
	public void clearAttributes() {
		attrs.clear();		
	}

}
