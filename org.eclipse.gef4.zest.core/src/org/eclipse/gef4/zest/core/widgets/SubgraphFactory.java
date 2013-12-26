/*******************************************************************************
 * Copyright (c) 2009-2010 Mateusz Matela and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Mateusz Matela - initial API and implementation
 *               Ian Bull
 ******************************************************************************/
package org.eclipse.gef4.zest.core.widgets;

import org.eclipse.gef4.layout.interfaces.LayoutContext;
import org.eclipse.gef4.layout.interfaces.NodeLayout;
import org.eclipse.gef4.layout.interfaces.SubgraphLayout;

/**
 * Factory used by {@link Graph} to create subgraphs. One instance of
 * SubgraphFactory can be used with multiple graphs unless explicitly stated
 * otherwise.
 * 
 * @since 2.0
 */
public interface SubgraphFactory {
	SubgraphLayout createSubgraph(NodeLayout[] nodes, LayoutContext context);
}
