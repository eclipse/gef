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
@SuiteClasses({ org.eclipse.gef4.graphics.tests.swt.AllSWTTests.class,
		org.eclipse.gef4.graphics.tests.awt.AllAWTTests.class })
public class AllTests {

}
