package org.eclipse.zest.core.widgets;

import org.eclipse.draw2d.IFigure;

/**
 * A Custom Graph Node
 */
public class CGraphNode extends GraphNode {

	IFigure figure = null;

	public CGraphNode(IContainer graphModel, int style, IFigure figure) {
		super(graphModel, style, figure);
	}

	public IFigure getFigure() {
		return super.getFigure();
	}

	protected IFigure createFigureForModel() {
		this.figure = (IFigure) this.getData();
		return this.figure;
	}

}
