/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.mvc.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.gef4.mvc.partviewer.IVisualPartViewer;
import org.eclipse.gef4.mvc.tools.ITool;

/**
 * The collective state of a GEF "application", loosely defined by a
 * CommandStack, one or more EditPartViewers, and the active Tool. An EditDomain
 * is usually tied with an Eclipse {@link org.eclipse.ui.IEditorPart
 * IEditorPart}). However, the distinction between EditorPart and EditDomain was
 * made to allow for much flexible use of the Graphical Editing Framework.
 */
public abstract class AbstractEditDomain<V> implements IEditDomain<V> {

	private Stack<ITool<V>> toolsStack = new Stack<ITool<V>>();
	private IVisualPartViewer<V> viewer;
	private Map<Class<? extends Object>, Object> properties;

	private IOperationHistory operationHistory = new DefaultOperationHistory();

	/**
	 * Constructs an EditDomain and loads the default tool.
	 */
	public AbstractEditDomain() {
		pushTool(getDefaultTool());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.IEditDomain#addViewer(org.eclipse.gef.ui.parts
	 * .IEditPartViewer)
	 */
	@Override
	public void setViewer(IVisualPartViewer<V> viewer) {
		if (this.viewer == viewer) {
			return;
		}
		if (this.viewer != null) {
			if (peekTool() != null) {
				peekTool().deactivate();
				peekTool().setDomain(null);
			}
			this.viewer.setEditDomain(null);
		}
		this.viewer = viewer;
		if (viewer != null) {
			viewer.setEditDomain(this);
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
	public ITool<V> peekTool() {
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

	protected abstract ITool<V> getDefaultTool();

	@Override
	public IVisualPartViewer<V> getViewer() {
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
	public void pushTool(ITool<V> tool) {
		if (tool != null) {
			ITool<V> currentTool = peekTool();
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
	public ITool<V> popTool() {
		if (!toolsStack.isEmpty()) {
			ITool<V> currentTool = toolsStack.pop();
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
	public <P extends Object> void setProperty(Class<P> key, P property) {
		// unregister old property
		if (properties != null && properties.get(key) != null) {
			properties.remove(key);
		}

		// register new property
		if (property != null) {
			// create map
			if (properties == null) {
				properties = new HashMap<Class<? extends Object>, Object>();
			}
			properties.put(key, property);
		} else {
			// dispose map
			if (properties != null && properties.size() == 0) {
				properties = null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P extends Object> P getProperty(Class<P> key) {
		if (properties == null) {
			return null;
		}
		return (P) properties.get(key);
	}

}
