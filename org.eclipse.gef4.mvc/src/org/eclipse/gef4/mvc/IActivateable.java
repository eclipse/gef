package org.eclipse.gef4.mvc;

import org.eclipse.gef4.mvc.parts.IRootVisualPart;

public interface IActivateable {

	/**
	 * Activates the EditPart. EditParts that observe a dynamic model or support
	 * editing must be <i>active</i>. Called by the managing EditPart, or the
	 * Viewer in the case of the {@link IRootVisualPart}. This method may be
	 * called again once {@link #deactivate()} has been called.
	 * <P>
	 * During activation the receiver should:
	 * <UL>
	 * <LI>begin to observe its model if appropriate, and should continue the
	 * observation until {@link #deactivate()} is called.
	 * <LI>activate all of its EditPolicies. EditPolicies may also observe the
	 * model, although this is rare. But it is common for EditPolicies to
	 * contribute additional visuals, such as selection handles or feedback
	 * during interactions. Therefore it is necessary to tell the EditPolicies
	 * when to start doing this, and when to stop.
	 * <LI>call activate() on the EditParts it manages. This includes its
	 * children, and for GraphicalEditParts, its <i>source connections</i>.
	 * </UL>
	 */
	public void activate();

	/**
	 * Deactivates the EditPart. EditParts that observe a dynamic model or
	 * support editing must be <i>active</i>. <code>deactivate()</code> is
	 * guaranteed to be called when an EditPart will no longer be used. Called
	 * by the managing EditPart, or the Viewer in the case of the
	 * {@link IRootVisualPart}. This method may be called multiple times.
	 * <P>
	 * During deactivation the receiver should:
	 * <UL>
	 * <LI>remove all listeners that were added in {@link #activate}
	 * <LI>deactivate all of its EditPolicies. EditPolicies may be contributing
	 * additional visuals, such as selection handles or feedback during
	 * interactions. Therefore it is necessary to tell the EditPolicies when to
	 * start doing this, and when to stop.
	 * <LI>call deactivate() on the EditParts it manages. This includes its
	 * children, and for <code>GraphicalEditParts</code>, its <i>source
	 * connections</i>.
	 * </UL>
	 */
	public void deactivate();
	
//	public boolean isActive();

}