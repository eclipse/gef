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
package org.eclipse.gef4.mvc.domain;

import java.util.Stack;

import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.gef4.mvc.bindings.AdaptableSupport;
import org.eclipse.gef4.mvc.tools.ITool;
import org.eclipse.gef4.mvc.viewer.IVisualViewer;

/**
 * 
 * @author anyssen
 * 
 * @param <VR>
 */
public abstract class AbstractDomain<VR> implements IDomain<VR> {

	private AdaptableSupport<IDomain<VR>> as = new AdaptableSupport<IDomain<VR>>(
			this);

			private Stack<ITool<VR>> toolsStack = new Stack<ITool<VR>>();
	private IVisualViewer<VR> viewer;


	private IOperationHistory operationHistory = new DefaultOperationHistory();
	private IUndoContext undoContext = IOperationHistory.GLOBAL_UNDO_CONTEXT;

	/**
	 * Constructs an EditDomain and loads the default tool.
	 */
	public AbstractDomain() {
		pushTool(getDefaultTool());
	}

	@Override
	public <T> T getAdapter(Class<T> key) {
		return as.getAdapter(key);
	}

	@Override
	public <T> void setAdapter(T adapter) {
		as.setAdapter(adapter);
	}

	@Override
	public <T> void setAdapter(Class<T> key, T adapter) {
		as.setAdapter(key, adapter);
	}

	@Override
	public <T> T unsetAdapter(Class<T> key) {
		return as.unsetAdapter(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.IEditDomain#addViewer(org.eclipse.gef.ui.parts
	 * .IEditPartViewer)
	 */
	@Override
	public void setViewer(IVisualViewer<VR> viewer) {
		if (this.viewer == viewer) {
			return;
		}
		if (this.viewer != null) {
			if (peekTool() != null) {
				peekTool().deactivate();
				peekTool().setDomain(null);
			}
			this.viewer.setDomain(null);
		}
		this.viewer = viewer;
		if (viewer != null) {
			viewer.setDomain(this);
			if (peekTool() != null) {
				peekTool().setDomain(this);
				peekTool().activate();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.IEditDomain#getActiveTool()
	 */
	@Override
	public ITool<VR> peekTool() {
		if (toolsStack.isEmpty()) {
			return null;
		}
		return toolsStack.peek();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.IEditDomain#getCommandStack()
	 */
	@Override
	public IOperationHistory getOperationHistory() {
		return operationHistory;
	}

	protected abstract ITool<VR> getDefaultTool();

	@Override
	public IVisualViewer<VR> getViewer() {
		return viewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.IEditDomain#setCommandStack(org.eclipse.gef.
	 * commands.CommandStack)
	 */
	@Override
	public void setOperationHistory(IOperationHistory stack) {
		operationHistory = stack;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.IEditDomain#setActiveTool(org.eclipse.gef.tools
	 * .ITool)
	 */
	@Override
	public void pushTool(ITool<VR> tool) {
		if (tool != null) {
			ITool<VR> currentTool = peekTool();
			toolsStack.push(tool);
			if (viewer != null) {
				if (currentTool != null) {
					currentTool.deactivate();
					currentTool.setDomain(null);
				}
				tool.setDomain(this);
				tool.activate();
			}
		}
	}

	@Override
	public ITool<VR> popTool() {
		if (!toolsStack.isEmpty()) {
			ITool<VR> currentTool = toolsStack.pop();
			if (viewer != null) {
				if (currentTool != null) {
					currentTool.deactivate();
					currentTool.setDomain(null);
				}
				// activate former tool, in case we are attached to a viewer
				if (peekTool() != null) {
					peekTool().setDomain(this);
					peekTool().activate();
				}
			}
			return currentTool;
		}
		return null;
	}

	@Override
	public IUndoContext getUndoContext() {
		return undoContext;
	}

	@Override
	public void setUndoContext(IUndoContext undoContext) {
		this.undoContext = undoContext;
	}

}
