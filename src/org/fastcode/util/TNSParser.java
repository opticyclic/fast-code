package org.fastcode.util;

/*
* Java TNS parser API
*
* File: TNSParse.java
* Purpose: Parse Oracle's Tnsnames.ora file
* Author: Rauf Sarwar
* Java Ver: Recommended Java versions are 1.2 and above. Tested and compiled on
* Java(TM) 2 Runtime Environment, Standard Edition (build 1.3.1_01).
* Check your version from command line.. java -showversion
* API's: Requires java.io, java.util, java.lang, java.sql
* Vesrion: $Id: TNSParser.java,v 1.7 2015/04/17 03:36:37 gdind2003 Exp $
* Bugs: Bug fixes, suggestions and/or comments should be sent to:
* Rauf.Sarwar@ifsna.com
* Usage: Freeware. Modify/Distribute as you wish.
*
*
* Test this API using following. e.g. Get list of Service names.
*
* 1) Make sure Java SDK 1.2 or above is installed and CLASSPATH system variable includes
* all Java API's.
* To check... type javac at command line.
* 2) Create file TNSParse.java (Filename Case matters) and copy TNSParse code to it
* 3) Open command line (DOS Prompt)
* 4) C:\> cd
* 5) Set CLASSPATH to include current directory
* C:\> set CLASSPATH=.;%CLASSPATH%
* 6) Compile TNSParse.java
* C:\> javac TNSParse.java
* 7) Create file testTnsParse.java (Filename Case matters) in same directory and copy this text to it.
* public class testTnsParse {
* public void main (String[] args)
* throws Exception {
*
* TNSParse t = new TNSParse (args[0]);
* String s = new String (t.getAliasList());
* t.close();
* System.out.println (s);
* }
* }
* 8) Compile testTnsParse.java
* C:\> javac testTnsParse.java
* 9) Run testTnsParse.class file passing tnsnames.ora file as parameter
* C:\> java testTnsParse C:\\network\admin\tnsnames.ora
* OR
* C:\> jview testTnsParse C:\\network\admin\tnsnames.ora
*
*
* Date Name Revision History
* -------- --------------- -------- -----------------------------------
* 07152001 Rauf Sarwar V 1.0 Created
* 10102001 Rauf Sarwar V 1.1 Added public getFullConnString, getOciConn,
* getThinTcpConn, enumerateAddresses, connClose
* 04052002 Rauf Sarwar V 1.2 1) Fixed Private tnsRemoveNonDataLines()
* Exception resolved if closing Parenthesis mismatched
* 2) Fixed private tnsSetAliasString()
* Skip alias from aliasstr if invalid
* 3) Added better error handling to getOciConn() and getThinTcpConn().
* Added private buildConnURL() to build string separately.
* 4) Added extractTcpString() to return TCP string as used
* by Oracle Thin JDBC driver like ::
*
* ------------------------------------------------------------------------
*
*
* ****** CONTSTRUCTORS ******
* public TNSParse (String tnsFile)
* public TNSParse (File tnsFile)
*
* ****** PUBLIC METHODS ******
* public String getAliasList ()
* public String getFullConnString (String alias)
* public Connection getOciConn (String alias, String username, String pwd)
* public Connection getOciConn (String alias, String username, String pwd, boolean autocommit)
* public Connection getThinTcpConn (String alias, String username, String pwd)
* public Connection getThinTcpConn (String alias, String username, String pwd, boolean autocommit)
* public String extractTcpString (String alias)
* public String enumerateAddresses (String alias)
* public boolean isConnStrValid (String alias)
* public void connClose ()
* public void close ()
* public String getVersion ()
*
*/

import java.io.*;
import java.util.*;
import java.lang.*;
import java.sql.*;

public class TNSParser {

