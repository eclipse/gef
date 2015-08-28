/*******************************************************************************
 * Copyright (c) 2009, 2015 Fabian Steeg and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Fabian Steeg - initial API and implementation (see bug #277380)
 *******************************************************************************/
package org.eclipse.gef4.dot.tests;

import static org.eclipse.gef4.dot.tests.DotTestUtils.RESOURCES_TESTS;

import java.io.File;

import org.eclipse.gef4.internal.dot.DotAst;
import org.eclipse.gef4.internal.dot.DotFileUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the {@link DotAst} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public class DotAstTests {

	@Test
	public void parseSampleInput() {
		DotAst ast = new DotAst(DotFileUtils
				.read(new File(RESOURCES_TESTS + "sample_input.dot")));
		Assert.assertEquals("SampleGraph", ast.graphName()); //$NON-NLS-1$
		Assert.assertEquals(0, ast.errors().size());
	}
}
