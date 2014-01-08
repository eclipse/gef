/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef4.mvc.tools;

import org.eclipse.gef4.mvc.IActivatable;
import org.eclipse.gef4.mvc.domain.AbstractEditDomain;
import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.partviewer.IVisualPartViewer;

/**
 * A <code>Tool</code> interprets Mouse and Keyboard input from an
 * {@link AbstractEditDomain} and its {@link IVisualPartViewer EditPartViewers}.
 * The active Tool and its state determines how the EditDomain will interpret
 * input. Input flows from a Viewer, to the EditDomain, to the EditDomain's
 * active Tool.
 * <P>
 * <code>Tools</code> process low-level events and turn them into higher-level
 * operations. These operations are encapsulated by {@link IRequest Requests}.
 * The Requests are then used to communicate with EditParts in the Viewer to
 * perform the User's operation. Using Requests, Tools will:
 * <UL>
 * <LI>Ask EditParts for {@link org.eclipse.gef4.mvc.commands.AbstractCommand
 * Commands} to perform changes on the model.
 * <LI>Ask EditParts to show and erase feedback during an operation.
 * <LI>Ask EditParts to perform a generic function, using
 * {@link org.eclipse.gef4.mvc.parts.IEditPart#performRequest(IRequest)}.
 * <P>
 * Tools also perform operations that do not involve the EditParts directly,
 * such as changing the Viewer's selection, scrolling the viewer, or invoking an
 * {@link org.eclipse.jface.action.IAction Action}.
 * <table>
 * <tr>
 * <td valign=top><img src="doc-files/important.gif"/>
 * <td>All feedback should be erased and temporary changes reverted prior to
 * executing any command.
 * </tr>
 * <tr>
 * <td valign=top><img src="doc-files/important.gif"/>
 * <td>Tools should process most keystrokes. For example, the DELETE key should
 * <EM>not</EM> be handled by adding a KeyListener to the Viewer's Control.
 * Doing so would mean that pressing DELETE would <EM>not</EM> be sensitive to
 * which Tool is currently active, and the state of the Tool. See
 * {@link org.eclipse.gef.KeyHandler} for how keystrokes are generally
 * processed.
 * </tr>
 * </table>
 * <p>
 * IMPORTANT: This interface is <EM>not</EM> intended to be implemented by
 * clients. Clients should inherit from
 * {@link org.eclipse.gef4.mvc.tools.AbstractTool}. New methods may be added in
 * the future.
 */
public interface ITool<V> extends IActivatable {

	/**
	 * Called to set the EditDomain for this Tool. This is called right before
	 * {@link #activate()}.
	 * 
	 * @param domain
	 *            The EditDomain to which this Tool belongs
	 */
	void setDomain(IEditDomain<V> domain);

	IEditDomain<V> getDomain();

	// TODO: tools/handles should change the cursor
}
