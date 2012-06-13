/*******************************************************************************
 * Copyright (c) 2012 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *     
 *******************************************************************************/
package org.eclipse.gef4.geometry.planar;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

import org.eclipse.gef4.geometry.Point;
import org.eclipse.gef4.geometry.utils.PrecisionUtils;

/**
 * The {@link AbstractPolyShape} class contains an algorithm to find the outline
 * segments of an object of an inheriting class.
 * 
 */
public abstract class AbstractPolyShape extends AbstractGeometry implements
		IPolyShape {

	private static final long serialVersionUID = 1L;

	private void assignRemainingSegment(HashMap<Line, Integer> seen,
			Stack<Line> addends, Line toAdd, Point start, Point end) {
		if (!start.equals(end)) {
			Line rest = new Line(start, end);
			if (start.equals(toAdd.getP1()) || start.equals(toAdd.getP2())) {
				// System.out
				// .println("    pushing rest (" + rest + ") to addends");
				addends.push(rest);
			} else {
				// System.out.println("    marking rest (" + rest +
				// ") as seen");
				seen.put(rest,
						seen.containsKey(rest) && seen.get(rest) == 2 ? 2 : 1);
			}
		}
	}

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
		for (Line seg : new HashSet<Line>(seen.keySet())) {
			if (seen.get(seg) == 2) {
				seen.remove(seg);
			}
		}
	}

	/**
	 * Collects all edges of the internal {@link IShape}s. For a {@link Region}
	 * the internal {@link IShape}s are {@link Rectangle}s. For a {@link Ring}
	 * the internal {@link IShape}s are {@link Polygon}s (triangles).
	 * 
	 * The internal edges are needed to determine inner and outer segments of
	 * the {@link IPolyShape}. Based on the outline of the {@link IPolyShape},
	 * the outline intersections can be computed. These outline intersections
	 * are required to test if an {@link ICurve} is fully-contained by the
	 * {@link IPolyShape}.
	 * 
	 * @return the edges of all internal {@link IShape}s
	 */
	abstract protected Line[] getAllEdges();

	/**
	 * Computes the outline of this {@link AbstractPolyShape}.
	 * 
	 * @return the outline of this {@link AbstractPolyShape}
	 * @see #getOutlineSegments()
	 */
	public Polyline getOutline() {
		return new Polyline(getOutlineSegments());
	}

	/**
	 * Computes the outline segments of this {@link AbstractPolyShape}.
	 * 
	 * The outline segments are those outline segments of the internal
	 * {@link Rectangle}s that only exist once.
	 * 
	 * @return the outline segments of this {@link AbstractPolyShape}
	 */
	public Line[] getOutlineSegments() {
		// System.out.println("collecting all edges...");
		HashMap<Line, Integer> seen = new HashMap<Line, Integer>();
		Stack<Line> elementsToAdd = new Stack<Line>();
		for (Line e : getAllEdges())
			elementsToAdd.push(e);

		int c = 0;
		addingElements: while (c++ < 1000 && !elementsToAdd.empty()) {
			Line toAdd = elementsToAdd.pop();
			// System.out.println("adding " + toAdd + "...");
			for (Line seg : new HashSet<Line>(seen.keySet())) {
				if (seg.overlaps(toAdd)) {
					// System.out.println("  overlaps with " + seg);
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
			// System.out.println("  did not overlap");
			seen.put(toAdd, 1);
		}

		// System.out.println("filter out inner segments...");
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
		final Point[] p = new Point[] { seg.getP1(), seg.getP2(),
				toAdd.getP1(), toAdd.getP2() };
		Arrays.sort(p, new Comparator<Point>() {
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
	private void markOverlap(HashMap<Line, Integer> seen, Point start, Point end) {
		if (!start.equals(end)) {
			// Count an overlapping segment twice to assure that it is going to
			// get deleted afterwards.
			Line overlap = new Line(start, end);
			seen.put(overlap, 2);
			// System.out.println("    mark segment " + overlap +
			// " as overlap");
		}
	}

}
