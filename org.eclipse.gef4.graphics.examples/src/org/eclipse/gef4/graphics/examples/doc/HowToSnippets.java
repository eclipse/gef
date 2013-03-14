/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
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
package org.eclipse.gef4.graphics.examples.doc;

import java.util.Random;

import org.eclipse.gef4.geometry.planar.BezierCurve;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.PolyBezier;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.Gradient;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.IImageGraphics;
import org.eclipse.gef4.graphics.color.Color;
import org.eclipse.gef4.graphics.image.Image;
import org.eclipse.gef4.graphics.swt.SwtGraphics;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class HowToSnippets implements PaintListener {

	private static class Snippet {
		private String name = "";

		public Snippet(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void renderScene(IGraphics g) {
		}
	}

	private static final int HEIGHT = 300;

	private static final int WIDTH = 400;

	private static Snippet[] SNIPPETS = new Snippet[] {
			new Snippet("draw geometric primitives") {
				@Override
				public void renderScene(IGraphics graphics) {
					graphics.setDraw(new Color(0, 0, 0)).draw(
							new Line(10, 10, 390, 290));
					graphics.setFill(new Color(255, 0, 0)).fill(
							new Ellipse(130, 15, 50, 50));
					graphics.draw(new Rectangle(55, 150, 200, 40).getOutline());
				}
			}, new Snippet("draw geometric primitives") {
				@Override
				public void renderScene(IGraphics graphics) {
					// connection passes through the given points
					PolyBezier connection = PolyBezier.interpolateCubic(
							new Point(50, 50), new Point(80, 170), new Point(
									320, 150), new Point(350, 250));
					graphics.draw(connection);
				}
			}, new Snippet("draw arbitrary Bezier curves") {
				@Override
				public void renderScene(IGraphics graphics) {
					graphics.draw(new BezierCurve(new Point(300, 25),
							new Point(330, 165), new Point(110, 65), new Point(
									180, 240), new Point(160, 220), new Point(
									100, 165)));
					graphics.draw(new BezierCurve(new Point(10, 150),
							new Point(86, 90), new Point(162, 25), new Point(
									238, 130), new Point(314, 200), new Point(
									380, 150)));
				}
			}, new Snippet("do off-screen rendering") {
				@Override
				public void renderScene(IGraphics graphics) {
					Image image = new Image(WIDTH, HEIGHT);

					IImageGraphics ig = graphics.createImageGraphics(image);

					Random rng = new Random(System.currentTimeMillis());
					for (int i = 0; i < 10; i++) {
						double a = rng.nextDouble() * 50 + 10;
						double b = rng.nextDouble() * 50 + 10;
						double x = rng.nextDouble() * WIDTH - a;
						double y = rng.nextDouble() * HEIGHT - b;
						Ellipse ellipse = new Ellipse(0, 0, a + a, b + b);
						ig.setFill(new Gradient.Radial(ellipse).addStop(0,
								new Color(255, 255, 255, 255)).addStop(1,
								new Color(255, 255, 255, 128)));
						ig.pushState().translate(x, y);
						ig.fill(ellipse);
						ig.popState();
					}

					ig.cleanUp();

					graphics.paint(image);
				}
			} };

	public static void main(String[] args) {
		new HowToSnippets("How to...");
	}

	private final Combo snippetCombo;
	private final Canvas canvas;
	private Snippet selected;

	public HowToSnippets(String title) {
		final Display display = new Display();

		final Shell shell = new Shell(display, SWT.SHELL_TRIM
				| SWT.DOUBLE_BUFFERED);
		shell.setText(title);
		shell.setLayout(new GridLayout(1, false));

		final Composite selectionComposite = new Composite(shell, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		selectionComposite.setLayoutData(gd);
		selectionComposite.setLayout(new RowLayout(SWT.HORIZONTAL));

		final Label selectionLabel = new Label(selectionComposite, SWT.NONE);
		selectionLabel.setText("Select How-To: ");

		snippetCombo = new Combo(selectionComposite, SWT.VERTICAL
				| SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);

		for (int i = 0; i < SNIPPETS.length; i++) {
			Snippet s = SNIPPETS[i];
			snippetCombo.add((i + 1) + ". How to " + s.getName());
		}

		snippetCombo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				int i = snippetCombo.getSelectionIndex();
				if (i != -1) {
					selected = SNIPPETS[i];
				}
				canvas.redraw();
			}
		});

		canvas = new Canvas(shell, SWT.BORDER);
		gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = WIDTH;
		gd.heightHint = HEIGHT;
		canvas.setLayoutData(gd);
		canvas.setBackground(new org.eclipse.swt.graphics.Color(display, 255,
				255, 255));
		canvas.addPaintListener(this);

		shell.pack();
		shell.open();
		shell.redraw(); // platform independently triggers a PaintEvent

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	@Override
	public void paintControl(PaintEvent e) {
		SwtGraphics g = new SwtGraphics(e.gc);
		if (selected != null) {
			selected.renderScene(g);
		}
		g.cleanUp();
	}
}
