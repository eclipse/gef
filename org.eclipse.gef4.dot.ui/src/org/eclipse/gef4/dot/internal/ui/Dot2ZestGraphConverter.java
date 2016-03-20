/*******************************************************************************
 * Copyright (c) 2015 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *     Alexander Nyßen  (itemis AG) - initial API & implementation
 *     Tamas Miklossy (itemis AG)   - Add support for arrowType edge decorations (bug #477980)
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.dot.internal.parser.dotArrowType.ArrowShape;
import org.eclipse.gef4.dot.internal.parser.dotArrowType.ArrowShapes;
import org.eclipse.gef4.dot.internal.parser.dotArrowType.ArrowType;
import org.eclipse.gef4.dot.internal.parser.dotArrowType.DeprecatedArrowShape;
import org.eclipse.gef4.dot.internal.parser.dotSplineType.Spline;
import org.eclipse.gef4.dot.internal.parser.dotSplineType.SplineType;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.ILayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef4.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef4.zest.fx.ZestProperties;

import javafx.geometry.Bounds;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * A converter that transforms a {@link Graph} that is attributed with
 * {@link ZestProperties} into a {@link Graph} that is attributed with
 * {@link DotAttributes}.
 * 
 * @author anyssen
 *
 */
public class Dot2ZestGraphConverter extends AbstractGraphConverter {

	public static final class Options {

		/**
		 * Indicates whether layout should be emulated or not. If set to
		 * <code>true</code>, an {@link ILayoutAlgorithm} is to be inferred for
		 * the given dot, and set as value of the
		 * {@link ZestProperties#GRAPH_LAYOUT_ALGORITHM} attribute. If set to
		 * <code>false</code> (i.e. native layout is performed via Graphviz and
		 * position information is already provided in the dot input), the
		 * {@link ZestProperties#GRAPH_LAYOUT_ALGORITHM} should remain unset.
		 */
		public boolean emulateLayout = true;

		/**
		 * Specifies whether the y-coordinate values of all position information
		 * is to be inverted. If set to <code>true</code> the y-values of all
		 * position information is to be inverted. If set to <code>false</code>,
		 * it is to be transformed without inversion.
		 */
		public boolean invertYAxis = true;
	}

	private Options options;

	public Dot2ZestGraphConverter() {
		this.options = new Options();
	}

	public Options options() {
		return options;
	}

