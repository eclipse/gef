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
package org.eclipse.gef.fx.examples;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.geometry.planar.BezierCurve;
import org.eclipse.gef.geometry.planar.CurvedPolygon;
import org.eclipse.gef.geometry.planar.Line;
import org.eclipse.gef.geometry.planar.PolyBezier;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Light.Distant;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class GeometryNodeSnippet extends AbstractFxExample {

	private static final Color GEF_COLOR_BLUE = Color.rgb(135, 150, 220);

	private static final Effect GEF_SHADOW_EFFECT = createShadowEffect();

	protected static CurvedPolygon createEShapeGeometry() {
		final List<BezierCurve> segments = new ArrayList<>();
		segments.add(new Line(1, 10, 6, 10));
		segments.addAll(Arrays.asList(
				PolyBezier.interpolateCubic(6, 10, 5, 25, 7, 52, 6, 70, 6, 81)
						.toBezier()));
		segments.addAll(Arrays.asList(
				PolyBezier.interpolateCubic(6, 81, 5, 81, 3, 84).toBezier()));
		segments.add(new Line(3, 84, 3, 87));
		segments.add(new Line(3, 87, 64, 86));
		segments.add(new Line(64, 86, 65, 79));
		segments.addAll(Arrays.asList(PolyBezier
				.interpolateCubic(65, 79, 59, 81, 51, 82).toBezier()));
		segments.add(new Line(51, 82, 12, 82));
		segments.addAll(Arrays.asList(PolyBezier
				.interpolateCubic(12, 82, 11, 56, 11, 30).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier
				.interpolateCubic(11, 30, 27, 30, 45, 31).toBezier()));
		segments.add(new Line(45, 31, 48, 25));
		segments.addAll(Arrays.asList(PolyBezier
				.interpolateCubic(48, 25, 35, 27, 19, 27, 10, 26).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier
				.interpolateCubic(10, 26, 10, 20, 11, 10).toBezier()));
		segments.addAll(Arrays.asList(PolyBezier
				.interpolateCubic(11, 10, 24, 11, 31, 11, 51, 12).toBezier()));
		segments.add(new Line(51, 12, 55, 6));
		segments.addAll(Arrays.asList(
				PolyBezier.interpolateCubic(55, 6, 45, 7, 33, 8, 15, 7, 7, 6)
						.toBezier()));
		segments.add(new Line(7, 6, 1, 10));
		return new CurvedPolygon(segments);
	}

	protected static Effect createShadowEffect() {
		final DropShadow outerShadow = new DropShadow();
		outerShadow.setRadius(3);
		outerShadow.setSpread(0.2);
		outerShadow.setOffsetX(3);
		outerShadow.setOffsetY(3);
		outerShadow.setColor(new Color(0.3, 0.3, 0.3, 1));

		final Distant light = new Distant();
		light.setAzimuth(-135.0f);

		final Lighting l = new Lighting();
		l.setLight(light);
		l.setSurfaceScale(3.0f);

		final Blend effects = new Blend(BlendMode.MULTIPLY);
		effects.setTopInput(l);
		effects.setBottomInput(outerShadow);

		return effects;
	}

	public static void main(final String[] args) {
		Application.launch(args);
	}

	public GeometryNodeSnippet() {
		super("GeometryNode Snippet");
	}

	@Override
	public Scene createScene() {
		final GeometryNode<CurvedPolygon> eLetterShape = new GeometryNode<>(
				createEShapeGeometry());
		eLetterShape.relocate(25, 25);
		eLetterShape.resize(250, 250);
		eLetterShape.setEffect(GEF_SHADOW_EFFECT);
		eLetterShape.setFill(GEF_COLOR_BLUE);
		eLetterShape.setManaged(false);

		final HBox hbox = new HBox();
		hbox.getChildren().add(eLetterShape);

		return new Scene(hbox, 300, 300);
	}

}
