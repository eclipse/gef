/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui.actions;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.actions.ActionGroup;

/**
 *
 * @author mwienand
 *
 */
public class ScrollActionGroup extends ActionGroup {

	private ScrollCenterAction scrollCenter = new ScrollCenterAction();
	private ScrollTopLeftAction scrollTopLeft = new ScrollTopLeftAction();
	private ScrollTopRightAction scrollTopRight = new ScrollTopRightAction();
	private ScrollBottomRightAction scrollBottomRight = new ScrollBottomRightAction();
	private ScrollBottomLeftAction scrollBottomLeft = new ScrollBottomLeftAction();

	/**
	 *
	 */
	public ScrollActionGroup() {
	}

	@Override
	public void dispose() {
		if (scrollCenter != null) {
			scrollCenter.dispose();
			scrollCenter = null;
		}
		if (scrollTopLeft != null) {
			scrollTopLeft.dispose();
			scrollTopLeft = null;
		}
		if (scrollTopRight != null) {
			scrollTopRight.dispose();
			scrollTopRight = null;
		}
		if (scrollBottomRight != null) {
			scrollBottomRight.dispose();
			scrollBottomRight = null;
		}
		if (scrollBottomLeft != null) {
			scrollBottomLeft.dispose();
			scrollBottomLeft = null;
		}
		super.dispose();
	}

	@Override
	public void fillActionBars(org.eclipse.ui.IActionBars actionBars) {
		IToolBarManager tbm = actionBars.getToolBarManager();
		tbm.add(scrollCenter);
		IMenuCreator menuCreator = new IMenuCreator() {
			private Menu menu;
			private ActionContributionItem topLeftItem;
			private ActionContributionItem topRightItem;
			private ActionContributionItem bottomRightItem;
			private ActionContributionItem bottomLeftItem;

			@Override
			public void dispose() {
				if (menu != null) {
					topLeftItem.dispose();
					topLeftItem = null;
					topRightItem.dispose();
					topRightItem = null;
					bottomRightItem.dispose();
					bottomRightItem = null;
					bottomLeftItem.dispose();
					bottomLeftItem = null;
					menu.dispose();
					menu = null;
				}
			}

			private void fillMenu(Menu menu) {
				topLeftItem = new ActionContributionItem(scrollTopLeft);
				topLeftItem.fill(menu, -1);
				topRightItem = new ActionContributionItem(scrollTopRight);
				topRightItem.fill(menu, -1);
				bottomRightItem = new ActionContributionItem(scrollBottomRight);
				bottomRightItem.fill(menu, -1);
				bottomLeftItem = new ActionContributionItem(scrollBottomLeft);
				bottomLeftItem.fill(menu, -1);
			}

			@Override
			public Menu getMenu(Control parent) {
				if (menu == null) {
					menu = new Menu(parent);
					fillMenu(menu);
				}
				return menu;
			}

			@Override
			public Menu getMenu(Menu parent) {
				if (menu == null) {
					menu = new Menu(parent);
					fillMenu(menu);
				}
				return menu;
			}
		};
		scrollCenter.setMenuCreator(menuCreator);
	}
}
