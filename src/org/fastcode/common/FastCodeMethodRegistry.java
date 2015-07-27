/**
 *
 */
package org.fastcode.common;

import static org.fastcode.util.FastCodeUtil.getFCAnnotationList;

import java.util.ArrayList;
import java.util.List;

import org.fastcode.common.FastCodeConstants.JUNIT_TYPE;

/**
 * @author Gautam
 *
 */
public class FastCodeMethodRegistry {

	public static FastCodeMethod[] getRegisteredUnitTestStubMethods(final JUNIT_TYPE type) {
		if (JUNIT_TYPE.JUNIT_TYPE_3.equals(type)) {
			final FastCodeMethod[] fastCodeMethods = new FastCodeMethod[3];
			final List<FastCodeAnnotation> annotations = new ArrayList<FastCodeAnnotation>();
			annotations.add(new FastCodeAnnotation(null, new FastCodeType("org.junit.Override")));
			final List<FastCodeType> exception = new ArrayList<FastCodeType>();
			exception.add(new FastCodeType("java.lang.Exception"));

			fastCodeMethods[0] = new FastCodeMethod("setUp", null, exception, false, annotations, null, null);
			fastCodeMethods[1] = new FastCodeMethod("tearDown", null, exception, false, annotations, null, null);
			fastCodeMethods[2] = new FastCodeMethod("Constructor", null, null, false, null, null, null);
			return fastCodeMethods;
		}
		if (JUNIT_TYPE.JUNIT_TYPE_4.equals(type)) {
			final FastCodeMethod[] fastCodeMethods = new FastCodeMethod[4];
			/*final List<FastCodeAnnotation> annotations = new ArrayList<FastCodeAnnotation>();
			annotations.add(new FastCodeAnnotation(null, new FastCodeType("org.junit.BeforeClass")));*/
			final List<FastCodeType> exception = new ArrayList<FastCodeType>();
			exception.add(new FastCodeType("java.lang.Exception"));

			fastCodeMethods[0] = new FastCodeMethod("setUpBeforeClass", null, exception, true,
					getFCAnnotationList("org.junit.BeforeClass"), null, null);
			/*final List<FastCodeAnnotation> annotations1 = new ArrayList<FastCodeAnnotation>();
			annotations1.add(new FastCodeAnnotation(null, new FastCodeType("org.junit.AfterClass")));*/
			fastCodeMethods[1] = new FastCodeMethod("tearDownAfterClass", null, exception, true,
					getFCAnnotationList("org.junit.AfterClass"), null, null);
			/*final List<FastCodeAnnotation> annotations2 = new ArrayList<FastCodeAnnotation>();
			annotations2.add(new FastCodeAnnotation(null, new FastCodeType("org.junit.Before")));*/
			fastCodeMethods[2] = new FastCodeMethod("setUp", null, exception, false, getFCAnnotationList("org.junit.Before"), null, null);
			/*final List<FastCodeAnnotation> annotations3 = new ArrayList<FastCodeAnnotation>();
			annotations3.add(new FastCodeAnnotation(null, new FastCodeType("org.junit.After")));*/
			fastCodeMethods[3] = new FastCodeMethod("tearDown", null, exception, false, getFCAnnotationList("org.junit.After"), null, null);
			return fastCodeMethods;
		}
		if (JUNIT_TYPE.JUNIT_TYPE_TESTNG.equals(type)) {
			final FastCodeMethod[] fastCodeMethods = new FastCodeMethod[4];
			/*final List<FastCodeAnnotation> annotations = new ArrayList<FastCodeAnnotation>();
			annotations.add(new FastCodeAnnotation(null, new FastCodeType("org.junit.BeforeClass")));*/
			fastCodeMethods[0] = new FastCodeMethod("setUpBeforeClass", null, null, false, getFCAnnotationList("org.junit.BeforeClass"),
					null, null);
			/*final List<FastCodeAnnotation> annotations1 = new ArrayList<FastCodeAnnotation>();
			annotations1.add(new FastCodeAnnotation(null, new FastCodeType("org.junit.AfterClass")));*/
			fastCodeMethods[1] = new FastCodeMethod("tearDownAfterClass", null, null, false, getFCAnnotationList("org.junit.AfterClass"),
					null, null);
			/*	final List<FastCodeAnnotation> annotations2 = new ArrayList<FastCodeAnnotation>();
				annotations2.add(new FastCodeAnnotation(null, new FastCodeType("org.junit.BeforeMethod")));*/
			fastCodeMethods[2] = new FastCodeMethod("setUpBeforeMethod", null, null, false, getFCAnnotationList("org.junit.BeforeMethod"),
					null, null);
			/*final List<FastCodeAnnotation> annotations3 = new ArrayList<FastCodeAnnotation>();
			annotations3.add(new FastCodeAnnotation(null, new FastCodeType("org.junit.AfterMethod")));*/
			fastCodeMethods[3] = new FastCodeMethod("tearDownAfterMethod", null, null, false, getFCAnnotationList("org.junit.AfterMethod"),
					null, null);
			return fastCodeMethods;
		}
		return new FastCodeMethod[0];
	}
}
