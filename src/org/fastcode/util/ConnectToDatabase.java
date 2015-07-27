package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.AT_THE_RATE;
import static org.fastcode.common.FastCodeConstants.COLON;
import static org.fastcode.common.FastCodeConstants.DOUBLE_FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.FC_PLUGIN;
import static org.fastcode.common.FastCodeConstants.FORWARD_SLASH;
import static org.fastcode.common.FastCodeConstants.HSQL;
import static org.fastcode.common.FastCodeConstants.HSQLDB;
import static org.fastcode.common.FastCodeConstants.HSQLDB_JDBC_DRIVER;
import static org.fastcode.common.FastCodeConstants.JDBC;
import static org.fastcode.common.FastCodeConstants.MYSQL;
import static org.fastcode.common.FastCodeConstants.MYSQL_CONNECTION_ERROR_SQLSTATE;
import static org.fastcode.common.FastCodeConstants.MYSQL_JDBC_DRIVER;
import static org.fastcode.common.FastCodeConstants.ORACLE;
import static org.fastcode.common.FastCodeConstants.ORACLE_CONNECTION_ERROR_SQLSTATE1;
import static org.fastcode.common.FastCodeConstants.ORACLE_CONNECTION_ERROR_SQLSTATE2;
import static org.fastcode.common.FastCodeConstants.ORACLE_JDBC_DRIVER;
import static org.fastcode.common.FastCodeConstants.POSTGRESQL;
import static org.fastcode.common.FastCodeConstants.POSTGRESQL_CONNECTION_ERROR_SQLSTATE;
import static org.fastcode.common.FastCodeConstants.POSTGRESQL_JDBC_DRIVER;
import static org.fastcode.common.FastCodeConstants.SQLSERVER;
import static org.fastcode.common.FastCodeConstants.SYBASE;
import static org.fastcode.common.FastCodeConstants.SYBASE_JDBC_DRIVER;
import static org.fastcode.common.FastCodeConstants.THIN;
import static org.fastcode.util.MessageUtil.showError;
import static org.fastcode.util.StringUtil.isEmpty;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.fastcode.common.DatabaseDetails;
import org.fastcode.preferences.DatabaseConnectionSettings;
import static org.fastcode.common.FastCodeConstants.ORACLE_DRIVER;

public class ConnectToDatabase {
	private static Connection			con;
	private static ConnectToDatabase	connectToDatabase	= new ConnectToDatabase();

	/**
	 * @return
	 */
	public static ConnectToDatabase getInstance() {
		return connectToDatabase;
	}

	/**
	 * @param databaseName
	 * @return
	 * @throws Exception
	 */
	public Connection getNewConnection(final String databaseName) throws Exception {
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		con = null;
		if (isEmpty(databaseConnectionSettings.getTypesofDabases())) {
			showError(
					"Database Connection has not been set up. Please go to  Windows -> Preference -> Fast Code Preference -> Database Connection Preferences.Select one connection as default",
					"Database Connection");
			return null;
		}
		final DatabaseDetails databaseDetails = databaseConnectionSettings.getConnMap().get(databaseName);

		return getNewConnection(databaseDetails.getDatabaseType(), databaseDetails.getDatabaseName(), databaseDetails.getHostAddress(),
				databaseDetails.getPort(), databaseDetails.getUserName(), databaseDetails.getPassword(), databaseDetails.getDriverClass(), databaseDetails.getDriverPrj());
	}

