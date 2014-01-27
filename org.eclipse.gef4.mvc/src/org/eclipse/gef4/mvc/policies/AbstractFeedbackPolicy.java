package org.eclipse.gef4.mvc.policies;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFeedbackPolicy<V> extends AbstractEditPolicy<V> {

	/**
	 * Returns a list of visuals for which selection feedback should be
	 * presented. Per default, only the host visual is returned.
	 * 
	 * @return list of visuals for which selection feedback is presented
	 */
	public List<V> getSelectionVisuals() {
		ArrayList<V> list = new ArrayList<V>();
		list.add(getHost().getVisual());
		return list;
	}
	
	/**
	 * Adds computed feedback visuals to the feedback layer of your editor.
	 */
	public abstract void showFeedback();
	
	/**
	 * Removes feedback visuals from the feedback layer of your editor.
	 */
	public abstract void hideFeedback();

	/**
	 * Creates feedback visuals for all selection visuals. For every selection
	 * visual returned by {@link #getSelectionVisuals()}, we try to
	 * {@link #createFeedback(Node)}. If feedback is <code>!= null</code>, we
	 * add it as a feedback visual and {@link #applyPosition(Node, Node)}.
	 * Additionally, if a both the selection and the feedback visuals are
	 * instances of {@link Shape}, we {@link #applyPaint(Shape, Shape)} as well.
	 */
	public void createFeedbackVisuals() {
		initFeedback();
		for (V selectionVisual : getSelectionVisuals()) {
			V feedbackVisual = createFeedback(selectionVisual);
			if (feedbackVisual != null) {
				addFeedbackVisual(feedbackVisual);
				applyProperties(selectionVisual, feedbackVisual);
			}
		}
	}

	public abstract void applyProperties(V selectionVisual, V feedbackVisual);

	/**
	 * <pre>feedback.getChildren().add(feedbackVisual);</pre>
	 * @param feedbackVisual
	 */
	public abstract void addFeedbackVisual(V feedbackVisual);

	/**
	 * <pre>feedback = new Group();</pre>
	 */
	public abstract void initFeedback();

	/**
	 * Creates a feedback visual ({@link Node}) for the given selection visual.
	 * Per default, a {@link Rectangle} is created for the selection visual's
	 * layout-bounds.
	 * 
	 * @param selectionVisual
	 * @return
	 */
	public abstract V createFeedback(V selectionVisual);
	
}
