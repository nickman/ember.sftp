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
package org.helios.ember.sftp;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.provider.sftp.PrincipalProvidedSessionSftpFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.apache.log4j.Logger;
import org.helios.ember.auth.SFTPPrivateKeyRepository;
import org.helios.ember.auth.SFTPUserInfo;
import org.helios.ember.auth.SSHLoginService.SessionLogin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * <p>Title: SFTPSessionFactory</p>
 * <p>Description: A factory for creating SFTP sessions</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.ember.sftp.SFTPSessionFactory</code></p>
 * <p><b>CAUTION!  :</b> This app passes around SSH passwords and private key passphrases which you should NEVER do. It's just a demo until I figure out a better way to do this.<p>
 */

public class SFTPSessionFactory implements ApplicationContextAware, InitializingBean {
	/** Main app context */
	protected ApplicationContext applicationContext = null;
	/** Instance logger */
	protected final Logger log = Logger.getLogger(getClass());
	/** the SFTP URL template, defaulting to {@link #DEFAULT_SFTP_URL} */
	protected String sftpUrl = null;
	/** The pk repository */
	protected SFTPPrivateKeyRepository pkRepo = null;
	/** The SFTP file system provider */
	protected org.helios.ember.sftp.SFTPFileSystemManager fileSystemManager = null;
	
	

	public org.helios.ember.sftp.SFTPFileSystemManager getFileSystemManager() {
		return fileSystemManager;
	}

	public void setFileSystemManager(
			org.helios.ember.sftp.SFTPFileSystemManager fileSystemManager) {
		this.fileSystemManager = fileSystemManager;
	}

	/** The default SFTP URL template [user, host, port, landingdir]*/
	public static final String DEFAULT_SFTP_URL = "sftp://%s@%s:%s%s";

	
	/** Indicates if strict host key checking should be used. Defaults to true. */
	protected boolean strictHostKeyChecking = true;

	/**
	 * <p>Authenticates the passed principal and returns a <i>root</i> sftp file object in accordance with the factory configuration.</p>
	 * <p><b>CAUTION!  :</b> This app passes around SSH passwords and private key passphrases which you should NEVER do. It's just a demo until I figure out a better way to do this.<p>
	 * 
	 * @param sessionLogin The principal
	 * @param landingDirectory The optional landing directory that the returned <i>root</i> sftp file object will represent. Defaults to the principal's home directory 
	 * @return the <i>root</i> sftp file object
	 */
	public FileObject newSFTPSession(SessionLogin sessionLogin, String landingDirectory)  {
		if(sessionLogin==null) throw new IllegalArgumentException("The passed principal was null", new Throwable());
		landingDirectory = cleanLandingDirectory(landingDirectory);
		//String sessionUri = String.format(sftpUrl, sessionLogin.getName(), sessionLogin.getHost(), sessionLogin.getPort(), landingDirectory);		
		String sessionUri = String.format(sftpUrl, sessionLogin.getName(), sessionLogin.getHost(), landingDirectory);
		log.info("Building session for [" + sessionUri + "]");
	    FileSystemOptions fsOptions = new FileSystemOptions();
	    PrincipalProvidedSessionSftpFileSystemConfigBuilder builder = PrincipalProvidedSessionSftpFileSystemConfigBuilder.getInstance();
	    try {	    
		    //builder.setUserDirIsRoot(fsOptions, landingDirectory.trim().isEmpty());
	    	builder.setUserDirIsRoot(fsOptions, true);
		    builder.setRootURI(fsOptions, sessionUri);
		    builder.setSession(fsOptions, sessionLogin.getSession());		    
		    return fileSystemManager.resolveFile(sessionUri, fsOptions);
	    } catch (FileSystemException fse) {
	    	throw new RuntimeException("VFS FileSystem Exception", fse);
	    } catch (Exception ex) {
	    	throw new RuntimeException("Unexpected Exception", ex);
	    }
	}
	
	/**
	 * Cleans the passed landing directory name. If the passed value is null or empty, returns an empty string.
	 * Otherwise returns the trimmed value with a <b><code>"/"</code></b> prefixed if not already present. 
	 * @param landingDirectory The landing directory name to clean
	 * @return the cleaned landing directory name
	 */
	protected String cleanLandingDirectory(String landingDirectory) {
		if(landingDirectory==null) return "";
		landingDirectory = landingDirectory.trim();
		if(landingDirectory.isEmpty()) return "";
		if(landingDirectory.startsWith("/")) return landingDirectory;
		return "/" + landingDirectory;
	}
	
	
	/**
	 * {@inheritDoc}
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	/**
	 * {@inheritDoc}
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if(sftpUrl==null) sftpUrl = DEFAULT_SFTP_URL;
		
	}
	
	/**
	 * Returns the SFTP URL template
	 * @return the SFTP URL template
	 */
	public String getSftpUrl() {
		return sftpUrl;
	}

	/**
	 * Sets the SFTP URL template
	 * @param sftpUrl the SFTP URL template
	 */
	public void setSftpUrl(String sftpUrl) {
		this.sftpUrl = sftpUrl;
	}

	/**
	 * Sets the pk repository
	 * @param pkRepo the pk repository
	 */
	public void setPkRepo(SFTPPrivateKeyRepository pkRepo) {
		this.pkRepo = pkRepo;
	}


}
