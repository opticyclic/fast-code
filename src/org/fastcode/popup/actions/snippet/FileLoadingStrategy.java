/**
 *
 */
package org.fastcode.popup.actions.snippet;

import java.io.InputStream;

/**
 * @author Gautam
 *
 */
public interface FileLoadingStrategy {

	/**
	 * @param inputStream
	 * @param configuration
	 * @return
	 * @throws Exception
	 */
	public Object[] loadFileElementsFromInputStream(final InputStream inputStream, final Object configuration) throws Exception;

}
