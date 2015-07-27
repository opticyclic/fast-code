package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.FC_PLUGIN;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_FILE;
import static org.fastcode.common.FastCodeConstants.PLACEHOLDER_FOLDER;
import static org.fastcode.common.FastCodeConstants.UNDERSCORE;
import static org.fastcode.util.SVNRepositoryService.connectToRepository;
import static org.fastcode.util.SourceUtil.getImagefromFCCacheMap;
import static org.fastcode.util.SourceUtil.populateFCCacheEntityImageMap;
import static org.fastcode.util.StringUtil.isEmpty;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.fastcode.Activator;
import org.fastcode.common.FastCodeColor;
import org.fastcode.common.RepositoryData;
import org.fastcode.common.RepositoryFolder;
import org.fastcode.common.RepositoryFolders;
import org.fastcode.exception.FastCodeRepositoryException;
import org.fastcode.popup.actions.snippet.FastCodeCache;
import org.fastcode.setting.GlobalSettings;

public class RepositoryDialog extends TrayDialog {

	Combo					repositoryName;
	Text					repositoryLocation;
	Text					repositoryURL;
	Button					browseRepo;
	Text					userName;
	Text					password;
	Combo					project;
	Shell					shell;
	RepositoryData			repositoryData;
	private String			errorMessage;
	private Text			errorMessageText;
	private final String	defaultMessage		= NEWLINE;
	private final String	pathNotFoundMessage	= "There is no path ";
	private boolean			prjUrlPairExists;
	String					origPrj;
	String					origUrl;

	/**
	 * @param shell
	 */
	public RepositoryDialog(final Shell shell) {
		super(shell);
		this.shell = shell;
	}

	/**
	 * @param shell
	 * @param repositoryData
	 */
	public RepositoryDialog(final Shell shell, final RepositoryData repositoryData) {
		super(shell);
		this.shell = shell;
		this.repositoryData = repositoryData;
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Select Project and corresponding Repository URL");
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		createErrorMessageText(parent);
		createAssociatedProject(parent);
		createRepositoryURL(parent);
		return parent;
	}

