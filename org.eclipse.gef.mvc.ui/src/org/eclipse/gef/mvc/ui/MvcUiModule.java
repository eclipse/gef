/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.ui;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.gef.mvc.ui.properties.IPropertySheetPageFactory;
import org.eclipse.gef.mvc.ui.properties.UndoablePropertySheetPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * The {@link MvcUiModule} contains Eclipse UI specific bindings in the context
 * of an MVC application.
 *
 * @author anyssen
 *
 */
public class MvcUiModule extends AbstractModule {

	/**
	 * Binds {@link IOperationHistory} to the operation history of the Eclipse
	 * workbench.
	 */
	protected void bindIOperationHistory() {
		binder().bind(IOperationHistory.class).toInstance(PlatformUI
				.getWorkbench().getOperationSupport().getOperationHistory());
	}

	/**
	 * Binds a factory for assisted injection of
	 * {@link UndoablePropertySheetPage} as {@link IPropertySheetPage}.
	 */
	protected void bindIPropertySheetPageFactory() {
		install(new FactoryModuleBuilder()
				.implement(IPropertySheetPage.class,
						UndoablePropertySheetPage.class)
				.build(IPropertySheetPageFactory.class));
	}

	@Override
	protected void configure() {
		// bindings related to workbench integration
		bindIOperationHistory();
		bindIPropertySheetPageFactory();
	}
}
