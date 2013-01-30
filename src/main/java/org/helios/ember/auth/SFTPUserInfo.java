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

import org.apache.log4j.Logger;

import com.jcraft.jsch.UserInfo;

/**
 * <p>Title: SFTPUserInfo</p>
 * <p>Description: Credentials provider for JSch</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.ember.sftp.SFTPUserInfo</code></p>
 */
public class SFTPUserInfo implements UserInfo {
	/** Static class logger */
	protected static final Logger log = Logger.getLogger(SFTPUserInfo.class);
	
	/** The passphrase or password */
	private final String pass;
	
	/**
	 * Creates a new SFTPUserInfo
	 * @param pass The passphrase or password
	 * @return a new SFTPUserInfo
	 */
	public static SFTPUserInfo newUserInfo(String pass) {
		return new SFTPUserInfo(pass);
	}

    /**
	 * Creates a new SFTPUserInfo
	 * @param pass The passphrase or password
	 */
	private SFTPUserInfo(String pass) {			
		this.pass = pass;
	}
	/**
	 * {@inheritDoc}
	 * @see com.jcraft.jsch.UserInfo#getPassphrase()
	 */
	@Override
	public String getPassphrase() { return pass; }
    /**
     * {@inheritDoc}
     * @see com.jcraft.jsch.UserInfo#getPassword()
     */
    @Override
	public String getPassword() { return pass; }
    /**
     * {@inheritDoc}
     * @see com.jcraft.jsch.UserInfo#promptPassphrase(java.lang.String)
     */
    @Override
	public boolean promptPassphrase(String message) { if(log.isDebugEnabled()) log.debug("JSCH (pp) says [" + message + "]"); return true; }
    /**
     * {@inheritDoc}
     * @see com.jcraft.jsch.UserInfo#promptPassword(java.lang.String)
     */
    @Override
	public boolean promptPassword(String message) { log.debug("JSCH (pw) says [" + message + "]"); return true; }
    /**
     * {@inheritDoc}
     * @see com.jcraft.jsch.UserInfo#promptYesNo(java.lang.String)
     */
    @Override
	public boolean promptYesNo(String message) { log.debug("JSCH (pw) says [" + message + "]"); return true; }
    /**
     * {@inheritDoc}
     * @see com.jcraft.jsch.UserInfo#showMessage(java.lang.String)
     */
    @Override
	public void showMessage(String message) { log.debug("JSCH (msg) says [" + message + "]");  }           
}

