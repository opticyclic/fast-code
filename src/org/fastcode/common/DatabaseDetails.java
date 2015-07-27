package org.fastcode.common;

public class DatabaseDetails implements Comparable<DatabaseDetails> {

	private String	databaseName;
	private String	databaseType;
	private String	hostAddress;
	private int		port;
	private String	userName;
	private String	password;
	private boolean	isDefaultConn;
	private String driverClass;
	private String driverPrj;

	public DatabaseDetails(final String databaseName, final String databaseType, final String hostAddress, final int port, final String userName, final String password,
			final boolean defaultConn, final String driverClass, final String driverPrj) {
		super();

		this.databaseName = databaseName;
		this.databaseType = databaseType;
		this.hostAddress = hostAddress;
		this.port = port;
		this.userName = userName;
		this.password = password;
		this.isDefaultConn = defaultConn;
		this.driverClass = driverClass;
		this.driverPrj = driverPrj;
	}

	public DatabaseDetails() {

	}

	public String getDatabaseName() {
		return this.databaseName;
	}

	public void setDatabaseName(final String databaseName) {
		this.databaseName = databaseName;
	}

	public String getDatabaseType() {
		return this.databaseType;
	}

	public void setDatabaseType(final String databaseType) {
		this.databaseType = databaseType;
	}

	public String getHostAddress() {
		return this.hostAddress;
	}

	public void setHostAddress(final String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(final int port) {
		this.port = port;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public boolean isDefaultConn() {
		return this.isDefaultConn;
	}

	public void setDefaultConn(final boolean isDefault) {
		this.isDefaultConn = isDefault;
	}

	@Override
	public int compareTo(final DatabaseDetails databaseDetails) {
		return this.databaseName.compareToIgnoreCase(databaseDetails.databaseName);
	}

	/**
	 *
	 * getter method for driverClass
	 * @return
	 *
	 */
	public String getDriverClass() {
		return this.driverClass;
	}

	/**
	 *
	 * setter method for driverClass
	 * @param driverClass
	 *
	 */
	public void setDriverClass(final String driverClass) {
		this.driverClass = driverClass;
	}

	/**
	 *
	 * getter method for driverPrj
	 * @return
	 *
	 */
	public String getDriverPrj() {
		return this.driverPrj;
	}

	/**
	 *
	 * setter method for driverPrj
	 * @param driverPrj
	 *
	 */
	public void setDriverPrj(final String driverPrj) {
		this.driverPrj = driverPrj;
	}

}
