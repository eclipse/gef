/*******************************************************************************
 * Copyright (c) 2014, 2016 itemis AG and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.tests.fx;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.common.reflect.ReflectionUtils;
import org.eclipse.gef.fx.listeners.VisualChangeListener;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.AbstractHandlePart;
import org.eclipse.gef.mvc.fx.parts.IVisualPart;
import org.eclipse.gef.mvc.fx.parts.LayeredRootPart;
import org.eclipse.gef.mvc.tests.fx.rules.FXNonApplicationThreadRule;
import org.junit.Rule;
import org.junit.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

import javafx.scene.Group;
import javafx.scene.Node;

/**
 * Tests for {@link AbstractHandlePart}.
 *
 * @author anyssen
 *
 */
public class AbstractHandlePartTests {

	/**
	 * Ensure the JavaFX toolkit is properly initialized.
	 */
	@Rule
	public FXNonApplicationThreadRule ctx = new FXNonApplicationThreadRule();

	// TODO: use injection
	private LayeredRootPart rp = new LayeredRootPart() {

		@Override
		protected void doAddChildVisual(IVisualPart<? extends Node> child, int index) {
			getVisual().getChildren().add(index, child.getVisual());
		}

		@Override
		protected Group doCreateVisual() {
			return new Group();
		}

		@Override
		public void doRefreshVisual(Group visual) {
			// nothing to do
		};

		@Override
		protected void doRemoveChildVisual(IVisualPart<? extends Node> child, int index) {
			getVisual().getChildren().remove(index);
		};
	};

	private AbstractContentPart<Node> cp = new AbstractContentPart<Node>() {
		@Override
		protected Node doCreateVisual() {
			return new Group();
		}

		@Override
		protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
			return HashMultimap.create();
		}

		@Override
		protected List<? extends Object> doGetContentChildren() {
			return Collections.emptyList();
		}

		@Override
		protected void doRefreshVisual(Node visual) {
			// nothing to do
		}
	};

	private AbstractHandlePart<Node> hp = new AbstractHandlePart<Node>() {

		@Override
		protected Node doCreateVisual() {
			return new Group();
		}

		@Override
		protected void doRefreshVisual(Node visual) {
			// nothing to do
		}
	};

	protected Map<IVisualPart<? extends Node>, Integer> getAnchorageLinkCount(AbstractHandlePart<Node> hp) {
		return ReflectionUtils.getPrivateFieldValue(hp, "anchorageLinkCount");
	}

	protected Map<IVisualPart<? extends Node>, VisualChangeListener> getVisualChangeListeners(
			AbstractHandlePart<Node> hp) {
		return ReflectionUtils.getPrivateFieldValue(hp, "visualChangeListeners");
	}

	/**
	 * Tests that an {@link AbstractHandlePart} only registers a single
	 * {@link VisualChangeListener} per anchorage (even if there are several
	 * anchorage links with different roles), and that this listener is not
	 * removed before all links are removed again.
	 */
	@Test
	public void testProperRegistrationOfVisualChangeListeners() {
		// ensure the visual of both parts have a common ancestor (the
		// root part
		// visual)
		rp.addChild(cp);
		rp.addChild(hp);
		assertNull(getAnchorageLinkCount(hp).get(cp));
		assertEquals(0, getVisualChangeListeners(hp).size());
		// check we have a single visual change listener after anchoring
		// the handle part
		hp.attachToAnchorage(cp, "r1");
		assertEquals(1, getAnchorageLinkCount(hp).get(cp).intValue());
		assertEquals(1, getVisualChangeListeners(hp).size());
		// check we still have only a single change listener after
		// anchoring the
		// same handle part with a different role
		hp.attachToAnchorage(cp, "r2");
		assertEquals(2, getAnchorageLinkCount(hp).get(cp).intValue());
		assertEquals(1, getVisualChangeListeners(hp).size());
		// check we still have a visual change listener, even if one
		// anchorage
		// is removed
		hp.detachFromAnchorage(cp, "r2");
		assertEquals(1, getAnchorageLinkCount(hp).get(cp).intValue());
		assertEquals(1, getVisualChangeListeners(hp).size());
		// ensure no visual change listener is registered any more
		hp.detachFromAnchorage(cp, "r1");
		assertNull(getAnchorageLinkCount(hp).get(cp));
		assertEquals(0, getVisualChangeListeners(hp).size());
		// re-attach and assure the map is intialized again
		hp.attachToAnchorage(cp, "r1");
		assertEquals(1, getAnchorageLinkCount(hp).get(cp).intValue());
		assertEquals(1, getVisualChangeListeners(hp).size());
	}

}