	/*
	* File Version

	private final String version = "$Id: TNSParser.java,v 1.7 2015/04/17 03:36:37 gdind2003 Exp $";*/
	/*
	* java.sql Connection interface
	*/
	private Connection		conn		= null;
	/*
	* Tnsnames.ora File object
	*/
	private File			tnsnamesora	= null;
	/*
	* Holds Tnsnames.ora file contents
	*/
	private String			filestr		= "";
	/*
	* Holds delimited Alias/Service name information
	*/
	private String			aliasstr	= "";
	/*
	* Standard End-Of-Line Carriage Return\Line feed
	*/
	private final String	eol			= "\r\n";
	/*
	* Space identifier
	*/
	private final String	spacerep	= " ";
	/*
	* null identifier
	*/
	private final String	nullrep		= "";
	/*
	* Comment identifier
	*/
	private final char		commchar	= '#';
	/*
	* (char)5 identifier
	*/
	private final String	strchar5	= (char) 5 + "";

	/**************************
	****** CONSTRUCTORS ******
	**************************/

	/*
	* public TNSParse (String tnsFile)
	* @param tnsFile: fully qualified Tnsnames.ora filename
	*
	* Constructs TNSParse object used to parse Tnsnames.ora file
	*/
	public TNSParser(final String tnsFile) throws Exception {

		this(new File(tnsFile));
	}

	/*
	* public TNSParse (File tnsFile)
	* @param tnsFile: File object
	*
	* Constructs TNSParse object used to parse Tnsnames.ora file
	*/
	public TNSParser(final File tnsFile) throws Exception {

		this.tnsnamesora = tnsFile;
		tnsReadAndParseFile();
	}

	/**************************
	***** PUBLIC METHODS *****
	**************************/

	/*
	* public String getAliasList ()
	* @param
	* @return String: End-Of-Line delimited list of Alias/Service names
	*
	* Return End-Of-Line delimited list of Alias/Service names
	*/
	public String getAliasList() throws Exception {

		return this.aliasstr;
	}

	/*
	* public String getFullConnString (String alias)
	* @param alias: Alias/Service name
	* @return String: Full connect string
	*
	* Return full connect string. Full connect string begins with Alias/Service
	* name and ends with last ")"
	*/
	public String getFullConnString(final String alias) throws Exception {

		checkAliasExist(alias);
		return getConnectString(alias);
	}

	/*
	* public Connection getOciConn (String alias, String username, String pwd)
	* @param alias: Alias/Service name
	* username: Oracle database username
	* pwd: Oracle password
	* @return Connection: SQL Connection interface
	*
	* Builds and returns JDBC native OCI Connection.
	* AutoCommit is disabled by default.
	*
	* When done, Connection MUST be closed by calling connClose()
	*
	* throws: Exception if no TCP protocol is defined
	* SQLException if database error occurs
	*/
	/*public Connection getOciConn(final String alias, final String username, final String pwd) throws Exception {

		return getOciConn(alias, username, pwd, false);
	}*/

	/*
	* public Connection getOciConn (String alias, String username, String pwd, boolean autocommit)
	* @param alias: Alias/Service name
	* username: Oracle database username
	* pwd: Oracle password
	* autocommit: true to enable AutoCommit, false to disable AutoCommit
	* @return Connection: SQL Connection interface
	*
	* Builds and returns JDBC native OCI Connection.
	*
	* When done, Connection MUST be closed by calling connClose()
	*
	* throws: Exception if Connection string is invalid. (If parenthesis mismatch).
	* SQLException if database error occurs
	*/
	/*public Connection getOciConn(final String alias, final String username, final String pwd, final boolean autocommit) throws Exception {

		checkAliasExist(alias);
		if (!isConnStrValid(alias)) {
			throwException("Service name '" + alias.toUpperCase() + "' is invalid. Check parenthesis", false);
		}
		final String strtmp = new String(makeSqlConnection(alias, "oci", username, pwd, autocommit));
		if (!strtmp.equalsIgnoreCase("SUCCESS")) {
			throwException(strtmp, false);
		}
		return this.conn;
	}*/

	/*
	* public Connection getThinTcpConn (String alias, String username, String pwd)
	* @param alias: Alias/Service name
	* username: Oracle database username
	* pwd: Oracle password
	* @return Connection: SQL Connection interface
	*
	* Builds and returns JDBC thin Connection. Java uses TCP stack to connect,
	* therefore connection string must specify TCP protocol.
	* If multiple ADDRESSES defined, returns first available TCP connection.
	* AutoCommit is disabled by default.
	*
	* When done, Connection MUST be closed by calling connClose()
	*
	* throws: Exception if no TCP protocol is defined
	* SQLException if database error occurs
	*/
	/*public Connection getThinTcpConn(final String alias, final String username, final String pwd) throws Exception {

		return getThinTcpConn(alias, username, pwd, false);
	}*/

