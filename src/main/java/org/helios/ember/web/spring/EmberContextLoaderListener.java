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

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.helios.ember.Boot;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.StaticWebApplicationContext;

/**
 * <p>Title: EmberContextLoaderListener</p>
 * <p>Description: Shortcut loader for the main app ctx</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.ember.web.spring.EmberContextLoaderListener</code></p>
 */

public class EmberContextLoaderListener extends ContextLoaderListener {
	protected ContextLoader ctxLoader = null;
	protected final Logger log = Logger.getLogger(getClass());
	
	public EmberContextLoaderListener() {
		log.info("\n\t============================================\n\tCreated EmberContextLoaderListener\n\t============================================");
	}
	
	/**
	 * Create the ContextLoader to use.
	 * @return the new ContextLoader
	 * @see org.springframework.web.context.ContextLoaderListener#createContextLoader()
	 */
	@Override
	protected ContextLoader createContextLoader() {
		ctxLoader = new EmberContextLoader();
		log.info("\n\t============================================\n\tCreated EmberContextLoader\n\t============================================");
		return ctxLoader;
	}
	
	/**
	 * Return the ContextLoader used by this listener. 
	 * @return the current ContextLoader
	 * @see org.springframework.web.context.ContextLoaderListener#getContextLoader()
	 */
	@Override
	public ContextLoader getContextLoader() {
		log.info("\n\t============================================\n\tReturned EmberContextLoader\n\t============================================");
		return ctxLoader;
	}

	/**
	 * <p>Title: EmberContextLoader</p>
	 * <p>Description: Shortcut loader for the main app ctx</p> 
	 * <p>Company: Helios Development Group LLC</p>
	 * @author Whitehead (nwhitehead AT heliosdev DOT org)
	 * <p><code>org.helios.ember.web.spring.EmberContextLoaderListener.EmberContextLoader</code></p>
	 */
	public static class EmberContextLoader extends ContextLoader {
		protected ApplicationContext loadParentContext(ServletContext servletContext)   throws BeansException {
			StaticWebApplicationContext gwac = new StaticWebApplicationContext();
//			gwac.setServletContext(servletContext);
//			gwac.setParent(Boot.APPCTX);
//			gwac.refresh();
			return gwac;
		}
	}

}
