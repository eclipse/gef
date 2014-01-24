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
public class CompositeAndTool<V> extends AbstractCompositeTool<V> {
	
	@Override
	public void activate() {
		super.activate();
		for (ITool<V> tool : getSubTools()) {
			tool.activate();
		}
	}

	@Override
	public void deactivate() {
		for(ITool<V> tool : getSubTools()){
			tool.deactivate();
		}
		super.deactivate();
	}
	
}
