/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Matthias Wienand (itemis AG) - initial text
 *
 *******************************************************************************/
/**
 * This package provides:
 * <ul>
 * <li>an adaptation of an {@link org.eclipse.gef4.geometry.planar.IGeometry} to
 * {@link javafx.scene.Node}: {@link org.eclipse.gef4.fx.nodes.FXGeometryNode}
 * </li>
 * <li>a connection abstraction that is based on
 * {@link org.eclipse.gef4.fx.anchors.IFXAnchor}:
 * {@link org.eclipse.gef4.fx.nodes.FXConnection}</li>
 * <li>a grid layer implementation:
 * {@link org.eclipse.gef4.fx.nodes.FXGridLayer}</li>
 * <li>a visual to display an image which is overlayed by another image on mouse
 * hover: {@link org.eclipse.gef4.fx.nodes.FXImageViewHoverOverlay}</li>
 * <li>a visual providing an scrollable infinite canvas:
 * {@link org.eclipse.gef4.fx.nodes.InfiniteCanvas}</li>
 * <li>utilities (picking nodes, querying the pointer location,
 * transformations): {@link org.eclipse.gef4.fx.nodes.FXUtils}</li>
 * </ul>
 */
package org.eclipse.gef4.fx.nodes;