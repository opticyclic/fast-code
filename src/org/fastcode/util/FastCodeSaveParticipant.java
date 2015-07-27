package org.fastcode.util;

import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ISaveContext;
import org.eclipse.core.resources.ISaveParticipant;
import org.eclipse.core.resources.ISavedState;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class FastCodeSaveParticipant implements ISavedState, ISaveParticipant {

	@Override
	public void doneSaving(final ISaveContext arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepareToSave(final ISaveContext arg0) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollback(final ISaveContext arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saving(final ISaveContext arg0) throws CoreException {
		// TODO Auto-generated method stub

	}

	@Override
	public IPath[] getFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSaveNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IPath lookup(final IPath arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processResourceChangeEvents(final IResourceChangeListener arg0) {
		// TODO Auto-generated method stub

	}



}
