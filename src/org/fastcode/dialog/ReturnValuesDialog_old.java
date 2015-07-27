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
import static org.fastcode.util.SourceUtil.getTypeFromWorkspace;
import static org.fastcode.util.SourceUtil.isFullNameOfFile;
import static org.fastcode.util.StringUtil.isEmpty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

public class ReturnValuesDialog_old extends TrayDialog {

	Shell									shell;
	private ReturnValuesData				returnValuesData;
	Label[]									label;
	Text[]									valueText;
	int										count, size = 0;
	StringBuffer							valueType					= new StringBuffer();
	private Text							errorMessageText;
	private final String					defaultMessage				= NEWLINE;
	private final String					selectMsg					= "Select a ";
	private Combo[]							fileNameCombo;
	private Combo[]							classNameCombo;
	private Combo[]							folderNameCombo;
	private Combo							arrayDataCombo;
	private Combo[]							packageCombo;
	private Button							packageBrowseButton;
	private Combo[]							projectCombo;
	private Combo[]							localVarCombo;
	Map<String, IProject>					prjMap						= new HashMap<String, IProject>();
	private Object[]						valuesObj;
	private ICompilationUnit				compilationUnit;
	private IType							currentClass;
	private IFile							currentFile;
	private IFolder							currentFolder;
	private IPackageFragment				currentPackage;
	private Button[]						booleanParam;
	Map<String, FastCodeAdditionalParams>	labelFCAdditonalParamMap	= new HashMap<String, FastCodeAdditionalParams>();
	private Combo[]							interfaceNameCombo;
	private IType							currentInterface;
	private Combo[]							enumNameCombo;
	private Combo[]							allowedValuesCombo;
	String[]								placeHolderNames;

	/**
	 * @param shell
	 */
	public ReturnValuesDialog_old(final Shell shell) {
		super(shell);
		this.shell = shell;
	}

