/*******************************************************************************
 * Copyright (c) 2009, 2010 Fabian Steeg. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * <p/>
 * Contributors: Fabian Steeg - initial API and implementation; see bug 277380
 *******************************************************************************/
package org.eclipse.gef4.zest.tests.dot;

import static org.eclipse.gef4.zest.tests.dot.DotImportTestUtils.RESOURCES_TESTS;
import static org.eclipse.gef4.zest.tests.dot.DotImportTestUtils.importFrom;

import java.io.File;

import org.eclipse.gef4.zest.internal.dot.DotImport;
import org.junit.Test;

/**
 * Tests for the {@link DotImport} class.
 * 
 * @author Fabian Steeg (fsteeg)
 */
public final class TestAnimationDotImport {

	/**
	 * Minimal DOT graph to be transformed to a Zest animation.
	 */
	@Test
	public void simple() {
		importFrom(new File(RESOURCES_TESTS
				+ "experimental_animation_simple.dot")); //$NON-NLS-1$
	}

	/**
	 * Binary tree DOT graph to be transformed to a Zest animation.
	 */
	@Test
	public void binTree() {
		importFrom(new File(RESOURCES_TESTS
				+ "experimental_animation_bintree.dot")); //$NON-NLS-1$
	}

	/**
	 * DOT graph to be transformed to a Zest animation with specified layout
	 * algorithm.
	 */
	@Test
	public void layout() {
		importFrom(new File(RESOURCES_TESTS
				+ "experimental_animation_layout.dot")); //$NON-NLS-1$
	}

	/**
	 * DOT graph to be transformed to a Zest animation with all supported
	 * settings.
	 */
	@Test
	public void full() {
		importFrom(new File(RESOURCES_TESTS + "experimental_animation_full.dot")); //$NON-NLS-1$
	}

}
