package org.fastcode.templates.rules;

/**
 * Definition of Velocity template partitioning and its partitions.
 */
public interface FastCodeTemplatePartitions {

	/**
	 * Identifier of the Velocity template partitioning.
	 */
	public static final String	TEMPLATE_PARTITIONING	= "template_partitioning";	//$NON-NLS-1$

	/**
	 * Identifier of the Velocity template single line comment partition.
	 */
	public static final String	SINGLE_LINE_COMMENT		= "singleline_comment";	//$NON-NLS-1$

	public static final String	FC_METHOD		= "<fc:method";	 //$NON-NLS-1$
	public static final String	FC_FIELD		= "<fc:field";	 //$NON-NLS-1$
	public static final String	FC_CLASS		= "<fc:class";	 //$NON-NLS-1$
	public static final String	FC_FILE		    = "<fc:file";	 //$NON-NLS-1$
	public static final String	FC_PACKAGE		= "<fc:package"; //$NON-NLS-1$
	public static final String	FC_FOLDER		= "<fc:folder";  //$NON-NLS-1$
	public static final String	FC_PROJECT		= "<fc:project"; //$NON-NLS-1$
	public static final String	FC_MESSAGE		= "<fc:message"; //$NON-NLS-1$
	public static final String	FC_EXIT			= "<fc:exit";    //$NON-NLS-1$
	public static final String	FC_IMPORT		= "<fc:import";  //$NON-NLS-1$
	public static final String	FC_XML			= "<fc:xml";     //$NON-NLS-1$
	public static final String	FC_CLASSES		= "<fc:classes"; //$NON-NLS-1$
	public static final String	FC_FILES		= "<fc:files";   //$NON-NLS-1$
	public static final String	FC_PROPERTY		= "<fc:property";//$NON-NLS-1$
	public static final String	FC_INFO			= "<fc:info";    //$NON-NLS-1$
	public static final String	FC_SNIPPET		= "<fc:snippet"; //$NON-NLS-1$

	/**
	 * Identifier of the Velocity template multi line comment partition.
	 */
	public static final String	MULTI_LINE_COMMENT		= "multiline_comment";

}
