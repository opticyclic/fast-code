package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.ASTERISK_CHAR;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.common.FastCodeConstants.MYSQL_CONNECTION_ERROR_SQLSTATE;
import static org.fastcode.common.FastCodeConstants.NEWLINE;
import static org.fastcode.common.FastCodeConstants.ORACLE_CONNECTION_ERROR_SQLSTATE1;
import static org.fastcode.common.FastCodeConstants.ORACLE_CONNECTION_ERROR_SQLSTATE2;
import static org.fastcode.common.FastCodeConstants.POSTGRESQL_CONNECTION_ERROR_SQLSTATE;
import static org.fastcode.preferences.PreferenceConstants.P_DATABASE_CONN_DATA;
import static org.fastcode.preferences.PreferenceConstants.P_DBCONN_FIELD_DELIMITER;
import static org.fastcode.preferences.PreferenceConstants.P_DBCONN_RECORD_DELIMITER;
import static org.fastcode.util.StringUtil.isEmpty;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.preference.IPreferenceStore;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.fastcode.common.DatabaseDetails;
import org.fastcode.common.FastCodeColor;
import org.fastcode.util.ConnectToDatabase;
import org.fastcode.util.DataBaseTypeInfo;

public class DatabaseConnectionDialog extends TrayDialog {

	private Composite				parent;

	private Text					errorMessageText;
	Connection						con;

	private Object					errorMessage;
	private Object					warningMessage;

	private String					currDatabaseType;
	private String					currDatabaseName;
	private String					currHostAddress;
	private Integer					currPortNumber;
	private String					currUserName;
	private String					currPassword;
	private String					driverFQName;
	private String					driverPrj;
	private boolean					isdefaultConn;

	Text							databaseMachine;
	Combo							databaseTypeField;
	Text							databaseNameField;
	Text							databaseHost;
	Text							databasePort;
	Text							userNameField;
	Text							passwordField;
	Button							defaultCheckBox;
	Text							driverClass;
	Button							browseButton;

	private DatabaseDetails			databaseConndata	= null;

	private final IPreferenceStore	preferenceStore;

	private static String[]			databaseTypes		= new String[] { "mysql", "oracle", "postgresql" };

	private boolean					editConn			= false;

	private String					currConnString;

	final String					RECORD_DELIMITER;
	final String					FIELD_DELIMITER;

	public DatabaseConnectionDialog(final Shell shell, final IPreferenceStore preferenceStore) {
		super(shell);
		this.preferenceStore = preferenceStore;
		this.RECORD_DELIMITER = preferenceStore.getString(P_DBCONN_RECORD_DELIMITER);
		this.FIELD_DELIMITER = preferenceStore.getString(P_DBCONN_FIELD_DELIMITER);

	}

	public DatabaseConnectionDialog(final Shell shell, final IPreferenceStore preferenceStore, final DatabaseDetails databaseConnection) {
		super(shell);
		this.databaseConndata = databaseConnection;
		this.preferenceStore = preferenceStore;
		this.editConn = true;
		this.RECORD_DELIMITER = preferenceStore.getString(P_DBCONN_RECORD_DELIMITER);
		this.FIELD_DELIMITER = preferenceStore.getString(P_DBCONN_FIELD_DELIMITER);
		System.out.println(DatabaseConnectionSettings.getInstance().getConnMap().size());

	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		if (this.editConn) {
			shell.setText("Edit Database Details");
		} else {
			shell.setText("Database Details");
		}

	}

	@Override
	protected Control createDialogArea(final Composite ancestor) {

		this.parent = new Composite(ancestor, SWT.NONE);
		final GridLayout layout1 = new GridLayout();
		layout1.numColumns = 2;
		layout1.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout1.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout1.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout1.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		this.parent.setLayout(layout1);
		final GridData gd = new GridData(GridData.FILL_BOTH);
		gd.grabExcessVerticalSpace = true;
		gd.grabExcessHorizontalSpace = true;
		this.parent.setLayoutData(gd);

		createFieldEditors(this.parent);

		return ancestor;
	}