	@Override
	protected void convertAttributes(Edge dot, Edge zest) {
		// convert id and label
		String dotId = DotAttributes.getId(dot);
		if (dotId != null) {
			ZestProperties.setCssId(zest, dotId);
		}

		String dotLabel = DotAttributes.getLabel(dot);
		if (dotLabel != null && dotLabel.equals("\\E")) { //$NON-NLS-1$
			// The node default label '\N' is used to indicate that a node's
			// name or id becomes its label.
			dotLabel = dotId != null ? dotId : DotAttributes.getName(dot);
		}
		if (dotLabel != null) {
			ZestProperties.setLabel(zest, dotLabel);
		}

		// external label (xlabel)
		String dotXLabel = DotAttributes.getXLabel(dot);
		if (dotXLabel != null) {
			ZestProperties.setExternalLabel(zest, dotXLabel);
		}

		// head and tail labels (headlabel, taillabel)
		String dotHeadLabel = DotAttributes.getHeadLabel(dot);
		if (dotHeadLabel != null) {
			ZestProperties.setTargetLabel(zest, dotHeadLabel);
		}
		String dotTailLabel = DotAttributes.getTailLabel(dot);
		if (dotTailLabel != null) {
			ZestProperties.setSourceLabel(zest, dotTailLabel);
		}

		// convert edge style
		String dotStyle = DotAttributes.getStyle(dot);
		String connectionCssStyle = null;
		if (DotAttributes.STYLE__E__DASHED.equals(dotStyle)) {
			connectionCssStyle = "-fx-stroke-dash-array: 7 7;"; //$NON-NLS-1$
		} else if (DotAttributes.STYLE__E__DOTTED.equals(dotStyle)) {
			connectionCssStyle = "-fx-stroke-dash-array: 1 7;"; //$NON-NLS-1$
		} else if (DotAttributes.STYLE__E__BOLD.equals(dotStyle)) {
			connectionCssStyle = "-fx-stroke-width: 2;"; //$NON-NLS-1$
		}
		// TODO: handle tapered edges
		if (connectionCssStyle != null) {
			ZestProperties.setEdgeCurveCssStyle(zest, connectionCssStyle);
		}

		// arrow head
		String dotArrowHead = DotAttributes.getArrowHead(dot);
		Shape connectionTargetDecoration = null;
		if (dotArrowHead == null) {
			// use "normal" in case graph is directed
			if (DotAttributes._TYPE__G__DIGRAPH.equals(dot.getGraph()
					.getAttributes().get(DotAttributes._TYPE__G))) {
				connectionTargetDecoration = new DotArrowShapeDecorations.Normal();
			}
		} else {
			connectionTargetDecoration = computeZestDecoration(
					DotAttributes.getArrowHeadParsed(dot));
		}
		ZestProperties.setTargetDecoration(zest, connectionTargetDecoration);

		// only convert layout information in native mode, as the results will
		// otherwise
		// not match
		if (!options.emulateLayout) {
			// position (pos)
			String dotPos = DotAttributes.getPos(dot);
			if (dotPos != null) {
				ZestProperties.setControlPoints(zest,
						computeZestConnectionBSplineControlPoints(dot));
				ZestProperties.setInterpolator(zest,
						new DotBSplineInterpolator());
			}

			// label position (lp)
			String dotLp = DotAttributes.getLp(dot);
			if (dotLabel != null && dotLp != null) {
				ZestProperties.setLabelPosition(zest, computeZestLabelPosition(
						DotAttributes.getLpParsed(dot), dotLabel));
			}

			// external label position (xlp)
			String dotXlp = DotAttributes.getXlp(dot);
			if (dotXLabel != null && dotXlp != null) {
				ZestProperties.setExternalLabelPosition(zest,
						computeZestLabelPosition(
								DotAttributes.getXlpParsed(dot), dotXLabel));
			}
			// head and tail label positions (head_lp, tail_lp)
			String headLp = DotAttributes.getHeadLp(dot);
			if (dotHeadLabel != null && headLp != null) {
				ZestProperties.setTargetLabelPosition(zest,
						computeZestLabelPosition(
								DotAttributes.getHeadLpParsed(dot),
								dotHeadLabel));
			}
			String tailLp = DotAttributes.getTailLp(dot);
			if (dotTailLabel != null && tailLp != null) {
				ZestProperties.setSourceLabelPosition(zest,
						computeZestLabelPosition(
								DotAttributes.getTailLpParsed(dot),
								dotTailLabel));
			}

		}
	}

