/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.tests.fx.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.fx.swt.canvas.FXCanvasEx;
import org.eclipse.gef.fx.swt.canvas.IFXCanvasFactory;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.mvc.fx.parts.IContentPartFactory;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXEditor;
import org.eclipse.gef.mvc.fx.ui.parts.HistoryBasedDirtyStateProvider;
import org.eclipse.gef.mvc.fx.ui.parts.IDirtyStateProvider;
import org.eclipse.gef.mvc.fx.ui.parts.IDirtyStateProviderFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.part.NullEditorInput;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;

public class AbstractFXEditorTests {

	private static class ContentIrrelevantOperation extends AbstractOperation implements ITransactionalOperation {
		public ContentIrrelevantOperation() {
			super("ContentIrrelevant");
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return Status.OK_STATUS;
		}

		@Override
		public boolean isContentRelevant() {
			return false;
		}

		@Override
		public boolean isNoOp() {
			return false;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return Status.OK_STATUS;
		}
	}

	private static class ContentRelevantOperation extends AbstractOperation implements ITransactionalOperation {
		public ContentRelevantOperation() {
			super("ContentRelevant");
		}

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return Status.OK_STATUS;
		}

		@Override
		public boolean isContentRelevant() {
			return true;
		}

		@Override
		public boolean isNoOp() {
			return false;
		}

