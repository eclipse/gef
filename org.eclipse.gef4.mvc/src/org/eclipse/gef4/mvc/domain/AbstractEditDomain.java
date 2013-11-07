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

import java.util.List;

import org.eclipse.gef4.mvc.commands.CommandStack;
import org.eclipse.gef4.mvc.partviewer.IEditPartViewer;
import org.eclipse.gef4.mvc.tools.ITool;

/**
 * The collective state of a GEF "application", loosely defined by a
 * CommandStack, one or more EditPartViewers, and the active Tool. An EditDomain
 * is usually tied with an Eclipse {@link org.eclipse.ui.IEditorPart
 * IEditorPart}). However, the distinction between EditorPart and EditDomain was
 * made to allow for much flexible use of the Graphical Editing Framework.
 */
public abstract class AbstractEditDomain<V> implements IEditDomain<V> {

	private List<ITool<V>> activeTools;
	private IEditPartViewer<V> viewer;

	private SelectionAndFocusModel<V> selectionAndFocusModel = new SelectionAndFocusModel<V>();

	private CommandStack commandStack = new CommandStack();

	/**
	 * Constructs an EditDomain and loads the default tool.
	 */
	public AbstractEditDomain() {
		setActiveTools(getDefaultTools());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.IEditDomain#addViewer(org.eclipse.gef.ui.parts
	 * .IEditPartViewer)
	 */
	@Override
	public void setViewer(IEditPartViewer<V> viewer) {
		if (this.viewer == viewer) {
			return;
		}
		if (this.viewer != null) {
			if (activeTools != null) {
				for (ITool<V> t : activeTools) {
					t.deactivate();
					t.setViewer(null);
				}
			}
			this.viewer.setEditDomain(null);
		}
		this.viewer = viewer;
		if (this.viewer != null) {
			this.viewer.setEditDomain(this);
			if (activeTools != null) {
				for (ITool<V> t : activeTools) {
					t.setViewer(viewer);
					t.activate();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.IEditDomain#getActiveTool()
	 */
	@Override
	public List<ITool<V>> getActiveTools() {
		return activeTools;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.ui.parts.IEditDomain#getCommandStack()
	 */
	 @Override
	 public CommandStack getCommandStack() {
	 return commandStack;
	 }

	protected abstract List<ITool<V>> getDefaultTools();

	@Override
	public IEditPartViewer<V> getViewer() {
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
	public void setCommandStack(CommandStack stack) {
		commandStack = stack;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.ui.parts.IEditDomain#setActiveTool(org.eclipse.gef.tools
	 * .ITool)
	 */
	@Override
	public void setActiveTools(List<ITool<V>> tools) {
		if (activeTools != null) {
			for (ITool<V> t : activeTools) {
				if (viewer != null) {
					t.deactivate();
				}
				t.setEditDomain(null);
			}
		}
		activeTools = tools;
		if (activeTools != null) {
			for (ITool<V> t : activeTools) {
				t.setEditDomain(this);
				if (viewer != null) {
					t.activate();
				}
			}
		}
	}

	@Override
	public SelectionAndFocusModel<V> getSelectionAndFocusModel() {
		return selectionAndFocusModel;
	}

}
