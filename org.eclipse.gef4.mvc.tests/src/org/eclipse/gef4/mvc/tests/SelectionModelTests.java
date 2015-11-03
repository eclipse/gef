/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.tests;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.AbstractContentPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests proper behavior of the {@link SelectionModel}.
 *
 * @author anyssen
 *
 */
public class SelectionModelTests {

	private class ContentPartStub extends AbstractContentPart<Object, Object> {

		@Override
		protected Object createVisual() {
			return null;
		}

		@Override
		protected void doRefreshVisual(Object visual) {
		}
	}

	@Test
	public void testAppend() {
		SelectionModel<Object> selectionModel = new SelectionModel<Object>();
		IContentPart<Object, Object> c1 = new ContentPartStub();
		IContentPart<Object, Object> c2 = new ContentPartStub();
		IContentPart<Object, Object> c3 = new ContentPartStub();
		selectionModel.appendToSelection(c3);
		Assert.assertEquals(Arrays.asList(c3), selectionModel.getSelection());
		selectionModel.appendToSelection(c2);
		Assert.assertEquals(Arrays.asList(c3, c2), selectionModel.getSelection());
		selectionModel.appendToSelection(c1);
		Assert.assertEquals(Arrays.asList(c3, c2, c1), selectionModel.getSelection());
		selectionModel.appendToSelection(c3);
		Assert.assertEquals(Arrays.asList(c2, c1, c3), selectionModel.getSelection());
		selectionModel.appendToSelection(c2);
		Assert.assertEquals(Arrays.asList(c1, c3, c2), selectionModel.getSelection());
		selectionModel.appendToSelection(Arrays.asList(c1, c2));
		Assert.assertEquals(Arrays.asList(c3, c1, c2), selectionModel.getSelection());
		try {
			selectionModel.appendToSelection(Arrays.asList(c1, c2, c1));
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// expect that an IllegalArgumentException is thrown if a selection
			// list contains duplicates
		}
	}

	@Test
	public void testDeselect() {
		SelectionModel<Object> selectionModel = new SelectionModel<Object>();
		IContentPart<Object, Object> c1 = new ContentPartStub();
		IContentPart<Object, Object> c2 = new ContentPartStub();
		IContentPart<Object, Object> c3 = new ContentPartStub();
		selectionModel.setSelection(Arrays.asList(c1, c2, c3));
		Assert.assertEquals(Arrays.asList(c1, c2, c3), selectionModel.getSelection());
		selectionModel.removeFromSelection(c2);
		Assert.assertEquals(Arrays.asList(c1, c3), selectionModel.getSelection());

		selectionModel.removeFromSelection(c3);
		Assert.assertEquals(Arrays.asList(c1), selectionModel.getSelection());

		// deselect c3 again; the selection of c1 should be preserved
		selectionModel.removeFromSelection(c3);
		Assert.assertEquals(Arrays.asList(c1), selectionModel.getSelection());

		selectionModel.removeFromSelection(c1);
		Assert.assertEquals(Collections.emptyList(), selectionModel.getSelection());
	}

	@Test
	public void testDeselectAll() {
		SelectionModel<Object> selectionModel = new SelectionModel<Object>();
		IContentPart<Object, Object> c1 = new ContentPartStub();
		IContentPart<Object, Object> c2 = new ContentPartStub();
		IContentPart<Object, Object> c3 = new ContentPartStub();
		selectionModel.setSelection(Arrays.asList(c1, c2, c3));
		selectionModel.clearSelection();
		Assert.assertEquals(Collections.emptyList(), selectionModel.getSelection());
	}

	@Test
	public void testPrepend() {
		SelectionModel<Object> selectionModel = new SelectionModel<Object>();
		IContentPart<Object, Object> c1 = new ContentPartStub();
		IContentPart<Object, Object> c2 = new ContentPartStub();
		IContentPart<Object, Object> c3 = new ContentPartStub();
		selectionModel.prependToSelection(c3);
		Assert.assertEquals(Arrays.asList(c3), selectionModel.getSelection());
		selectionModel.prependToSelection(c2);
		Assert.assertEquals(Arrays.asList(c2, c3), selectionModel.getSelection());
		selectionModel.prependToSelection(c1);
		Assert.assertEquals(Arrays.asList(c1, c2, c3), selectionModel.getSelection());
		selectionModel.prependToSelection(c3);
		Assert.assertEquals(Arrays.asList(c3, c1, c2), selectionModel.getSelection());
		selectionModel.prependToSelection(c2);
		Assert.assertEquals(Arrays.asList(c2, c3, c1), selectionModel.getSelection());
		selectionModel.prependToSelection(Arrays.asList(c1, c2));
		Assert.assertEquals(Arrays.asList(c1, c2, c3), selectionModel.getSelection());
		try {
			selectionModel.prependToSelection(Arrays.asList(c1, c2, c1));
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// expect that an IllegalArgumentException is thrown if a selection
			// list contains duplicates
		}
	}

