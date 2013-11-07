package org.eclipse.gef4.mvc.javafx;

import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;

import org.eclipse.gef4.mvc.policies.AbstractEditPolicy;
import org.eclipse.gef4.mvc.policies.ISelectionPolicy;

public class FXSelectionPolicy extends AbstractEditPolicy<Node> implements ISelectionPolicy<Node> {

	private Effect primarySelectionEffect;
	private Effect secondarySelectionEffect;
	
	public FXSelectionPolicy() {
		primarySelectionEffect = createPrimarySelectionEffect();
		secondarySelectionEffect = createSecondarySelectionEffect();
	}

	protected Effect createPrimarySelectionEffect() {
		DropShadow effect = new DropShadow(4.0, Color.BLACK);
		effect.setOffsetX(4.0);
		effect.setOffsetY(4.0);
		return effect;
	}
	
	protected Effect createSecondarySelectionEffect() {
		DropShadow effect = new DropShadow(4.0, Color.GREY);
		effect.setOffsetX(4.0);
		effect.setOffsetY(4.0);
		return effect;
	}

	@Override
	public void selectPrimary() {
		getHost().getVisual().setEffect(primarySelectionEffect);
	}

	@Override
	public void becomeSecondary() {
		getHost().getVisual().setEffect(secondarySelectionEffect);	
	}

	@Override
	public void deselect() {
		getHost().getVisual().setEffect(null);
	}

}
