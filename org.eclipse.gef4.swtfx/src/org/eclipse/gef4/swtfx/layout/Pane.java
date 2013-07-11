package org.eclipse.gef4.swtfx.layout;

import org.eclipse.gef4.swtfx.AbstractParent;
import org.eclipse.gef4.swtfx.IParent;
import org.eclipse.swt.widgets.Composite;

public class Pane extends AbstractParent {

	public Pane(Composite parent) {
		super(parent);
		if (parent instanceof IParent) {
			((IParent) parent).addChildNodes(this);
		}
	}

	@Override
	public void setParentNode(IParent parent) {
		IParent parentNode = getParentNode();
		if (parentNode != null) {
			parentNode.getChildNodes().remove(this);
		}
		parent.getChildNodes().add(this);
	}

}
