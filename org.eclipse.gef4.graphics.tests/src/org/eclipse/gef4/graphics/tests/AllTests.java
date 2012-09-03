package org.eclipse.gef4.graphics.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This class bundles the main test classes for the individual IGraphics
 * implementations.
 * 
 * @author mwienand
 * 
 */
@RunWith(Suite.class)
@SuiteClasses({ org.eclipse.gef4.graphics.tests.swt.AllTests.class,
		org.eclipse.gef4.graphics.tests.awt.AllTests.class })
public class AllTests {

}
