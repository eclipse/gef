/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
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
package org.eclipse.gef4.graphics.swt;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Resource;

abstract class GCLazyProperty<T, S extends Resource> {

	protected boolean dirty = true;
	protected T data = null;
	protected S tmp = null, mem = null;

	public void apply(GC gc) {
		if (dirty) {
			tmp = generate(data);
			write(gc, tmp);
			Utils.dispose(mem);
			mem = tmp;
			dirty = false;
		} else {
			write(gc, mem);
		}
	}

	public void clean() {
		Utils.dispose(mem);
	}

	abstract protected S generate(T data);

	public void set(T val) {
		data = val;
		dirty = true;
	}

	abstract protected void write(GC gc, S val);

}
