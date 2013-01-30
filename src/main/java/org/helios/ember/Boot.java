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
package org.helios.ember;

import org.apache.log4j.Logger;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;

/**
 * <p>Title: Boot</p>
 * <p>Description: The server bootstrap main</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.ember.Boot</code></p>
 */

public class Boot {
	/** The main spring app context */
	public static GenericXmlApplicationContext APPCTX = null;
	/** Static class logger */
	protected static final Logger LOG = Logger.getLogger(Boot.class);
	/**
	 * Main boot
	 * @param args none
	 */
	public static void main(String[] args) {
		LOG.info("Booting Ember.sftp Server");
		try {
			APPCTX = new GenericXmlApplicationContext(new FileSystemResource("./src/main/resources/META-INF/jetty.xml"));
			LOG.info("Ember.sftp Server Up");
			Thread.currentThread().join();
		} catch (Exception ex) {
			LOG.error("Failed to boot Ember.sftp Server", ex);
			System.exit(-1);
		}
	}

}
