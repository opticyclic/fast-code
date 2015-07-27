package org.fastcode.preferences;

/**
 * Constants for this Plugin.
 */
public interface Constants {

	public static final String	LINE_SEPARATOR					= System.getProperty("line.separator");

	public static final String	NON_JAVADOC_TAG					= "(non-Javadoc)";
	public static final String	INHERIT_DOC_TAG					= "{@inheritDoc}";

	public static final String	ID_PROPERTY_PAGE				= "org.fastcode.properties.MainPreferencePage";
	public static final String	ID_PREFERENCE_PAGE				= "org.fastcode.preferences.MainPreferencePage";

	// --------------------------------
	// keys for preferences store
	// --------------------------------

	public static final String	MODE							= "mode";
	public static final String	MODE_COMPLETE					= "mode_complete";
	public static final String	MODE_KEEP						= "mode_keep";
	public static final String	MODE_REPLACE					= "mode_replace";

	public static final String	VISIBILITY_PUBLIC				= "visibility_public";
	public static final String	VISIBILITY_PROTECTED			= "visibility_protected";
	public static final String	VISIBILITY_PACKAGE				= "visibility_package";
	public static final String	VISIBILITY_PRIVATE				= "visibility_private";

	public static final String	FILTER_TYPES					= "filter_types";
	public static final String	FILTER_FIELDS					= "filter_fields";
	public static final String	FILTER_METHODS					= "filter_methods";
	public static final String	FILTER_GETSET					= "filter_getset";
	public static final String	FILTER_EXCLGETSET				= "filter_exclgetset";

	public static final String	ADD_TODO						= "add_todo";
	public static final String	CREATE_DUMMY_DOC				= "create_dummy_doc";
	public static final String	REPLACEMENTS					= "replacements";
	public static final String	FIELDS							= "fields";
	public static final String	SINGLE_LINE						= "single_line_comment";
	public static final String	USE_FORMATTER					= "use_internal_formatter";
	public static final String	GET_SET_FROM_FIELD				= "get_set_from_field";
	public static final String	INCLUDE_SUBPACKAGES				= "include_subpackages";

	public static final String	ADD_HEADER						= "add_header";
	public static final String	REPLACE_HEADER					= "replace_header";
	public static final String	MULTI_HEADER					= "multi_header";
	public static final String	HEADER_TEXT						= "header_text";
	public static final String	USE_PKG_INFO					= "use_pkg_info";
	public static final String	PKG_DOC_TEXT					= "package_doc_text";
	public static final String	PKG_INFO_TEXT					= "package_info_text";
	public static final String	PROPERTIES						= "properties";

	public static final String	PROJECT_SPECIFIC				= "project_specific_settings";

	// --------------------------------
	// default values
	// --------------------------------

	public static final String	DEFAULT_MODE					= MODE_COMPLETE;

	public static final boolean	DEFAULT_VISIBILITY_PUBLIC		= true;
	public static final boolean	DEFAULT_VISIBILITY_PROTECTED	= true;
	public static final boolean	DEFAULT_VISIBILITY_PACKAGE		= true;
	public static final boolean	DEFAULT_VISIBILITY_PRIVATE		= true;

	public static final boolean	DEFAULT_FILTER_TYPES			= true;
	public static final boolean	DEFAULT_FILTER_FIELDS			= true;
	public static final boolean	DEFAULT_FILTER_METHODS			= true;
	public static final boolean	DEFAULT_FILTER_GETSET			= false;
	public static final boolean	DEFAULT_FILTER_EXCLGETSET		= false;

	public static final boolean	DEFAULT_CREATE_DUMMY_DOC		= true;
	public static final boolean	DEFAULT_ADD_TODO				= true;
	public static final boolean	DEFAULT_SINGLE_LINE				= true;
	public static final boolean	DEFAULT_USE_FORMATTER			= false;
	public static final boolean	DEFAULT_GET_SET_FROM_FIELD		= false;
	public static final boolean	DEFAULT_INCLUDE_SUBPACKAGES		= false;

	public static final boolean	DEFAULT_ADD_HEADER				= false;
	public static final boolean	DEFAULT_REPLACE_HEADER			= false;
	public static final boolean	DEFAULT_MULTI_HEADER			= false;
	public static final boolean	DEFAULT_USE_PKG_INFO			= false;

	public static final boolean	DEFAULT_PROJECT_SPECIFIC		= false;

	public static final String	DEFAULT_HEADER_TEXT				= "/*" + LINE_SEPARATOR + " * " + LINE_SEPARATOR + " */";

	public static final String	DEFAULT_PKG_DOC_TEXT			= "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">"
																		+ LINE_SEPARATOR + "<html>" + LINE_SEPARATOR + "<head></head>"
																		+ LINE_SEPARATOR + "<body>" + LINE_SEPARATOR + "  Provides..."
																		+ LINE_SEPARATOR + "</body>" + LINE_SEPARATOR + "</html>";

	public static final String	DEFAULT_PKG_INFO_TEXT			= "/**" + LINE_SEPARATOR + " * Provides..." + LINE_SEPARATOR + " */"
																		+ LINE_SEPARATOR + "package ${package_name};";

	public static final String	EMPTY_JAVADOC					= "/**" + Constants.LINE_SEPARATOR + " * " + Constants.LINE_SEPARATOR
																		+ " */";

	public static final String	EMPTY_PARAMDOC					= "/**" + Constants.LINE_SEPARATOR + " * @param ${e} "
																		+ Constants.LINE_SEPARATOR + " */";

	public static final String	EMPTY_THROWSDOC					= "/**" + Constants.LINE_SEPARATOR + " * @throws ${e} "
																		+ Constants.LINE_SEPARATOR + " */";

	// --------------------------------
	// todo string
	// --------------------------------

}
