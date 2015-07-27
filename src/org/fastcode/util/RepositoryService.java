package org.fastcode.util;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.fastcode.common.FastCodeEntityHolder;
import org.fastcode.common.RepositoryFolder;
import org.fastcode.exception.FastCodeRepositoryException;

public interface RepositoryService {

	void commitToRepository(final Map<Object, List<FastCodeEntityHolder>> commitMessage, boolean addToCache) throws Exception;

	void getAllMembers(final IResource[] resources, final List<IResource> resList) throws Exception;

	void getChangesInWorkspace(final IWorkspace workspace) throws Exception;

	void checkInFile(final File file, final String comment, IProject project)/*, final String repositoryURL)*/ throws FastCodeRepositoryException;

	boolean isFileInRepository(final File file) throws FastCodeRepositoryException;

	List<String> getPreviousComments(String name) throws FastCodeRepositoryException;

	List<RepositoryFolder> getSharedProjects(String url, String userId, String passwd) throws FastCodeRepositoryException;

	boolean doesFileHaveChanges(final File file) throws FastCodeRepositoryException;

	//public abstract String getRepositoryURL(final IProject prj) throws SVNException;

	//String getCommentsFromUser(final IFile file, final RepositoryService checkin) throws FastCodeRepositoryException;
}
