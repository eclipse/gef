/*******************************************************************************
 * Copyright (c) 2011, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.common.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.eclipse.gef.common.adapt.AdaptableSupport;
import org.eclipse.gef.common.adapt.AdapterKey;
import org.eclipse.gef.common.adapt.IAdaptable;
import org.junit.Test;

import com.google.common.reflect.TypeToken;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.ObservableMap;

public class AdaptableSupportTests {

	private class AdaptableSupportWrapper implements IAdaptable {

		private AdaptableSupport<AdaptableSupportWrapper> ads = new AdaptableSupport<>(
				this);

		@Override
		public ReadOnlyMapProperty<AdapterKey<?>, Object> adaptersProperty() {
			return ads.adaptersProperty();
		}

		protected void clear() {
			for (Object adapter : ads.getAdapters().values()) {
				ads.unsetAdapter(adapter);
			}
		}

		@Override
		public <T> T getAdapter(AdapterKey<T> key) {
			return ads.getAdapter(key);
		}

		@Override
		public <T> T getAdapter(Class<T> key) {
			return ads.getAdapter(key);
		}

		@Override
		public <T> T getAdapter(TypeToken<T> key) {
			return ads.getAdapter(key);
		}

		@Override
		public <T> AdapterKey<T> getAdapterKey(T adapter) {
			return ads.getAdapterKey(adapter);
		}

		@Override
		public ObservableMap<AdapterKey<?>, Object> getAdapters() {
			return ads.getAdapters();
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
		public <T> void setAdapter(T adapter) {
			ads.setAdapter(adapter);
		}

		@Override
		public <T> void setAdapter(T adapter, String role) {
			ads.setAdapter(adapter, role);
		}

		@Override
		public <T> void setAdapter(TypeToken<T> adapterType, T adapter) {
			ads.setAdapter(adapterType, adapter);
		}

		@Override
		public <T> void setAdapter(TypeToken<T> adapterType, T adapter,
				String role) {
			ads.setAdapter(adapterType, adapter, role);
		}

		@Override
		public <T> void unsetAdapter(T adapter) {
			ads.unsetAdapter(adapter);
		}
	}

	private class BoundParameterizedType extends ParameterizedType<Object> {

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

	@SuppressWarnings({ "serial", "rawtypes" })
	@Test
	public void registrationWithAdapterTypeOnly() {
		AdaptableSupportWrapper td = new AdaptableSupportWrapper();

		ParameterizedSubType<ParameterType1> adapter1 = new ParameterizedSubType<>();

		TypeToken<ParameterizedSuperType<ParameterType1>> superSuperType = new TypeToken<ParameterizedSuperType<ParameterType1>>() {
		};
		TypeToken<ParameterizedType<ParameterType1>> superType = new TypeToken<ParameterizedType<ParameterType1>>() {
		};
		TypeToken<ParameterizedSubType<ParameterType1>> actualType = new TypeToken<ParameterizedSubType<ParameterType1>>() {
		};

		// register adapter via actual type should succeed
		td.setAdapter(actualType, adapter1);

		// should fail (as not matching runtime type)
		td.clear();
		try {
			td.setAdapter(superType, adapter1);
			fail("Call succeeded but should have failed");
		} catch (Exception e) {
		}

		// should fail (as not matching runtime type)
		td.clear();
		try {
			td.setAdapter(superSuperType, adapter1);
			fail("Call succeeded but should have failed");
		} catch (IllegalArgumentException e) {
		}

		// should fail (as actual parameter type not bound)
		td.clear();
		try {
			td.setAdapter(new TypeToken<ParameterizedType<?>>() {
			}, adapter1);
			fail("Call succeeded but should have failed");
		} catch (IllegalArgumentException e) {
		}

		// should fail (as actual parameter type not matching)
		td.clear();
		try {
			td.setAdapter(
					new TypeToken<ParameterizedType<? extends ParameterType1>>() {
					}, adapter1);
			fail("Call succeeded but should have failed");
		} catch (IllegalArgumentException e) {
		}

		// should fail (as actual parameter type not matching)
		td.clear();
		try {
			td.setAdapter(
					new TypeToken<ParameterizedType<? super ParameterType1>>() {
					}, adapter1);
			fail("Call succeeded but should have failed");
		} catch (IllegalArgumentException e) {
		}

		// should fail (as raw type is used)
		td.clear();
		try {
			td.setAdapter(new TypeToken<ParameterizedType>() {
			}, adapter1);
			fail("Call succeeded but should have failed");
		} catch (IllegalArgumentException e) {
		}

		BoundParameterizedType adapter2 = new BoundParameterizedType();

		// should succeed
		td.clear();
		td.setAdapter(new TypeToken<BoundParameterizedType>() {
		}, adapter2);

		// should fail (because adapter type is raw super type)
		td.clear();
		try {
			td.setAdapter(new TypeToken<ParameterizedType>() {
			}, adapter1);
			fail("Call succeeded but should have failed");
		} catch (IllegalArgumentException e) {
		}

		// should fail (because adapter type is super type)
		td.clear();
		try {
			td.setAdapter(new TypeToken<ParameterizedType<ParameterType1>>() {
			}, adapter1);
			fail("Call succeeded but should have failed");
		} catch (IllegalArgumentException e) {
		}

	}

	@SuppressWarnings({ "serial", "rawtypes" })
	@Test
	public void retrievalOfMultipleAdapters() {
		AdaptableSupportWrapper td = new AdaptableSupportWrapper();

		// register adapters
		ParameterizedType<ParameterType1> parameterType1 = new ParameterizedType<>();
		ParameterizedType<ParameterType2> parameterType2 = new ParameterizedType<>();

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
	public void retrievalOfParameterizedType() {
		AdaptableSupportWrapper td = new AdaptableSupportWrapper();

		ParameterizedSubType<ParameterType1> adapter = new ParameterizedSubType<>();

		TypeToken<ParameterizedSuperType<ParameterType1>> superType = new TypeToken<ParameterizedSuperType<ParameterType1>>() {
		};
		TypeToken<ParameterizedSubType<ParameterType1>> actualType = new TypeToken<ParameterizedSubType<ParameterType1>>() {
		};

		// register adapter via actual type
		td.setAdapter(actualType, adapter);

		// retrieve via actual type
		assertEquals(adapter, td.getAdapter(actualType));

		// retrieve via super type
		assertEquals(adapter, td.getAdapter(superType));

		// retrieve via wildcard actual type
		assertEquals(adapter,
				td.getAdapter(new TypeToken<ParameterizedSubType<?>>() {
				}));

		// retrieve via wildcard super type
		assertEquals(adapter,
				td.getAdapter(new TypeToken<ParameterizedSuperType<?>>() {
				}));

		// retrieve via raw actual type
		assertEquals(adapter, td.getAdapter(ParameterizedSubType.class));

		// retrieve via raw super type
		assertEquals(adapter, td.getAdapter(ParameterizedSuperType.class));
	}
}
