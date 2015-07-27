package org.fastcode.dialog;

import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_CLASS_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_FILE_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_FOLDER_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_INTERFACE_STR;
import static org.fastcode.common.FastCodeConstants.ENCLOSING_PACKAGE_STR;
import static org.fastcode.common.FastCodeConstants.FALSE_STR;
import static org.fastcode.common.FastCodeConstants.FC_PLUGIN;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.HYPHEN;
import static org.fastcode.common.FastCodeConstants.INT;
import static org.fastcode.common.FastCodeConstants.LEFT_PAREN;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.RIGHT_PAREN;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.common.FastCodeConstants.SPACE_CHAR;
import static org.fastcode.common.FastCodeConstants.STARTLINE_ENDLINE;
import static org.fastcode.common.FastCodeConstants.TRUE_STR;
import static org.fastcode.common.FastCodeConstants.ZERO_STRING;
import static org.fastcode.util.FastCodeUtil.getEmptyListForNull;
import static org.fastcode.util.SourceUtil.getAlteredPackageName;
import static org.fastcode.util.SourceUtil.getDefaultPathFromProject;
import static org.fastcode.util.SourceUtil.getIJavaProjectFromName;
import static org.fastcode.util.SourceUtil.getLocalVarFromCompUnit;
import static org.fastcode.util.SourceUtil.getPackagesInProject;
import static org.fastcode.util.SourceUtil.isFullNameOfFile;
import static org.fastcode.util.StringUtil.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.internal.ide.dialogs.OpenResourceDialog;
import org.fastcode.common.FastCodeAdditionalParams;
import org.fastcode.common.FastCodeColor;
import org.fastcode.common.FastCodeConstants.RETURN_TYPES;
import org.fastcode.common.FastCodeFile;
import org.fastcode.common.FastCodeFolder;
import org.fastcode.common.FastCodePackage;
import org.fastcode.common.FastCodeProject;
import org.fastcode.common.FastCodeReturn;
import org.fastcode.common.FastCodeType;
import org.fastcode.common.IntRange;
import org.fastcode.common.PackageSelectionDialog;
import org.fastcode.common.ReturnValuesData;
import org.fastcode.popup.actions.snippet.FastCodeCache;

public class ReturnValuesDialog extends TrayDialog {

	Shell									shell;
	private ReturnValuesData				returnValuesData;
	Label[]									label;
	Text[]									valueText;
	int										size						= 0;
	StringBuffer							valueType					= new StringBuffer();
	private Text							errorMessageText;
	private final String					defaultMessage				= NEWLINE;
	private final String					selectMsg					= "Select a ";
	//private Combo[]							fileNameCombo;
	//private Combo[]							classNameCombo;
	//private Combo[]							folderNameCombo;
	private Combo							arrayDataCombo;
	//private Combo[]							packageCombo;
	//private Button							packageBrowseButton;
	//private Combo[]							projectCombo;
	//private Combo[]							localVarCombo;
	Map<String, IProject>					prjMap						= new HashMap<String, IProject>();
	//private Object[]						valuesObj;
	private ICompilationUnit				compilationUnit;
	private IType							currentClass;
	private IFile							currentFile;
	private IFolder							currentFolder;
	private IPackageFragment				currentPackage;
	//private Button[]						booleanParam;
	//Map<String, FastCodeAdditionalParams>	labelFCAdditonalParamMap	= new HashMap<String, FastCodeAdditionalParams>();
	//private Combo[]							interfaceNameCombo;
	private IType							currentInterface;
	//private Combo[]							enumNameCombo;
	//private Combo[]							allowedValuesCombo;
	//String[]								placeHolderNames;

	Map<Button, Combo>						browseButtonComboMap		= new HashMap<Button, Combo>();
	Map<String, Object>						paramKeyParamValueMap		= new HashMap<String, Object>();
	Map<String, Object>						paramKeySWTObjectMap		= new HashMap<String, Object>();
	Map<String, String>						paramKeyParamTypeMap		= new HashMap<String, String>();					//used for String and primitive types
	Map<String, List<String>>				parentKeyChildrenKeyMap		= new HashMap<String, List<String>>();
	Map<String, Object>						paramKeyLableSWTMap			= new HashMap<String, Object>();					//to add * for required children, after there is value in the parent
	Map<String, FastCodeAdditionalParams>	paramKeyFCAdditonalParamMap	= new HashMap<String, FastCodeAdditionalParams>();
	ScrolledComposite						scrolledComposite;

	/**
	 * @param shell
	 */
	public ReturnValuesDialog(final Shell shell) {
		super(shell);
		this.shell = shell;
	}

