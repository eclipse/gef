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
package org.eclipse.gef4.mvc.parts;


/**
 * The base implementation for
 * {@link org.eclipse.gef4.mvc.parts.IEdgeContentPart}.
 */
public abstract class AbstractEdgeContentPart<V> extends
		AbstractContentEditPart<V> implements IEdgeContentPart<V> {

//	private List<IAnchor<V>> sourceAnchors;
//	private List<IAnchor<V>> targetAnchors;
//
//	@Override
//	public List<IAnchor<V>> getSourceAnchors() {
//		if (sourceAnchors == null) {
//			return Collections.emptyList();
//		}
//		return Collections.unmodifiableList(sourceAnchors);
//	}
//
//	@Override
//	public void addSourceAnchor(IAnchor<V> sourceAnchor) {
//		if (sourceAnchors == null) {
//			sourceAnchors = new ArrayList<IAnchor<V>>();
//		}
//		sourceAnchors.add(sourceAnchor);
//		refreshVisual();
//	}
//
//	@Override
//	public void removeSourceAnchor(IAnchor<V> sourceAnchor) {
//		sourceAnchors.remove(sourceAnchor);
//		if (sourceAnchors.size() == 0) {
//			sourceAnchors = null;
//		}
//		refreshVisual();
//	}
//
//	@Override
//	public List<IAnchor<V>> getTargetAnchors() {
//		if (targetAnchors == null) {
//			return Collections.emptyList();
//		}
//		return Collections.unmodifiableList(targetAnchors);
//	}
//
//	@Override
//	public void addTargetAnchor(IAnchor<V> targetAnchor) {
//		if (targetAnchors == null) {
//			targetAnchors = new ArrayList<IAnchor<V>>();
//		}
//		targetAnchors.add(targetAnchor);
//		refreshVisual();
//
//	}
//
//	@Override
//	public void removeTargetAnchor(IAnchor<V> targetAnchor) {
//		targetAnchors.remove(targetAnchor);
//		if (targetAnchors.size() == 0) {
//			targetAnchors = null;
//		}
//		refreshVisual();
//	}

}
