package org.eclipse.gef4.zest.fx;

import org.eclipse.gef4.layout.interfaces.LayoutContext;
import org.eclipse.gef4.mvc.IPropertyChangeSupport;

public interface ILayoutModel extends IPropertyChangeSupport {

	public static final String LAYOUT_CONTEXT_PROPERTY = "layoutContext";

	public LayoutContext getLayoutContext();

	public void setLayoutContext(LayoutContext context);

}
