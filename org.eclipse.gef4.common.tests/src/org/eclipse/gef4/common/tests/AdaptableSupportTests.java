/*******************************************************************************
 * Copyright (c) 2011, 2014 itemis AG and others.
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
		public void removePropertyChangeListener(
				PropertyChangeListener listener) {
			pcs.removePropertyChangeListener(listener);
		}

		@Override
		public <T> void setAdapter(AdapterKey<? super T> key,
				TypeToken<T> adapterType, T adapter) {
			ads.setAdapter(key, adapterType, adapter);
		}

		@Override
		public <T> void setAdapter(AdapterKey<T> key, T adapter) {
			ads.setAdapter(key, adapter);
		}

		@Override
		public <T> void setAdapter(Class<T> key, T adapter) {
			ads.setAdapter(key, adapter);
		}

		@Override
		public <T> void setAdapter(TypeToken<? super T> key,
				TypeToken<T> adapterType, T adapter) {
			ads.setAdapter(key, adapterType, adapter);
		}

		@Override
		public <T> void unsetAdapter(T adapter) {
			ads.unsetAdapter(adapter);
		}
	}

	private class ParameterizedSubType<T> extends ParameterizedType<T> {
	}

	private class ParameterizedSuperType<T> extends Object {
	}

	private class ParameterizedType<T> extends ParameterizedSuperType<T> {
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
	@SuppressWarnings("serial")
	@Test
	public void adapterInstanceRegisteredUnderDifferentKeys() {
		AdaptableSupportTestDriver td = new AdaptableSupportTestDriver();

		ParameterizedType<ParameterType1> adapter1 = new ParameterizedType<ParameterType1>();
		// register adapter (without adapter type)
		td.setAdapter(AdapterKey
				.get(new TypeToken<ParameterizedType<ParameterType1>>() {
				}), new TypeToken<ParameterizedType<ParameterType1>>() {
				}, adapter1);
		td.setAdapter(AdapterKey
				.get(new TypeToken<ParameterizedSuperType<ParameterType1>>() {
				}), new TypeToken<ParameterizedType<ParameterType1>>() {
				}, adapter1);
		// retrieve adapter
		assertEquals(adapter1, td.getAdapter(AdapterKey
				.get(new TypeToken<ParameterizedType<ParameterType1>>() {
				})));
		assertEquals(adapter1, td.getAdapter(AdapterKey
				.get(new TypeToken<ParameterizedSuperType<ParameterType1>>() {
				})));

		// register adapter
		ParameterizedType<ParameterType2> adapter2 = new ParameterizedType<ParameterType2>();
		td.setAdapter(AdapterKey
				.get(new TypeToken<ParameterizedType<ParameterType2>>() {
				}, "role"), new TypeToken<ParameterizedType<ParameterType2>>() {
				}, adapter2);
		td.setAdapter(AdapterKey
				.get(new TypeToken<ParameterizedSuperType<ParameterType2>>() {
				}, "role"),
				new TypeToken<ParameterizedSuperType<ParameterType2>>() {
				}, adapter2);

		// retrieve adapter
		assertEquals(adapter2, td.getAdapter(AdapterKey
				.get(new TypeToken<ParameterizedSuperType<ParameterType2>>() {
				}, "role")));
	}

	@SuppressWarnings("serial")
	@Test
	public void polymorphicRegistrationAndRetrievalWithoutRole() {
		AdaptableSupportTestDriver td = new AdaptableSupportTestDriver();

		// register adapter via super type key
		ParameterizedSubType<ParameterType1> adapter = new ParameterizedSubType<ParameterType1>();
		TypeToken<ParameterizedSuperType<ParameterType1>> keyTypeToken = new TypeToken<ParameterizedSuperType<ParameterType1>>() {
		};
		td.setAdapter(keyTypeToken,
				new TypeToken<ParameterizedSubType<ParameterType1>>() {
				}, adapter);

		// retrieve via key type token
		assertEquals(adapter, td.getAdapter(keyTypeToken));

		// retrieve via intermediate type token
		assertEquals(adapter, td
				.getAdapter(new TypeToken<ParameterizedType<ParameterType1>>() {
				}));

		// retrieve via runtime type token
		assertEquals(adapter, td.getAdapter(
				new TypeToken<ParameterizedSubType<ParameterType1>>() {
				}));

		// cannot retrieve with a super type key
		assertNull(td.getAdapter(Object.class));
	}

	@SuppressWarnings("serial")
	@Test
	public void polymorphicRegistrationAndRetrievalWithRole() {
		AdaptableSupportTestDriver td = new AdaptableSupportTestDriver();

		// register adapter via super type key
		ParameterizedSubType<ParameterType1> adapter = new ParameterizedSubType<ParameterType1>();
		TypeToken<ParameterizedSuperType<ParameterType1>> keyTypeToken = new TypeToken<ParameterizedSuperType<ParameterType1>>() {
		};
		td.setAdapter(AdapterKey.get(keyTypeToken, "role"),
				new TypeToken<ParameterizedSubType<ParameterType1>>() {
				}, adapter);

		// retrieve via key type token
		assertEquals(adapter,
				td.getAdapter(AdapterKey.get(keyTypeToken, "role")));

		// retrieve via intermediate type token
		assertEquals(adapter, td.getAdapter(AdapterKey
				.get(new TypeToken<ParameterizedType<ParameterType1>>() {
				}, "role")));

		// retrieve via runtime type token
		assertEquals(adapter, td.getAdapter(AdapterKey
				.get(new TypeToken<ParameterizedSubType<ParameterType1>>() {
				}, "role")));

		// cannot retrieve with a super type key
		assertNull(td.getAdapter(Object.class));
	}

	@SuppressWarnings({ "serial", "rawtypes" })
	@Test
	public void registrationAndRetrievalOfParameterizedTypes() {
		AdaptableSupportTestDriver td = new AdaptableSupportTestDriver();

		// register adapters
		ParameterizedType<ParameterType1> parameterType1 = new ParameterizedType<ParameterType1>();
		ParameterizedType<ParameterType2> parameterType2 = new ParameterizedType<ParameterType2>();

		td.setAdapter(new TypeToken<ParameterizedType<ParameterType1>>() {
		}, new TypeToken<ParameterizedType<ParameterType1>>() {
		}, parameterType1);
		td.setAdapter(new TypeToken<ParameterizedType<ParameterType2>>() {
		}, new TypeToken<ParameterizedType<ParameterType2>>() {
		}, parameterType2);

		// check retrieval
		assertNotNull(td
				.getAdapter(new TypeToken<ParameterizedType<ParameterType1>>() {
				}));
		assertNotNull(td
				.getAdapter(new TypeToken<ParameterizedType<ParameterType2>>() {
				}));
		// getAdapter(TypeToken) using parameterized type
		assertEquals(parameterType1, td
				.getAdapter(new TypeToken<ParameterizedType<ParameterType1>>() {
				}));
		assertEquals(parameterType2, td
				.getAdapter(new TypeToken<ParameterizedType<ParameterType2>>() {
				}));
		// getAdapter(Class) -> no adapter found because of ambiguity
		assertNull(td.getAdapter(ParameterizedType.class));
		// getAdapters(TypeToken) using parameterized types -> matching adapter
		// found
		assertEquals(1, td.getAdapters(
				new TypeToken<ParameterizedType<ParameterType1>>() {
				}).size());
		assertEquals(1, td.getAdapters(
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

	@SuppressWarnings("serial")
	@Test
	public void registrationAndRetrievalOfParameterizedTypeWithRoles() {
		AdaptableSupportTestDriver td = new AdaptableSupportTestDriver();

		// register adapters
		ParameterizedType<ParameterType1> adapter = new ParameterizedType<ParameterType1>();

		td.setAdapter(AdapterKey.get(
				new TypeToken<ParameterizedType<? extends ParameterType1>>() {
				}, "role"), new TypeToken<ParameterizedType<ParameterType1>>() {
				}, adapter);

		assertEquals(adapter, td.getAdapter(AdapterKey
				.get(new TypeToken<ParameterizedType<ParameterType1>>() {
				}, "role")));
	}
}
