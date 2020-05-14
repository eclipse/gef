/*******************************************************************************
 * Copyright (c) 2018 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.dot.tests;

public class DotExecutablePDFImageExportTest
		extends AbstractDotExecutableImageExportTest {

	@Override
	protected void test(String inputFileName) {
		dotImageExport(inputFileName, "pdf");
	}

}