	/*
	* public Connection getThinTcpConn (String alias, String username, String pwd, boolean autocommit)
	* @param alias: Alias/Service name
	* username: Oracle database username
	* pwd: Oracle password
	* autocommit: true to enable AutoCommit, false to disable AutoCommit
	* @return Connection: SQL Connection interface jdbc:oracle:thin:@::
	*
	* Builds and returns JDBC thin Connection. Java uses TCP stack to connect,
	* therefore connection string must specify TCP protocol.
	* If multiple ADDRESSES defined, returns first available TCP connection.
	*
	* When done, Connection MUST be closed by calling connClose()
	*
	* throws: Exception if no TCP protocol is defined
	* SQLException if database error occurs
	*/
	/*public Connection getThinTcpConn(final String alias, final String username, final String pwd, final boolean autocommit)
			throws Exception {

		checkAliasExist(alias);
		final String connstr = new String(getConnectString(alias));
		if (!isTcpAvailable(connstr)) {
			throwException("Service name '" + alias.toUpperCase() + "' does not use TCP protocol", false);
		}
		final String strthin = new String(buildThinString(getFirstTcpString(connstr)));
		final String strtmp = new String(makeSqlConnection(strthin, "thin", username, pwd, autocommit));
		if (!strtmp.equalsIgnoreCase("SUCCESS")) {
			throwException(strtmp, false);
		}
		return this.conn;
	}*/

	/*
	* public String extractTcpString (String alias)
	* @param alias: Alias/Service name
	* @return String: Fully concatenated Thin TCP string like :: OR null if does not exis
	t
	*
	* Extracts a TCP string if available, builds the string as used by Oracle thin jdbc driver
	*/
	public String extractTcpString(final String alias) throws Exception {

		checkAliasExist(alias);
		final String connstr = new String(getConnectString(alias));
		if (!isTcpAvailable(connstr)) {
			return null;
		}


		return buildThinString(getFirstTcpString(connstr));
	}

	/*
	* public String enumerateAddresses (String alias)
	* @param alias: Alias/Service name
	* @return String: End-Of-Line delimited list of all addresses including CONNECT_DATA
	* (ADDRESS=(.....))(CONNECT_DATA=(...))
	*
	* Parses a connection string and returns all defined ADDRESSES
	*/
	public String enumerateAddresses(final String alias) throws Exception {

		checkAliasExist(alias);
		final String connstr = new String(getConnectString(alias));
		return enumerateAddressList(connstr);
	}

	/*
	* public boolean isConnStrValid (String alias)
	* @param alias: Alias/Service name
	* @return boolean: true if valid, false otherwise
	*
	* Only checks if all parenthesis "(" and ")" are matched
	*/
	public boolean isConnStrValid(final String alias) throws Exception {

		checkAliasExist(alias);
		String strtmp = new String(getConnectString(alias));
		strtmp = strtmp + " ";
		int i = 0;
		int j = 0;
		int countera = 0;
		int counterb = 0;
		while (true) {
			i = strtmp.indexOf("(", i + 1);
			if (i == -1) {
				break;
			}
			countera = countera + 1;
		}
		while (true) {
			j = strtmp.indexOf(")", j + 1);
			if (j == -1) {
				break;
			}
			counterb = counterb + 1;
		}
		if (countera != counterb) {
			return false;
		} else {
			return true;
		}
	}

	/*
	* public String connClose ()
	* @param
	* @return
	*
	* Close SQL connection handle
	*/
	public void connClose() throws SQLException {

		if (this.conn != null) {
			try {
				if (!this.conn.isClosed()) {
					this.conn.close();
					this.conn = null;
				}
			}
			/* If SQLException, just set it to null */
			catch (final SQLException e) {
				this.conn = null;
			}
		}
	}

	/*
	* public String close ()
	* @param
	* @return
	*
	* Flush out filestr, aliasstr, tnsnamesora and close SQL Connection if open
	*/
	public void close() throws Exception {

		this.filestr = "";
		this.aliasstr = "";
		this.tnsnamesora = null;
		connClose();
	}

