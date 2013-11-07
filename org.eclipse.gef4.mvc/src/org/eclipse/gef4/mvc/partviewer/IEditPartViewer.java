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
package org.eclipse.gef4.mvc.partviewer;

import java.util.Map;

import org.eclipse.gef4.mvc.domain.AbstractEditDomain;
import org.eclipse.gef4.mvc.domain.IEditDomain;
import org.eclipse.gef4.mvc.parts.IEditPart;
import org.eclipse.gef4.mvc.parts.IRootEditPart;

/**
 * An adapter on an SWT {@link org.eclipse.swt.widgets.Control} that manages the
 * {@link org.eclipse.gef4.mvc.parts.IEditPart EditParts}. The viewer is
 * responsible for the editpart lifecycle. Editparts have <i>visuals</i>, such
 * as <code>TreeItems</code> or <code>Figures</code>, which are hosted by the
 * viewer and its control. The viewer provides targeting of editparts via their
 * visuals.
 * 
 * <P>
 * A viewer is a {@link org.eclipse.jface.viewers.ISelectionProvider}. It
 * maintains a list of selected editparts. The last member of this list is the
 * <i>primary</i> member of the selection. The list should never be empty; when
 * no editparts are selected, the viewer's contents editpart is used.
 * 
 * <P>
 * A viewer is populated by setting its <i>contents</i>. This can be done by
 * passing the model corresponding to the contents. The viewer's
 * {@link org.eclipse.gef4.mvc.partviewer.IEditPartFactory EditPartFactory} is
 * then used to create the contents editpart, and add it to the <i>root</i>
 * editpart. Alternatively, the contents editpart itself can be provided. Once
 * the contents editpart is parented, it will populate the rest of the viewer by
 * calling its {@link IEditPart#refresh()} method.
 * 
 * <P>
 * The Root editpart does not correspond to anything in the model, it is used to
 * bootstrap the viewer, and to parent the contents. Depending on the type of
 * viewer being used, it may be common to replace the root editpart. See
 * implementations of {@link org.eclipse.gef4.mvc.parts.IRootEditPart}.
 * 
 * <P>
 * An editpart's lifecycle is managed by the viewer. When the Viewer is
 * realized, meaning it has an SWT <code>Control</code>, it activates its root,
 * which in turn activates all editparts. Editparts are deactivated when they
 * are removed from the viewer. When the viewer's control is disposed, all
 * editparts are similarly deactivated by decativating the root.
 * 
 * <P>
 * A Viewer has an arbitrary collection of keyed properties that can be set and
 * queried. A value of <code>null</code> is used to remove a key from the
 * property map. A viewer will fire property change notification whenever these
 * values are updated.
 * 
 * <P>
 * WARNING: This interface is not intended to be implemented. Clients should
 * extend {@link org.eclipse.gef4.mvc.partviewer.AbstractEditPartViewer}.
 */
public interface IEditPartViewer<V> {

	// /**
	// * Returns <code>null</code> or the <code>EditPart</code> associated with
	// * the specified location. The location is relative to the client area of
	// * the Viewer's <code>Control</code>. An EditPart is not directly visible.
	// * It is targeted using its <i>visual part</i> which it registered using
	// the
	// * {@link #getVisualPartMap() visual part map}. What constitutes a
	// <i>visual
	// * part</i> is viewer-specific. Examples include Figures and TreeItems.
	// *
	// * @param location
	// * The location
	// * @return <code>null</code> or an EditPart
	// */
	// IEditPart getPartAt(Point location);
	//
	// /**
	// * Returns <code>null</code> or the <code>EditPart</code> at the specified
	// * location, excluding the specified set. This method behaves similarly to
	// * {@link #findObjectAt(Point)}.
	// *
	// * @param location
	// * The mouse location
	// * @param exclusionSet
	// * The set of EditParts to be excluded
	// * @return <code>null</code> or an EditPart
	// */
	// IEditPart findObjectAtExcluding(Point location, Collection<IEditPart>
	// exclusionSet);

	/**
	 * Returns the <i>contents</i> of this Viewer. The contents is the EditPart
	 * associated with the top-level model object. It is considered to be
	 * "The Diagram". If the user has nothing selected, the <i>contents</i> is
	 * implicitly the selected object.
	 * <P>
	 * The <i>Root</i> of the Viewer is different. By constrast, the root is
	 * never selected or targeted, and does not correspond to something in the
	 * model.
	 * 
	 * @see #getRootEditPart()
	 * @return the <i>contents</i> <code>EditPart</code>
	 */
	Object getContents();

	/**
	 * Returns the {@link AbstractEditDomain EditDomain} to which this viewer
	 * belongs.
	 * 
	 * @return the viewer's EditDomain
	 */
	IEditDomain<V> getEditDomain();

	/**
	 * Returns the <code>EditPartFactory</code> for this viewer. The
	 * EditPartFactory is used to create the <i>contents</i> EditPart when
	 * {@link #setContents(Object)} is called. It is made available so that
	 * other EditParts can use it to create their children or connection
	 * editparts.
	 * 
	 * @return EditPartFactory
	 */
	IEditPartFactory<V> getEditPartFactory();

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
	Map<Object, IEditPart<V>> getEditPartRegistry();

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
	 * @see #setRootEditPart(IRootEditPart)
	 * @return the RootEditPart
	 */
	IRootEditPart<V> getRootEditPart();

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
	Map<V, IEditPart<V>> getVisualPartMap();

	/**
	 * Reveals the given EditPart if it is not visible.
	 * 
	 * @param editpart
	 *            the EditPart to reveal
	 */
	void reveal(IEditPart<V> editpart);

	/**
	 * Sets the <code>EditDomain</code> for this viewer. The Viewer will route
	 * all mouse and keyboard events to the EditDomain.
	 * 
	 * @param domain
	 *            The EditDomain
	 */
	void setEditDomain(IEditDomain<V> domain);

	/**
	 * Sets the EditPartFactory.
	 * 
	 * @param factory
	 *            the factory
	 * @see #getEditPartFactory()
	 */
	void setEditPartFactory(IEditPartFactory<V> factory);

	/**
	 * Sets the <i>root</i> of this viewer. The root should not be confused with
	 * the <i>contents</i>.
	 * 
	 * @param root
	 *            the RootEditPart
	 * @see #getRootEditPart()
	 * @see #getContents()
	 */
	void setRootEditPart(IRootEditPart<V> root);

}
