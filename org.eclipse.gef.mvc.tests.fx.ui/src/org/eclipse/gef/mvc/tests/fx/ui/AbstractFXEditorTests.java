/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
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
package org.eclipse.gef.mvc.tests.fx.ui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gef.fx.swt.canvas.FXCanvasEx;
import org.eclipse.gef.fx.swt.canvas.IFXCanvasFactory;
import org.eclipse.gef.mvc.behaviors.IBehavior;
import org.eclipse.gef.mvc.fx.MvcFxModule;
import org.eclipse.gef.mvc.fx.ui.parts.AbstractFXEditor;
import org.eclipse.gef.mvc.fx.viewer.FXViewer;
import org.eclipse.gef.mvc.operations.ITransactionalOperation;
import org.eclipse.gef.mvc.parts.IContentPart;
import org.eclipse.gef.mvc.parts.IContentPartFactory;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.part.NullEditorInput;
import org.eclipse.ui.operations.UndoRedoActionGroup;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

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
		protected UndoRedoActionGroup createUndoRedoActionGroup() {
			return null;
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
		 * Binds an {@link IFXCanvasFactory} that creates an {@link FXCanvasEx}
		 * as the container for the {@link FXViewer}.
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
			binder().bind(new TypeLiteral<IContentPartFactory<Node>>() {
			}).toInstance(new IContentPartFactory<Node>() {
				@Override
				public IContentPart<Node, ? extends Node> createContentPart(Object content,
						IBehavior<Node> contextBehavior, Map<Object, Object> contextMap) {
					return null;
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
		operation.addContext(editor.getDomain().getUndoContext());
		try {
			editor.getDomain().getOperationHistory().execute(operation, null, null);
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

		// make first dirty
		IUndoableOperation operation = new ContentRelevantOperation();
		operation.addContext(editor1.getDomain().getUndoContext());
		try {
			editor1.getDomain().getOperationHistory().execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		assertTrue(editor1.isDirty());
		assertFalse(editor2.isDirty());

		// make second dirty
		operation = new ContentRelevantOperation();
		operation.addContext(editor2.getDomain().getUndoContext());
		try {
			editor2.getDomain().getOperationHistory().execute(operation, null, null);
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
		operation.addContext(editor2.getDomain().getUndoContext());
		try {
			editor2.getDomain().getOperationHistory().execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		assertFalse(editor1.isDirty());
		assertTrue(editor2.isDirty());

		// make first dirty
		operation = new ContentRelevantOperation();
		operation.addContext(editor1.getDomain().getUndoContext());
		try {
			editor1.getDomain().getOperationHistory().execute(operation, null, null);
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
		operation.addContext(editor.getDomain().getUndoContext());
		try {
			editor.getDomain().getOperationHistory().execute(operation, null, null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		assertFalse(editor.isDirty());
	}

}
