/*******************************************************************************
 * Copyright (c) 2017 itemis AG and others.
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
package org.eclipse.gef.mvc.fx.ui.actions;

import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.common.adapt.IAdaptable;
import org.eclipse.gef.mvc.fx.ui.MvcFxUiBundle;
import org.eclipse.gef.mvc.fx.viewer.IViewer;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;

/**
 * The {@link ScrollActionGroup} is an {@link AbstractViewerActionGroup} that
 * combines the {@link ScrollCenterAction}, {@link ScrollTopLeftAction},
 * {@link ScrollTopRightAction}, {@link ScrollBottomRightAction}, and
 * {@link ScrollBottomLeftAction}. Only the {@link ScrollCenterAction} is
 * inserted into the tool bar when this action group contributes to the UI. The
 * other actions are put in a drop down menu that is rooted at the
 * {@link ScrollCenterAction}.
 *
 * @author mwienand
 *
 */
public class ScrollActionGroup extends AbstractViewerActionGroup {

	private ScrollCenterAction scrollCenterDropDownAction = new ScrollCenterAction(
			"Scroll Center", IAction.AS_DROP_DOWN_MENU,
			MvcFxUiBundle.getDefault().getImageRegistry()
					.getDescriptor(MvcFxUiBundle.IMG_ICONS_SCROLL_CENTER)) {
	};
	private ScrollCenterAction scrollCenterAction = new ScrollCenterAction();
	private ScrollTopLeftAction scrollTopLeftAction = new ScrollTopLeftAction();
	private ScrollTopRightAction scrollTopRightAction = new ScrollTopRightAction();
	private ScrollBottomRightAction scrollBottomRightAction = new ScrollBottomRightAction();
	private ScrollBottomLeftAction scrollBottomLeftAction = new ScrollBottomLeftAction();

	@Override
	public List<IAdaptable.Bound<IViewer>> createContributions() {
		return Arrays.asList(scrollCenterDropDownAction, scrollCenterAction,
				scrollTopLeftAction, scrollTopRightAction,
				scrollBottomRightAction, scrollBottomLeftAction);
	}

	@Override
	public void fillActionBars(org.eclipse.ui.IActionBars actionBars) {
		IToolBarManager tbm = actionBars.getToolBarManager();
		tbm.add(scrollCenterDropDownAction);
		// TODO: Review to ensure that disposal is correct.
		IMenuCreator menuCreator = new IMenuCreator() {
			private Menu menu;
			private ActionContributionItem centerItem;
			private ActionContributionItem topLeftItem;
			private ActionContributionItem topRightItem;
			private ActionContributionItem bottomRightItem;
			private ActionContributionItem bottomLeftItem;

			@Override
			public void dispose() {
				if (menu != null) {
					centerItem.dispose();
					centerItem = null;
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
				centerItem = new ActionContributionItem(scrollCenterAction);
				centerItem.fill(menu, -1);
				topLeftItem = new ActionContributionItem(scrollTopLeftAction);
				topLeftItem.fill(menu, -1);
				topRightItem = new ActionContributionItem(scrollTopRightAction);
				topRightItem.fill(menu, -1);
				bottomRightItem = new ActionContributionItem(
						scrollBottomRightAction);
				bottomRightItem.fill(menu, -1);
				bottomLeftItem = new ActionContributionItem(
						scrollBottomLeftAction);
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
		scrollCenterDropDownAction.setMenuCreator(menuCreator);
	}
}