	/*
	* public String getVersion (String alias)
	* @param
	* @return String: Current version
	*
	* Return current TNSParse version

	public String getVersion () {
	return this.version;
	}*/

	/***************************
	**** PROTECTED METHODS ****
	***************************/

	/***************************
	***** PRIVATE METHODS *****
	***************************/

	/*
	* private String getConnectString (String alias)
	* @param alias: Alias/Service name
	* @return String: Full connection string
	*
	* Return full connection string from to to last ")"
	*/
	private String getConnectString(final String alias) throws Exception {

		String retstr = new String("");
		String tokstr = new String();
		String strtmp = new String(alias.toUpperCase().trim());
		int i = 0;
		int j = 0;
		final StringTokenizer st = new StringTokenizer(this.aliasstr, this.eol);
		while (st.hasMoreTokens()) {
			tokstr = st.nextToken().trim();
			if (tokstr.lastIndexOf(".") != -1) {
				if (strtmp.lastIndexOf(".") == -1) {
					strtmp = strtmp + tokstr.substring(tokstr.lastIndexOf("."), tokstr.length());
				}
			}
			if (strtmp.trim().equalsIgnoreCase(tokstr.trim())) {
				i = this.filestr.indexOf(strtmp.trim(), 0);
				j = this.filestr.indexOf(this.strchar5, i);
				if (i != -1 && j != -1) {
					retstr = this.filestr.substring(i, j);
				}
				break;
			}
		}
		return retstr;
	}

	/*
	* private void checkAliasExist (String alias)
	* @param alias: Alias/Service name
	* @return
	*
	* Checks if Alias/Service name exists
	*
	* throws: Exception if does not exist
	*/
	private void checkAliasExist(final String alias) throws Exception {

		final String strtmp = new String(getConnectString(alias));
		if (strtmp.equalsIgnoreCase("")) {
			throwException(
					"Service name '" + alias.toUpperCase() + "' does not exist in file '" + this.tnsnamesora.getAbsolutePath() + "'", false);
		}
	}

	/*
	* private boolean isTcpAvailable (String connstr)
	* @param connstr: Full connection string to test
	* @return boolean: true if TCP available, false otherwise
	*
	* Checks if connection string has TCP protocol defined
	*/
	private boolean isTcpAvailable(final String connstr) throws Exception {

		if (connstr.indexOf("(PROTOCOL=TCP)") == -1) {
			return false;
		} else {
			return true;
		}
	}

	/*
	* private String getFirstTcpString (String connstr)
	* @param connstr: Full connection string to parse
	* @return String: First available TCP connection string
	*
	* Returns first available TCP connection string used by getThinTcpConn()
	*/
	private String getFirstTcpString(final String connstr) throws Exception {

		String retstr = new String("");
		int i = 0;
		int j = 0;
		while (true) {
			i = connstr.indexOf("(ADDRESS=", j);
			if (i == -1) {
				break;
			}
			i = connstr.indexOf("=", i) + 1;
			j = connstr.indexOf("))", i) + 1;
			if (i != -1 && j != -1) {
				retstr = connstr.substring(i, j);
			}
			if (retstr.indexOf("PROTOCOL=TCP") != -1) {
				i = connstr.indexOf("(CONNECT_DATA=", j);
				j = connstr.indexOf("))", i) + 2;
				if (i != -1 && j != -1) {
					retstr = retstr + connstr.substring(i, j);
				}
				break;
			} else {
				retstr = "";
			}
		}
		return retstr;
	}

	/*
	* private String parseTcpString (String tcpstring, String getval)
	* @param tcpstring: Unparsed TCP string returned by getFirstTcpString()
	* @return getval: Value to get e.g. "HOST", "PORT" etc
	*
	* Parses TCP string and returns value of "HOST", "PORT" etc
	*/
	private String parseTcpString(final String tcpstring, final String getval) throws Exception {

		String retstr = new String("");
		int i = 0;
		int j = 0;
		if (getval.equalsIgnoreCase("CONNECT_DATA")) {
			i = tcpstring.indexOf(getval);
			i = tcpstring.indexOf("=", i);
			i = tcpstring.indexOf("(", i);
			i = tcpstring.indexOf("=", i) + 1;
			j = tcpstring.indexOf("))", i);
			if (i != -1 && j != -1) {
				retstr = tcpstring.substring(i, j);
			}
		} else {
			i = tcpstring.indexOf(getval);
			i = tcpstring.indexOf("=", i) + 1;
			j = tcpstring.indexOf(")", i);
			if (i != -1 && j != -1) {
				retstr = tcpstring.substring(i, j);
			}
		}
		return retstr;
	}

