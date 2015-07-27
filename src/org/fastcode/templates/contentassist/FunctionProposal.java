package org.fastcode.templates.contentassist;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Function proposal class.
 */
public class FunctionProposal extends ElementProposal {
	private final String				regex;
	private final Set<ElementProposal>	predecessors	= new LinkedHashSet<ElementProposal>();

	/**
	 * Instantiates a new function proposal.
	 *
	 * @param proposal the proposal
	 * @param displayString the display string
	 * @param description the description
	 * @param selectionOffset the selection offset
	 */
	public FunctionProposal(final String proposal, final String displayString, final String description, final int selectionOffset) {
		this(proposal, displayString, description, null, selectionOffset, 0, false);
	}

	/**
	 * Instantiates a new function proposal.
	 *
	 * @param proposal the proposal
	 * @param displayString the display string
	 * @param description the description
	 * @param selectionOffset the selection offset
	 * @param multiple the multiple
	 */
	public FunctionProposal(final String proposal, final String displayString, final String description, final int selectionOffset,
			final boolean multiple) {
		this(proposal, displayString, description, null, selectionOffset, 0, multiple);
	}

	/**
	 * Instantiates a new function proposal.
	 *
	 * @param proposal the proposal
	 * @param displayString the display string
	 * @param description the description
	 * @param regex the regex
	 * @param selectionOffset the selection offset
	 * @param selectionLength the selection length
	 * @param multiple the multiple
	 */
	public FunctionProposal(final String proposal, final String displayString, final String description, final String regex,
			final int selectionOffset, final int selectionLength, final boolean multiple) {
		super(proposal, displayString, description, selectionOffset, selectionLength, multiple);
		this.regex = regex;
	}

	/**
	 * Adds a predecessor.
	 *
	 * @param predecessor a predecessor
	 */
	public void addPredecessor(final ElementProposal predecessor) {
		this.predecessors.add(predecessor);
	}

	/**
	 * Checks if the given element is a valid predecessor.
	 *
	 * @param predecessor the predecessor
	 *
	 * @return true, if valid predecessor
	 */
	public boolean isPredecessor(final ElementProposal predecessor) {
		return this.predecessors.contains(predecessor);
	}

	@Override
	public boolean matches(final String token) {
		if (this.regex == null) {
			return super.matches(token);
		} else {
			return token.matches(this.regex);
		}
	}
}
