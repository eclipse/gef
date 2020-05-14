/******************************************************************************
 * Copyright (c) 2011, 2016 Stephan Schwiebert and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Stephan Schwiebert - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef.cloudio.internal.ui.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.cloudio.internal.ui.CloudioUiBundle;
import org.eclipse.gef.cloudio.internal.ui.TagCloudViewer;
import org.eclipse.gef.cloudio.internal.ui.Word;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FontDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * Provides options to modify the rendering of a {@link TagCloudViewer} using an
 * {@link IEditableCloudLabelProvider}.
 * 
 * @author sschwieb
 *
 */
public class CloudOptionsComposite extends Composite {

	protected TagCloudViewer viewer;

	protected List<RGB> colors = new ArrayList<>();
	protected List<FontData> fonts = new ArrayList<>();

	protected List<List<RGB>> colorSchemes = new ArrayList<>();

	protected int currentScheme;

	private static class ListContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			return ((List<?>) inputElement).toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return null;
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}

	}

	public CloudOptionsComposite(Composite parent, int style, TagCloudViewer viewer) {
		super(parent, style);
		Assert.isLegal(viewer.getLabelProvider() instanceof IEditableCloudLabelProvider,
				"Cloud label provider must be of type " + IEditableCloudLabelProvider.class);
		this.viewer = viewer;
		setLayout(new GridLayout());
		addGroups();
	}

	protected void addGroups() {
		addLayoutButtons(this);
		addColorButtons(this);
		addFontButtons(this);
	}

	protected void addScheme(RGB... rgbs) {
		List<RGB> colors = new ArrayList<>();
		for (RGB rgb : rgbs) {
			colors.add(rgb);
		}
		colorSchemes.add(colors);
	}

	protected void updateColors() {
		IEditableCloudLabelProvider lp = (IEditableCloudLabelProvider) viewer.getLabelProvider();
		lp.setColors(colors);
		List<Word> words = viewer.getCloud().getWords();
		for (Word word : words) {
			word.setColor(lp.getColor(word.data));
		}
		viewer.getCloud().redrawTextLayerImage();
	}

	protected void updateFonts() {
		IEditableCloudLabelProvider lp = (IEditableCloudLabelProvider) viewer.getLabelProvider();
		lp.setFonts(fonts);
	}

	protected void initColors() {
		addScheme(new RGB(222, 177, 17), new RGB(97, 28, 24), new RGB(102, 109, 17), new RGB(189, 112, 20),
				new RGB(111, 92, 16), new RGB(111, 32, 27));
		addScheme(new RGB(1, 175, 255), new RGB(57, 99, 213), new RGB(21, 49, 213), new RGB(30, 125, 42));
		addScheme(new RGB(255, 92, 93), new RGB(255, 0, 0), new RGB(255, 41, 43), new RGB(182, 31, 32),
				new RGB(153, 0, 0));
		addScheme(new RGB(255, 157, 0), new RGB(255, 206, 0), new RGB(40, 0, 159), new RGB(0, 41, 156));
		addScheme(new RGB(255, 46, 0), new RGB(255, 255, 14), new RGB(183, 183, 183), new RGB(122, 122, 122),
				new RGB(81, 81, 81), new RGB(61, 61, 61), new RGB(165, 165, 165));
		addScheme(new RGB(255, 0, 206), new RGB(255, 220, 0), new RGB(0, 255, 42));
		addScheme(new RGB(89, 79, 69), new RGB(168, 165, 126), new RGB(68, 49, 14), new RGB(86, 68, 34),
				new RGB(148, 141, 129), new RGB(92, 90, 41));
		addScheme(new RGB(66, 71, 37), new RGB(85, 122, 18), new RGB(117, 131, 49), new RGB(49, 45, 17));
		addScheme(new RGB(254, 213, 44), new RGB(255, 177, 10), new RGB(233, 121, 0), new RGB(229, 109, 3),
				new RGB(202, 80, 8), new RGB(129, 52, 7), new RGB(89, 47, 14));
		addScheme(new RGB(139, 124, 115), new RGB(91, 95, 129), new RGB(50, 23, 18), new RGB(255, 251, 237));
		nextColors();
	}

	protected void nextColors() {
		currentScheme = (currentScheme + 1) % colorSchemes.size();
		colors = colorSchemes.get(currentScheme);
	}

	protected Group addFontButtons(final Composite parent) {
		Group buttons = new Group(parent, SWT.SHADOW_IN);
		buttons.setLayout(new GridLayout(2, false));
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		Label l = new Label(buttons, SWT.NONE);
		l.setText("Fonts");
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		l.setLayoutData(gd);
		final TreeViewer tv = new TreeViewer(buttons);
		Composite comp = new Composite(buttons, SWT.NONE);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true));
		comp.setLayout(new RowLayout(SWT.VERTICAL));
		tv.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		ListContentProvider cp = new ListContentProvider();
		tv.setContentProvider(cp);
		tv.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				FontData fd = (FontData) element;
				return fd.getName();
			}

		});
		fonts.add(getFont().getFontData()[0]);
		tv.setInput(fonts);
		Button add = new Button(comp, SWT.FLAT);
		add.setImage(CloudioUiBundle.getDefault().getImageRegistry().get(CloudioUiBundle.ADD));
		add.setToolTipText("Add font...");
		add.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				FontDialog fd = new FontDialog(parent.getShell());
				FontData fontData = fd.open();
				if (fontData != null) {
					fonts.add(fontData);
					tv.setInput(fonts);
					updateFonts();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Button remove = new Button(comp, SWT.FLAT);
		remove.setToolTipText("Remove selected fonts");
		remove.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tv.getSelection();
				fonts.removeAll(selection.toList());
				tv.setInput(fonts);
				updateFonts();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		remove.setImage(CloudioUiBundle.getDefault().getImageRegistry().get(CloudioUiBundle.REMOVE));
		return buttons;
	}

	protected Image createImageFromColor(RGB rgb, int size) {
		Image image;
		Color color = new Color(Display.getDefault(), rgb);
		image = new Image(Display.getDefault(), size, size);
		GC gc = new GC(image);
		gc.setBackground(color);
		gc.fillRoundRectangle(0, 0, size, size, 3, 3);
		color.dispose();
		gc.dispose();
		return image;
	}

	protected Group addColorButtons(final Composite parent) {
		Group buttons = new Group(parent, SWT.SHADOW_IN);
		buttons.setLayout(new GridLayout(2, false));
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		Label l = new Label(buttons, SWT.NONE);
		l.setText("Colors");
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		l.setLayoutData(gd);
		final TreeViewer tv = new TreeViewer(buttons);
		Composite comp = new Composite(buttons, SWT.NONE);
		comp.setLayout(new RowLayout(SWT.VERTICAL));
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, true));
		tv.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		ListContentProvider cp = new ListContentProvider();
		tv.setContentProvider(cp);
		tv.setLabelProvider(new ColumnLabelProvider() {

			private Map<Object, Image> images = new HashMap<>();

			@Override
			public Image getImage(Object element) {
				Image image = images.get(element);
				if (image == null) {
					RGB rgb = (RGB) element;
					image = createImageFromColor(rgb, 24);
					images.put(element, image);
				}
				return image;
			}

			@Override
			public void dispose() {
				Collection<Image> images = this.images.values();
				for (Image image : images) {
					image.dispose();
				}
				this.images.clear();
			}

		});
		initColors();
		tv.setInput(colors);
		Button add = new Button(comp, SWT.FLAT);
		add.setImage(CloudioUiBundle.getDefault().getImageRegistry().get(CloudioUiBundle.ADD));
		add.setToolTipText("Add color...");
		add.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog cd = new ColorDialog(parent.getShell());
				RGB color = cd.open();
				if (color != null) {
					colors.add(color);
					tv.setInput(colors);
					updateColors();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		Button remove = new Button(comp, SWT.FLAT);
		remove.setToolTipText("Remove selected colors");
		remove.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tv.getSelection();
				colors.removeAll(selection.toList());

				tv.setInput(colors);
				updateColors();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		remove.setImage(CloudioUiBundle.getDefault().getImageRegistry().get(CloudioUiBundle.REMOVE));
		Button toggle = new Button(comp, SWT.FLAT);
		toggle.setToolTipText("Toggle Colors");
		toggle.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				nextColors();
				tv.setInput(colors);
				updateColors();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		toggle.setImage(CloudioUiBundle.getDefault().getImageRegistry().get(CloudioUiBundle.TOGGLE_COLORS));

		comp = new Composite(buttons, SWT.NONE);
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.horizontalSpan = 2;
		comp.setLayout(new GridLayout(2, true));
		comp.setLayoutData(gd);
		final Button bg = new Button(comp, SWT.FLAT);
		bg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		bg.setText("Background");
		bg.setImage(createImageFromColor(viewer.getCloud().getBackground().getRGB(), 16));
		bg.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog cd = new ColorDialog(parent.getShell());
				RGB color = cd.open();
				if (color == null)
					return;
				Color old = viewer.getCloud().getBackground();
				Color c = new Color(Display.getDefault(), color);
				viewer.getCloud().setBackground(c);
				old.dispose();
				viewer.getCloud().redrawTextLayerImage();
				Image oldImage = bg.getImage();
				Image newImage = createImageFromColor(color, 16);
				bg.setImage(newImage);
				oldImage.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		final Button sel = new Button(comp, SWT.FLAT);
		sel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		sel.setText("Selection");
		sel.setImage(createImageFromColor(viewer.getCloud().getSelectionColor().getRGB(), 16));
		sel.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				ColorDialog cd = new ColorDialog(parent.getShell());
				RGB color = cd.open();
				if (color == null)
					return;
				Color old = viewer.getCloud().getSelectionColor();
				Color c = new Color(Display.getDefault(), color);
				viewer.getCloud().setSelectionColor(c);
				old.dispose();
				viewer.getCloud().redrawTextLayerImage();
				Image oldImage = sel.getImage();
				Image newImage = createImageFromColor(color, 16);
				sel.setImage(newImage);
				oldImage.dispose();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return buttons;
	}

	protected Group addLayoutButtons(Composite parent) {
		Group buttons = new Group(parent, SWT.SHADOW_IN);
		buttons.setLayout(new GridLayout(2, true));
		buttons.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		Label l = new Label(buttons, SWT.NONE);
		l.setText("Number of Words");
		final Combo words = new Combo(buttons, SWT.DROP_DOWN | SWT.READ_ONLY);
		words.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		words.setItems(new String[] { "100", "200", "300", "400", "500", "600", "700", "800", "900", "1000", "1100",
				"1200", "1300", "1400", "1500", "1600", "1700", "1800", "1900", "2000" });
		words.select(2);
		words.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String item = words.getItem(words.getSelectionIndex());
				viewer.setMaxWords(Integer.parseInt(item));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		l = new Label(buttons, SWT.NONE);
		l.setText("Max Font Size");
		final Combo font = new Combo(buttons, SWT.DROP_DOWN | SWT.READ_ONLY);
		font.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		font.setItems(new String[] { "50", "100", "150", "200", "250", "300", "350", "400", "450", "500" });
		font.select(1);
		font.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String item = font.getItem(font.getSelectionIndex());
				viewer.getCloud().setMaxFontSize(Integer.parseInt(item));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		l = new Label(buttons, SWT.NONE);
		l.setText("Min Font Size");
		final Combo minFont = new Combo(buttons, SWT.DROP_DOWN | SWT.READ_ONLY);
		minFont.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		minFont.setItems(new String[] { "10", "15", "20", "25", "30", "35", "40", "45", "50" });
		minFont.select(1);
		minFont.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String item = minFont.getItem(minFont.getSelectionIndex());
				viewer.getCloud().setMinFontSize(Integer.parseInt(item));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		l = new Label(buttons, SWT.NONE);
		l.setText("Boost");
		final Combo boost = new Combo(buttons, SWT.DROP_DOWN | SWT.READ_ONLY);
		boost.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		boost.setItems(new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" });
		boost.select(0);
		boost.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String item = boost.getItem(boost.getSelectionIndex());
				viewer.setBoost(Integer.parseInt(item));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		l = new Label(buttons, SWT.NONE);
		l.setText("Boost Factor");
		final Combo boostFactor = new Combo(buttons, SWT.DROP_DOWN | SWT.READ_ONLY);
		boostFactor.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		boostFactor.setItems(new String[] { "1", "1.5", "2", "2.5", "3", "3.5", "4", "4.5", "5" });
		boostFactor.select(0);
		boostFactor.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				String item = boostFactor.getItem(boostFactor.getSelectionIndex());
				viewer.setBoostFactor(Float.parseFloat(item));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		if (viewer.getLabelProvider() instanceof IEditableCloudLabelProvider) {

		}

		l = new Label(buttons, SWT.NONE);
		l.setText("Angles");
		final Combo angles = new Combo(buttons, SWT.DROP_DOWN | SWT.READ_ONLY);
		angles.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		angles.setItems(new String[] { "Horizontal only", "Vertical only", "Horizontal & Vertical",
				"45 Degrees, mostly horizontal", "45 Degrees", "Random" });
		final List<List<Float>> anglesLists = new ArrayList<>();
		anglesLists.add(Arrays.asList(0F));
		anglesLists.add(Arrays.asList(-90F, 90F));
		anglesLists.add(Arrays.asList(0F, -90F, 0F, 90F));
		anglesLists.add(Arrays.asList(0F, -90F, -45F, 0F, 45F, 90F, 0F, 0F, 0F, 0F));
		anglesLists.add(Arrays.asList(-90F, -45F, 0F, 45F, 90F));
		List<Float> tmp = new ArrayList<>();
		for (int i = -90; i <= 90; i++) {
			tmp.add((float) i);
		}
		anglesLists.add(tmp);
		angles.select(0);
		angles.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = angles.getSelectionIndex();
				IEditableCloudLabelProvider lp = (IEditableCloudLabelProvider) viewer.getLabelProvider();
				lp.setAngles(anglesLists.get(index));
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		return buttons;
	}

	public List<RGB> getColors() {
		return colors;
	}

	public List<FontData> getFonts() {
		return fonts;
	}
}
