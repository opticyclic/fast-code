package org.fastcode.versioncontrol;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import java.util.List;
import java.util.ArrayList;
import org.eclipse.core.resources.IProject;
import org.fastcode.util.FastCodeFileForCheckin;

public class FastCodeCheckinCache {

	//Set<File>							filesToCheckInSet	= new HashSet<File>();
	//Map<File, String>					commentsForFile		= new HashMap<File, String>();
	//private final List<String>			commentKey			= new ArrayList<String>();
	Map<String, String>					commentKeyDetail	= new HashMap<String, String>();
	//Map<File, String>					fileStatusMap		= new HashMap<File, String>();
	//Map<File, String>					fileTimeMap			= new HashMap<File, String>();
	//IProject							projectToRefresh;
	 Set<FastCodeFileForCheckin>			filesToCheckIn		= new HashSet<FastCodeFileForCheckin>();
	private static FastCodeCheckinCache	fastCodeCache		= new FastCodeCheckinCache();

	private FastCodeCheckinCache() {

	}

	/**
	 *
	 * @return
	 */
	public static FastCodeCheckinCache getInstance() {
		return fastCodeCache;
	}

	/*public Set<File> getFilesToCheckInSet() {
		return this.filesToCheckInSet;
	}*/

	/*public Map<File, String> getCommentsForFile() {
		return this.commentsForFile;
	}*/

	/*public List<String> getCommentKey() {
		return this.commentKey;
	}*/

	public Map<String, String> getCommentKeyDetail() {
		return this.commentKeyDetail;
	}

	/*public Map<File, String> getFileStatusMap() {
		return this.fileStatusMap;
	}*/

	/*public Map<File, String> getFileTimeMap() {
		return this.fileTimeMap;
	}*/

	/**
	 *
	 * getter method for projectToRefresh
	 * @return
	 *
	 *//*
	public IProject getProjectToRefresh() {
		return this.projectToRefresh;
	}

	*//**
	 *
	 * setter method for projectToRefresh
	 * @param projectToRefresh
	 *
	 *//*
	public void setProjectToRefresh(final IProject projectToRefresh) {
		this.projectToRefresh = projectToRefresh;
	}*/

	public Set<FastCodeFileForCheckin> getFilesToCheckIn() {
		return this.filesToCheckIn;
	}

}
