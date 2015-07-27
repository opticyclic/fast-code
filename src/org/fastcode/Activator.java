/*
 * Fast Code Plugin for Eclipse
 *
 * Copyright (C) 2008  Gautam Dev
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA.
 *
 * Plugin Home Page: http://fast-code.sourceforge.net/
 */

package org.fastcode;

import static org.fastcode.common.FastCodeConstants.CHECKED;
import static org.fastcode.common.FastCodeConstants.DISABLED_CHECKBOX;
import static org.fastcode.common.FastCodeConstants.UNCHECKED;
import static org.fastcode.util.SourceUtil.checkForJavaProjectInWorkspace;

import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.fastcode.common.FastCodeColor;
import org.fastcode.common.FastCodeFont;
import org.fastcode.popup.actions.snippet.FastCodeCache;
import org.fastcode.util.FastCodeResourceChangeListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID

	public static final String	PLUGIN_ID	= "FastCode";
	// The shared instance
	private static Activator	plugin;
	IWorkspace					workspace	= ResourcesPlugin.getWorkspace();
	IResourceChangeListener		listener	= FastCodeResourceChangeListener.getInstance();

	/**
	 * The constructor
	 */
	public Activator() {
		//
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		try {
			super.start(context);
			plugin = this;
			this.workspace.addResourceChangeListener(this.listener);
			checkForJavaProjectInWorkspace();
		} catch (final Exception e) {
			//
			e.printStackTrace();
		}
	}

	private boolean isEmpty(final IProject[] projectArr) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			super.stop(context);
			this.workspace.removeResourceChangeListener(this.listener);

			FastCodeFont.disposeFont();
			FastCodeColor.disposeColor();
			final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
			for (final Entry<String, Image> entry : fastCodeCache.getEntityImageMap().entrySet()) {

				final Image image = entry.getValue();
				if (image != null && !image.isDisposed()) {
					image.dispose();
				}

			}
			fastCodeCache.getEntityImageMap().clear();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry registry) {
		super.initializeImageRegistry(registry);
		final Bundle bundle = Platform.getBundle("org.fastcode");

		//
		// Setup our own images to be used as icons, etc
		//
		ImageDescriptor image = ImageDescriptor.createFromURL(Platform.find(bundle, new Path("icons/checked.gif")));

		registry.put(CHECKED, image);

		image = ImageDescriptor.createFromURL(Platform.find(bundle, new Path("icons/unchecked.gif")));
		registry.put(UNCHECKED, image);

		image = ImageDescriptor.createFromURL(Platform.find(bundle, new Path("icons/disabled_checkbox.gif")));
		registry.put(DISABLED_CHECKBOX, image);
	}
}
