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

	private class ParameterType1 extends Object {

	}

	private class ParameterType2 extends Object {

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
	public void polymorphicRetrieval() {
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
}
