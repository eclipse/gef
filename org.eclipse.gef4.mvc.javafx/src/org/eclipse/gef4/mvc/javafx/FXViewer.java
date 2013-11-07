package org.eclipse.gef4.mvc.javafx;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

import org.eclipse.gef4.mvc.parts.IEditPart;
import org.eclipse.gef4.mvc.parts.IRootEditPart;
import org.eclipse.gef4.mvc.partviewer.AbstractEditPartViewer;

public class FXViewer extends AbstractEditPartViewer<Node> {

	private FXCanvas canvas;

	public FXViewer(FXCanvas canvas) {
		this.canvas = canvas;
		setRootEditPart(new FXRootEditPart());
	}
	
	@Override
	public void setRootEditPart(IRootEditPart<Node> editpart) {
		super.setRootEditPart(editpart);
		if(editpart != null){
			canvas.setScene(new Scene((Parent) editpart.getVisual()));
		}
		else {
			canvas.setScene(null);
		}
	}

	protected FXCanvas getCanvas() {
		return canvas;
	}
	
	@Override
	public void reveal(IEditPart<Node> editpart) {
		// TODO Auto-generated method stub
		
	}
}
