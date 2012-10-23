/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot;

import org.eclipse.gef4.zest.tests.dot.DotTestUtils;
import org.eclipse.gef4.zest.tests.dot.TestDotExport;
import org.eclipse.gef4.zest.tests.dot.TestDotTemplate;
import org.eclipse.gef4.zest.tests.dot.TestImageExport;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Main test suite for the {@code org.eclipse.gef4.zest.dot.export} bundle.
 * 
 * @author Fabian Steeg (fsteeg)
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ TestDotTemplate.class, TestDotExport.class,
		TestImageExport.class })
public final class DotExportSuite {
	private DotExportSuite() { /* Enforce non-instantiability */
	}

	@BeforeClass
	public static void wipe() {
		DotTestUtils.wipeOutput(TestDotExport.OUTPUT, ".dot"); //$NON-NLS-1$
	}
}
