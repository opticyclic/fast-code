package org.fastcode.handler;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;
import org.fastcode.FastCodeCommandState;

public class VersionControlHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		// Get the source provider service
	    final ISourceProviderService sourceProviderService = (ISourceProviderService) HandlerUtil
	        .getActiveWorkbenchWindow(event).getService(ISourceProviderService.class);
	    // Now get my service
	    final FastCodeCommandState commandStateService = (FastCodeCommandState) sourceProviderService
	        .getSourceProvider(FastCodeCommandState.MY_STATE);
	    //commandStateService.toogleEnabled();
	    return null;
	}

	/*public Object execute(final ExecutionEvent arg0) throws ExecutionException {
		// TODO Auto-generated method stub
		return null;
	}*/

}
