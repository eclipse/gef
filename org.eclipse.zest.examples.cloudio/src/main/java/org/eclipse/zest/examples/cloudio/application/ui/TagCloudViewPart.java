/*******************************************************************************
* Copyright (c) 2011 Stephan Schwiebert. All rights reserved. This program and
* the accompanying materials are made available under the terms of the Eclipse
* Public License v1.0 which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
* <p/>
* Contributors: Stephan Schwiebert - initial API and implementation
*******************************************************************************/
package org.eclipse.zest.examples.cloudio.application.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.cloudio.CloudOptionsComposite;
import org.eclipse.zest.cloudio.TagCloud;
import org.eclipse.zest.cloudio.TagCloudViewer;
import org.eclipse.zest.cloudio.layout.DefaultLayouter;
import org.eclipse.zest.cloudio.layout.ILayouter;
import org.eclipse.zest.examples.cloudio.application.data.Type;

/**
 * 
 * @author sschwieb
 *
 */
public class TagCloudViewPart extends ViewPart {

	private TagCloudViewer viewer;
	private TypeLabelProvider labelProvider;
	private CloudOptionsComposite options;
	private ILayouter layouter;

	public TagCloudViewPart() {
	}
	
	@Override
	public void createPartControl(Composite parent) {
		SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
		sash.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		Composite cloudComp = new Composite(sash, SWT.NONE);
		cloudComp.setLayout(new GridLayout());
		cloudComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		TagCloud cloud = new TagCloud(cloudComp, SWT.HORIZONTAL | SWT.VERTICAL);
		cloud.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer = new TagCloudViewer(cloud);

		layouter = new DefaultLayouter(20, 10);
		//layouter = new CharacterLayouter(20,10);
		viewer.setLayouter(layouter);
		labelProvider = new TypeLabelProvider();
		//labelProvider = new CharacterLabelProvider();
		viewer.setLabelProvider(labelProvider);
		viewer.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer v, Object oldInput, Object newInput) {
				List<?> list = (List<?>) newInput;
				if(list == null || list.size() == 0) return;
				labelProvider.setMaxOccurrences(((Type)list.get(0)).getOccurrences());
				int minIndex =Math.min(list.size()-1, viewer.getMaxWords());
				labelProvider.setMinOccurrences(((Type)list.get(minIndex)).getOccurrences());
			}
			
			@Override
			public void dispose() {
				
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				return ((List<?>)inputElement).toArray();
			}
		});
		createSideTab(sash);
		
		cloud.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		viewer.getCloud().addControlListener(new ControlListener() {
			
			@Override
			public void controlResized(ControlEvent e) {
				viewer.getCloud().zoomFit();	
			}
			
			@Override
			public void controlMoved(ControlEvent e) {}
		});
		ArrayList<Type> types = new ArrayList<Type>();
		types.add(new Type("Cloudio", 220));
		types.add(new Type("Cloudio", 150));
		types.add(new Type("Cloudio", 100));
		types.add(new Type("No data available", 150));
		int size = 55;
		for(int i = 0; i < 50; i++) {
			types.add(new Type("Tag Cloud", size));
			size--;
		}
		viewer.getCloud().setMaxFontSize(100);
		viewer.getCloud().setMinFontSize(15);
		labelProvider.setColors(options.getColors());
		labelProvider.setFonts(options.getFonts());
		sash.setWeights(new int[] {72,28});
		viewer.setInput(types);
	}

	private void createSideTab(SashForm form) {
		Composite parent = new Composite(form, SWT.NONE);
		parent.setLayout(new GridLayout());
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		options = new CloudOptionsComposite(parent, SWT.NONE, viewer) {
			
			protected Group addLayoutButtons(Composite parent) {
				Group buttons = super.addLayoutButtons(parent);
				
				Label l = new Label(buttons, SWT.NONE);
				l.setText("Scale");
				final Combo scale = new Combo(buttons, SWT.DROP_DOWN | SWT.READ_ONLY);
				scale.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
				scale.setItems(new String[] {"linear", "logarithmic"});
				scale.select(1);
				scale.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						switch(scale.getSelectionIndex()) {
						case 0 : labelProvider.setScale(TypeLabelProvider.Scaling.LINEAR); break;
						case 1 : labelProvider.setScale(TypeLabelProvider.Scaling.LOGARITHMIC); break;
						default: break;
						}
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}
				});
				l = new Label(buttons, SWT.NONE);
				l.setText("X Axis Variation");
				final Combo xAxis = new Combo(buttons, SWT.DROP_DOWN | SWT.READ_ONLY);
				xAxis.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
				xAxis.setItems(new String[] {"0", "10","20","30","40","50", "60", "70", "80", "90", "100"});
				xAxis.select(2);
				xAxis.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						String item = xAxis.getItem(xAxis.getSelectionIndex());
						layouter.setOption(DefaultLayouter.X_AXIS_VARIATION, Integer.parseInt(item));
						
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}
				});
				
				l = new Label(buttons, SWT.NONE);
				l.setText("Y Axis Variation");
				final Combo yAxis = new Combo(buttons, SWT.DROP_DOWN | SWT.READ_ONLY);
				yAxis.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
				yAxis.setItems(new String[] {"0", "10","20","30","40","50", "60", "70", "80", "90", "100"});
				yAxis.select(1);
				yAxis.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						String item = yAxis.getItem(yAxis.getSelectionIndex());
						layouter.setOption(DefaultLayouter.Y_AXIS_VARIATION, Integer.parseInt(item));
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}
				});
				
				Button run = new Button(buttons, SWT.NONE);
				run.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				run.setText("Re-Position");
				run.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						final ProgressMonitorDialog dialog = new ProgressMonitorDialog(viewer.getControl().getShell());
						dialog.setBlockOnOpen(false);
						dialog.open();
						dialog.getProgressMonitor().beginTask("Layouting tag cloud...", 100);
						viewer.reset(dialog.getProgressMonitor(),false);
						dialog.close();
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}
				});
				Button layout = new Button(buttons, SWT.NONE);
				layout.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				layout.setText("Re-Layout");
				layout.addSelectionListener(new SelectionListener() {
					
					@Override
					public void widgetSelected(SelectionEvent e) {
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(viewer.getControl().getShell());
						dialog.setBlockOnOpen(false);
						dialog.open();
						dialog.getProgressMonitor().beginTask("Layouting tag cloud...", 200);
						viewer.setInput(viewer.getInput(), dialog.getProgressMonitor());
						//viewer.reset(dialog.getProgressMonitor(),false);
						dialog.close();
					}
					
					@Override
					public void widgetDefaultSelected(SelectionEvent e) {}
				});
				return buttons;
			};
			
		};
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		options.setLayoutData(gd);
	}
	

	@Override
	public void setFocus() {
		viewer.getCloud().setFocus();
	}

	@Override
	public void dispose() {
		viewer.getCloud().dispose();
		labelProvider.dispose();
	}
	
	public TagCloudViewer getViewer() {
		return viewer;
	}
	
}
