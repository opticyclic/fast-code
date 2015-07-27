package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.COMPLETED;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.INITIATED;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.util.SourceUtil.getRepositoryServiceClass;
import static org.fastcode.util.StringUtil.isEmpty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.fastcode.common.FastCodeCheckinCommentsData;
import org.fastcode.common.FastCodeConstants.CHECK_IN;
import org.fastcode.dialog.FastCodeCheckinCommentsDialog;
import org.fastcode.exception.FastCodeRepositoryException;
import org.fastcode.preferences.VersionControlPreferences;
import org.fastcode.versioncontrol.FastCodeCheckinCache;

public class VersionControlUtil {
	public static boolean checkInNow(final String fileChanged) {
		final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();

		if (isEmpty(versionControlPreferences.getCheckIn())) {
			MessageDialog.openWarning(new Shell(), "Check in Preference not set, adding changes to cache",
					"Please set the check in preference in window->preferences->Fast code Preference-> Version Control");
			return false;
		}
		if (versionControlPreferences.getCheckIn().equals(CHECK_IN.CHECK_IN.getValue())) {
			return true;
		} else if (versionControlPreferences.getCheckIn().equals(CHECK_IN.ASK_BEFORE_CHECKIN.getValue())) {
			if (MessageDialog.openQuestion(new Shell(), "Check In", "Do you want to check in files " + fileChanged + " now?")) {
				return true;
			} else {
				return false;
			}
		} else if (versionControlPreferences.getCheckIn().equals(CHECK_IN.DONOT_CHECKIN.getValue())) {
			return false;
		}
		return false;
	}

	public static IProject getPrjFromFile(final File file) {

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IPath location = Path.fromOSString(file.getAbsolutePath());
		final IFile ifile = workspace.getRoot().getFileForLocation(location);
		return ifile.getProject();
	}

	public static List<String> getPreviousCommentsFromCache(final File fileObj) {
		final List<String> comntInCache = new ArrayList<String>();
		//final File fileObj = new File(ifile.getLocationURI());
		final FastCodeCheckinCache fastCodeCheckinCache = FastCodeCheckinCache.getInstance();
		for (final FastCodeFileForCheckin fileToCheckin : fastCodeCheckinCache.getFilesToCheckIn()) {
			System.out.println("Ifile location URI:-" + fileObj.getAbsolutePath());
			System.out.println("File full name:-" + fileToCheckin.getFileFullName());
			if (fileToCheckin.getStatus().equals(COMPLETED)) { // && fileToCheckin.getFileFullName().equals(fileObj.getAbsolutePath())) {
				comntInCache.add(fileToCheckin.getComments());
			}
		}
		return comntInCache;
	}

	/**
	 * @param prj
	 * @param versionControlPreferences
	 * @return
	 * @throws FastCodeRepositoryException
	 */
	public static String isPrjConfigured(final String prj) throws FastCodeRepositoryException {
		final VersionControlPreferences versionControlPreferences = VersionControlPreferences.getInstance();
		final String urlFromPref = versionControlPreferences.getPrjUrlPair().get(prj);
		return urlFromPref;
	}

	public static void addOrUpdateFileStatusInCache(final File file) {
		boolean fileFound = false;
		final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
		for (final FastCodeFileForCheckin fileForCheckin : checkinCache.getFilesToCheckIn()) {
			System.out.println(fileForCheckin.getFileFullName());
			System.out.println(file.getAbsolutePath());
			if (fileForCheckin.getFileFullName().equals(file.getAbsolutePath())) {
				fileForCheckin.setStatus(INITIATED);
				fileFound = true;
				break;
			}
		}

		if (!fileFound) {
			checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(INITIATED, file.getAbsolutePath()));
		}
	}

	/**
	 * @param checkinCache
	 * @param fileStatusMap
	 * @param prefx
	 * @param foot
	 * @param fileComnt
	 */
	public static void addFileToCache(final File file, final String comment) {
		boolean fileFound = false;
		//if file already present in cache, then append the comment (except the prefix and footer)
		final FastCodeCheckinCache checkinCache = FastCodeCheckinCache.getInstance();
		for (final FastCodeFileForCheckin fileForCheckin : checkinCache.getFilesToCheckIn()) {
			System.out.println(fileForCheckin.getFileFullName());
			System.out.println(file.getAbsolutePath());
			if (fileForCheckin.getFileFullName().equals(file.getAbsolutePath())) {
				fileFound = true;
				fileForCheckin.setComments(isEmpty(fileForCheckin.getComments()) ? comment : fileForCheckin.getComments() + NEWLINE + comment);
				fileForCheckin.setCachedTime(Long.toString(System.currentTimeMillis()));
				fileForCheckin.setFile(file);
				fileForCheckin.setStatus(COMPLETED);
				fileForCheckin.setProject(getPrjFromFile(file));
				break;
			}
		}
		if (!fileFound) {
			checkinCache.getFilesToCheckIn().add(new FastCodeFileForCheckin(file, file.getAbsolutePath(), comment, COMPLETED, Long.toString(System.currentTimeMillis()), getPrjFromFile(file)));
		}
	}


	public class UserQuestion implements Callable<FastCodeCheckinCommentsData> {

		File	file;
		String	prjName;

		public UserQuestion(final File file, final String prjName) {
			this.file = file;
			this.prjName = prjName;
		}

		@Override
		public FastCodeCheckinCommentsData call() throws Exception {
			/*final InputDialog inputDialog = new InputDialog(new Shell(), "",  "", null, null);

			if (inputDialog.open() == Window.CANCEL) {
				return EMPTY_STR;
			}
			return inputDialog.getValue();*/
			System.out.println("i am here.....");
			final String comment = EMPTY_STR;
			//return "i m krish........";
			/*final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final IPath location = Path.fromOSString(this.file.getAbsolutePath());
			final IFile ifile = workspace.getRoot().getFileForLocation(location);*/

			final RepositoryService checkin = getRepositoryServiceClass();

			final List<String> cmntsFromRepo = checkin.getPreviousComments(this.prjName);
			final List<String> cmntsFromCache = getPreviousCommentsFromCache(this.file);

			final FastCodeCheckinCommentsData comboData = new FastCodeCheckinCommentsData();
			comboData.setComntsFromCache(cmntsFromCache);
			comboData.setComntsFromRepo(cmntsFromRepo);
			comboData.setTitle("Comments for " + this.file.getName());
			final FastCodeCheckinCommentsDialog fastCodeCombo = new FastCodeCheckinCommentsDialog(new Shell(), comboData);
			if (fastCodeCombo.open() == Window.CANCEL) {
				return null;
			} else {
				return comboData;
			}
		}
	}

}