	/*
	* private String buildThinString (String tcpstring)
	* @param tcpstring: Unparsed TCP string returned by getFirstTcpString()
	*
	* Parses TCP string and returns a full concatenated string e.g. ::
	*/
	private String buildThinString(final String tcpstring) throws Exception {

		String retstr = new String("");
		retstr = parseTcpString(tcpstring, "HOST") + ":";
		retstr = retstr + parseTcpString(tcpstring, "PORT") + ":";
		retstr = retstr + parseTcpString(tcpstring, "CONNECT_DATA");
		return retstr;
	}

	/*
	* private String makeSqlConnection (String connstr, String drivertype, String username, String pwd, boolean
	autocommit)
	* @param connstr: Connection string. Oracle alias for oci drivers. :: for thin drive
	r
	* drivertype: Driver type to use "oci" or "thin"
	* username: Oracle database username
	* pwd: Oracle password
	* autocommit: true to enable AutoCommit, false to disable AutoCommit
	* @return String: "SUCCESS" Or SQLException error text
	*
	* Connects to the database using given url
	*/
	/*private String makeSqlConnection(final String connstr, final String drivertype, final String username, final String pwd,
			final boolean autocommit) throws SQLException {

		try {
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		} catch (final SQLException e1) {
			return "java.sql.SQLException: " + e1.getMessage();
		}

		final String connurl = new String(buildConnURL(connstr, drivertype));
		if (connurl == null) {
			return "java.sql.SQLException: No suitable oracle driver found";
		}
		try {
			this.conn = DriverManager.getConnection(connurl, username, pwd);
			this.conn.setAutoCommit(autocommit);
			return "SUCCESS";
		} catch (final SQLException e2) {
			connClose();
			return "java.sql.SQLException: " + e2.getMessage();
		}
	}*/

	/*
	* private String buildConnURL (String connstr, String drivertype)
	* @param connstr: Connection string. Oracle alias for oci drivers. :: for thin drive
	r
	* drivertype: Driver type. "oci" or "thin"
	* @return String: Full Connection URL OR null if error occurs
	*
	* Builds a full Connection URL and returns using a suitable available Oracle driver
	*/
	private String buildConnURL(final String connstr, final String drivertype) throws SQLException {

		final String strurl = new String("jdbc:oracle:" + drivertype);
		String url = new String();
		if (drivertype.equalsIgnoreCase("THIN")) {
			try {
				url = strurl + ":@" + connstr;
				final Driver driver = DriverManager.getDriver(url);
				return url;
			} catch (final SQLException e1) {
				return null;
			}
		} else {
			try {
				/* Try oci8 driver first */
				url = strurl + "8:@" + connstr;
				final Driver driver = DriverManager.getDriver(url);
				return url;
			} catch (final SQLException e2) {
				try {
					/* Now try oci7 driver */
					url = strurl + "7:@" + connstr;
					final Driver driver = DriverManager.getDriver(url);
					return url;
				} catch (final SQLException e3) {
					return null;
				}
			}
		}
	}

	/*
	* private String enumerateAddressList (String connstr)
	* @param connstr: Full connection string to parse
	* @return String: End-Of-Line delimited list of all addresses including CONNECT_DATA
	* (ADDRESS=(.....))(CONNECT_DATA=(...))
	*
	* Parses a connection string and returns all defined ADDRESSES
	*/
	private String enumerateAddressList(final String connstr) throws Exception {

		String retstr = new String("");
		String strtmp = new String("");
		int i = 0;
		int j = 0;
		int k = 0;
		int l = 0;
		while (true) {
			i = connstr.indexOf("(ADDRESS=", j);
			if (i == -1) {
				break;
			}
			j = connstr.indexOf("))", i);
			j = connstr.indexOf("(", j);
			if (i != -1 && j != -1) {
				strtmp = connstr.substring(i, j);
				if (strtmp.indexOf("(PROTOCOL=BEQ)") != -1) {
					strtmp = strtmp.substring(0, strtmp.indexOf("'))") + 3);
				} else {
					strtmp = strtmp.substring(0, strtmp.indexOf("))") + 2);
				}
				retstr = retstr + strtmp;
				strtmp = "";
			}
			k = connstr.indexOf("(CONNECT_DATA=", j);
			l = connstr.indexOf("))", k) + 2;
			if (k != -1 && l != -1) {
				retstr = retstr + connstr.substring(k, l) + this.eol;
			}
		}
		if (retstr.endsWith(this.eol)) {
			retstr = retstr.substring(0, retstr.length() - 1);
		}
		return retstr;
	}

