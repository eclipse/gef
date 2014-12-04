/*******************************************************************************
 * Copyright (c) 2011 itemis AG and others.
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
package org.eclipse.gef4.common.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Map;

import org.eclipse.gef4.common.adapt.AdaptableSupport;
import org.eclipse.gef4.common.adapt.AdapterKey;
import org.eclipse.gef4.common.adapt.IAdaptable;
import org.junit.Test;

import com.google.common.reflect.TypeToken;

// TODO: re-write getAdapter() tests systematically, testing all possible combinations of registering and retrieving adapters
public class AdaptableSupportTests {

	private class AdaptableSupportTestDriver implements IAdaptable {

		private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
		private AdaptableSupport<AdaptableSupportTestDriver> ads = new AdaptableSupport<AdaptableSupportTestDriver>(
				this, pcs);

		@Override
		public void addPropertyChangeListener(PropertyChangeListener listener) {
			pcs.addPropertyChangeListener(listener);
		}

		@Override
		public <T> T getAdapter(AdapterKey<? super T> key) {
			return ads.getAdapter(key);
		}

		@Override
		public <T> T getAdapter(Class<? super T> key) {
			return ads.getAdapter(key);
		}

		@Override
		public <T> T getAdapter(TypeToken<? super T> key) {
			return ads.getAdapter(key);
		}

		@Override
		public <T> Map<AdapterKey<? extends T>, T> getAdapters(
				Class<? super T> key) {
			return ads.getAdapters(key);
		}

		@Override
		public <T> Map<AdapterKey<? extends T>, T> getAdapters(
				TypeToken<? super T> key) {
			return ads.getAdapters(key);
		}

		@Override
		public void removePropertyChangeListener(PropertyChangeListener listener) {
			pcs.removePropertyChangeListener(listener);
		}

		@Override
		public <T> void setAdapter(AdapterKey<? super T> key, T adapter) {
			ads.setAdapter(key, adapter);
		}

		@Override
		public <T> void setAdapter(Class<? super T> key, T adapter) {
			ads.setAdapter(key, adapter);
		}

		@Override
		public <T> void setAdapter(TypeToken<? super T> key, T adapter) {
			ads.setAdapter(key, adapter);
		}

		@Override
		public <T> T unsetAdapter(AdapterKey<? super T> key) {
			return ads.unsetAdapter(key);
		}
	}

	private class ParameterizedSubType<T> extends ParameterizedType<T> {

	}

	private class ParameterizedSuperType<T> extends Object {

	}

	private class ParameterizedType<T> extends ParameterizedSuperType<T> {
	}

	private class ParameterSubType1 extends ParameterType1 {

	}

	private class ParameterType1 extends Object {

	}

	private class ParameterType2 extends Object {

	}

	/**
	 * Test that an adapter instance can be registered and properly retrieved if
	 * registered under different type keys.
	 *
	 */
	@Test
	public void adapterInstanceRegisteredUnderDifferentKeys() {
		AdaptableSupportTestDriver td = new AdaptableSupportTestDriver();

		ParameterizedType<ParameterType1> parameterizedType1 = new ParameterizedType<ParameterType1>();

		ParameterizedType<ParameterType2> parameterizedType2 = new ParameterizedType<ParameterType2>();

		td.setAdapter(AdapterKey
				.get(new TypeToken<ParameterizedType<ParameterType1>>() {
				}), parameterizedType1);
		td.setAdapter(AdapterKey
				.get(new TypeToken<ParameterizedSuperType<ParameterType1>>() {
				}), parameterizedType1);

		assertEquals(parameterizedType1, td.getAdapter(AdapterKey
				.get(new TypeToken<ParameterizedSuperType<ParameterType1>>() {
				})));

		td.setAdapter(AdapterKey.get(
				new TypeToken<ParameterizedType<ParameterType2>>() {
				}, "role"), parameterizedType2);
		td.setAdapter(AdapterKey.get(
				new TypeToken<ParameterizedSuperType<ParameterType2>>() {
				}, "role"), parameterizedType2);

		assertEquals(parameterizedType2, td.getAdapter(AdapterKey.get(
				new TypeToken<ParameterizedSuperType<ParameterType2>>() {
				}, "role")));
	}

	@Test
	public void polymorphicRegistration() {
		AdaptableSupportTestDriver td = new AdaptableSupportTestDriver();

		// register adapter
		ParameterizedSubType<ParameterType1> parameterType1 = new ParameterizedSubType<ParameterType1>();
		td.setAdapter(new TypeToken<ParameterizedSuperType<ParameterType1>>() {
		}, parameterType1);

		// retrieve adapter
		assertEquals(
				parameterType1,
				td.getAdapter(new TypeToken<ParameterizedSuperType<ParameterType1>>() {
				}));
		assertNull(td
				.getAdapter(new TypeToken<ParameterizedType<ParameterType1>>() {
				}));
		assertNull(td
				.getAdapter(new TypeToken<ParameterizedSubType<ParameterType1>>() {
				}));
	}

	@Test
	public void polymorphicRetrievalOfParameterizedTypes() {
		AdaptableSupportTestDriver td = new AdaptableSupportTestDriver();

		// register adapter
		ParameterizedSubType<ParameterType1> parameterType1 = new ParameterizedSubType<ParameterType1>();
		td.setAdapter(new TypeToken<ParameterizedSubType<ParameterType1>>() {
		}, parameterType1);

		// retrieve adapter
		assertEquals(
				parameterType1,
				td.getAdapter(new TypeToken<ParameterizedSuperType<ParameterType1>>() {
				}));
		assertEquals(
				parameterType1,
				td.getAdapter(new TypeToken<ParameterizedType<ParameterType1>>() {
				}));
		assertEquals(
				parameterType1,
				td.getAdapter(new TypeToken<ParameterizedSubType<ParameterType1>>() {
				}));
		assertEquals(
				parameterType1,
				td.getAdapter(new TypeToken<ParameterizedSuperType<? extends ParameterType1>>() {
				}));
		assertEquals(
				parameterType1,
				td.getAdapter(new TypeToken<ParameterizedType<? extends ParameterType1>>() {
				}));
		assertEquals(
				parameterType1,
				td.getAdapter(new TypeToken<ParameterizedSubType<? extends ParameterType1>>() {
				}));
	}

	@Test
	public void registrationAndRetrievalOfParameterizedSubTypeWithRoles() {
		AdaptableSupportTestDriver td = new AdaptableSupportTestDriver();

		// register adapters
		ParameterizedType<ParameterSubType1> parameterSubType1 = new ParameterizedType<ParameterSubType1>();

		td.setAdapter(AdapterKey.get(
				new TypeToken<ParameterizedType<ParameterSubType1>>() {
				}, "role"), parameterSubType1);

		assertEquals(parameterSubType1, td.getAdapter(AdapterKey.get(
				new TypeToken<ParameterizedType<? extends ParameterType1>>() {
				}, "role")));
	}

	@Test
	public void registrationAndRetrievalOfParameterizedTypes() {
		AdaptableSupportTestDriver td = new AdaptableSupportTestDriver();

		// register adapters
		ParameterizedType<ParameterType1> parameterType1 = new ParameterizedType<ParameterType1>();
		ParameterizedType<ParameterType2> parameterType2 = new ParameterizedType<ParameterType2>();

		td.setAdapter(new TypeToken<ParameterizedType<ParameterType1>>() {
		}, parameterType1);
		td.setAdapter(new TypeToken<ParameterizedType<ParameterType2>>() {
		}, parameterType2);

		// check retrieval
		assertNotNull(td
				.getAdapter(new TypeToken<ParameterizedType<ParameterType1>>() {
				}));
		assertNotNull(td
				.getAdapter(new TypeToken<ParameterizedType<ParameterType2>>() {
				}));
		// getAdapter(TypeToken) using parameterized type
		assertEquals(
				parameterType1,
				td.getAdapter(new TypeToken<ParameterizedType<ParameterType1>>() {
				}));
		assertEquals(
				parameterType2,
				td.getAdapter(new TypeToken<ParameterizedType<ParameterType2>>() {
				}));
		// getAdapter(Class) -> no adapter found because of ambiguity
		assertNull(td.getAdapter(ParameterizedType.class));
		// getAdapters(TypeToken) using parameterized types -> matching adapter
		// found
		assertEquals(
				1,
				td.getAdapters(
						new TypeToken<ParameterizedType<ParameterType1>>() {
						}).size());
		assertEquals(
				1,
				td.getAdapters(
						new TypeToken<ParameterizedType<ParameterType2>>() {
						}).size());
		// getAdapters(Class) -> both adapters found, as compatible
		assertEquals(2, td.getAdapters(ParameterizedType.class).size());
		// getAdapters(TypeToken) using wildcard type -> -> both adapters found,
		// as compatible
		assertEquals(2, td.getAdapters(new TypeToken<ParameterizedType<?>>() {
		}).size());
		// getAdapters(TypeToken) using raw type -> -> both adapters found, as
		// compatible
		assertEquals(2, td.getAdapters(new TypeToken<ParameterizedType>() {
		}).size());
	}

	@Test
	public void registrationAndRetrievalOfParameterizedWithRoles() {
		AdaptableSupportTestDriver td = new AdaptableSupportTestDriver();

		// register adapters
		ParameterizedType<ParameterType1> parameterType1 = new ParameterizedType<ParameterType1>();

		td.setAdapter(AdapterKey.get(
				new TypeToken<ParameterizedType<ParameterType1>>() {
				}, "role"), parameterType1);

		assertEquals(parameterType1, td.getAdapter(AdapterKey.get(
				new TypeToken<ParameterizedType<? extends ParameterType1>>() {
				}, "role")));
	}
}