	/**
	 * @param parent
	 */
	private void createRepositoryURL(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData(150, 20);
		final Label repURL = new Label(composite, SWT.NONE);
		repURL.setText("Repository URL");
		repURL.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData(400, 20);
		/*gridDataText.grabExcessHorizontalSpace = true;
		gridDataText.minimumWidth = 200;*/
		this.repositoryURL = new Text(composite, SWT.BORDER);// | SWT.READ_ONLY);
		this.repositoryURL.setLayoutData(gridDataText);

		if (this.repositoryData.getSource().equals("Add") && !isEmpty(this.repositoryData.getRepBaseLocation())) {
			this.repositoryURL.setText(this.repositoryData.getRepBaseLocation());
		} else if (this.repositoryData.getSource().equals("Edit")) {
			this.repositoryURL.setText(this.repositoryData.getRepUrl());
			this.origUrl = this.repositoryData.getRepUrl();
		}

		this.repositoryURL.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent arg0) {
				setErrorMessage(RepositoryDialog.this.defaultMessage);
			}

		});
		/*final GridData gridDataButton = new GridData();
		this.browseRepo = new Button(composite, SWT.PUSH);
		this.browseRepo.setText("Browse");
		this.browseRepo.setLayoutData(gridDataButton);

		this.browseRepo.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event e) {
				RepositoryService repositoryService;
				try {
					System.out.println("before call to SVN");
					repositoryService = getRepositoryServiceClass();

					final List<RepositoryFolder> repFldrList = repositoryService.getSharedProjects(
							RepositoryDialog.this.repositoryData.getRepBaseLocation(), RepositoryDialog.this.repositoryData.getUserName(),
							RepositoryDialog.this.repositoryData.getPassword());
					System.out.println("after call to SVN");
					if (!repFldrList.isEmpty()) {
						final RepositoryFolders repFldrs = new RepositoryFolders.Builder().withRepositoryFolder(
								repFldrList.toArray(new RepositoryFolder[0])).build();
						final CheckedTreeSelectionDialog checkedTreeSelectionDialog = new CheckedTreeSelectionDialog(new Shell(),
								new RepFldrLabelProvider(), new RepFldrContentProvider());
						checkedTreeSelectionDialog.setTitle("Action(s) Selection");
						checkedTreeSelectionDialog.setMessage("Select the action(s)");
						checkedTreeSelectionDialog.setInput(repFldrs);
						//checkedTreeSelectionDialog.setInitialElementSelections(filterActionList(actionList));//checkedTreeSelectionDialog.setInitialElementSelections(actionList);
						//checkedTreeSelectionDialog.setExpandedElements(actionList.toArray(new Action[0]));
						//checkedTreeSelectionDialog.setContainerMode(true);

						if (checkedTreeSelectionDialog.open() == Window.CANCEL) {
							return;
						}
						if (checkedTreeSelectionDialog.getResult() != null) {

							final RepositoryFolder repFldrSelected = (RepositoryFolder) checkedTreeSelectionDialog.getResult()[0];
							final String newUrl = repFldrSelected.getPath() + FORWARD_SLASH + repFldrSelected.getName();
							if (urlAlreadyInUse(newUrl, RepositoryDialog.this.repositoryData.getAssociatedProject(),
									RepositoryDialog.this.repositoryData.getPrjUrlMap())) {
								setErrorMessage("The url " + newUrl
										+ " is already use for some other project. Please select another Location.");
							} else {
								setErrorMessage(RepositoryDialog.this.defaultMessage);
							}
							RepositoryDialog.this.repositoryURL.setText(newUrl);
						}
					}
				} catch (final FastCodeRepositoryException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
					setErrorMessage(ex.getMessage());
				}
			}

		});*/

	}

	/**
	 * @param parent
	 */
	private void createAssociatedProject(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData(150, 20);
		final Label repPrj = new Label(composite, SWT.NONE);
		repPrj.setText("Project");
		repPrj.setLayoutData(gridDataLabel);

		final GridData gridDataCombo = new GridData();
		/*gridDataText.grabExcessHorizontalSpace = true;
		gridDataText.minimumWidth = 200;*/
		this.project = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		this.project.setLayoutData(gridDataCombo);

		if (!isEmpty(this.repositoryData.getAssociatedProject())) {
			this.project.add(this.repositoryData.getAssociatedProject());
			this.project.select(0);
			this.origPrj = this.repositoryData.getAssociatedProject();
		}

		final IProject projects[] = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (final IProject prj : projects) {
			if (prj == null || !prj.exists() || !prj.isOpen()) {
				continue;
			}
			if (prj.getName().equals(FC_PLUGIN)) {
				continue;
			}

			if (this.repositoryData.getSource().equalsIgnoreCase("Add") && this.repositoryData.getPrjNamesPair().containsKey(prj.getName())) {
				continue;
			}
			if (!isEmpty(this.repositoryData.getAssociatedProject())
					&& this.repositoryData.getAssociatedProject().equalsIgnoreCase(prj.getName())) {
				continue;
			}
			this.project.add(prj.getName());
		}

		this.project.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String projectName = RepositoryDialog.this.project.getText();
				if (!isEmpty(projectName)) {
					//RepositoryDialog.this.repositoryData.setAssociatedProject(projectName);
					//all is url in use
					if (prjAlreadyInUse(RepositoryDialog.this.repositoryURL.getText(), projectName,
							RepositoryDialog.this.repositoryData.getPrjUrlMap())) {
						setErrorMessage("The project " + projectName + " is mapped to "
								+ RepositoryDialog.this.repositoryData.getPrjUrlMap().get(projectName) + ".Please select another Poject.");
					} else {
						setErrorMessage(RepositoryDialog.this.defaultMessage);
					}
				} else {
					setErrorMessage("Please select a project");
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

	}

	@Override
	protected void okPressed() {
		//Feb 4th disable cancel
		enableCancleButton(false);
		/*this.repositoryData.setRepName(this.repositoryName.getItem(this.repositoryName.getSelectionIndex()));
		this.repositoryData.setRepLocation(this.repositoryLocation.getText());*/
		if (this.project.getSelectionIndex() == -1 || isEmpty(this.project.getItem(this.project.getSelectionIndex()))) {
			setErrorMessage("Please select a Project");
			return;
		} else {
			//this.repositoryData.setAssociatedProject(this.project.getItem(this.project.getSelectionIndex()));
			setErrorMessage(this.defaultMessage);
		}

		if (isEmpty(this.repositoryURL.getText())) {
			setErrorMessage("Please select an URL");
			return;
		} else {
			/*if (this.repositoryURL.getText().trim().equals(this.repositoryData.getRepBaseLocation()) || this.repositoryURL.getText().trim().equals(this.repositoryData.getRepBaseLocation() + FORWARD_SLASH) || this.repositoryURL.getText().trim().equals(this.repositoryData.getRepBaseLocation() + FORWARD_SLASH + FORWARD_SLASH)) {
				setErrorMessage("Please enter a folder/project name along with the base location.");
				return;
			}*/

			final String url = this.repositoryURL.getText();
			final String userId = this.repositoryData.getUserName();
			final String passwd = this.repositoryData.getPassword();
			try {
				//Feb 4th add a meeage -- verifying url
				setErrorMessage("Veryfing URL");
				connectToRepository(url, userId, passwd);
				setErrorMessage(this.defaultMessage);
			} catch (final FastCodeRepositoryException ex) {
				//Feb 4th enable cancel
				enableCancleButton(true);
				setErrorMessage(ex.getMessage());
				return;
			} finally {

			}

			//this.repositoryData.setRepUrl(this.repositoryURL.getText());
			setErrorMessage(this.defaultMessage);
		}

		if (urlAlreadyInUse(this.repositoryURL.getText(), this.project.getItem(this.project.getSelectionIndex()),
				RepositoryDialog.this.repositoryData.getPrjUrlMap())) {
			setErrorMessage("The url " + this.repositoryURL.getText()
					+ " is already use for some other project. Please select another Location.");
			return;
		} else {
			setErrorMessage(RepositoryDialog.this.defaultMessage);
		}

		if (this.repositoryData.getSource().equals("Add") && this.prjUrlPairExists) {
			setErrorMessage("The project " + this.project.getItem(this.project.getSelectionIndex()) + " is already mapped to url "
					+ this.repositoryURL.getText() + " Please select another project and URL.");
			return;
		}

		this.repositoryData.setAssociatedProject(this.project.getItem(this.project.getSelectionIndex()));
		this.repositoryData.setRepUrl(this.repositoryURL.getText());
		super.okPressed();
	}

	/**
	 * @param errorMessage
	 */
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
		if (this.errorMessageText != null && !this.errorMessageText.isDisposed()) {
			this.errorMessageText.setText(errorMessage == null ? " \n " : errorMessage); //$NON-NLS-1$
			boolean hasError = false;
			if (errorMessage != null && !errorMessage.equals(this.defaultMessage)) {
				hasError = true;
			}
			this.errorMessageText.setEnabled(hasError);
			this.errorMessageText.setVisible(hasError);
			this.errorMessageText.getParent().update();
			final Control button = getButton(IDialogConstants.OK_ID);
			if (button != null) {
				button.setEnabled(errorMessage.equals(this.defaultMessage));
			}
		}
	}

	/**
	 * @param parent
	 */
	private void createErrorMessageText(final Composite parent) {

		final GridData errText = new GridData(590, 40);
		errText.grabExcessHorizontalSpace = true;

		this.errorMessageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
		// this.errorMessageText.setBackground(new Color(null, 255, 255, 255));
		//this.errorMessageText.setForeground(new Color(null, 255, 0, 0));
		this.errorMessageText.setForeground(FastCodeColor.getErrorMsgColor());
		this.errorMessageText.setLayoutData(errText);
		setErrorMessage(this.defaultMessage);
	}

	/**
	 * @param url
	 * @param prj
	 * @param prjUrlMap
	 * @return
	 */
	private boolean urlAlreadyInUse(final String url, final String prj, final Map<String, String> prjUrlMap) {

		if (this.repositoryData.getSource().equals("Add")) {
			for (final Entry<String, String> prjUrl : prjUrlMap.entrySet()) {
				if (prjUrl.getKey().equalsIgnoreCase(prj) || prjUrl.getValue().equals(url)) {
					return true;
				}
				if (prjUrl.getKey().equalsIgnoreCase(prj) && prjUrl.getValue().equals(url)) {
					this.prjUrlPairExists = true;
				}

			}
		} else if (this.repositoryData.getSource().equals("Edit")) {
			/*if (prj.equals(this.origPrj) && url.equals(this.origUrl)) {
				return false;
			}*/

			for (final Entry<String, String> prjUrl : prjUrlMap.entrySet()) {

				if (prjUrl.getKey().equalsIgnoreCase(prj) && prjUrl.getValue().equals(url)) {
					return false;
				}
				if (!(prjUrl.getKey().equalsIgnoreCase(prj) || prjUrl.getKey().equals(this.origPrj)) && prjUrl.getValue().equals(url)) {
					return true;
				}
			}
		}

		return false;

	}

	/**
	 * @param url
	 * @param prj
	 * @param prjUrlMap
	 * @return
	 */
	private boolean prjAlreadyInUse(final String url, final String prj, final Map<String, String> prjUrlMap) {

		if (this.repositoryData.getSource().equals("Add")) {
			for (final Entry<String, String> prjUrl : prjUrlMap.entrySet()) {
				if (prjUrl.getKey().equalsIgnoreCase(prj)) { // || prjUrl.getValue().equals(url)) {
					return true;
				}
				if (prjUrl.getKey().equalsIgnoreCase(prj) && prjUrl.getValue().equals(url)) {
					this.prjUrlPairExists = true;
				}
			}
		} else if (this.repositoryData.getSource().equals("Edit")) {

			for (final Entry<String, String> prjUrl : prjUrlMap.entrySet()) {
				if (prjUrl.getKey().equalsIgnoreCase(prj) && prjUrl.getValue().equals(url)) {
					return false;
				}
				if (prjUrl.getKey().equalsIgnoreCase(prj) && !prjUrl.getValue().equals(url)) {
					return true;
				}
			}
		}

		return false;

	}

	/**
	 * @param enable
	 */
	private void enableCancleButton(final boolean enable) {
		final Control button = getButton(IDialogConstants.CANCEL_ID);
		if (button != null) {
			button.setEnabled(enable);
		}
	}

	/**
	 * @author
	 *
	 */
	private class RepFldrLabelProvider implements ILabelProvider {

		private Image	image;

		@Override
		public void addListener(final ILabelProviderListener arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isLabelProperty(final Object arg0, final String arg1) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void removeListener(final ILabelProviderListener arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public Image getImage(final Object input) {
			if (input instanceof RepositoryFolder) {
				return getImageForRepositoryFolder((RepositoryFolder) input);
			}
			if (input instanceof RepositoryFolders) {
				for (final RepositoryFolder repFldr : ((RepositoryFolders) input).getRepositoryFolder()) {
					return getImageForRepositoryFolder(repFldr);
				}
			}
			return null;
		}

		/**
		 * @param input
		 * @return
		 */
		private Image getImageForRepositoryFolder(final RepositoryFolder input) {
			final GlobalSettings globalSettings = GlobalSettings.getInstance();
			final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
			String imageName = null;
			Image image = null;
			if (input.getType().equals("FILE")) {
				if (fastCodeCache.getEntityImageMap().containsKey(PLACEHOLDER_FILE)) {
					return getImagefromFCCacheMap(PLACEHOLDER_FILE);
				}
				imageName = globalSettings.getPropertyValue(PLACEHOLDER_FILE.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
				image = getImage(imageName);
				populateFCCacheEntityImageMap(PLACEHOLDER_FILE, image);
			} else if (input.getType().equals("DIR")) {
				if (fastCodeCache.getEntityImageMap().containsKey(PLACEHOLDER_FOLDER)) {
					return getImagefromFCCacheMap(PLACEHOLDER_FOLDER);
				}
				imageName = globalSettings.getPropertyValue(PLACEHOLDER_FOLDER.toUpperCase() + UNDERSCORE + "IMAGE", EMPTY_STR);
				image = getImage(imageName);
				populateFCCacheEntityImageMap(PLACEHOLDER_FOLDER, image);
			}
			return image;
		}

		/**
		 * Gets the image.
		 *
		 * @param imageName
		 *            the image name
		 * @return the image
		 */
		private Image getImage(String imageName) {
			URL url = null;
			if (imageName == null) {
				return null;
			}
			final Image image = PlatformUI.getWorkbench().getSharedImages().getImage(imageName);
			if (image != null && !image.isDisposed()) {
				// this.image = null;
				return image;
			}
			try {
				if (imageName.startsWith("org.eclipse.jdt.ui.")) {
					imageName = imageName.substring("org.eclipse.jdt.ui.".length());
				}
				url = new URL(Activator.getDefault().getDescriptor().getInstallURL(), "icons/" + imageName);
			} catch (final MalformedURLException ex) {
				ex.printStackTrace();
				return null;
			}
			final ImageDescriptor descriptor = ImageDescriptor.createFromURL(url);
			this.image = descriptor.createImage();
			return this.image;
		}

		@Override
		public String getText(final Object input) {
			if (input instanceof RepositoryFolder) {
				return ((RepositoryFolder) input).getName();
			}
			if (input instanceof RepositoryFolders) {
				for (final RepositoryFolder repFldr : ((RepositoryFolders) input).getRepositoryFolder()) {
					return repFldr.getName();
				}
			}
			return null;
		}

	}

	/**
	 * @author
	 *
	 */
	private class RepFldrContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void inputChanged(final Viewer arg0, final Object arg1, final Object arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object[] getChildren(final Object input) {
			if (!(input instanceof RepositoryFolders || input instanceof RepositoryFolder)) {
				return null;
			}
			RepositoryFolder repFldr = null;
			RepositoryFolder[] subFolders = null;
			try {
				if (input instanceof RepositoryFolder) {
					repFldr = (RepositoryFolder) input;
					if (repFldr.getSubFolder() != null) {
						subFolders = repFldr.getSubFolder().isEmpty() ? null : repFldr.getSubFolder().toArray(new RepositoryFolder[0]);
						return subFolders;
					}
				}
				if (input instanceof RepositoryFolders) {
					return ((RepositoryFolders) input).getRepositoryFolder();
				}

			} catch (final Exception ex) {
				ex.printStackTrace();
				return null;
			}
			return null;
		}

		@Override
		public Object[] getElements(final Object input) {
			return getChildren(input);
		}

		@Override
		public Object getParent(final Object input) {
			return input instanceof RepositoryFolder ? input : null;
		}

		@Override
		public boolean hasChildren(final Object input) {
			if (!(input instanceof RepositoryFolder || input instanceof RepositoryFolders)) {
				return false;
			}
			try {
				if (input instanceof RepositoryFolder) {
					if (((RepositoryFolder) input).getSubFolder() != null && !((RepositoryFolder) input).getSubFolder().isEmpty()) {
						return true;
					}
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}
			return false;
		}
	}

}