	/*
	* private String getLastToken (String str)
	* @param str: String to test
	* @return String: Last token in the string
	*
	* Tokenize a string and return last token
	*/
	private String getLastToken(final String str) throws Exception {

		String retstr = new String("");
		if (str.length() > 0) {
			final StringTokenizer st = new StringTokenizer(str);
			while (st.hasMoreTokens()) {
				retstr = st.nextToken();
			}
		}
		return retstr;
	}

	/*
	* private void throwException (String msg, boolean flush)
	* @param msg: Exception message
	* flush: if true flush all strings and close any open Connections
	*
	* Throws new Exception
	*/
	private void throwException(final String msg, final boolean flush) throws Exception {

		if (flush) {
			close();
		}
		throw new Exception(msg);
	}

	/*
	* === EXCLUSIVE FILE READERS AND PARSERS ===
	*
	* These private methods exclusively Check, Read and Parse the Tnsnames.ora file
	* and build the filestr (Continuous string of services names without any spaces,
	* CRLF, comments or any Non-Service name data). Also builds aliasstr (End-Of-Line
	* separated string that contains all service names. ..
	*
	*
	*
	* private void tnsReadAndParseFile ()
	* @param
	* @return
	*
	* Read and parse Tnsnames.ora file. Build the filestr, aliasstr to be used globally
	*/
	private void tnsReadAndParseFile() throws Exception {

		/* Check Tnsnames.ora file existense and readability */
		tnsCheckFileValid();
		/* Read Tnsnames.ora file using FileReader. Check if file is valid format */
		tnsReadFile();
		/* Remove all spaces and replace with null's "" */
		tnsRemoveAndReplace(this.spacerep, this.nullrep);
		/* Remove all comment lines */
		tnsRemoveComments();
		/* Remove all multiple eol's with single eol */
		tnsRemoveAndReplace(this.eol, this.eol);
		/* Fix =(DESCRIPTION keyword */
		tnsFixDescKeyword();
		/* Separate all Connection strings by strchar5 */
		tnsSeparateConnStrings();
		/* Now remove all eol's with null's "". This will create one continuous string */
		tnsRemoveAndReplace(this.eol, this.nullrep);
		/* Remove any lines that are not part of connection strings */
		//tnsRemoveNonDataLines();
		/* Set Alias/Service name string */
		tnsSetAliasString();
	}

	/*
	* private void tnsCheckFileValid ()
	* @param
	* @return
	*
	* Check Tnsnames.ora file existense and readability
	*/
	private void tnsCheckFileValid() throws Exception {

		if (!this.tnsnamesora.isFile()) {
			throwException("File '" + this.tnsnamesora.getAbsolutePath() + "' does not exist or is not a normal file", true);
		}
		if (!this.tnsnamesora.canRead()) {
			throwException("File '" + this.tnsnamesora.getAbsolutePath() + "' is not readable", true);
		}
		if (this.tnsnamesora.length() == 0) {
			throwException("File '" + this.tnsnamesora.getAbsolutePath() + "' is empty", true);
		}
	}

	/*
	* private void tnsReadFile ()
	* @param
	* @return
	*
	* Read Tnsnames.ora file using FileReader. Check if file is of valid
	* Tnsnames.ora format
	*/
	private void tnsReadFile() throws IOException, Exception {

		final FileReader fr = new FileReader(this.tnsnamesora);
		final long fsize = this.tnsnamesora.length();
		final char[] cbuf = new char[(int) fsize];
		final int offset = 0;
		final int len = (int) fsize;
		fr.read(cbuf, offset, len);
		fr.close();
		final String strtmp = new String(cbuf);
		this.filestr = "";
		/* Convert everything to UPPERCASE */
		this.filestr = strtmp.toUpperCase();
		/* Check file format */
		tnsCheckFileFormat();
	}

