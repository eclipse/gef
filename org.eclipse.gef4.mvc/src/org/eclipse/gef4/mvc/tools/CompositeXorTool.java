/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.mvc.tools;

/**
 * 
 * @author mwienand
 *
 * @param <V>
 */
public class CompositeXorTool<V> extends AbstractCompositeTool<V> implements
		ICompositeTool<V> {

	private ITool<V> selectedTool = null;

	protected void selectTool(ITool<V> tool) {
		if (tool == selectedTool) {
			return;
		}
		if (selectedTool != null) {
			if (selectedTool.isActive()) {
				selectedTool.deactivate();
			}
		}
		selectedTool = tool;
		if (selectedTool != null) {
			if (isActive()) {
				selectedTool.activate();
			}
		}
	}

	@Override
	public void deactivate() {
		if (selectedTool != null) {
			selectedTool.deactivate();
		}
		super.deactivate();
	}
	
	@Override
	public void activate() {
		super.activate();
		if (selectedTool != null) {
			selectedTool.activate();
		}
	}

}
