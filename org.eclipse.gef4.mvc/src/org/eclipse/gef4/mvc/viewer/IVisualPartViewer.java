/*******************************************************************************
 * Copyright (c) 2014 itemis AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *     
 * Note: Parts of this interface have been transferred from org.eclipse.gef.EditPartViewer.
 * 
 *******************************************************************************/
package org.eclipse.gef4.mvc.viewer;

import java.util.List;
import java.util.Map;

import org.eclipse.gef4.mvc.domain.AbstractDomain;
import org.eclipse.gef4.mvc.domain.IDomain;
import org.eclipse.gef4.mvc.models.IContentModel;
import org.eclipse.gef4.mvc.models.IFocusModel;
import org.eclipse.gef4.mvc.models.IHoverModel;
import org.eclipse.gef4.mvc.models.ISelectionModel;
import org.eclipse.gef4.mvc.models.IZoomModel;
import org.eclipse.gef4.mvc.parts.IContentPart;
import org.eclipse.gef4.mvc.parts.IContentPartFactory;
import org.eclipse.gef4.mvc.parts.IHandlePartFactory;
import org.eclipse.gef4.mvc.parts.IRootPart;
import org.eclipse.gef4.mvc.parts.IVisualPart;

/**
 * 
 * @author anyssen
 * 
 * @param <V>
 */
public interface IVisualPartViewer<V> {

	/**
	 * Returns the {@link AbstractDomain EditDomain} to which this viewer
	 * belongs.
	 * 
	 * @return the viewer's EditDomain
	 */
	IDomain<V> getDomain();

	/**
	 * Returns the {@link Map} for registering <code>EditParts</code> by
	 * <i>Keys</i>. EditParts may register themselves using any method, and may
	 * register themselves with multiple keys. The purpose of such registration
	 * is to allow an EditPart to be found by other EditParts, or by listeners
	 * of domain notifiers. By default, EditParts are registered by their model.
	 * <P>
	 * Some models use a "domain" notification system, in which all changes are
	 * dispatched to a single listener. Such a listener might use this map to
	 * lookup editparts for a given model, and then ask the editpart to update.
	 * 
	 * @return the registry map
	 */
	Map<Object, IContentPart<V>> getContentPartMap();

	/**
	 * Returns the <code>RootEditPart</code>. The RootEditPart is a special
	 * EditPart that serves as the parent to the contents editpart. The
	 * <i>root</i> is never selected. The root does not correspond to anything
	 * in the model. The User does not interact with the root.
	 * <P>
	 * The RootEditPart has a single child: the {@link #getContents() contents}.
	 * <P>
	 * By defining the concept of "root", GEF allows the application's "real"
	 * EditParts to be more homogeneous. For example, all non-root EditParts
	 * have a parent. Also, it allows applications to change the type of root
	 * being used without affecting their own editpart implementation hierarchy.
	 * 
	 * @see #getContents()
	 * @see #setRootPart(IRootPart)
	 * @return the RootEditPart
	 */
	IRootPart<V> getRootPart();

	/**
	 * Returns the {@link Map} for associating <i>visual parts</i> with their
	 * <code>EditParts</code>. This map is used for hit-testing. Hit testing is
	 * performed by first determining which visual part is hit, and then mapping
	 * that part to an <code>EditPart</code>. What consistutes a <i>visual
	 * part</i> is viewer-specific. Examples include <code>Figures</code> and
	 * <code>TreeItems</code>.
	 * 
	 * @return the visual part map
	 */
	Map<V, IVisualPart<V>> getVisualPartMap();

	/**
	 * Reveals the given EditPart if it is not visible.
	 * 
	 * @param editpart
	 *            the EditPart to reveal
	 */
	void reveal(IVisualPart<V> visualPart);

	/**
	 * Sets the <code>EditDomain</code> for this viewer. The Viewer will route
	 * all mouse and keyboard events to the EditDomain.
	 * 
	 * @param domain
	 *            The EditDomain
	 */
	void setDomain(IDomain<V> domain);

	/**
	 * Sets the <i>root</i> of this viewer. The root should not be confused with
	 * the <i>contents</i>.
	 * 
	 * @param root
	 *            the RootEditPart
	 * @see #getRootPart()
	 * @see #getContents()
	 */
	void setRootPart(IRootPart<V> root);

	List<Object> getContents();
	
	void setContents(List<Object> contents);
	/**
	 * Sets the EditPartFactory.
	 * 
	 * @param factory
	 *            the factory
	 * @see #getContentPartFactory()
	 */
	void setContentPartFactory(IContentPartFactory<V> factory);

	/**
	 * Returns the <code>EditPartFactory</code> for this viewer. The
	 * EditPartFactory is used to create the <i>contents</i> EditPart when
	 * {@link #setContents(Object)} is called. It is made available so that
	 * other EditParts can use it to create their children or connection
	 * editparts.
	 * 
	 * @return EditPartFactory
	 */
	IContentPartFactory<V> getContentPartFactory();

	IHandlePartFactory<V> getHandlePartFactory();

	void setHandlePartFactory(IHandlePartFactory<V> factory);

	// selection based on content parts
	ISelectionModel<V> getSelectionModel();

	IHoverModel<V> getHoverModel();

	IFocusModel<V> getFocusModel();

	IZoomModel getZoomModel();

	IContentModel getContentModel();

}
