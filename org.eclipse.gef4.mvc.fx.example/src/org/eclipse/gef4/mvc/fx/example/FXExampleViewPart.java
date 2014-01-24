package org.eclipse.gef4.mvc.fx.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.embed.swt.FXCanvas;

import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.IGeometry;
import org.eclipse.gef4.geometry.planar.IShape;
import org.eclipse.gef4.geometry.planar.Line;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.mvc.fx.domain.FXEditDomain;
import org.eclipse.gef4.mvc.fx.viewer.FXViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class FXExampleViewPart extends ViewPart {

	public class ExampleGeometricModel {

		private IShape s1 = new Rectangle(50, 50, 50, 50);
		private IShape s2 = new Rectangle(150, 50, 50, 50);
		private ICurve c1 = new Line(100, 75, 150, 75);

		public IShape[] getShapes() {
			return new IShape[] {s1, s2};
		}
		
		public ICurve[] getCurves() {
			return new ICurve[]{c1};
		}
		
		public IGeometry[] getAllGeometries() {
			List<IGeometry> geometriesList = new ArrayList<>();
			geometriesList.addAll(Arrays.asList(getShapes()));
			geometriesList.addAll(Arrays.asList(getCurves()));
			return geometriesList.toArray(new IGeometry[]{});
		}
		
		// return anchorages and related anchoreds
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Map<IGeometry, List<IGeometry>> getAnchors(){
			Map<IGeometry, List<IGeometry>> anchors = new HashMap<IGeometry, List<IGeometry>>();
			anchors.put(s1, (List)Collections.singletonList(c1));
			anchors.put(s2, (List)Collections.singletonList(c1));
			return anchors;
		}
	}

	private FXCanvas canvas;

	public FXExampleViewPart() {
	}

	@Override
	public void createPartControl(Composite parent) {
		canvas = new FXCanvas(parent, SWT.NONE);
		FXEditDomain domain = new FXExampleDomain();
		FXViewer viewer = new FXViewer(canvas);
		viewer.setContentPartFactory(new FXExampleContentPartFactory());
		viewer.setHandlePartFactory(new FXExampleHandlePartFactory());
		viewer.setEditDomain(domain);
		viewer.setContents(new ExampleGeometricModel());
	}

	@Override
	public void setFocus() {
		canvas.setFocus();
	}

}
