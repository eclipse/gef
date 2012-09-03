package org.eclipse.gef4.graphics.tests.swt;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This class bundles all GEF 4 Graphics component test classes to be able to
 * run them at once.
 * 
 * @author mwienand
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ SWTBlitPropertiesTests.class, SWTCanvasPropertiesTests.class,
		SWTDrawPropertiesTests.class })
public class AllSWTTests {
}