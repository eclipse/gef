/*******************************************************************************
 * Copyright (c) 2015, 2020 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand   (itemis AG) - initial API & implementation
 *     Alexander Nyßen    (itemis AG) - initial API & implementation
 *     Tamas Miklossy     (itemis AG) - Add support for arrowType edge decorations (bug #477980)
 *                                    - Add support for polygon-based node shapes (bug #441352)
 *                                    - Add support for all dot attributes (bug #461506)
 *     Zoey Gerrit Prigge (itemis AG) - Add support for record-based node shapes (bug #454629)
 *                                    - Add support for HTML labels (bug #321775)
 *                                    - Fix handling of "\N", "\E", "\G" in labels (bug #534707)
 *                                    - Add support for labelfontcolor attribute (bug #540958)
 *                                    - Add support for (label)fontsize/name (bug #541056)
 *                                    - Add penwidth support (bug #541106)
 *                                    - Add support for "\l", "\r" linebreaks (bug #508868)
 *
 *******************************************************************************/
package org.eclipse.gef.dot.internal.ui.conversion;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.common.attributes.IAttributeCopier;
import org.eclipse.gef.common.attributes.IAttributeStore;
import org.eclipse.gef.dot.internal.DotAttributes;
import org.eclipse.gef.dot.internal.language.arrowtype.ArrowType;
import org.eclipse.gef.dot.internal.language.color.Color;
import org.eclipse.gef.dot.internal.language.colorlist.ColorList;
import org.eclipse.gef.dot.internal.language.colorlist.WC;
import org.eclipse.gef.dot.internal.language.dir.DirType;
import org.eclipse.gef.dot.internal.language.dot.GraphType;
import org.eclipse.gef.dot.internal.language.escstring.EscString;
import org.eclipse.gef.dot.internal.language.escstring.JustifiedText;
import org.eclipse.gef.dot.internal.language.fontname.FontName;
import org.eclipse.gef.dot.internal.language.layout.Layout;
import org.eclipse.gef.dot.internal.language.rankdir.Rankdir;
import org.eclipse.gef.dot.internal.language.shape.PolygonBasedNodeShape;
import org.eclipse.gef.dot.internal.language.shape.PolygonBasedShape;
import org.eclipse.gef.dot.internal.language.shape.RecordBasedShape;
import org.eclipse.gef.dot.internal.language.splines.Splines;
import org.eclipse.gef.dot.internal.language.splinetype.Spline;
import org.eclipse.gef.dot.internal.language.splinetype.SplineType;
import org.eclipse.gef.dot.internal.language.style.EdgeStyle;
import org.eclipse.gef.dot.internal.language.style.NodeStyle;
import org.eclipse.gef.dot.internal.language.terminals.ID;
import org.eclipse.gef.dot.internal.ui.DotProperties;
import org.eclipse.gef.fx.nodes.GeometryNode;
import org.eclipse.gef.fx.nodes.OrthogonalRouter;
import org.eclipse.gef.fx.nodes.PolylineInterpolator;
import org.eclipse.gef.fx.nodes.StraightRouter;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Ellipse;
import org.eclipse.gef.geometry.planar.ICurve;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.graph.Edge;
import org.eclipse.gef.graph.Graph;
import org.eclipse.gef.graph.Node;
import org.eclipse.gef.layout.ILayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.RadialLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.SpringLayoutAlgorithm;
import org.eclipse.gef.layout.algorithms.TreeLayoutAlgorithm;
import org.eclipse.gef.zest.fx.ZestProperties;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;

/**
 * A converter that transforms a {@link Graph} that is attributed with
 * {@link DotAttributes} into a {@link Graph} that is attributed with
 * {@link ZestProperties}.
 * 
 * @author anyssen
 *
 */
public class Dot2ZestAttributesConverter implements IAttributeCopier {

	public final static class Options {