	public Connection getNewConnection(final String databaseType, final String databaseName, final String hostAddress,
			final int portNumber, final String userName, final String passWord, final String driveClass, final String driverPrj) throws Exception {
		try {
			if (databaseType.equals(MYSQL)) {
				final Driver driver = getDriver(databaseType, driveClass, driverPrj);
				final Properties props = setProperties(userName, passWord);
				con = driver.connect(JDBC + COLON + databaseType + COLON + DOUBLE_FORWARD_SLASH + hostAddress + FORWARD_SLASH
						+ databaseName, props);
				/*Class.forName(MYSQL_JDBC_DRIVER);
				this.con = DriverManager.getConnection(JDBC + COLON + databaseType + COLON + DOUBLE_FORWARD_SLASH + hostAddress
						+ FORWARD_SLASH + databaseName, userName, passWord);*/

			} else if (databaseType.equals(ORACLE)) {
				final Driver driver = getDriver(ORACLE_DRIVER, driveClass, driverPrj);
				final Properties props = setProperties(userName, passWord);
				this.con = driver.connect(JDBC + COLON + databaseType + COLON + THIN + COLON + AT_THE_RATE + hostAddress + COLON
						+ portNumber + COLON + databaseName, props);
				/*Class.forName(ORACLE_JDBC_DRIVER);
				this.con = DriverManager.getConnection(JDBC + COLON + databaseType + COLON + THIN + COLON + AT_THE_RATE + hostAddress
						+ COLON + portNumber + COLON + databaseName, userName, passWord);*/

			} else if (databaseType.equals(SQLSERVER)) {
				Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
				// con=

			} else if (databaseType.equals(HSQLDB)) {
				final Driver driver = getDriver(databaseType, driveClass, driverPrj);
				final Properties props = setProperties(userName, passWord);
				this.con = driver.connect(JDBC + COLON + databaseType + COLON + HSQL + COLON + DOUBLE_FORWARD_SLASH + hostAddress
						+ FORWARD_SLASH + databaseName, props);
				/*Class.forName(HSQLDB_JDBC_DRIVER);
				this.con = DriverManager.getConnection(JDBC + COLON + databaseType + COLON + HSQL + COLON + DOUBLE_FORWARD_SLASH
						+ hostAddress + FORWARD_SLASH + databaseName, userName, passWord);*/

			} else if (databaseType.equals(SYBASE)) {
				final Driver driver = getDriver(databaseType, driveClass, driverPrj);
				// con=

			} else if (databaseType.equals(POSTGRESQL)) {
				final Driver driver = getDriver(databaseType, driveClass, driverPrj);
				final Properties props = setProperties(userName, passWord);
				this.con = driver.connect(JDBC + COLON + databaseType + COLON + DOUBLE_FORWARD_SLASH + hostAddress + FORWARD_SLASH
						+ databaseName, props);
				/*Class.forName(POSTGRESQL_JDBC_DRIVER);
				this.con = DriverManager.getConnection(JDBC + COLON + databaseType + COLON + DOUBLE_FORWARD_SLASH + hostAddress
						+ FORWARD_SLASH + databaseName, userName, passWord);*/

			}

			if (!con.isClosed()) {
				System.out.println("Succesfully connected to database");
			} else {
				throw new Exception("Could Not Connect to Database");
			}
			return con;
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
			return con;
		} catch (final SQLException e) {
			e.printStackTrace();
			if (e.getSQLState().equals(MYSQL_CONNECTION_ERROR_SQLSTATE) || e.getSQLState().equals(ORACLE_CONNECTION_ERROR_SQLSTATE1)
					|| e.getSQLState().equals(POSTGRESQL_CONNECTION_ERROR_SQLSTATE)
					|| e.getSQLState().equals(ORACLE_CONNECTION_ERROR_SQLSTATE2)) {
				throw new Exception("Could Not Connect to Database: \n", e);
			} else {
				throw new Exception(e.getMessage(), e);
			}

		}
	}

