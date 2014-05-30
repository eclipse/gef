package org.eclipse.gef4.zest.fx.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.List;

import org.eclipse.gef4.fx.widgets.FXLabeledNode;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graph.Edge;
import org.eclipse.gef4.graph.Graph;
import org.eclipse.gef4.graph.Node;
import org.eclipse.gef4.layout.algorithms.GridLayoutAlgorithm;
import org.eclipse.gef4.layout.interfaces.EntityLayout;
import org.eclipse.gef4.zest.fx.NodeContentPart;
import org.eclipse.gef4.zest.layout.GraphLayoutContext;
import org.eclipse.gef4.zest.layout.GraphNodeLayout;
import org.junit.Test;

public class LayoutToViewTests {

	private static final GridLayoutAlgorithm LAYOUT_ALGORITHM = new GridLayoutAlgorithm() {
		{
			setResizing(true);
		}
	};
	public static final double DELTA = 0.0001;

	@Test
	public void test_1_0() {
		// construct graph
		final List<Node> nodes = DataToLayoutTests.nodes("1");
		final List<Edge> edges = DataToLayoutTests.edges(nodes);
		final Graph graph = new Graph(DataToLayoutTests.ATTR_EMPTY, nodes,
				edges);
		final GraphLayoutContext glc = new GraphLayoutContext(graph);

		// fill layout data
		final double w = 30;
		final double h = 40;
		final double x = 1;
		final double y = 2;
		NodeContentPart part = new NodeContentPart(nodes.get(0)) {
			@Override
			public void adaptLayout() {
				nodeLayout = glc.getNodeLayout(nodes.get(0));
				nodeLayout.setLocation(x, y);
				nodeLayout.setSize(w, h);
				super.adaptLayout();
			}
		};

		// adapt data
		part.adaptLayout();
		// check if visual properties equal layout data
		FXLabeledNode node = (FXLabeledNode) part.getVisual();
		assertEquals(w, node.getBoxWidth(), DELTA);
		assertEquals(h, node.getBoxHeight(), DELTA);
		assertEquals(x, node.getLayoutX(), DELTA);
		assertEquals(y, node.getLayoutY(), DELTA);
	}

	@Test
	public void test_2_1() {
		// construct graph
		final List<Node> nodes = DataToLayoutTests.nodes("1", "2");
		final List<Edge> edges = DataToLayoutTests.edges(nodes, 0, 1);
		final Graph graph = new Graph(DataToLayoutTests.ATTR_EMPTY, nodes,
				edges);
		final GraphLayoutContext glc = new GraphLayoutContext(graph);

		// fill layout data
		final double w1 = 30;
		final double h1 = 40;
		final double x1 = 1;
		final double y1 = 2;
		NodeContentPart part1 = new NodeContentPart(nodes.get(0)) {
			@Override
			public void adaptLayout() {
				nodeLayout = glc.getNodeLayout(nodes.get(0));
				nodeLayout.setLocation(x1, y1);
				nodeLayout.setSize(w1, h1);
				super.adaptLayout();
			}
		};
		final double w2 = 6;
		final double h2 = 5;
		final double x2 = 40;
		final double y2 = 30;
		NodeContentPart part2 = new NodeContentPart(nodes.get(1)) {
			@Override
			public void adaptLayout() {
				nodeLayout = glc.getNodeLayout(nodes.get(1));
				nodeLayout.setLocation(x2, y2);
				nodeLayout.setSize(w2, h2);
				super.adaptLayout();
			}
		};

		// adapt data
		part1.adaptLayout();
		part2.adaptLayout();
		// check if visual properties equal layout data
		FXLabeledNode node = (FXLabeledNode) part1.getVisual();
		assertEquals(w1, node.getBoxWidth(), DELTA);
		assertEquals(h1, node.getBoxHeight(), DELTA);
		assertEquals(x1, node.getLayoutX(), DELTA);
		assertEquals(y1, node.getLayoutY(), DELTA);
		node = (FXLabeledNode) part2.getVisual();
		assertEquals(w2, node.getBoxWidth(), DELTA);
		assertEquals(h2, node.getBoxHeight(), DELTA);
		assertEquals(x2, node.getLayoutX(), DELTA);
		assertEquals(y2, node.getLayoutY(), DELTA);
	}

	@Test
	public void test_not_movable() {
		// construct graph
		final List<Node> nodes = DataToLayoutTests.nodes("1");
		final List<Edge> edges = DataToLayoutTests.edges(nodes);
		final Graph graph = new Graph(DataToLayoutTests.ATTR_EMPTY, nodes,
				edges);
		final GraphLayoutContext glc = new GraphLayoutContext(graph);
		glc.setBounds(new Rectangle(0, 0, 500, 500));
		glc.setStaticLayoutAlgorithm(LAYOUT_ALGORITHM);

		// fill layout data
		final double x = 1;
		final double y = 2;
		final double w = 10;
		final double h = 20;

		NodeContentPart part = new NodeContentPart(nodes.get(0)) {
			@Override
			public void adaptLayout() {
				nodeLayout = glc.getNodeLayout(nodes.get(0));
				nodeLayout.setProperty(EntityLayout.MOVABLE_PROPERTY, false);
				nodeLayout
						.setProperty(GraphNodeLayout.RESIZABLE_PROPERTY, true);
				nodeLayout.setLocation(x, y);
				nodeLayout.setSize(w, h);
				super.adaptLayout();
				glc.applyStaticLayout(true);
				super.adaptLayout();
			}
		};

		// adapt data
		part.adaptLayout();
		// check if visual properties equal layout data
		FXLabeledNode node = (FXLabeledNode) part.getVisual();
		assertEquals(x, node.getLayoutX(), DELTA);
		assertEquals(y, node.getLayoutY(), DELTA);
		assertNotEquals(w, node.getBoxWidth(), DELTA);
		assertNotEquals(h, node.getBoxHeight(), DELTA);
	}

	@Test
	public void test_not_resizable() {
		// construct graph
		final List<Node> nodes = DataToLayoutTests.nodes("1");
		final List<Edge> edges = DataToLayoutTests.edges(nodes);
		final Graph graph = new Graph(DataToLayoutTests.ATTR_EMPTY, nodes,
				edges);
		final GraphLayoutContext glc = new GraphLayoutContext(graph);
		glc.setBounds(new Rectangle(0, 0, 500, 500));
		glc.setStaticLayoutAlgorithm(LAYOUT_ALGORITHM);

		// fill layout data
		final double x = 500;
		final double y = 500;
		final double w = 10;
		final double h = 20;

		NodeContentPart part = new NodeContentPart(nodes.get(0)) {
			@Override
			public void adaptLayout() {
				nodeLayout = glc.getNodeLayout(nodes.get(0));
				nodeLayout.setProperty(GraphNodeLayout.RESIZABLE_PROPERTY,
						false);
				nodeLayout.setLocation(x, y);
				nodeLayout.setSize(w, h);
				super.adaptLayout();
				glc.applyStaticLayout(true);
				super.adaptLayout();
			}
		};

		// adapt data
		part.adaptLayout();
		// check if visual properties equal layout data
		FXLabeledNode node = (FXLabeledNode) part.getVisual();
		assertEquals(w, node.getBoxWidth(), DELTA);
		assertEquals(h, node.getBoxHeight(), DELTA);
		assertNotEquals(x, node.getLayoutX(), DELTA);
		assertNotEquals(y, node.getLayoutY(), DELTA);
	}

}
