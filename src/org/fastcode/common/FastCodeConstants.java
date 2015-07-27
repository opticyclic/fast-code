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

package org.fastcode.common;

import static org.fastcode.util.StringUtil.isEmpty;

/**
 * @author Gautam
 *
 */
public class FastCodeConstants {

	public static final String	FAST_CODE_PLUGIN_ID						= "org.fastcode";

	public static final String	TEST_PREFIX								= "Test";

	public static final String	EMPTY_QUOTE_STR							= "\"\"";

	public static final char	QUOTE_STR								= '\"';

	public static final String	EQUAL_WITH_SPACE						= " = ";

	public static final String	EMPTY_STR								= "";

	public static final String	FALSE_STR								= "false";

	public static final String	TRUE_STR								= "true";

	public static final String	VALUE									= "value";

	public static final String	JAVA_EXTENSION							= "java";

	public static final String	XML_EXTENSION							= "xml";

	public static final String	JSP_EXTENSION							= "jsp";

	public static final String	JS_EXTENSION							= "js";

	public static final String	LEFT_PAREN								= "(";

	public static final String	RIGHT_PAREN								= ")";

	public static final String	SPACE									= " ";

	public static final char	SPACE_CHAR								= ' ';

	public static final String	DOT										= ".";

	public static final String	COPYOF									= "copyOf";

	public static final String	SRC_JAVA_PATH							= "src/main/java/";

	public static final String	SRC_TEST_PATH							= "src/test/java/";

	public static final String	NEW_IMPORT								= "NEW_IMPORT";

	public static final String	METHOD_PATTERN_DEFAULT					= "${method_name}";

	public static final String	CLASS_NAME_STR							= "class_name";

	public static final String	DEFAULT_CLASS_NAME_STR					= "class";

	public static final String	FULL_CLASS_NAME_STR						= "full_" + CLASS_NAME_STR + "_name";

	public static final String	ENCLOSING_CLASS_STR						= "enclosing_class";

	public static final String	ENCLOSING_FILE_STR						= "enclosing_file";

	public static final String	CLASS_MODIFIER_STR						= "class_modifier";

	public static final String	CLASS_INSTANCE_STR						= "class_instance";

	public static final String	INSTANCE_STR							= "instance";

	public static final String	FROM_INSTANCE_STR						= "from_instance";

	public static final String	TO_INSTANCE_STR							= "to_instance";

	public static final String	PACKAGE_NAME_STR						= "package_name";

	public static final String	CLASS_ANNOTATIONS_STR					= "class_annotations";

	public static final String	CLASS_COMMENTS_STR						= "class_comments";

	public static final String	CLASS_TYPE_STR							= "class_type";

	public static final String	CLASS_IMPORTS_STR						= "class_imports";

	public static final String	CLASS_BODY_STR							= "class_body";

	public static final String	CLASS_HEADER_STR						= "class_header";

	public static final String	FIELD_ANNOTATIONS_STR					= "field_annotations";

	public static final String	METHOD_NAME_STR							= "method_name";

	public static final String	METHOD_EQUALS							= "equals";

	public static final String	METHOD_HASHCODE							= "hashCode";

	public static final String	METHOD_TOSTRING							= "toString";

	public static final String	METHOD_ANNOTATIONS_STR					= "method_annotations";

	public static final String	METHOD_MODIFIER_STR						= "method_modifier";

	public static final String	DEFAULT_TEMPLATE_VARIATION_FIELD		= "default.template.variation.field";

	public static final String	DEFAULT_TEMPLATE_VARIATION_FIELD_NAME	= "default.template.variation.field.name";

	public static final String	DEFAULT_TEMPLATE_VARIATION_FIELD_VALUE	= "_template_variation";

	public static final String	MODIFIER_PUBLIC							= "public";

	public static final String	MODIFIER_PRIVATE						= "private";

	public static final String	METHOD_EXCEPTIONS_STR					= "method_exceptions";

	public static final String	METHOD_COMMENTS_STR						= "method_comments";

	public static final String	METHOD_RETURN_TYPE_STR					= "method_return_type";

	public static final String	METHOD_RETURN_TYPE_VOID					= "void";

	public static final String	FIELD_TYPE_STR							= "field_type";

	public static final String	FIELD_CLASS_STR							= "field_class";

	public static final String	FIELD_FULL_CLASS_STR					= "field_full_class";

	public static final String	FIELD_MODIFIER_STR						= "field_modifier";

	public static final String	FIELD_NAME_STR							= "field_name";

	public static final String	METHOD_ARGS_STR							= "method_args";

	public static final String	METHOD_BODY_STR							= "method_body";

	public static final String	EXCLUDE_FIELDS_FROM_SNIPPETS			= "exclude.fields.from.snippets";

	public static final String	EXCLUDE_FIELDS_FILE_EXTENSIONS			= "exclude.fields.file.extensions";

	public static final String	ENUM									= "enum";

	public static final String	METHOD_PRIVATE							= "method_private";

	public static final String	METHOD_PROTECTED						= "method_protected";

	public static final String	METHOD_PUBLIC							= "method_public";

	public static enum RETURN_TYPE {
		RETURN_TYPE_PASS_THRU, RETURN_TYPE_CONSUME
		// Used for Junits for now.
	}

	/**
	 *
	 * @author Gautam
	 *
	 */
	public static enum GETTER_SETTER {
		GETTER_EXIST("getter"), SETTER_EXIST("setter"), GETTER_SETTER_EXIST("gettersetter"), GETTER_ADDER_EXIST("getteradder"), NONE("none");

		private String	value;

		/**
		 *
		 * @param value
		 */
		private GETTER_SETTER(final String value) {
			setValue(value);
		}

