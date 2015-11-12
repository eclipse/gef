package org.eclipse.gef4.mvc.tests;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef4.mvc.parts.AbstractVisualPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.junit.Assert;
import org.junit.Test;

public class AbstractVisualPartTests {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public class AbstractVisualPartStub extends AbstractVisualPart {

		@Override
		protected void addChildVisual(IVisualPart child, int index) {
		}

		@Override
		protected Object createVisual() {
			return null;
		}

		@Override
		protected void doRefreshVisual(Object visual) {
		}

		@Override
		protected void removeChildVisual(IVisualPart child, int index) {
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddChild() {
		final List<PropertyChangeEvent> events = new ArrayList<PropertyChangeEvent>();
		AbstractVisualPart<?, ?> parent = new AbstractVisualPartStub();
		AbstractVisualPartStub child1 = new AbstractVisualPartStub();
		AbstractVisualPartStub child2 = new AbstractVisualPartStub();
		parent.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				events.add(evt);
			}
		});
		Assert.assertEquals(0, parent.getChildren().size());
		Assert.assertEquals(0, events.size());
		// check that first child is properly added and that a valid
		// property change event is fired.
		parent.addChild(child1);
		Assert.assertEquals(1, parent.getChildren().size());
		Assert.assertEquals(1, events.size());
		Assert.assertEquals(IVisualPart.CHILDREN_PROPERTY, events.get(0).getPropertyName());
		Assert.assertEquals(Collections.emptyList(), events.get(0).getOldValue());
		Assert.assertEquals(Arrays.asList(new AbstractVisualPartStub[] { child1 }), events.get(0).getNewValue());
		// check that second child is properly added and that a valid
		// property change event is fired.
		parent.addChild(child2);
		Assert.assertEquals(2, parent.getChildren().size());
		Assert.assertEquals(2, events.size());
		Assert.assertEquals(IVisualPart.CHILDREN_PROPERTY, events.get(1).getPropertyName());
		Assert.assertEquals(Arrays.asList(new AbstractVisualPartStub[] { child1 }), events.get(1).getOldValue());
		Assert.assertEquals(Arrays.asList(new AbstractVisualPartStub[] { child1, child2 }),
				events.get(1).getNewValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveChild() {
		final List<PropertyChangeEvent> events = new ArrayList<PropertyChangeEvent>();
		AbstractVisualPartStub parent = new AbstractVisualPartStub();
		AbstractVisualPartStub child1 = new AbstractVisualPartStub();
		AbstractVisualPartStub child2 = new AbstractVisualPartStub();
		parent.addChild(child1);
		parent.addChild(child2);
		parent.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				events.add(evt);
			}
		});
		Assert.assertEquals(2, parent.getChildren().size());
		Assert.assertEquals(0, events.size());
		// check that second child is properly removed and a valid property
		// change event is fired.
		parent.removeChild(child2);
		Assert.assertEquals(1, parent.getChildren().size());
		Assert.assertEquals(1, events.size());
		Assert.assertEquals(IVisualPart.CHILDREN_PROPERTY, events.get(0).getPropertyName());
		Assert.assertEquals(Arrays.asList(new AbstractVisualPartStub[] { child1, child2 }),
				events.get(0).getOldValue());
		Assert.assertEquals(Arrays.asList(new AbstractVisualPartStub[] { child1 }), events.get(0).getNewValue());
		// check that first child is properly removed and a valid property
		// change event is fired.
		parent.removeChild(child1);
		Assert.assertEquals(0, parent.getChildren().size());
		Assert.assertEquals(2, events.size());
		Assert.assertEquals(IVisualPart.CHILDREN_PROPERTY, events.get(1).getPropertyName());
		Assert.assertEquals(Arrays.asList(new AbstractVisualPartStub[] { child1 }), events.get(1).getOldValue());
		Assert.assertEquals(Collections.emptyList(), events.get(1).getNewValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testReorderChild() {
		final List<PropertyChangeEvent> events = new ArrayList<PropertyChangeEvent>();
		AbstractVisualPartStub parent = new AbstractVisualPartStub();
		AbstractVisualPartStub child1 = new AbstractVisualPartStub();
		AbstractVisualPartStub child2 = new AbstractVisualPartStub();
		AbstractVisualPartStub child3 = new AbstractVisualPartStub();
		parent.addChild(child1);
		parent.addChild(child2);
		parent.addChild(child3);
		parent.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				events.add(evt);
			}
		});
		Assert.assertEquals(3, parent.getChildren().size());
		Assert.assertEquals(0, events.size());
		// check that second child is properly removed and a valid property
		// change event is fired.
		parent.reorderChild(child2, 2);
		Assert.assertEquals(3, parent.getChildren().size());
		Assert.assertEquals(1, events.size());
		Assert.assertEquals(IVisualPart.CHILDREN_PROPERTY, events.get(0).getPropertyName());
		Assert.assertEquals(Arrays.asList(new AbstractVisualPartStub[] { child1, child2, child3 }),
				events.get(0).getOldValue());
		Assert.assertEquals(Arrays.asList(new AbstractVisualPartStub[] { child1, child3, child2 }),
				events.get(0).getNewValue());
	}
}
