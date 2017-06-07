/*******************************************************************************
 * Copyright (c) 2011, 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.common.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AdaptableSupportTests.class, AdapterInjectorTests.class,
		AdaptableScopeTests.class, CollectionUtilsTests.class,
		MapPropertyExTests.class, SetPropertyExTests.class,
		ListPropertyExTests.class, ObservableListTests.class,
		ObservableSetMultimapTests.class, ObservableMultisetTests.class,
		SetMultimapPropertyTests.class, MultisetPropertyTests.class,
		TypesTests.class, ReadOnlyListWrapperExTests.class,
		ReadOnlyMapWrapperExTests.class, ReadOnlySetWrapperExTests.class })
public class AllTests {
}
