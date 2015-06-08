/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Alexander Nyßen (itemis AG) - initial text
 *
 *******************************************************************************/
/**
 * This package provides behaviors for transferring information between graph
 * model elements and layout abstractions (
 * {@link org.eclipse.gef4.zest.fx.behaviors.LayoutContextBehavior},
 * {@link org.eclipse.gef4.zest.fx.behaviors.AbstractLayoutBehavior},
 * {@link org.eclipse.gef4.zest.fx.behaviors.NodeLayoutBehavior},
 * {@link org.eclipse.gef4.zest.fx.behaviors.EdgeLayoutBehavior}),
 * hiding/unhiding of nodes and related edges (
 * {@link org.eclipse.gef4.zest.fx.behaviors.AbstractHidingBehavior},
 * {@link org.eclipse.gef4.zest.fx.behaviors.HidingBehavior},
 * {@link org.eclipse.gef4.zest.fx.behaviors.EdgeHidingBehavior},
 * {@link org.eclipse.gef4.zest.fx.behaviors.EdgeLabelHidingBehavior}), as well
 * as navigation of nested graphs via semantic zooming (
 * {@link org.eclipse.gef4.zest.fx.behaviors.SynchronizeChildrenOnZoomBehavior},
 * {@link org.eclipse.gef4.zest.fx.behaviors.OpenNestedGraphOnZoomBehavior},
 * {@link org.eclipse.gef4.zest.fx.behaviors.OpenParentGraphOnZoomBehavior}).
 */
package org.eclipse.gef4.zest.fx.behaviors;