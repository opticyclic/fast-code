package org.fastcode.util;

import static org.fastcode.common.FastCodeConstants.ASTERISK;
import static org.fastcode.common.FastCodeConstants.EMPTY_STR;
import static org.fastcode.util.StringUtil.isEmpty;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalProvider;

public class FastCodeContentProposalProvider implements IContentProposalProvider {
	protected String[]	proposals	= null;
	private String		comboName	= EMPTY_STR;

	@Override
	public IContentProposal[] getProposals(final String contents, final int arg1) {
		final List contentProposals = getMatchingProposals(this.proposals, contents);
		return (IContentProposal[]) contentProposals.toArray(new IContentProposal[contentProposals.size()]);
	}

	public FastCodeContentProposalProvider(final String[] proposals) {
		super();
		this.proposals = proposals;
	}

	public FastCodeContentProposalProvider(final String[] proposals, final String comboName) {
		super();
		this.proposals = proposals;
		this.comboName = comboName;
	}

	protected List<IContentProposal> getMatchingProposals(final String[] proposals, String contents) {
		final List<IContentProposal> contentProposals = new ArrayList<IContentProposal>();
		if (!isEmpty(this.comboName) & this.comboName.equals("snippet")) {
			contents = ASTERISK + contents + ASTERISK;
		}
		for (int i = 0; i < proposals.length; i++) {
			int j = 0;
			final String proposal = proposals[i];
			if (contents.contains(ASTERISK)) {
				if (contents.length() == 1) {
					addToList(contentProposals, proposal);
				} else {
					if (!contents.equals(ASTERISK + ASTERISK)) {
						final String[] filtertext = contents.toUpperCase().split("\\*");
						final String start = filtertext[0];
						String end = EMPTY_STR;
						boolean toBeAdded = true;
						if (filtertext.length > 1) {
							end = contents.endsWith(ASTERISK) ? EMPTY_STR : filtertext[filtertext.length - 1];
						}
						for (int k = 1; k < filtertext.length; k++) {
							if (!proposal.toUpperCase().contains(filtertext[k])) {
								toBeAdded = false;
							}
						}
						if (proposal.toUpperCase().startsWith(start.toUpperCase()) && proposal.toUpperCase().endsWith(end.toUpperCase())
								&& toBeAdded) {
							addToList(contentProposals, proposal);
						}
					}
				}
			} else if (proposal.toUpperCase().startsWith(contents.toUpperCase())) {

				addToList(contentProposals, proposal);
			}
			j++;

		}
		return contentProposals;
	}

	private void addToList(final List<IContentProposal> contentProposals, final String proposal) {
		contentProposals.add(new IContentProposal() {
			@Override
			public String getContent() {
				return proposal;
			}

			@Override
			public String getDescription() {
				return null;
			}

			@Override
			public String getLabel() {
				return null;
			}

			@Override
			public int getCursorPosition() {
				return proposal.length();
			}
		});
	}
}
