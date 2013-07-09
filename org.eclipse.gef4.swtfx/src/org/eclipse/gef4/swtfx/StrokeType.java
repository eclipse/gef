/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.swtfx;

public enum StrokeType {

	/**
	 * The {@link ShapeFigure} is stroked on the inside.
	 * 
	 * <pre>
	 * +----------+
	 * |##########|
	 * |#        #|
	 * |#        #|
	 * |#        #|
	 * |##########|
	 * +----------+
	 * </pre>
	 * 
	 * @see #OUTSIDE
	 * @see #CENTER
	 */
	INSIDE,

	/**
	 * The {@link ShapeFigure} is stroked on the outside.
	 * 
	 * <pre>
	 * ##############
	 * #+----------+#
	 * #|          |#
	 * #|          |#
	 * #|          |#
	 * #|          |#
	 * #|          |#
	 * #+----------+#
	 * ##############
	 * </pre>
	 * 
	 * @see #INSIDE
	 * @see #CENTER
	 */
	OUTSIDE,

	/**
	 * The {@link ShapeFigure} is stroked on its outline.
	 * 
	 * <pre>
	 * ############
	 * #          #
	 * #          #
	 * #          #
	 * #          #
	 * #          #
	 * ############
	 * </pre>
	 * 
	 * @see #INSIDE
	 * @see #OUTSIDE
	 */
	CENTER,

}
