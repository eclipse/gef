/*******************************************************************************
 * Copyright (c) 2015, 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API & implementation
 *     Alexander Nyßen  (itemis AG) - initial API & implementation
 *     Tamas Miklossy   (itemis AG) - Add support for arrowType edge decorations (bug #477980)
 *
 *******************************************************************************/
package org.eclipse.gef4.dot.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef4.common.attributes.IAttributeCopier;
import org.eclipse.gef4.common.attributes.IAttributeStore;
import org.eclipse.gef4.dot.internal.DotAttributes;
import org.eclipse.gef4.dot.internal.parser.arrowtype.ArrowType;
import org.eclipse.gef4.dot.internal.parser.dir.DirType;
import org.eclipse.gef4.dot.internal.parser.rankdir.Rankdir;
import org.eclipse.gef4.dot.internal.parser.splinetype.Spline;
import org.eclipse.gef4.dot.internal.parser.splinetype.SplineType;
import org.eclipse.gef4.fx.nodes.OrthogonalRouter;
import org.eclipse.gef4.fx.nodes.PolylineInterpolator;
import org.eclipse.gef4.fx.nodes.StraightRouter;
import org.eclipse.gef4.geometry.planar.Dimension;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.GraphCopier;
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
 * {@link DotAttributes} into a {@link Graph} that is attributed with
 * {@link ZestProperties}.
 * 
 * @author anyssen
 *
 */
