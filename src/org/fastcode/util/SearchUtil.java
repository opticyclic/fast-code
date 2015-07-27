package org.fastcode.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

public class SearchUtil {

	/**
	 *
	 * @param searchJavaElement
	 * @param type
	 * @param scope
	 * @return
	 */
	public List<IJavaElement> search(final IJavaElement searchJavaElement, final int type, final IJavaSearchScope scope) {
		final SearchPattern pattern = SearchPattern.createPattern(searchJavaElement, IJavaSearchConstants.REFERENCES);
		final SearchParticipant[] participants = { SearchEngine.getDefaultSearchParticipant() };
		final List<IJavaElement> result = new ArrayList<IJavaElement>();

		final SearchRequestor collector = new SearchRequestor() {

			@Override
			public void acceptSearchMatch(final SearchMatch match) throws CoreException {
				result.add((IJavaElement) match.getElement());
			}
		};
		try {
			new SearchEngine().search(pattern, participants, scope, collector, null);
		} catch (final CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 *
	 * @param searchString
	 * @param type
	 * @param scope
	 * @return
	 */
	public List<IType> searchForTestClass(final String searchString, final int type, final IJavaSearchScope scope) {
		final SearchPattern pattern = SearchPattern.createPattern(searchString, type, IJavaSearchConstants.DECLARATIONS,
				IJavaSearchConstants.EXACT_MATCH);
		final SearchParticipant[] participants = { SearchEngine.getDefaultSearchParticipant() };

		final List<IType> result = new ArrayList<IType>();

		final SearchRequestor collector = new SearchRequestor() {

			@Override
			public void acceptSearchMatch(final SearchMatch match) throws CoreException {
				result.add((IType) match.getElement());

			}
		};
		try {
			new SearchEngine().search(pattern, participants, scope, collector, null);
		} catch (final CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	/**
	 *
	 * @param searchString
	 * @param type
	 * @param scope
	 * @return
	 */
	public IType searchForItype(final String searchString, final int type, final IJavaSearchScope scope) {
		final SearchPattern pattern = SearchPattern.createPattern(searchString, type, IJavaSearchConstants.DECLARATIONS,
				IJavaSearchConstants.EXACT_MATCH);
		final SearchParticipant[] participants = { SearchEngine.getDefaultSearchParticipant() };

		final List<IType> result = new ArrayList<IType>();

		final SearchRequestor collector = new SearchRequestor() {

			@Override
			public void acceptSearchMatch(final SearchMatch match) throws CoreException {
				result.add((IType) match.getElement());

			}
		};
		try {
			new SearchEngine().search(pattern, participants, scope, collector, null);
		} catch (final CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result.isEmpty() ? null :result.get(0);

	}
}
