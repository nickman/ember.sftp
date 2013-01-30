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
package org.helios.ember.auth;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.jcraft.jsch.JSch;

/**
 * <p>Title: SFTPPrivateKeyRepository</p>
 * <p>Description: A private ket aggregated repository</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.ember.auth.SFTPPrivateKeyRepository</code></p>
 */

public class SFTPPrivateKeyRepository implements ApplicationContextAware, InitializingBean {
	/** The Jsch instance that will hold all the private keys */
	protected final JSch jsch = new JSch();
	/** Directories to search for private keys in */
	protected final Set<String> pkSearchDirectories = new HashSet<String>();
	/** Located private key files */
	protected final Set<File> pkFiles = new CopyOnWriteArraySet<File>();
	
	/** The main spring app context */
	protected ApplicationContext applicationContext = null;
	/** Instance logger */
	protected final Logger log = Logger.getLogger(getClass());
	/**
	 * Creates a new SFTPPrivateKeyRepository
	 */
	public SFTPPrivateKeyRepository() {
	}
	
	/**
	 * Sets the directories which will be searched for private keys to add to the repo
	 * @param pkDirs A set of directories
	 */
	public void setPkDirectories(Set<String> pkDirs) {
		if(pkDirs!=null) {
			pkSearchDirectories.addAll(pkDirs);
		}
	}
	
	/**
	 * Returns an array of the located pk files
	 * @return an array of the located pk files
	 */
	public File[] getPkFiles() {
		return pkFiles.toArray(new File[pkFiles.size()]);
	}
	
	public JSch getJSch() {
		return jsch;
	}
	/**
	 * {@inheritDoc}
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		String sshDir = System.getProperty("user.home") + "/.ssh";
		pkSearchDirectories.add(sshDir);
		for(String dirName: pkSearchDirectories) {
			if(dirName==null || dirName.trim().isEmpty()) continue;
			File dir = new File(dirName.trim());
			if(!dir.exists()) continue;
			recurse(dir);
		}
		log.info("Added [" + pkFiles.size() + "] PK files");		
	}
	
	/**
	 * Validates that the passed file is a pk and adds it
	 * @param pkFile The file to test and add
	 */
	protected void addFile(File pkFile) {
		try {
			jsch.addIdentity(pkFile.getAbsolutePath());
			pkFiles.add(pkFile);
			if(log.isDebugEnabled()) log.debug("Added Private Key [" + pkFile + "]");			
		} catch (Exception ex) {			
		}
	}
	
	/**
	 * Recursively processes a directory, adding pk files when it finds them
	 * @param file a directory to recurse, or a file to test for being a pk file
	 */
	protected void recurse(File file) {
		if(file==null) return;
		if(file.isFile()) addFile(file); 
		else {
			for(File f: file.listFiles()) {
				recurse(f);
			}
		}
	}
	
	/**
	 * Concatenates the passed <b><code>array</code></b> and the passed <b><code>files</code></b> and returns them as one array
	 * @param array An array of files
	 * @param files An array of tiles to append
	 * @return a new array with all the files
	 */
	protected static File[] append(File[] array, File...files) {
		if(files==null || files.length<1) return array;
		File[] newArr = new File[((array==null||array.length<1) ? 0 : array.length)+files.length];
		int offset = 0;
		if(array!=null) {
			System.arraycopy(array, offset, newArr, 0, array.length);
			System.arraycopy(files, 0, newArr, array.length, files.length);			
		} else {
			System.arraycopy(files, 0, newArr, 0, files.length);
		}
		return newArr;
	}

	/**
	 * {@inheritDoc}
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

}