	/**
	 * @param shell
	 * @param returnValuesData
	 */
	public ReturnValuesDialog_old(final Shell shell, final ReturnValuesData returnValuesData) {
		super(shell);
		this.shell = shell;
		this.returnValuesData = returnValuesData;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		final GridLayout layout = new GridLayout();
		// layout.numColumns = 2;
		parent.setLayout(layout);
		createErrorMessageText(parent);
		createValueText(parent);
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
			boolean required = Boolean.valueOf(this.labelFCAdditonalParamMap.get(placeHolderName).getRequired());
			final boolean enabled = Boolean.valueOf(this.labelFCAdditonalParamMap.get(placeHolderName).getEnabled());
			if (!enabled) {
				required = false;
			}
			if (this.valueText[i] != null) {
				if (ReturnValuesDialog_old.this.returnValuesData.isUnitTest()) {
					if (isEmpty(this.valueText[i].getText())) {
						setErrorMessage("please enter value for " + this.label[i].getText().replace(ASTERISK, EMPTY_STR));
						return;
					}
				}

				if (required && this.label[i].getText().split(SPACE)[0].replace(ASTERISK, EMPTY_STR).contains(STARTLINE_ENDLINE)) {
					final String lines = this.valueText[i].getText();
					final String[] stringArray = lines.split(" ");
					if (!this.valueText[i].getText().matches(".*\\d.*") || !this.valueText[i].getText().contains(HYPHEN)) {
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

				if (required && isEmpty(this.valueText[i].getText())) {
					setErrorMessage("please enter value for " + this.label[i].getText().replace(ASTERISK, EMPTY_STR));
					return;
				}
				if (this.returnValuesData.getFastCodeAdditionalParams()[i].getReturnTypes().getValue() == INT) {
					this.returnValuesData.addReturnValuesMap(this.returnValuesData.getFastCodeAdditionalParams()[i].getName(), new Integer(
							this.valueText[i].getText()));
				} else if (this.returnValuesData.getFastCodeAdditionalParams()[i].getReturnTypes().getValue() == RETURN_TYPES.INTRANGE
						.getValue()) {
					IntRange intRangeObj = null;
					try {
						intRangeObj = parseIntrange(this.valueText[i].getText());

						if (Integer.parseInt(this.returnValuesData.getFastCodeAdditionalParams()[i].getMax()) != 0) {
							validateIntRange(this.returnValuesData.getFastCodeAdditionalParams()[i].getMin(),
									this.returnValuesData.getFastCodeAdditionalParams()[i].getMax(), this.valueText[i], intRangeObj);
						}
					} catch (final Exception ex) {
						setErrorMessage(ex.getMessage());
						this.valueText[i].setFocus();
						return;
					}
					this.returnValuesData.addReturnValuesMap(this.returnValuesData.getFastCodeAdditionalParams()[i].getName(), intRangeObj);
				} else {
					this.returnValuesData.addReturnValuesMap(this.returnValuesData.getFastCodeAdditionalParams()[i].getName(),
							this.valueText[i].getText());
				}
			} else {
				String label = EMPTY_STR;
				String additionalParamType = EMPTY_STR;
				//boolean required = false;
				if (this.label[i].getText().contains(LEFT_PAREN)) {
					label = this.label[i].getText().split(SPACE)[0].replace(ASTERISK, EMPTY_STR);
					additionalParamType = this.label[i].getText().split(SPACE)[1].replace(LEFT_PAREN, EMPTY_STR).replace(RIGHT_PAREN,
							EMPTY_STR);

				} else {
					label = this.label[i].getText().replace(ASTERISK, EMPTY_STR);
					additionalParamType = this.returnValuesData.getFastCodeAdditionalParams()[i].getReturnTypes().getValue();
				}
				additionalParamType = this.returnValuesData.getFastCodeAdditionalParams()[i].getReturnTypes().getValue();
				//final String placeHolderName = this.returnValuesData.getFastCodeAdditionalParams()[i].getName();
				final String allowedVal = this.returnValuesData.getFastCodeAdditionalParams()[i].getAllowedValues();
				//required = Boolean.valueOf(this.labelFCAdditonalParamMap.get(placeHolderName).getRequired());
				final String pattern = this.labelFCAdditonalParamMap.get(placeHolderName).getPattern();
				if (!additionalParamType.equals(RETURN_TYPES.LOCALVAR.getValue())) {
					/* else {
						continue;
					}*/
				}

				//final String placeHolderName= this.returnValuesData.getFastCodeAdditionalParams()[this.count].getName();
				if (additionalParamType.equals(RETURN_TYPES.CLASS.getValue())) {
					/*if (required && this.valuesObj[i] == null) {
						setErrorMessage("please enter value for " + label);
						return;
					}*/
					if (this.currentClass != null && !isEmpty(this.classNameCombo[i].getText())
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
							: this.valuesObj[i]);

				} else if (additionalParamType.equals(RETURN_TYPES.FILE.getValue())) {
					/*if (required && this.valuesObj[i] == null) {
						setErrorMessage("please enter value for " + label);
						return;
					}*/
					final String textValue = this.fileNameCombo[i].getText();
					if (!isEmpty(textValue) && !validatePattern(pattern, textValue)) {
						setErrorMessage("The selected value " + textValue + " does not match pattern " + pattern + " specified.");
						return;
					}/* else {

						}*/

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
					this.returnValuesData.addReturnValuesMap(placeHolderName, isEmpty(textValue) ? null : this.valuesObj[i]);

				} else if (additionalParamType.equals(RETURN_TYPES.FOLDER.getValue())) {

					final String textValue = this.folderNameCombo[i].getText();
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
					this.returnValuesData.addReturnValuesMap(placeHolderName, this.valuesObj[i]);

				} else if (additionalParamType.equals(RETURN_TYPES.PACKAGE.getValue())) {
					/*if (required && this.valuesObj[i] == null) {
						setErrorMessage("please enter value for " + label);
						return;
					}*/
					if (this.currentPackage != null && !isEmpty(this.packageCombo[i].getText())
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
					this.returnValuesData.addReturnValuesMap(placeHolderName, this.valuesObj[i]);

				} else if (additionalParamType.equals(RETURN_TYPES.PROJECT.getValue())
						|| additionalParamType.equals(RETURN_TYPES.JAVAPROJECT.getValue())) {
					if (required && isEmpty(this.projectCombo[i].getText())) {
						setErrorMessage("please enter value for " + label);
						return;
					}
					this.returnValuesData.addReturnValuesMap(label, this.valuesObj[i]);
				} else if (additionalParamType.equals(RETURN_TYPES.LOCALVAR.getValue())) {
					if (required && isEmpty(this.localVarCombo[i].getText())) {
						setErrorMessage("please enter value for " + label);
						return;
					}
					for (final FastCodeReturn fastCodeReturn : getEmptyListForNull(this.returnValuesData.getLocalVars())) {
						if (fastCodeReturn.getName().equals(this.valuesObj[i])) {
							this.returnValuesData.addReturnValuesMap(placeHolderName, fastCodeReturn);
							break;
						}
					}

				} else if (additionalParamType.equals(RETURN_TYPES.BOOLEAN.getValue())) {
					this.returnValuesData.addReturnValuesMap(placeHolderName, !this.booleanParam[i].getSelection()
							|| this.valuesObj[i] == null ? false : this.valuesObj[i]);

				} else if (additionalParamType.equals(RETURN_TYPES.INTERFACE.getValue())) {
					if (this.currentInterface != null && !isEmpty(this.interfaceNameCombo[i].getText())
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
							: this.valuesObj[i]);
				} else if (additionalParamType.equals(RETURN_TYPES.ENUMERATION.getValue())) {
					/*if (this.valuesObj[i] == null) {
						setErrorMessage("please enter value for " + label);
						return;
					}*/
					if (!valueAvailable(i, label, required)) {
						return;
					}
					/*final String textValue = this.valuesObj[i].getText();
					validatePattern(pattern, textValue);*/
					if (!fastCodeCache.getTypeSet().contains(((FastCodeType) this.valuesObj[i]).getiType())) {
						fastCodeCache.getTypeSet().add(((FastCodeType) this.valuesObj[i]).getiType());
					}
					this.returnValuesData.addReturnValuesMap(placeHolderName, this.valuesObj[i]);
				} else if (!isEmpty(allowedVal)) {
					this.returnValuesData.addReturnValuesMap(placeHolderName, this.valuesObj[i]);
				}
				/*
				}
				* else if (this.arrayData) {
				* this.returnValuesData.addReturnValuesMap
				* (this.label[i].getText(), this.arrayValue); }
				*/
			}
		}
		super.okPressed();
	}

	/**
	 * @param i
	 * @param label
	 * @param required
	 */
	public boolean valueAvailable(final int i, final String label, final boolean required) {
		if (required && this.valuesObj[i] == null) {
			setErrorMessage("please enter value for " + label);
			return false;
		}
		return true;
	}

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
	 */
	private void createValueText(final Composite parent) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setSize(300, 500);
		// String[] labelText;

		this.size = this.returnValuesData.getFastCodeAdditionalParams().length;
		if (this.returnValuesData.getEditorPart() != null) {
			this.compilationUnit = getCompilationUnitFromEditor(this.returnValuesData.getEditorPart());
		}

		/*final int noStringValues = 0;
		int noClassValues = 0;
		int noFileValues = 0;
		int noPackageValues = 0;
		int noFolderValues = 0;
		int noProjectValues = 0;
		for (int i = 0; i <= this.size; i++) {
			final String valueType = this.returnValuesData.getFastCodeAdditionalParams()[this.count].getReturnTypes().getValue();
			if (valueType.equals(RETURN_TYPES.CLASS.getValue())) {
				noClassValues++;
			} else if (valueType.equals(RETURN_TYPES.FILE.getValue())) {
				noFileValues++;
			} else if (valueType.equals(RETURN_TYPES.FOLDER.getValue())) {
				noFolderValues++;
			} else if (valueType.equals(RETURN_TYPES.PACKAGE.getValue())) {
				noPackageValues++;
			} else if (valueType.equalsIgnoreCase(RETURN_TYPES.PROJECT.getValue())
					|| valueType.equalsIgnoreCase(RETURN_TYPES.JAVAPROJECT.getValue())) {
				noProjectValues++;
			}
		}*/

		this.label = new Label[this.size];
		//this.placeHolderNames = new String[this.size];
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
		this.allowedValuesCombo = new Combo[this.size];

		/*this.classSelected = new FastCodeType[this.size];
		this.fileSelected = new FastCodeFile[this.size];
		this.packageSelected = new FastCodePackage[this.size];
		this.folderSelected = new FastCodeFolder[this.size];
		this.projectSelected = new FastCodeProject[this.size];*/
		this.valuesObj = new Object[this.size];
		// final Text[] countText = new Text[this.size];
		// labelText = this.returnValuesData.getLabelText();
		// //labelTestStr.toString().split(COMMA);
		// this.valueTypeArray = this.returnValuesData.getValueTypes();
		// //this.valueType.toString().split(COMMA);

		for (int count = 0; count < this.size; count++) {
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
			//this.placeHolderNames[count] =
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
			this.label[count].setLayoutData(gridDataLabel);

			/*
			 * if (labelName.contains(COLON)) { final String[] classFileDet =
			 * labelText[count].split(COLON); this.placeHolderName =
			 * classFileDet[0]; classOrFile = classFileDet[1]; if
			 * (valueType.equals(RETURN_TYPES.CLASS.getValue())) {
			 * this.requireClass = true; createClassSelectionPane(composite); }
			 * else if (valueType.equals(RETURN_TYPES.FILE.getValue())) {
			 * this.requireFile = true; createFileSelectionPane(composite); }
			 * classOrFile = EMPTY_STR;
			 */
			if (valueType.equals(RETURN_TYPES.CLASS.getValue())) {
				createClassSelectionPane(composite, count, project, pattern, enabled);
			} else if (valueType.equals(RETURN_TYPES.FILE.getValue())) {
				createFileSelectionPane(composite, count, project, pattern, enabled);
			} else if (valueType.equals(RETURN_TYPES.FOLDER.getValue())) {
				createFolderSelectionPane(composite, count, defaultValue, project, pattern, enabled);
			} else if (valueType.equals(RETURN_TYPES.PACKAGE.getValue())) {
				createPackageSelectionPane(composite, count, defaultValue, pattern, enabled);
			} else if (valueType.equalsIgnoreCase(RETURN_TYPES.PROJECT.getValue())
					|| valueType.equalsIgnoreCase(RETURN_TYPES.JAVAPROJECT.getValue())) {
				createProjectSelectionPane(composite, valueType, count, defaultValue, pattern, enabled);
			} else if (valueType.equalsIgnoreCase(RETURN_TYPES.LOCALVAR.getValue())) {
				createLocalVarSelectionPane(composite, count, defaultValue, pattern, enabled, type);
			} else if (valueType.equalsIgnoreCase(RETURN_TYPES.BOOLEAN.getValue())) {
				createBooleanParamCheckBox(composite, count, defaultValue, enabled);
			} else if (!isEmpty(allowedValues)) {
				createDropDown(composite, count, allowedValues, defaultValue, required, enabled);
			} else if (valueType.equalsIgnoreCase(RETURN_TYPES.INTERFACE.getValue())) {
				createInterfaceSelectionPane(composite, count, defaultValue, pattern, enabled);
			} else if (valueType.equalsIgnoreCase(RETURN_TYPES.ENUMERATION.getValue())) {
				craeteEnumSelectionPane(composite, count, defaultValue, pattern, enabled);
			}
			/*
				* else if (valueType.matches("[*\\s+*]")) { this.arrayData = true;
				* final String values = valueType.substring(1, valueType.length());
				* createComboBoxPane(composite, values); }
				*/
			else {
				final GridData gridDataText = new GridData();
				gridDataText.grabExcessHorizontalSpace = true;

				this.valueText[count] = new Text(composite, SWT.BORDER);
				this.valueText[count].setLayoutData(gridDataText);
				gridDataText.minimumWidth = 250;
				this.valueText[count].setEnabled(enabled);
				this.valueText[count].setText(defaultValue);

				/*
				 * if (this.valueTypeArray.length == 1) { // check this scenario
				 * this.valueText[count].setToolTipText("String"); //chk when it
				 * comes here } else {
				 */
				this.valueText[count].setToolTipText(valueType);
				// }
				//this.valueText[count].setText(this.returnValuesData.getDefaultValue());
				final boolean req = required;
				this.valueText[count].addModifyListener(new ModifyListener() {

					@Override
					public void modifyText(final ModifyEvent e) {
						final Text text = (Text) e.widget;
						final String value = text.getText();

						if (req) {
							if (isEmpty(value)) {
								ReturnValuesDialog_old.this.setErrorMessage("Please enter a value.");
								text.setFocus();
							} else {
								ReturnValuesDialog_old.this.setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
							}
						}
						if (text.getToolTipText().equals("double") || text.getToolTipText().equals("Double")) {
							try {
								Double.parseDouble(value);
								ReturnValuesDialog_old.this.setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
							} catch (final NumberFormatException exception) {
								ReturnValuesDialog_old.this.setErrorMessage("Please enter a " + text.getToolTipText() + " value.");
								text.setFocus();
							}
						} else if (text.getToolTipText().equals("int") || text.getToolTipText().equals("Integer")) {
							try {
								final int intVal = Integer.parseInt(value);
								if (!isEmpty(min)) {
									final int minVal = Integer.parseInt(min);
									if (intVal < minVal) {
										ReturnValuesDialog_old.this.setErrorMessage("Please enter a value greater than " + min + ".");
										text.setFocus();
										return;
									} else {
										ReturnValuesDialog_old.this.setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
									}
								}
								if (!isEmpty(max)) {
									final int maxVal = Integer.parseInt(max);
									if (intVal > maxVal) {
										ReturnValuesDialog_old.this.setErrorMessage("Please enter a value lesser than " + max + ".");
										text.setFocus();
										return;
									} else {
										ReturnValuesDialog_old.this.setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
									}
								}
								//ReturnValuesDialog.this.setErrorMessage(ReturnValuesDialog.this.defaultMessage);
							} catch (final NumberFormatException exception) {
								ReturnValuesDialog_old.this.setErrorMessage("Please enter a " + text.getToolTipText() + " value.");
								text.setFocus();
							}
						} else if (text.getToolTipText().equals("float") || text.getToolTipText().equals("Float")) {
							try {
								Float.parseFloat(value);
								ReturnValuesDialog_old.this.setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
							} catch (final NumberFormatException exception) {
								ReturnValuesDialog_old.this.setErrorMessage("Please enter a " + text.getToolTipText() + " value.");
								text.setFocus();
							}
						} else if (text.getToolTipText().equals("boolean") || text.getToolTipText().equals("Boolean")) {
							if (value.equalsIgnoreCase(TRUE_STR) || value.equalsIgnoreCase(FALSE_STR)) {
								ReturnValuesDialog_old.this.setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
							} else {
								ReturnValuesDialog_old.this.setErrorMessage("Please enter a " + text.getToolTipText() + " value.");
								text.setFocus();
							}

						} else if (text.getToolTipText().equals("char")) {
							if (value.length() == 1) {
								ReturnValuesDialog_old.this.setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
							} else {
								ReturnValuesDialog_old.this.setErrorMessage("Please enter a " + text.getToolTipText() + " value.");
								text.setFocus();
							}
						} else if (text.getToolTipText().equals("long")) {
							try {
								Long.parseLong(value);
								ReturnValuesDialog_old.this.setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
							} catch (final NumberFormatException exception) {
								ReturnValuesDialog_old.this.setErrorMessage("Please enter a " + text.getToolTipText() + " value.");
								text.setFocus();
							}
						} else if (text.getToolTipText().equals("short")) {
							try {
								Short.parseShort(value);
								ReturnValuesDialog_old.this.setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
							} catch (final NumberFormatException exception) {
								ReturnValuesDialog_old.this.setErrorMessage("Please enter a " + text.getToolTipText() + " value.");
								text.setFocus();
							}
						} else if (text.getToolTipText().equals("byte")) {
							try {
								Byte.parseByte(value);
								ReturnValuesDialog_old.this.setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
							} catch (final NumberFormatException exception) {
								ReturnValuesDialog_old.this.setErrorMessage("Please enter a " + text.getToolTipText() + " value.");
								text.setFocus();
							}
						}
						if (text.getToolTipText().equals(RETURN_TYPES.INTRANGE.getValue())) {
							if (!isEmpty(max)) {
								if (Integer.parseInt(max) != 0) {
									try {
										if (!isEmpty(value)) {
											final IntRange intRange = parseIntrange(value);

											validateIntRange(min, max, text, intRange);
										}
									} catch (final Exception ex) {
										ReturnValuesDialog_old.this.setErrorMessage(ex.getMessage());
									}
								}
							}
						}
					}
				});
			}
		}

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
				setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
			}
		}
		if (!isEmpty(max)) {
			final int maxVal = Integer.parseInt(max);
			if (intRange.getEnd() > maxVal) {
				setErrorMessage("Please enter a value lesser than " + max + " for end.");
				text.setFocus();
				return;
			} else {
				setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
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
	 * @param count
	 * @param allowedValues
	 * @param defaultValue
	 * @param required
	 * @param enabled
	 */
	private void createDropDown(final Composite parent, final int count, final String allowedValues, final String defaultValue,
			final boolean required, final boolean enabled) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataCombo = new GridData();
		gridDataCombo.grabExcessHorizontalSpace = true;

		this.allowedValuesCombo[count] = new Combo(composite, SWT.NONE | SWT.READ_ONLY);
		this.allowedValuesCombo[count].setSize(200, 20);
		this.allowedValuesCombo[count].setLayoutData(gridDataCombo);
		this.allowedValuesCombo[count].setToolTipText(Integer.toString(count));
		this.allowedValuesCombo[count].setEnabled(enabled);
		gridDataCombo.minimumWidth = 500;

		if (!required) {
			this.allowedValuesCombo[count].add(EMPTY_STR);
		}
		for (final String allowedValue : allowedValues.split(SPACE)) {
			this.allowedValuesCombo[count].add(allowedValue);
		}

		if (!isEmpty(defaultValue)) {
			this.allowedValuesCombo[count].setText(defaultValue);
		}
		this.valuesObj[count] = defaultValue;
		this.allowedValuesCombo[count].addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String selectedValue = ((Combo) event.widget).getText();
				ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = selectedValue;
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * @param parent
	 * @param count
	 * @param defaultValue
	 * @param pattern
	 * @param enabled
	 */
	private void craeteEnumSelectionPane(final Composite parent, final int count, final String defaultValue, final String pattern,
			final boolean enabled) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataCombo = new GridData();
		gridDataCombo.grabExcessHorizontalSpace = true;

		this.enumNameCombo[count] = new Combo(composite, SWT.NONE);// new
		// Text(composite,
		// SWT.BORDER);
		this.enumNameCombo[count].setSize(200, 20);
		this.enumNameCombo[count].setLayoutData(gridDataCombo);
		this.enumNameCombo[count].setToolTipText(Integer.toString(count));
		this.enumNameCombo[count].setEnabled(enabled);
		gridDataCombo.minimumWidth = 500;

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (!fastCodeCache.getTypeSet().isEmpty()) {
			for (final IType type : fastCodeCache.getTypeSet()) {
				try {
					if (type.isEnum()) {
						this.enumNameCombo[count].add(type.getFullyQualifiedName());
					}
				} catch (final Exception ex) {

				}
			}
		}
		this.enumNameCombo[count].addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final String selectedEnumName = ((Combo) event.widget).getText();
				try {
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.isEnum()) {
								if (type.getFullyQualifiedName().equals(selectedEnumName)) {
									ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = new FastCodeType(
											type);
								}
							}
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.enumNameCombo[count].addFocusListener(new FocusListener() {

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
					try {
						final IType inputEnumType = getTypeFromWorkspace(inputEnumName);
						if (inputEnumType != null) {
							ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Combo) e.widget).getToolTipText())] = new FastCodeType(
									inputEnumType);
							if (!fastCodeCache.getTypeSet().contains(inputEnumType)) {
								fastCodeCache.getTypeSet().add(inputEnumType);
							}
						} else {
							setErrorMessage("Enumeration does not exist,Please enter an enumeration  name ");
						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.enumNameCombo[count].addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent event) {

			}
		});

		final GridData gridDataButton = new GridData();
		final Button browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse");
		browse.setLayoutData(gridDataButton);
		browse.setToolTipText(Integer.toString(count));
		browse.setEnabled(enabled);
		browse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				SelectionDialog selectionDialog = null;
				try {
					selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
							SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ENUMS, false, EMPTY_STR);

					selectionDialog.setTitle(ReturnValuesDialog_old.this.selectMsg + "Enumeration");
					selectionDialog.setMessage(ReturnValuesDialog_old.this.selectMsg + "enumeration");

					if (selectionDialog.open() == CANCEL) {
						return;
					}

					final IType browsedEnumType = (IType) selectionDialog.getResult()[0];
					boolean addItem = true;
					if (ReturnValuesDialog_old.this.enumNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())].getItems() != null) {
						for (final String existingEnum : ReturnValuesDialog_old.this.enumNameCombo[Integer.parseInt(((Button) e.widget)
								.getToolTipText())].getItems()) {
							if (existingEnum.equals(browsedEnumType.getFullyQualifiedName())) {
								if (!existingEnum.equals(ReturnValuesDialog_old.this.enumNameCombo[Integer.parseInt(((Button) e.widget)
										.getToolTipText())].getText())) {
									ReturnValuesDialog_old.this.enumNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
											.select(ReturnValuesDialog_old.this.enumNameCombo[Integer.parseInt(((Button) e.widget)
													.getToolTipText())].indexOf(existingEnum));
								}
								addItem = false;
								break;
							}
						}
					}
					if (addItem) {
						ReturnValuesDialog_old.this.enumNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())].add(browsedEnumType
								.getFullyQualifiedName());
						ReturnValuesDialog_old.this.enumNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
								.select(ReturnValuesDialog_old.this.enumNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
										.getItemCount() - 1);
					}
					ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Button) e.widget).getToolTipText())] = new FastCodeType(
							browsedEnumType);
					if (!fastCodeCache.getTypeSet().contains(selectionDialog.getResult()[0])) {
						fastCodeCache.getTypeSet().add((IType) selectionDialog.getResult()[0]);
					}
				} catch (final JavaModelException ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * @param composite
	 * @param count
	 * @param pattern
	 * @param defaultValue
	 * @param enabled
	 */
	private void createInterfaceSelectionPane(final Composite parent, final int count, final String defaultValue, final String pattern,
			final boolean enabled) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataCombo = new GridData();
		gridDataCombo.grabExcessHorizontalSpace = true;

		this.interfaceNameCombo[count] = new Combo(composite, SWT.NONE);// new
		// Text(composite,
		// SWT.BORDER);
		this.interfaceNameCombo[count].setSize(200, 20);
		this.interfaceNameCombo[count].setLayoutData(gridDataCombo);
		this.interfaceNameCombo[count].setToolTipText(Integer.toString(count));
		this.interfaceNameCombo[count].setEnabled(enabled);
		gridDataCombo.minimumWidth = 500;

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		try {
			if (this.compilationUnit != null && this.compilationUnit.getPrimary().findPrimaryType().isInterface()) {
				this.currentInterface = this.compilationUnit.getPrimary().findPrimaryType();
				this.interfaceNameCombo[count].add(ENCLOSING_INTERFACE_STR + HYPHEN + this.currentInterface.getFullyQualifiedName());
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
						if (this.interfaceNameCombo[count].getItems() != null) {
							for (final String existingInterface : this.interfaceNameCombo[count].getItems()) {
								if (existingInterface.contains(ENCLOSING_INTERFACE_STR)) {
									continue;
								}
								if (existingInterface.equals(type.getFullyQualifiedName())) {
									addItem = false;
									break;
								}
							}
							if (addItem) {
								this.interfaceNameCombo[count].add(type.getFullyQualifiedName());
							}
						}
					}
				} catch (final JavaModelException ex) {
					ex.printStackTrace();
				}
			}
		}
		this.interfaceNameCombo[count].addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				String selectedInterfaceName = ((Combo) event.widget).getText(); //ReturnValuesDialog.this.classNameCombo.getText();
				try {
					if (selectedInterfaceName.contains(ENCLOSING_INTERFACE_STR)) {
						selectedInterfaceName = ReturnValuesDialog_old.this.currentInterface.getFullyQualifiedName();
					}
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.isInterface()) {
								if (type.getFullyQualifiedName().equals(selectedInterfaceName)) {
									ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = new FastCodeType(
											type);
								}
							}
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.interfaceNameCombo[count].addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				String inputInterfaceName = ((Combo) e.widget).getText();
				if (!isEmpty(inputInterfaceName)) {
					if (inputInterfaceName.contains(ENCLOSING_INTERFACE_STR)) {
						inputInterfaceName = ReturnValuesDialog_old.this.currentInterface.getFullyQualifiedName();
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
					try {
						final IType inputInterfaceType = getTypeFromWorkspace(inputInterfaceName);
						if (inputInterfaceType != null) {
							ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Combo) e.widget).getToolTipText())] = new FastCodeType(
									inputInterfaceType);
							if (!fastCodeCache.getTypeSet().contains(inputInterfaceType)) {
								fastCodeCache.getTypeSet().add(inputInterfaceType);
							}
						} else {
							setErrorMessage("Interface does not exist,Please enter an existing interface name ");
						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.interfaceNameCombo[count].addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent event) {
				if (isEmpty(((Combo) event.widget).getText())) {
					setErrorMessage("Please choose a Class");
				} else {
					setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
				}
			}
		});

		final GridData gridDataButton = new GridData();
		final Button browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse");
		browse.setLayoutData(gridDataButton);
		browse.setToolTipText(Integer.toString(count));
		browse.setEnabled(enabled);
		browse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent e) {
				SelectionDialog selectionDialog = null;
				try {
					selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
							SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_INTERFACES, false, EMPTY_STR);

					selectionDialog.setTitle(ReturnValuesDialog_old.this.selectMsg + "Interface");
					selectionDialog.setMessage(ReturnValuesDialog_old.this.selectMsg + "Interface");

					if (selectionDialog.open() == CANCEL) {
						return;
					}

					final IType browsedInterfaceType = (IType) selectionDialog.getResult()[0];
					boolean addItem = true;
					if (ReturnValuesDialog_old.this.interfaceNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())].getItems() != null) {
						for (final String existingInterface : ReturnValuesDialog_old.this.interfaceNameCombo[Integer
								.parseInt(((Button) e.widget).getToolTipText())].getItems()) {
							if (existingInterface.equals(browsedInterfaceType.getFullyQualifiedName())) {
								if (!existingInterface.equals(ReturnValuesDialog_old.this.interfaceNameCombo[Integer
										.parseInt(((Button) e.widget).getToolTipText())].getText())) {
									ReturnValuesDialog_old.this.interfaceNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
											.select(ReturnValuesDialog_old.this.interfaceNameCombo[Integer.parseInt(((Button) e.widget)
													.getToolTipText())].indexOf(existingInterface));
								}
								addItem = false;
								break;
							}
						}
					}
					if (addItem) {
						ReturnValuesDialog_old.this.interfaceNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
								.add(browsedInterfaceType.getFullyQualifiedName());
						ReturnValuesDialog_old.this.interfaceNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
								.select(ReturnValuesDialog_old.this.interfaceNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
										.getItemCount() - 1);
					}
					ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Button) e.widget).getToolTipText())] = new FastCodeType(
							browsedInterfaceType);
					if (!fastCodeCache.getTypeSet().contains(selectionDialog.getResult()[0])) {
						fastCodeCache.getTypeSet().add((IType) selectionDialog.getResult()[0]);
					}
				} catch (final JavaModelException ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * @param parent
	 * @param count
	 * @param defaultValue
	 * @param enabled
	 */
	private void createBooleanParamCheckBox(final Composite parent, final int count, final String defaultValue, final boolean enabled) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(gridDataLabel);

		this.booleanParam[count] = new Button(composite, SWT.CHECK);
		this.booleanParam[count].setToolTipText(Integer.toString(count));
		this.booleanParam[count].setSelection(Boolean.valueOf(defaultValue));
		this.valuesObj[count] = Boolean.valueOf(defaultValue);
		this.booleanParam[count].setEnabled(enabled);
		this.booleanParam[count].addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

				ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Button) event.widget).getToolTipText())] = ReturnValuesDialog_old.this.booleanParam[Integer
						.parseInt(((Button) event.widget).getToolTipText())].getSelection();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});
	}

	/**
	 * @param parent
	 * @param count
	 * @param defaultValue
	 * @param pattern
	 * @param enabled
	 * @param type
	 */
	private void createLocalVarSelectionPane(final Composite parent, final int count, final String defaultValue, final String pattern,
			final boolean enabled, final String type) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.localVarCombo[count] = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.localVarCombo[count].setSize(200, 20);
		this.localVarCombo[count].setLayoutData(gridDataText);
		this.localVarCombo[count].setToolTipText(Integer.toString(count));
		this.localVarCombo[count].setEnabled(enabled);
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
			this.localVarCombo[count].add(localVar.getType().getName() + SPACE + localVar.getName());
		}
		this.localVarCombo[count].addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Combo loclVar = (Combo) event.widget;
				ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(loclVar.getToolTipText())] = loclVar.getText().split(SPACE)[1].trim();
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		this.localVarCombo[count].addModifyListener(new ModifyListener() {

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
	 * @param count
	 * @param defaultValue
	 * @param pattern
	 * @param enabled
	 */
	private void createProjectSelectionPane(final Composite parent, final String valueType, final int count, final String defaultValue,
			final String pattern, final boolean enabled) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.projectCombo[count] = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		this.projectCombo[count].setSize(200, 20);
		this.projectCombo[count].setLayoutData(gridDataText);
		this.projectCombo[count].setToolTipText(Integer.toString(count));
		this.projectCombo[count].setEnabled(enabled);
		gridDataText.minimumWidth = 500;

		final int curPrjIndex;
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
			this.projectCombo[count].add(prj.getName());
			this.prjMap.put(prj.getName(), prj);

		}

		if (this.returnValuesData.getJavaProject() != null) {
			this.projectCombo[count].select(this.projectCombo[count].indexOf(this.returnValuesData.getJavaProject().getElementName()));
			this.valuesObj[count] = new FastCodeProject(this.prjMap.get(this.projectCombo[count].getText()));
		}

		this.projectCombo[count].addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Combo prjCombo = (Combo) event.widget;
				ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(prjCombo.getToolTipText())] = new FastCodeProject(
						ReturnValuesDialog_old.this.prjMap.get(prjCombo.getText())); //ReturnValuesDialog.this.projectCombo[count].getText()
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

		this.projectCombo[count].addModifyListener(new ModifyListener() {

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

	}

	/**
	 * @param parent
	 * @param count
	 * @param defaultValue
	 * @param pattern
	 * @param enabled
	 */
	private void createPackageSelectionPane(final Composite parent, final int count, final String project, final String pattern,
			final boolean enabled) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataLabel = new GridData();
		final Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(gridDataLabel);

		final GridData gridDataText = new GridData();
		gridDataText.grabExcessHorizontalSpace = true;

		this.packageCombo[count] = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);// new
		// Text(composite,
		// SWT.BORDER);
		this.packageCombo[count].setSize(200, 20);
		this.packageCombo[count].setLayoutData(gridDataText);
		this.packageCombo[count].setToolTipText(Integer.toString(count));
		this.packageCombo[count].setEnabled(enabled);
		gridDataText.minimumWidth = 500;

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (this.compilationUnit != null) {
			this.currentPackage = this.compilationUnit.getPrimary().findPrimaryType().getPackageFragment();
			this.packageCombo[count].add(ENCLOSING_PACKAGE_STR + HYPHEN + getAlteredPackageName(this.currentPackage));
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
				if (this.packageCombo[count].getItems() != null) {
					for (final String existingPkg : this.packageCombo[count].getItems()) {
						if (existingPkg.contains(ENCLOSING_PACKAGE_STR)) {
							continue;
						}
						if (existingPkg.equals(getAlteredPackageName(pkgFrgmt))) {
							addItem = false;
							break;
						}
					}
					if (addItem) {
						this.packageCombo[count].add(getAlteredPackageName(pkgFrgmt));
					}
				}
			}
		}
		this.packageCombo[count].addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Combo pkgCombo = (Combo) event.widget;
				String selectedPkgName = pkgCombo.getText();
				if (selectedPkgName.contains(ENCLOSING_PACKAGE_STR)) {
					selectedPkgName = ReturnValuesDialog_old.this.currentPackage.getElementName();
				}
				try {
					for (final IPackageFragment pkg : fastCodeCache.getPackageSet()) {
						if (getAlteredPackageName(pkg).equals(selectedPkgName)) {
							ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = new FastCodePackage(
									pkg);
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}

		});
		this.packageCombo[count].addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				final Combo pkgCombo = (Combo) e.widget;
				String inputPkgName = pkgCombo.getText();
				if (!isEmpty(inputPkgName)) {
					if (inputPkgName.contains(ENCLOSING_PACKAGE_STR)) {
						inputPkgName = ReturnValuesDialog_old.this.currentPackage.getElementName();
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
				}
			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		this.packageCombo[count].addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				final Combo pkgCombo = (Combo) e.widget;
				if (isEmpty(pkgCombo.getText())) {
					setErrorMessage("Please choose a package");
				} else {
					setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
				}
			}

		});

		final GridData gridDataButton = new GridData();

		this.packageBrowseButton = new Button(composite, SWT.PUSH);
		this.packageBrowseButton.setText("Browse");
		this.packageBrowseButton.setLayoutData(gridDataButton);
		this.packageBrowseButton.setToolTipText(Integer.toString(count));
		this.packageBrowseButton.setEnabled(enabled);
		this.packageBrowseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				final Button brw = (Button) event.widget;
				try {
					final String srcPath = getDefaultPathFromProject(
							isEmpty(project) ? ReturnValuesDialog_old.this.returnValuesData.getJavaProject() : getIJavaProjectFromName(project),
							"source", EMPTY_STR);
					final IPackageFragment allPackages[] = getPackagesInProject(
							isEmpty(project) ? ReturnValuesDialog_old.this.returnValuesData.getJavaProject() : getIJavaProjectFromName(project),
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
						if (ReturnValuesDialog_old.this.packageCombo[Integer.parseInt(((Button) event.widget).getToolTipText())].getItems() != null) {
							for (final String existingPkg : ReturnValuesDialog_old.this.packageCombo[Integer.parseInt(((Button) event.widget)
									.getToolTipText())].getItems()) {
								if (existingPkg.equals(getAlteredPackageName(packageFragment))) {
									if (!ReturnValuesDialog_old.this.packageCombo[Integer.parseInt(((Button) event.widget).getToolTipText())]
											.getText().equals(existingPkg)) {
										ReturnValuesDialog_old.this.packageCombo[Integer.parseInt(((Button) event.widget).getToolTipText())]
												.select(ReturnValuesDialog_old.this.packageCombo[Integer.parseInt(((Button) event.widget)
														.getToolTipText())].indexOf(existingPkg));

									}
									addItem = false;
									break;
								}
							}
						}
						if (addItem) {
							ReturnValuesDialog_old.this.packageCombo[Integer.parseInt(((Button) event.widget).getToolTipText())]
									.add(getAlteredPackageName(packageFragment));
							ReturnValuesDialog_old.this.packageCombo[Integer.parseInt(((Button) event.widget).getToolTipText())]
									.select(ReturnValuesDialog_old.this.packageCombo[Integer.parseInt(((Button) event.widget).getToolTipText())]
											.getItemCount() - 1);
						}
						if (!fastCodeCache.getPackageSet().contains(packageFragment)) {
							fastCodeCache.getPackageSet().add(packageFragment);
						}
						ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Button) event.widget).getToolTipText())] = new FastCodePackage(
								packageFragment);
					}

				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
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
	 * @param count
	 * @param defaultValue
	 * @param pattern
	 * @param enabled
	 */
	private void createFolderSelectionPane(final Composite parent, final int count, final String defaultValue, final String project,
			final String pattern, final boolean enabled) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataCombo = new GridData();
		gridDataCombo.grabExcessHorizontalSpace = true;

		this.folderNameCombo[count] = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);// new
		// Text(composite,
		// SWT.BORDER);
		this.folderNameCombo[count].setSize(200, 20);
		this.folderNameCombo[count].setLayoutData(gridDataCombo);
		this.folderNameCombo[count].setToolTipText(Integer.toString(count));
		gridDataCombo.minimumWidth = 500;
		this.folderNameCombo[count].setEnabled(enabled);

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
						this.folderNameCombo[count].add(ENCLOSING_FOLDER_STR + HYPHEN + this.currentFolder.getFullPath().toString());

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
				if (this.folderNameCombo[count].getItems() != null) {
					for (final String existingFolder : this.folderNameCombo[count].getItems()) {
						if (existingFolder.contains(ENCLOSING_FOLDER_STR)) {
							continue;
						}
						if (existingFolder.equals(folder.getFullPath().toString())) {
							addItem = false;
							break;

						}
					}
					if (addItem) {
						this.folderNameCombo[count].add(folder.getFullPath().toString());
					}
				}
			}
		}
		this.folderNameCombo[count].addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {

				String selectedFolderPath = ((Combo) event.widget).getText(); // ReturnValuesDialog.this.folderNameCombo.getText();
				if (selectedFolderPath.contains(ENCLOSING_FOLDER_STR)) {
					selectedFolderPath = ReturnValuesDialog_old.this.currentFolder.getFullPath().toString();
				}
				try {
					if (!fcCache.getFolderSet().isEmpty()) {
						for (final IFolder folder : fcCache.getFolderSet()) {
							if (folder.getFullPath().toString().equals(selectedFolderPath)) {
								ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = new FastCodeFolder(
										folder);
							}
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}

		});
		this.folderNameCombo[count].addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				String inputFolderPath = ((Combo) e.widget).getText();
				if (!isEmpty(inputFolderPath)) {
					if (inputFolderPath.contains(ENCLOSING_FOLDER_STR)) {
						inputFolderPath = ReturnValuesDialog_old.this.currentFolder.getFullPath().toString();
					}
					for (final IFolder folder : fcCache.getFolderSet()) {
						if (folder.getFullPath().toString().equals(inputFolderPath)) {
							return;
						}
					}

				}

			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		this.folderNameCombo[count].addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent arg0) {
				if (isEmpty(((Combo) arg0.widget).getText())) {
					setErrorMessage("Please select a folder.");
				} else {
					setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
				}

			}
		});

		final GridData gridDataButton = new GridData();
		final Button browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse");
		browse.setLayoutData(gridDataButton);
		browse.setToolTipText(Integer.toString(count));
		browse.setEnabled(enabled);
		browse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				try {
					Path path = null;

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
						final IFolder folder = ReturnValuesDialog_old.this.returnValuesData.getJavaProject().getProject()
								.getFolder(new Path(srcPath));

						ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Button) e.widget).getToolTipText())] = new FastCodeFolder(
								folder);
						//ReturnValuesDialog.this.folderNameCombo.setText(folder.getFullPath().toString());
						/*for (final Combo fldrCombo : ReturnValuesDialog.this.folderNameCombo) {
							if (((Button) e.widget).getToolTipText().equals(fldrCombo.getToolTipText())) {
								fldrCombo.add(folder.getFullPath().toString());
								fldrCombo.select(fldrCombo.getItemCount() - 1);
							}
						}*/
						boolean addItem = true;
						if (ReturnValuesDialog_old.this.folderNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())].getItems() != null) {
							for (final String existingFolder : ReturnValuesDialog_old.this.folderNameCombo[Integer.parseInt(((Button) e.widget)
									.getToolTipText())].getItems()) {
								if (existingFolder.equals(folder.getFullPath().toString())) {
									if (!existingFolder.equals(ReturnValuesDialog_old.this.folderNameCombo[Integer.parseInt(((Button) e.widget)
											.getToolTipText())].getText())) {
										ReturnValuesDialog_old.this.folderNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
												.select(ReturnValuesDialog_old.this.folderNameCombo[Integer.parseInt(((Button) e.widget)
														.getToolTipText())].indexOf(existingFolder));
									}
									addItem = false;
									break;
								}
							}
						}
						if (addItem) {
							ReturnValuesDialog_old.this.folderNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())].add(folder
									.getFullPath().toString());
							ReturnValuesDialog_old.this.folderNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
									.select(ReturnValuesDialog_old.this.folderNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
											.getItemCount() - 1);
						}
						if (!fcCache.getFolderSet().contains(folder)) {
							fcCache.getFolderSet().add(folder);
						}
					}
				} catch (final Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}

		});
	}

	/**
	 * @param parent
	 * @param count
	 * @param enabled
	 * @param defaultValue
	 */
	private void createClassSelectionPane(final Composite parent, final int count, final String project, final String pattern,
			final boolean enabled) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataCombo = new GridData();
		gridDataCombo.grabExcessHorizontalSpace = true;

		this.classNameCombo[count] = new Combo(composite, SWT.NONE);// new
		// Text(composite,
		// SWT.BORDER);
		this.classNameCombo[count].setSize(200, 20);
		this.classNameCombo[count].setLayoutData(gridDataCombo);
		this.classNameCombo[count].setToolTipText(Integer.toString(count));
		gridDataCombo.minimumWidth = 500;
		this.classNameCombo[count].setEnabled(enabled);

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (this.compilationUnit != null) {
			this.currentClass = this.compilationUnit.getPrimary().findPrimaryType();
			this.classNameCombo[count].add(ENCLOSING_CLASS_STR + HYPHEN + this.currentClass.getFullyQualifiedName());
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
				if (this.classNameCombo[count].getItems() != null) {
					for (final String existingClass : this.classNameCombo[count].getItems()) {
						if (existingClass.contains(ENCLOSING_CLASS_STR)) {
							continue;
						}
						if (existingClass.equals(type.getFullyQualifiedName())) {
							addItem = false;
							break;
						}
					}
					if (addItem) {
						this.classNameCombo[count].add(type.getFullyQualifiedName());
					}
				}
			}
		}

		this.classNameCombo[count].addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				String selectedFromClassName = ((Combo) event.widget).getText(); //ReturnValuesDialog.this.classNameCombo.getText();
				try {
					if (selectedFromClassName.contains(ENCLOSING_CLASS_STR)) {
						selectedFromClassName = ReturnValuesDialog_old.this.currentClass.getFullyQualifiedName();
					}
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.getFullyQualifiedName().equals(selectedFromClassName)) {
								ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = new FastCodeType(
										type);
							}
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.classNameCombo[count].addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				String inputFromClassName = ((Combo) e.widget).getText();
				if (!isEmpty(inputFromClassName)) {
					if (inputFromClassName.contains(ENCLOSING_CLASS_STR)) {
						inputFromClassName = ReturnValuesDialog_old.this.currentClass.getFullyQualifiedName();
					}
					if (!fastCodeCache.getTypeSet().isEmpty()) {
						for (final IType type : fastCodeCache.getTypeSet()) {
							if (type.getFullyQualifiedName().equals(inputFromClassName)) {
								return;
							}
						}
					}
					try {
						final IType inputClassType = getTypeFromWorkspace(inputFromClassName);
						if (inputClassType != null) {
							ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Combo) e.widget).getToolTipText())] = new FastCodeType(
									inputClassType);
							if (!fastCodeCache.getTypeSet().contains(inputClassType)) {
								fastCodeCache.getTypeSet().add(inputClassType);
							}
						} else {
							setErrorMessage("Class does not exist,Please enter an existing class name ");
						}
					} catch (final Exception ex) {
						ex.printStackTrace();
					}
				}
			}

			@Override
			public void focusGained(final FocusEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.classNameCombo[count].addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {

				if (isEmpty(((Combo) e.widget).getText())) {
					setErrorMessage("Please choose a Class");
				} else {
					setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
				}
			}

		});

		final GridData gridDataButton = new GridData();
		final Button browse = new Button(composite, SWT.PUSH);
		browse.setText("Browse");
		browse.setLayoutData(gridDataButton);
		browse.setToolTipText(Integer.toString(count));
		browse.setEnabled(enabled);
		browse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {

				SelectionDialog selectionDialog = null;
				try {
					IProject prj = null;
					if (!isEmpty(project)) {
						prj = getIJavaProjectFromName(project).getProject();
					}
					final IProject prjArr[] = new IProject[] { prj };
					selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
							isEmpty(project) ? SearchEngine.createWorkspaceScope() : SearchEngine.createJavaSearchScope(prjArr),
							IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, pattern);
					selectionDialog.setTitle(ReturnValuesDialog_old.this.selectMsg + "Class");
					selectionDialog.setMessage(ReturnValuesDialog_old.this.selectMsg + "Class");

					if (selectionDialog.open() == CANCEL) {
						return;
					}
					final IType browsedClassType = (IType) selectionDialog.getResult()[0];
					boolean addItem = true;
					if (ReturnValuesDialog_old.this.classNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())].getItems() != null) {
						for (final String existingClass : ReturnValuesDialog_old.this.classNameCombo[Integer.parseInt(((Button) e.widget)
								.getToolTipText())].getItems()) {
							if (existingClass.equals(browsedClassType.getFullyQualifiedName())) {
								if (!existingClass.equals(ReturnValuesDialog_old.this.classNameCombo[Integer.parseInt(((Button) e.widget)
										.getToolTipText())].getText())) {
									ReturnValuesDialog_old.this.classNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
											.select(ReturnValuesDialog_old.this.classNameCombo[Integer.parseInt(((Button) e.widget)
													.getToolTipText())].indexOf(existingClass));
								}
								addItem = false;
								break;
							}
						}
					}
					if (addItem) {
						ReturnValuesDialog_old.this.classNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())].add(browsedClassType
								.getFullyQualifiedName());
						ReturnValuesDialog_old.this.classNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
								.select(ReturnValuesDialog_old.this.classNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
										.getItemCount() - 1);
					}
					ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Button) e.widget).getToolTipText())] = new FastCodeType(
							browsedClassType);
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
					// TODO Auto-generated catch block
					ex.printStackTrace();
				} catch (final Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}

		});
	}

	/**
	 * @param parent
	 * @param count
	 * @param defaultValue
	 * @param pattern
	 * @param enabled
	 */
	private void createFileSelectionPane(final Composite parent, final int count, final String project, final String pattern,
			final boolean enabled) {
		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		final GridData gridDataCombo = new GridData();
		gridDataCombo.grabExcessHorizontalSpace = true;

		this.fileNameCombo[count] = new Combo(composite, SWT.NONE);// new
		// Text(composite,
		// SWT.BORDER);
		this.fileNameCombo[count].setSize(200, 20);
		this.fileNameCombo[count].setLayoutData(gridDataCombo);
		this.fileNameCombo[count].setToolTipText(Integer.toString(count));
		gridDataCombo.minimumWidth = 500;
		this.fileNameCombo[count].setEnabled(enabled);

		final FastCodeCache fastCodeCache = FastCodeCache.getInstance();
		if (this.compilationUnit == null) {
			final IFile file = (IFile) this.returnValuesData.getEditorPart().getEditorInput().getAdapter(IFile.class);
			this.currentFile = file;
			this.fileNameCombo[count].add(ENCLOSING_FILE_STR + HYPHEN + this.currentFile.getFullPath().toString());
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
				if (this.fileNameCombo[count].getItems() != null) {
					for (final String existingFile : this.fileNameCombo[count].getItems()) {
						if (existingFile.contains(ENCLOSING_FILE_STR)) {
							continue;
						}
						if (existingFile.equals(file.getFullPath().toString())) {
							addItem = false;
							break;
						}
					}
					if (addItem) {
						this.fileNameCombo[count].add(file.getFullPath().toString());
					}
				}

			}
		}

		this.fileNameCombo[count].addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent event) {
				String selectedFileName = ((Combo) event.widget).getText(); //ReturnValuesDialog.this.fileNameCombo.getText();
				try {
					if (selectedFileName.contains(ENCLOSING_FILE_STR)) {
						selectedFileName = ReturnValuesDialog_old.this.currentFile.getFullPath().toString();
					}
					if (!fastCodeCache.getFileSet().isEmpty()) {
						for (final IFile file : fastCodeCache.getFileSet()) {
							if (file.getName().equals(selectedFileName.substring(selectedFileName.lastIndexOf(FORWARD_SLASH) + 1))) {
								ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Combo) event.widget).getToolTipText())] = new FastCodeFile(
										file);//new FastCodeFile(file.getName(), file.getProjectRelativePath().toString());
							}
						}
					}
				} catch (final Exception ex) {
					ex.printStackTrace();
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		this.fileNameCombo[count].addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {

				if (isEmpty(((Combo) e.widget).getText())) {
					setErrorMessage("Please choose a file");
				} else {
					setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
				}
			}
		});
		this.fileNameCombo[count].addFocusListener(new FocusListener() {

			@Override
			public void focusLost(final FocusEvent e) {
				setErrorMessage(ReturnValuesDialog_old.this.defaultMessage);
				String inputFileName = ((Combo) e.widget).getText();
				if (!isEmpty(inputFileName)) {
					if (inputFileName.contains(ENCLOSING_FILE_STR)) {
						inputFileName = ReturnValuesDialog_old.this.currentFile.getFullPath().toString();
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
								ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Combo) e.widget).getToolTipText())] = inputFastCodeFile;
								if (!fastCodeCache.getFileSet().contains(inputFile)) {
									fastCodeCache.getFileSet().add(inputFile);
								}
							} else {
								ReturnValuesDialog_old.this.setErrorMessage("File does not exist,Please enter an existing file name");
							}
						} catch (final Exception ex) {
							ex.printStackTrace();
						}

					} else {
						ReturnValuesDialog_old.this
								.setErrorMessage("Please enter file name with full path like /Project Name/....../file name ");
					}
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
		browse.setToolTipText(Integer.toString(count));
		browse.setEnabled(enabled);
		browse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
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

				resourceDialog.setTitle(ReturnValuesDialog_old.this.selectMsg + "File");
				resourceDialog.setMessage(ReturnValuesDialog_old.this.selectMsg + "File");

				if (resourceDialog.open() == CANCEL) {
					return;
				}

				final IFile browseFile = (IFile) resourceDialog.getResult()[0];
				final String browsePath = browseFile.getFullPath().toString();
				ReturnValuesDialog_old.this.valuesObj[Integer.parseInt(((Button) e.widget).getToolTipText())] = new FastCodeFile(browseFile);
				boolean addItem = true;
				if (ReturnValuesDialog_old.this.fileNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())].getItems() != null) {
					for (final String existingFile : ReturnValuesDialog_old.this.fileNameCombo[Integer.parseInt(((Button) e.widget)
							.getToolTipText())].getItems()) {
						if (existingFile.equals(browsePath)) {
							if (!existingFile.equals(ReturnValuesDialog_old.this.fileNameCombo[Integer.parseInt(((Button) e.widget)
									.getToolTipText())].getText())) {
								ReturnValuesDialog_old.this.fileNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())].select(ReturnValuesDialog_old.this.fileNameCombo[Integer
										.parseInt(((Button) e.widget).getToolTipText())].indexOf(existingFile));
							}
							addItem = false;
							break;
						}
					}
				}
				if (addItem) {
					ReturnValuesDialog_old.this.fileNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())].add(browsePath);
					ReturnValuesDialog_old.this.fileNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
							.select(ReturnValuesDialog_old.this.fileNameCombo[Integer.parseInt(((Button) e.widget).getToolTipText())]
									.getItemCount() - 1);
				}
				if (!fastCodeCache.getFileSet().contains(browseFile)) {
					fastCodeCache.getFileSet().add(browseFile);
				}

			}

		});
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

		final GridData errText = new GridData(200, 30);
		errText.grabExcessHorizontalSpace = true;

		this.errorMessageText = new Text(parent, SWT.READ_ONLY | SWT.WRAP);
		// this.errorMessageText.setBackground(new Color(null, 255, 255, 255));
		//this.errorMessageText.setForeground(new Color(null, 255, 0, 0));
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
		final ReturnValuesDialog_old returnValuesDialog = new ReturnValuesDialog_old(shell);

		returnValuesDialog.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
