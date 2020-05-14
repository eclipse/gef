/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.mvc.tests.fx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.gef.mvc.fx.parts.AbstractVisualPart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.junit.Assert;
import org.junit.Test;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public class AbstractVisualPartTests {

	public class AbstractVisualPartStub extends AbstractVisualPart<Node> {

		@Override
		protected void doAddChildVisual(IVisualPart<? extends Node> child, int index) {
		}

		@Override
		protected Node doCreateVisual() {
			return null;
		}

		@Override
		protected void doRefreshVisual(Node visual) {
		}

		@Override
		protected void doRemoveChildVisual(IVisualPart<? extends Node> child, int index) {
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
	public void testAddChild() {
		AbstractVisualPart<? extends Object> parent = new AbstractVisualPartStub();
		ListChangeExpector<IVisualPart<? extends Object>> listChangeListener = new ListChangeExpector<>(
				parent.getChildrenUnmodifiable());
		parent.getChildrenUnmodifiable().addListener(listChangeListener);
		AbstractVisualPartStub child1 = new AbstractVisualPartStub();
		AbstractVisualPartStub child2 = new AbstractVisualPartStub();
		Assert.assertEquals(0, parent.getChildrenUnmodifiable().size());
		// check that first child is properly added and that a valid
		// property change event is fired.
		listChangeListener.addExpectation(Collections.<IVisualPart<? extends Object>>emptyList(),
				Collections.<IVisualPart<? extends Object>>singletonList(child1));
		parent.addChild(child1);
		Assert.assertEquals(1, parent.getChildrenUnmodifiable().size());
		listChangeListener.check();
		// check that second child is properly added and that a valid
		// property change event is fired.
		listChangeListener.addExpectation(Collections.<IVisualPart<? extends Object>>emptyList(),
				Collections.<IVisualPart<? extends Object>>singletonList(child2));
		parent.addChild(child2);
		listChangeListener.check();
		Assert.assertEquals(2, parent.getChildrenUnmodifiable().size());
	}

	@Test
	public void testRemoveChild() {
		AbstractVisualPartStub parent = new AbstractVisualPartStub();
		AbstractVisualPartStub child1 = new AbstractVisualPartStub();
		AbstractVisualPartStub child2 = new AbstractVisualPartStub();
		parent.addChild(child1);
		parent.addChild(child2);
		Assert.assertEquals(2, parent.getChildrenUnmodifiable().size());

		ListChangeExpector<IVisualPart<? extends Object>> listChangeListener = new ListChangeExpector<>(
				parent.getChildrenUnmodifiable());
		parent.getChildrenUnmodifiable().addListener(listChangeListener);
		// check that second child is properly removed and a valid property
		// change event is fired.
		listChangeListener.addExpectation(Collections.<IVisualPart<? extends Object>>singletonList(child2),
				Collections.<IVisualPart<? extends Object>>emptyList());
		parent.removeChild(child2);
		listChangeListener.check();
		Assert.assertEquals(1, parent.getChildrenUnmodifiable().size());

		// check that first child is properly removed and a valid property
		// change event is fired.
		listChangeListener.addExpectation(Collections.<IVisualPart<? extends Object>>singletonList(child1),
				Collections.<IVisualPart<? extends Object>>emptyList());
		parent.removeChild(child1);
		listChangeListener.check();
		Assert.assertEquals(0, parent.getChildrenUnmodifiable().size());
	}

	@Test
	public void testReorderChild() {
		AbstractVisualPartStub parent = new AbstractVisualPartStub();
		AbstractVisualPartStub child1 = new AbstractVisualPartStub();
		AbstractVisualPartStub child2 = new AbstractVisualPartStub();
		AbstractVisualPartStub child3 = new AbstractVisualPartStub();
		parent.addChild(child1);
		parent.addChild(child2);
		parent.addChild(child3);
		Assert.assertEquals(3, parent.getChildrenUnmodifiable().size());

		// check that second child is properly removed and a valid property
		// change event is fired.
		ListChangeExpector<IVisualPart<? extends Object>> listChangeListener = new ListChangeExpector<>(
				parent.getChildrenUnmodifiable());
		parent.getChildrenUnmodifiable().addListener(listChangeListener);
		listChangeListener.addExpectation(Collections.<IVisualPart<? extends Object>>singletonList(child2),
				Collections.<IVisualPart<? extends Object>>emptyList());
		listChangeListener.addExpectation(Collections.<IVisualPart<? extends Object>>emptyList(),
				Collections.<IVisualPart<? extends Object>>singletonList(child2));
		parent.reorderChild(child2, 2);
		listChangeListener.check();
		Assert.assertEquals(3, parent.getChildrenUnmodifiable().size());
	}
}
