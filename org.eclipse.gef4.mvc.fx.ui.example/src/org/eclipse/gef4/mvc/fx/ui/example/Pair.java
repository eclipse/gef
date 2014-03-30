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
package org.eclipse.gef4.mvc.fx.ui.example;

/**
 * Generic class for holding any two values.
 * 
 * @author mwienand
 *
 * @param <S>
 * @param <T>
 */
public class Pair<S, T> {

	private S first;
	private T second;
	
	public Pair(S first, T second) {
		setFirst(first);
		setSecond(second);
	}
	
	public void setFirst(S first) {
		this.first = first;
	}
	
	public void setSecond(T second) {
		this.second = second;
	}
	
	public S getFirst() {
		return first;
	}
	
	public T getSecond() {
		return second;
	}
	
}
