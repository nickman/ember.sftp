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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.vfs2.FileContent;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;

/**
 * <p>Title: FileObjectWrapper</p>
 * <p>Description: A JSON friendly wrapper for {@link FileObject}s.</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.ember.sftp.FileObjectWrapper</code></p>
 */

public class FileObjectWrapper {
	/** The wrapped file object */
	protected final FileObject fo;
	/** The file object content */
	protected final FileContent content;
	

	
	/**
	 * Creates a new {@link FileObjectWrapperCollection} that represents this parent folder and all its children
	 * @param parent The parent folder
	 * @param fileObjects An array of wrapped file objects representing the children
	 * @return a new {@link FileObjectWrapperCollection}
	 */
	public static FileObjectWrapperCollection wrap(FileObject parent, FileObject[] fileObjects) {
		if(fileObjects==null || fileObjects.length<1) return new FileObjectWrapperCollection(parent);
		FileObjectWrapper[] wrappers = new FileObjectWrapper[fileObjects.length];
		for(int i = 0; i < fileObjects.length; i++) {
			wrappers[i] = new FileObjectWrapper(fileObjects[i]);
		}
		return new FileObjectWrapperCollection(parent, wrappers);
	}
	
	/**
	 * Creates a new FileObjectWrapper
	 * @param fo The file object to wrap
	 */
	public FileObjectWrapper(FileObject fo) {
		this.fo = fo;
		try {
			this.content = fo.getContent();
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	/**
	 * Returns the size of the file unless it is a directory in which case it returns <code>0L</code>.
	 * @return the size of the file
	 * @throws FileSystemException thrown on any file content exception
	 */
	public long getSize() throws FileSystemException {
		if(fo.getType()==FileType.FILE) {
			return content.getSize();
		}
		return 0L;
	}

	/**
	 * Returns a UTC long representing the last modified timestamp of a file
	 * @return the last modified timestamp of a file
	 * @throws FileSystemException thrown on any file content exception
	 */
	public long getLastModifiedTime() throws FileSystemException {
		return content.getLastModifiedTime();
	}

	/**
	 * Returns a map of stringified attributes of this file
	 * @return a map of stringified attributes
	 * @throws FileSystemException thrown on any file content exception
	 */
	public Map<String, String> getAttributes() throws FileSystemException {
		Map<String, String> map = new HashMap<String, String>(content.getAttributes().size());
		for(Map.Entry<String, Object> entry: content.getAttributes().entrySet()) {
			map.put(entry.getKey(), entry.getValue().toString());
		}
		return map;
	}

	/**
	 * Returns the content type of this file.
	 * @return the file content type or a blank string if there is an exception retrieving it
	 */
	public String getContentType() {
		try {
			return content.getContentInfo().getContentType();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			return "";
		}
	}
	
	/**
	 * Returns the content encoding of this file.
	 * @return the file encoding type or a blank string if there is an exception retrieving it
	 */
	public String getContentEncoding()  {
		try {
			return content.getContentInfo().getContentEncoding();
		} catch (Exception ex) {
			return "";
		}
		
	}
	

	/**
	 * Indicates if the file is open
	 * @return true if the file is open, false otherwise
	 */
	public boolean isOpen() {
		return content.isOpen();
	}

	
	/**
	 * Returns the VFS name of this file
	 * @return the VFS name of this file
	 */
	public String getName() {
		return fo.getName().getBaseName();
	}
//	public URL getURL() throws FileSystemException {
//		return fo.getURL();
//	}
	/**
	 * Indicates if this file is hidden
	 * @return true if this file is hidden, false otherwise
	 * @throws FileSystemException thrown on a filesystem exception acquiring this attribute
	 */
	public boolean isHidden() throws FileSystemException {
		return fo.isHidden();
	}
	/**
	 * Indicates if the file is readable
	 * @return true if the file is readable, false otherwise
	 * @throws FileSystemException thrown on a filesystem exception acquiring this attribute
	 */
	public boolean isReadable() throws FileSystemException {
		return fo.isReadable();
	}
	/**
	 * Indicates if the file is writable
	 * @return true if the file is writable, false otherwise
	 * @throws FileSystemException thrown on a filesystem exception acquiring this attribute
	 */
	public boolean isWriteable() throws FileSystemException {
		return fo.isWriteable();
	}
	/**
	 * Returns the file type as defined in {@link FileType}.
	 * @return the file type
	 * @throws FileSystemException thrown on a filesystem exception acquiring this attribute
	 */
	public String getType() throws FileSystemException {
		return fo.getType().getName();
	}

}
