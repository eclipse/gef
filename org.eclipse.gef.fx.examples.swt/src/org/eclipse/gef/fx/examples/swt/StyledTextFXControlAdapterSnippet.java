/*******************************************************************************
 * Copyright (c) 2013, 2016 itemis AG and others.
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
package org.eclipse.gef.fx.examples.swt;

import org.eclipse.gef.fx.swt.controls.FXControlAdapter;
import org.eclipse.gef.fx.swt.controls.FXControlAdapter.IControlFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class StyledTextFXControlAdapterSnippet extends AbstractFxSwtExample {

	private static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur\nadipisicing elit, sed do eiusmod tempor\nincididunt ut labore et dolore magna\naliqua. Ut enim ad minim veniam,\nquis nostrud exercitation ullamco laboris nisi\nut aliquip ex ea commodo consequat.\nDuis aute irure dolor in reprehenderit\nin voluptate velit esse cillum dolore\neu fugiat nulla pariatur. Excepteur sint\noccaecat cupidatat non proident, sunt\nin culpa qui officia deserunt mollit\nanim id est laborum.";

	public static void main(String[] args) {
		new StyledTextFXControlAdapterSnippet();
	}

	public StyledTextFXControlAdapterSnippet() {
		super("FXControlAdapter Example (StyledText)");
	}

	private void colorAction(Node actionNode,
			final FXControlAdapter<StyledText> stNode, final int fgSwtColorId,
			final int bgSwtColorId) {
		actionNode.addEventHandler(ActionEvent.ACTION,
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						StyledText st = stNode.getControl();
						if (st.getSelectionText().equals("")) {
							return;
						}
						Point selRange = st.getSelectionRange();
						StyleRange styleRange = new StyleRange();
						styleRange.start = selRange.x;
						styleRange.length = selRange.y;
						Display display = st.getDisplay();
						styleRange.foreground = display
								.getSystemColor(fgSwtColorId);
						styleRange.background = display
								.getSystemColor(bgSwtColorId);
						st.setStyleRange(styleRange);
					}
				});
	}

	@Override
	public Scene createScene() {
		// create layout panes
		VBox vbox = new VBox();

		AnchorPane toolBarPane = new AnchorPane();
		vbox.getChildren().add(toolBarPane);

		AnchorPane stPane = new AnchorPane();
		vbox.getChildren().add(stPane);
		VBox.setVgrow(stPane, Priority.ALWAYS);

		// create toolbar
		Button whiteBlackButton = new Button("white/black");
		Button yellowGrayButton = new Button("yellow/gray");
		Button boldButton = new Button("Bold");
		Button italicButton = new Button("Italic");
		Button underlineButton = new Button("Underline");
		Button clearStyleButton = new Button("Clear style");

		// create styled text
		final FXControlAdapter<StyledText> stNode = new FXControlAdapter<>(
				new IControlFactory<StyledText>() {
					@Override
					public StyledText createControl(Composite canvas) {
						return new StyledText(canvas, SWT.BORDER);
					}
				});
		stNode.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

		FXControlAdapter<org.eclipse.swt.widgets.Button> loremIpsumButton = new FXControlAdapter<>(
				new IControlFactory<org.eclipse.swt.widgets.Button>() {

					@Override
					public org.eclipse.swt.widgets.Button createControl(
							Composite canvas) {
						org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button(
								canvas, SWT.PUSH);
						b.setText("Lorem Ipsum");
						b.addSelectionListener(new SelectionListener() {
							@Override
							public void widgetDefaultSelected(
									SelectionEvent e) {
							}

							@Override
							public void widgetSelected(SelectionEvent e) {
								stNode.getControl().setText(LOREM_IPSUM);
							}
						});
						return b;
					}
				});
		FXControlAdapter<org.eclipse.swt.widgets.Button> newButton = new FXControlAdapter<>(
				new IControlFactory<org.eclipse.swt.widgets.Button>() {

					@Override
					public org.eclipse.swt.widgets.Button createControl(
							Composite canvas) {
						org.eclipse.swt.widgets.Button b = new org.eclipse.swt.widgets.Button(
								canvas, SWT.PUSH);
						b.setText("New");
						b.addSelectionListener(new SelectionListener() {
							@Override
							public void widgetDefaultSelected(
									SelectionEvent e) {
							}

							@Override
							public void widgetSelected(SelectionEvent e) {
								stNode.getControl().setText("");
							}
						});
						return b;
					}
				});
		ToolBar toolBar = new ToolBar(whiteBlackButton, yellowGrayButton,
				new Separator(), boldButton, italicButton, underlineButton,
				clearStyleButton, new Separator(), loremIpsumButton, newButton);

		// layout toolbar
		toolBarPane.getChildren().add(toolBar);
		AnchorPane.setTopAnchor(toolBar, 10d);
		AnchorPane.setLeftAnchor(toolBar, 10d);
		AnchorPane.setRightAnchor(toolBar, 10d);

		// layout styled text
		stPane.getChildren().add(stNode);
		AnchorPane.setTopAnchor(stNode, 10d);
		AnchorPane.setLeftAnchor(stNode, 10d);
		AnchorPane.setRightAnchor(stNode, 10d);
		AnchorPane.setBottomAnchor(stNode, 10d);

		// add behavior
		colorAction(whiteBlackButton, stNode, SWT.COLOR_WHITE, SWT.COLOR_BLACK);
		colorAction(yellowGrayButton, stNode, SWT.COLOR_DARK_YELLOW,
				SWT.COLOR_GRAY);
		styleAction(boldButton, stNode, SWT.BOLD, false);
		styleAction(italicButton, stNode, SWT.ITALIC, false);
		styleAction(underlineButton, stNode, SWT.NORMAL, true);
		styleAction(clearStyleButton, stNode, SWT.NORMAL, false);

		return new Scene(vbox, 800, 600);
	}

	private void styleAction(Node actionNode,
			final FXControlAdapter<StyledText> stNode, final int fontStyle,
			final boolean underline) {
		actionNode.addEventHandler(ActionEvent.ACTION,
				new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						StyledText st = stNode.getControl();
						if (st.getSelectionText().equals("")) {
							return;
						}
						Point selRange = st.getSelectionRange();
						StyleRange styleRange = new StyleRange();
						styleRange.start = selRange.x;
						styleRange.length = selRange.y;
						styleRange.fontStyle = fontStyle;
						styleRange.underline = underline;
						st.setStyleRange(styleRange);
					}
				});
	}
}