	/*
	* private void tnsRemoveAndReplace (String remstr, String repstr)
	* @param remstr: String to be removed
	* repstr: String to be replaced with
	* @return
	*
	* Remove and replace strings from filestr
	*/
	private void tnsRemoveAndReplace(final String remstr, final String repstr) throws Exception {

		final String strtmp = new String(this.filestr);
		this.filestr = "";
		if (strtmp.indexOf(remstr) != -1) {
			final StringTokenizer st = new StringTokenizer(strtmp, remstr);
			while (st.hasMoreTokens()) {
				this.filestr = this.filestr + st.nextToken() + repstr;
			}
		}
		if (this.filestr.equalsIgnoreCase("")) {
			this.filestr = strtmp;
		}
		tnsCheckFileFormat();
	}

	/*
	* private void tnsRemoveComments ()
	* @param
	* @return
	*
	* Remove all comment lines from filestr marked by "#"
	*/
	private void tnsRemoveComments() throws Exception {

		final String strtmp = new String(this.filestr);
		this.filestr = "";
		String tokenstr = new String();
		if (strtmp.indexOf(this.commchar + "") != -1) {
			final StringTokenizer st = new StringTokenizer(strtmp, this.eol);
			while (st.hasMoreTokens()) {
				tokenstr = st.nextToken().trim();
				if (tokenstr.charAt(0) != this.commchar) {
					this.filestr = this.filestr + tokenstr + this.eol;
				}
			}
		}
		if (this.filestr.equalsIgnoreCase("")) {
			this.filestr = strtmp;
		}
		tnsCheckFileFormat();
	}

	/*
	* private void tnsFixDescKeyword ()
	* @param
	* @return
	*
	* Fix =(DESCRIPTION keyword so that there are no spaces and eol's between '='
	* and '(DESCRIPTION'
	*/
	private void tnsFixDescKeyword() throws Exception {

		final String strtmp = new String(this.filestr);
		this.filestr = "";
		int i = 0;
		int j = 0;
		if (strtmp.indexOf("=" + this.eol + "(DESCRIPTION") != -1) {
			while (strtmp.indexOf("=" + this.eol + "(DESCRIPTION", i) != -1) {
				j = strtmp.indexOf("=" + this.eol + "(DESCRIPTION", i);
				this.filestr = this.filestr + strtmp.substring(i, j + 1);
				i = strtmp.indexOf("(", j);
				if (strtmp.indexOf("=" + this.eol + "(DESCRIPTION", i) == -1) {
					this.filestr = this.filestr + strtmp.substring(i, strtmp.length());
				}
			}
		}
		if (this.filestr.equalsIgnoreCase("")) {
			this.filestr = strtmp;
		}
		tnsCheckFileFormat();
	}

	/*
	* private void tnsSeparateConnStrings ()
	* @param
	* @return
	*
	* Separate all Connection strings using constant strchar5
	*/
	private void tnsSeparateConnStrings() throws Exception {

		final String strtmp = new String(this.filestr);
		this.filestr = "";
		String strext = new String();
		String tokstr = new String();
		boolean bExit = false;
		int i = 0;
		int j = 0;
		if (strtmp.indexOf("=(DESCRIPTION") != -1) {
			while (true) {
				tokstr = getLastToken(strtmp.substring(i, strtmp.indexOf("=(DESCRIPTION", i)));
				i = strtmp.indexOf(tokstr, i);
				/* Jump 20 spaces to deal with =(DESCRIPTION_LIST=(DESCRIPTION */
				j = strtmp.indexOf("=(DESCRIPTION", strtmp.indexOf("=(DESCRIPTION", i) + 20);
				if (j == -1) {
					j = strtmp.length();
					bExit = true;
				} else {
					tokstr = getLastToken(strtmp.substring(i, j));
					j = strtmp.substring(0, j).lastIndexOf(tokstr);
				}
				strext = strtmp.substring(i, j);
				this.filestr = this.filestr + strext + this.strchar5;
				if (bExit) {
					break;
				}
				i = j;
			}
		}
		if (this.filestr.equalsIgnoreCase("")) {
			this.filestr = strtmp;
		}
		if (this.filestr.endsWith(this.strchar5)) {
			this.filestr = this.filestr.substring(0, this.filestr.length() - 1);
		}
		tnsCheckFileFormat();
	}

