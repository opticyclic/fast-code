package org.fastcode.templates.rules;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * Partition scanner for templates.
 *
 */
public class FastCodeTemplatePartitionScanner extends RuleBasedPartitionScanner {

	public FastCodeTemplatePartitionScanner() {
		final IToken multiLineComment = new Token(FastCodeTemplatePartitions.MULTI_LINE_COMMENT);
		final IToken singleLineComment = new Token(FastCodeTemplatePartitions.SINGLE_LINE_COMMENT);
		final IToken fcMethod = new Token(FastCodeTemplatePartitions.FC_METHOD);
		final IToken fcField = new Token(FastCodeTemplatePartitions.FC_FIELD);
		final IToken fcClass= new Token(FastCodeTemplatePartitions.FC_CLASS);
		final IToken fcFile= new Token(FastCodeTemplatePartitions.FC_FILE);
		final IToken fcPackage= new Token(FastCodeTemplatePartitions.FC_PACKAGE);
		final IToken fcFolder= new Token(FastCodeTemplatePartitions.FC_FOLDER);
		final IToken fcProject= new Token(FastCodeTemplatePartitions.FC_PROJECT);
		final IToken fcMessage= new Token(FastCodeTemplatePartitions.FC_MESSAGE);
		final IToken fcExit= new Token(FastCodeTemplatePartitions.FC_EXIT);
		final IToken fcImport= new Token(FastCodeTemplatePartitions.FC_IMPORT);
		final IToken fcXml= new Token(FastCodeTemplatePartitions.FC_XML);
		final IToken fcClasses= new Token(FastCodeTemplatePartitions.FC_CLASSES);
		final IToken fcFiles= new Token(FastCodeTemplatePartitions.FC_FILES);
		final IToken fcProperty= new Token(FastCodeTemplatePartitions.FC_PROPERTY);
		final IToken fcInfo= new Token(FastCodeTemplatePartitions.FC_INFO);
		final IToken fcSnippet= new Token(FastCodeTemplatePartitions.FC_SNIPPET);

		final List<IRule> rules = new ArrayList<IRule>();

		// Add rule for single line comments
		rules.add(new EndOfLineRule("##", singleLineComment));

		/*// Add rules for empty multi-line comments
		rules.add(new EmptyCommentRule(multiLineComment));
*/
		// Add rules for multi-line comments
		rules.add(new MultiLineRule("#*", "*#", multiLineComment));

		rules.add(new MultiLineRule("<fc:method ", "</fc:method>", fcMethod));
		rules.add(new MultiLineRule("<fc:field ", "</fc:field>", fcField));
		rules.add(new MultiLineRule("<fc:class ", "</fc:class>", fcClass));
		rules.add(new MultiLineRule("<fc:file ", "</fc:file>", fcFile));
		rules.add(new MultiLineRule("<fc:package ", "</fc:package>", fcPackage));
		rules.add(new MultiLineRule("<fc:folder ", "</fc:folder>", fcFolder));
		rules.add(new MultiLineRule("<fc:project ", "</fc:project>", fcProject));
		rules.add(new MultiLineRule("<fc:message ", "</fc:message>", fcMessage));
		rules.add(new MultiLineRule("<fc:exit ", "</fc:exit>", fcExit));
		rules.add(new MultiLineRule("<fc:import ", "</fc:import", fcImport));
		rules.add(new MultiLineRule("<fc:xml ", "</fc:xml>", fcXml));
		rules.add(new MultiLineRule("<fc:classes ", "</fc:classes>", fcClasses));
		rules.add(new MultiLineRule("<fc:files ", "</fc:files>", fcFiles));
		rules.add(new MultiLineRule("<fc:property ", "</fc:property>", fcProperty));
		rules.add(new MultiLineRule("<fc:info ", "</fc:info>", fcInfo));
		rules.add(new MultiLineRule("<fc:snippet ", "</fc:snippet>", fcSnippet));

		final IPredicateRule[] result = new IPredicateRule[rules.size()];
		final IPredicateRule[] array = rules.toArray(result);
		setPredicateRules(result);
	}
}