		@Override
		public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return execute(monitor, info);
		}

		@Override
		public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			return Status.OK_STATUS;
		}
	}

	private final class FXEditor extends AbstractFXEditor {
		private FXEditor(Injector injector) {
			super(injector);
		}

		@Override
		protected void createActions() {
		}

		@Override
		protected void disposeActions() {
		}

		@Override
		public void doSave(IProgressMonitor monitor) {
		}

		@Override
		public void doSaveAs() {
		}

		@Override
		public boolean isSaveAsAllowed() {
			return false;
		}

		@Override
		protected void setSite(IWorkbenchPartSite site) {
		}
	}

	private static class Module extends MvcFxModule {
		/**
		 * Binds an {@link IFXCanvasFactory} that creates an {@link FXCanvasEx}.
		 */
		protected void bindFXCanvasFactory() {
			// TODO: change to assisted inject
			binder().bind(IFXCanvasFactory.class).toInstance(new IFXCanvasFactory() {
				@Override
				public FXCanvas createCanvas(Composite parent, int style) {
					return new FXCanvasEx(parent, style);
				}
			});
		}

		protected void bindIContentPartFactory() {
			binder().bind(IContentPartFactory.class).toInstance(new IContentPartFactory() {
				@Override
				public IContentPart<? extends Node> createContentPart(Object content, Map<Object, Object> contextMap) {
					return null;
				}
			});
		}

		/**
		 * Binds a factory for the creation of
		 * {@link HistoryBasedDirtyStateProvider} as
		 * {@link IDirtyStateProvider}.
		 */
		protected void bindIDirtyStateProviderFactory() {
			binder().bind(IDirtyStateProviderFactory.class).toInstance(new IDirtyStateProviderFactory() {

				@Override
				public IDirtyStateProvider create(IWorkbenchPart workbenchPart) {
					return new HistoryBasedDirtyStateProvider(
							(IOperationHistory) workbenchPart.getAdapter(IOperationHistory.class),
							(IUndoContext) workbenchPart.getAdapter(IUndoContext.class));
				}
			});
		}

		/**
		 * Binds {@link IOperationHistory} to the operation history of the
		 * Eclipse workbench.
		 */
		@Override
		protected void bindIOperationHistory() {
			binder().bind(IOperationHistory.class).to(DefaultOperationHistory.class);
		}

		@Override
		protected void configure() {
			super.configure();
			bindIOperationHistory();
			bindIContentPartFactory();
			bindFXCanvasFactory();
			bindIDirtyStateProviderFactory();
		}
	}

	@Test
	public void test_dirty_when_content_relevant_operation_is_added() {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new Module());

		// create editor
		AbstractFXEditor editor = new FXEditor(injector);
		try {
			editor.init(null, new NullEditorInput());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		assertFalse(editor.isDirty());

		// execute content relevant operation
		ContentRelevantOperation operation = new ContentRelevantOperation();
		IUndoContext undoContext = (IUndoContext) editor.getAdapter(IUndoContext.class);
		operation.addContext(undoContext);
		try {
			((IOperationHistory) editor.getAdapter(IOperationHistory.class)).execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		assertTrue(editor.isDirty());
	}

	@Test
	public void test_independent_dirty_state_01() {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new Module());

		// create first editor
		AbstractFXEditor editor1 = new FXEditor(injector);
		try {
			editor1.init(null, new NullEditorInput());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		assertFalse(editor1.isDirty());

		// create second editor
		AbstractFXEditor editor2 = new FXEditor(injector);
		try {
			editor2.init(null, new NullEditorInput());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		assertFalse(editor2.isDirty());

		IOperationHistory operationHistory1 = (IOperationHistory) editor1.getAdapter(IOperationHistory.class);
		IOperationHistory operationHistory2 = (IOperationHistory) editor2.getAdapter(IOperationHistory.class);
		assertNotSame(operationHistory1, operationHistory2);

		IUndoContext undoContext1 = (IUndoContext) editor1.getAdapter(IUndoContext.class);
		IUndoContext undoContext2 = (IUndoContext) editor2.getAdapter(IUndoContext.class);
		assertNotSame(undoContext1, undoContext2);

		// make first dirty
		IUndoableOperation operation = new ContentRelevantOperation();
		operation.addContext(undoContext1);
		try {
			operationHistory1.execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		assertTrue(editor1.isDirty());
		assertFalse(editor2.isDirty());

		// make second dirty
		operation = new ContentRelevantOperation();
		operation.addContext(undoContext2);
		try {
			operationHistory2.execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		assertTrue(editor1.isDirty());
		assertTrue(editor2.isDirty());
	}

	@Test
	public void test_independent_dirty_state_02() {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new Module());

		// create first editor
		AbstractFXEditor editor1 = new FXEditor(injector);
		try {
			editor1.init(null, new NullEditorInput());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		assertFalse(editor1.isDirty());

		// create second editor
		AbstractFXEditor editor2 = new FXEditor(injector);
		try {
			editor2.init(null, new NullEditorInput());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		assertFalse(editor2.isDirty());

		// make second dirty
		IUndoableOperation operation = new ContentRelevantOperation();
		operation.addContext((IUndoContext) editor2.getAdapter(IUndoContext.class));
		try {
			((IOperationHistory) editor2.getAdapter(IOperationHistory.class)).execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		assertFalse(editor1.isDirty());
		assertTrue(editor2.isDirty());

		// make first dirty
		operation = new ContentRelevantOperation();
		operation.addContext((IUndoContext) editor1.getAdapter(IUndoContext.class));
		try {
			((IOperationHistory) editor1.getAdapter(IOperationHistory.class)).execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		assertTrue(editor1.isDirty());
		assertTrue(editor2.isDirty());
	}

	@Test
	public void test_not_dirty_when_content_irrelevant_operation_is_added() {
		// create injector (adjust module bindings for test)
		Injector injector = Guice.createInjector(new Module());

		// create editor
		AbstractFXEditor editor = new FXEditor(injector);
		try {
			editor.init(null, new NullEditorInput());
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		assertFalse(editor.isDirty());

		// execute content relevant operation
		IUndoableOperation operation = new ContentIrrelevantOperation();
		operation.addContext((IUndoContext) editor.getAdapter(IUndoContext.class));
		try {
			((IOperationHistory) editor.getAdapter(IOperationHistory.class)).execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		assertFalse(editor.isDirty());
	}

}