	/*
	* private void tnsRemoveNonDataLines ()
	* @param
	* @return
	*
	* Remove all lines if any, that are not comments and not part of connection strings
	*/
	private void tnsRemoveNonDataLines() throws Exception {

		final String strtmp = new String(this.filestr);
		this.filestr = "";
		String tokstr = new String();
		final boolean bOk = true;
		int i = 0;
		if (strtmp.indexOf("=(DESCRIPTION") != -1) {
			final StringTokenizer st = new StringTokenizer(strtmp, this.strchar5);
			while (st.hasMoreTokens()) {
				tokstr = st.nextToken();
				i = 0;
				i = tokstr.lastIndexOf("(CONNECT_DATA=");
				if (i == -1) {
					i = tokstr.length();
				} else {
					if (tokstr.indexOf("(SOURCE_ROUTE=", i) != -1) {
						i = tokstr.indexOf("(SOURCE_ROUTE=", i);
					}
					if (tokstr.indexOf(")", i) == -1) {
						i = tokstr.length();
					} else {
						i = tokstr.indexOf(")", i);
						//while (i <> i = i + 1;
						if (i >= tokstr.length() || tokstr.charAt(i) != (char) 41) {
							break;
						}
					}
				}
			}
			this.filestr = this.filestr + tokstr.substring(0, i) + this.strchar5;
		}
		//}
		if (this.filestr.equalsIgnoreCase("")) {
			this.filestr = strtmp;
		}
		tnsCheckFileFormat();
	}

	/*
	* private void tnsSetAliasString ()
	* @param
	* @return
	*
	* Set End-Of-Line delimited aliasstr that contains all service names
	*/
	private void tnsSetAliasString() throws Exception {

		final String strtmp = new String(this.aliasstr);
		this.aliasstr = "";
		String tokstr = new String();
		final StringTokenizer st = new StringTokenizer(this.filestr, this.strchar5);
		while (st.hasMoreTokens()) {
			tokstr = st.nextToken();
			if (tokstr.indexOf("=", 0) != -1) {
				this.aliasstr = this.aliasstr + tokstr.substring(0, tokstr.indexOf("=", 0)) + this.eol;
			}
		}
		if (this.aliasstr.endsWith(this.eol)) {
			this.aliasstr = this.aliasstr.substring(0, this.aliasstr.length() - 1);
		}
	}

	/*
	* private void tnsCheckFileFormat ()
	* @param
	* @return
	*
	* Check filestr for Tnsnames.ora file keywords. If missing, throw Exception
	*/
	private void tnsCheckFileFormat() throws Exception {

		if (this.filestr.length() > 0) {
			if (this.filestr.indexOf("DESCRIPTION") == -1 || this.filestr.indexOf("ADDRESS") == -1
					|| this.filestr.indexOf("CONNECT_DATA") == -1) {
				throwException("File '" + this.tnsnamesora.getAbsolutePath()
						+ "' is not an Oracle tnsnames.ora format file OR unable to parse", true);
			}
		}
	}


	public static void main(final String[] args) {
		try {
			final TNSParser parser = new TNSParser(new File("C:/oracle/product/10.2.0/db_1/network/admin/tnsnames.ora"));
			for (final String alias : parser.getAliasList().split("\\n")) {
				final String tcp = parser.extractTcpString(alias);
				System.out.println(tcp);
				if (tcp != null) {

				}
				final String conStr = parser.getConnectString(alias);
				System.out.println(conStr);
				final String fullConStr = parser.getFullConnString(alias);
				System.out.println(fullConStr);
				final String thinStr = parser.buildThinString(tcp);
				System.out.println(thinStr);
				final String frstTcpStr = parser.getFirstTcpString(conStr);
				System.out.println(frstTcpStr);

			}

		} catch (final Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
}
