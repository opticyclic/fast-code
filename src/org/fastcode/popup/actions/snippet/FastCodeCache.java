package org.fastcode.popup.actions.snippet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.graphics.Image;

public class FastCodeCache {

	/*
	 * Map<FastCodeType,IFile> classIFileMap = new
	 * HashMap<FastCodeType,IFile>(); List <IFile> ifileList = new
	 * ArrayList<IFile>();
	 */
	// Set<FastCodeType> fastCodeTypeSet = new HashSet<FastCodeType>();
	// Set<FastCodeFile> fastCodeFileSet = new HashSet<FastCodeFile>();
	Set<IType>						typeSet			= new HashSet<IType>();
	Set<IFile>						fileSet			= new HashSet<IFile>();
	Set<IPackageFragment>			packageSet		= new HashSet<IPackageFragment>();
	Set<IFolder>					folderSet		= new HashSet<IFolder>();

	/*Set<File>						filesToCheckInSet	= new HashSet<File>();
	Map<File, String>				commentsForFile		= new HashMap<File, String>();
	//private final List<String>			commentKey			= new ArrayList<String>();
	Map<String, String>				commentKeyDetail	= new HashMap<String, String>();
	Map<File, String>				fileStatusMap		= new HashMap<File, String>();
	String							projectToReresh		= EMPTY_STR;*/

	Map<String, Image>				entityImageMap	= new HashMap<String, Image>();

	private static FastCodeCache	fastCodeCache	= new FastCodeCache();

	private FastCodeCache() {

	}

	/**
	 *
	 * @return
	 */
	public static FastCodeCache getInstance() {
		return fastCodeCache;
	}

	public Set<IType> getTypeSet() {
		return this.typeSet;
	}

	public Set<IFile> getFileSet() {
		return this.fileSet;
	}

	public Set<IPackageFragment> getPackageSet() {
		return this.packageSet;
	}

	public Set<IFolder> getFolderSet() {
		return this.folderSet;
	}

	public Map<String, Image> getEntityImageMap() {
		return entityImageMap;
	}
	/*	public Set<File> getFilesToCheckInSet() {
			return this.filesToCheckInSet;
		}

		public Map<File, String> getCommentsForFile() {
			return this.commentsForFile;
		}

		public List<String> getCommentKey() {
			return this.commentKey;
		}

		public Map<String, String> getCommentKeyDetail() {
			return this.commentKeyDetail;
		}

		public Map<File, String> getFileStatusMap() {
			return this.fileStatusMap;
		}

		public String getProjectToReresh() {
			return this.projectToReresh;
		}*/

}