	/**
	 * @param shell
	 * @param returnValuesData
	 */
	public ReturnValuesDialog(final Shell shell, final ReturnValuesData returnValuesData) {
		super(shell);
		this.shell = shell;
		/*this.shell.redraw();
		this.shell.layout();*/
		/*	this.shell.layout();
			final Point newSize = this.shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
			this.shell.setSize(newSize);*/
		this.returnValuesData = returnValuesData;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final GridLayout layout = new GridLayout();
		// layout.numColumns = 2;
		parent.setLayout(layout);

		this.scrolledComposite = new ScrolledComposite(parent, SWT.V_SCROLL | SWT.BORDER);
		this.scrolledComposite.setExpandHorizontal(true);
		this.scrolledComposite.setExpandVertical(false);

		final Composite composite = new Composite(this.scrolledComposite, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);
		composite.setSize(400, 400);

		this.scrolledComposite.setContent(composite);

		this.scrolledComposite.addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(final ControlEvent e) {
				final Rectangle clientArea = ReturnValuesDialog.this.scrolledComposite.getClientArea();
				final Point minSize = ReturnValuesDialog.this.scrolledComposite.getContent().computeSize(clientArea.width, SWT.DEFAULT);
				ReturnValuesDialog.this.scrolledComposite.getContent().setSize(minSize);
			}
		});
		createErrorMessageText(composite);
		this.size = this.returnValuesData.getFastCodeAdditionalParams().length;
		if (this.returnValuesData.getEditorPart() != null) {
			this.compilationUnit = getCompilationUnitFromEditor(this.returnValuesData.getEditorPart());
		}
		for (int count = 0; count < this.size; count++) {
			final String paramKey = this.returnValuesData.getFastCodeAdditionalParams()[count].getName();
			String labelName = this.returnValuesData.getFastCodeAdditionalParams()[count].getLabel();
			final String valueType = this.returnValuesData.getFastCodeAdditionalParams()[count].getReturnTypes().getValue();
			boolean required = Boolean.valueOf(this.returnValuesData.getFastCodeAdditionalParams()[count].getRequired());
			final boolean requiredOrig = required;
			final String defaultValue = this.returnValuesData.getFastCodeAdditionalParams()[count].getDefaultValue();
			final String pattern = this.returnValuesData.getFastCodeAdditionalParams()[count].getPattern();
			final String allowedValues = this.returnValuesData.getFastCodeAdditionalParams()[count].getAllowedValues();
			final String project = this.returnValuesData.getFastCodeAdditionalParams()[count].getProject();
			//boolean enabled = Boolean.valueOf(this.returnValuesData.getFastCodeAdditionalParams()[count].getEnabled());
			boolean enabled = true;
			final String max = this.returnValuesData.getFastCodeAdditionalParams()[count].getMax();
			final String min = this.returnValuesData.getFastCodeAdditionalParams()[count].getMin();
			final String localVarType = this.returnValuesData.getFastCodeAdditionalParams()[count].getType();
			final String dependsOn = this.returnValuesData.getFastCodeAdditionalParams()[count].getDependsOn();

			if (!isEmpty(dependsOn)) {
				required = false;
				enabled = false;
				if (this.parentKeyChildrenKeyMap.containsKey(dependsOn)) {
					final List<String> childrenList = this.parentKeyChildrenKeyMap.get(dependsOn);
					childrenList.add(paramKey);
				} else {
					final List<String> childerenList = new ArrayList<String>();
					childerenList.add(paramKey);
					this.parentKeyChildrenKeyMap.put(dependsOn, childerenList);
				}
			}
			if (!enabled) {
				required = false;
			}

			//this.placeHolderNames[count] =
			if (isEmpty(labelName)) {
				labelName = paramKey; //this.returnValuesData.getFastCodeAdditionalParams()[count].getName();
				final String labelNameSuffix = SPACE + LEFT_PAREN + valueType + RIGHT_PAREN;
				if (!(valueType.equals(RETURN_TYPES.STRING.getValue()) || valueType.equals(RETURN_TYPES.BOOLEAN.getValue()))) {
					labelName = labelName + labelNameSuffix;
				}
			}
			this.paramKeyFCAdditonalParamMap.put(paramKey, this.returnValuesData.getFastCodeAdditionalParams()[count]);

			final GridData gridDataLabel = new GridData();
			final Label label = new Label(composite, SWT.NONE);
			label.setText(required ? ASTERISK + labelName : labelName);
			label.setLayoutData(gridDataLabel);

			this.paramKeyLableSWTMap.put(paramKey, label);

			if (valueType.equals(RETURN_TYPES.CLASS.getValue())) {
				final Combo classCombo = createClassSelectionPane(composite, project, pattern, enabled, required, paramKey);
				this.paramKeySWTObjectMap.put(paramKey, classCombo);
			} else if (valueType.equals(RETURN_TYPES.FILE.getValue())) {
				final Combo fileCombo = createFileSelectionPane(composite, project, pattern, enabled, required, paramKey);
				this.paramKeySWTObjectMap.put(paramKey, fileCombo);
			} else if (valueType.equals(RETURN_TYPES.FOLDER.getValue())) {
				final Combo folderCombo = createFolderSelectionPane(composite, defaultValue, project, pattern, enabled, required, paramKey);
				this.paramKeySWTObjectMap.put(paramKey, folderCombo);
			} else if (valueType.equals(RETURN_TYPES.PACKAGE.getValue())) {
				final Combo packageCombo = createPackageSelectionPane(composite, defaultValue, pattern, enabled, required, paramKey);
				this.paramKeySWTObjectMap.put(paramKey, packageCombo);
			} else if (valueType.equalsIgnoreCase(RETURN_TYPES.PROJECT.getValue())
					|| valueType.equalsIgnoreCase(RETURN_TYPES.JAVAPROJECT.getValue())) {
				final Combo prjCombo = createProjectSelectionPane(composite, valueType, defaultValue, pattern, enabled, required, paramKey);
				this.paramKeySWTObjectMap.put(paramKey, prjCombo);
			} else if (valueType.equalsIgnoreCase(RETURN_TYPES.LOCALVAR.getValue())) {
				final Combo localVarCombo = createLocalVarSelectionPane(composite, defaultValue, pattern, enabled, localVarType, paramKey);
				this.paramKeySWTObjectMap.put(paramKey, localVarCombo);
			} else if (valueType.equalsIgnoreCase(RETURN_TYPES.BOOLEAN.getValue())) {
				final Button booleanButton = createBooleanParamCheckBox(composite, defaultValue, enabled, paramKey);
				this.paramKeySWTObjectMap.put(paramKey, booleanButton);
			} else if (!isEmpty(allowedValues)) {
				final Combo allowedValuesCombo = createDropDown(composite, allowedValues, defaultValue, enabled, required, paramKey);
				this.paramKeySWTObjectMap.put(paramKey, allowedValuesCombo);
			} else if (valueType.equalsIgnoreCase(RETURN_TYPES.INTERFACE.getValue())) {
				final Combo interfaceCombo = createInterfaceSelectionPane(composite, defaultValue, pattern, enabled, required, paramKey);
				this.paramKeySWTObjectMap.put(paramKey, interfaceCombo);
			} else if (valueType.equalsIgnoreCase(RETURN_TYPES.ENUMERATION.getValue())) {
				final Combo enumCombo = createEnumSelectionPane(composite, defaultValue, pattern, enabled, required, paramKey);
				this.paramKeySWTObjectMap.put(paramKey, enumCombo);
			} else { //for string, and primitive types
				this.paramKeyParamTypeMap.put(paramKey, valueType);
				final Text plainText = createPlainTextPane(composite, defaultValue, pattern, enabled, paramKey, required, max, min); //chk if reqd is needed
				this.paramKeySWTObjectMap.put(paramKey, plainText);
			}

		}
		parent.getShell().setText(this.returnValuesData.getShellTitle());
		return parent;
	}

	@Override
	protected Control createButtonBar(final Composite parent) {
		return super.createButtonBar(parent);
	}

	@Override
	protected void okPressed() {

		if (this.returnValuesData == null) {
			this.returnValuesData = new ReturnValuesData();
		}
		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		for (int i = 0; i < this.size; i++) {
			final String placeHolderName = this.returnValuesData.getFastCodeAdditionalParams()[i].getName();
			boolean required = Boolean.valueOf(this.returnValuesData.getFastCodeAdditionalParams()[i].getRequired()); //Boolean.valueOf(this.labelFCAdditonalParamMap.get(placeHolderName).getRequired());
			final boolean enabled = Boolean.valueOf(this.returnValuesData.getFastCodeAdditionalParams()[i].getEnabled()); //Boolean.valueOf(this.labelFCAdditonalParamMap.get(placeHolderName).getEnabled());
			if (!enabled) {
				required = false;
			}

			final String label = EMPTY_STR;
			String additionalParamType = EMPTY_STR;
			/*if (this.label[i].getText().contains(LEFT_PAREN)) {
				label = this.label[i].getText().split(SPACE)[0].replace(ASTERISK, EMPTY_STR);
				additionalParamType = this.label[i].getText().split(SPACE)[1].replace(LEFT_PAREN, EMPTY_STR).replace(RIGHT_PAREN,
						EMPTY_STR);

			} else {
				label = this.label[i].getText().replace(ASTERISK, EMPTY_STR);
				additionalParamType = this.returnValuesData.getFastCodeAdditionalParams()[i].getReturnTypes().getValue();
			}*/
			additionalParamType = this.returnValuesData.getFastCodeAdditionalParams()[i].getReturnTypes().getValue();
			//final String placeHolderName = this.returnValuesData.getFastCodeAdditionalParams()[i].getName();
			final String allowedVal = this.returnValuesData.getFastCodeAdditionalParams()[i].getAllowedValues();
			required = Boolean.valueOf(this.returnValuesData.getFastCodeAdditionalParams()[i].getRequired()); //Boolean.valueOf(this.labelFCAdditonalParamMap.get(placeHolderName).getRequired());
			final String dependsOn = this.returnValuesData.getFastCodeAdditionalParams()[i].getDependsOn();
			if (!isEmpty(dependsOn)) {
				final Object parentSWTObjct = this.paramKeySWTObjectMap.get(dependsOn);
				if (parentSWTObjct instanceof Text) {
					if (isEmpty(((Text) parentSWTObjct).getText())) {
						required = false;
					}
				} else if (parentSWTObjct instanceof Combo) {
					if (isEmpty(((Combo) parentSWTObjct).getText())) {
						required = false;
					}
				} else if (parentSWTObjct instanceof Button) {
					if (!((Button) parentSWTObjct).getSelection()) {
						required = false;
					}

				}
			}
			final String pattern = this.returnValuesData.getFastCodeAdditionalParams()[i].getPattern(); //this.labelFCAdditonalParamMap.get(placeHolderName).getPattern();
			if (!additionalParamType.equals(RETURN_TYPES.LOCALVAR.getValue())) {
				/* else {
					continue;
				}*/
			}

			//final String placeHolderName= this.returnValuesData.getFastCodeAdditionalParams()[this.count].getName();
			if (additionalParamType.equals(RETURN_TYPES.CLASS.getValue())) {
				final String classComboText = ((Combo) this.paramKeySWTObjectMap.get(placeHolderName)).getText();
				if (required && isEmpty(classComboText)) {
					setErrorMessage("Please enter value for " + placeHolderName + ".");
					return;
				}

				if (this.currentClass != null && !isEmpty(classComboText) && classComboText.contains(ENCLOSING_CLASS_STR)) {
					if (!fastCodeCache.getTypeSet().contains(this.currentClass)) {
						fastCodeCache.getTypeSet().add(this.currentClass);
					}
					if (this.paramKeyParamValueMap.get(placeHolderName) == null) {
						this.paramKeyParamValueMap.put(placeHolderName, new FastCodeType(this.currentClass));
					}
				}
				this.returnValuesData.addReturnValuesMap(placeHolderName, isEmpty(((Combo) this.paramKeySWTObjectMap.get(placeHolderName))
						.getText()) ? null : this.paramKeyParamValueMap.get(placeHolderName));
				/*if (this.currentClass != null && !isEmpty(this.classNameCombo[i].getText())
						&& this.classNameCombo[i].getText().contains(ENCLOSING_CLASS_STR)) {
					if (!fastCodeCache.getTypeSet().contains(this.currentClass)) {
						fastCodeCache.getTypeSet().add(this.currentClass);
					}
					if (this.valuesObj[i] == null) {
						this.valuesObj[i] = new FastCodeType(this.currentClass);
					}
				}
				if (!valueAvailable(i, label, required)) {
					return;
				}
				this.returnValuesData.addReturnValuesMap(placeHolderName, isEmpty(this.classNameCombo[i].getText()) ? null
						: this.valuesObj[i]);*/

			} else if (additionalParamType.equals(RETURN_TYPES.FILE.getValue())) {
				final String fileComboText = ((Combo) this.paramKeySWTObjectMap.get(placeHolderName)).getText();

				if (required && isEmpty(fileComboText)) {
					setErrorMessage("Please enter value for " + placeHolderName + ".");
					return;
				}

				final String textValue = isEmpty(fileComboText) ? EMPTY_STR : ((FastCodeFile) this.paramKeyParamValueMap
						.get(placeHolderName)).getName(); //((Combo) this.paramKeySWTObjectMap.get(placeHolderName)).getText();
				if (!isEmpty(textValue) && !validatePattern(pattern, textValue)) {
					setErrorMessage("The selected value " + textValue + " does not match pattern " + pattern + " specified.");
					return;
				}

				if (this.currentFile != null && !isEmpty(fileComboText) && fileComboText.contains(ENCLOSING_FILE_STR)) {
					if (!fastCodeCache.getFileSet().contains(this.currentFile)) {
						fastCodeCache.getFileSet().add(this.currentFile);
					}
					if (!validatePattern(pattern, this.currentFile.getName())) {
						setErrorMessage("The selected value " + textValue + " does not match pattern " + pattern + " specified.");
						return;
					}
					if (this.paramKeyParamValueMap.get(placeHolderName) == null) {
						this.paramKeyParamValueMap.put(placeHolderName, new FastCodeFile(this.currentFile));
					}
				}

				this.returnValuesData.addReturnValuesMap(placeHolderName,
						isEmpty(fileComboText) ? null : this.paramKeyParamValueMap.get(placeHolderName));

				/*final String textValue = this.fileNameCombo[i].getText();
				if (!isEmpty(textValue) && !validatePattern(pattern, textValue)) {
					setErrorMessage("The selected value " + textValue + " does not match pattern " + pattern + " specified.");
					return;
				} else {

					}

				if (this.currentFile != null && !isEmpty(textValue) && textValue.contains(ENCLOSING_FILE_STR)) {
					if (!fastCodeCache.getFileSet().contains(this.currentFile)) {
						fastCodeCache.getFileSet().add(this.currentFile);
					}
					if (this.valuesObj[i] == null) {
						this.valuesObj[i] = new FastCodeFile(this.currentFile);
					}
				}
				if (!valueAvailable(i, label, required)) {
					return;
				}
				this.returnValuesData.addReturnValuesMap(placeHolderName, isEmpty(textValue) ? null : this.valuesObj[i]);*/

			} else if (additionalParamType.equals(RETURN_TYPES.FOLDER.getValue())) {
				final String textValue = ((Combo) this.paramKeySWTObjectMap.get(placeHolderName)).getText();
				if (required && isEmpty(textValue)) {
					setErrorMessage("Please enter value for " + placeHolderName + ".");
					return;
				}


				if (!isEmpty(textValue) && !validatePattern(pattern, textValue)) {
					setErrorMessage("The selected value " + textValue + " does not match pattern " + pattern + " specified.");
					return;
				}

				if (this.currentFolder != null && !isEmpty(textValue)
						&& textValue.contains(ENCLOSING_FOLDER_STR)) {
					if (!fastCodeCache.getFolderSet().contains(this.currentFolder)) {
						fastCodeCache.getFolderSet().add(this.currentFolder);
					}
					if (this.paramKeyParamValueMap.get(placeHolderName) == null) {
						this.paramKeyParamValueMap.put(placeHolderName, new FastCodeFolder(this.currentFolder));
					}
				}
				this.returnValuesData.addReturnValuesMap(placeHolderName, isEmpty(textValue) ? null : this.paramKeyParamValueMap.get(placeHolderName));

				/*final String textValue = this.folderNameCombo[i].getText();
				if (!isEmpty(textValue) && !validatePattern(pattern, textValue)) {
					setErrorMessage("The selected value " + textValue + " does not match pattern " + pattern + " specified.");
					return;
				}

				if (this.currentFolder != null && !isEmpty(this.folderNameCombo[i].getText())
						&& this.folderNameCombo[i].getText().contains(ENCLOSING_FOLDER_STR)) {
					if (!fastCodeCache.getFolderSet().contains(this.currentFolder)) {
						fastCodeCache.getFolderSet().add(this.currentFolder);
					}
					if (this.valuesObj[i] == null) {
						this.valuesObj[i] = new FastCodeFolder(this.currentFolder);
					}
				}
				if (!valueAvailable(i, label, required)) {
					return;
				}
				this.returnValuesData.addReturnValuesMap(placeHolderName, this.valuesObj[i]);*/

			} else if (additionalParamType.equals(RETURN_TYPES.PACKAGE.getValue())) {
				final String textValue = ((Combo) this.paramKeySWTObjectMap.get(placeHolderName)).getText();
				if (required && isEmpty(textValue)) {
					setErrorMessage("Please enter value for " + placeHolderName + ".");
					return;
				}

				if (this.currentPackage != null && !isEmpty(textValue)
						&& textValue.contains(ENCLOSING_PACKAGE_STR)) {


					if (!isEmpty(textValue) && !validatePattern(pattern, textValue)) {
						setErrorMessage("The selected value " + textValue + " does not match pattern " + pattern + " specified.");
						return;
					}
					if (!fastCodeCache.getPackageSet().contains(this.currentPackage)) {
						fastCodeCache.getPackageSet().add(this.currentPackage);
					}
					if (this.paramKeyParamValueMap.get(placeHolderName) == null) {
						this.paramKeyParamValueMap.put(placeHolderName, new FastCodePackage(this.currentPackage));
					}
				}
				this.returnValuesData.addReturnValuesMap(placeHolderName, isEmpty(textValue) ? null : this.paramKeyParamValueMap.get(placeHolderName));
				/*if (this.currentPackage != null && !isEmpty(this.packageCombo[i].getText())
						&& this.packageCombo[i].getText().contains(ENCLOSING_PACKAGE_STR)) {

					final String textValue = this.packageCombo[i].getText();
					if (!isEmpty(textValue) && !validatePattern(pattern, textValue)) {
						setErrorMessage("The selected value " + textValue + " does not match pattern " + pattern + " specified.");
						return;
					}

					if (!fastCodeCache.getPackageSet().contains(this.currentPackage)) {
						fastCodeCache.getPackageSet().add(this.currentPackage);
					}
					if (this.valuesObj[i] == null) {
						this.valuesObj[i] = new FastCodePackage(this.currentPackage);
					}
				}
				if (!valueAvailable(i, label, required)) {
					return;
				}
				this.returnValuesData.addReturnValuesMap(placeHolderName, this.valuesObj[i]);*/

			} else if (additionalParamType.equals(RETURN_TYPES.PROJECT.getValue())
					|| additionalParamType.equals(RETURN_TYPES.JAVAPROJECT.getValue())) {
				final String prjComboText = ((Combo) this.paramKeySWTObjectMap.get(placeHolderName)).getText();
				if (required && isEmpty(prjComboText)) {
					setErrorMessage("Please enter value for " + placeHolderName + ".");
					return;
				}
				this.returnValuesData.addReturnValuesMap(placeHolderName, isEmpty(prjComboText) ? null : this.paramKeyParamValueMap.get(placeHolderName));
				/*if (required && isEmpty(this.projectCombo[i].getText())) {
					setErrorMessage("please enter value for " + label);
					return;
				}
				this.returnValuesData.addReturnValuesMap(label, this.valuesObj[i]);*/
			} else if (additionalParamType.equals(RETURN_TYPES.LOCALVAR.getValue())) {
				final String localValText = ((Combo) this.paramKeySWTObjectMap.get(placeHolderName)).getText();
				if (required && isEmpty(localValText)) {
					setErrorMessage("Please enter value for " + placeHolderName + ".");
					return;
				}
				System.out.println(localValText);
				for (final FastCodeReturn fastCodeReturn : getEmptyListForNull(this.returnValuesData.getLocalVars())) {

					if (fastCodeReturn.getName().equals(this.paramKeyParamValueMap.get(placeHolderName))) {
						this.returnValuesData.addReturnValuesMap(placeHolderName, fastCodeReturn);
						break;
					}
				}

				/*this.returnValuesData.addReturnValuesMap(placeHolderName, isEmpty(((Combo) this.paraKeySWTObjectMap.get(placeHolderName))
						.getText()) ? null : this.paramKeyParamValueMap.get(placeHolderName));*/
				/*if (required && isEmpty(this.localVarCombo[i].getText())) {
					setErrorMessage("please enter value for " + label);
					return;
				}
				for (final FastCodeReturn fastCodeReturn : getEmptyListForNull(this.returnValuesData.getLocalVars())) {
					if (fastCodeReturn.getName().equals(this.valuesObj[i])) {
						this.returnValuesData.addReturnValuesMap(placeHolderName, fastCodeReturn);
						break;
					}
				}*/

			} else if (additionalParamType.equals(RETURN_TYPES.BOOLEAN.getValue())) {
				this.returnValuesData.addReturnValuesMap(
						placeHolderName,
						!((Button) this.paramKeySWTObjectMap.get(placeHolderName)).getSelection()
								|| this.paramKeyParamValueMap.get(placeHolderName) == null ? false : this.paramKeyParamValueMap
								.get(placeHolderName));
				/*this.returnValuesData.addReturnValuesMap(placeHolderName, !this.booleanParam[i].getSelection()
						|| this.valuesObj[i] == null ? false : this.valuesObj[i]);*/

			} else if (additionalParamType.equals(RETURN_TYPES.INTERFACE.getValue())) {
				final String interfaceComboText = ((Combo) this.paramKeySWTObjectMap.get(placeHolderName)).getText();
				if (required && isEmpty(interfaceComboText )) {
					setErrorMessage("Please enter value for " + placeHolderName + ".");
					return;
				}

				if (this.currentClass != null && !isEmpty(interfaceComboText)
						&& interfaceComboText.contains(ENCLOSING_INTERFACE_STR)) {
					if (!fastCodeCache.getTypeSet().contains(this.currentInterface)) {
						fastCodeCache.getTypeSet().add(this.currentInterface);
					}
					if (this.paramKeyParamValueMap.get(placeHolderName) == null) {
						this.paramKeyParamValueMap.put(placeHolderName, new FastCodeType(this.currentInterface));
					}
				}
				this.returnValuesData.addReturnValuesMap(placeHolderName, isEmpty(((Combo) this.paramKeySWTObjectMap.get(placeHolderName))
						.getText()) ? null : this.paramKeyParamValueMap.get(placeHolderName));

				/*if (this.currentInterface != null && !isEmpty(this.interfaceNameCombo[i].getText())
						&& this.interfaceNameCombo[i].getText().contains(ENCLOSING_INTERFACE_STR)) {
					final String textValue = this.interfaceNameCombo[i].getText();
					if (!isEmpty(textValue) && !validatePattern(pattern, textValue)) {
						setErrorMessage("The selected value " + textValue + " does not match pattern " + pattern + " specified.");
						return;
					}
					if (!fastCodeCache.getTypeSet().contains(this.currentInterface)) {
						fastCodeCache.getTypeSet().add(this.currentInterface);
					}
					if (this.valuesObj[i] == null) {
						this.valuesObj[i] = new FastCodeType(this.currentInterface);
					}
				}
				if (!valueAvailable(i, label, required)) {
					return;
				}
				this.returnValuesData.addReturnValuesMap(placeHolderName, isEmpty(this.interfaceNameCombo[i].getText()) ? null
						: this.valuesObj[i]);*/
			} else if (additionalParamType.equals(RETURN_TYPES.ENUMERATION.getValue())) {
				final String enumComboText = ((Combo) this.paramKeySWTObjectMap.get(placeHolderName)).getText();
				if (required && isEmpty(enumComboText )) {
					setErrorMessage("Please enter value for " + placeHolderName + ".");
					return;
				}
				if (!fastCodeCache.getTypeSet().contains(((FastCodeType) this.paramKeyParamValueMap.get(placeHolderName)).getiType())) {
					fastCodeCache.getTypeSet().add(((FastCodeType) this.paramKeyParamValueMap.get(placeHolderName)).getiType());
				}
				this.returnValuesData.addReturnValuesMap(placeHolderName, isEmpty(((Combo) this.paramKeySWTObjectMap.get(placeHolderName))
						.getText()) ? null : this.paramKeyParamValueMap.get(placeHolderName));
				/*if (!valueAvailable(i, label, required)) {
					return;
				}

				if (!fastCodeCache.getTypeSet().contains(((FastCodeType) this.valuesObj[i]).getiType())) {
					fastCodeCache.getTypeSet().add(((FastCodeType) this.valuesObj[i]).getiType());
				}
				this.returnValuesData.addReturnValuesMap(placeHolderName, this.valuesObj[i]);*/
			} else if (!isEmpty(allowedVal)) {
				this.returnValuesData.addReturnValuesMap(placeHolderName, this.paramKeyParamValueMap.get(placeHolderName));
			} else { //if (this.returnValuesData.getFastCodeAdditionalParams()[i].equals(RETURN_TYPES.STRING.getValue())) {
				if (ReturnValuesDialog.this.returnValuesData.isUnitTest()) {
					if (isEmpty(((Text) this.paramKeySWTObjectMap.get(placeHolderName)).getText())) {
						setErrorMessage("please enter value for " + placeHolderName /*this.label[i].getText().replace(ASTERISK, EMPTY_STR)*/);
						return;
					}
				}

				if (required && placeHolderName.contains(STARTLINE_ENDLINE)) {
					//if (required && this.label[i].getText().split(SPACE)[0].replace(ASTERISK, EMPTY_STR).contains(STARTLINE_ENDLINE)) {
					final String lines = ((Text) this.paramKeySWTObjectMap.get(placeHolderName)).getText();//this.valueText[i].getText();
					final String[] stringArray = lines.split(" ");
					if (!lines.matches(".*\\d.*") || !lines.contains(HYPHEN)) {
						//if (!this.valueText[i].getText().matches(".*\\d.*") || !this.valueText[i].getText().contains(HYPHEN)) {
						setErrorMessage("please provide startLine-endline in format(1-10 20-30) with space seperated");
						return;
					}
					for (int x = 0; x < stringArray.length; x++) {

						final String startLine = stringArray[x].substring(0, stringArray[x].indexOf('-'));
						final String endLine = stringArray[x].substring(stringArray[x].indexOf('-') + 1);
						if (!startLine.matches(".*\\d.*")) {
							setErrorMessage("startLine doesnot contain numeric value, please provide in format(1-10 20-30)");
							return;
						}
						if (!endLine.matches(".*\\d.*")) {
							setErrorMessage("endLine doesnot contain numeric value, please provide in format(1-10 20-30)");
							return;
						}
						if (startLine.equals("0") || endLine.equals("0")) {
							setErrorMessage("Please enter valid line number, please provide in format(1-10 20-30)");
							return;
						}
					}
				}

				if (required && isEmpty(((Text) this.paramKeySWTObjectMap.get(placeHolderName)).getText())) {
					setErrorMessage("please enter value for " + placeHolderName/*this.label[i].getText().replace(ASTERISK, EMPTY_STR)*/);
					return;
				}
				if (this.returnValuesData.getFastCodeAdditionalParams()[i].getReturnTypes().getValue() == INT) {
					this.returnValuesData.addReturnValuesMap(this.returnValuesData.getFastCodeAdditionalParams()[i].getName(), new Integer(
							((Text) this.paramKeySWTObjectMap.get(placeHolderName)).getText()));
				} else if (this.returnValuesData.getFastCodeAdditionalParams()[i].getReturnTypes().getValue() == RETURN_TYPES.INTRANGE
						.getValue()) {
					IntRange intRangeObj = null;
					try {
						intRangeObj = parseIntrange(((Text) this.paramKeySWTObjectMap.get(placeHolderName)).getText());//this.valueText[i].getText());

						if (Integer.parseInt(this.returnValuesData.getFastCodeAdditionalParams()[i].getMax()) != 0) {
							validateIntRange(this.returnValuesData.getFastCodeAdditionalParams()[i].getMin(),
									this.returnValuesData.getFastCodeAdditionalParams()[i].getMax(),
									(Text) this.paramKeySWTObjectMap.get(placeHolderName)/*this.valueText[i]*/, intRangeObj);
						}
					} catch (final Exception ex) {
						setErrorMessage(ex.getMessage());
						((Text) this.paramKeySWTObjectMap.get(placeHolderName)).setFocus();
						return;
					}
					this.returnValuesData.addReturnValuesMap(this.returnValuesData.getFastCodeAdditionalParams()[i].getName(), intRangeObj);
				} else {
					this.returnValuesData.addReturnValuesMap(this.returnValuesData.getFastCodeAdditionalParams()[i].getName(),
							((Text) this.paramKeySWTObjectMap.get(placeHolderName)).getText());
				}
			}
			/*
			}
			* else if (this.arrayData) {
			* this.returnValuesData.addReturnValuesMap
			* (this.label[i].getText(), this.arrayValue); }
			*/
		}
		super.okPressed();
	}

	/**
	 * @param i
	 * @param label
	 * @param required
	 */
	/*public boolean valueAvailable(final int i, final String label, final boolean required) {
		if (required && this.valuesObj[i] == null) {
			setErrorMessage("please enter value for " + label);
			return false;
		}
		return true;
	}*/

	/**
	 * @param pattern
	 * @param textValue
	 */
	public boolean validatePattern(final String pattern, final String textValue) {
		if (!isEmpty(pattern)) {
			if (!textValue.matches(pattern.replace(ASTERISK, ".*"))) {
				return false;
			} else {
				return true;
			}
		}
		return true;
	}

	/**
	 * @param parent
	 * @param required
	 * @param paramKey
	 * @param enabled
	 * @param pattern
	 * @param defaultValue
	 * @param min
	 * @param max
	 * @return
	 */
	private Text createPlainTextPane(final Composite parent, final String defaultValue, final String pattern, final boolean enabled,
			final String paramKey, final boolean required, final String max, final String min) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setSize(300, 500);

		/*final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText(required ? ASTERISK + labelName : labelName);
		label.setLayoutData(gridDataLabel);*/

		/*this.size = this.returnValuesData.getFastCodeAdditionalParams().length;
		if (this.returnValuesData.getEditorPart() != null) {
			this.compilationUnit = getCompilationUnitFromEditor(this.returnValuesData.getEditorPart());
		}

		*/

		/*this.label = new Label[this.size];
		this.valueText = new Text[this.size]; //new Text[noStringValues];
		this.classNameCombo = new Combo[this.size]; //[noClassValues];
		this.fileNameCombo = new Combo[this.size]; //[noFileValues];
		this.packageCombo = new Combo[this.size]; //[noPackageValues];
		this.folderNameCombo = new Combo[this.size]; //[noFolderValues];
		this.projectCombo = new Combo[this.size]; //[noProjectValues];
		this.localVarCombo = new Combo[this.size];
		this.booleanParam = new Button[this.size];
		this.interfaceNameCombo = new Combo[this.size];
		this.enumNameCombo = new Combo[this.size];
		this.allowedValuesCombo = new Combo[this.size];*/

		//this.valuesObj = new Object[this.size];

		/*for (int count = 0; count < this.size; count++) {
			String labelName = this.returnValuesData.getFastCodeAdditionalParams()[count].getLabel();
			final String valueType = this.returnValuesData.getFastCodeAdditionalParams()[count].getReturnTypes().getValue();
			boolean required = Boolean.valueOf(this.returnValuesData.getFastCodeAdditionalParams()[count].getRequired());
			final String defaultValue = this.returnValuesData.getFastCodeAdditionalParams()[count].getDefaultValue();
			final String pattern = this.returnValuesData.getFastCodeAdditionalParams()[count].getPattern();
			final String allowedValues = this.returnValuesData.getFastCodeAdditionalParams()[count].getAllowedValues();
			final String project = this.returnValuesData.getFastCodeAdditionalParams()[count].getProject();
			final boolean enabled = Boolean.valueOf(this.returnValuesData.getFastCodeAdditionalParams()[count].getEnabled());
			final String max = this.returnValuesData.getFastCodeAdditionalParams()[count].getMax();
			final String min = this.returnValuesData.getFastCodeAdditionalParams()[count].getMin();
			final String type = this.returnValuesData.getFastCodeAdditionalParams()[count].getType();
			final String dependsOn = this.returnValuesData.getFastCodeAdditionalParams()[count].getDependsOn();
			FastCodeAdditionalParams parentParameter = null;
			if (this.labelFCAdditonalParamMap.containsKey(dependsOn)) {
				parentParameter = this.labelFCAdditonalParamMap.get(dependsOn);
			}
			if (!enabled) {
				required = false;
			}
			if (isEmpty(labelName)) {
				labelName = this.returnValuesData.getFastCodeAdditionalParams()[count].getName();
				final String labelNameSuffix = SPACE + LEFT_PAREN + valueType + RIGHT_PAREN;
				if (!(valueType.equals(RETURN_TYPES.STRING.getValue()) || valueType.equals(RETURN_TYPES.BOOLEAN.getValue()))) {
					labelName = labelName + labelNameSuffix;
				}
			}
			this.labelFCAdditonalParamMap.put(this.returnValuesData.getFastCodeAdditionalParams()[count].getName(),
					this.returnValuesData.getFastCodeAdditionalParams()[count]);

			final GridData gridDataLabel = new GridData();
			this.label[count] = new Label(composite, SWT.NONE);
			this.label[count].setText(required ? ASTERISK + labelName : labelName);
			this.label[count].setLayoutData(gridDataLabel);*/

		/*if (valueType.equals(RETURN_TYPES.CLASS.getValue())) {
			//createClassSelectionPane(composite, count, project, pattern, enabled);
		} else if (valueType.equals(RETURN_TYPES.FILE.getValue())) {
			//createFileSelectionPane(composite, count, project, pattern, enabled);
		} else if (valueType.equals(RETURN_TYPES.FOLDER.getValue())) {
			//createFolderSelectionPane(composite, count, defaultValue, project, pattern, enabled);
		} else if (valueType.equals(RETURN_TYPES.PACKAGE.getValue())) {
			//createPackageSelectionPane(composite, count, defaultValue, pattern, enabled);
		} else if (valueType.equalsIgnoreCase(RETURN_TYPES.PROJECT.getValue())
				|| valueType.equalsIgnoreCase(RETURN_TYPES.JAVAPROJECT.getValue())) {
			//createProjectSelectionPane(composite, valueType, count, defaultValue, pattern, enabled);
		} else if (valueType.equalsIgnoreCase(RETURN_TYPES.LOCALVAR.getValue())) {
			//createLocalVarSelectionPane(composite, count, defaultValue, pattern, enabled, type);
		} else if (valueType.equalsIgnoreCase(RETURN_TYPES.BOOLEAN.getValue())) {
			//createBooleanParamCheckBox(composite, count, defaultValue, enabled);
		} else if (!isEmpty(allowedValues)) {
			//createDropDown(composite, count, allowedValues, defaultValue, required, enabled);
		} else if (valueType.equalsIgnoreCase(RETURN_TYPES.INTERFACE.getValue())) {
			//createInterfaceSelectionPane(composite, count, defaultValue, pattern, enabled);
		} else if (valueType.equalsIgnoreCase(RETURN_TYPES.ENUMERATION.getValue())) {
			//craeteEnumSelectionPane(composite, count, defaultValue, pattern, enabled);
		}*/
		/*
			* else if (valueType.matches("[*\\s+*]")) { this.arrayData = true;
			* final String values = valueType.substring(1, valueType.length());
			* createComboBoxPane(composite, values); }
			*/
		/*else {*/
		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		final Text stringText = new Text(composite, SWT.BORDER);
		stringText.setLayoutData(gridDataText);
		gridDataText.minimumWidth = 250;
		stringText.setEnabled(enabled);
		stringText.setText(defaultValue);

		//stringText.setToolTipText(this.valueType);

		final boolean req = required;
		stringText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				final Text text = (Text) e.widget;
				final String value = text.getText();
				final String dataType = ReturnValuesDialog.this.paramKeyParamTypeMap.get(paramKey);
				if (req) {
					if (isEmpty(value)) {
						ReturnValuesDialog.this.setErrorMessage("Please enter a value.");
						text.setFocus();
					} else {
						ReturnValuesDialog.this.setErrorMessage(ReturnValuesDialog.this.defaultMessage);
					}
				}
				//if (text.getToolTipText().equals("double") || text.getToolTipText().equals("Double")) {
				if (!isEmpty(value)) {
					if (dataType.equals("double") || dataType.equals("Double")) {
						try {
							Double.parseDouble(value);
							ReturnValuesDialog.this.setErrorMessage(ReturnValuesDialog.this.defaultMessage);
						} catch (final NumberFormatException exception) {
							ReturnValuesDialog.this.setErrorMessage("Please enter a " + dataType + " value.");
							text.setFocus();
						}
					} else if (dataType.equals("int") || dataType.equals("Integer")) {
						//else if (text.getToolTipText().equals("int") || text.getToolTipText().equals("Integer")) {
						try {
							final int intVal = Integer.parseInt(value);
							if (!isEmpty(min)) {
								final int minVal = Integer.parseInt(min);
								if (intVal < minVal) {
									ReturnValuesDialog.this.setErrorMessage("Please enter a value greater than " + min + ".");
									text.setFocus();
									return;
								} else {
									ReturnValuesDialog.this.setErrorMessage(ReturnValuesDialog.this.defaultMessage);
								}
							}
							if (!isEmpty(max)) {
								final int maxVal = Integer.parseInt(max);
								if (intVal > maxVal) {
									ReturnValuesDialog.this.setErrorMessage("Please enter a value lesser than " + max + ".");
									text.setFocus();
									return;
								} else {
									ReturnValuesDialog.this.setErrorMessage(ReturnValuesDialog.this.defaultMessage);
								}
							}
							//ReturnValuesDialog.this.setErrorMessage(ReturnValuesDialog.this.defaultMessage);
						} catch (final NumberFormatException exception) {
							ReturnValuesDialog.this.setErrorMessage("Please enter a " + dataType + " value.");
							text.setFocus();
						}
					} else if (dataType.equals("float") || dataType.equals("Float")) {
						//else if (text.getToolTipText().equals("float") || text.getToolTipText().equals("Float")) {
						try {
							Float.parseFloat(value);
							ReturnValuesDialog.this.setErrorMessage(ReturnValuesDialog.this.defaultMessage);
						} catch (final NumberFormatException exception) {
							ReturnValuesDialog.this.setErrorMessage("Please enter a " + dataType + " value.");
							text.setFocus();
						}
					} else if (dataType.equals("boolean") || dataType.equals("Boolean")) {
						//else if (text.getToolTipText().equals("boolean") || text.getToolTipText().equals("Boolean")) {
						if (value.equalsIgnoreCase(TRUE_STR) || value.equalsIgnoreCase(FALSE_STR)) {
							ReturnValuesDialog.this.setErrorMessage(ReturnValuesDialog.this.defaultMessage);
						} else {
							ReturnValuesDialog.this.setErrorMessage("Please enter a " + dataType + " value.");
							text.setFocus();
						}

					} else if (dataType.equals("char")) {
						//else if (text.getToolTipText().equals("char")) {
						if (value.length() == 1) {
							ReturnValuesDialog.this.setErrorMessage(ReturnValuesDialog.this.defaultMessage);
						} else {
							ReturnValuesDialog.this.setErrorMessage("Please enter a " + dataType + " value.");
							text.setFocus();
						}
					} else if (dataType.equals("long")) {
						//else if (text.getToolTipText().equals("long")) {
						try {
							Long.parseLong(value);
							ReturnValuesDialog.this.setErrorMessage(ReturnValuesDialog.this.defaultMessage);
						} catch (final NumberFormatException exception) {
							ReturnValuesDialog.this.setErrorMessage("Please enter a " + dataType + " value.");
							text.setFocus();
						}
					} else if (dataType.equals("short")) {
						//else if (text.getToolTipText().equals("short")) {
						try {
							Short.parseShort(value);
							ReturnValuesDialog.this.setErrorMessage(ReturnValuesDialog.this.defaultMessage);
						} catch (final NumberFormatException exception) {
							ReturnValuesDialog.this.setErrorMessage("Please enter a " + dataType + " value.");
							text.setFocus();
						}
					} else if (dataType.equals("byte")) {
						//else if (text.getToolTipText().equals("byte")) {
						try {
							Byte.parseByte(value);
							ReturnValuesDialog.this.setErrorMessage(ReturnValuesDialog.this.defaultMessage);
						} catch (final NumberFormatException exception) {
							ReturnValuesDialog.this.setErrorMessage("Please enter a " + dataType + " value.");
							text.setFocus();
						}
					}
				}
				if (dataType.equals(RETURN_TYPES.INTRANGE.getValue())) {
					//(text.getToolTipText().equals(RETURN_TYPES.INTRANGE.getValue())) {
					if (!isEmpty(max)) {
						if (Integer.parseInt(max) != 0) {
							try {
								if (!isEmpty(value)) {
									final IntRange intRange = parseIntrange(value);

									validateIntRange(min, max, text, intRange);
								}
							} catch (final Exception ex) {
								ReturnValuesDialog.this.setErrorMessage(ex.getMessage());
							}
						}
					}
				}
				enableOrDisableChildren(paramKey);
			}
		});
		//}
		//}
		return stringText;
	}

	/**
	 * @param max
	 * @param min
	 * @param text
	 * @param intRange
	 */
	public void validateIntRange(final String min, final String max, final Text text, final IntRange intRange) {
		if (intRange == null) {
			return;
		}
		if (!isEmpty(min)) {
			final int minVal = Integer.parseInt(min);
			if (intRange.getStart() < minVal) {
				setErrorMessage("Please enter a value greater than " + min + " for start.");
				text.setFocus();
				return;
			} else {
				setErrorMessage(ReturnValuesDialog.this.defaultMessage);
			}
		}
		if (!isEmpty(max)) {
			final int maxVal = Integer.parseInt(max);
			if (intRange.getEnd() > maxVal) {
				setErrorMessage("Please enter a value lesser than " + max + " for end.");
				text.setFocus();
				return;
			} else {
				setErrorMessage(ReturnValuesDialog.this.defaultMessage);
			}
		}
	}

	protected IntRange parseIntrange(final String value) throws Exception {
		final StringBuilder max = new StringBuilder();
		final StringBuilder min = new StringBuilder();
		final StringBuilder step = new StringBuilder(ZERO_STRING);
		boolean maxOver = false;
		boolean minOver = false;
		for (final char ch : value.toCharArray()) {

			if (ch == HYPHEN.toCharArray()[0]) {
				if (!isEmpty(min.toString())) {
					minOver = true;
				}
				continue;
			}
			if (ch == SPACE_CHAR) {
				if (!isEmpty(max.toString())) {
					maxOver = true;
				}
				continue;
			}
			if (!minOver) {
				min.append(ch);
				continue;
			}
			if (!maxOver) {
				max.append(ch);
				continue;
			}
			step.append(ch);

		}
		IntRange intRange = null;
		if (!isEmpty(min.toString()) && !isEmpty(max.toString())) {
			intRange = new IntRange(Integer.parseInt(min.toString()), Integer.parseInt(max.toString()), Integer.parseInt(step.toString()));
		}
		return intRange;
	}

	/**
	 * @param parent
	 * @param allowedValues
	 * @param defaultValue
	 * @param required
	 * @param required2
	 * @param paramKey
	 */
	private Combo createDropDown(final Composite parent, final String allowedValues, final String defaultValue, final boolean enabled,
			final boolean required, final String paramKey) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		/*final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText(required ? ASTERISK + labelName : labelName);
		label.setLayoutData(gridDataLabel);*/

		final GridData gridDataCombo = new GridData();
		gridDataCombo.grabExcessHorizontalSpace = true;

		final Combo allowedValuesCombo = new Combo(composite, SWT.NONE | SWT.READ_ONLY);
		allowedValuesCombo.setSize(200, 20);
		allowedValuesCombo.setLayoutData(gridDataCombo);
		allowedValuesCombo.setEnabled(enabled);
		gridDataCombo.minimumWidth = 500;

		if (!required) {
			allowedValuesCombo.add(EMPTY_STR);
		}
		for (final String allowedValue : allowedValues.split(SPACE)) {
			allowedValuesCombo.add(allowedValue);
		}

		if (!isEmpty(defaultValue)) {
			allowedValuesCombo.setText(defaultValue);
		}
		ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, defaultValue);
		//this.valuesObj[count] = defaultValue;
		allowedValuesCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String selectedValue = ((Combo) event.widget).getText();
				ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, selectedValue);
				//New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = selectedValue;
				enableOrDisableChildren(paramKey);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
		return allowedValuesCombo;
	}

	/**
	 * @param parent
	 * @param defaultValue
	 * @param pattern
	 * @param enabled
	 * @param required
	 * @param required
	 * @param paramKey
	 */
	private Combo createEnumSelectionPane(final Composite parent, final String defaultValue, final String pattern, final boolean enabled,
			final boolean required, final String paramKey) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		/*final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText(required ? ASTERISK + labelName : labelName);
		label.setLayoutData(gridDataLabel);*/

		final GridData gridDataCombo = new GridData();
		gridDataCombo.grabExcessHorizontalSpace = true;

		final Combo enumNameCombo = new Combo(composite, SWT.READ_ONLY);// new
		// Text(composite,
		// SWT.BORDER);
		enumNameCombo.setSize(200, 20);
		enumNameCombo.setLayoutData(gridDataCombo);
		enumNameCombo.setEnabled(enabled);
		gridDataCombo.minimumWidth = 500;
		if (!required) {
			enumNameCombo.add(EMPTY_STR);
		}
		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (!fastCodeCache.getTypeSet().isEmpty()) {
			for (final IType type : fastCodeCache.getTypeSet()) {
				try {
					if (type.isEnum()) {
						enumNameCombo.add(type.getFullyQualifiedName());
					}
				} catch (final Exception ex) {

				}
			}
		}
		enumNameCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String selectedEnumName = ((Combo) event.widget).getText();
				try {
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.isEnum()) {
								if (type.getFullyQualifiedName().equals(selectedEnumName)) {
									ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeType(type));
									/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = new FastCodeType(
											type);*/
								}
							}
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
				enableOrDisableChildren(paramKey);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
		enumNameCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				final String inputEnumName = ((Combo) e.widget).getText();
				if (!isEmpty(inputEnumName)) {
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							try {
								if (type.isEnum()) {
									if (type.getFullyQualifiedName().equals(inputEnumName)) {
										return;
									}
								}
							} catch (final JavaModelException ex) {
								ex.printStackTrace();
							}
						}
					}
					//					try {
					//						final IType inputEnumType = getTypeFromWorkspace(inputEnumName);
					//						if (inputEnumType != null) {
					//							ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeType(inputEnumType));
					//							/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Combo) e.widget).getToolTipText())] = new FastCodeType(
					//									inputEnumType);*/
					//							if (!fastCodeCache.getTypeSet().contains(inputEnumType)) {
					//								fastCodeCache.getTypeSet().add(inputEnumType);
					//							}
					//						} else {
					//							setErrorMessage("Enumeration does not exist,Please enter an enumeration  name ");
					//						}
					//					} catch (final Exception ex) {
					//						ex.printStackTrace();
					//					}
					enableOrDisableChildren(paramKey);
				}
			}

			@Override
			public void focusGained(final FocusEvent arg0) {

			}
		});
		enumNameCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent event) {
				if (required && isEmpty(((Combo) event.widget).getText())) {
					setErrorMessage("Please choose a value for " + paramKey);
					enableOrDisableChildren(paramKey);
				} else {
					setErrorMessage(ReturnValuesDialog.this.defaultMessage);
				}

			}
		});

		final GridData gridDataButton = new GridData();
		final Button browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse");
		browse.setLayoutData(gridDataButton);
		browse.setEnabled(enabled);
		this.browseButtonComboMap.put(browse, enumNameCombo);
		browse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Button b = (Button) e.getSource();
				SelectionDialog selectionDialog = null;
				try {
					selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
							SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ENUMS, false, EMPTY_STR);

					selectionDialog.setTitle(ReturnValuesDialog.this.selectMsg + "Enumeration");
					selectionDialog.setMessage(ReturnValuesDialog.this.selectMsg + "enumeration");

					if (selectionDialog.open() == CANCEL) {
						return;
					}

					final IType browsedEnumType = (IType) selectionDialog.getResult()[0];
					boolean addItem = true;
					if (enumNameCombo.getItems() != null) {
						for (final String existingEnum : enumNameCombo.getItems()) {
							if (existingEnum.equals(browsedEnumType.getFullyQualifiedName())) {
								if (!existingEnum.equals(enumNameCombo.getText())) {
									enumNameCombo.select(enumNameCombo.indexOf(existingEnum));
								}
								addItem = false;
								break;
							}
						}
					}
					if (addItem) {
						enumNameCombo.add(browsedEnumType.getFullyQualifiedName());
						enumNameCombo.select(enumNameCombo.getItemCount() - 1);
					}
					ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeType(browsedEnumType));
					/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Button) e.widget).getToolTipText())] = new FastCodeType(
							browsedEnumType);*/
					if (!fastCodeCache.getTypeSet().contains(selectionDialog.getResult()[0])) {
						fastCodeCache.getTypeSet().add((IType) selectionDialog.getResult()[0]);
					}
				} catch (final JavaModelException ex) {
					ex.printStackTrace();
				}
				enableOrDisableChildren(paramKey);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
		return enumNameCombo;
	}

	/**
	 * @param composite
	 * @param pattern
	 * @param defaultValue
	 * @param enabled
	 * @param required
	 * @param required
	 * @param paramKey
	 */
	private Combo createInterfaceSelectionPane(final Composite parent, final String defaultValue, final String pattern,
			final boolean enabled, final boolean required, final String paramKey) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		/*final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText(required ? ASTERISK + labelName : labelName);
		label.setLayoutData(gridDataLabel);*/

		final GridData gridDataCombo = new GridData();
		gridDataCombo.grabExcessHorizontalSpace = true;

		final Combo interfaceCombo = new Combo(composite, SWT.READ_ONLY);// new
		// Text(composite,
		// SWT.BORDER);
		interfaceCombo.setSize(200, 20);
		interfaceCombo.setLayoutData(gridDataCombo);
		interfaceCombo.setEnabled(enabled);
		gridDataCombo.minimumWidth = 500;
		if (!required) {
			interfaceCombo.add(EMPTY_STR);
		}
		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		try {
			if (this.compilationUnit != null && this.compilationUnit.getPrimary().findPrimaryType().isInterface()) {
				this.currentInterface = this.compilationUnit.getPrimary().findPrimaryType();
				interfaceCombo.add(ENCLOSING_INTERFACE_STR + HYPHEN + this.currentInterface.getFullyQualifiedName());
				//this.interfaceNameCombo[count].select(0);
			}
		} catch (final JavaModelException ex1) {
			ex1.printStackTrace();
		}

		if (!fastCodeCache.getTypeSet().isEmpty()) {
			for (final IType type : fastCodeCache.getTypeSet()) {
				try {
					if (type.isInterface()) {
						if (this.currentInterface != null && this.currentInterface.equals(type)) {
							continue;
						}
						boolean addItem = true;
						if (interfaceCombo.getItems() != null) {
							for (final String existingInterface : interfaceCombo.getItems()) {
								if (existingInterface.contains(ENCLOSING_INTERFACE_STR)) {
									continue;
								}
								if (existingInterface.equals(type.getFullyQualifiedName())) {
									addItem = false;
									break;
								}
							}
							if (addItem) {
								interfaceCombo.add(type.getFullyQualifiedName());
							}
						}
					}
				} catch (final JavaModelException ex) {
					ex.printStackTrace();
				}
			}
		}
		interfaceCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				String selectedInterfaceName = ((Combo) event.widget).getText(); //ReturnValuesDialog.this.classNameCombo.getText();
				try {
					if (selectedInterfaceName.contains(ENCLOSING_INTERFACE_STR)) {
						selectedInterfaceName = ReturnValuesDialog.this.currentInterface.getFullyQualifiedName();
					}
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.isInterface()) {
								if (type.getFullyQualifiedName().equals(selectedInterfaceName)) {
									ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeType(type));
									/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = new FastCodeType(
											type);*/
								}
							}
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
				enableOrDisableChildren(paramKey);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
		interfaceCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				String inputInterfaceName = ((Combo) e.widget).getText();
				if (!isEmpty(inputInterfaceName)) {
					if (inputInterfaceName.contains(ENCLOSING_INTERFACE_STR)) {
						inputInterfaceName = ReturnValuesDialog.this.currentInterface.getFullyQualifiedName();
					}
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							try {
								if (type.isInterface()) {
									if (type.getFullyQualifiedName().equals(inputInterfaceName)) {
										return;
									}
								}
							} catch (final JavaModelException ex) {
								ex.printStackTrace();
							}
						}
					}
					//					try {
					//						final IType inputInterfaceType = getTypeFromWorkspace(inputInterfaceName);
					//						if (inputInterfaceType != null) {
					//							ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeType(inputInterfaceType));
					//							/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Combo) e.widget).getToolTipText())] = new FastCodeType(
					//									inputInterfaceType);*/
					//							if (!fastCodeCache.getTypeSet().contains(inputInterfaceType)) {
					//								fastCodeCache.getTypeSet().add(inputInterfaceType);
					//							}
					//						} else {
					//							setErrorMessage("Interface does not exist,Please enter an existing interface name ");
					//						}
					//					} catch (final Exception ex) {
					//						ex.printStackTrace();
					//					}
					enableOrDisableChildren(paramKey);
				}
			}

			@Override
			public void focusGained(final FocusEvent arg0) {

			}
		});
		interfaceCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent event) {
				if (required && isEmpty(((Combo) event.widget).getText())) {
					setErrorMessage("Please choose a value for " + paramKey);
					enableOrDisableChildren(paramKey);
				} else {
					setErrorMessage(ReturnValuesDialog.this.defaultMessage);
				}
			}
		});

		final GridData gridDataButton = new GridData();
		final Button browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse");
		browse.setLayoutData(gridDataButton);
		browse.setEnabled(enabled);
		this.browseButtonComboMap.put(browse, interfaceCombo);
		browse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Button b = (Button) e.getSource();
				SelectionDialog selectionDialog = null;
				try {
					selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
							SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_INTERFACES, false, EMPTY_STR);

					selectionDialog.setTitle(ReturnValuesDialog.this.selectMsg + "Interface");
					selectionDialog.setMessage(ReturnValuesDialog.this.selectMsg + "Interface");

					if (selectionDialog.open() == CANCEL) {
						return;
					}

					final IType browsedInterfaceType = (IType) selectionDialog.getResult()[0];
					boolean addItem = true;
					if (interfaceCombo.getItems() != null) {
						for (final String existingInterface : interfaceCombo.getItems()) {
							if (existingInterface.equals(browsedInterfaceType.getFullyQualifiedName())) {
								if (!existingInterface.equals(interfaceCombo.getText())) {
									interfaceCombo.select(interfaceCombo.indexOf(existingInterface));
								}
								addItem = false;
								break;
							}
						}
					}
					if (addItem) {
						interfaceCombo.add(browsedInterfaceType.getFullyQualifiedName());
						interfaceCombo.select(interfaceCombo.getItemCount() - 1);
					}
					ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeType(browsedInterfaceType));
					/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Button) e.widget).getToolTipText())] = new FastCodeType(
							browsedInterfaceType);*/
					if (!fastCodeCache.getTypeSet().contains(selectionDialog.getResult()[0])) {
						fastCodeCache.getTypeSet().add((IType) selectionDialog.getResult()[0]);
					}
				} catch (final JavaModelException ex) {
					ex.printStackTrace();
				}
				enableOrDisableChildren(paramKey);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
		return interfaceCombo;
	}

	/**
	 * @param parent
	 * @param defaultValue
	 * @param enabled
	 * @param required
	 * @param paramKey
	 */
	private Button createBooleanParamCheckBox(final Composite parent, final String defaultValue, final boolean enabled,
			final String paramKey) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);

		/*final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText(required ? ASTERISK + labelName : labelName);
		label.setLayoutData(gridDataLabel);*/

		final Button booleanParam = new Button(composite, SWT.CHECK);
		booleanParam.setSelection(Boolean.valueOf(defaultValue));
		ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, Boolean.valueOf(defaultValue));
		//this.valuesObj[count] = Boolean.valueOf(defaultValue);
		booleanParam.setEnabled(enabled);
		booleanParam.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Button b = (Button) event.getSource();
				ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, b.getSelection());
				enableOrDisableChildren(paramKey);
				/*New__ReturnValuesDialog.this.New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Button) event.widget).getToolTipText())] = New__ReturnValuesDialog.this.booleanParam[Integer
						.parseInt(((Button) event.widget).getToolTipText())].getSelection();*/
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
		return booleanParam;
	}

	/**
	 * @param parent
	 * @param defaultValue
	 * @param pattern
	 * @param enabled
	 * @param type
	 * @param required
	 * @param paramKey
	 */
	private Combo createLocalVarSelectionPane(final Composite parent, final String defaultValue, final String pattern,
			final boolean enabled, final String type, final String paramKey) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		/*final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText(required ? ASTERISK + labelName : labelName);
		label.setLayoutData(gridDataLabel);*/

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		final Combo localVarCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		localVarCombo.setSize(200, 20);
		localVarCombo.setLayoutData(gridDataText);
		localVarCombo.setEnabled(enabled);
		gridDataText.minimumWidth = 500;

		try {
			if (this.returnValuesData.getCompUnit() == null) {
				setErrorMessage("Please invoke this template from a java class(method), to select local variables.");
			}
			if (isEmpty(type)) {
				this.returnValuesData.setLocalVars(getLocalVarFromCompUnit(this.returnValuesData.getCompUnit(),
						this.returnValuesData.getEditorPart()));
			} else {
				this.returnValuesData.setLocalVars(getLocalVarsOfType(
						getLocalVarFromCompUnit(this.returnValuesData.getCompUnit(), this.returnValuesData.getEditorPart()), type));
			}
		} catch (final Exception ex) {
			setErrorMessage(ex.getMessage());
		}

		for (final FastCodeReturn localVar : getEmptyListForNull(this.returnValuesData.getLocalVars())) {
			localVarCombo.add(localVar.getType().getName() + SPACE + localVar.getName());
		}
		localVarCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Combo loclVar = (Combo) event.widget;
				ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, loclVar.getText().split(SPACE)[1].trim());
				/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(loclVar.getToolTipText())] = loclVar.getText().split(SPACE)[1]
						.trim();*/
				enableOrDisableChildren(paramKey);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}

		});

		localVarCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent arg0) {
				/*final Combo loclVar = (Combo) arg0.widget;
				if (isEmpty(loclVar.getText())) {
					setErrorMessage("Please select a local variable.");
				} else {
					setErrorMessage(ReturnValuesDialog.this.defaultMessage);
				}*/

			}
		});

		return localVarCombo;
	}

	private List<FastCodeReturn> getLocalVarsOfType(final List<FastCodeReturn> localVarFromCompUnit, final String type) {
		final List<FastCodeReturn> localVarsList = new ArrayList<FastCodeReturn>();
		for (final FastCodeReturn fastCodeReturn : localVarFromCompUnit) {
			if (fastCodeReturn.getType().getName().equals(type)) {
				localVarsList.add(fastCodeReturn);
			}
		}
		return localVarsList;
	}

	/**
	 * @param parent
	 * @param valueType
	 * @param defaultValue
	 * @param pattern
	 * @param enabled
	 * @param required
	 * @param required
	 * @param paramKey
	 */
	private Combo createProjectSelectionPane(final Composite parent, final String valueType, final String defaultValue,
			final String pattern, final boolean enabled, final boolean required, final String paramKey) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		/*final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText(required ? ASTERISK + labelName : labelName);
		label.setLayoutData(gridDataLabel);*/

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		final Combo projectCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		projectCombo.setSize(200, 20);
		projectCombo.setLayoutData(gridDataText);
		projectCombo.setEnabled(enabled);
		gridDataText.minimumWidth = 500;
		if (!required) {
			projectCombo.add(EMPTY_STR);
		}
		final IProject projects[] = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (final IProject prj : projects) {
			if (prj == null || !prj.exists() || !prj.isOpen()) {
				continue;
			}
			if (prj.getName().equals(FC_PLUGIN)) {
				continue;
			}
			if (valueType.equalsIgnoreCase(RETURN_TYPES.JAVAPROJECT.getValue())) {
				final IJavaProject javaProject = JavaCore.create(prj);
				if (javaProject == null || !javaProject.isOpen()) { // && javaProject.exists()) {
					continue;
				}
			}
			projectCombo.add(prj.getName());
			this.prjMap.put(prj.getName(), prj);

		}

		if (this.returnValuesData.getJavaProject() != null) {
			projectCombo.select(projectCombo.indexOf(this.returnValuesData.getJavaProject().getElementName()));
			ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeProject(this.prjMap.get(projectCombo.getText())));
			/*	this.valuesObj[count] = new FastCodeProject(this.prjMap.get(projectCombo.getText()));*/
		}

		projectCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Combo prjCombo = (Combo) event.widget;
				ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey,
						new FastCodeProject(ReturnValuesDialog.this.prjMap.get(prjCombo.getText())));
				/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(prjCombo.getToolTipText())] = new FastCodeProject(
						New__ReturnValuesDialog.this.prjMap.get(prjCombo.getText()));*///ReturnValuesDialog.this.projectCombo[count].getText()
				enableOrDisableChildren(paramKey);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}

		});

		projectCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent arg0) {
				/*	final Combo prjCombo = (Combo) arg0.widget;
					if (isEmpty(prjCombo.getText())) {
						setErrorMessage("Please select a project.");
					} else {
						setErrorMessage(ReturnValuesDialog.this.defaultMessage);
					}*/

			}
		});
		return projectCombo;
	}

	/**
	 * @param parent
	 * @param defaultValue
	 * @param pattern
	 * @param enabled
	 * @param required
	 * @param required
	 * @param paramKey
	 */
	private Combo createPackageSelectionPane(final Composite parent, final String project, final String pattern, final boolean enabled,
			final boolean required, final String paramKey) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		/*final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText(required ? ASTERISK + labelName : labelName);
		label.setLayoutData(gridDataLabel);*/

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		final Combo pkgCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);// new
		// Text(composite,
		// SWT.BORDER);
		pkgCombo.setSize(200, 20);
		pkgCombo.setLayoutData(gridDataText);
		pkgCombo.setEnabled(enabled);
		gridDataText.minimumWidth = 500;

		if (!required) {
			pkgCombo.add(EMPTY_STR);
		}

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (this.compilationUnit != null) {
			this.currentPackage = this.compilationUnit.getPrimary().findPrimaryType().getPackageFragment();
			pkgCombo.add(ENCLOSING_PACKAGE_STR + HYPHEN + getAlteredPackageName(this.currentPackage));
			//this.packageCombo[count].select(0);
		}
		if (!fastCodeCache.getPackageSet().isEmpty()) {
			for (final IPackageFragment pkgFrgmt : fastCodeCache.getPackageSet()) {
				if (this.currentPackage != null && this.currentPackage.equals(pkgFrgmt)) {
					continue;
				}
				if (!isEmpty(project)) {
					if (!project.equals(pkgFrgmt.getJavaProject().getElementName())) {
						continue;
					}
				}
				if (!isEmpty(pattern)) {
					if (!validatePattern(pattern, pkgFrgmt.getElementName())) {
						continue;
					}
				}
				boolean addItem = true;
				if (pkgCombo.getItems() != null) {
					for (final String existingPkg : pkgCombo.getItems()) {
						if (existingPkg.contains(ENCLOSING_PACKAGE_STR)) {
							continue;
						}
						if (existingPkg.equals(getAlteredPackageName(pkgFrgmt))) {
							addItem = false;
							break;
						}
					}
					if (addItem) {
						pkgCombo.add(getAlteredPackageName(pkgFrgmt));
					}
				}
			}
		}
		pkgCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Combo pkgCombo = (Combo) event.widget;
				String selectedPkgName = pkgCombo.getText();
				if (selectedPkgName.contains(ENCLOSING_PACKAGE_STR)) {
					selectedPkgName = ReturnValuesDialog.this.currentPackage.getElementName();
				}
				try {
					for (final IPackageFragment pkg : fastCodeCache.getPackageSet()) {
						if (getAlteredPackageName(pkg).equals(selectedPkgName)) {
							ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodePackage(pkg));
							/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = new FastCodePackage(
									pkg);*/
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
				enableOrDisableChildren(paramKey);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}

		});
		pkgCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				final Combo pkgCombo = (Combo) e.widget;
				String inputPkgName = pkgCombo.getText();
				if (!isEmpty(inputPkgName)) {
					if (inputPkgName.contains(ENCLOSING_PACKAGE_STR)) {
						inputPkgName = ReturnValuesDialog.this.currentPackage.getElementName();
					}
					for (final IPackageFragment pkg : fastCodeCache.getPackageSet()) {
						if (pkg.getElementName().equals(inputPkgName) || getAlteredPackageName(pkg).equals(inputPkgName)) {
							return;
						}
					}
					/*if (inputPkgName.contains(CURRENT_PACKAGE)) {
						ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Combo) e.widget).getToolTipText())] = new FastCodePackage(
								ReturnValuesDialog.this.currentPackage);
						if (!fastCodeCache.getPackageSet().contains(ReturnValuesDialog.this.currentPackage)) {
							fastCodeCache.getPackageSet().add(ReturnValuesDialog.this.currentPackage);

						}
					}*/
					enableOrDisableChildren(paramKey);
				}

			}

			@Override
			public void focusGained(final FocusEvent arg0) {

			}
		});

		pkgCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				final Combo pkgCombo = (Combo) e.widget;
				if (required && isEmpty(pkgCombo.getText())) {
					setErrorMessage("Please choose a package");
					enableOrDisableChildren(paramKey);
				} else {
					setErrorMessage(ReturnValuesDialog.this.defaultMessage);
				}
			}

		});

		final GridData gridDataButton = new GridData();

		final Button browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse");
		browse.setLayoutData(gridDataButton);
		browse.setEnabled(enabled);
		this.browseButtonComboMap.put(browse, pkgCombo);
		browse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Button b = (Button) event.getSource();
				try {
					final String srcPath = getDefaultPathFromProject(
							isEmpty(project) ? ReturnValuesDialog.this.returnValuesData.getJavaProject() : getIJavaProjectFromName(project),
							"source", EMPTY_STR);
					final IPackageFragment allPackages[] = getPackagesInProject(
							isEmpty(project) ? ReturnValuesDialog.this.returnValuesData.getJavaProject() : getIJavaProjectFromName(project),
							srcPath, "source");
					if (allPackages == null) {
						return;
					}
					final PackageSelectionDialog selectionDialog = new PackageSelectionDialog(new Shell(), "Package ",
							"Choose a package from below", allPackages, pattern);
					IPackageFragment packageFragment = null;
					if (selectionDialog.open() != CANCEL) {
						packageFragment = (IPackageFragment) selectionDialog.getFirstResult();
						//ReturnValuesDialog.this.packageCombo.setText(getAlteredPackageName(packageFragment));
						/*for (final Combo pkgCombo : ReturnValuesDialog.this.packageCombo) {
							if(brw.getToolTipText().equals(pkgCombo.getToolTipText())) {
								pkgCombo.add(getAlteredPackageName(packageFragment));
								pkgCombo.select(pkgCombo.getItemCount() - 1);
							}
						}*/
						boolean addItem = true;
						if (pkgCombo.getItems() != null) {
							for (final String existingPkg : pkgCombo.getItems()) {
								if (existingPkg.equals(getAlteredPackageName(packageFragment))) {
									if (!pkgCombo.getText().equals(existingPkg)) {
										pkgCombo.select(pkgCombo.indexOf(existingPkg));

									}
									addItem = false;
									break;
								}
							}
						}
						if (addItem) {
							pkgCombo.add(getAlteredPackageName(packageFragment));
							pkgCombo.select(pkgCombo.getItemCount() - 1);
						}
						if (!fastCodeCache.getPackageSet().contains(packageFragment)) {
							fastCodeCache.getPackageSet().add(packageFragment);
						}
						ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodePackage(packageFragment));
						/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Button) event.widget).getToolTipText())] = new FastCodePackage(
								packageFragment);*/
					}

				} catch (final Exception ex) {
					ex.printStackTrace();
				}
				enableOrDisableChildren(paramKey);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
		return pkgCombo;
	}

	private void createComboBoxPane(final Composite parent, final String values) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataCombo = new GridData();
		gridDataCombo.grabExcessHorizontalSpace = true;

		this.arrayDataCombo = new Combo(composite, SWT.NONE);
		this.arrayDataCombo.setSize(200, 20);
		this.arrayDataCombo.setLayoutData(gridDataCombo);
		gridDataCombo.minimumWidth = 500;

		for (final String val : values.split(SPACE)) {
			this.arrayDataCombo.setText(val);
		}

	}

	/**
	 * @param parent
	 * @param defaultValue
	 * @param pattern
	 * @param enabled
	 * @param required
	 * @param required
	 * @param paramKey
	 */
	private Combo createFolderSelectionPane(final Composite parent, final String defaultValue, final String project, final String pattern,
			final boolean enabled, final boolean required, final String paramKey) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		/*final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText(required ? ASTERISK + labelName : labelName);
		label.setLayoutData(gridDataLabel);*/

		final GridData gridDataCombo = new GridData();
		gridDataCombo.grabExcessHorizontalSpace = true;

		final Combo folderCombo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);// new
		// Text(composite,
		// SWT.BORDER);
		folderCombo.setSize(200, 20);
		folderCombo.setLayoutData(gridDataCombo);
		gridDataCombo.minimumWidth = 500;
		folderCombo.setEnabled(enabled);

		if (!required) {
			folderCombo.add(EMPTY_STR);
		}

		final FastCodeCache fcCache = FastCodeCache.getInstance();
		if (this.compilationUnit == null) {
			final IFile file = (IFile) this.returnValuesData.getEditorPart().getEditorInput().getAdapter(IFile.class);

			try {
				if (file != null) {
					final String srcPath = file
							.getProjectRelativePath()
							.toString()
							.substring(0, file.getProjectRelativePath().toString().indexOf(file.getProjectRelativePath().lastSegment()) - 1);
					final IFolder folder = this.returnValuesData.getJavaProject().getProject().getFolder(srcPath);
					if (folder != null) {
						this.currentFolder = folder;
						folderCombo.add(ENCLOSING_FOLDER_STR + HYPHEN + this.currentFolder.getFullPath().toString());

						//this.folderNameCombo[count].select(0);
					}
				}
			} catch (final Exception ex) {
				ex.printStackTrace();
			}

		}
		if (!fcCache.getFolderSet().isEmpty()) {
			for (final IFolder folder : fcCache.getFolderSet()) {
				if (this.currentFolder != null && this.currentFolder.equals(folder)) {
					continue;
				}
				if (!isEmpty(project)) {
					if (!project.equals(folder.getProject().getName())) {
						continue;
					}
				}
				if (!isEmpty(pattern)) {
					if (!validatePattern(pattern, folder.getName())) {
						continue;
					}
				}
				boolean addItem = true;
				if (folderCombo.getItems() != null) {
					for (final String existingFolder : folderCombo.getItems()) {
						if (existingFolder.contains(ENCLOSING_FOLDER_STR)) {
							continue;
						}
						if (existingFolder.equals(folder.getFullPath().toString())) {
							addItem = false;
							break;

						}
					}
					if (addItem) {
						folderCombo.add(folder.getFullPath().toString());
					}
				}
			}
		}
		folderCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

				String selectedFolderPath = ((Combo) event.widget).getText(); // ReturnValuesDialog.this.folderNameCombo.getText();
				if (selectedFolderPath.contains(ENCLOSING_FOLDER_STR)) {
					selectedFolderPath = ReturnValuesDialog.this.currentFolder.getFullPath().toString();
				}
				try {
					if (!fcCache.getFolderSet().isEmpty()) {
						for (final IFolder folder : fcCache.getFolderSet()) {
							if (folder.getFullPath().toString().equals(selectedFolderPath)) {
								ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeFolder(folder));
								/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = new FastCodeFolder(
										folder);*/
							}
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
				enableOrDisableChildren(paramKey);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}

		});
		folderCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				String inputFolderPath = ((Combo) e.widget).getText();
				if (!isEmpty(inputFolderPath)) {
					if (inputFolderPath.contains(ENCLOSING_FOLDER_STR)) {
						inputFolderPath = ReturnValuesDialog.this.currentFolder.getFullPath().toString();
					}
					for (final IFolder folder : fcCache.getFolderSet()) {
						if (folder.getFullPath().toString().equals(inputFolderPath)) {
							return;
						}
					}
					enableOrDisableChildren(paramKey);
				}

			}

			@Override
			public void focusGained(final FocusEvent arg0) {

			}
		});

		folderCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent arg0) {
				if (required && isEmpty(((Combo) arg0.widget).getText())) {
					setErrorMessage("Please select a folder.");
					enableOrDisableChildren(paramKey);
				} else {
					setErrorMessage(ReturnValuesDialog.this.defaultMessage);
				}

			}
		});

		final GridData gridDataButton = new GridData();
		final Button browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse");
		browse.setLayoutData(gridDataButton);
		browse.setEnabled(enabled);
		this.browseButtonComboMap.put(browse, folderCombo);
		browse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					Path path = null;
					final Button b = (Button) e.getSource();
					final ContainerSelectionDialog dialog = new ContainerSelectionDialog(new Shell(), null, true, "Select a folder:");
					dialog.setTitle("Select a Folder");
					dialog.showClosedProjects(false);
					if (dialog.open() != CANCEL) {
						path = (Path) dialog.getResult()[0];
						String srcPath = null;
						if (path != null) {
							final String project = path.segment(0);
							srcPath = path.toString().substring(project.length() + 1);
						}
						final IFolder folder = ReturnValuesDialog.this.returnValuesData.getJavaProject().getProject()
								.getFolder(new Path(srcPath));
						ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeFolder(folder));
						/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Button) e.widget).getToolTipText())] = new FastCodeFolder(
								folder);*/

						boolean addItem = true;
						if (folderCombo.getItems() != null) {
							for (final String existingFolder : folderCombo.getItems()) {
								if (existingFolder.equals(folder.getFullPath().toString())) {
									if (!existingFolder.equals(folderCombo.getText())) {
										folderCombo.select(folderCombo.indexOf(existingFolder));
									}
									addItem = false;
									break;
								}
							}
						}
						if (addItem) {
							folderCombo.add(folder.getFullPath().toString());
							folderCombo.select(folderCombo.getItemCount() - 1);
						}
						if (!fcCache.getFolderSet().contains(folder)) {
							fcCache.getFolderSet().add(folder);
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
				enableOrDisableChildren(paramKey);
			}

		});
		return folderCombo;
	}

	/**
	 * @param parent
	 * @param enabled
	 * @param required
	 * @param required
	 * @param labelName
	 * @param defaultValue
	 */
	private Combo createClassSelectionPane(final Composite parent, final String project, final String pattern, final boolean enabled,
			final boolean required, final String paramKey/*, final String labelName*/) {
		final Composite composite = new Composite(parent, SWT.NONE/*parent.getStyle()*/);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataCombo = new GridData(/*SWT.FILL, SWT.NONE, true, false*/);
		gridDataCombo.horizontalAlignment = GridData.FILL;
		gridDataCombo.grabExcessHorizontalSpace = true;

		final Combo classCombo = new Combo(composite, SWT.READ_ONLY);// new
		// Text(composite,
		// SWT.BORDER);
		//classCombo.setSize(200, 20);
		classCombo.setLayoutData(gridDataCombo);
		classCombo.setRedraw(true);
		gridDataCombo.minimumWidth = 500;
		classCombo.setEnabled(enabled);

		if (!required) {
			classCombo.add(EMPTY_STR);
		}

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (this.compilationUnit != null) {
			this.currentClass = this.compilationUnit.getPrimary().findPrimaryType();
			classCombo.add(ENCLOSING_CLASS_STR + HYPHEN + this.currentClass.getFullyQualifiedName());
			//this.classNameCombo[count].select(0);
		}

		if (!fastCodeCache.getTypeSet().isEmpty()) {
			for (final IType type : fastCodeCache.getTypeSet()) {
				if (this.currentClass != null && this.currentClass.equals(type)) {
					continue;
				}
				if (!isEmpty(project)) {
					if (!project.equals(type.getJavaProject().getElementName())) {
						continue;
					}
				}
				if (!isEmpty(pattern)) {
					if (!validatePattern(pattern, type.getElementName())) {
						continue;
					}
				}

				boolean addItem = true;
				if (classCombo.getItems() != null) {
					for (final String existingClass : classCombo.getItems()) {
						if (existingClass.contains(ENCLOSING_CLASS_STR)) {
							continue;
						}
						if (existingClass.equals(type.getFullyQualifiedName())) {
							addItem = false;
							break;
						}
					}
					if (addItem) {
						classCombo.add(type.getFullyQualifiedName());
					}
				}
			}
		}

		classCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				String selectedFromClassName = ((Combo) event.widget).getText(); //ReturnValuesDialog.this.classNameCombo.getText();
				try {
					if (selectedFromClassName.contains(ENCLOSING_CLASS_STR)) {
						selectedFromClassName = ReturnValuesDialog.this.currentClass.getFullyQualifiedName();
					}
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.getFullyQualifiedName().equals(selectedFromClassName)) {
								ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeType(type));
								/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = new FastCodeType(
										type);*/
							}
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
				enableOrDisableChildren(paramKey);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
		classCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				String inputFromClassName = ((Combo) e.widget).getText();
				if (!isEmpty(inputFromClassName)) {
					if (inputFromClassName.contains(ENCLOSING_CLASS_STR)) {
						inputFromClassName = ReturnValuesDialog.this.currentClass.getFullyQualifiedName();
					}
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.getFullyQualifiedName().equals(inputFromClassName)) {
								return;
							}
						}
					}
					try {
						//						final IType inputClassType = getTypeFromWorkspace(inputFromClassName);
						//						if (inputClassType != null) {
						//							ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeType(inputClassType));
						/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Combo) e.widget).getToolTipText())] = new FastCodeType(
								inputClassType);*/
						//							if (!fastCodeCache.getTypeSet().contains(inputClassType)) {
						//								fastCodeCache.getTypeSet().add(inputClassType);
						//							}
						//						} else {
						//							setErrorMessage("Class does not exist,Please enter an existing class name ");
						//						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
					enableOrDisableChildren(paramKey);
				}
			}

			@Override
			public void focusGained(final FocusEvent arg0) {

			}
		});
		classCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {

				if (required && isEmpty(((Combo) e.widget).getText())) {
					setErrorMessage("Please choose a Class");
					enableOrDisableChildren(paramKey);
				} else {
					setErrorMessage(ReturnValuesDialog.this.defaultMessage);
				}
			}

		});

		final GridData gridDataButton = new GridData(SWT.FILL, SWT.NONE, true, false);
		final Button browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse");
		browse.setLayoutData(gridDataButton);
		browse.setEnabled(enabled);
		this.browseButtonComboMap.put(browse, classCombo);
		browse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Button b = (Button) e.getSource();
				SelectionDialog selectionDialog = null;
				try {
					IProject prj = null;
					if (!isEmpty(project)) {
						prj = getIJavaProjectFromName(project).getProject();
					}
					final IProject prjArr[] = new IProject[] { prj };
					selectionDialog = JavaUI.createTypeDialog(composite.getShell() == null ? new Shell() : composite.getShell(), null,
							isEmpty(project) ? SearchEngine.createWorkspaceScope() : SearchEngine.createJavaSearchScope(prjArr),
							IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, pattern);
					selectionDialog.setTitle(ReturnValuesDialog.this.selectMsg + "Class");
					selectionDialog.setMessage(ReturnValuesDialog.this.selectMsg + "Class");

					if (selectionDialog.open() == CANCEL) {
						return;
					}
					final IType browsedClassType = (IType) selectionDialog.getResult()[0];
					boolean addItem = true;

					if (classCombo.getItems() != null) {
						for (final String existingClass : classCombo.getItems()) {
							if (existingClass.equals(browsedClassType.getFullyQualifiedName())) {
								if (!existingClass.equals(classCombo.getText())) {
									classCombo.select(classCombo.indexOf(existingClass));
								}
								addItem = false;
								break;
							}
						}
					}
					if (addItem) {
						classCombo.add(browsedClassType.getFullyQualifiedName());
						classCombo.select(classCombo.getItemCount() - 1);
					}
					ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeType(browsedClassType));
					/*for (final Combo clsCombo : ReturnValuesDialog.this.classNameCombo) {
						if (clsCombo != null && ((Button) e.widget).getToolTipText().equals(clsCombo.getToolTipText())) {
							clsCombo.setText(((IType) selectionDialog.getResult()[0]).getFullyQualifiedName());
							ReturnValuesDialog.this.classNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]..setText(((IType) selectionDialog.getResult()[0]).getFullyQualifiedName());
							ReturnValuesDialog.this.classSelected[Integer.parseInt(((Button) e.widget).getToolTipText())] = new FastCodeType((IType) selectionDialog.getResult()[0]);
						}
					}*/
					if (!fastCodeCache.getTypeSet().contains(selectionDialog.getResult()[0])) {
						fastCodeCache.getTypeSet().add((IType) selectionDialog.getResult()[0]);
					}
				} catch (final JavaModelException ex) {
					ex.printStackTrace();
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
				enableOrDisableChildren(paramKey);
			}

		});
		return classCombo;
	}

	protected void enableOrDisableChildren(final String paramKey) {
		final Object parentSWTObject = this.paramKeySWTObjectMap.get(paramKey);
		boolean enable = false;
		if (parentSWTObject instanceof Text) {
			final Text textObj = (Text) parentSWTObject;
			enable = !isEmpty(textObj.getText());
		} else if (parentSWTObject instanceof Combo) {
			final Combo comboObj = (Combo) parentSWTObject;
			enable = !isEmpty(comboObj.getText());
		} else if (parentSWTObject instanceof Button) {
			final Button buttonObj = (Button) parentSWTObject;
			enable = buttonObj.getSelection();
		}
		if (this.parentKeyChildrenKeyMap.containsKey(paramKey)) {
			final List<String> childrenList = this.parentKeyChildrenKeyMap.get(paramKey);
			for (final String childKey : childrenList) {
				final Object childSWTObjct = this.paramKeySWTObjectMap.get(childKey);
				if (childSWTObjct instanceof Text) {
					final Text textObj = (Text) childSWTObjct;
					textObj.setEnabled(enable);
				} else if (childSWTObjct instanceof Combo) {
					final Combo comboObj = (Combo) childSWTObjct;
					comboObj.setEnabled(enable);
					for (final Entry<Button, Combo> browseComboEntries : this.browseButtonComboMap.entrySet()) {
						if (comboObj.equals(browseComboEntries.getValue())) {
							final Button browseButton = browseComboEntries.getKey();
							browseButton.setEnabled(enable);
							break;
						}
					}
				} else if (childSWTObjct instanceof Button) {
					final Button buttonObj = (Button) childSWTObjct;
					buttonObj.setEnabled(enable);
				}

				if (enable) {
					if (Boolean.valueOf(this.paramKeyFCAdditonalParamMap.get(childKey).getRequired())) {
						final Label label = (Label) this.paramKeyLableSWTMap.get(childKey);
						final String labelText = label.getText();
						label.setText(ASTERISK + labelText);
						label.redraw();
						label.getParent().layout();
					}
				} else {
					final Label label = (Label) this.paramKeyLableSWTMap.get(childKey);
					final String labelText = label.getText().replace(ASTERISK, EMPTY_STR);
					label.setText(labelText);
					label.redraw();
					label.getParent().layout();
				}

			}
		}

	}

	/**
	 * @param parent
	 * @param defaultValue
	 * @param pattern
	 * @param enabled
	 * @param required
	 * @param required
	 * @param paramKey
	 */
	private Combo createFileSelectionPane(final Composite parent, final String project, final String pattern, final boolean enabled,
			final boolean required, final String paramKey) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		/*final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setText(required ? ASTERISK + labelName : labelName);
		label.setLayoutData(gridDataLabel);*/

		final GridData gridDataCombo = new GridData();
		gridDataCombo.grabExcessHorizontalSpace = true;

		final Combo fileCombo = new Combo(composite, SWT.READ_ONLY);// new
		// Text(composite,
		// SWT.BORDER);
		fileCombo.setSize(200, 20);
		fileCombo.setLayoutData(gridDataCombo);
		gridDataCombo.minimumWidth = 500;
		fileCombo.setEnabled(enabled);

		if (!required) {
			fileCombo.add(EMPTY_STR);
		}

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (this.compilationUnit == null) {
			final IFile file = (IFile) this.returnValuesData.getEditorPart().getEditorInput().getAdapter(IFile.class);
			this.currentFile = file;
			fileCombo.add(ENCLOSING_FILE_STR + HYPHEN + this.currentFile.getFullPath().toString());
			//this.fileNameCombo[count].select(0);
		}
		if (!fastCodeCache.getFileSet().isEmpty()) {
			for (final IFile file : fastCodeCache.getFileSet()) {
				if (this.currentFile != null && this.currentFile.equals(file)) {
					continue;
				}
				if (!isEmpty(project)) {
					if (!project.equals(file.getProject().getName())) {
						continue;
					}
				}
				if (!isEmpty(pattern)) {
					if (!validatePattern(pattern, file.getName())) {
						continue;
					}
				}
				boolean addItem = true;
				if (fileCombo.getItems() != null) {
					for (final String existingFile : fileCombo.getItems()) {
						if (existingFile.contains(ENCLOSING_FILE_STR)) {
							continue;
						}
						if (existingFile.equals(file.getFullPath().toString())) {
							addItem = false;
							break;
						}
					}
					if (addItem) {
						fileCombo.add(file.getFullPath().toString());
					}
				}

			}
		}

		fileCombo.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				String selectedFileName = ((Combo) event.widget).getText(); //ReturnValuesDialog.this.fileNameCombo.getText();
				try {
					if (selectedFileName.contains(ENCLOSING_FILE_STR)) {
						selectedFileName = ReturnValuesDialog.this.currentFile.getFullPath().toString();
					}
					if (!fastCodeCache.getFileSet().isEmpty()) {
						for (final IFile file : fastCodeCache.getFileSet()) {
							if (file.getName().equals(selectedFileName.substring(selectedFileName.lastIndexOf(FORWARD_SLASH) + 1))) {
								ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeFile(file));
								/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = new FastCodeFile(
										file);*///new FastCodeFile(file.getName(), file.getProjectRelativePath().toString());
							}
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
				enableOrDisableChildren(paramKey);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
		fileCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {

				if (required && isEmpty(((Combo) e.widget).getText())) {
					setErrorMessage("Please choose a file");
					enableOrDisableChildren(paramKey);
				} else {
					setErrorMessage(ReturnValuesDialog.this.defaultMessage);
				}
			}
		});
		fileCombo.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				setErrorMessage(ReturnValuesDialog.this.defaultMessage);
				String inputFileName = ((Combo) e.widget).getText();
				if (!isEmpty(inputFileName)) {
					if (inputFileName.contains(ENCLOSING_FILE_STR)) {
						inputFileName = ReturnValuesDialog.this.currentFile.getFullPath().toString();
					}
					if (!fastCodeCache.getFileSet().isEmpty()) {
						for (final IFile file : fastCodeCache.getFileSet()) {
							if (file.getName().equals(inputFileName.substring(inputFileName.lastIndexOf(FORWARD_SLASH) + 1))) {
								return;
							}
						}
					}

					final boolean inputResult = isFullNameOfFile(inputFileName);
					if (inputResult) {
						try {
							final Path inputPath = new Path(inputFileName);
							final IPath iInputPath = inputPath.makeAbsolute();
							final IFile inputFile = ResourcesPlugin.getWorkspace().getRoot().getFile(iInputPath);
							if (inputFile != null && inputFile.exists()) {
								final FastCodeFile inputFastCodeFile = new FastCodeFile(inputFile);//new FastCodeFile(inputFile.getName(), inputFile.getProjectRelativePath().toString());
								ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, inputFastCodeFile);
								//New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Combo) e.widget).getToolTipText())] = inputFastCodeFile;
								if (!fastCodeCache.getFileSet().contains(inputFile)) {
									fastCodeCache.getFileSet().add(inputFile);
								}
							} else {
								ReturnValuesDialog.this.setErrorMessage("File does not exist,Please enter an existing file name");
							}
						} catch (final Exception ex) {
							ex.printStackTrace();
						}

					} else {
						ReturnValuesDialog.this
								.setErrorMessage("Please enter file name with full path like /Project Name/....../file name ");
					}
					enableOrDisableChildren(paramKey);
				}

			}

			@Override
			public void focusGained(final FocusEvent arg0) {

			}
		});

		final GridData gridDataButton = new GridData();
		final Button browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse");
		browse.setLayoutData(gridDataButton);
		browse.setEnabled(enabled);
		this.browseButtonComboMap.put(browse, fileCombo);
		browse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Button b = (Button) e.getSource();
				OpenResourceDialog resourceDialog = null;
				/*resourceDialog = new OpenResourceDialog(parent.getShell() == null ? new Shell() : parent.getShell(), ResourcesPlugin
						.getWorkspace().getRoot(), IResource.FILE);*/

				try {
					resourceDialog = new OpenResourceDialog(parent.getShell() == null ? new Shell() : parent.getShell(),
							isEmpty(project) ? ResourcesPlugin.getWorkspace().getRoot() : getIJavaProjectFromName(project).getProject(),
							IResource.FILE);
				} catch (final Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

				resourceDialog.setTitle(ReturnValuesDialog.this.selectMsg + "File");
				resourceDialog.setMessage(ReturnValuesDialog.this.selectMsg + "File");

				if (resourceDialog.open() == CANCEL) {
					return;
				}

				final IFile browseFile = (IFile) resourceDialog.getResult()[0];
				final String browsePath = browseFile.getFullPath().toString();
				ReturnValuesDialog.this.paramKeyParamValueMap.put(paramKey, new FastCodeFile(browseFile));
				/*New__ReturnValuesDialog.this.valuesObj[Integer.parseInt(((Button) e.widget).getToolTipText())] = new FastCodeFile(
						browseFile);*/
				boolean addItem = true;
				if (fileCombo.getItems() != null) {
					for (final String existingFile : fileCombo.getItems()) {
						if (existingFile.equals(browsePath)) {
							if (!existingFile.equals(fileCombo.getText())) {
								fileCombo.select(fileCombo.indexOf(existingFile));
							}
							addItem = false;
							break;
						}
					}
				}
				if (addItem) {
					fileCombo.add(browsePath);
					fileCombo.select(fileCombo.getItemCount() - 1);
				}
				if (!fastCodeCache.getFileSet().contains(browseFile)) {
					fastCodeCache.getFileSet().add(browseFile);
				}
				enableOrDisableChildren(paramKey);
			}

		});
		return fileCombo;
	}

	/**
	 * @return
	 */
	public ReturnValuesData getReturnValuesData() {
		return this.returnValuesData;
	}

	/**
	 * @param parent
	 */
	private void createErrorMessageText(final Composite parent) {
		/*final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);*/

		final GridData errText = new GridData(700, 20);
		errText.horizontalSpan = 3;
		this.errorMessageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		this.errorMessageText.setForeground(FastCodeColor.getErrorMsgColor());
		this.errorMessageText.setLayoutData(errText);
		setErrorMessage(this.defaultMessage);

	}

	/**
	 *
	 * @param errorMessage
	 */
	public void setErrorMessage(final String errorMessage) {
		// this.errorMessage = errorMessage;
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
	 * @param editorPart
	 * @return
	 */
	protected ICompilationUnit getCompilationUnitFromEditor(final IEditorPart editorPart) {
		final IWorkingCopyManager manager = JavaUI.getWorkingCopyManager();
		final ICompilationUnit compilationUnit = manager.getWorkingCopy(editorPart.getEditorInput());
		return compilationUnit != null && compilationUnit.exists() ? compilationUnit : null;
	}

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		shell.setText("Return Values");

		shell.setFullScreen(true);
		final ReturnValuesDialog returnValuesDialog = new ReturnValuesDialog(shell);

		returnValuesDialog.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	@Override
	protected boolean isResizable() {
		/*if (this.shell != null) {
			this.shell.redraw();
			this.shell.layout();
		}*/
		return true;
	}

}