		/**
		 *
		 * @param arg
		 * @return
		 */
		public static GETTER_SETTER getGetterSetter(final String arg) {
			for (final GETTER_SETTER gs : GETTER_SETTER.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			if (!isEmpty(arg)) {
				return null;
			}
			return NONE;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(final String value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return this.value;
		}

		public static String getFormattedValue(final String arg) {
			if (!isEmpty(arg) && arg.equalsIgnoreCase(GETTER_SETTER_EXIST.value) || arg.equalsIgnoreCase(GETTER_ADDER_EXIST.value)) {
				return arg.substring(0, "getter".length()) + " " + arg.substring("getter".length(), arg.length());
			}
			if (!isEmpty(arg)) {
				if (arg.equals(NONE.value)) {
					return EMPTY_STR;
				}
				return arg;
			}
			return EMPTY_STR;
		}
	}

	public static enum CREATE_OPTIONS_CHOICE {
		ALWAYS_CREATE, ASK_TO_CREATE, NEVER_CREATE
	}

	public static enum JUNIT_TYPE {
		JUNIT_TYPE_3, JUNIT_TYPE_4, JUNIT_TYPE_TESTNG, JUNIT_TYPE_CUSTOM
	}

	public static enum CLASS_TYPE {
		CLASS("Class"), INTERFACE("Interface"), TEST("Test");

		private final String	value;

		/**
		 *
		 * @param value
		 */
		private CLASS_TYPE(final String value) {
			this.value = value;
		}

		/**
		 *
		 * @return
		 */
		public String value() {
			return this.value;
		}

		/**
		 *
		 * @param value
		 * @return
		 */
		public static CLASS_TYPE getClassType(final String value) {
			for (final CLASS_TYPE type : CLASS_TYPE.values()) {
				if (type.value.equalsIgnoreCase(value)) {
					return type;
				}
			}
			return null;
		}
	}

	public static enum RELATION_TYPE {
		RELATION_TYPE_IMPLEMENTS, RELATION_TYPE_EXTENDS
	}

	public static enum ACCESS_MODIFIER {
		PRIVATE("private"), PUBLIC("public"), PROTECTED("protected"), DEFAULT("default");

		private String	value;

		/**
		 *
		 * @param value
		 */
		private ACCESS_MODIFIER(final String value) {
			setValue(value);
		}

		/**
		 *
		 * @param arg
		 * @return
		 */
		public static ACCESS_MODIFIER getGetterSetter(final String arg) {
			for (final ACCESS_MODIFIER ac : ACCESS_MODIFIER.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(ac.value)) {
					return ac;
				}
			}
			return PRIVATE;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(final String value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return this.value;
		}
	}

	public static final String	GLOBAL_ALWAYS_CREATE						= "GLOBAL ALWAYS CREATE";

	public static final String	GLOBAL_EXTENDS								= "GLOBAL EXTENDS";

	public static final String	GLOBAL_IMPLEMENTS							= "GLOBAL IMPLEMENTS";

	public static final String	GLOBAL_NO_RELATION							= "GLOBAL NO RELATION";

	public static final String	GLOBAL_CLASS								= "GLOBAL CLASS";

	public static final String	GLOBAL_INTERFACE							= "GLOBAL INTERFACE";

	public static final String	GLOBAL_ASK_TO_CREATE						= "GLOBAL ASK TO CREATE";

	public static final String	GLOBAL_NEVER_CREATE							= "GLOBAL NEVER CREATE";

	public static final String	GLOBAL_MAKE_PLURAL							= "GLOBAL MAKE PLURAL";

	public static final String	GLOBAL_ADD_TYPE_END							= "GLOBAL ADD TYPE END";

	public static final String	GLOBAL_MAKE_PLURAL_ADD_TYPE_END				= "GLOBAL MAKE PLURAL ADD TYPE END";

	public static final String	TAB											= "\t";

	public static final String	BUTTON_TEXT_BROWSE							= "Browse";

	public static final String	MY_NEW										= "myNew";

	public static final String	DEFAULT_IMPL_EXTENSION						= "Impl";

	public static final String	FILE_TYPE_PROPERTY							= "properties";

	public static final String	FILE_TYPE_XML								= "xml";

	public static final String	CUSTOM										= "custom";

	public static final String	COLON										= ":";

	public static final String	SEMICOLON									= ";";

	public static final String	COMMA										= ",";

	public static final String	NEWLINE										= "\n";

	public static final String	JAVADOC_COMMENT_START						= "/**";

	public static final String	JAVADOC_COMMENT_END							= " */";

	public static final char	LINEFEED									= '\r';

	public static final String	FASTCODE									= "FASTCODE";

	public static final String	TODAY										= "today";

	public static final String	DEFAULT										= "default";

	public static final String	USER										= "user";

	public static final String	HYPHEN										= "-";

	public static final String	UNDERSCORE									= "_";

	public static final String	KEYWORD_TO_INSTANCE							= "to_instance";

	public static final String	KEYWORD_FROM_INSTANCE						= "from_instance";

	public static final String	KEYWORD_FROM_CLASS							= "from_class";

	public static final String	KEYWORD_FROM_FULL_CLASS						= "from_full_class";

	public static final String	KEYWORD_TO_CLASS							= "to_class";

	public static final String	KEYWORD_TO_PACKAGE							= "to_package";

	public static final String	KEYWORD_TO_IMPL_PACKAGE						= "to_impl_package";

	public static final String	KEYWORD_TO_FULL_CLASS						= "to_full_class";

	public static final String	KEYWORD_TO_FULL_IMPL_CLASS					= "to_full_impl_class";

	public static final String	KEYWORD_TO_IMPL_CLASS						= "to_impl_class";

	public static final String	_KEYWORD_FROM_TYPE							= "_from_type";

	public static final String	_KEYWORD_TO_TYPE							= "_to_type";

	public static final String	CONVERSION_NONE								= "None";

	public static final String	CONVERSION_LOWER_CASE						= "Lower Case";

	public static final String	EXTENSION_OTHER								= "Other";

	public static final String	CONVERSION_CAMEL_CASE						= "Camel Case";

	public static final String	CONVERSION_CAMEL_CASE_HYPHEN				= "Camel Case (Hyphen)";

	public static final String	ALL_LETTER_UPPER_CASE_PATTERN				= "[A-Z]*";

	public static final String	EXIT_KEY									= "_exit";

	public static final String	MAPPING										= "Mapping";

	public static final String	ANY_PACKAGE									= "ANY_PACKAGE";

	public static final String	ANY_CLASS									= "ANY_CLASS";

	public static final int		MAPPING_MAX									= 6;

	public static final int		NUM_MAX_CONFIGS								= 5;

	public static final int		NUM_SIMILAR_CLASSES_DEFAULT					= 2;

	public static final int		MESSAGE_DIALOG_RETURN_YES					= 2;

	public static final String	FORWARD_SLASH								= "/";

	public static final String	BACK_SLASH									= "\\";

	public static final String	DOUBLE_FORWARD_SLASH						= "//";

	public static final String	JDBC										= "jdbc";

	public static final String	MYSQL_JDBC_DRIVER							= "com.mysql.jdbc.Driver";

	public static final String	ORACLE_JDBC_DRIVER							= "oracle.jdbc.driver.OracleDriver";

	public static final String	MYSQL_ALL_TABLES_SELECT						= "select  distinct table_name from information_schema.columns where table_schema='";

	public static final String	MYSQL_ALL_COLUMN_DETAILS_SELECT_PART1		= "select column_name,data_type,character_maximum_length,is_nullable from information_schema.columns where table_name='";

	public static final String	MYSQL_ALL_COLUMN_DETAILS_SELECT_PART2		= "' and table_schema='";

	public static final String	SINGLE_QUOTATION_MARK						= "'";

	public static final String	ORACLE_ALL_TABLES_SELECT					= "select table_name from user_tables";

	public static final String	ORACLE_ALL_COLUMN_DETAILS_SELECT_PART1		= "select column_name,data_type,data_length,nullable,data_precision,data_scale from all_tab_columns where table_name like '";

	public static final String	ORACLE_ALL_COLUMN_DETAILS_SELECT_PART2		= " AND OWNER='";

	public static final String	MYSQL										= "mysql";

	public static final String	ORACLE										= "oracle";

	public static final String	SQLSERVER									= "sqlserver";

	public static final String	HSQLDB										= "hsqldb";

	public static final String	SYBASE										= "sybase";

	public static final String	THIN										= "thin";

	public static final String	AT_THE_RATE									= "@";

	public static final String	HSQLDB_JDBC_DRIVER							= "org.hsqldb.jdbc.JDBCDriver";

	public static final String	SQLSERVER_JDBC_DRIVER						= "";

	public static final String	SYBASE_JDBC_DRIVER							= "com.sybase.jdbc.SybDriver";

	public static final String	SQLSERVER_ALL_TABLES						= "";

	public static final String	HSQLDB_ALL_TABLES							= "select table_name from information_schema.system_tables where table_type='TABLE'";

	public static final String	SYBASE_ALL_TABLES							= "select * from sysobjects where type = 'U'";

	public static final String	SQLSERVER_ALL_COLUMN_DETAILS_SELECT			= "";

	public static final String	HSQLDB_ALL_COLUMN_DETAILS_SELECT			= "select column_name, type_name, column_size, is_nullable from information_schema.system_columns where table_name  like '";

	public static final String	SYBASE_ALL_COLUMN_DETAILS_SELECT			= "select c.column_name from systabcol c key join systab t on t.table_id=c.table_id where t.table_name='";

	public static final String	ORACLE_PRIMARY_KEY_SELECT_FIRST_PART		= "SELECT cols.column_name FROM all_constraints cons, all_cons_columns cols WHERE cols.table_name ='";

	public static final String	ORACLE_PRIMARY_KEY_SELECT_SECOND_PART		= "'  AND cons.constraint_type = 'P'  AND cons.constraint_name = cols.constraint_name AND cons.owner = cols.owner and cols.owner='";

	public static final String	MYSQL_PRIMARY_KEY_SELECT_FIRST_PART			= "select column_name from information_schema.columns where table_name='";

	public static final String	MYSQL_PRIMARY_KEY_SELECT_SECOND_PART		= "'  AND column_key='PRI' and table_schema='";

	public static final String	NO											= "NO";

	public static final String	YES											= "YES";

	public static final String	N											= "N";

	public static final String	Y											= "Y";

	public static final String	NUMBER										= "number";

	public static final String	INT											= "int";

	public static final String	DECIMAL										= "decimal";

	public static final String	DOUBLE										= "double";

	public static final String	FLOAT										= "float";

	public static final String	INTEGER										= "integer";

	public static final String	DATE										= "date";

	public static final String	TIMESTAMP									= "timestamp";

	public static final String	TIMESTAMP6									= "timestamp(6)";

	public static final String	DATETIME									= "datetime";

	public static final String	BIGINT										= "bigint";

	public static final String	SMALLINT									= "smallint";

	public static final String	TINYINT										= "tinyint";

	public static final String	BINARY										= "binary";

	public static final String	VARBINARY									= "varbinary";

	public static final String	LONGVARBINARY								= "longvarbinary";

	public static final String	BIT											= "bit";

	public static final String	TIME										= "time";

	public static final String	HSQL										= "hsql";

	// public static final String IFEXIST_TRUE = "ifexist=true";

	public static final String	POSTGRESQL									= "postgresql";

	public static final String	POSTGRESQL_JDBC_DRIVER						= "org.postgresql.Driver";

	public static final String	POSTGRESQL_ALL_TABLES						= "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'";

	public static final String	POSTGRESQL_ALL_COLUMN_DETAILS_SELECT_PART1	= "select column_name, data_type, character_maximum_length, is_nullable from information_schema.columns WHERE table_name='";

	public static final String	POSTGRESQL_ALL_COLUMN_DETAILS_SELECT_PART2	= "' and table_schema='";

	public static final String	POSTGRESQL_PRIMARY_KEY_SELECT_FIRST_PART	= "SELECT pg_attribute.attname FROM pg_index, pg_class, pg_attribute WHERE pg_class.oid = '";

	public static final String	POSTGRESQL_PRIMARY_KEY_SELECT_SECOND_PART	= "'::regclass AND indrelid = pg_class.oid AND pg_attribute.attrelid = pg_class.oid AND pg_attribute.attnum = any(pg_index.indkey) AND indisprimary";

	public static final String	REAL										= "real";

	public static final String	DOUBLEPRECISION								= "double precision";

	public static final String	TEXT										= "text";

	public static final String	CHARACTERVARYING							= "character varying";

	public static final String	CHARACTER									= "character";

	public static final String	BOOLEAN										= "boolean";

	public static final String	BYTEA										= "bytea";

	/*
	 * public static enum TEMPLATE_KEYWORD { ALL_TEMPLATES,
	 * ALL_ADDITIONAL_TEMPLATES, ALL_DATABASE_TEMPLATES,
	 * ALL_ADDITIONAL_DATABASE_TEMPLATES, GLOBAL_PROPS }
	 */

	public static enum TEMPLATE_PREFERENCE_NAME {
		ALL_TEMPLATES("TEMPLATE"), ALL_COMMON_TEMPLATES("COMMON_TEMPLATE"), ALL_DATABASE_TEMPLATES("DATABASE_TEMPLATE"), /*ALL_ADDITIONAL_DATABASE_TEMPLATES(
																															"ADDITIONAL_DATABASE_TEMPLATE"),*/GLOBAL_PROPS(
				""), ALL_FILE_TEMPLATES("FILE_TEMPLATE");

		private final String	value;

		/**
		 *
		 * @param value
		 */
		private TEMPLATE_PREFERENCE_NAME(final String value) {
			this.value = value;
		}

		/**
		 *
		 * @return
		 */
		public String value() {
			return this.value;
		}

		/**
		 *
		 * @param value
		 * @return
		 */
		public static String getTemplatePrefix(final String arg) {
			for (final TEMPLATE_PREFERENCE_NAME templatePrefix : TEMPLATE_PREFERENCE_NAME.values()) {
				if (templatePrefix.name().equalsIgnoreCase(arg)) {
					return templatePrefix.value();
				}
			}
			return null;
		}
	}

	public static final String	TABLE				= "table";

	public static final String	POJO				= "POJO";

	public static final String	HASH				= "#";

	public static final String	DOUBLE_HASH			= "##";

	public static final String	LIST				= "List";

	public static final String	SET					= "Set";

	public static final String	STATIC				= "static";

	public static final String	FINAL				= "final";

	public static final String	NEW					= "new";

	public static final String	LEFT_CURL			= "{";

	public static final String	RIGHT_CURL			= "}";

	public static final String	DOUBLE_QUOTES		= "\"";

	public static final String	MAP					= "Map";

	public static final String	STRING_DATA_TYPE	= "String";

	public static final String	STRING_INSTANCE		= "string";

	public static final String	NUMERIC				= "numeric";

	public static final String	GROOVY_EXTENSION	= "groovy";

	/**
	 *
	 * @author Gautam
	 *
	 */
	public static enum GETTER_SETTER_FORMAT {
		MULTILINE_WITH_COMMENT("multilineComment"), MULTILINE_WITHOUT_COMMENT("multilineNoComment"), SINGLE_LINE("singleLine"), CUSTOM(
				"custom");

		private String	value;

		/**
		 *
		 * @param value
		 */
		private GETTER_SETTER_FORMAT(final String value) {
			setValue(value);
		}

		/**
		 *
		 * @param arg
		 * @return
		 */
		public static GETTER_SETTER_FORMAT getGetterSetterFormat(final String arg) {
			for (final GETTER_SETTER_FORMAT gs : GETTER_SETTER_FORMAT.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return null;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(final String value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return this.value;
		}
	}

	public static final String	LOMBOK_ACCESS_LEVEL		= "AccessLevel";

	public static final String	LOMBOK_GETTER			= "Getter";

	public static final String	LOMBOK_SETTER			= "Setter";

	public static final String	SELECT_PARAMETER_TITLE	= "Select Parameter";

	public static final String	SELECT_TYPE_TITLE		= "Select Type";

	public static enum QUERY_CHOICES {
		CREATE_NEW_NAMED_QUERY("createnewnamedquery"), USE_EXISTING_NAMED_QUERY("useexistingnamedquery");
		private String	value;

		private QUERY_CHOICES(final String value) {
			setValue(value);
		}

		public static QUERY_CHOICES getQueryChoices(final String arg) {
			for (final QUERY_CHOICES gs : QUERY_CHOICES.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return null;

		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}

	public static enum WHERE_CLAUSE_QUALIFIER {
		EQUAL("="), NOT_EQUAL("!="), LIKE("LIKE"), NOT_LIKE("NOT LIKE"), IS_NULL("IS NULL"), IS_NOT_NULL("IS NOT NULL"), IN("IN"), BETWEEN(
				"BETWEEN"), NONE("");
		private String	value;

		private WHERE_CLAUSE_QUALIFIER(final String value) {
			setValue(value);
		}

		public static WHERE_CLAUSE_QUALIFIER getWhereClauseQualifier(final String arg) {
			for (final WHERE_CLAUSE_QUALIFIER gs : WHERE_CLAUSE_QUALIFIER.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return NONE;

		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}

	public static enum WHERE_CLAUSE_SEPARATOR {
		AND("and"), OR("or"), NONE("");
		private String	value;

		private WHERE_CLAUSE_SEPARATOR(final String value) {
			setValue(value);
		}

		public static WHERE_CLAUSE_SEPARATOR getWhereClauseSeparator(final String arg) {
			for (final WHERE_CLAUSE_SEPARATOR gs : WHERE_CLAUSE_SEPARATOR.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return NONE;

		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}

	public static final String	SQL_QUERY	= "sql-query";

	public static final String	NAMED_QUERY	= "named-query";

	public static enum SECOND_TEMPLATE {
		method("method"), field("field"), both("both"), custom("custom"), Class("class"), file("file"), none("none"), property("property"), data(
				"data");

		private String	value;

		/**
		 *
		 * @param value
		 */
		private SECOND_TEMPLATE(final String value) {
			setValue(value);
		}

		/**
		 *
		 * @param arg
		 * @return
		 */
		public static SECOND_TEMPLATE getSecondTemplate(final String arg) {
			for (final SECOND_TEMPLATE gs : SECOND_TEMPLATE.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			if (!isEmpty(arg)) {
				return null;
			}
			return none;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(final String value) {
			this.value = value;

		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return this.value;
		}

	}

	public static enum JUNIT_TEST_CHOICES {
		REGULAR_TEST("regulartest"), EXCEPTION_TEST("exceptiontest");
		private String	value;

		private JUNIT_TEST_CHOICES(final String value) {
			setValue(value);
		}

		public static JUNIT_TEST_CHOICES getJunitTestChoices(final String arg) {
			for (final JUNIT_TEST_CHOICES gs : JUNIT_TEST_CHOICES.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return null;

		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}

	public static final String	NAMED_QUERIES_STR		= "NamedQueries";

	public static final String	ENTITY_STR				= "Entity";

	public static final String	NAMED_QUERIES_ANNOT_1	= "@NamedQueries({";

	public static final String	NAMED_QUERIES_ANNOT_2	= "})";

	public static final String	NAMED_QUERY_ANNOT_1		= "@NamedQuery(name = \"";

	public static final String	NAMED_QUERY_ANNOT_2		= "\", query = \"";

	public static final String	NAMED_QUERY_ANNOT_3		= "\")";

	public static final String	QUERY_NAME_STR			= "queryName";

	public static enum UNIT_TEST_TYPE {
		REGULAR_TEST("regulartest"), EXCEPTION_TEST("exceptiontest");
		private String	value;

		private UNIT_TEST_TYPE(final String value) {
			setValue(value);
		}

		public static UNIT_TEST_TYPE getTestType(final String arg) {
			for (final UNIT_TEST_TYPE gs : UNIT_TEST_TYPE.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return null;

		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}

	public static enum UNIT_TEST_CHOICE {
		CREATEADDITIONALTEST("CreateAdditionalTest"), JUMPTOTEST("JumpToTheTest");
		private String	value;

		private UNIT_TEST_CHOICE(final String value) {
			setValue(value);
		}

		public static UNIT_TEST_CHOICE getUnitTestChoices(final String arg) {
			for (final UNIT_TEST_CHOICE gs : UNIT_TEST_CHOICE.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return null;

		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}

	public static enum HANDLE_EXCEPTION {
		CONSUME, THROW,
	}

	public static final String	METHOD_ANNOTATION_TAG		= "methodannotation";

	public static final String	CLASS_ANNOTATION_TAG		= "classannotation";

	public static final String	CLASS_IMPORT_TAG			= "classimport";

	public static final String	SELECTED_POJO_CLASS_ITYPE	= "SelectedPojoClassIype";

	public static final String	SELECTED_CLASS_ITYPE		= "SelectedClassIype";

	public static enum JOIN_TYPES {
		INNERJOIN("inner join"), OUTERJOIN("outer join"), LEFTJOIN("left join"), RIGHTJOIN("right join"), SELFJOIN("self join");
		private String	value;

		private JOIN_TYPES(final String value) {
			setValue(value);
		}

		public static JOIN_TYPES getJoinType(final String arg) {
			for (final JOIN_TYPES gs : JOIN_TYPES.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return null;

		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}

	public static final String	NAMED_QUERY_STR				= "NAMED_QUERY";

	public static final String	NAMED_QUERY_ANNOTATION_STR	= "NAMED_QUERY_ANNOTATION";

	public static final String	ASTERISK					= "*";

	public static enum FIRST_TEMPLATE {
		Class("class"), File("file"), Package("package"), Folder("folder"), Enumeration("enum"), None("none");

		private String	value;

		/**
		 *
		 * @param value
		 */
		private FIRST_TEMPLATE(final String value) {
			setValue(value);
		}

		/**
		 *
		 * @param arg
		 * @return
		 */
		public static FIRST_TEMPLATE getFirstTemplate(final String arg) {
			for (final FIRST_TEMPLATE gs : FIRST_TEMPLATE.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			if (!isEmpty(arg)) {
				return null;
			}
			return None;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(final String value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return this.value;
		}

	}

	public static final String	HQL_NAMED_QUERY_WITH_ANNOTATION_STR	= "NAMED_QUERY_WITH_ANNOTATION";

	public static enum ACTION_TYPE {
		Create("create"), Prompt("prompt"), Import("import"), Select("select"), Update("update");

		private String	value;

		/**
		 *
		 * @param value
		 */
		private ACTION_TYPE(final String value) {
			setValue(value);
		}

		/**
		 *
		 * @param arg
		 * @return
		 */
		public static ACTION_TYPE getActionType(final String arg) {
			for (final ACTION_TYPE gs : ACTION_TYPE.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return null;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(final String value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return this.value;
		}

	}

	public static enum ACTION_ENTITY {
		Class("class"), Field("field"), Interface("interface"), Snippet("snippet"), Method("method"), File("file"), Folder("folder"), Package(
				"package"), Import("import"), Xml("xml"), Message("message"), Exit("exit"), LocalVar("localVar"), Innerclass("innerclass"), Test(
				"test"), Project("project"), Classes("classes"), Files("files"), Property("property"), Info("info"), Read("read"), Write(
				"write"), Open("open");

		private String	value;

		/**
		 *
		 * @param value
		 */
		private ACTION_ENTITY(final String value) {
			setValue(value);
		}

		/**
		 *
		 * @param arg
		 * @return
		 */
		public static ACTION_ENTITY getEntity(final String arg) {
			for (final ACTION_ENTITY gs : ACTION_ENTITY.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return null;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(final String value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return this.value;
		}

	}

	public static final String	TEMPLATE_TYPE					= "TEMPLATE_TYPE";

	public static final String	PLACEHOLDER_PACKAGE				= "package";

	public static final String	PLACEHOLDER_PROJECT				= "project";

	public static final String	PLACEHOLDER_SRC_PATH			= "src_path";

	public static final String	PLACEHOLDER_SOURCE				= "source";

	public static final String	PLACEHOLDER_FIELD				= "field";

	public static final String	PLACEHOLDER_CLASS				= "class";

	public static final String	PLACEHOLDER_INTERFACE			= "interface";

	public static final String	PLACEHOLDER_STATIC_IMPORT		= "staticImport";

	public static final String	PLACEHOLDER_BASE_CLASS			= "baseClass";

	public static final String	PLACEHOLDER_IMPL_SUB_PACKAGE	= "implSubPckage";

	public static final String	PLACEHOLDER_ABSTRACT			= "abstract";

	public static final String	PLACEHOLDER_TITLE				= "title";

	public static final String	PLACEHOLDER_MESSAGE				= "message";

	public static final String	PLACEHOLDER_TARGET				= "target";

	public static final String	PLACEHOLDER_SNIPPET				= "snippet";

	public static final String	PLACEHOLDER_INPUT				= "input";

	public static final String	PLACEHOLDER_RESULT				= "result";

	public static final String	PLACEHOLDER_SUPER_INTERFACE		= "superInterface";

	public static final String	PLACEHOLDER_NAME				= "name";

	public static final String	PLACEHOLDER_ENUM				= "enum_constant";

	public static final String	PLACEHOLDER_JAVA_PROJECT		= "java_project";

	public static enum NUMBER_OF_JOIN_TABLES {
		TWO(2), THREE(3);
		private int	value;

		private NUMBER_OF_JOIN_TABLES(final int value) {
			setValue(value);
		}

		public static NUMBER_OF_JOIN_TABLES getNumberOfJoinTables(final String arg) {
			for (final NUMBER_OF_JOIN_TABLES gs : NUMBER_OF_JOIN_TABLES.values()) {
				if (!isEmpty(arg)) {
					return gs;
				}
			}
			return null;

		}

		public int getValue() {
			return this.value;
		}

		public void setValue(final int value) {
			this.value = value;
		}
	}

	public static final String	FIELD_ANNOTATION_TAG			= "fieldannotation";

	public static final String	CREATE_SIMILAR_CLASSES_ACTION	= "CREATE_SIMILAR_CLASSES";

	public static final String	ACTON_SELECTED_STR				= "ActionSelected";

	public static final String	BUILDER_CLASS_NAME				= "Builder";

	public static final String	BUILDER_TYPE_VARIBLE			= "builder";

	public static enum EMBEDDED_FIELDS_VIEW {
		FLAT_VIEW("flatView"), HIERARCHICAL_VIEW("hierarchicalView");
		private String	value;

		private EMBEDDED_FIELDS_VIEW(final String value) {
			setValue(value);
		}

		public static EMBEDDED_FIELDS_VIEW getEmbeddedFieldsView(final String arg) {
			for (final EMBEDDED_FIELDS_VIEW gs : EMBEDDED_FIELDS_VIEW.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return FLAT_VIEW;

		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}

	public static final String	CHECKED				= "checked";

	public static final String	UNCHECKED			= "unchecked";

	public static final String	DISABLED_CHECKBOX	= "disabled_checkbox";

	public static enum TemplateTag {
		IMPORT, METHOD, FIELD, FILES, FILE, XML, CLASSES, CLASS, MESSAGE, EXIT, LOCALVAR, FOLDER, PACKAGE, PROJECT, PROPERTY, SNIPPET, INFO
	}

	public static final String	TEMPLATE_TAG_PREFIX	= "fc";

	public static final String	EQUAL				= "=";

	public static final String	XML_START			= "<";

	public static final String	XML_END				= ">";

	public static final String	XML_COMPLETE_END	= "/>";

	public static final int		EQUAL_TO_COLUMN		= 0;

	public static final int		NOT_EQUAL_TO_COLUMN	= 1;

	public static final int		LESS_THAN_COLUMN	= 2;

	public static final int		GREATER_THAN_COLUMN	= 3;

	public static final int		NULLABLE_COLUMN		= 4;

	public static final int		NOT_NULLABLE_COLUMN	= 5;

	public static final int		IN_COLUMN			= 6;

	public static final int		BETWEEN_COLUMN		= 7;

	public static final int		LIKE_COLUMN			= 8;

	public static final int		NOT_LIKE_COLUMN		= 9;

	public static enum GETTER_SETTER_POSITION {
		GETTER_SETTER_PAIR("getterSEtterPair"), GETTER_FIRST_THEN_SETTER("getterfirst");

		private String	value;

		/**
		 *
		 * @param value
		 */
		private GETTER_SETTER_POSITION(final String value) {
			setValue(value);
		}

		/**
		 *
		 * @param arg
		 * @return
		 */
		public static GETTER_SETTER_POSITION getGetterSetterPosition(final String arg) {
			for (final GETTER_SETTER_POSITION gs : GETTER_SETTER_POSITION.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return null;
		}

		/**
		 * @param value
		 *            the value to set
		 */
		public void setValue(final String value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return this.value;
		}

	}

	public static enum RETURN_TYPES {
		STRING("String"), CHAR("char"), DOUBLE("double"), DBL_CLASS("Double"), INT("int"), INTEGER("Integer"), FLOAT("float"), FLOAT_CLASS(
				"Float"), BOOLEAN("boolean"), BOOLEAN_CLASS("Boolean"), BYTE("byte"), LONG("long"), SHORT("short"), CLASS("class"), FILE(
				"file"), FOLDER("folder"), PACKAGE("package"), PROJECT("project"), JAVAPROJECT("javaProject"), LOCALVAR("localvar"), INTERFACE(
				"interface"), ENUMERATION("enum"), INTRANGE("intRange");
		private String	value;

		private RETURN_TYPES(final String value) {
			setValue(value);
		}

		public static RETURN_TYPES getReturnType(final String arg) {
			for (final RETURN_TYPES gs : RETURN_TYPES.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return null;

		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}

	public static final String	CLASSES_SELECTED						= "classes";

	public static final String	FILES_SELECTED							= "files";

	public static final String	PACKAGE_STR								= "package";

	public static final String	DIR										= "dir";

	public static final String	NODE									= "node";

	public static final String	PARENT									= "parent";

	public static final String	TEMPLATES_FOLDER						= "templates";

	public static final String	DB_TEMPLATES_FOLDER						= "databaseTemplates";

	public static final String	FILE_TEMPLATES_FOLDER					= "fileTemplates";

	public static final String	UNIT_TEST_FOLDER						= "unitTest";

	public static final String	RULES_FOLDER							= "rules";

	public static final String	VARIABLE_FOLDER							= "variable";

	public static final String	TYPE									= "type";

	public static final String	P_AUTO_SAVE								= "AUTO SAVE";

	public static final char	HASH_CHAR								= '#';

	public static final char	ASTERISK_CHAR							= '*';

	public static final String	ASTERISK_WITH_SPACE						= " * ";

	public static final String	ASTERISK_WITH_SINGLE_SPACE				= "* ";

	public static final char	TAB_CHAR								= '\t';

	public static final String	MULTILINE_COMMENT_START_TAG				= "/*";

	public static final String	MULTILINE_COMMENT_END_TAG				= "*/";

	public static final String	VELOCITY_COMMENT_START_TAG				= "#*";

	public static final String	VELOCITY_COMMENT_END_TAG				= "*#";

	public static final char	FORWARD_SLASH_CHAR						= '/';

	public static final char	DOUBLE_SLASH_CHAR						= '\\';

	public static final char	DOUBLE_QUOTES_CHAR						= '\"';

	public static final char	SINGLE_QUOTES_CHAR						= '\'';

	public static final char	LEFT_BRACKET							= '[';

	public static final char	RIGHT_BRACKET							= ']';

	public static final char	EMPTY_CHAR								= ' ';

	public static final char	DOT_CHAR								= '.';

	public static final char	DOLLAR									= '$';

	public static final char	NOT_CHAR								= '!';

	public static final char	UNDER_SCORE								= '_';

	public static final char	COMMA_CHAR								= ',';

	public static final char	QUOTE_STR_CHAR							= '\"';

	public static final char	SINGLE_QUOTE							= '\'';

	public static final char	LEFT_CURL_CHAR							= '{';

	public static final char	RIGHT_CURL_CHAR							= '}';

	public static final char	LEFT_PAREN_CHAR							= '(';

	public static final char	RIGHT_PAREN_CHAR						= ')';

	public static final char	NEW_LINE_CHAR							= '\n';

	public static final char	LINEFEED_CHAR							= '\r';

	public static final String	ORACLE_ALL_SCHEMA_SELECT				= "select USERNAME from SYS.ALL_USERS order by USERNAME";

	public static final String	ORACLE_TABLES_SELECT_FROM_SCHEMA		= "select table_name from all_tables where owner='";

	public static final String	POSTGRESQL_TABLES_SELECT_FROM_SCHEMA	= "SELECT table_name FROM information_schema.tables WHERE table_schema = '";

	public static final String	POSTGRESQL_ALL_SCHEMA_SELECT			= "select schema_name from information_schema.schemata where schema_name <> 'information_schema' and schema_name !~ E'^pg_'";

	public static final String	MYSQL_ALL_DB_SELECT						= "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA where SCHEMA_NAME <> 'information_schema'";

	public enum Qualifier {
		EQUALTO("Equal To", "=", 1), NOTEQUALTO("Not Equal To", "!=", 1), LESSTHAN("Less Than", "<", 1), GREATERTHAN("Greater Than", ">", 1), NULLABLE(
				"Is Null", "IS NULL", 0), NOTNULLABLE("Is Not Null", "IS NOT NULL", 0), IN("In", "IN", 1), BETWEEN("Between", "BETWEEN", 2), LIKE(
				"Like", "LIKE", 1), NOTLIKE("Not  Like", "NOT LIKE", 1);

		private final String	label;
		private final String	sign;
		private final int		count;

		Qualifier(final String label, final String sign, final int requiredValuesCount) {
			this.count = requiredValuesCount;
			this.label = label;
			this.sign = sign;
		}

		public int getCount() {
			return this.count;
		}

		public String getLabel() {
			return this.label;
		}

		public String getsign() {
			return this.sign;
		}

	}

	public static enum CHECK_IN {
		CHECK_IN("checkIn"), ASK_BEFORE_CHECKIN("askBeforeCheckIn"), DONOT_CHECKIN("donotCheckIn");
		private String	value;

		private CHECK_IN(final String value) {
			setValue(value);
		}

		public static CHECK_IN getCheckIn(final String arg) {
			for (final CHECK_IN gs : CHECK_IN.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return DONOT_CHECKIN;

		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}

	public static String	FC_PLUGIN	= "Fast Code Eclipse Plugin";

	public static enum EXPORT_OPTIONS {
		OVERWRITE("overWrite"), BACKUP("backUp"), ASK_TO_OVERWRITE_OR_BACKUP("askToOverwriteOrBackup");
		private String	value;

		private EXPORT_OPTIONS(final String value) {
			setValue(value);
		}

		public static EXPORT_OPTIONS getExportOptions(final String arg) {
			for (final EXPORT_OPTIONS gs : EXPORT_OPTIONS.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.value)) {
					return gs;
				}
			}
			return ASK_TO_OVERWRITE_OR_BACKUP;

		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}

	}

	public static String		FILE_SEPARATOR				= System.getProperty("file.separator");

	public static String		FC_OBJ_CREATED				= "fc_obj_created";

	public static String		SCHEMA						= "schema";

	public static final String	COMMON_CLASS_SUFFIX			= "common_class_suffix";

	public static final String	CREATE_CLASS				= "create.class";

	public static final String	CREATE_FIELDS				= "create.class.field";

	public static final String	CREATE_METHODS				= "create.class.method";

	public static final String	CREATE_FILE					= "create.file";

	public static final String	CREATE_PACKAGE				= "create.package";

	public static final String	CREATE_LOCALVAR				= "create.localvariable";

	public static final String	CREATE_INNER_CLASS			= "create.innerclass";

	public static final String	CREATE_LOCALVARIABLE		= "create.localvariable";

	public static final String	PLACEHOLDER_FIELDS			= "fields";

	public static final String	PLACEHOLDER_METHODS			= "methods";

	public static final String	PLACEHOLDER_FILE			= "file";

	public static final String	PLACEHOLDER_INNERCLASSES	= "innerClasses";

	public static final String	PLACEHOLDER_LOCALVARS		= "localvars";

	public static final String	PLACEHOLDER_STUBMETHODS		= "stubMethods";

	public static final String	PLACEHOLDER_TESTMETHODS		= "testMethods";

	public static final String	CREATE_STUBMETHOD			= "create.class.stubmethod";

	public static final String	CREATE_TESTMETHOD			= "create.class.testmethod";

	public static enum REPOSITORY {
		SVN("svn"), CVS("cvs"), GIT("git"), PERFORCE("perforce"), MKS("mks");
		private String	value;

		private REPOSITORY(final String value) {
			setValue(value);
		}

		public static REPOSITORY getRepository(final String arg) {
			for (final REPOSITORY gs : REPOSITORY.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.name())) {
					return gs;
				}
			}
			return null;

		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}

	}

	/*public static enum CHECKIN_COMMENTS {
		fields("created field #foreach ($field in ${fields})${field.name} #if ($foreach.count < ${fields.size()}), #end #end."),
		Class("created ${class.name} in ${class.package.name}."),
		methods("created method #foreach ($method in ${methods})${method.name} #if ($foreach.count < ${methods.size()}), #end #end."),
		file("created file ${file.name}."),
		Package("created package ${package.name}."),
		innerclass("created innerclass ${innerClass.name}."),
		localvar("created local variable ${localvar.name}.");


		private CHECKIN_COMMENTS(final String value) {
			setValue(value);
		}

		public static CHECKIN_COMMENTS getCheckinComments(final String arg) {
			for (final CHECKIN_COMMENTS gs : CHECKIN_COMMENTS.values()) {
				if (!isEmpty(arg) && arg.equalsIgnoreCase(gs.name())) {
					return gs;
				}
			}
			return null;

		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}

	}*/

	public static final String	MYSQL_CONNECTION_ERROR_SQLSTATE			= "08S01";

	public static final String	ORACLE_CONNECTION_ERROR_SQLSTATE1		= "61000";

	public static final String	ORACLE_CONNECTION_ERROR_SQLSTATE2		= "66000";

	public static final String	POSTGRESQL_CONNECTION_ERROR_SQLSTATE	= "08001";

	public static final String	JAVA_TABLE_NAME							= "javaTableName";

	public static final String	PLACEHOLDER_MODIFY_FIELD				= "modifyField";

	public static final String	MODIFY_FIELD							= "modify.field";

	public static final String	SUPER									= "super";

	public static final String	INITIATED								= "initiated";

	public static final String	COMPLETED								= "completed";

	public static final String	CURRENT_PACKAGE							= "Current Package";

	public static final String	CURRENT_FOLDER							= "Current Folder";

	public static final String	CURRENT_CLASS							= "Current Class";

	public static final String	CURRENT_FILE							= "Current File";

	public static final String	FILE_DETETE								= "deleted";

	public static final String	FILE_CREATED							= "Created file";

	public static final String	LOCAL_HOST								= "localhost";

	public static final String	PLACEHOLDER_FOLDER						= "folder";

	public static final String	CREATE_FOLDER							= "create.folder";

	public static final char	COLON_CHAR								= ':';

	public static final String	PROJECT_URL_DELIMITER					= "project_url_delimiter";

	public static final String	PROJECT_URL_PAIR_DELIMITER				= "project_url_pair_delimiter";

	public static final String	OPTIONAL								= "optional";

	public static final String	DELIMITER								= "delimiter";

	public static final String	NAMES									= "names";

	public static final String	DIR_TO_SKIP								= "dir_to_skip";

	public static final String	STRING_CONSTANT							= "String";

	public static final String	ATTRIBUTE_VALUE							= "value";

	public static final String	ATTRIBUTE_REQUIRED						= "required";

	public static final String	ATTRIBUTE_PATTERN						= "pattern";

	public static final String	ATTRIBUTE_ALLOWED_VALUES				= "allowed_values";

	public static final String	ADD_FILE								= "adding.file";

	public static final String	MODIFIED_FILE							= "modified.file";

	public static final String	ATTRIBUTE_LABEL							= "label";

	public static final String	STARTLINE_ENDLINE						= "startLine-endLine";

	public static final String	ENCLOSING_PACKAGE_STR					= "enclosing_package";

	public static final String	ENCLOSING_FOLDER_STR					= "enclosing-folder";

	public static final String	ENCLOSING_PROJECT_STR					= "enclosing-project";

	public static final String	ENCLOSING_INTERFACE_STR					= "enclosing_interface";

	public static enum FIELDS {
		ADDITIONAL_PARAMETER, TEMPLATE_BODY

	}

	public static final String	DATABASE_NAME			= "databaseName";

	public static final String	ATTRIBUTE_ENABLED		= "enabled";

	public static final String	ORACLE_DEFAULT_DATE		= "sysdate";

	public static final String	MYSQL_DEFAULT_DATE		= "sysdate";

	public static final String	POSTGRE_DEFAULT_DATE	= "sysdate";

	public static final String	SELECTED_TEXT			= "selected_text";

	public static final String	DEFAULT_SUFFIX			= " (default)";

	public static final String	NUMBER_OF_RECORDS		= "select count(*) from ";

	public static final String	AUTO_CHECKIN			= "auto_checkin";

	public static final String	DB_FOREACH_VARS			= "db_foreach_vars";

	public static final String	ORACLE_DRIVER			= "ojdbc";

	public static final String	FC_TAG_START			= "<fc:";

	public static enum TARGET {
		target("target"), file("file"), clas("class"), packag("package"), folder("folder");

		private String	value;

		private TARGET(final String value) {
			setValue(value);
		}

		public String getValue() {
			return this.value;
		}

		public void setValue(final String value) {
			this.value = value;
		}
	}

	public static final String	ATTRIBUTE_MIN			= "min";

	public static final String	ATTRIBUTE_MAX			= "max";

	public static final String	ZERO_STRING				= "0";

	public static final String	ATTRIBUTE_TYPE			= "type";

	public static final String	ATTRIBUTE_DEPENDSON		= "dependsOn";

	public static final String	VALID_VARIABLES			= "valid_variables";

	public static final String	LOCAL_VARIABLES			= "local_variables";

	public static final String	SET_VARIABLES			= "set_variables";

	public static final String	RETURN_VALUE			= "return_value";

	public static final String	FOR_LOOP_LOCAL_VAR		= "for_loop_local_var";

	public static final String	FC_LOCAL_VAL_LIST		= "fc_local_val_list";

	public static final String	VALID_VARIABLE_LIST		= "valid_variableslist";

	public static final String	INVALID_ATTRIBUTES_LIST	= "invalid_attributes_list";
}