	public void createFieldEditors(final Composite parent) {

		final FocusListener listener = new FocusListener() {

			@Override
			public void focusGained(final FocusEvent e) {
				setErrorMessage(null);

			}

			@Override
			public void focusLost(final FocusEvent e) {
				// TODO Auto-generated method stub

			}

		};
		createErrorMessageText(parent);

		final Label dbTypeLabel = new Label(parent, SWT.NONE);
		dbTypeLabel.setText("Database Type");

		this.databaseTypeField = new Combo(parent, SWT.NONE | SWT.BORDER);
		this.databaseTypeField.setLayoutData(new GridData(285, 20));
		this.databaseTypeField.setFocus();
		this.databaseTypeField.addFocusListener(listener);
		this.databaseTypeField.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				if (!DatabaseConnectionDialog.this.databaseTypeField.getText().equals(EMPTY_STR)) {
					enableFields(true);
				} else {
					enableFields(false);
				}

			}

		});
		for (final String dbType : databaseTypes) {
			this.databaseTypeField.add(dbType);
		}

		final Label dbNameLabel = new Label(parent, SWT.NONE);
		dbNameLabel.setText("Database Name");

		this.databaseNameField = new Text(parent, SWT.NONE | SWT.BORDER);
		this.databaseNameField.setLayoutData(new GridData(300, 20));
		this.databaseNameField.addFocusListener(listener);

		final Label dbHostLabel = new Label(parent, SWT.NONE);
		dbHostLabel.setText("Host");

		this.databaseHost = new Text(parent, SWT.NONE | SWT.BORDER);
		this.databaseHost.setLayoutData(new GridData(300, 20));
		this.databaseHost.addFocusListener(listener);
		/*
		 * databaseHost.addModifyListener(new ModifyListener() {
		 *
		 * @Override public void modifyText(ModifyEvent e) { if
		 * (!DatabaseConnectionDialog.this.databaseHost.getText().matches(
		 * "\\d{3}.?\\d{1,3}.?\\d{1,3}.?\\d{1,3}$")) {
		 * setErrorMessage("Host address incorrect"); } else {
		 * setErrorMessage(null); }
		 *
		 * }
		 *
		 * });
		 */

		final Label dbPortLabel = new Label(parent, SWT.NONE);
		dbPortLabel.setText("Port");

		this.databasePort = new Text(parent, SWT.NONE | SWT.BORDER);
		this.databasePort.setLayoutData(new GridData(300, 20));
		this.databasePort.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(final FocusEvent e) {
				if (!DatabaseConnectionDialog.this.databasePort.getText().equals(EMPTY_STR)
						&& !DatabaseConnectionDialog.this.databasePort.getText().matches("\\d{4}")) {
					setErrorMessage("Port number is invalid");
				} else {
					setErrorMessage(null);
				}
			}

			@Override
			public void focusLost(final FocusEvent e) {

			}

		});
		this.databasePort.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				if (!DatabaseConnectionDialog.this.databasePort.getText().equals(EMPTY_STR)
						&& !DatabaseConnectionDialog.this.databasePort.getText().matches("\\d{4}")) {
					setErrorMessage("Port number is invalid");
				} else {
					setErrorMessage(null);
				}

			}

		});

		final Label userNameLabel = new Label(parent, SWT.NONE);
		userNameLabel.setText("User Name");

		this.userNameField = new Text(parent, SWT.NONE | SWT.BORDER);
		this.userNameField.setLayoutData(new GridData(300, 20));
		this.userNameField.addFocusListener(listener);

		final Label passwordLabel = new Label(parent, SWT.NONE);
		passwordLabel.setText("Password");

		this.passwordField = new Text(parent, SWT.NONE | SWT.BORDER);
		this.passwordField.setLayoutData(new GridData(300, 20));
		this.passwordField.setEchoChar(ASTERISK_CHAR);
		this.passwordField.addFocusListener(listener);

		final Label driverLabel = new Label(parent, SWT.NONE);
		driverLabel.setText("Driver Class");

		/*final GridData gridDataLabel = new GridData();
		driverLabel.setLayoutData(gridDataLabel);*/

		final Composite composite = new Composite(parent, parent.getStyle());
		final GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		final GridData gridDataText = new GridData(200, 20);
		gridDataText.grabExcessHorizontalSpace = true;

		this.driverClass = new Text(composite, SWT.NONE | SWT.BORDER);
		this.driverClass.setEditable(false);
		this.driverClass.setLayoutData(gridDataText);

		final GridData gridDataButton = new GridData();
		this.browseButton = new Button(composite, SWT.PUSH);
		this.browseButton.setText(" Browse Driver Class");

		this.browseButton.setLayoutData(gridDataButton);
		this.browseButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(final SelectionEvent arg0) {
				final SelectionDialog selectionDialog;
				try {
					selectionDialog = JavaUI.createTypeDialog(parent.getShell() == null ? new Shell() : parent.getShell(), null,
							SearchEngine.createWorkspaceScope(), IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false, EMPTY_STR);

					selectionDialog.setTitle("Driver Class");
					selectionDialog.setMessage("Select driver class");

					if (selectionDialog.open() == CANCEL) {
						return;
					}
					final IType driverClass = (IType) selectionDialog.getResult()[0];
					DatabaseConnectionDialog.this.driverClass.setText(driverClass.getFullyQualifiedName());
					DatabaseConnectionDialog.this.driverFQName = driverClass.getFullyQualifiedName();
					DatabaseConnectionDialog.this.driverPrj = driverClass.getJavaProject().getElementName();
					setErrorMessage(null);

				} catch (final JavaModelException ex) {
					ex.printStackTrace();
				}

			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent arg0) {

			}
		});

		/*final Composite composite1 = new Composite(parent, parent.getStyle());
		final GridLayout layout1 = new GridLayout();
		layout1.numColumns = 2;
		composite1.setLayout(layout1);*/
		/*final GridData gridDataLabel2 = new GridData();
		final Label defaultLabel1 = new Label(composite1, SWT.NONE);
		defaultLabel1.setText("");
		defaultLabel1.setLayoutData(gridDataLabel2);*/

		final GridData gridDataLabel1 = new GridData();
		final Label defaultLabel = new Label(parent, SWT.NONE);
		defaultLabel.setText("Set As Default");
		defaultLabel.setLayoutData(gridDataLabel1);

		final GridData gridDataButton1 = new GridData();
		this.defaultCheckBox = new Button(parent, SWT.CHECK);
		this.defaultCheckBox.setLayoutData(gridDataButton1);
		this.defaultCheckBox.addFocusListener(listener);

		if (this.editConn) {

			this.databaseTypeField.select(ArrayUtils.indexOf(databaseTypes, this.databaseConndata.getDatabaseType()));
			this.databaseNameField.setText(this.databaseConndata.getDatabaseName());
			this.databaseHost.setText(this.databaseConndata.getHostAddress());
			this.databasePort.setText(String.valueOf(this.databaseConndata.getPort()));
			this.userNameField.setText(this.databaseConndata.getUserName());
			this.passwordField.setText(this.databaseConndata.getPassword());
			this.defaultCheckBox.setSelection(this.databaseConndata.isDefaultConn());
			this.driverClass.setText(this.databaseConndata.getDriverClass() == null ? EMPTY_STR : this.databaseConndata.getDriverClass());
			this.driverPrj = this.databaseConndata.getDriverPrj() == null ? EMPTY_STR : this.databaseConndata.getDriverPrj();
			this.currConnString = this.databaseConndata.getDatabaseType() + this.FIELD_DELIMITER + this.databaseConndata.getDatabaseName()
					+ this.FIELD_DELIMITER + this.databaseConndata.getHostAddress() + this.FIELD_DELIMITER
					+ this.databaseConndata.getPort() + this.FIELD_DELIMITER + this.databaseConndata.getUserName() + this.FIELD_DELIMITER
					+ this.databaseConndata.getPassword() + this.FIELD_DELIMITER + this.databaseConndata.isDefaultConn()
					+ this.FIELD_DELIMITER + this.databaseConndata.getDriverClass() + this.FIELD_DELIMITER
					+ this.databaseConndata.getDriverPrj();

		} else {
			enableFields(false);
		}
	}

	/**
	 * @param parent
	 */
	private void createErrorMessageText(final Composite parent) {

		this.errorMessageText = new Text(parent, SWT.READ_ONLY | SWT.MULTI);
		//this.errorMessageText.setForeground(new Color(null, 255, 0, 0));
		this.errorMessageText.setForeground(FastCodeColor.getErrorMsgColor());
		this.errorMessageText.setLayoutData(new GridData(GridData.FILL_BOTH));
		final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData.heightHint = 2 * this.errorMessageText.getLineHeight();
		gridData.horizontalSpan = 2;
		this.errorMessageText.setLayoutData(gridData);
		// setErrorMessage(this.defaultMessage);

	}

	/**
	 * @param defaultMessage2
	 * @param errorMessage
	 */
	public void setErrorMessage(final String errorMessage) {

		this.errorMessage = errorMessage;
		String message = this.errorMessage != null ? errorMessage : EMPTY_STR;
		message = this.warningMessage != null ? message + EMPTY_STR + NEWLINE + this.warningMessage : message;
		if (this.errorMessage != null) {
			this.errorMessageText.setForeground(FastCodeColor.getErrorMsgColor());//(new Color(null, 255, 0, 0));
		}

		if (this.errorMessageText != null && !this.errorMessageText.isDisposed()) {

			this.errorMessageText.setText(message);
			final boolean hasError = this.errorMessage != null || this.warningMessage != null;
			this.errorMessageText.setEnabled(hasError);
			this.errorMessageText.setVisible(hasError);
			this.errorMessageText.getParent().update();
			final Control button = getButton(IDialogConstants.OK_ID);
			if (button != null) {
				button.setEnabled(this.errorMessage == null);
			}
		}
	}

	private void enableFields(final boolean value) {

		this.databaseNameField.setEnabled(value);
		this.databaseHost.setEnabled(value);
		this.databasePort.setEnabled(value);
		this.userNameField.setEnabled(value);
		this.passwordField.setEnabled(value);
		this.defaultCheckBox.setEnabled(value);
		this.browseButton.setEnabled(value);
	}

	private boolean isFieldsEmpty() {

		if (this.currDatabaseType.equals(EMPTY_STR)) {
			setErrorMessage("Database type is required");
			return true;
		}
		if (this.currDatabaseName.equals(EMPTY_STR)) {
			setErrorMessage("Database name is required");
			return true;
		}
		if (this.currHostAddress.equals(EMPTY_STR)) {
			setErrorMessage("Host address is required");
			return true;
		}
		if (this.currPortNumber == 0) {
			setErrorMessage("Port is empty");
			return true;
		}

		if (this.currUserName.equals(EMPTY_STR)) {
			setErrorMessage("username is required");
			return true;
		}

		if (this.driverFQName.equals(EMPTY_STR)) {
			setErrorMessage("Please select driver class");
			return true;
		}
		return false;
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	/**
	 * @param currDatabaseName
	 * @param newConnString
	 */

	private void updatePreferenceStore(final String currDatabaseName, final String newConnString) {

		final String dbConnRecords = this.preferenceStore.getString(P_DATABASE_CONN_DATA);
		String newConnRecords = EMPTY_STR;
		if (!isEmpty(dbConnRecords)) {
			final String[] recordArray = dbConnRecords.split(this.RECORD_DELIMITER);

			for (final String record : recordArray) {
				if (!isEmpty(record)) {
					String[] attrValues = record.split(this.FIELD_DELIMITER);
					System.out.println(attrValues.length);
					if (attrValues.length > 3) {
						if (attrValues.length == 7) {
							attrValues = resizeArray(attrValues, 9);
							//attrValues = System.arraycopy(arg0, arg1, arg2, arg3, arg4)(attrValues, attrValues.length + 2);
							if (attrValues[1].equals(this.currDatabaseName)) {
								attrValues[7] = this.driverFQName;
								attrValues[8] = this.driverPrj;
							} else {
								attrValues[7] = EMPTY_STR;
								attrValues[8] = EMPTY_STR;
							}
						}
						if (this.isdefaultConn && !attrValues[1].equals(this.currDatabaseName) && Boolean.valueOf(attrValues[6]).equals(true)) {

							attrValues[6] = String.valueOf(false);
							final String newrecord = attrValues[0] + this.FIELD_DELIMITER + attrValues[1] + this.FIELD_DELIMITER
									+ attrValues[2] + this.FIELD_DELIMITER + attrValues[3] + this.FIELD_DELIMITER + attrValues[4]
									+ this.FIELD_DELIMITER + attrValues[5] + this.FIELD_DELIMITER + attrValues[6] + this.FIELD_DELIMITER
									+ attrValues[7] + this.FIELD_DELIMITER + attrValues[8];
							newConnRecords += newrecord + this.RECORD_DELIMITER;

						} else if (attrValues[1].equals(this.currDatabaseName)) {
							newConnRecords += newConnString + this.RECORD_DELIMITER;
						} else {
							newConnRecords += record + this.RECORD_DELIMITER;
						}
					}
				}

			}
			this.preferenceStore.setValue(P_DATABASE_CONN_DATA, newConnRecords);
		}

	}

	private static String[] resizeArray(final String[] attrValues, final int newSize) {
		final String[] tempArr = new String[newSize];
		/*int oldSize = java.lang.reflect.Array.getLength(oldArray);
		Class elementType = oldArray.getClass().getComponentType();
		Object newArray = java.lang.reflect.Array.newInstance(
		      elementType, newSize);*/
		final int preserveLength = Math.min(attrValues.length, newSize);
		if (preserveLength > 0) {
			System.arraycopy(attrValues, 0, tempArr, 0, preserveLength);
		}
		return tempArr;
	}

	@Override
	public void okPressed() {

		this.currDatabaseType = this.databaseTypeField.getText();
		this.currDatabaseName = this.databaseNameField.getText();
		this.currHostAddress = this.databaseHost.getText();
		this.currPortNumber = this.databasePort.getText() == EMPTY_STR ? 0 : Integer.parseInt(this.databasePort.getText());
		this.currUserName = this.userNameField.getText();
		this.currPassword = this.passwordField.getText();
		this.isdefaultConn = this.defaultCheckBox.getSelection();
		this.driverFQName = this.driverClass.getText(); // ((IType)this.driverClass.getText()).getFullyQualifiedName();
		/*if (this.isdefaultConn == true) {
			updatePreferenceStore(this.currDatabaseName);
		}*/

		if (isFieldsEmpty()) {
			return;
		}

		try {
			checkConnection(this.currDatabaseType, this.currDatabaseName, this.currHostAddress, this.currPortNumber, this.currUserName,
					this.currPassword, this.driverFQName, this.driverPrj);

			final String newConnString = this.currDatabaseType + this.FIELD_DELIMITER + this.currDatabaseName + this.FIELD_DELIMITER
					+ this.currHostAddress + this.FIELD_DELIMITER + this.currPortNumber + this.FIELD_DELIMITER + this.currUserName
					+ this.FIELD_DELIMITER + this.currPassword + this.FIELD_DELIMITER + this.isdefaultConn + this.FIELD_DELIMITER
					+ this.driverFQName + this.FIELD_DELIMITER + this.driverPrj;

			updatePreferenceStore(this.currDatabaseName, newConnString);

			if (!this.editConn) {
				/*existingConns = existingConns.replace(this.currConnString, newConnString);
				this.preferenceStore.setValue(P_DATABASE_CONN_DATA, existingConns);

				} else {*/

				final String existingConns = this.preferenceStore.getString(P_DATABASE_CONN_DATA);
				this.preferenceStore.setValue(P_DATABASE_CONN_DATA, existingConns + this.RECORD_DELIMITER + newConnString);
			}

			super.okPressed();

			DatabaseConnectionSettings.setReload(true);
			DataBaseTypeInfo.setReload(true);

		} catch (final Exception ex) {
			setErrorMessage("You have given wrong data :\n " + ex.getMessage());
			ex.printStackTrace();
		}

	}

	@Override
	public void cancelPressed() {

		super.cancelPressed();

	}

	/**
	 *
	 * @param databaseType
	 * @param databaseName
	 * @param hostAddress
	 * @param portNumber
	 * @param userName
	 * @param passWord
	 * @param driverPrj
	 * @param driverFQName
	 * @return
	 * @throws Exception
	 */
	public void checkConnection(final String databaseType, final String databaseName, final String hostAddress, final int portNumber,
			final String userName, final String passWord, final String driverFQName, final String driverPrj) throws Exception {
		try {
			final ConnectToDatabase connect = ConnectToDatabase.getInstance();
			connect.getNewConnection(databaseType, databaseName, hostAddress, portNumber, userName, passWord, driverFQName, driverPrj);
			/*if (databaseType.equals(MYSQL)) {
				Class.forName(MYSQL_JDBC_DRIVER);
				this.con = DriverManager.getConnection(JDBC + COLON + databaseType + COLON + DOUBLE_FORWARD_SLASH + hostAddress
						+ FORWARD_SLASH + databaseName, userName, passWord);

			} else if (databaseType.equals(ORACLE)) {
				Class.forName(ORACLE_JDBC_DRIVER);
				this.con = DriverManager.getConnection(JDBC + COLON + databaseType + COLON + THIN + COLON + AT_THE_RATE + hostAddress
						+ COLON + portNumber + COLON + databaseName, userName, passWord);

			} else if (databaseType.equals(SQLSERVER)) {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				// con=

			} else if (databaseType.equals(HSQLDB)) {
				Class.forName(HSQLDB_JDBC_DRIVER);
				this.con = DriverManager.getConnection(JDBC + COLON + databaseType + COLON + HSQL + COLON + DOUBLE_FORWARD_SLASH
						+ hostAddress + FORWARD_SLASH + databaseName, userName, passWord);

			} else if (databaseType.equals(SYBASE)) {
				Class.forName(SYBASE_JDBC_DRIVER);
				// con=

			} else if (databaseType.equals(POSTGRESQL)) {
				Class.forName(POSTGRESQL_JDBC_DRIVER);
				this.con = DriverManager.getConnection(JDBC + COLON + databaseType + COLON + DOUBLE_FORWARD_SLASH + hostAddress
						+ FORWARD_SLASH + databaseName, userName, passWord);

			}*/
		} catch (final SQLException ex) {
			System.out.println(ex.getSQLState());
			if (ex.getSQLState().equals(MYSQL_CONNECTION_ERROR_SQLSTATE) || ex.getSQLState().equals(ORACLE_CONNECTION_ERROR_SQLSTATE1)
					|| ex.getSQLState().equals(POSTGRESQL_CONNECTION_ERROR_SQLSTATE)
					|| ex.getSQLState().equals(ORACLE_CONNECTION_ERROR_SQLSTATE2)) {
				setErrorMessage("Could Not Connect to Database: \n" + ex.getMessage());
			} else {
				setErrorMessage("You have given wrong data :\n " + ex.getMessage());
			}
			ex.printStackTrace();
			throw ex;
		} catch (final ClassNotFoundException ex) {
			setErrorMessage("Could not find the class:\n " + ex.getMessage());
			ex.printStackTrace();
			throw ex;
		} catch (final Exception e) {
			throw e;
		} finally {

			if (this.con != null) {
				try {
					this.con.close();
				} catch (final SQLException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		}

	}

}