	@Test
	public void testPropertyChangeEvents() {
		SelectionModel<Object> selectionModel = new SelectionModel<Object>();
		IContentPart<Object, Object> c1 = new ContentPartStub();
		IContentPart<Object, Object> c2 = new ContentPartStub();
		IContentPart<Object, Object> c3 = new ContentPartStub();
		final List<PropertyChangeEvent> events = new ArrayList<PropertyChangeEvent>();
		PropertyChangeListener listener = new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				events.add(evt);
			}
		};
		selectionModel.addPropertyChangeListener(listener);

		// ensure multi select raises a single event only
		selectionModel.setSelection(Arrays.asList(c1, c2, c3));
		Assert.assertEquals(1, events.size());
		SelectionModel.SELECTION_PROPERTY.equals(events.get(0).getPropertyName());
		Assert.assertEquals(Collections.emptyList(), events.get(0).getOldValue());
		Assert.assertEquals(Arrays.asList(c1, c2, c3), events.get(0).getNewValue());
		events.clear();
		selectionModel.setSelection(Arrays.asList(c1, c2, c3));
		Assert.assertEquals(0, events.size());

		selectionModel.clearSelection();
		Assert.assertEquals(1, events.size());
		SelectionModel.SELECTION_PROPERTY.equals(events.get(0).getPropertyName());
		Assert.assertEquals(Arrays.asList(c1, c2, c3), events.get(0).getOldValue());
		Assert.assertEquals(Collections.emptyList(), events.get(0).getNewValue());
		events.clear();

		// ensure multi append raises a single event only
		selectionModel.appendToSelection(Arrays.asList(c1, c2, c3));
		Assert.assertEquals(1, events.size());
		SelectionModel.SELECTION_PROPERTY.equals(events.get(0).getPropertyName());
		Assert.assertEquals(Collections.emptyList(), events.get(0).getOldValue());
		Assert.assertEquals(Arrays.asList(c1, c2, c3), events.get(0).getNewValue());
		selectionModel.clearSelection();
		events.clear();

		// ensure multi prepend raises a single event only
		selectionModel.prependToSelection(Arrays.asList(c1, c2, c3));
		Assert.assertEquals(1, events.size());
		SelectionModel.SELECTION_PROPERTY.equals(events.get(0).getPropertyName());
		Assert.assertEquals(Collections.emptyList(), events.get(0).getOldValue());
		Assert.assertEquals(Arrays.asList(c1, c2, c3), events.get(0).getNewValue());
		events.clear();

		selectionModel.removeFromSelection(c2);
		Assert.assertEquals(1, events.size());
		SelectionModel.SELECTION_PROPERTY.equals(events.get(0).getPropertyName());
		Assert.assertEquals(Arrays.asList(c1, c2, c3), events.get(0).getOldValue());
		Assert.assertEquals(Arrays.asList(c1, c3), events.get(0).getNewValue());
		events.clear();

		// ensure listener is properly de-registered
		selectionModel.removePropertyChangeListener(listener);
		selectionModel.removeFromSelection(c3);
		Assert.assertEquals(0, events.size());
	}

	@Test
	public void testSelect() {
		SelectionModel<Object> selectionModel = new SelectionModel<Object>();
		IContentPart<Object, Object> c1 = new ContentPartStub();
		IContentPart<Object, Object> c2 = new ContentPartStub();
		IContentPart<Object, Object> c3 = new ContentPartStub();
		selectionModel.setSelection(Arrays.asList(c1, c2));
		Assert.assertEquals(Arrays.asList(c1, c2), selectionModel.getSelection());
		Assert.assertTrue(selectionModel.isSelected(c1));
		Assert.assertTrue(selectionModel.isSelected(c2));
		selectionModel.setSelection(c3);
		Assert.assertEquals(Arrays.asList(c3), selectionModel.getSelection());
		Assert.assertTrue(selectionModel.isSelected(c3));
		try {
			selectionModel.setSelection(Arrays.asList(c1, c2, c1));
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// expect that an IllegalArgumentException is thrown if a selection
			// list contains duplicates
		}
	}

}