	private Driver getDriver(final String databaseType, final String classFQName, final String driverPrj) {
		try {

			final List<IJavaProject> javaProjects = new ArrayList<IJavaProject>();
			final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

			for (final IProject project : projects) {
				if (project == null || !project.exists() || !project.isOpen()) {
					continue;
				}
				if (project.getName().equals(FC_PLUGIN)) {
					continue;
				}
				if (!project.getName().equals(driverPrj)) {
					continue;
				}
				project.open(null /* IProgressMonitor */);
				final IJavaProject javaProject = JavaCore.create(project);
				if (!javaProject.isOpen() || !javaProject.exists()) {
					continue;
				}
				javaProjects.add(javaProject);

			}
			boolean jarFound = false;
			for (final IJavaProject jPrj : javaProjects) {
				final String[] classPathEntries = JavaRuntime.computeDefaultRuntimeClassPath(jPrj);
				/*System.out.println("Project  -- " + jPrj);
				System.out.println(classPathEntries);*/
				final List<URL> urlList = new ArrayList<URL>();
				for (int i = 0; i < classPathEntries.length; i++) {
					final String entry = classPathEntries[i];
					if (entry.contains(databaseType)) { // || entry.contains("rt.jar")) {
						final IPath path = new Path(entry);
						//System.out.println(path.toString());
						final URL url = path.toFile().toURI().toURL(); //new File ("jar:file:" + path.toString()).toURI().toURL();//
						urlList.add(url);
						jarFound = true;
						break;
					}

				}
				if (urlList.isEmpty()) {
					continue;
				}
				final ClassLoader parentClassLoader = jPrj.getClass().getClassLoader();
				final URL[] urls = urlList.toArray(new URL[urlList.size()]);

				final URLClassLoader classLoader = new URLClassLoader(urls, parentClassLoader);

				try {
					final ClassLoader loader = classLoader; //new URLClassLoader(urls);
					final Driver driver = (Driver) loader.loadClass(classFQName).newInstance();
					if (driver == null) {
						throw new Exception("Problem loading the driver");
					}
					return driver;

					/*DriverManager.registerDriver(driver);
					DriverManager.getDriver("jdbc:mysql://localhost/TestFc");*/
					//Class.forName(MYSQL_JDBC_DRIVER, true, loader);
					/*final Class clazz = loader.loadClass("java.sql.DriverManager");
					return clazz;*/
					//Class.forName(MYSQL_JDBC_DRIVER, true, loader);//.newInstance();

				} catch (final ClassNotFoundException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				/*final List<URLClassLoader> loaders = new ArrayList<URLClassLoader>();
				loaders.add(getProjectClassLoader(jPrj, urlList));*/
				/*for (final URLClassLoader urlClassLoader : loaders) {
					try {
						final ClassLoader loader = new URLClassLoader(urls);
						final Class classObject = loader.loadClass(className);
					} catch (final ClassNotFoundException e) {
					}
				}*/
				if (jarFound) {
					break;
				}
			}

		} catch (final Exception e) {
			System.out.println(e.getMessage());
		}
		return null;

	}

	/**
	 * @param passwd
	 * @param userName
	 * @return
	 */
	public Properties setProperties(final String userName, final String passwd) {
		final Properties props = new Properties();
		props.put("user", userName);
		props.put("password", passwd);
		return props;
	}

	/**
	 * @param jPrj
	 * @param urlList
	 * @return
	 */
	public void getProjectClassLoader(final IJavaProject jPrj, final List<URL> urlList) {
		final ClassLoader parentClassLoader = jPrj.getClass().getClassLoader();
		final URL[] urls = urlList.toArray(new URL[urlList.size()]);
		/*final URLClassLoader classLoader*/final ClassLoader loader = new URLClassLoader(urls, parentClassLoader);
		//final ClassLoader loader = new URLClassLoader(urls);
		try {
			final Class<?> classObject = loader.loadClass(MYSQL_JDBC_DRIVER);
		} catch (final ClassNotFoundException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public Connection getConnection() throws Exception {
		final DatabaseConnectionSettings databaseConnectionSettings = DatabaseConnectionSettings.getInstance();
		return getNewConnection(databaseConnectionSettings.getNameofDabase());
	}

	/**
	 * @param con
	 */
	public void closeConnection(final Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public static Connection getCon() {
		return con;
	}

}
