package org.fastcode.util;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.fastcode.common.FastCodeTNSFields;

public class FastCodeTNSParser {
	private File			tnsnamesora	= null;
	private String			filestr		= "";
	private final String	spacerep	= " ";
	private final String	nullrep		= "";
	private String			aliasstr	= "";
	private final String	eol			= "\r\n";
	private final char		commchar	= '#';
	private final String	strchar5	= (char) 5 + "";

	public FastCodeTNSParser(final File tnsFile) throws Exception {

		this.tnsnamesora = tnsFile;
		tnsReadAndParseFile();
	}

	public String getAliasList() throws Exception {

		return this.aliasstr;
	}

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

	private void throwException(final String msg, final boolean flush) throws Exception {

		if (flush) {
			close();
		}
		throw new Exception(msg);
	}

	public void close() throws Exception {

		this.filestr = "";
		this.aliasstr = "";
		this.tnsnamesora = null;
	}

	private void tnsCheckFileFormat() throws Exception {

		if (this.filestr.length() > 0) {
			if (this.filestr.indexOf("DESCRIPTION") == -1 || this.filestr.indexOf("ADDRESS") == -1
					|| this.filestr.indexOf("CONNECT_DATA") == -1) {
				throwException("File '" + this.tnsnamesora.getAbsolutePath()
						+ "' is not an Oracle tnsnames.ora format file OR unable to parse", true);
			}
		}
	}

	private void checkAliasExist(final String alias) throws Exception {

		final String strtmp = new String(getConnectString(alias));
		if (strtmp.equalsIgnoreCase("")) {
			throwException(
					"Service name '" + alias.toUpperCase() + "' does not exist in file '" + this.tnsnamesora.getAbsolutePath() + "'", false);
		}
	}

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

	public FastCodeTNSFields extractTcpString(final String alias) throws Exception {

		checkAliasExist(alias);
		final String connstr = new String(getConnectString(alias));
		if (!isTcpAvailable(connstr)) {
			return null;
		}
		return setTCPToPref(getFirstTcpString(connstr));
	}

	private FastCodeTNSFields setTCPToPref(final String tcpstring) throws Exception {


		final String host = parseTcpString(tcpstring, "HOST");
		final String port = parseTcpString(tcpstring, "PORT");
		//final String conData = parseTcpString(tcpstring, "CONNECT_DATA");
		final String server = parseTcpString(tcpstring, "SERVER");
		final String serviceName = parseTcpString(tcpstring, "SERVICE_NAME");
		final FastCodeTNSFields fcTNSFields = new FastCodeTNSFields(host, port, server, serviceName);
		System.out.println(host);
		System.out.println(port);
		//System.out.println(conData);
		System.out.println(server);
		System.out.println(serviceName);
		return fcTNSFields;

		/*String retstr = new String("");
		retstr = parseTcpString(tcpstring, "HOST") + ":";
		retstr = retstr + parseTcpString(tcpstring, "PORT") + ":";
		retstr = retstr + parseTcpString(tcpstring, "CONNECT_DATA");
		return retstr;*/
	}

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

	private boolean isTcpAvailable(final String connstr) throws Exception {

		if (connstr.indexOf("(PROTOCOL=TCP)") == -1) {
			return false;
		} else {
			return true;
		}
	}

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

	public static List<FastCodeTNSFields> parseTNSFields(final File tnsFile) throws Exception {
		final List<FastCodeTNSFields> tnsFieldsList = new ArrayList<FastCodeTNSFields>();
		final FastCodeTNSParser parser = new FastCodeTNSParser(tnsFile);//new File("C:/oracle/product/10.2.0/db_1/network/admin/tnsnames.ora"));
		for (final String alias : parser.getAliasList().split("\\n")) {
			final FastCodeTNSFields tnsFields = parser.extractTcpString(alias);
			if (tnsFields != null) {
				tnsFieldsList.add(tnsFields);
			}
		}
		return tnsFieldsList;

	}

	public static void main(final String[] args) {
		try {



		} catch (final Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}
}