		/**
		 * Indicates whether layout should be emulated or not. If set to
		 * <code>true</code>, an {@link ILayoutAlgorithm} is to be inferred for
		 * the given dot, and set as value of the
		 * {@link ZestProperties#LAYOUT_ALGORITHM__G} attribute. If set to
		 * <code>false</code> (i.e. native layout is performed via Graphviz and
		 * position and size information is already provided in the dot input),
		 * the {@link ZestProperties#LAYOUT_ALGORITHM__G} should remain unset.
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
		public boolean invertYAxis = false;
	}

	DotColorUtil colorUtil = new DotColorUtil();
	public final DotFontUtil fontUtil = new DotFontUtil();

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

		String edgeLabelCssStyle = computeZestEdgeLabelCssStyle(dot);
		String targetSourceLabelCssStyle = computeZestTargetSourceLabelCssStyle(
				dot);

		String dotLabel = DotAttributes.getLabel(dot);
		javafx.scene.Node dotLabelFxRepresentant = null;
		if (dotLabel != null) {
			if (DotAttributes.getLabelRaw(dot)
					.getType() == ID.Type.HTML_STRING) {
				DotHTMLLabelJavaFxNode htmlNode = new DotHTMLLabelJavaFxNode(
						colorUtil, fontUtil);
				setHtmlDefaults(dot, htmlNode);
				htmlNode.setLabel(dotLabel);
				htmlNode.refreshFxElement();
				dotLabelFxRepresentant = htmlNode.getMasterFxElement();
				DotProperties.setHtmlLikeLabel(zest, dotLabelFxRepresentant);
			}
			dotLabel = decodeEscString(dotLabel, dot);
			dotLabel = decodeLineBreak(dotLabel);
			ZestProperties.setLabel(zest, dotLabel);
			if (edgeLabelCssStyle != null) {
				ZestProperties.setLabelCssStyle(zest, edgeLabelCssStyle);
			}
			if (dotLabelFxRepresentant == null) {
				dotLabelFxRepresentant = dummyTextNodeWithStyle(dotLabel,
						edgeLabelCssStyle);
			}
		}

		// external label (xlabel)
		String dotXLabel = DotAttributes.getXlabel(dot);
		javafx.scene.Node dotXLabelFxRepresentant = null;
		if (dotXLabel != null) {
			if (DotAttributes.getXlabelRaw(dot)
					.getType() == ID.Type.HTML_STRING) {
				DotHTMLLabelJavaFxNode htmlNode = new DotHTMLLabelJavaFxNode(
						colorUtil, fontUtil);
				setHtmlDefaults(dot, htmlNode);
				htmlNode.setLabel(dotXLabel);
				htmlNode.refreshFxElement();
				dotXLabelFxRepresentant = htmlNode.getMasterFxElement();
				DotProperties.setHtmlLikeExternalLabel(zest,
						dotXLabelFxRepresentant);
			}
			dotXLabel = decodeEscString(dotXLabel, dot);
			dotXLabel = decodeLineBreak(dotXLabel);
			ZestProperties.setExternalLabel(zest, dotXLabel);
			if (edgeLabelCssStyle != null) {
				ZestProperties.setExternalLabelCssStyle(zest,
						edgeLabelCssStyle);
			}
			if (dotXLabelFxRepresentant == null) {
				dotXLabelFxRepresentant = dummyTextNodeWithStyle(dotXLabel,
						edgeLabelCssStyle);
			}
		}

		// head and tail labels (headlabel, taillabel)
		String dotHeadLabel = DotAttributes.getHeadlabel(dot);
		javafx.scene.Node dotHeadLabelFxRepresentant = null;
		if (dotHeadLabel != null) {
			if (DotAttributes.getHeadlabelRaw(dot)
					.getType() == ID.Type.HTML_STRING) {
				DotHTMLLabelJavaFxNode htmlNode = new DotHTMLLabelJavaFxNode(
						colorUtil, fontUtil);
				setHtmlSourceTargetDefaults(dot, htmlNode);
				htmlNode.setLabel(dotHeadLabel);
				htmlNode.refreshFxElement();
				dotHeadLabelFxRepresentant = htmlNode.getMasterFxElement();
				DotProperties.setHtmlLikeTargetLabel(zest,
						dotHeadLabelFxRepresentant);
			}
			dotHeadLabel = decodeEscString(dotHeadLabel, dot);
			dotHeadLabel = decodeLineBreak(dotHeadLabel);
			ZestProperties.setTargetLabel(zest, dotHeadLabel);
			if (targetSourceLabelCssStyle != null) {
				ZestProperties.setTargetLabelCssStyle(zest,
						targetSourceLabelCssStyle);
			}
			if (dotHeadLabelFxRepresentant == null) {
				dotHeadLabelFxRepresentant = dummyTextNodeWithStyle(
						dotHeadLabel, targetSourceLabelCssStyle);
			}
		}
		String dotTailLabel = DotAttributes.getTaillabel(dot);
		javafx.scene.Node dotTailLabelFxRepresentant = null;
		if (dotTailLabel != null) {
			if (DotAttributes.getTaillabelRaw(dot)
					.getType() == ID.Type.HTML_STRING) {
				DotHTMLLabelJavaFxNode htmlNode = new DotHTMLLabelJavaFxNode(
						colorUtil, fontUtil);
				setHtmlSourceTargetDefaults(dot, htmlNode);
				htmlNode.setLabel(dotTailLabel);
				htmlNode.refreshFxElement();
				dotTailLabelFxRepresentant = htmlNode.getMasterFxElement();
				DotProperties.setHtmlLikeSourceLabel(zest,
						dotTailLabelFxRepresentant);
			}
			dotTailLabel = decodeEscString(dotTailLabel, dot);
			dotTailLabel = decodeLineBreak(dotTailLabel);
			ZestProperties.setSourceLabel(zest, dotTailLabel);
			if (targetSourceLabelCssStyle != null) {
				ZestProperties.setSourceLabelCssStyle(zest,
						targetSourceLabelCssStyle);
			}
			if (dotTailLabelFxRepresentant == null) {
				dotTailLabelFxRepresentant = dummyTextNodeWithStyle(
						dotTailLabel, targetSourceLabelCssStyle);
			}
		}

		// convert edge style
		String connectionCssStyle = null;
		Double penwidth = DotAttributes.getPenwidthParsed(dot);

		// style attribute
		String dotStyle = DotAttributes.getStyle(dot);
		if (EdgeStyle.DASHED.toString().equals(dotStyle)) {
			connectionCssStyle = "-fx-stroke-dash-array: 7 7;"; //$NON-NLS-1$
		} else if (EdgeStyle.DOTTED.toString().equals(dotStyle)) {
			connectionCssStyle = "-fx-stroke-dash-array: 1 7;"; //$NON-NLS-1$
		} else if (EdgeStyle.BOLD.toString().equals(dotStyle)
				&& penwidth == null) {
			connectionCssStyle = "-fx-stroke-width: 2;"; //$NON-NLS-1$
		} else if (EdgeStyle.INVIS.toString().equals(dotStyle)) {
			// mark as invisible
			ZestProperties.setInvisible(zest, true);
		}
		// TODO: handle tapered edges
		if (connectionCssStyle == null) {
			connectionCssStyle = "-fx-stroke-line-cap: butt;"; //$NON-NLS-1$
		}
		// direction
		DirType dotDir = DotAttributes.getDirParsed(dot);
		if (dotDir == null) {
			// use the default direction if no direction is specified for
			// the edge
			dotDir = GraphType.DIGRAPH.equals(
					DotAttributes._getType(dot.getGraph().getRootGraph()))
							? DirType.FORWARD
							: DirType.NONE;
		}

		// color
		Color dotColor = null;
		ColorList colorList = DotAttributes.getColorParsed(dot);
		if (colorList != null) {
			EList<WC> colorValues = colorList.getColorValues();
			if (!colorValues.isEmpty()) {
				// TODO: add support for colorList
				dotColor = colorValues.get(0).getColor();
			}
		}

		String dotColorScheme = DotAttributes.getColorscheme(dot);
		String javaFxColor = colorUtil.computeZestColor(dotColorScheme,
				dotColor);
		if (javaFxColor != null) {
			String zestStroke = "-fx-stroke: " + javaFxColor + ";"; //$NON-NLS-1$ //$NON-NLS-2$
			connectionCssStyle += zestStroke;
		}

		// penwidth
		if (penwidth != null) {
			connectionCssStyle += "-fx-stroke-width:" + penwidth + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		ZestProperties.setCurveCssStyle(zest, connectionCssStyle);

		// fillcolor
		Color dotFillColor = DotAttributes.getFillcolorParsed(dot);
		String javaFxFillColor = colorUtil.computeZestColor(dotColorScheme,
				dotFillColor);

		// arrow size
		Double arrowSizeParsed = DotAttributes.getArrowsizeParsed(dot);
		double arrowSize = arrowSizeParsed == null ? 1.0 : arrowSizeParsed;

		// arrow head
		String dotArrowHead = DotAttributes.getArrowhead(dot);
		javafx.scene.Node zestEdgeTargetDecoration = null;
		if (dotArrowHead == null || DotAttributes.getArrowheadParsed(dot)
				.getArrowShapes().isEmpty()) {
			// use the default arrow head decoration in case the graph is
			// directed
			if (GraphType.DIGRAPH.equals(DotAttributes
					._getType(dot.getGraph().getRootGraph().getRootGraph()))) {
				zestEdgeTargetDecoration = DotArrowShapeDecorations.getDefault(
						arrowSize, true, penwidth, javaFxColor,
						javaFxFillColor);
			}
		} else {
			zestEdgeTargetDecoration = computeZestDecoration(
					DotAttributes.getArrowheadParsed(dot), arrowSize, penwidth,
					javaFxColor, javaFxFillColor);
		}

		// The zest edge target decoration should only appear if the edge
		// direction is "forward" or "both".
		if (DirType.FORWARD.equals(dotDir) || DirType.BOTH.equals(dotDir)) {
			ZestProperties.setTargetDecoration(zest, zestEdgeTargetDecoration);
		}

		// arrow tail
		String dotArrowTail = DotAttributes.getArrowtail(dot);
		javafx.scene.Node zestEdgeSourceDecoration = null;
		if (dotArrowTail == null || DotAttributes.getArrowtailParsed(dot)
				.getArrowShapes().isEmpty()) {
			// use the default arrow tail decoration in case the graph is
			// directed
			if (GraphType.DIGRAPH.equals(DotAttributes
					._getType(dot.getGraph().getRootGraph().getRootGraph()))) {
				zestEdgeSourceDecoration = DotArrowShapeDecorations.getDefault(
						arrowSize, true, penwidth, javaFxColor,
						javaFxFillColor);
			}
		} else {
			zestEdgeSourceDecoration = computeZestDecoration(
					DotAttributes.getArrowtailParsed(dot), arrowSize, penwidth,
					javaFxColor, javaFxFillColor);
		}

		// The zest edge source decoration should only appear if the edge
		// direction is "back" or "both".
		if (DirType.BACK.equals(dotDir) || DirType.BOTH.equals(dotDir)) {
			ZestProperties.setSourceDecoration(zest, zestEdgeSourceDecoration);
		}

		// convert tooltip
		EscString dotTooltip = DotAttributes.getTooltipParsed(dot);
		if (dotTooltip != null) {
			String stringTooltip = computeTooltip(dotTooltip);
			String zestTooltip = decodeEscString(stringTooltip, dot);
			ZestProperties.setTooltip(zest, zestTooltip);
		}

		// convert label tooltip
		EscString dotLabelTooltip = DotAttributes.getLabeltooltipParsed(dot);
		if (dotLabelTooltip != null) {
			String stringTooltip = computeTooltip(dotLabelTooltip);
			String zestLabelTooltip = decodeEscString(stringTooltip, dot);
			ZestProperties.setLabelTooltip(zest, zestLabelTooltip);
		}

		// create edge curve
		GeometryNode<ICurve> curve = new GeometryNode<>();
		ZestProperties.setCurve(zest, curve);

		// only convert layout information in native mode, as the results
		// will otherwise not match
		if (!options().emulateLayout) {
			// splines attribute defines connection type
			String splines = DotAttributes
					.getSplines(dot.getGraph().getRootGraph());
			if (Splines.EMPTY.toString().equals(splines)
					|| Splines.NONE.toString().equals(splines)) {
				// mark as invisible
				ZestProperties.setInvisible(zest, true);
			}

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
				if (Splines.LINE.toString().equals(splines)
						|| Splines.FALSE.toString().equals(splines)) {
					// use polyline interpolator
					// use straight router
					// do not use control points
					ZestProperties.setInterpolator(zest,
							new PolylineInterpolator());
					ZestProperties.setRouter(zest, new StraightRouter());
					ZestProperties.setStartPoint(zest,
							bSplineControlPoints.get(0));
					ZestProperties.setEndPoint(zest, bSplineControlPoints
							.get(bSplineControlPoints.size() - 1));
				} else if (Splines.POLYLINE.toString().equals(splines)) {
					// use polyline interpolator
					// use straight router
					// use control points (without start/end) TODO: verify
					ZestProperties.setInterpolator(zest,
							new PolylineInterpolator());
					ZestProperties.setRouter(zest, new StraightRouter());
					ZestProperties.setStartPoint(zest,
							bSplineControlPoints.get(0));
					ZestProperties.setEndPoint(zest, bSplineControlPoints
							.get(bSplineControlPoints.size() - 1));
					ZestProperties.setControlPoints(zest, bSplineControlPoints
							.subList(1, bSplineControlPoints.size() - 1));
				} else if (Splines.ORTHO.toString().equals(splines)) {
					// use polyline interpolator
					// use orthogonal router
					// normalize control points for orthogonal lines
					ZestProperties.setInterpolator(zest,
							new PolylineInterpolator());
					ZestProperties.setRouter(zest, new OrthogonalRouter());
					ZestProperties.setStartPoint(zest,
							bSplineControlPoints.get(0));
					ZestProperties.setEndPoint(zest, bSplineControlPoints
							.get(bSplineControlPoints.size() - 1));
					ZestProperties.setControlPoints(zest,
							computeZestOrthogonalControlPoints(
									bSplineControlPoints));
					// XXX: OrthogonalProjectionStrategy is set within EdgePart
					// when an anchor is attached.
				} else if (Splines.COMPOUND.toString().equals(splines)) {
					// TODO
				} else {
					// splines = spline, true and unset
					// use dot bspline interpolator
					// use dot bspline router
					// use control points (without start/end)
					ZestProperties.setInterpolator(zest,
							new DotBSplineInterpolator());
					// use start/end as reference points for the anchor
					// computation
					ZestProperties.setRouter(zest, new StraightRouter());
					ZestProperties.setStartPoint(zest,
							bSplineControlPoints.get(0));
					ZestProperties.setEndPoint(zest, bSplineControlPoints
							.get(bSplineControlPoints.size() - 1));
					// first and last way point are provided by start and end
					// anchor, so we need to remove them as control points
					ZestProperties.setControlPoints(zest, bSplineControlPoints
							.subList(1, bSplineControlPoints.size() - 1));
				}
			}

			// label position (lp)
			String dotLp = DotAttributes.getLp(dot);
			if (dotLabel != null && dotLp != null
					&& !options().ignorePositions) {
				ZestProperties.setLabelPosition(zest,
						computeZestLabelPosition(DotAttributes.getLpParsed(dot),
								dotLabelFxRepresentant));
			}

			// external label position (xlp)
			String dotXlp = DotAttributes.getXlp(dot);
			if (dotXLabel != null && dotXlp != null
					&& !options().ignorePositions) {
				ZestProperties.setExternalLabelPosition(zest,
						computeZestLabelPosition(
								DotAttributes.getXlpParsed(dot),
								dotXLabelFxRepresentant));
			}
			// head and tail label positions (head_lp, tail_lp)
			String headLp = DotAttributes.getHeadLp(dot);
			if (dotHeadLabel != null && headLp != null
					&& !options().ignorePositions) {
				ZestProperties.setTargetLabelPosition(zest,
						computeZestLabelPosition(
								DotAttributes.getHeadLpParsed(dot),
								dotHeadLabelFxRepresentant));
			}
			String tailLp = DotAttributes.getTailLp(dot);
			if (dotTailLabel != null && tailLp != null
					&& !options().ignorePositions) {
				ZestProperties.setSourceLabelPosition(zest,
						computeZestLabelPosition(
								DotAttributes.getTailLpParsed(dot),
								dotTailLabelFxRepresentant));
			}
		}
	}

	private List<Point> computeZestOrthogonalControlPoints(
			List<Point> bSplineControlPoints) {
		// remove start and end point (both are present twice)
		List<Point> subList = new ArrayList<>(bSplineControlPoints.subList(1,
				bSplineControlPoints.size() - 1));
		// normalize remaining points
		for (int i = subList.size() - 2; i > 0; i--) {
			Point p = subList.get(i + 1);
			Point q = subList.get(i);
			Point r = subList.get(i - 1);
			if (p.x == q.x && q.x == r.x || p.y == q.y && q.y == r.y) {
				// remove q
				subList.remove(i);
			}
		}
		List<Point> subList2 = subList.subList(1, subList.size() - 1);
		return subList2;
	}

	private javafx.scene.Node computeZestDecoration(ArrowType arrowType,
			double arrowSize, Double penwidth, String javaFxColor,
			String javaFxFillColor) {
		return DotArrowShapeDecorations.get(arrowType, arrowSize, penwidth,
				javaFxColor, javaFxFillColor);
	}

	private List<Point> computeZestBSplineControlPoints(Edge dot) {
		SplineType splineType = DotAttributes.getPosParsed(dot);
		List<Point> controlPoints = new ArrayList<>();
		for (Spline spline : splineType.getSplines()) {
			// start
			org.eclipse.gef.dot.internal.language.point.Point startp = spline
					.getStartp();
			if (startp == null) {
				// if we have no start point, add the first control
				// point twice
				startp = spline.getControlPoints().get(0);
			}
			controlPoints.add(new Point(startp.getX(),
					(options().invertYAxis ? -1 : 1) * startp.getY()));

			// control points
			for (org.eclipse.gef.dot.internal.language.point.Point cp : spline
					.getControlPoints()) {
				controlPoints.add(new Point(cp.getX(),
						(options().invertYAxis ? -1 : 1) * cp.getY()));
			}

			// end
			org.eclipse.gef.dot.internal.language.point.Point endp = spline
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

	private String computeZestEdgeLabelCssStyle(Edge dot) {
		Color dotColor = DotAttributes.getFontcolorParsed(dot);
		String dotColorScheme = DotAttributes.getColorscheme(dot);
		FontName dotFont = DotAttributes.getFontnameParsed(dot);
		Double dotSize = DotAttributes.getFontsizeParsed(dot);
		return computeZestLabelCssStyle(dotColor, dotColorScheme, dotFont,
				dotSize);
	}

	String computeZestGraphLabelCssStyle(Graph dot) {
		Color dotColor = DotAttributes.getFontcolorParsed(dot);
		String dotColorScheme = DotAttributes.getColorscheme(dot);
		FontName dotFont = DotAttributes.getFontnameParsed(dot);
		Double dotSize = DotAttributes.getFontsizeParsed(dot);
		return computeZestLabelCssStyle(dotColor, dotColorScheme, dotFont,
				dotSize);
	}

	private String computeZestTargetSourceLabelCssStyle(Edge dot) {
		Color dotColor = DotAttributes.getLabelfontcolorParsed(dot);
		if (dotColor == null) {
			dotColor = DotAttributes.getFontcolorParsed(dot);
		}
		String dotColorScheme = DotAttributes.getColorscheme(dot);
		FontName dotFont = DotAttributes.getLabelfontnameParsed(dot);
		if (dotFont == null) {
			dotFont = DotAttributes.getFontnameParsed(dot);
		}
		Double dotSize = DotAttributes.getLabelfontsizeParsed(dot);
		if (dotSize == null) {
			dotSize = DotAttributes.getFontsizeParsed(dot);
		}
		return computeZestLabelCssStyle(dotColor, dotColorScheme, dotFont,
				dotSize);
	}

	private void setHtmlSourceTargetDefaults(Edge dot,
			DotHTMLLabelJavaFxNode htmlNode) {
		Color dotColor = DotAttributes.getLabelfontcolorParsed(dot);
		if (dotColor == null) {
			dotColor = DotAttributes.getFontcolorParsed(dot);
		}
		String dotColorScheme = DotAttributes.getColorscheme(dot);
		FontName dotFont = DotAttributes.getLabelfontnameParsed(dot);
		if (dotFont == null) {
			dotFont = DotAttributes.getFontnameParsed(dot);
		}
		Double dotSize = DotAttributes.getLabelfontsizeParsed(dot);
		if (dotSize == null) {
			dotSize = DotAttributes.getFontsizeParsed(dot);
		}
		htmlNode.setDefaults(dotFont, dotSize, dotColor, dotColorScheme);
	}

	private void setHtmlDefaults(Edge dot, DotHTMLLabelJavaFxNode htmlNode) {
		htmlNode.setDefaults(DotAttributes.getFontnameParsed(dot),
				DotAttributes.getFontsizeParsed(dot),
				DotAttributes.getFontcolorParsed(dot),
				DotAttributes.getColorscheme(dot));
	}

	private void setHtmlDefaults(Node dot, DotHTMLLabelJavaFxNode htmlNode) {
		htmlNode.setDefaults(DotAttributes.getFontnameParsed(dot),
				DotAttributes.getFontsizeParsed(dot),
				DotAttributes.getFontcolorParsed(dot),
				DotAttributes.getColorscheme(dot));
	}

	private String computeZestLabelCssStyle(Color dotColor,
			String dotColorScheme, FontName dotFont, Double dotSize) {
		StringBuilder zestStyle = new StringBuilder();
		if (dotColor != null) {
			String javaFxColor = colorUtil.computeZestColor(dotColorScheme,
					dotColor);
			if (javaFxColor != null) {
				zestStyle.append("-fx-fill: "); //$NON-NLS-1$
				zestStyle.append(javaFxColor);
				zestStyle.append(";"); //$NON-NLS-1$
			}
		}
		if (dotFont != null) {
			zestStyle.append("-fx-font-family: \""); //$NON-NLS-1$
			zestStyle.append(fontUtil.cssLocalFontFamily(dotFont));
			zestStyle.append("\";"); //$NON-NLS-1$
			zestStyle.append("-fx-font-weight: "); //$NON-NLS-1$
			zestStyle.append(fontUtil.cssWeight(dotFont));
			zestStyle.append(";"); //$NON-NLS-1$
			zestStyle.append("-fx-font-style: "); //$NON-NLS-1$
			zestStyle.append(fontUtil.cssStyle(dotFont));
			zestStyle.append(";"); //$NON-NLS-1$
		}
		if (dotSize != null) {
			zestStyle.append("-fx-font-size: "); //$NON-NLS-1$
			zestStyle.append(dotSize);
			zestStyle.append(";"); //$NON-NLS-1$
		}
		return zestStyle.length() > 0 ? zestStyle.toString() : null;
	}

	protected void convertAttributes(Node dot, Node zest) {
		// for record shape (where Label is consumed by the Zest shape)
		org.eclipse.gef.dot.internal.language.shape.Shape dotShape = DotAttributes
				.getShapeParsed(dot);
		boolean isRecordBasedShape = dotShape != null
				&& dotShape.getShape() instanceof RecordBasedShape;
		DotNodeStyleUtil nodeStyleUtil = isRecordBasedShape
				? new DotRecordBasedNodeStyleUtil(colorUtil, dot)
				: new DotDefaultNodeStyleUtil(colorUtil, dot);

		// id
		String dotId = DotAttributes.getId(dot);
		if (dotId != null) {
			ZestProperties.setCssId(zest, dotId);
		}

		// width and height
		// Convert position and size; as node position is interpreted as
		// center,
		// we need to know the size in order to infer correct zest positions
		String dotHeight = DotAttributes.getHeight(dot);
		String dotWidth = DotAttributes.getWidth(dot);

		// default width is 0.75 inches
		double zestWidth = (dotWidth == null ? 0.75
				: Double.parseDouble(dotWidth)) * 72;
		// default height is 0.5 inches
		double zestHeight = (dotHeight == null ? 0.5
				: Double.parseDouble(dotHeight)) * 72;

		// label
		String dotLabel = DotAttributes.getLabel(dot);
		// The escString '\N' is the node's default label (replaced by
		// the node's name); i.e. the default label is the node's name.
		if (dotLabel == null) {
			dotLabel = "\\N"; //$NON-NLS-1$
		}
		dotLabel = decodeEscString(dotLabel, dot);

		boolean isHtmlLabel = DotAttributes.getLabelRaw(dot) != null
				? DotAttributes.getLabelRaw(dot)
						.getType() == ID.Type.HTML_STRING
				: false;

		if (isHtmlLabel) {
			DotHTMLLabelJavaFxNode htmlNode = new DotHTMLLabelJavaFxNode(
					colorUtil, fontUtil);
			setHtmlDefaults(dot, htmlNode);
			htmlNode.setLabel(dotLabel);
			htmlNode.refreshFxElement();
			DotProperties.setHtmlLikeLabel(zest, htmlNode.getMasterFxElement());
			// TODO redo bounds, prior code
			// Bounds htmlNodeBounds = htmlNode.getBounds();
			// zestWidth = htmlNodeBounds.getWidth();
			// zestHeight = htmlNodeBounds.getHeight();
		}

		// label fontcolor, fontsize, fontname
		String zestNodeLabelCssStyle = computeZestNodeLabelCssStyle(dot);
		if (zestNodeLabelCssStyle != null) {
			ZestProperties.setLabelCssStyle(zest, zestNodeLabelCssStyle);
		}

		// style and color
		StringBuilder zestShapeStyle = nodeStyleUtil.computeZestStyle();

		javafx.scene.Node zestShape = null;
		javafx.scene.Node innerShape = null;
		double innerDistance = 0;
		if (dotShape == null || dotShape.getShape() == null) {
			// ellipse is default shape
			zestShape = new GeometryNode<>(new Ellipse(0, 0, 0, 0));
		} else if (dotShape.getShape() instanceof PolygonBasedShape) {
			PolygonBasedNodeShape polygonShape = ((PolygonBasedShape) dotShape
					.getShape()).getShape();
			zestShape = nodeStyleUtil.hasStyle(NodeStyle.ROUNDED)
					? DotPolygonBasedNodeShapes.getRoundedStyled(polygonShape)
					: DotPolygonBasedNodeShapes.get(polygonShape);
			innerShape = DotPolygonBasedNodeShapes.getInner(polygonShape);
			innerDistance = DotPolygonBasedNodeShapes
					.getInnerDistance(polygonShape);
		} else if (isRecordBasedShape && !isHtmlLabel) {
			// TODO record shapes that have HTML labels
			StringBuilder recordBasedShapeLineStyle = ((DotRecordBasedNodeStyleUtil) nodeStyleUtil)
					.computeLineStyle();

			DotRecordBasedJavaFxNode node = new DotRecordBasedJavaFxNode(
					dotLabel, DotAttributes.getRankdirParsed(dot.getGraph()),
					zestNodeLabelCssStyle,
					recordBasedShapeLineStyle.toString());
			zestShape = node.getFxElement();

			Bounds bounds = node.getBounds();
			zestWidth = Math.max(zestWidth, bounds.getWidth());
			zestHeight = Math.max(zestHeight, bounds.getHeight());
			isRecordBasedShape = true;
		} else {
			// handle custom shapes
		}

		if (zestShape != null) {
			if (zestShapeStyle.length() > 0) {
				if (innerShape != null) {
					String style = zestShapeStyle.toString();
					innerShape.setStyle(style);
					zestShape.setStyle(style.replaceAll("-fx-fill[^;]+;", "")); //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					zestShape.setStyle(zestShapeStyle.toString());
				}
			}
			ZestProperties.setShape(zest, zestShape);
			if (innerShape != null) {
				// TODO: enhance zest capabilities
				zest.attributesProperty().put(DotProperties.INNER_SHAPE__N,
						innerShape);
				zest.attributesProperty().put(
						DotProperties.INNER_SHAPE_DISTANCE__N, innerDistance);
			}
		}

		if (nodeStyleUtil.hasStyle(NodeStyle.INVIS)) {
			ZestProperties.setInvisible(zest, true);
		}

		if (!isRecordBasedShape) {
			// The label of a record based node shape is consumed by the zest
			// shape hence, it needs not to be set again.

			// OrdinaryLabel (should never be null, due to standard label \N)
			dotLabel = decodeLineBreak(dotLabel);

			ZestProperties.setLabel(zest, dotLabel);
		}

		// external label (xlabel)
		String dotXLabel = DotAttributes.getXlabel(dot);
		javafx.scene.Node dotXLabelFxRepresentant = null;
		if (dotXLabel != null) {
			if (DotAttributes.getXlabelRaw(dot)
					.getType() == ID.Type.HTML_STRING) {
				DotHTMLLabelJavaFxNode htmlNode = new DotHTMLLabelJavaFxNode(
						colorUtil, fontUtil);
				setHtmlDefaults(dot, htmlNode);
				htmlNode.setLabel(dotXLabel);
				htmlNode.refreshFxElement();
				dotXLabelFxRepresentant = htmlNode.getMasterFxElement();
				DotProperties.setHtmlLikeExternalLabel(zest,
						dotXLabelFxRepresentant);
			}
			dotXLabel = decodeEscString(decodeLineBreak(dotXLabel), dot);
			ZestProperties.setExternalLabel(zest, dotXLabel);
			if (zestNodeLabelCssStyle != null) {
				ZestProperties.setExternalLabelCssStyle(zest,
						zestNodeLabelCssStyle);
			}
			if (dotXLabelFxRepresentant == null) {
				dotXLabelFxRepresentant = dummyTextNodeWithStyle(dotXLabel,
						zestNodeLabelCssStyle);
			}
		}

		// In case of a record based node shape the label is consumed by the
		// zest shape, hence we do not need to account for label dimensions
		if (options().emulateLayout
				&& !Boolean.TRUE.equals(DotAttributes.getFixedsizeParsed(dot))
				&& !isRecordBasedShape && !isHtmlLabel) {
			// if we are to emulate dot and fixedsize=true is not given, we
			// have to compute the size to enclose image, label, and margin.
			// TODO: also enclose image and margin
			Dimension labelSize = computeZestLabelSize(
					dummyTextNodeWithStyle(dotLabel, zestNodeLabelCssStyle));
			ZestProperties.setSize(zest, Dimension
					.max(new Dimension(zestWidth, zestHeight), labelSize));
		} else {
			ZestProperties.setSize(zest, new Dimension(zestWidth, zestHeight));
		}

		String dotPos = DotAttributes.getPos(dot);
		if (dotPos != null && !options().ignorePositions) {
			// node position is interpreted as center of node in Dot,
			// and top-left in Zest
			org.eclipse.gef.dot.internal.language.point.Point dotPosParsed = DotAttributes
					.getPosParsed(dot);
			ZestProperties.setPosition(zest,
					computeZestPosition(dotPosParsed, zestWidth, zestHeight));
			// if a position is marked as input-only in Dot, have Zest
			// ignore it
			ZestProperties.setLayoutIrrelevant(zest,
					dotPosParsed.isInputOnly());
		}

		// tooltip
		EscString dotTooltip = DotAttributes.getTooltipParsed(dot);
		if (dotTooltip != null) {
			String stringTooltip = computeTooltip(dotTooltip);
			String zestTooltip = decodeEscString(stringTooltip, dot);
			ZestProperties.setTooltip(zest, zestTooltip);
		}

		// external label position (xlp)
		String dotXlp = DotAttributes.getXlp(dot);
		if (dotXLabel != null && dotXlp != null && !options().ignorePositions) {
			org.eclipse.gef.dot.internal.language.point.Point dotXlpParsed = DotAttributes
					.getXlpParsed(dot);
			ZestProperties.setExternalLabelPosition(zest,
					computeZestLabelPosition(dotXlpParsed,
							dotXLabelFxRepresentant));
		}

	}

	private String computeTooltip(EscString dotTooltip) {
		// TODO: consider EscString Justification
		return dotTooltip.getLines().stream().map(JustifiedText::getText)
				.collect(Collectors.joining(System.lineSeparator()));
	}

	private Point computeZestPosition(
			org.eclipse.gef.dot.internal.language.point.Point dotPosition,
			double widthInPixel, double heightInPixel) {
		// dot positions are provided as center positions, Zest uses
		// top-left
		return new Point(dotPosition.getX() - widthInPixel / 2,
				(options().invertYAxis ? -1 : 1) * (dotPosition.getY())
						- heightInPixel / 2);
	}

	private Point computeZestLabelPosition(
			org.eclipse.gef.dot.internal.language.point.Point dotLabelPosition,
			javafx.scene.Node fxNode) {
		Dimension labelSize = computeZestLabelSize(fxNode);
		return computeZestPosition(dotLabelPosition, labelSize.getWidth(),
				labelSize.getHeight());
	}

	javafx.scene.Node dummyTextNodeWithStyle(String labelText, String css) {
		// TODO: respect font settings (font name and size)
		javafx.scene.Node fxNode = new Text(labelText);
		fxNode.setStyle(css);
		return fxNode;
	}

	Dimension computeZestLabelSize(javafx.scene.Node fxNode) {
		Bounds layoutBounds = getBounds(fxNode);
		return new Dimension(layoutBounds.getWidth(), layoutBounds.getHeight());
	}

	private String computeZestNodeLabelCssStyle(Node dot) {
		Color dotColor = DotAttributes.getFontcolorParsed(dot);
		String dotColorScheme = DotAttributes.getColorscheme(dot);
		FontName dotFont = DotAttributes.getFontnameParsed(dot);
		Double dotSize = DotAttributes.getFontsizeParsed(dot);
		return computeZestLabelCssStyle(dotColor, dotColorScheme, dotFont,
				dotSize);
	}

	protected void convertAttributes(Graph dot, Graph zest) {
		// TODO: graph label
		if (options().emulateLayout) {
			// convert layout and rankdir to LayoutAlgorithm
			Object dotLayout = DotAttributes.getLayout(dot);
			ILayoutAlgorithm algo = null;
			if (Layout.CIRCO.toString().equals(dotLayout)
					|| Layout.NEATO.toString().equals(dotLayout)
					|| Layout.TWOPI.toString().equals(dotLayout)) {
				algo = new RadialLayoutAlgorithm();
			} else if (Layout.FDP.toString().equals(dotLayout)
					|| Layout.SFDP.toString().equals(dotLayout)) {
				algo = new SpringLayoutAlgorithm();
			} else if (Layout.OSAGE.toString().equals(dotLayout)) {
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

	private String decodeLineBreak(String text) {
		// TODO implement justification support
		return text.replaceAll("\\\\[lnr]\\Z", "").replaceAll("\\\\[lnr]", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"\n"); //$NON-NLS-1$
	}

	private String decodeEscString(String escString, Edge edge) {
		Node tail = edge.getSource();
		Node head = edge.getTarget();
		Graph graph = edge.getGraph();

		String label = DotAttributes.getLabel(edge);

		String edgeName = DotAttributes._getName(edge);
		String tailName = tail != null ? DotAttributes._getName(tail) : null;
		String headName = head != null ? DotAttributes._getName(head) : null;
		String graphName = graph != null ? DotAttributes._getName(graph) : null;

		/*
		 * \L is replaced first using the raw Label, such that we can avoid a
		 * loop if a label contains \L. As such, we need to double all
		 * backslashes as single backslashes are consumed by replace all.
		 * 
		 * Graphviz behaviour differs slightly for unset names and error
		 * handling, however we cannot reproduce this (i.e. an internally used
		 * variable is produced and for escape sequences invalid in this
		 * context, e.g. \N, graphviz removes the backslash.)
		 */
		return escString.replaceAll("\\\\L", //$NON-NLS-1$
				(label != null ? label : "").replaceAll("\\\\", //$NON-NLS-1$ //$NON-NLS-2$
						"\\\\\\\\")) //$NON-NLS-1$
				.replaceAll("\\\\E", edgeName != null ? edgeName : "") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("\\\\T", tailName != null ? tailName : "") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("\\\\H", headName != null ? headName : "") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("\\\\G", graphName != null ? graphName : ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String decodeEscString(String escString, Node node) {
		Graph graph = node.getGraph();

		String label = DotAttributes.getLabel(node);

		String nodeName = DotAttributes._getName(node);
		String graphName = graph != null ? DotAttributes._getName(graph) : null;

		/*
		 * \L is replaced first using the raw Label, such that we can avoid a
		 * loop if a label contains \L. As such, we need to double all
		 * backslashes as single backslashes are consumed by replace all.
		 * 
		 * For a node, the label defaults to \N.
		 * 
		 * Graphviz behaviour differs slightly for unset names and error
		 * handling, however we cannot reproduce this (i.e. an internally used
		 * variable is produced and for escape sequences invalid in this
		 * context, e.g. \E, graphviz removes the backslash.)
		 */
		return escString.replaceAll("\\\\L", //$NON-NLS-1$
				(label != null ? label : "\\N").replaceAll("\\\\", //$NON-NLS-1$ //$NON-NLS-2$
						"\\\\\\\\")) //$NON-NLS-1$
				.replaceAll("\\\\N", //$NON-NLS-1$
						nodeName != null ? Matcher.quoteReplacement(nodeName)
								: "") //$NON-NLS-1$
				.replaceAll("\\\\G", //$NON-NLS-1$
						graphName != null ? Matcher.quoteReplacement(graphName)
								: ""); //$NON-NLS-1$
	}

	private Options options;

	public Options options() {
		if (options == null) {
			options = new Options();
		}
		return options;
	}

	/**
	 * Bounds for the JavaFxElement after CSS
	 *
	 * @param fxNode
	 *            the fxnode of which the bounds should be calculated
	 * @return the bounds of the node
	 */
	public Bounds getBounds(javafx.scene.Node fxNode) {
		// TODO css class
		Group fxElement = new Group(fxNode);
		@SuppressWarnings("unused") // group layout only works, if the group is
		// included in a scene.
		Scene scene = new Scene(fxElement);
		fxElement.applyCss();
		fxElement.layout();
		return fxElement.getBoundsInParent();
	}

}
