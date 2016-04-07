/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.domain;

import org.eclipse.gef4.mvc.domain.AbstractDomain;

import javafx.scene.Node;

/**
 * The {@link FXDomain} is an implementation of {@link AbstractDomain} which
 * binds the visual root type to {@link Node}.
 *
 * @author anyssen
 *
 */
public class FXDomain extends AbstractDomain<Node> {

	/**
	 * The adapter role for the content viewer.
	 */
	public static final String CONTENT_VIEWER_ROLE = "contentViewer";
}
