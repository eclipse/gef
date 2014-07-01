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
package org.eclipse.gef4.fx.nodes;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;

import org.eclipse.gef4.fx.anchors.AnchorKey;
import org.eclipse.gef4.fx.anchors.AnchorLink;
import org.eclipse.gef4.fx.anchors.FXChopBoxAnchor;
import org.eclipse.gef4.fx.anchors.IFXAnchor;
import org.eclipse.gef4.geometry.planar.ICurve;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Polyline;

public class FXCurveConnection extends AbstractFXConnection<ICurve> {

	private MapChangeListener<? super AnchorKey, ? super Point> startPCL = new MapChangeListener<AnchorKey, Point>() {
		@Override
		public void onChanged(
				javafx.collections.MapChangeListener.Change<? extends AnchorKey, ? extends Point> change) {
			updateEndReferencePoint();
		}
	};

	private MapChangeListener<? super AnchorKey, ? super Point> endPCL = new MapChangeListener<AnchorKey, Point>() {
		@Override
		public void onChanged(
				javafx.collections.MapChangeListener.Change<? extends AnchorKey, ? extends Point> change) {
			updateStartReferencePoint();
		}
	};

	private ChangeListener<? super AnchorLink> onStartAnchorLinkChange = new ChangeListener<AnchorLink>() {
		@Override
		public void changed(ObservableValue<? extends AnchorLink> observable,
				AnchorLink oldLink, AnchorLink newLink) {
			if (newLink != null) {
				IFXAnchor anchor = newLink.getAnchor();
				if (anchor != null) {
					anchor.positionProperty().addListener(startPCL);
				}
			}
		}
	};

	private ChangeListener<? super AnchorLink> onEndAnchorLinkChange = new ChangeListener<AnchorLink>() {
		@Override
		public void changed(ObservableValue<? extends AnchorLink> observable,
				AnchorLink oldLink, AnchorLink newLink) {
			if (newLink != null) {
				IFXAnchor anchor = newLink.getAnchor();
				if (anchor != null) {
					anchor.positionProperty().addListener(endPCL);
				}
			}
		}
	};

	private ListChangeListener<? super Point> onWayPointChange = new ListChangeListener<Point>() {
		@Override
		public void onChanged(
				javafx.collections.ListChangeListener.Change<? extends Point> c) {
			updateStartReferencePoint();
			updateEndReferencePoint();
		}
	};

	public FXCurveConnection() {
		setOnStartAnchorLinkChange(onStartAnchorLinkChange);
		setOnEndAnchorLinkChange(onEndAnchorLinkChange);
		setOnWayPointChange(onWayPointChange);
	}

	@Override
	public ICurve computeGeometry(Point[] points) {
		return new Polyline(points);
	}

	private void updateEndReferencePoint() {
		AnchorLink anchorLink = endAnchorLinkProperty.get();
		if (anchorLink == null) {
			return;
		}
		IFXAnchor endAnchor = anchorLink.getAnchor();
		if (endAnchor instanceof FXChopBoxAnchor) {
			FXChopBoxAnchor a = (FXChopBoxAnchor) endAnchor;
			Point[] refPoints = computeReferencePoints();
			AnchorKey key = endAnchorLinkProperty.get().getKey();
			Point oldRef = a.getReferencePoint(key);
			if (oldRef == null || !oldRef.equals(refPoints[1])) {
				// System.out.println("E-Ref: " + refPoints[1]);
				a.setReferencePoint(key, refPoints[1]);
			}
		}
	}

	private void updateStartReferencePoint() {
		AnchorLink anchorLink = startAnchorLinkProperty.get();
		if (anchorLink == null) {
			return;
		}
		IFXAnchor startAnchor = anchorLink.getAnchor();
		if (startAnchor instanceof FXChopBoxAnchor) {
			FXChopBoxAnchor a = (FXChopBoxAnchor) startAnchor;
			Point[] refPoints = computeReferencePoints();
			AnchorKey key = startAnchorLinkProperty.get().getKey();
			Point oldRef = a.getReferencePoint(key);
			if (oldRef == null || !oldRef.equals(refPoints[0])) {
				// System.out.println("S-Ref: " + refPoints[0]);
				a.setReferencePoint(key, refPoints[0]);
			}
		}
	}

}
