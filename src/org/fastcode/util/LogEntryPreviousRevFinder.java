package org.fastcode.util;

import java.util.Iterator;
import java.util.Set;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

public class LogEntryPreviousRevFinder implements ISVNLogEntryHandler {
	private final String	interestingFile;
	private String			previousPath;
	private final long		thisRevision;
	private long			previousRevision;
	private boolean			isSuccess;

	public LogEntryPreviousRevFinder(final String interestingFile, final long revision) {
		this.interestingFile = interestingFile;
		this.thisRevision = revision;
		this.isSuccess = false;
	}

	@Override
	public void handleLogEntry(final SVNLogEntry logEntry) throws SVNException {
		if (this.isSuccess) {
			return;
		}

		if (this.thisRevision == logEntry.getRevision()) {
			return;
		}

		final Set changedPathsSet = logEntry.getChangedPaths().keySet();
		for (final Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
			final SVNLogEntryPath entryPath = logEntry.getChangedPaths().get(changedPaths.next());
			final String workingFileName = entryPath.getPath();
			System.out.println(workingFileName + " --> " + this.interestingFile);
			if (workingFileName.endsWith(this.interestingFile)) {
				this.previousRevision = logEntry.getRevision();
				this.previousPath = workingFileName;
				this.isSuccess = true;
			}
		}
	}

	public long getPreviousRevision() {
		return this.previousRevision;
	}

	public String getPreviousPath() {
		return this.previousPath;
	}

	public boolean isSuccess() {
		return this.isSuccess;
	}
}
