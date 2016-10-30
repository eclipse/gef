package org.eclipse.gef.mvc.tests.fx;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.gef.common.collections.CollectionUtils;
import org.eclipse.gef.fx.nodes.Connection;
import org.eclipse.gef.geometry.planar.AffineTransform;
import org.eclipse.gef.geometry.planar.Dimension;
import org.eclipse.gef.geometry.planar.Point;
import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.mvc.fx.parts.AbstractContentPart;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart;
import org.eclipse.gef.mvc.fx.parts.IBendableContentPart.BendPoint;
import org.junit.Test;

import com.google.common.collect.SetMultimap;

public class BendableTests {

	private static class Bendable extends AbstractContentPart<Connection> implements IBendableContentPart<Connection> {

		private List<BendPoint> contentBendPoints = new ArrayList<>();

		public Bendable(Point... points) {
			for (Point p : points) {
				contentBendPoints.add(new BendPoint(p));
			}
		}

		@Override
		public void setContentBendPoints(List<org.eclipse.gef.mvc.fx.parts.IBendableContentPart.BendPoint> bendPoints) {
			contentBendPoints = bendPoints;
		}

		@Override
		protected Connection doCreateVisual() {
			return new Connection();
		}

		@Override
		protected SetMultimap<? extends Object, String> doGetContentAnchorages() {
			return CollectionUtils.emptySetMultimap();
		}

		@Override
		protected List<? extends Object> doGetContentChildren() {
			return Collections.emptyList();
		}

		@Override
		protected void doRefreshVisual(Connection visual) {
		}

		@Override
		public List<org.eclipse.gef.mvc.fx.parts.IBendableContentPart.BendPoint> getContentBendPoints() {
			return contentBendPoints;
		}

	}

	@Test
	public void test_resize() {
		Point start = new Point(0, 0);
		Point end = new Point(100, 50);
		Bendable bendable = new Bendable(start, end);
		// check content bend points
		List<BendPoint> contentBendPoints = bendable.getContentBendPoints();
		assertEquals(start, contentBendPoints.get(0).getPosition());
		assertEquals(end, contentBendPoints.get(1).getPosition());
		// check size
		Dimension contentSize = bendable.getContentSize();
		assertEquals(new Rectangle(start, end).getSize(), contentSize);
		// check transform (should equal translation to offset)
		Point contentOffset = new Point(bendable.getContentTransform().getTranslateX(),
				bendable.getContentTransform().getTranslateY());
		assertEquals(start, contentOffset);
		// check resize
		Point newEnd = end.getTranslated(0, 50);
		Rectangle newBounds = new Rectangle(start, newEnd);
		bendable.setContentSize(newBounds.getSize());
		assertEquals(newBounds.getSize(), bendable.getContentSize());
		// check content offset did not change
		contentOffset = new Point(bendable.getContentTransform().getTranslateX(),
				bendable.getContentTransform().getTranslateY());
		assertEquals(start, contentOffset);
	}

	@Test
	public void test_translate() {
		Point start = new Point(0, 0);
		Point end = new Point(100, 50);
		Bendable bendable = new Bendable(start, end);
		// check content bend points
		List<BendPoint> contentBendPoints = bendable.getContentBendPoints();
		assertEquals(start, contentBendPoints.get(0).getPosition());
		assertEquals(end, contentBendPoints.get(1).getPosition());
		// check size
		Dimension contentSize = bendable.getContentSize();
		assertEquals(new Rectangle(start, end).getSize(), contentSize);
		// check transform (should equal translation to offset)
		Point contentOffset = new Point(bendable.getContentTransform().getTranslateX(),
				bendable.getContentTransform().getTranslateY());
		assertEquals(start, contentOffset);
		// apply translation
		Point newStart = start.getTranslated(20, 50);
		Point newEnd = end.getTranslated(20, 50);
		Rectangle newBounds = new Rectangle(newStart, newEnd);
		bendable.setContentTransform(new AffineTransform().setToTranslation(newStart.x, newStart.y));
		assertEquals(newBounds.getSize(), bendable.getContentSize());
		contentOffset = new Point(bendable.getContentTransform().getTranslateX(),
				bendable.getContentTransform().getTranslateY());
		assertEquals(newStart, contentOffset);
	}

}
