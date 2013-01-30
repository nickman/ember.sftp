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
package org.apache.commons.vfs2.provider.sftp;

import java.io.Serializable;

import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemConfigBuilder;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;

import com.jcraft.jsch.Session;

/**
 * <p>Title: PrincipalProvidedSessionSftpFileSystemConfigBuilder</p>
 * <p>Description: An SFTP extension of {@link FileSystemConfigBuilder} that creates filesystems using an existing JSch session.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.apache.commons.vfs2.provider.sftp.PrincipalProvidedSessionSftpFileSystemConfigBuilder</code></p>
 */

public class PrincipalProvidedSessionSftpFileSystemConfigBuilder extends FileSystemConfigBuilder {

	

    /** HTTP Proxy. */
    public static final ProxyType PROXY_HTTP = new ProxyType("http");
    /** SOCKS Proxy. */
    public static final ProxyType PROXY_SOCKS5 = new ProxyType("socks");

    private static final PrincipalProvidedSessionSftpFileSystemConfigBuilder BUILDER = new PrincipalProvidedSessionSftpFileSystemConfigBuilder();
    private static final String USER_DIR_IS_ROOT = SftpFileSystemConfigBuilder.class.getName() + ".USER_DIR_IS_ROOT";
    private static final String TIMEOUT = SftpFileSystemConfigBuilder.class.getName() + ".TIMEOUT";

    private PrincipalProvidedSessionSftpFileSystemConfigBuilder()
    {
        super("sftp.");
    }

    public static PrincipalProvidedSessionSftpFileSystemConfigBuilder getInstance()
    {
        return BUILDER;
    }

    /**
     * Proxy type.
     */
    public static final class ProxyType implements Serializable, Comparable<ProxyType>
    {
        /**
         * serialVersionUID format is YYYYMMDD for the date of the last binary change.
         */
        private static final long serialVersionUID = 20101208L;

        private final String proxyType;

        private ProxyType(final String proxyType)
        {
            this.proxyType = proxyType;
        }

        public int compareTo(ProxyType o)
        {
            return proxyType.compareTo(o.proxyType);
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            ProxyType proxyType1 = (ProxyType) o;

            if (proxyType != null ? !proxyType.equals(proxyType1.proxyType) : proxyType1.proxyType != null)
            {
                return false;
            }

            return true;
        }

        /** @since 2.0 */
        @Override
        public int hashCode()
        {
            return proxyType.hashCode();
        }
    }

 

    /**
	 * Sets the JSch {@link Session} to be used by this builder  
	 * @param opts The FileSystem options.
	 * @param session The session to use
	 * @throws FileSystemException if an error occurs.
	 */
	public void setSession(FileSystemOptions opts, Session session) throws FileSystemException {
		setParam(opts, "session", session);
	}
	
	
    

    /**
     * Retrieves the session from the passed file system options
     * @param opts The FileSystem options.
     * @return the session
     */
    public Session getSession(FileSystemOptions opts) {
    	return (Session) getParam(opts, "session");
    }

    /**
     * use user directory as root (do not change to fs root).
     *
     * @param opts The FileSystem options.
     * @param userDirIsRoot true if the user dir is the root directory.
     */
    public void setUserDirIsRoot(FileSystemOptions opts, boolean userDirIsRoot)
    {
        setParam(opts, USER_DIR_IS_ROOT, userDirIsRoot ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * @param opts The FileSystemOptions.
     * @return true if the user directory is the root.
     * @see #setUserDirIsRoot
     */
    public Boolean getUserDirIsRoot(FileSystemOptions opts)
    {
        return getBoolean(opts, USER_DIR_IS_ROOT, Boolean.TRUE);
    }

    @Override
    protected Class<? extends FileSystem> getConfigClass()
    {
        return SftpFileSystem.class;
    }

}
