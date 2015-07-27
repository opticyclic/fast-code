package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.COMPLETED;
import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.preferences.PreferenceConstants.P_CHECK_IN;
import static org.fastcode.util.StringUtil.isEmpty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.FastCodeConstants.CHECK_IN;
import org.fastcode.preferences.VersionControlPreferences;
import org.fastcode.versioncontrol.FastCodeCheckinCache;

public class AbstractRepositoryService {

}
