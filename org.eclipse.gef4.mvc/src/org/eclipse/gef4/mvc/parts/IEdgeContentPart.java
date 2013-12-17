/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     itemis AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.mvc.parts;

import java.util.List;

import org.eclipse.gef4.mvc.anchors.IAnchor;

public interface IEdgeContentPart<V> extends IContentPart<V> {

	List<IAnchor<V>> getSourceAnchors();
	void addSourceAnchor(IAnchor<V> sourceAnchor);
	void removeSourceAnchor(IAnchor<V> sourceAnchor);

	List<IAnchor<V>> getTargetAnchors();
	void addTargetAnchor(IAnchor<V> targetAnchor);
	void removeTargetAnchor(IAnchor<V> targetAnchor);
	
}
