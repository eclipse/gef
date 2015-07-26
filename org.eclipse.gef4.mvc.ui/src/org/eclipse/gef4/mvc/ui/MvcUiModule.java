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
package org.eclipse.gef4.mvc.ui;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.gef4.mvc.ui.parts.DefaultSelectionProvider;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.PlatformUI;

import com.google.inject.AbstractModule;

public class MvcUiModule extends AbstractModule {

	protected void bindIOperationHistory() {
		binder().bind(IOperationHistory.class).toInstance(PlatformUI
				.getWorkbench().getOperationSupport().getOperationHistory());
	}

	protected void bindISelectionProvider() {
		binder().bind(ISelectionProvider.class)
				.to(DefaultSelectionProvider.class);
	}

	@Override
	protected void configure() {
		// bindings related to workbench integration
		bindISelectionProvider();
		bindIOperationHistory();
	}

}