	private Shape computeZestDecoration(ArrowType arrowType) {
		Shape shape = null;
		if (arrowType instanceof DeprecatedArrowShape) {
			String firstArrowShape = ((DeprecatedArrowShape) arrowType)
					.getArrowShapes().get(0);
			switch (firstArrowShape) {
			case "ediamond": //$NON-NLS-1$
				// "ediamond" is deprecated, use "odiamond"
				shape = new DotArrowShapeDecorations.Diamond();
				shape.setStyle("-fx-fill: white"); //$NON-NLS-1$
				break;
			case "open": //$NON-NLS-1$
				// "open" is deprecated, use "vee"
				shape = new DotArrowShapeDecorations.Vee();
				shape.setStyle("-fx-fill: black"); //$NON-NLS-1$
				break;
			case "halfopen": //$NON-NLS-1$
				// "halfopen" is deprecated, use "lvee"
				shape = new DotArrowShapeDecorations.LVee();
				shape.setStyle("-fx-fill: black"); //$NON-NLS-1$
				break;
			case "empty": //$NON-NLS-1$
				// "empty" is deprecated, use "onormal"
				shape = new DotArrowShapeDecorations.Normal();
				shape.setStyle("-fx-fill: white"); //$NON-NLS-1$
				break;
			case "invempty": //$NON-NLS-1$
				// "invempty" is deprecated, use "oinv"
				shape = new DotArrowShapeDecorations.Inv();
				shape.setStyle("-fx-fill: white"); //$NON-NLS-1$
				break;
			default:
				break;
			}
		} else {
			// TODO: handle multi shapes
			ArrowShape arrowShape = ((ArrowShapes) arrowType).getArrowShapes()
					.get(0);
			switch (arrowShape.getShape()) {
			case BOX:
				shape = new DotArrowShapeDecorations.Box();
				break;
			case CROW:
				shape = new DotArrowShapeDecorations.Crow();
				break;
			case CURVE:
				shape = new DotArrowShapeDecorations.Curve();
				shape.setStyle("-fx-fill: white"); //$NON-NLS-1$
				break;
			case ICURVE:
				shape = new DotArrowShapeDecorations.ICurve();
				shape.setStyle("-fx-fill: white"); //$NON-NLS-1$
				break;
			case DIAMOND:
				shape = new DotArrowShapeDecorations.Diamond();
				break;
			case DOT:
				shape = new DotArrowShapeDecorations.Dot();
				break;
			case INV:
				shape = new DotArrowShapeDecorations.Inv();
				break;
			case NONE:
				return null;
			case NORMAL:
				shape = new DotArrowShapeDecorations.Normal();
				break;
			case TEE:
				shape = new DotArrowShapeDecorations.Tee();
				break;
			case VEE:
				shape = new DotArrowShapeDecorations.Vee();
				break;
			default:
				break;
			}
			if (arrowShape.isOpen()) {
				shape.setStyle("-fx-fill: white"); //$NON-NLS-1$
			}

		}

		return shape;
	}

	private List<Point> computeZestConnectionBSplineControlPoints(Edge dot) {
		SplineType splineType = DotAttributes.getPosParsed(dot);
		List<Point> controlPoints = new ArrayList<>();
		for (Spline spline : splineType.getSplines()) {
			// start
			org.eclipse.gef4.dot.internal.parser.dotPoint.Point startp = spline
					.getStartp();
			if (startp == null) {
				// if we have no start point, add the first control
				// point
				// twice
				startp = spline.getControlPoints().get(0);
			}
			controlPoints.add(new Point(startp.getX(),
					(options.invertYAxis ? -1 : 1) * startp.getY()));

			// control points
			for (org.eclipse.gef4.dot.internal.parser.dotPoint.Point cp : spline
					.getControlPoints()) {
				controlPoints.add(new Point(cp.getX(),
						(options.invertYAxis ? -1 : 1) * cp.getY()));
			}

			// end
			org.eclipse.gef4.dot.internal.parser.dotPoint.Point endp = spline
					.getEndp();
			if (endp == null) {
				// if we have no end point, add the last control point
				// twice
				endp = spline.getControlPoints()
						.get(spline.getControlPoints().size() - 1);
			}
			controlPoints.add(new Point(endp.getX(),
					(options.invertYAxis ? -1 : 1) * endp.getY()));
		}
		return controlPoints;
	}

