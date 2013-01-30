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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;

/**
 * <p>Title: FileObjectWrapperCollection</p>
 * <p>Description: An array of {@link FileObjectWrapper}s with a summary header</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>org.helios.ember.sftp.FileObjectWrapperCollection</code></p>
 */

public class FileObjectWrapperCollection {
	/** A set of wrapped file objects that are of type {@link FileType#FILE} */
	private final Set<FileObjectWrapper> files = new HashSet<FileObjectWrapper>();
	/** A set of wrapped file objects that are of type {@link FileType#FOLDER} */
	private final Set<FileObjectWrapper> folders = new HashSet<FileObjectWrapper>();
	/** The total byte size of the folders */
	private long totalSize = 0L;
	/** The name of the folder that is parent to these files */
	private final String parentFolder;
	/** The URL prefix of the SFTP resource */
	private final String urlPrefix;
	/** Indicates if this is a root context (i.e. one cannot navigate to a higher directory)  */
	private final boolean root;
	


	/**
	 * Creates a new FileObjectWrapperCollection

	 * @param parent The parent folder
	 * @param fileObjects The wrapped file objects to aggregate
	 */
	public FileObjectWrapperCollection(FileObject parent, FileObjectWrapper...fileObjects) {
		this.parentFolder = parent.getName().getPath();
		this.root = parent.getFileSystem().getRootName().equals(parent.getName());
		this.urlPrefix = parent.getFileSystem().getRootURI();
		
		if(fileObjects!=null) {
			for(FileObjectWrapper fileObject: fileObjects) {
				if(fileObject==null) continue;
				try {
					if(FileType.FILE.getName().equals(fileObject.getType())) {
						files.add(fileObject);
						totalSize += fileObject.getSize();
					} else if(FileType.FOLDER.getName().equals(fileObject.getType())) {
						folders.add(fileObject);
					}
				} catch (Exception ex) {
					ex.printStackTrace(System.err);
				}
			}
		}
	}
	
	/**
	 * Returns an array (which may be zero-size) of the file type file objects
	 * @return an array of the file type file objects
	 */
	public FileObjectWrapper[] getFiles() {
		return files.toArray(new FileObjectWrapper[files.size()]);
	}
	
	/**
	 * Returns an array (which may be zero-size) of the folder type file objects
	 * @return an array of the folder type file objects
	 */
	public FileObjectWrapper[] getFolders() {
		return folders.toArray(new FileObjectWrapper[folders.size()]);
	}
	
	/**
	 * Returns the number of files
	 * @return the number of files
	 */
	public int getFileCount() {
		return files.size();
	}
	
	/**
	 * Returns the number of folders
	 * @return the number of folders
	 */
	public int getFolderCount() {
		return folders.size();
	}
	
	/**
	 * Returns the total size in bytes of all the files
	 * @return the total size in bytes of all the files
	 */
	public long getTotalFileSize() {
		return totalSize;
	}
	
	/**
	 * Returns the name of the common parent folder
	 * @return the name of the common parent folder
	 */
	public String getParentFolder() {
		return parentFolder;
	}
	
	public String getURLPrefix() {
		return urlPrefix;
	}
	/**
	 * Indicates if this is a root context (i.e. one cannot navigate to a higher directory) 
	 * @return true if this is a root context, false otherwise
	 */
	public boolean isRoot() {
		return root;
	}
	

}
