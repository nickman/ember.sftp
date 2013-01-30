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

import java.util.Stack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.log4j.Logger;
import org.helios.ember.auth.SSHLoginService.SessionLogin;
import org.helios.ember.sftp.FileObjectWrapper;
import org.helios.ember.sftp.FileObjectWrapperCollection;
import org.helios.ember.sftp.SFTPSessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.sun.jersey.api.uri.UriComponent.Type;
import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;

/**
 * <p>Title: SFTPService</p>
 * <p>Description: Provides an SFTP service API</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.ember.api.SFTPService</code></p>
 */
@Path("/sftp")
public class SFTPService implements ApplicationContextAware, InitializingBean {
	/** Instance logger */
	protected final Logger log = Logger.getLogger(getClass());
	/** The injected security context */
	@Context
	protected SecurityContext securityContext = null;
	/** The SFTP FileSystem provider */
	@Autowired(required=true)
	protected org.helios.ember.sftp.SFTPFileSystemManager fileSystemManager = null;
	/** The SFTP session factory */	
	protected SFTPSessionFactory sessionFactory = null;
	/** The spring app context */
	protected ApplicationContext applicationContext;
	
	/**
	 * Creates a new SFTPService
	 */
	public SFTPService() {
		log.info("Created SFTPService");
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}


	/**
	 * Sets the SFTP session factory
	 * @param sessionFactory the SFTP session factory
	 */
	@Autowired(required=true)
	public void setSessionFactory(SFTPSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	/**
	 * Returns a file listing for the current SFTP context. If there is no current context, will list the root.
	 * @param request The Http request
	 * @param path The path to list
	 * @return A direcotry listing
	 */
	@GET
	@Produces({"application/json"})
	@Path("/list/{path}")
	public FileObjectWrapperCollection listPath(@Context HttpServletRequest request, @PathParam("path") String path) {
		if(path==null || path.trim().isEmpty()) {
			path = ".";
		}
		HttpSession session = request.getSession();
		SessionLogin sessionLogin = (SessionLogin)securityContext.getUserPrincipal();
		
		try {
			FileObject fo = getFileObject(sessionLogin, path);
			
			return FileObjectWrapper.wrap(fo, fo.getChildren());
		} catch (FileSystemException e) {
			log.error("Failed to list into [" + path + "]", e);
			throw new WebApplicationException(e, new ResponseBuilderImpl().status(Status.INTERNAL_SERVER_ERROR).entity(new GenericEntity<String>("{err:'Failed to list into " + path + "'}", String.class)).build());
		}
	}	
	
	/**
	 * Returns a file listing for the current SFTP context. If there is no current context, will list the root.
	 * @param request The Http request
	 * @return A direcotry listing
	 */
	@GET
	@Produces({"application/json"})
	@Path("/list")
	public FileObjectWrapperCollection listDefaultPath(@Context HttpServletRequest request) {
		return listPath(request, ".");
	}
	
	protected FileObject getFileObject(SessionLogin sessionLogin, String path) {
		return sessionFactory.newSFTPSession(sessionLogin, path);
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		StringBuilder b = new StringBuilder();
		for(String bean: applicationContext.getBeanDefinitionNames()) {
			b.append("\n\t\t").append(bean);
		}
		log.info("\n\t===================================\n\tStarted SFTPService\n\tAppCtx:" + applicationContext + "\n\tSessionFactory:" + sessionFactory + b.toString() + "\n\t===================================\n");
		
	}
	
	
}
