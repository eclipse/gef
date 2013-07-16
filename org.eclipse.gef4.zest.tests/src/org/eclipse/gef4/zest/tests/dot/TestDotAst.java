/*******************************************************************************
 * Copyright (c) 2009, 2011 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot;

import java.io.File;

import org.eclipse.gef4.zest.internal.dot.DotAst;
import org.eclipse.gef4.zest.internal.dot.DotFileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link DotAst} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class TestDotAst {
	private static final DotAst DOT_AST = new DotAst(
			DotFileUtils.read(new File(DotImportTestUtils.RESOURCES_TESTS
					+ "sample_input.dot"))); //$NON-NLS-1$

	@Test
	public void parseName() {
		Assert.assertEquals("SampleGraph", DOT_AST.graphName()); //$NON-NLS-1$
	}

	@Test
	public void parseErrors() {
		Assert.assertEquals(0, DOT_AST.errors().size());
	}

}
