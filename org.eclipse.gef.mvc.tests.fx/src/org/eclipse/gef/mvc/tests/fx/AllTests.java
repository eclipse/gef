/*******************************************************************************
 * Copyright (c) 2014, 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.tests.fx;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AbstractVisualPartTests.class, BendableTests.class, ContentSynchronizationTests.class,
		FocusTraversalPolicyTests.class, SelectionModelTests.class, AbstractHandlePartTests.class,
		BendConnectionPolicyTests.class, ClickDragGestureTests.class, TypeStrokeGestureTests.class, TransformPolicyTests.class,
		FocusTraversalPolicyTests.class, ResizePolicyTests.class })
public class AllTests {

}
