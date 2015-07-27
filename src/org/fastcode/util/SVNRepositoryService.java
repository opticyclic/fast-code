package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.COMMA;
import static org.fastcode.common.FastCodeConstants.CREATE_CLASS;
import static org.fastcode.common.FastCodeConstants.CREATE_FIELDS;
import static org.fastcode.common.FastCodeConstants.CREATE_FILE;
import static org.fastcode.common.FastCodeConstants.CREATE_FOLDER;
import static org.fastcode.common.FastCodeConstants.CREATE_INNER_CLASS;
import static org.fastcode.common.FastCodeConstants.CREATE_LOCALVARIABLE;
import static org.fastcode.common.FastCodeConstants.CREATE_METHODS;
import static org.fastcode.common.FastCodeConstants.CREATE_PACKAGE;
import static org.fastcode.common.FastCodeConstants.CREATE_STUBMETHOD;
import static org.fastcode.common.FastCodeConstants.CREATE_TESTMETHOD;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FILE_SEPARATOR;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.MODIFY_FIELD;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_CLASS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_FIELDS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_FILE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_FOLDER;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_INNERCLASSES;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_LOCALVARS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_METHODS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_MODIFY_FIELD;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_PACKAGE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_STUBMETHODS;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_TESTMETHODS;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.util.SourceUtil.checkForErrors;
import static org.fastcode.util.SourceUtil.loadComments;
import static org.fastcode.util.SourceUtil.refreshProject;
import static org.fastcode.util.StringUtil.evaluateByVelocity;
import static org.fastcode.util.StringUtil.getGlobalSettings;
import static org.fastcode.util.StringUtil.isEmpty;
import static org.fastcode.util.VersionControlUtil.addFileToCache;
import static org.fastcode.util.VersionControlUtil.checkInNow;
import static org.fastcode.util.VersionControlUtil.getPrjFromFile;
import static org.fastcode.util.VersionControlUtil.isPrjConfigured;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.fastcode.common.FastCodeCheckinCommentsData;
import org.fastcode.common.FastCodeEntityHolder;
import org.fastcode.common.RepositoryFolder;
import org.fastcode.exception.FastCodeRepositoryException;
import org.fastcode.preferences.VersionControlPreferences;
import org.fastcode.util.VersionControlUtil.UserQuestion;
import org.fastcode.versioncontrol.FastCodeCheckinCache;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNRepositoryService implements RepositoryService { //extends AbstractRepositoryService
	private SVNClientManager				ourClientManager;
	private ISVNEventHandler				myCommitEventHandler;
	//private SVNStatus					info;
	private SVNLogEntry						historyInfo;
	private SVNURL							repositoryURL	= null;
	private SVNRepository					repository		= null;
	private LogEntryPreviousRevFinder		myLogEntryHandler;
	SVNLogClient							svnLogClient;
	private final ISVNAuthenticationManager	authManager		= null;

	/*static {
		preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);

		try {
			repositoryURL = SVNURL.parseURIEncoded(preferenceStore.getString(P_REPOSITORY_URL));
		} catch (final SVNException e) {
			//
		}
		final ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		final ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager();
		ourClientManager = SVNClientManager.newInstance(options, authManager);
		myCommitEventHandler = new CommitEventHandler();
		ourClientManager.getCommitClient().setEventHandler(myCommitEventHandler);

	}*/

	public void setRepositoryDetailsForFile(final String prj) throws FastCodeRepositoryException {
		final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();
		final String urlFromPref = isPrjConfigured(prj);
		if (isEmpty(urlFromPref)) {
			throw new FastCodeRepositoryException("Please configure repository URL for project " + prj
					+ " in the version Control preference Page.");
		}
		try {
			this.repositoryURL = SVNURL.parseURIEncoded(urlFromPref);

			final ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
			final ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
					versionControlPreferences.getUserId(), versionControlPreferences.getPassword());
			this.ourClientManager = SVNClientManager.newInstance(options, authManager);
			this.myCommitEventHandler = new CommitEventHandler();
			this.ourClientManager.getCommitClient().setEventHandler(this.myCommitEventHandler);
			try {
				this.repository = SVNRepositoryFactory.create(this.repositoryURL);
			} catch (final SVNException ex) {
				throw new FastCodeRepositoryException(ex);
			}
			this.repository.setAuthenticationManager(authManager);
		} catch (final SVNException e) {
			throw new FastCodeRepositoryException(e.getMessage(), e.getCause());
		}
	}

	@Override
	public List<RepositoryFolder> getSharedProjects(final String url, final String userId, final String passwd) throws FastCodeRepositoryException {
		final List<String> projects = new ArrayList<String>();
		SVNRepository svnRepository;
		final List<RepositoryFolder> repFldrList = new ArrayList<RepositoryFolder>();
		try {
			svnRepository = connectToRepository(url, userId, passwd);
			final SVNNodeKind nodeKind = svnRepository.checkPath("", -1);
			if (nodeKind == SVNNodeKind.NONE) {
				System.err.println("There is no entry at '" + url + "'.");
				System.exit(1);
			} else if (nodeKind == SVNNodeKind.FILE) {
				System.err.println("The entry at '" + url + "' is a file while a directory was expected.");
				System.exit(1);
			}

			/*final String path = "";
			final Collection entries = svnRepository.getDir(path, -1, null, (Collection) null);
			//final Collection entries = this.repository.getLocations(path, (Collection) null, -1, 0);
			final Iterator iterator = entries.iterator();
			while (iterator.hasNext()) {
				final SVNDirEntry entry = (SVNDirEntry) iterator.next();
				projects.add(entry.getName());
				System.out.println("/" + (path.equals("") ? "" : path + "/") + entry.getName() + " ( author: '" + entry.getAuthor()
						+ "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")");
				if (entry.getKind() == SVNNodeKind.DIR) {
					//listEntries(repository, (path.equals("")) ? entry.getName() : path + "/" + entry.getName());
				}
			}*/

			listEntries(svnRepository, "", null, repFldrList, url);
		} catch (final SVNException ex) {
			throw new FastCodeRepositoryException(ex.getMessage(), ex.getCause());
		}
		return repFldrList;
	}

	public static void listEntries(final SVNRepository repository, final String path, final RepositoryFolder folder, final List<RepositoryFolder> repFldrList, final String url) throws SVNException {
		final Collection entries = repository.getDir(path, -1, null, (Collection) null);
		final Iterator iterator = entries.iterator();
		while (iterator.hasNext()) {
			final SVNDirEntry entry = (SVNDirEntry) iterator.next();
			String pathType = EMPTY_STR;
			if (entry.getKind() == SVNNodeKind.DIR ) {
				pathType = "DIR";
			} else if (entry.getKind() == SVNNodeKind.FILE) {
				pathType = "FILE";
			}
			final RepositoryFolder mainFolder;
			if (entry.getKind() == SVNNodeKind.DIR) {
				final String fullPath = url + FORWARD_SLASH + path;
			if (path.equals(EMPTY_STR)) {
				mainFolder = new RepositoryFolder.Builder().withName(entry.getName()).withAuthor(entry.getAuthor())
					.withRevision(entry.getRevision()).withRevDate(entry.getDate()).withType(pathType).withPath(fullPath).withAtRoot(true).build();
			 repFldrList.add(mainFolder);
			} else {
				mainFolder = new RepositoryFolder.Builder().withName(entry.getName()).withAuthor(entry.getAuthor())
						.withRevision(entry.getRevision()).withRevDate(entry.getDate()).withType(pathType).withPath(fullPath).withAtRoot(false).build();
				folder.getSubFolder().add(mainFolder);
			}

			/*System.out.println("/" + (path.equals("") ? "" : path + "/") + entry.getName() + " (author: '" + entry.getAuthor()
					+ "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")");*/
			/*
			 * Checking up if the entry is a directory.
			 */

				/*System.out.println("/" + (path.equals("") ? "" : path + "/") + entry.getName() + " (author: '" + entry.getAuthor()
						+ "'; revision: " + entry.getRevision() + "; date: " + entry.getDate() + ")");*/
				listEntries(repository, path.equals("") ? entry.getName() : path + "/" + entry.getName(), mainFolder, repFldrList, url);
			} else {

			}
		}
	}

	/**
	 * @param url
	 * @param userId
	 * @param passwd
	 * @return
	 * @throws SVNException
	 */
	public static SVNRepository connectToRepository(final String url, final String userId, final String passwd)
			throws FastCodeRepositoryException {
		SVNRepository svnRepository = null;
		try {
			svnRepository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));

			final ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(userId, passwd);
			svnRepository.setAuthenticationManager(authManager);
			final SVNNodeKind nodeKind = svnRepository.checkPath("", -1);
			if (nodeKind == SVNNodeKind.NONE) {
				throw new FastCodeRepositoryException("There is no path " + url);
			}
			final String path = "";
			final Collection entries = svnRepository.getDir(path, -1, null, (Collection) null);
		} catch (final SVNException ex) {
			throw new FastCodeRepositoryException(ex);
		}

		return svnRepository;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.util.RepositoryService#checkInFile(java.io.File, java.lang.String, boolean, org.eclipse.core.resources.IProject)
	 */
	@Override
	public void checkInFile(final File file, String comment, final IProject prj)/*, final String url)*/
	throws FastCodeRepositoryException {

		final boolean keepLocks = false;
		final File[] paths = { file };
		final boolean force = false;
		//final boolean recursive = false;
		final boolean keepChngList = false;
		final boolean mkdir = false;
		final boolean climbUnversionedParents = true;
		final boolean includeIgnored = false;
		final boolean makeParents = false;
		boolean addPrefixFooter = false;
		final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
		final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();
		String prefix = EMPTY_STR;
		String footer = EMPTY_STR;
		final Map<String, Object> placeHolder = new HashMap<String, Object>();
		setRepositoryDetailsForFile(prj.getName());
		try {
			getGlobalSettings(placeHolder);
			prefix = evaluateByVelocity(versionControlPreferences.getComntPrefix(), placeHolder);
			footer = evaluateByVelocity(versionControlPreferences.getComntFooter(), placeHolder);
			if (isEmpty(comment)) {
				 final FastCodeCheckinCommentsData commentsFromUser = getCommentsFromUser(file, prj.getName());
				if (isEmpty(commentsFromUser.getFinalComment())) {
					return;
				} else {
					comment = commentsFromUser.getFinalComment();
					addPrefixFooter = commentsFromUser.isAddPrefixFooter();
				}
			}
			final String projectName = prj.getName();
			if (file.getAbsolutePath().contains(projectName)) {
				final String dir = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(projectName));
				final StringTokenizer dirToken = new StringTokenizer(dir, FILE_SEPARATOR);
				final int tokenCount = dirToken.countTokens();
				int counter = 1;
				while (dirToken.hasMoreElements()) {
					final String dirTok = (String) dirToken.nextElement();
					final String fileAbsPath = file.getAbsolutePath();
					/*final String path = counter <= tokenCount ? file.getAbsolutePath().substring(0, file.getAbsolutePath().indexOf(dirTok)) : file
							.getAbsolutePath();*/
					if (isEmpty(fileAbsPath)) {
						throw new FastCodeRepositoryException("File path not found for file:" + file.getName());
					}
					final String path = fileAbsPath.substring(0, fileAbsPath.indexOf(dirTok) + dirTok.length());
					System.out.println("Toker-->" + dirTok);
					System.out.println("PATH ==>" + path);
					System.out.println(prj.getLocation().toString());
					if (isEmpty(path)) {
						continue;
					}
					if (path.startsWith(prj.getLocation().toFile().getAbsolutePath())) {
						final File subFile = new File(path);

						final File[] subFilePath = { subFile };
						final SVNStatus info = this.ourClientManager.getStatusClient().doStatus(subFile, true);
						/*if (info == null) {
							System.out.println(subFile.getAbsolutePath());
							System.out.println(this.repositoryURL.toString());
							throw new FastCodeRepositoryException("File status not available for file " + file.getName());
						}*/
						String finalComment = comment.trim();
						if (addPrefixFooter) {
							finalComment = prefix + NEWLINE + comment.trim() + NEWLINE + footer;
						}

						if (info == null || info.getContentsStatus() == SVNStatusType.STATUS_UNVERSIONED) {
							this.ourClientManager.getWCClient().doAdd(subFile, force, mkdir, climbUnversionedParents, SVNDepth.EMPTY,
									includeIgnored, makeParents);
							final SVNCommitInfo commitInfo = this.ourClientManager.getCommitClient().doCommit(subFilePath, keepLocks,
									finalComment, null, null, keepChngList, force, SVNDepth.EMPTY); //preferenceStore.getString(P_COMMENTS_TEXT)
							if(commitInfo.getErrorMessage() != null && !isEmpty(commitInfo.getErrorMessage().getFullMessage())) {
								throw new FastCodeRepositoryException(commitInfo.getErrorMessage().getMessage(), commitInfo.getErrorMessage().getCause());
							}
							final boolean allowUnversionedObstructions = false;
							final boolean depthIsSticky = false;
							this.ourClientManager.getUpdateClient().doUpdate(subFile, SVNRevision.create(commitInfo.getNewRevision()), SVNDepth.EMPTY, allowUnversionedObstructions, depthIsSticky);
						} else {
							if (!subFile.isDirectory()) {
								/*long prevRevision = -1;

								this.myLogEntryHandler = new LogEntryPreviousRevFinder(FORWARD_SLASH + info.getRepositoryRelativePath(), -1);
								System.out.println(info.isConflicted());

								this.repository.log(new String[] { "" }, -1, 0l, true, false, 0, this.myLogEntryHandler);
								System.out.println("myLogEntryHandle-" + this.myLogEntryHandler.isSuccess());
								if (this.myLogEntryHandler.isSuccess()) {
									final String prevPath = this.myLogEntryHandler.getPreviousPath();
									prevRevision = this.myLogEntryHandler.getPreviousRevision();
								}
								System.out.println("committed revision number-" + info.getCommittedRevision().getNumber());
								System.out.println("previous revision number-" + prevRevision);*/

								//System.out.println(this.myLogEntryHandler.);
								//if (prevRevision == -1) {
									/*if (info.getCopyFromURL() != null) {
										final boolean move = true; //means copy false
										System.out.println(info.getCopyFromURL());
										System.out.println(info.getURL());
										 final SVNCopySource copySource = new SVNCopySource(SVNRevision.UNDEFINED, SVNRevision.HEAD, SVNURL.parseURIEncoded(info.getCopyFromURL()));
										 final SVNCommitInfo info1 = this.ourClientManager.getCopyClient().doCopy(new SVNCopySource[] { copySource }, info.getURL(), move, false, true,
												 prefix + NEWLINE + comment + NEWLINE + footer, null);
										 System.out.println(info1.getNewRevision());
										 prevRevision = info1.getNewRevision();
											final boolean allowUnversionedObstructions = false;
											final boolean depthIsSticky = false;
											this.ourClientManager.getUpdateClient().doUpdate(subFile, SVNRevision.HEAD, SVNDepth.EMPTY, allowUnversionedObstructions, depthIsSticky);
									} else {*/
									/*throw new FastCodeRepositoryException("File " + info.getRepositoryRelativePath()
											+ " is not found in the repository at " + this.repositoryURL);*/
									//}
								//} else {
									if (!info.isConflicted()) { //info.getCommittedRevision().getNumber() == prevRevision) {
										final SVNCommitInfo commitInfo = this.ourClientManager.getCommitClient().doCommit(paths, keepLocks,
												finalComment, null, null, keepChngList, force, SVNDepth.EMPTY);
										if(commitInfo.getErrorMessage() != null && !isEmpty(commitInfo.getErrorMessage().getFullMessage())) {
											throw new FastCodeRepositoryException(commitInfo.getErrorMessage().getMessage(), commitInfo.getErrorMessage().getCause());
										}
										final boolean allowUnversionedObstructions = false;
										final boolean depthIsSticky = false;
										this.ourClientManager.getUpdateClient().doUpdate(subFile, SVNRevision.create(commitInfo.getNewRevision())/*SVNRevision.HEAD*/, SVNDepth.EMPTY, allowUnversionedObstructions, depthIsSticky);
										//to do -- removed file from cache after check in
										/*if (checkinCache.getFilesToCheckIn().contains(file)) {
											checkinCache.getFilesToCheckInSet().remove(file);
											//checkinCache.getCommentsForFile().remove(file);
											throw new FastCodeRepositoryException("File " + file.getName()
													+ " has been committed and removed from cache.");

										}*/
									} else {
										throw new FastCodeRepositoryException("File " + info.getRepositoryRelativePath() + " is out of date.");
									}
								//}
							}
						}
					}
					counter++;
				}
			}
		} catch (final SVNException ex) {
			throw new FastCodeRepositoryException(ex.getMessage(), ex.getCause());
		} catch (final Exception ex) {
			throw new FastCodeRepositoryException(ex.getMessage(), ex.getCause());
		}

		//if the file checked in is present in the cache ...then following for loop removes it from cache -- need to revisit this logic -- 20 Aug '13
		final List<FastCodeFileForCheckin> filesCheckedIn = new ArrayList<FastCodeFileForCheckin>();
		for (final FastCodeFileForCheckin fastCodeFileForCheckin : checkinCache.getFilesToCheckIn()) {
			if (fastCodeFileForCheckin.getFileFullName().equals(file.getAbsolutePath())) {
				filesCheckedIn.add(fastCodeFileForCheckin);
			}
		}

		if (filesCheckedIn != null && !filesCheckedIn.isEmpty()) {
			for (final FastCodeFileForCheckin checkedIn : filesCheckedIn) {
				checkinCache.getFilesToCheckIn().remove(checkedIn);
			}
		}
	}

	/**
	 * @param file
	 * @param prjName
	 * @param defaultValue
	 * @return
	 * @throws FastCodeRepositoryException
	 */
	public FastCodeCheckinCommentsData getCommentsFromUser(final File file, final String prjName) throws FastCodeRepositoryException {

		FastCodeCheckinCommentsData comboData;
		final VersionControlUtil versionControlUtil = new VersionControlUtil();
		final UserQuestion userQuestion = versionControlUtil.new UserQuestion(file, prjName);
		final FutureTask<FastCodeCheckinCommentsData> futureUserAnswer = new FutureTask<FastCodeCheckinCommentsData>(userQuestion);
		Display.getDefault().syncExec(futureUserAnswer);
		try {
			comboData = futureUserAnswer.get();
		} catch (final InterruptedException ex) {
			throw new FastCodeRepositoryException(ex.getMessage(), ex.getCause());
		} catch (final ExecutionException ex) {
			throw new FastCodeRepositoryException(ex.getMessage(), ex.getCause());
		}

		return comboData;
	}

	/**
	 * @param prj
	 * @return
	 * @throws SVNException
	 */
	/*public String getRepositoryURL(final IProject prj) throws SVNException {
		String location = EMPTY_STR;
		final String prjRepo = EMPTY_STR;
		try {
			for (final Entry<QualifiedName, String> entry : prj.getPersistentProperties().entrySet()) {
				if (entry.getKey().getLocalName().equalsIgnoreCase("location")) {
					final String loc[] = entry.getValue().split(SEMICOLON);
					location = loc[1];
				}
				System.out.println(entry.getKey().getLocalName());
				System.out.println(entry.getKey().getQualifier());
				System.out.println(entry.getValue()); //-- when entry.getKey().getLocalName() == location, split entry.getValue() by ; and take [1]
			}
			for (final Entry<QualifiedName, Object> props : prj.getSessionProperties().entrySet()) {
				final Object obj = props.getValue();
				final QualifiedName name = props.getKey();
				System.out.println(name.getLocalName());
				System.out.println(name.getQualifier());
				if (props.getKey().getLocalName().equalsIgnoreCase("Repository")) {
					System.out.println(obj.getClass());
					final SVNTeamProvider provider = (SVNTeamProvider) props.getValue();
					location = provider.getRepositoryLocation().getName();
					prjRepo = provider.getRepositoryResource().getName();
					//prjRepo = props.getValue().toString().substring(((String) props.getValue()).indexOf(COLON));
					//props.getValue().
				}

				System.out.println("local -->" + props.getKey().getLocalName());
				System.out.println("qualifer -->" + props.getKey().getQualifier());
				System.out.println(props.getValue()); //when props.getKey().getLocalName() = repository, split props.getValue() by : and take [0]
			}

		} catch (final Exception ex1) {
			// TODO Auto-generated catch block
			ex1.printStackTrace();
		}

		if (!isEmpty(location) && !isEmpty(prjRepo)) {
			//if(!repositoryURL.equals(location + FORWARD_SLASH + prjRepo)) {
			//MessageDialog.openError(new Shell(), "URL mismatch", "The SVN URL specified in the prefernce does not match that of the project");
			return location + FORWARD_SLASH + prjRepo;
			//}
		} else {
			MessageDialog.openError(new Shell(), "Project not Shared", "Looks like the project is not connected to Svn Repository.");
			return EMPTY_STR;
		}
	}*/


	/* (non-Javadoc)
	 * @see org.fastcode.util.RepositoryService#getChangesInWorkspace(org.eclipse.core.resources.IWorkspace)
	 */
	@Override
	public void getChangesInWorkspace(final IWorkspace workspace) throws Exception {
		final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
		final IProject projects[] = workspace.getRoot().getProjects();
		for (final IProject prj : projects) {
			System.out.println(prj.getLocationURI());
			SVNStatus info;
			try {
				info = this.ourClientManager.getStatusClient().doStatus(new File(prj.getLocationURI()), true);
			} catch (final SVNException svnExp) {
				continue;
			}
			if (info.isVersioned()) {
				final List<IResource> resList = new ArrayList<IResource>();
				getAllMembers(prj.members(), resList);
				for (final IResource res : resList.toArray(new IResource[0])) {
					try {
						info = this.ourClientManager.getStatusClient().doStatus(new File(res.getLocationURI()), true);
					} catch (final SVNException svnEx) {
						continue;
					}
					System.out.println(new File(res.getLocationURI()).getName());
					//if (new File(res.getLocationURI()).getName().equalsIgnoreCase("Song.java") || new File(res.getLocationURI()).getName().equalsIgnoreCase("Student.java")) {
					//System.out.println(new File(res.getLocationURI()));
					if (info.getContentsStatus() == SVNStatusType.STATUS_UNVERSIONED
							|| info.getContentsStatus() == SVNStatusType.STATUS_MODIFIED) {
						System.out.println(new File(res.getLocationURI()));
						//checkinCache.getFilesToCheckInSet().add(new File(res.getLocationURI()));
					}
					//}
				}
			}
		}
		/*if (checkinCache.getFilesToCheckInSet().size() > 0) {
			MessageDialog.openInformation(new Shell(), "Changes added to cache",
					"Some changes have been added to Cache. Please use \"Commit To Reository\" menu to commit the changes.");
		}*/

	}

	/* (non-Javadoc)
	 * @see org.fastcode.util.RepositoryService#getAllMembers(org.eclipse.core.resources.IResource[], java.util.List)
	 */
	@Override
	public void getAllMembers(final IResource[] resources, final List<IResource> resList) throws Exception {
		for (final IResource res : resources) {
			resList.add(res);
			if (res instanceof IFolder) {
				getAllMembers(((IFolder) res).members(), resList);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.fastcode.util.RepositoryService#commitToRepository(java.util.Map, boolean)
	 */
	@Override
	public void commitToRepository(final Map<Object, List<FastCodeEntityHolder>> placeHoldersForFile, final boolean addToCache)
			throws FastCodeRepositoryException, Exception {
		final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
		final String projectName = EMPTY_STR;
		//final Map<File, String> fileStatusMap = new HashMap<File, String>();
		final Map<File, String> fileMap = new HashMap<File, String>();
		final StringBuilder filesToCheckin = new StringBuilder();
		//String newFile = "false";
		/*String prefix = EMPTY_STR;
		String footer = EMPTY_STR;*/
		IProject prj = null;
		boolean fldrPkg = false;
		final StringBuilder filesWithError = new StringBuilder();
		if (checkinCache.getCommentKeyDetail().isEmpty()) {
			loadComments();
		}

		for (final Map.Entry<Object, List<FastCodeEntityHolder>> entry : placeHoldersForFile.entrySet()) {
			//if (entry.getKey() instanceof IType) {
			final StringBuilder commentForFile = new StringBuilder(EMPTY_STR);
			for (final FastCodeEntityHolder fcEntityHolder : entry.getValue()) {
				String comment = EMPTY_STR;
				final Map<String, Object> placeHolder = new HashMap<String, Object>();
				placeHolder.put(fcEntityHolder.getEntityName(), fcEntityHolder.getFastCodeEntity());
				/*if (EMPTY_STR.equals(commentForFile.toString())) {
					getGlobalSettings(placeHolder);
					prefix = evaluateByVelocity(this.preferenceStore.getString(P_COMMENTS_PREFIX), placeHolder);
					footer = evaluateByVelocity(this.preferenceStore.getString(P_COMMENTS_FOOTER), placeHolder);
				}*/
				if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_CLASS)) {
					comment = evaluateByVelocity(checkinCache.getCommentKeyDetail().get(CREATE_CLASS), placeHolder);
					//newFile = "true";
				} else if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_FIELDS)) {
					comment = evaluateByVelocity(checkinCache.getCommentKeyDetail().get(CREATE_FIELDS), placeHolder);
				} else if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_METHODS)) {
					comment = evaluateByVelocity(checkinCache.getCommentKeyDetail().get(CREATE_METHODS), placeHolder);
				} else if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_FILE)) {
					comment = evaluateByVelocity(checkinCache.getCommentKeyDetail().get(CREATE_FILE), placeHolder);
					//newFile = "true";
				} else if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_PACKAGE)) {
					comment = evaluateByVelocity(checkinCache.getCommentKeyDetail().get(CREATE_PACKAGE), placeHolder);
					fldrPkg = true;
				} else if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_INNERCLASSES)) {
					comment = evaluateByVelocity(checkinCache.getCommentKeyDetail().get(CREATE_INNER_CLASS), placeHolder);
				} else if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_LOCALVARS)) {
					comment = evaluateByVelocity(checkinCache.getCommentKeyDetail().get(CREATE_LOCALVARIABLE), placeHolder);
				} else if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_STUBMETHODS)) {
					comment = evaluateByVelocity(checkinCache.getCommentKeyDetail().get(CREATE_STUBMETHOD), placeHolder);
				} else if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_TESTMETHODS)) {
					comment = evaluateByVelocity(checkinCache.getCommentKeyDetail().get(CREATE_TESTMETHOD), placeHolder);
				} else if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_MODIFY_FIELD)) {
					comment = evaluateByVelocity(checkinCache.getCommentKeyDetail().get(MODIFY_FIELD), placeHolder);
				} else if (fcEntityHolder.getEntityName().equals(PLACEHOLDER_FOLDER)) {
					comment = evaluateByVelocity(checkinCache.getCommentKeyDetail().get(CREATE_FOLDER), placeHolder);
					fldrPkg = true;
				}

				commentForFile.append(EMPTY_STR.equals(commentForFile.toString()) ? comment : NEWLINE + comment);
				//checkinCache.setProjectToRefresh(((IFile) entry.getKey()).getProject());
				if (prj == null) {
					prj = ((IFile) entry.getKey()).getProject();
				}
			}

			if (fldrPkg) {
				fileMap.put(new File(((IFile) entry.getKey()).getLocationURI()), commentForFile.toString());
				filesToCheckin.append(EMPTY_STR.equals(filesToCheckin.toString()) ? ((IFile) entry.getKey()).getLocationURI()
						.toString() : COMMA + SPACE + ((IFile) entry.getKey()).getLocationURI().toString());
			} else {
				if (!checkForErrors((IFile) entry.getKey())) {
					fileMap.put(new File(((IFile) entry.getKey()).getLocationURI()), commentForFile.toString());
					//fileStatusMap.put(new File(((IFile) entry.getKey()).getLocationURI()), String.valueOf(newFile));
					filesToCheckin.append(EMPTY_STR.equals(filesToCheckin.toString()) ? ((IFile) entry.getKey()).getLocationURI()
							.toString() : COMMA + SPACE + ((IFile) entry.getKey()).getLocationURI().toString());
				} else {
					addFileToCache(new File(((IFile) entry.getKey()).getLocationURI()), commentForFile.toString());
					filesWithError.append(EMPTY_STR.equals(filesWithError.toString()) ? ((IFile) entry.getKey()).getLocationURI()
							.toString() : COMMA + SPACE + ((IFile) entry.getKey()).getLocationURI().toString());
				}
			}
		}

		if (!isEmpty(filesWithError.toString())) {
			MessageDialog.openWarning(new Shell(), "Warning", "There are errors present in file(s): " + filesWithError.toString()
					+ ". Adding to cache.");
		}
		if (!fileMap.isEmpty()) {
			boolean checkinNow = false;
			if (!addToCache) {
				checkinNow = checkInNow(filesToCheckin.toString());
			}
			final IProject project = prj;
			/*final String prefx = prefix;
			final String foot = footer;*/
			final boolean chkInNw = checkinNow;
			final IRunnableWithProgress op = new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor) {
					try {
						for (final Entry<File, String> fileComnt : fileMap.entrySet()) {
							if (addToCache || !chkInNw) {
								addFileToCache(fileComnt.getKey(), fileComnt.getValue());
								//System.out.println(System.currentTimeMillis());
							} else {
								/*final String url = getRepositoryURL(prj);
								if (!isEmpty(url)) {*/

								//}
								monitor.setTaskName("Auto checkin");
								monitor.subTask("Checking in " + fileComnt.getKey().getPath());
								final String previousComments = getPreviosCommentsFromCache(checkinCache, fileComnt.getKey());
								final Map<String, Object> placeHolder = new HashMap<String, Object>();
								getGlobalSettings(placeHolder);
								final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();
								final String prefix = evaluateByVelocity(versionControlPreferences .getComntPrefix(), placeHolder);
								final String footer = evaluateByVelocity(versionControlPreferences.getComntFooter(), placeHolder);
								final String finalComments = prefix + NEWLINE + previousComments + NEWLINE + fileComnt.getValue() + NEWLINE + footer;
								checkInFile(fileComnt.getKey() , finalComments, project); //, url);
							}

						}

					} catch (final FastCodeRepositoryException ex) {
						MessageDialog.openError(new Shell(), "Error", "Some error occured --" + ex.getMessage());
						ex.getCause().printStackTrace();
					} catch (final Exception ex) {
						MessageDialog.openError(new Shell(), "Error", "Some error occured --" + ex.getMessage());
						ex.printStackTrace();
					} finally {
						monitor.done();
					}
				}


			};

			final IWorkbench wb = PlatformUI.getWorkbench();
			final IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
			new ProgressMonitorDialog(new Shell()).run(false, false, op);
			refreshProject(prj.getName());
		}
	}

	private String getPreviosCommentsFromCache(final FastCodeCheckinCache checkinCache, final File file) {
		for (final FastCodeFileForCheckin fileForCheckin : checkinCache.getFilesToCheckIn()) {
			System.out.println(fileForCheckin.getFileFullName());
			System.out.println(file.getAbsolutePath());
			if (fileForCheckin.getFileFullName().equals(file.getAbsolutePath())) {
				return isEmpty(fileForCheckin.getComments()) ? EMPTY_STR : fileForCheckin.getComments();
			}
		}
		return EMPTY_STR;
	}

	/* (non-Javadoc)
	 * @see org.fastcode.util.RepositoryService#isFileInRepository(java.io.File)
	 */
	@Override
	public boolean isFileInRepository(final File file) throws FastCodeRepositoryException {
		SVNStatus info;
		if (this.ourClientManager == null) {
			setRepositoryDetailsForFile(getPrjFromFile(file).getName());
		}
		try {
			info = this.ourClientManager.getStatusClient().doStatus(file, true);
		} catch (final SVNException ex) {
			ex.printStackTrace();
			System.out.println(ex.getErrorMessage().getErrorCode().getCode());
			if (!isEmpty(ex.getMessage()) && ex.getErrorMessage().getErrorCode().getCode() == 155007) { //equals("155007: Path is not a working copy directory")) {
				return false;
			}
			throw new FastCodeRepositoryException(ex.getMessage());
		} catch (final Throwable th) {
			th.printStackTrace();
			throw new FastCodeRepositoryException(th.getMessage());
		}

		if (info == null) {
			return false;
		}

		return info.getContentsStatus() != SVNStatusType.STATUS_UNVERSIONED;
	}



	@Override
	public List<String> getPreviousComments(final String project) throws FastCodeRepositoryException {
		final long startRevision = 0;
		final long endRevision = -1; //HEAD (the latest) revision
		Collection logEntries = null;
		final List<String> preComments = new ArrayList<String>();

		try {
			if (this.repository == null) {
				setRepositoryDetailsForFile(project);
			}
			//logEntries = this.repository.log(new String[] { file.getFullPath().toString() }, null, startRevision, endRevision, true, true);
			logEntries = this.repository.log(new String[] { "" }, null, startRevision, endRevision, true, true);
			final List<SVNLogEntry> logEntriesList = new ArrayList<SVNLogEntry>(logEntries);
			Collections.reverse(logEntriesList);
			for (final SVNLogEntry logEntry : logEntriesList) {

				if (!isEmpty(logEntry.getMessage())) {
					preComments.add(logEntry.getMessage());
				}
				if (preComments.size() == 20) {
					break;
				}

			}
			/*for (final Iterator entries = logEntries.iterator(); entries.hasNext();) {
				final SVNLogEntry logEntry = (SVNLogEntry) entries.next();
				System.out.println("log message: " + logEntry.getMessage());
				preComments.
				if (!isEmpty(logEntry.getMessage())) {
					preComments.add(logEntry.getMessage());
				}
			}*/
		} catch (final SVNException ex) {
			throw new FastCodeRepositoryException(ex.getMessage(), ex.getCause());
		}
		return preComments;

	}

	/*public String getCommentsFromUser(IFile file) throws FastCodeRepositoryException {
		final String comment;
		final List<String> cmntsFromRepo = getPreviousComments(file.getProject().getName());
		final List<String> cmntsFromCache = getPreviousCommentsFromCache(file);

		final FastCodeCheckinCommentsData comboData = new FastCodeCheckinCommentsData();
		comboData.setComntsFromCache(cmntsFromCache);
		comboData.setComntsFromRepo(cmntsFromRepo);
		final FastCodeCheckinCommentsDialog fastCodeCombo = new FastCodeCheckinCommentsDialog(new Shell(), comboData);
		//

		if (fastCodeCombo.open() == Window.CANCEL) {
			comment = getCompilationUnitFromEditor() != null ? "Modified Class " + file.getName() + DOT : "Modified File "
					+ file.getName() + DOT;
		} else {
		comment = comboData.getFinalComment();
		}
		return comment;
	}*/

	@Override
	public boolean doesFileHaveChanges(final File file) throws FastCodeRepositoryException {
		SVNStatus info = null;
		if (this.ourClientManager == null) {
			setRepositoryDetailsForFile(getPrjFromFile(file).getName());
		}
		try {
			info = this.ourClientManager.getStatusClient().doStatus(file, true);
		} catch (final SVNException ex) {
			ex.printStackTrace();
			System.out.println(ex.getErrorMessage().getErrorCode().getCode());
			if (!isEmpty(ex.getMessage()) && ex.getErrorMessage().getErrorCode().getCode() == 155007) { //equals("155007: Path is not a working copy directory")) {
				return true;
			}
			throw new FastCodeRepositoryException(ex.getMessage());
		} catch (final Throwable th) {
			th.printStackTrace();
			throw new FastCodeRepositoryException(th.getMessage());
		}

		if (info == null) {
			return true;
		}

		return info.getContentsStatus() != SVNStatusType.STATUS_NORMAL;
	}
}
