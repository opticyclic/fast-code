package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.fastcode.common.DatabaseDetails;
import org.fastcode.util.DatabaseUtil;

public class DatabaseConnectionSettings {
	private String								typesOfDatabases;
	private String								nameOfDatabase;
	private String								hostAddress;
	private int									portNumber;
	private String								userName;
	private String								userPassword;

	private final IPreferenceStore				preferenceStore;

	private static DatabaseConnectionSettings	databaseConnectionSettings	= new DatabaseConnectionSettings();
	private static boolean						reload;

	private Map<String, DatabaseDetails>		connMap						= new HashMap<String, DatabaseDetails>();

	public Map<String, DatabaseDetails> getConnMap() {
		return connMap;
	}

	public void setConnMap(List<DatabaseDetails> connList) {
		for (DatabaseDetails conn : connList) {
			this.connMap.put(conn.getDatabaseName(), conn);
		}

	}

	private DatabaseConnectionSettings() {
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		loadFromPreferenceStore();

	}

	public String getTypesofDabases() {
		return this.typesOfDatabases;

	}

	public String getNameofDabase() {
		return this.nameOfDatabase;

	}

	public String getHostAddress() {
		return this.hostAddress;

	}

	public int getPortNumber() {
		return this.portNumber;

	}

	public String getUserName() {
		return this.userName;

	}

	public String getUserPassword() {
		return this.userPassword;

	}

	public static DatabaseConnectionSettings getInstance() {
		if (reload) {
			reload = false;
			databaseConnectionSettings = new DatabaseConnectionSettings();
		}
		return databaseConnectionSettings;

	}

	/**
	 *
	 */
	public void loadFromPreferenceStore() {

		/*this.typesOfDatabases = this.preferenceStore.getString(P_DATABASE_TYPE);
		this.nameOfDatabase = this.preferenceStore.getString(P_DATABASE_NAME);
		this.hostAddress = this.preferenceStore.getString(P_HOST_ADDRESS);
		this.portNumber = this.preferenceStore.getInt(P_PORT_NUMBER);
		this.userName = this.preferenceStore.getString(P_USER_NAME);
		this.userPassword = this.preferenceStore.getString(P_PASSWORD);*/
		setConnMap(DatabaseUtil.loadConnectionsFromPreferenceStore());
		for (DatabaseDetails conn : this.connMap.values()) {
			if (conn.isDefaultConn()) {
				this.typesOfDatabases = conn.getDatabaseType();
				this.nameOfDatabase = conn.getDatabaseName();
				this.hostAddress = conn.getHostAddress();
				this.portNumber = conn.getPort();
				this.userName = conn.getUserName();
				this.userPassword = conn.getPassword();
			}
		}
	}

	public static void setReload(final boolean aReload) {
		reload = aReload;
	}
}