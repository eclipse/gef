/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Matthias Wienand (itemis AG) - initial text
 *
 *******************************************************************************/
/**
 * This package provides:
 * <ul>
 * <li>an adaptation of an {@link org.eclipse.gef.geometry.planar.IGeometry} to
 * {@link javafx.scene.Node}: {@link org.eclipse.gef.fx.nodes.GeometryNode}
 * </li>
 * <li>a connection abstraction that is based on
 * {@link org.eclipse.gef.fx.anchors.IAnchor}:
 * {@link org.eclipse.gef.fx.nodes.Connection}</li>
 * <li>a visual to display an image which is overlayed by another image on mouse
 * hover: {@link org.eclipse.gef.fx.nodes.HoverOverlayImageView}</li>
 * <li>a visual providing a scrollable infinite canvas with a background grid:
 * {@link org.eclipse.gef.fx.nodes.InfiniteCanvas}</li>
 * </ul>
 */
package org.eclipse.gef.fx.nodes;