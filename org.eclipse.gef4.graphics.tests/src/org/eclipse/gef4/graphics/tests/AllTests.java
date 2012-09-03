/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
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