	@Override
	protected void convertAttributes(Node dot, Node zest) {
		// id
		String dotId = DotAttributes.getId(dot);
		if (dotId != null) {
			ZestProperties.setCssId(zest, dotId);
		}

		// label
		String dotLabel = DotAttributes.getLabel(dot);
		if (dotLabel == null || dotLabel.equals("\\N")) { //$NON-NLS-1$
			// The node default label '\N' is used to indicate that a node's
			// name or id becomes its label.
			dotLabel = dotId != null ? dotId : DotAttributes.getName(dot);
		}
		ZestProperties.setLabel(zest, dotLabel);

		// external label (xlabel)
		String dotXLabel = DotAttributes.getXLabel(dot);
		if (dotXLabel != null) {
			ZestProperties.setExternalLabel(zest, dotXLabel);
		}

		// Convert position and size; as node position is interpreted as center,
		// we need to know the size in order to infer correct zest positions
		String dotPos = DotAttributes.getPos(dot);
		String dotHeight = DotAttributes.getHeight(dot);
		String dotWidth = DotAttributes.getWidth(dot);
		if (dotPos != null && dotWidth != null && dotHeight != null) {
			// dot default scaling is 72 DPI
			// TODO: if dpi option is set, we should probably use it!
			double zestHeight = Double.parseDouble(dotHeight) * 72; // inches
			double zestWidth = Double.parseDouble(dotWidth) * 72; // inches
			ZestProperties.setSize(zest, new Dimension(zestWidth, zestHeight));

			// node position is interpreted as center of node in Dot, and
			// top-left in Zest
			org.eclipse.gef4.dot.internal.parser.dotPoint.Point dotPosParsed = DotAttributes
					.getPosParsed(dot);
			ZestProperties.setPosition(zest,
					computeZestPosition(dotPosParsed, zestWidth, zestHeight));
			// if a position is marked as input-only in Dot, have Zest ignore it
			ZestProperties.setLayoutIrrelevant(zest,
					dotPosParsed.isInputOnly());
		}

		// external label position (xlp)
		String dotXlp = DotAttributes.getXlp(dot);
		if (dotXLabel != null && dotXlp != null) {
			org.eclipse.gef4.dot.internal.parser.dotPoint.Point dotXlpParsed = DotAttributes
					.getXlpParsed(dot);
			ZestProperties.setExternalLabelPosition(zest,
					computeZestLabelPosition(dotXlpParsed, dotXLabel));
		}
	}

	private Point computeZestPosition(
			org.eclipse.gef4.dot.internal.parser.dotPoint.Point dotPosition,
			double widthInPixel, double heightInPixel) {
		// dot positions are provided as center positions, Zest uses top-left
		return new Point(dotPosition.getX() - widthInPixel / 2,
				(options.invertYAxis ? -1 : 1) * (dotPosition.getY())
						- heightInPixel / 2);
	}

	private Point computeZestLabelPosition(
			org.eclipse.gef4.dot.internal.parser.dotPoint.Point dotLabelPosition,
			String labelText) {
		// FIXME: Is it legal to use JavaFX for metrics calculation here (while
		// we are part of DOT.UI)?
		// TODO: respect font settings (font name and size)
		Bounds layoutBounds = new Text(labelText).getLayoutBounds();
		return computeZestPosition(dotLabelPosition, layoutBounds.getWidth(),
				layoutBounds.getHeight());
	}

	@Override
	protected void convertAttributes(Graph dot, Graph zest) {

		// TODO: graph label

		if (options.emulateLayout) {
			// convert layout and rankdir to LayoutAlgorithm
			Object dotLayout = DotAttributes.getLayout(dot);
			ILayoutAlgorithm algo = null;
			if (DotAttributes.LAYOUT__G__CIRCO.equals(dotLayout)
					|| DotAttributes.LAYOUT__G__NEATO.equals(dotLayout)
					|| DotAttributes.LAYOUT__G__TWOPI.equals(dotLayout)) {
				algo = new RadialLayoutAlgorithm();
			} else if (DotAttributes.LAYOUT__G__FDP.equals(dotLayout)
					|| DotAttributes.LAYOUT__G__SFDP.equals(dotLayout)) {
				algo = new SpringLayoutAlgorithm();
			} else if (DotAttributes.LAYOUT__G__GRID.equals(dotLayout)
					|| DotAttributes.LAYOUT__G__OSAGE.equals(dotLayout)) {
				algo = new GridLayoutAlgorithm();
			} else {
				Object dotRankdir = DotAttributes.getRankdir(dot);
				boolean lr = DotAttributes.RANKDIR__G__LR.equals(dotRankdir);
				algo = new TreeLayoutAlgorithm(
						lr ? TreeLayoutAlgorithm.LEFT_RIGHT
								: TreeLayoutAlgorithm.TOP_DOWN);
			}
			ZestProperties.setLayoutAlgorithm(zest, algo);
		}
	}
}
