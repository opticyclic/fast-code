package org.fastcode.templates.contentassist;



/**
 * Element proposal class.
 */
public class ElementProposal implements Comparable<ElementProposal> {
	protected String proposal;
	protected String displayPrefix;
	protected String description;
	protected int selectionOffset;
	protected int selectionLength;
	protected boolean multiple;


	/**
	 * Instantiates a new element proposal.
	 *
	 * @param proposal the proposal
	 * @param displayPrefix the display prefix
	 * @param description the description
	 */
	public ElementProposal(final String proposal, final String displayPrefix, final String description) {
		this(proposal, displayPrefix, description, 0, 0, false);
	}

	/**
	 * Instantiates a new element proposal.
	 *
	 * @param proposal the proposal
	 * @param displayPrefix the display prefix
	 * @param description the description
	 * @param selectionOffset the selection offset
	 */
	public ElementProposal(final String proposal, final String displayPrefix, final String description,
			final int selectionOffset) {
		this(proposal, displayPrefix, description, selectionOffset, 0, false);
	}

	/**
	 * Instantiates a new element proposal.
	 *
	 * @param proposal the proposal
	 * @param displayPrefix the display prefix
	 * @param description the description
	 * @param selectionOffset the selection offset
	 * @param selectionLength the selection length
	 * @param multiple the multiple
	 */
	public ElementProposal(final String proposal, final String displayPrefix, final String description,
			final int selectionOffset, final int selectionLength, final boolean multiple) {
		this.proposal = proposal;
		this.displayPrefix = displayPrefix;
		this.description = description;
		this.selectionOffset = selectionOffset;
		this.selectionLength = selectionLength;
		this.multiple = multiple;
	}

	/**
	 * Gets the proposal.
	 *
	 * @return the proposal
	 */
	public String getProposal() {
		return this.proposal;
	}

	/**
	 * Gets the display string.
	 *
	 * @return the display string
	 */
	public String getDisplayString() {
		return this.displayPrefix + this.description;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Gets the selection offset.
	 *
	 * @return the selection offset
	 */
	public int getSelectionOffset() {
		return this.selectionOffset;
	}

	/**
	 * Gets the selection length.
	 *
	 * @return the selection length
	 */
	public int getSelectionLength() {
		return this.selectionLength;
	}

	/**
	 * Checks if multiple occurence is allowed.
	 *
	 * @return the boolean
	 */
	public boolean isMultiple() {
		return this.multiple;
	}

	/**
	 * Checks if the proposal matches the given token.
	 *
	 * @param token the token
	 *
	 * @return true, if proposal matches the given token
	 */
	public boolean matches(final String token) {
		return this.proposal.equals(token);
	}





	@Override
	public boolean equals(final Object elementProposal) {
		// TODO Auto-generated method stub
		if(this.proposal.equals(((ElementProposal) elementProposal).getProposal())) {
			return true;
		} else {
			return false;
		}
	}


	@Override
	public int compareTo(final ElementProposal elementProposal ) {
		if( this.proposal.equals(elementProposal.getProposal())){
			return 0;
		}
		return 1;
	}


	@Override
    public int hashCode() {
        return this.proposal.hashCode();
    }



}
