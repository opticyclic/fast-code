/**
 *
 */
package org.fastcode.util;

import static org.fastcode.util.FastCodeUtil.closeInputStream;

import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.fastcode.Activator;

/**
 * @author Gautam
 *
 */
public class FileUtil {
	/**
	 *
	 * @return
	 * @throws Exception
	 */
	public static String getContentsFromFile(final IFile file) throws Exception {
		final InputStream inputStream = file.getContents();

		try {
			final byte[] bytes = new byte[inputStream.available()];
			inputStream.read(bytes);
			return new String(bytes);
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex);
		} finally {
			closeInputStream(inputStream);
		}
	}

	/**
	 * @param propertyFile
	 * @return
	 * @throws Exception
	 *
	 */
	public static Properties retrievePropertiesFromFile(final String propertyFile) throws Exception {
		InputStream input = null;

		final Properties properties = new Properties();
		try {
			input = FileLocator.openStream(Activator.getDefault().getBundle(), new Path(propertyFile), false);
			properties.load(input);
		} catch (final Exception ex) {
			ex.printStackTrace();
			throw new Exception(ex.getMessage(), ex);
		} finally {
			closeInputStream(input);
		}
		return properties;
	}

}
