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
 * This package provides a
 * {@link org.eclipse.ui.views.properties.PropertySheetPage} implementation that
 * integrates with the
 * {@link org.eclipse.core.commands.operations.IOperationHistory} to make
 * property changes undoable (
 * {@link org.eclipse.gef.mvc.ui.properties.UndoablePropertySheetPage}), as
 * well as an {@link org.eclipse.core.runtime.IAdapterFactory} (
 * {@link org.eclipse.gef.mvc.ui.properties.ContentPropertySourceAdapterFactory}
 * ) that adapts content elements of
 * {@link org.eclipse.gef.mvc.parts.IContentPart}s to
 * {@link org.eclipse.ui.views.properties.IPropertySource} .
 */
package org.eclipse.gef.mvc.ui.properties;