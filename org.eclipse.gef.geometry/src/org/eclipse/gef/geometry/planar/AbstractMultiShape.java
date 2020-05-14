/*******************************************************************************
 * Copyright (c) 2012, 2016 itemis AG and others.
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
package org.eclipse.gef.geometry.planar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;

import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;

/**
 * <p>
 * The {@link AbstractMultiShape} class contains an algorithm to find the outer
 * segments of all outline segments of all the outlines of the internal
 * {@link IShape}s of an {@link IMultiShape}. (see {@link #getOutlineSegments()}
 * )
 * </p>
 * <p>
 * Moreover, an algorithm to create closed outline objects for an
 * {@link IMultiShape} is provided. (see {@link #getOutlines()})
 * </p>
 *
 * @author mwienand
 *
 */
abstract class AbstractMultiShape extends AbstractGeometry
		implements IMultiShape {

	private static final long serialVersionUID = 1L;

	/**
	 * <p>
	 * Compares two {@link Point}s by their coordinate values. A {@link Point}
	 * is regarded to be "lower" than another {@link Point} if the first
	 * {@link Point}'s x coordinate is smaller than the x coordinate of the
	 * other {@link Point}. In case of equal x coordinates, the y coordinates
	 * are compared.
	 * </p>
	 * <p>
	 * Returns 0 if the {@link Point}s are equal to each other (see
	 * {@link Point#equals(Object)}).
	 * </p>
	 *
	 * @param o1
	 * @param o2
	 * @return <code>0</code> if the {@link Point}s are equal (Point
	 *         {@link #equals(Object)}), <code>-1</code> if the first
	 *         {@link Point} is "lower" than the second {@link Point}, otherwise
	 *         <code>1</code>
	 */
	private static int comparePoints(Point o1, Point o2) {
		if (o1.equals(o2)) {
			return 0;
		}
		if (o1.x < o2.x) {
			return -1;
		}
		if (o1.x == o2.x) {
			if (o1.y < o2.y) {
				return -1;
			}
		}
		return 1;
	}

	private void assignRemainingSegment(HashMap<Line, Integer> seen,
			Stack<Line> addends, Line toAdd, Point start, Point end) {
		if (!start.equals(end)) {
			Line rest = new Line(start, end);
			if (start.equals(toAdd.getP1()) || start.equals(toAdd.getP2())) {
				addends.push(rest);
			} else {
				seen.put(rest,
						seen.containsKey(rest) && seen.get(rest) == 2 ? 2 : 1);
			}
		}
	}

	@Override
	public boolean contains(Point p) {
		for (IShape s : getShapes()) {
			if (s.contains(p)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Inner segments are identified by a segment count of exactly 2.
	 *
	 * @param seen
	 */
	private void filterOutInnerSegments(HashMap<Line, Integer> seen) {
		for (Line seg : new HashSet<>(seen.keySet())) {
			if (seen.get(seg) == 2) {
				seen.remove(seg);
			}
		}
	}

	private Polyline findOutline(Set<Line> outlineSegments,
			Map<Point, List<Line>> segsAt) {
		// System.out.println("findOutline");

		Set<Point> visited = new HashSet<>();
		Line initial = outlineSegments.iterator().next();
		List<Point> way = findWay(segsAt, visited, initial.getP1(),
				initial.getP2(), 1);

		if (way == null) {
			// System.out.println("Cannot find outline!");
			return new Polyline(new Line[] { initial });
		}

		way.add(0, initial.getP1());

		return new Polyline(
				CurveUtils.toSegmentsArray(way.toArray(new Point[] {}), true));
	}

	/**
	 * Searches for the longest cycle-free way from the given start
	 * {@link Point} to the given end {@link Point} on the given segments.
	 *
	 * @param segmentsByEndPoints
	 * @param visited
	 * @param start
	 * @param end
	 * @param indent
	 * @return
	 */
	private List<Point> findWay(Map<Point, List<Line>> segmentsByEndPoints,
			Set<Point> visited, Point start, Point end, int indent) {
		// System.out.printf("%" + indent + "s", " ");
		// System.out.println("findWay from " + start + " to " + end);

		if (segmentsByEndPoints.get(end) == segmentsByEndPoints.get(start)) {
			// System.out.printf("%" + indent + "s", " ");
			// System.out.println("#closed");
			return new ArrayList<>(0);
		}

		visited.add(start);

		// find unvisited neighbors
		@SuppressWarnings("unchecked")
		List<Line> nextSegs = (List<Line>) ((ArrayList<Line>) segmentsByEndPoints
				.get(start)).clone();
		for (Iterator<Line> i = nextSegs.iterator(); i.hasNext();) {
			Line l = i.next();
			// System.out.printf("%" + indent + "s", " ");
			// System.out.print(l + "? ");
			if (l.getP1().equals(start)) {
				if (visited.contains(l.getP2())) {
					// System.out.print("delete");
					i.remove();
				}
			} else if (visited.contains(l.getP1())) {
				// System.out.print("delete");
				i.remove();
			}
			// System.out.println();
		}

		if (nextSegs.size() == 0) {
			// System.out.printf("%" + indent + "s", " ");
			// System.out.println("#null");
			return null;
		} else if (nextSegs.size() == 1) {
			// System.out.printf("%" + indent + "s", " ");
			// System.out.println("#single");
			Line nextSeg = nextSegs.get(0);
			Point nextPoint = start.equals(nextSeg.getP1()) ? nextSeg.getP2()
					: nextSeg.getP1();
			List<Point> way = findWay(segmentsByEndPoints, visited, nextPoint,
					end, indent + 1);
			if (way != null) {
				way.add(0, nextPoint);
			}
			return way;
		}

		// System.out.printf("%" + indent + "s", " ");
		// System.out.println("#multiple");

		// multiple possibilities, save visited
		int longestWayLength = -1;
		List<Point> longestWay = null;
		for (Line nextSeg : nextSegs) {
			@SuppressWarnings("unchecked")
			Set<Point> visitedCopy = (Set<Point>) ((HashSet<Point>) visited)
					.clone();
			Point nextPoint = start.equals(nextSeg.getP1()) ? nextSeg.getP2()
					: nextSeg.getP1();
			List<Point> way = findWay(segmentsByEndPoints, visitedCopy,
					nextPoint, end, indent + 1);
			if (way != null && way.size() >= longestWayLength) {
				way.add(0, nextPoint);
				longestWay = way;
				longestWayLength = way.size();
				// System.out.printf("%" + indent + "s", " ");
				// System.out.println("#longest = " + longestWayLength);
			}
		}

		// is it possible to have longestWay == null here?
		return longestWay;
	}

	/**
	 * Collects all edges of the internal {@link IShape}s. For a {@link Region}
	 * the internal {@link IShape}s are {@link Rectangle}s. For a {@link Ring}
	 * the internal {@link IShape}s are {@link Polygon}s (triangles).
	 *
	 * The internal edges are needed to determine inner and outer segments of
	 * the {@link IMultiShape}. Based on the outline of the {@link IMultiShape},
	 * the outline intersections can be computed. These outline intersections
	 * are required to test if an {@link ICurve} is fully-contained by the
	 * {@link IMultiShape}.
	 *
	 * @return the edges of all internal {@link IShape}s
	 */
	abstract protected Line[] getAllEdges();

	@Override
	public Polyline[] getOutlines() {
		List<Polyline> outlines = new ArrayList<>();
		Map<Point, List<Line>> segmentsByEndPoints = new HashMap<>();
		Set<Line> outlineSegments = new HashSet<>();

		for (Line seg : getOutlineSegments()) {
			// if (comparePoints(seg.getP1(), seg.getP2()) == 1) {
			// seg = new Line(seg.getP2(), seg.getP1());
			// }
			outlineSegments.add(seg);
		}

		// constructs segments tree
		for (Line seg : outlineSegments) {
			if (!segmentsByEndPoints.containsKey(seg.getP1())) {
				ArrayList<Line> segList = new ArrayList<>();
				segmentsByEndPoints.put(seg.getP1(), segList);
			}
			if (!segmentsByEndPoints.containsKey(seg.getP2())) {
				ArrayList<Line> segList = new ArrayList<>();
				segmentsByEndPoints.put(seg.getP2(), segList);
			}
			segmentsByEndPoints.get(seg.getP1()).add(seg);
			segmentsByEndPoints.get(seg.getP2()).add(seg);
		}

		// search for broken end points
		// List<Point> unconnectedEndPoints = new ArrayList<Point>();
		for (Point p : segmentsByEndPoints.keySet()) {
			List<Line> segments = segmentsByEndPoints.get(p);
			if (segments.size() < 2) {
				throw new IllegalStateException("There is an end point (" + p
						+ ") which is not connected to two segments!");
				// // unconnectedEndPoints.add(p);
				// if (segments.size() == 0) {
				// System.out.println("error: unconnected end point " + p);
				// } else {
				// assert segments.size() == 1;
				// System.out.println("error: loose end point " + p
				// + ", segment = " + segments.get(0));
				// // unconnectedEndPoints.add(segments.get(0).getP1());
				// // unconnectedEndPoints.add(segments.get(0).getP2());
				// }
				// System.out.println(" | remove point/segment from tree");
			}
		}

		while (!outlineSegments.isEmpty()) {
			Polyline outline = findOutline(outlineSegments,
					segmentsByEndPoints);
			// System.out.println("outline: " + outline);
			outlines.add(outline);

			// Remove the segments of the previously found outline from the set
			// of remaining outline segments.
			for (Line outlineSeg : CurveUtils
					.toSegmentsArray(outline.getPoints(), false)) {
				if (comparePoints(outlineSeg.getP1(),
						outlineSeg.getP2()) == 1) {
					outlineSeg = new Line(outlineSeg.getP2(),
							outlineSeg.getP1());
				}
				outlineSegments.remove(outlineSeg);
			}
		}

		// System.out.println("Found " + outlines.size() + " outlines.");

		return outlines.toArray(new Polyline[] {});
	}

	/**
	 * <p>
	 * Computes the outline segments of this {@link AbstractMultiShape}.
	 * </p>
	 * <p>
	 * The outline segments of this {@link AbstractMultiShape} are those outline
	 * segments of the internal {@link IShape}s that only exist once.
	 * </p>
	 *
	 * @return the outline segments of this {@link AbstractMultiShape}
	 */
	@Override
	public Line[] getOutlineSegments() {
		HashMap<Line, Integer> seen = new HashMap<>();
		Stack<Line> elementsToAdd = new Stack<>();
		for (Line e : getAllEdges()) {
			elementsToAdd.push(e);
		}

		addingElements: while (!elementsToAdd.empty()) {
			Line toAdd = elementsToAdd.pop();
			for (Line seg : new HashSet<>(seen.keySet())) {
				if (seg.overlaps(toAdd)) {
					Point[] p = getSortedEndpoints(toAdd, seg);
					seen.remove(seg);
					assignRemainingSegment(seen, elementsToAdd, toAdd, p[0],
							p[1]);
					assignRemainingSegment(seen, elementsToAdd, toAdd, p[3],
							p[2]);
					markOverlap(seen, p[1], p[2]);
					continue addingElements;
				}
			}
			seen.put(toAdd, 1);
		}

		filterOutInnerSegments(seen);

		return seen.keySet().toArray(new Line[] {});
	}

	/**
	 * Sorts the end {@link Point}s of two {@link Line}s that do overlap by
	 * their coordinate values.
	 *
	 * @param toAdd
	 * @param seg
	 * @return the sorted {@link Point}s
	 */
	private Point[] getSortedEndpoints(Line toAdd, Line seg) {
		final Point[] p = new Point[] { seg.getP1(), seg.getP2(), toAdd.getP1(),
				toAdd.getP2() };
		Arrays.sort(p, new Comparator<Point>() {
			@Override
			public int compare(Point p1, Point p2) {
				if (PrecisionUtils.equal(p1.x, p2.x)) {
					return p1.y < p2.y ? 1 : -1;
				}
				return p1.x < p2.x ? 1 : -1;
			}
		});
		return p;
	}

	/**
	 * Marks a given segment from start to end {@link Point} as an overlap in
	 * the seen {@link HashMap} if the segment is not degenerated, i.e. it is
	 * not just a single {@link Point}.
	 *
	 * @param seen
	 * @param start
	 * @param end
	 */
	private void markOverlap(HashMap<Line, Integer> seen, Point start,
			Point end) {
		if (!start.equals(end)) {
			// Count an overlapping segment twice to assure that it is going to
			// get deleted afterwards.
			Line overlap = new Line(start, end);
			seen.put(overlap, 2);
		}
	}

	@Override
	public Path toPath() {
		return toPath(Path::exclusiveOr);
	}

	/**
	 * Computes a {@link Path} for this {@link AbstractMultiShape} by combining
	 * the {@link Path} representations of the individual {@link #getOutlines()
	 * outlines} using the given <i>pathCombinator</i> ({@link BiFunction}). The
	 * <i>pathCombinator</i> is used as a folding operator, i.e. for three
	 * outlines A, B, and C, the <i>pathCombinator</i> is used as follows:
	 * <ol>
	 * <li><code>path = A.toPath();</code></li>
	 * <li><code>path = pathCombinator(path, B);</code></li>
	 * <li><code>path = pathCombinator(path, C);</code></li>
	 * </ol>
	 *
	 * @param pathCombinator
	 *            The {@link BiFunction} that is used to combine two consecutive
	 *            {@link Path}s to a result {@link Path}.
	 * @return The result of folding the (<i>closed</i>) {@link #getOutlines()
	 *         outlines} of this {@link AbstractMultiShape} using the given
	 *         <i>pathCombinator</i>.
	 */
	private Path toPath(BiFunction<Path, Path, Path> pathCombinator) {
		Polyline[] outlines = getOutlines();
		if (outlines == null || outlines.length < 1) {
			return new Path();
		}
		Path path = outlines[0].toPath().close();
		for (int i = 1; i < outlines.length; i++) {
			path = pathCombinator.apply(path, outlines[i].toPath().close());
		}
		return path;
	}

}