public class Dot2ZestGraphConverter extends GraphCopier
		implements IAttributeCopier {

	public Dot2ZestGraphConverter() {
		// TODO: this is not really nice; we have to overwrite
		// transferAttributes()
		// because we do not pass in an attribute transfer here. We should
		// rather use an IAttributeCopier as a delegate or convert this class
		// into an IAttributeCopier as a whole. -> we need to remove the need
		// for overriding copyEdge before -> introdude invisibility.
		super(null);
	}

	public final static class Options {

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
		 * Whether to ignore position data.
		 */
		public boolean ignorePositions = false;

		/**
		 * Specifies whether the y-coordinate values of all position information
		 * is to be inverted. If set to <code>true</code> the y-values of all
		 * position information is to be inverted. If set to <code>false</code>,
		 * it is to be transformed without inversion.
		 */
		public boolean invertYAxis = true;
	}

	@Override
	protected void copyAttributes(IAttributeStore inputStore,
			IAttributeStore outputStore) {
		copy(inputStore, outputStore);
	}

	@Override
	public void copy(IAttributeStore source, IAttributeStore target) {
		if (source instanceof Node && target instanceof Node) {
			convertAttributes((Node) source, (Node) target);
		} else if (source instanceof Edge && target instanceof Edge) {
			convertAttributes((Edge) source, (Edge) target);
		} else if (source instanceof Graph && target instanceof Graph) {
			convertAttributes((Graph) source, (Graph) target);
		} else {
			throw new IllegalArgumentException();
		}
	}

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
		// direction
		DirType dotDir = DotAttributes.getDirParsed(dot);
		if (dotDir == null) {
			// use the default direction if no direction is specified for
			// the edge
			dotDir = DotAttributes._TYPE__G__DIGRAPH.equals(
					dot.getGraph().getAttributes().get(DotAttributes._TYPE__G))
							? DirType.FORWARD : DirType.NONE;
		}

		// arrow size
		Double arrowSizeParsed = DotAttributes.getArrowSizeParsed(dot);
		double arrowSize = arrowSizeParsed == null ? 1.0 : arrowSizeParsed;

		// arrow head
		String dotArrowHead = DotAttributes.getArrowHead(dot);
		Shape zestEdgeTargetDecoration = null;
		if (dotArrowHead == null) {
			// use the default arrow head decoration in case the graph is
			// directed
			if (DotAttributes._TYPE__G__DIGRAPH.equals(dot.getGraph()
					.getAttributes().get(DotAttributes._TYPE__G))) {
				zestEdgeTargetDecoration = DotArrowShapeDecorations
						.getDefault(arrowSize, true);
			}
		} else {
			zestEdgeTargetDecoration = computeZestDecoration(
					DotAttributes.getArrowHeadParsed(dot), arrowSize);
		}

		// The zest edge target decoration should only appear if the edge
		// direction is "forward" or "both".
		if (DirType.FORWARD.equals(dotDir) || DirType.BOTH.equals(dotDir)) {
			ZestProperties.setTargetDecoration(zest, zestEdgeTargetDecoration);
		}

		// arrow tail
		String dotArrowTail = DotAttributes.getArrowTail(dot);
		Shape zestEdgeSourceDecoration = null;
		if (dotArrowTail == null) {
			// use the default arrow tail decoration in case the graph is
			// directed
			if (DotAttributes._TYPE__G__DIGRAPH.equals(dot.getGraph()
					.getAttributes().get(DotAttributes._TYPE__G))) {
				zestEdgeSourceDecoration = DotArrowShapeDecorations
						.getDefault(arrowSize, true);
			}
		} else {
			zestEdgeSourceDecoration = computeZestDecoration(
					DotAttributes.getArrowTailParsed(dot), arrowSize);
		}

		// The zest edge source decoration should only appear if the edge
		// direction is "back" or "both".
		if (DirType.BACK.equals(dotDir) || DirType.BOTH.equals(dotDir)) {
			ZestProperties.setSourceDecoration(zest, zestEdgeSourceDecoration);
		}

		// only convert layout information in native mode, as the results
		// will
		// otherwise
		// not match
		if (!options().emulateLayout) {
			// position (pos)
			String dotPos = DotAttributes.getPos(dot);
			if (dotPos != null && !options().ignorePositions) {
				// XXX: We use a special format to represent DOT B-splines:
				// in case start or end is not given, the
				// first or last control point will be contained twice.
				final List<Point> bSplineControlPoints = computeZestBSplineControlPoints(
						dot);

				// mapping to Zest depends on value of 'splines' graph
				// attribute
				String splines = DotAttributes.getSplines(dot.getGraph());
				if (DotAttributes.SPLINES__G__LINE.equals(splines)
						|| DotAttributes.SPLINES__G__FALSE.equals(splines)) {
					// use polyline interpolator
					// use straight router
					// do not use control points
					ZestProperties.setInterpolator(zest,
							new PolylineInterpolator());
					ZestProperties.setRouter(zest, new StraightRouter());
				} else if (DotAttributes.SPLINES__G__POLYLINE.equals(splines)) {
					// use polyline interpolator
					// use straight router
					// use control points (without start/end) TODO: verify
					ZestProperties.setInterpolator(zest,
							new PolylineInterpolator());
					ZestProperties.setRouter(zest, new StraightRouter());
					ZestProperties.setControlPoints(zest, bSplineControlPoints
							.subList(1, bSplineControlPoints.size() - 1));
				} else if (DotAttributes.SPLINES__G__SPLINE.equals(splines)
						|| DotAttributes.SPLINES__G__TRUE.equals(splines)) {
					// use dot bspline interpolator
					// use dot bspline router
					// use control points (without start/end)
					ZestProperties.setInterpolator(zest,
							new DotBSplineInterpolator());
					// use start/end as reference points for the anchor
					// computation
					ZestProperties.setRouter(zest,
							new DotBSplineRouter(bSplineControlPoints.get(0),
									bSplineControlPoints.get(
											bSplineControlPoints.size() - 1)));
					// first and last way point are provided by start and end
					// anchor, so we need to remove them as control points
					ZestProperties.setControlPoints(zest, bSplineControlPoints
							.subList(1, bSplineControlPoints.size() - 1));
				} else if (DotAttributes.SPLINES__G__ORTHO.equals(splines)) {
					// use polyline interpolator
					// use orthogonal router
					// normalize control points for orthogonal lines
					ZestProperties.setInterpolator(zest,
							new PolylineInterpolator());
					ZestProperties.setRouter(zest, new OrthogonalRouter());
					ZestProperties.setControlPoints(zest,
							computeZestOrthogonalControlPoints(
									bSplineControlPoints));
					// XXX: OrthogonalProjectionStrategy is set within EdgePart
					// when an anchor is attached.
				} else if (DotAttributes.SPLINES__G__COMPOUND.equals(splines)) {
					// TODO
				} else if (DotAttributes.SPLINES__G__EMPTY.equals(splines)
						|| DotAttributes.SPLINES__G__NONE.equals(splines)) {
					// TODO no edges
				}
			}

			// label position (lp)
			String dotLp = DotAttributes.getLp(dot);
			if (dotLabel != null && dotLp != null
					&& !options().ignorePositions) {
				ZestProperties.setLabelPosition(zest, computeZestLabelPosition(
						DotAttributes.getLpParsed(dot), dotLabel));
			}

			// external label position (xlp)
			String dotXlp = DotAttributes.getXlp(dot);
			if (dotXLabel != null && dotXlp != null
					&& !options().ignorePositions) {
				ZestProperties.setExternalLabelPosition(zest,
						computeZestLabelPosition(
								DotAttributes.getXlpParsed(dot), dotXLabel));
			}
			// head and tail label positions (head_lp, tail_lp)
			String headLp = DotAttributes.getHeadLp(dot);
			if (dotHeadLabel != null && headLp != null
					&& !options().ignorePositions) {
				ZestProperties.setTargetLabelPosition(zest,
						computeZestLabelPosition(
								DotAttributes.getHeadLpParsed(dot),
								dotHeadLabel));
			}
			String tailLp = DotAttributes.getTailLp(dot);
			if (dotTailLabel != null && tailLp != null
					&& !options().ignorePositions) {
				ZestProperties.setSourceLabelPosition(zest,
						computeZestLabelPosition(
								DotAttributes.getTailLpParsed(dot),
								dotTailLabel));
			}

		}
	}

	private List<Point> computeZestOrthogonalControlPoints(
			List<Point> bSplineControlPoints) {
		// remove start and end point (both are present twice)
		List<Point> subList = new ArrayList<>(bSplineControlPoints.subList(2,
				bSplineControlPoints.size() - 2));
		// normalize remaining points
		for (int i = subList.size() - 1; i > 0; i--) {
			Point p = subList.get(i);
			Point q = subList.get(i - 1);
			if (p.x == q.x) {
				// remove p, keep q but adjust its y coordinate
				subList.remove(i);
				q.y = p.y / 2 + q.y / 2;
			} else if (p.y == q.y) {
				// remove p, keep q but adjust its x coordinate
				subList.remove(i);
				q.x = p.x / 2 + q.x / 2;
			}
		}
		System.out.println("ortho: " + subList);
		return subList;
	}

	private Shape computeZestDecoration(ArrowType arrowType, double arrowSize) {
		return DotArrowShapeDecorations.get(arrowType, arrowSize);
	}

	private List<Point> computeZestBSplineControlPoints(Edge dot) {
		SplineType splineType = DotAttributes.getPosParsed(dot);
		List<Point> controlPoints = new ArrayList<>();
		for (Spline spline : splineType.getSplines()) {
			// start
			org.eclipse.gef4.dot.internal.parser.point.Point startp = spline
					.getStartp();
			if (startp == null) {
				// if we have no start point, add the first control
				// point twice
				startp = spline.getControlPoints().get(0);
			}
			controlPoints.add(new Point(startp.getX(),
					(options().invertYAxis ? -1 : 1) * startp.getY()));

			// control points
			for (org.eclipse.gef4.dot.internal.parser.point.Point cp : spline
					.getControlPoints()) {
				controlPoints.add(new Point(cp.getX(),
						(options().invertYAxis ? -1 : 1) * cp.getY()));
			}

			// end
			org.eclipse.gef4.dot.internal.parser.point.Point endp = spline
					.getEndp();
			if (endp == null) {
				// if we have no end point, add the last control point
				// twice
				endp = spline.getControlPoints()
						.get(spline.getControlPoints().size() - 1);
			}
			controlPoints.add(new Point(endp.getX(),
					(options().invertYAxis ? -1 : 1) * endp.getY()));
		}
		return controlPoints;
	}

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

		// Convert position and size; as node position is interpreted as
		// center,
		// we need to know the size in order to infer correct zest positions
		String dotPos = DotAttributes.getPos(dot);
		String dotHeight = DotAttributes.getHeight(dot);
		String dotWidth = DotAttributes.getWidth(dot);
		if (dotWidth != null && dotHeight != null) {
			// dot default scaling is 72 DPI
			// TODO: if dpi option is set, we should probably use it!
			double zestHeight = Double.parseDouble(dotHeight) * 72; // inches
			double zestWidth = Double.parseDouble(dotWidth) * 72; // inches
			ZestProperties.setSize(zest, new Dimension(zestWidth, zestHeight));

			if (dotPos != null && !options().ignorePositions) {
				// node position is interpreted as center of node in Dot,
				// and
				// top-left in Zest
				org.eclipse.gef4.dot.internal.parser.point.Point dotPosParsed = DotAttributes
						.getPosParsed(dot);
				ZestProperties.setPosition(zest, computeZestPosition(
						dotPosParsed, zestWidth, zestHeight));
				// if a position is marked as input-only in Dot, have Zest
				// ignore it
				ZestProperties.setLayoutIrrelevant(zest,
						dotPosParsed.isInputOnly());
			}
		}

		// external label position (xlp)
		String dotXlp = DotAttributes.getXlp(dot);
		if (dotXLabel != null && dotXlp != null && !options().ignorePositions) {
			org.eclipse.gef4.dot.internal.parser.point.Point dotXlpParsed = DotAttributes
					.getXlpParsed(dot);
			ZestProperties.setExternalLabelPosition(zest,
					computeZestLabelPosition(dotXlpParsed, dotXLabel));
		}
	}

	private Point computeZestPosition(
			org.eclipse.gef4.dot.internal.parser.point.Point dotPosition,
			double widthInPixel, double heightInPixel) {
		// dot positions are provided as center positions, Zest uses
		// top-left
		return new Point(dotPosition.getX() - widthInPixel / 2,
				(options().invertYAxis ? -1 : 1) * (dotPosition.getY())
						- heightInPixel / 2);
	}

	private Point computeZestLabelPosition(
			org.eclipse.gef4.dot.internal.parser.point.Point dotLabelPosition,
			String labelText) {
		// TODO: respect font settings (font name and size)
		Bounds labelSize = new Text(labelText).getLayoutBounds();
		return computeZestPosition(dotLabelPosition, labelSize.getWidth(),
				labelSize.getHeight());
	}

	protected void convertAttributes(Graph dot, Graph zest) {
		// TODO: graph label
		if (options().emulateLayout) {
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
				Rankdir dotRankdir = DotAttributes.getRankdirParsed(dot);
				algo = new TreeLayoutAlgorithm(Rankdir.LR.equals(dotRankdir)
						? TreeLayoutAlgorithm.LEFT_RIGHT
						: TreeLayoutAlgorithm.TOP_DOWN);
			}
			ZestProperties.setLayoutAlgorithm(zest, algo);
		}
	}

	private Options options;

	public Options options() {
		if (options == null) {
			options = new Options();
		}
		return options;
	}

	@Override
	protected Edge copyEdge(Edge inputEdge) {
		String splines = DotAttributes.getSplines(inputEdge.getGraph());
		// skip edges in case splines is set to empty or none.
		if (DotAttributes.SPLINES__G__EMPTY.equals(splines)
				|| DotAttributes.SPLINES__G__NONE.equals(splines)) {
			return null;
		}
		// TODO: make edge invisible instead??
		return super.copyEdge(inputEdge);
	}

}
