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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.gef4.mvc.models.SelectionModel;
import org.eclipse.gef4.mvc.parts.AbstractContentPart;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

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

		@Override
		protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
			return HashMultimap.create();
		}

		@Override
		protected List<? extends Object> doGetContentChildren() {
			return Collections.emptyList();
		}
	}

	private class ListChangeExpector<E> implements ListChangeListener<E> {

		private ObservableList<? extends E> source;
		private LinkedList<List<E>> removedValuesQueue = new LinkedList<>();
		private LinkedList<List<E>> addedValuesQueue = new LinkedList<>();

		public ListChangeExpector(ObservableList<? extends E> source) {
			this.source = source;
		}

		public void addExpectation(List<E> removedValues, List<E> addedValues) {
			// We check that the reference to the observable value is correct,
			// thus do not copy the passed in values.
			removedValuesQueue.addFirst(removedValues);
			addedValuesQueue.addFirst(addedValues);
		}

		public void check() {
			if (removedValuesQueue.size() > 0) {
				fail("Did not receive " + removedValuesQueue.size() + " expected changes.");
			}
		}

		@Override
		public void onChanged(javafx.collections.ListChangeListener.Change<? extends E> c) {
			if (removedValuesQueue.size() <= 0) {
				fail("Received unexpected change.");
			}
			while (c.next()) {
				assertEquals(source, c.getList());
				assertEquals(removedValuesQueue.pollLast(), c.getRemoved());
				assertEquals(addedValuesQueue.pollLast(), c.getAddedSubList());
			}
		}
	}

	@Test
	public void testAppend() {
		SelectionModel<Object> selectionModel = new SelectionModel<>();
		IContentPart<Object, Object> c1 = new ContentPartStub();
		IContentPart<Object, Object> c2 = new ContentPartStub();
		IContentPart<Object, Object> c3 = new ContentPartStub();
		selectionModel.appendToSelection(c3);
		Assert.assertEquals(Arrays.asList(c3), selectionModel.getSelectionUnmodifiable());
		selectionModel.appendToSelection(c2);
		Assert.assertEquals(Arrays.asList(c3, c2), selectionModel.getSelectionUnmodifiable());
		selectionModel.appendToSelection(c1);
		Assert.assertEquals(Arrays.asList(c3, c2, c1), selectionModel.getSelectionUnmodifiable());
		selectionModel.appendToSelection(c3);
		Assert.assertEquals(Arrays.asList(c2, c1, c3), selectionModel.getSelectionUnmodifiable());
		selectionModel.appendToSelection(c2);
		Assert.assertEquals(Arrays.asList(c1, c3, c2), selectionModel.getSelectionUnmodifiable());
		selectionModel.appendToSelection(Arrays.asList(c1, c2));
		Assert.assertEquals(Arrays.asList(c3, c1, c2), selectionModel.getSelectionUnmodifiable());
		try {
			selectionModel.appendToSelection(Arrays.asList(c1, c2, c1));
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// expect that an IllegalArgumentException is thrown if a selection
			// list contains duplicates
		}
	}

	@Test
	public void testChangeEvents() {
		SelectionModel<Object> selectionModel = new SelectionModel<>();
		IContentPart<Object, ? extends Object> c1 = new ContentPartStub();
		IContentPart<Object, ? extends Object> c2 = new ContentPartStub();
		IContentPart<Object, ? extends Object> c3 = new ContentPartStub();
		ListChangeExpector<IContentPart<Object, ? extends Object>> listener = new ListChangeExpector<>(
				selectionModel.getSelectionUnmodifiable());
		selectionModel.getSelectionUnmodifiable().addListener(listener);

		// no change on empty selection
		selectionModel.clearSelection();
		listener.check();

		// ensure multi select raises a single event only
		listener.addExpectation(Collections.<IContentPart<Object, ? extends Object>> emptyList(),
				Arrays.asList(c1, c2, c3));
		selectionModel.setSelection(Arrays.asList(c1, c2, c3));
		listener.check();

		// no event if selection did not change
		selectionModel.setSelection(Arrays.asList(c1, c2, c3));
		listener.check();

		listener.addExpectation(Arrays.asList(c1, c2, c3),
				Collections.<IContentPart<Object, ? extends Object>> emptyList());
		selectionModel.clearSelection();
		listener.check();

		// ensure multi append raises a single event only
		listener.addExpectation(Collections.<IContentPart<Object, ? extends Object>> emptyList(),
				Arrays.asList(c1, c2, c3));
		selectionModel.appendToSelection(Arrays.asList(c1, c2, c3));
		listener.check();

		// no notification if append is without effect
		selectionModel.appendToSelection(Arrays.asList(c1, c2, c3));
		listener.check();

		listener.addExpectation(Arrays.asList(c1, c2, c3),
				Collections.<IContentPart<Object, ? extends Object>> emptyList());
		selectionModel.clearSelection();
		listener.check();

		// ensure multi prepend raises a single event only
		listener.addExpectation(Collections.<IContentPart<Object, ? extends Object>> emptyList(),
				Arrays.asList(c1, c2, c3));
		selectionModel.prependToSelection(Arrays.asList(c1, c2, c3));
		listener.check();

		// no change if prepend is without effect
		selectionModel.prependToSelection(Arrays.asList(c1, c2, c3));
		listener.check();

		listener.addExpectation(Collections.<IContentPart<Object, ? extends Object>> singletonList(c2),
				Collections.<IContentPart<Object, ? extends Object>> emptyList());
		selectionModel.removeFromSelection(c2);
		listener.check();

		// no change if remove is without effect
		selectionModel.removeFromSelection(c2);
		listener.check();

		// ensure listener is properly de-registered
		selectionModel.getSelectionUnmodifiable().removeListener(listener);
		selectionModel.removeFromSelection(c3);
		listener.check();
	}

	@Test
	public void testDeselect() {
		SelectionModel<Object> selectionModel = new SelectionModel<>();
		IContentPart<Object, Object> c1 = new ContentPartStub();
		IContentPart<Object, Object> c2 = new ContentPartStub();
		IContentPart<Object, Object> c3 = new ContentPartStub();
		selectionModel.setSelection(Arrays.asList(c1, c2, c3));
		Assert.assertEquals(Arrays.asList(c1, c2, c3), selectionModel.getSelectionUnmodifiable());
		selectionModel.removeFromSelection(c2);
		Assert.assertEquals(Arrays.asList(c1, c3), selectionModel.getSelectionUnmodifiable());

		selectionModel.removeFromSelection(c3);
		Assert.assertEquals(Arrays.asList(c1), selectionModel.getSelectionUnmodifiable());

		// deselect c3 again; the selection of c1 should be preserved
		selectionModel.removeFromSelection(c3);
		Assert.assertEquals(Arrays.asList(c1), selectionModel.getSelectionUnmodifiable());

		selectionModel.removeFromSelection(c1);
		Assert.assertEquals(Collections.emptyList(), selectionModel.getSelectionUnmodifiable());
	}

	@Test
	public void testDeselectAll() {
		SelectionModel<Object> selectionModel = new SelectionModel<>();
		IContentPart<Object, Object> c1 = new ContentPartStub();
		IContentPart<Object, Object> c2 = new ContentPartStub();
		IContentPart<Object, Object> c3 = new ContentPartStub();
		selectionModel.setSelection(Arrays.asList(c1, c2, c3));
		selectionModel.clearSelection();
		Assert.assertEquals(Collections.emptyList(), selectionModel.getSelectionUnmodifiable());
	}

	@Test
	public void testPrepend() {
		SelectionModel<Object> selectionModel = new SelectionModel<>();
		IContentPart<Object, Object> c1 = new ContentPartStub();
		IContentPart<Object, Object> c2 = new ContentPartStub();
		IContentPart<Object, Object> c3 = new ContentPartStub();
		selectionModel.prependToSelection(c3);
		Assert.assertEquals(Arrays.asList(c3), selectionModel.getSelectionUnmodifiable());
		selectionModel.prependToSelection(c2);
		Assert.assertEquals(Arrays.asList(c2, c3), selectionModel.getSelectionUnmodifiable());
		selectionModel.prependToSelection(c1);
		Assert.assertEquals(Arrays.asList(c1, c2, c3), selectionModel.getSelectionUnmodifiable());
		selectionModel.prependToSelection(c3);
		Assert.assertEquals(Arrays.asList(c3, c1, c2), selectionModel.getSelectionUnmodifiable());
		selectionModel.prependToSelection(c2);
		Assert.assertEquals(Arrays.asList(c2, c3, c1), selectionModel.getSelectionUnmodifiable());
		selectionModel.prependToSelection(Arrays.asList(c1, c2));
		Assert.assertEquals(Arrays.asList(c1, c2, c3), selectionModel.getSelectionUnmodifiable());
		try {
			selectionModel.prependToSelection(Arrays.asList(c1, c2, c1));
			Assert.fail();
		} catch (IllegalArgumentException e) {
			// expect that an IllegalArgumentException is thrown if a selection
			// list contains duplicates
		}
	}

	@Test
	public void testSelect() {
		SelectionModel<Object> selectionModel = new SelectionModel<>();
		IContentPart<Object, Object> c1 = new ContentPartStub();
		IContentPart<Object, Object> c2 = new ContentPartStub();
		IContentPart<Object, Object> c3 = new ContentPartStub();
		selectionModel.setSelection(Arrays.asList(c1, c2));
		Assert.assertEquals(Arrays.asList(c1, c2), selectionModel.getSelectionUnmodifiable());
		Assert.assertTrue(selectionModel.isSelected(c1));
		Assert.assertTrue(selectionModel.isSelected(c2));
		selectionModel.setSelection(c3);
		Assert.assertEquals(Arrays.asList(c3), selectionModel.getSelectionUnmodifiable());
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
