/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.mvc.fx.tests;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import javafx.scene.Group;
import javafx.scene.Node;

import org.eclipse.gef4.common.reflect.ReflectionUtils;
import org.eclipse.gef4.fx.listeners.VisualChangeListener;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXContentPart;
import org.eclipse.gef4.mvc.fx.parts.AbstractFXHandlePart;
import org.eclipse.gef4.mvc.fx.parts.FXRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;
import org.junit.Test;

/**
 * Tests for {@link AbstractFXHandlePart}.
 * 
 * @author anyssen
 *
 */
public class AbstractFXHandlePartTests {

	private FXRootPart rp = new FXRootPart();

	private AbstractFXContentPart<Node> cp = new AbstractFXContentPart<Node>() {
		@Override
		protected Node createVisual() {
			return new Group();
		}

		@Override
		protected void doRefreshVisual(Node visual) {
			// nothing to do
		}
	};

	private AbstractFXHandlePart<Node> hp = new AbstractFXHandlePart<Node>() {

		@Override
		protected Node createVisual() {
			return new Group();
		}

		@Override
		protected void doRefreshVisual(Node visual) {
			// nothing to do
		}
	};

	/**
	 * Tests that an {@link AbstractFXHandlePart} only registers a single
	 * {@link VisualChangeListener} per anchorage (even if there are several
	 * anchorage links with different roles), and that this listener is not
	 * removed before all links are removed again.
	 */
	@Test
	public void testProperRegistrationOfVisualChangeListeners() {
		// ensure the visual of both parts have a common ancestor (the root part
		// visual)
		rp.addChild(cp);
		rp.addChild(hp);
		assertEquals(0, getVisualChangeListeners(hp).size());

		// check we have a single visual change listener after anchoring the
		// handle part
		hp.addAnchorage(cp, "r1");
		assertEquals(1, getVisualChangeListeners(hp).size());
		// check we still have only a single change listener after anchoring the
		// same handle part with a different role
		hp.addAnchorage(cp, "r2");
		assertEquals(1, getVisualChangeListeners(hp).size());
		// check we still have a visual change listener, even if one anchorage
		// is removed
		hp.removeAnchorage(cp, "r2");
		assertEquals(1, getVisualChangeListeners(hp).size());
		// ensure no visual change listener is registered any more
		hp.removeAnchorage(cp, "r1");
		assertEquals(0, getVisualChangeListeners(hp).size());
	}

	protected Map<IVisualPart<Node, ? extends Node>, VisualChangeListener> getVisualChangeListeners(
			AbstractFXHandlePart<Node> hp) {
		return ReflectionUtils.getPrivateFieldValue(hp, "visualChangeListeners");
	}

}
