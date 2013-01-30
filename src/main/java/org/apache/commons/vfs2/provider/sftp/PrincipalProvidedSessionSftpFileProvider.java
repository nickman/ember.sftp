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

import java.security.Principal;

import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystem;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.provider.GenericFileName;

import com.jcraft.jsch.Session;

/**
 * <p>Title: PrincipalProvidedSessionSftpFileProvider</p>
 * <p>Description: An extension of {@link SftpFileProvider} which is provided by the current {@link Principal}</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.apache.commons.vfs2.provider.sftp.PrincipalProvidedSessionSftpFileProvider</code></p>
 */

public class PrincipalProvidedSessionSftpFileProvider extends SftpFileProvider {
    /**
     * Creates a {@link FileSystem}.
     */
    @Override
    protected FileSystem doCreateFileSystem(final FileName name, final FileSystemOptions fileSystemOptions) throws FileSystemException {

        // Create the file system
        final GenericFileName rootName = (GenericFileName) name;

        Session session = PrincipalProvidedSessionSftpFileSystemConfigBuilder.getInstance().getSession(fileSystemOptions);
        return new SftpFileSystem(rootName, session, fileSystemOptions);
    }
}
