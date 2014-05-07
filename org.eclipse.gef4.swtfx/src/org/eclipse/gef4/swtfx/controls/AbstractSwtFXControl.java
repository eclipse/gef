/*******************************************************************************
 * Copyright (c) 2013 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef4.swtfx.controls;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.Scene;

import org.eclipse.gef4.swtfx.SwtFXCanvas;
import org.eclipse.gef4.swtfx.SwtFXScene;
import org.eclipse.swt.widgets.Control;

public abstract class AbstractSwtFXControl<T extends Control> extends
		SwtFXControlAdapter<T> {

	/**
	 * Retrieves the underlying {@link SwtFXCanvas} from a given {@link Node}.
	 * In case no {@link SwtFXCanvas} can be found, <code>null</code> is
	 * returned.
	 *
	 * @param node
	 * @return the {@link SwtFXCanvas} of the given {@link Node} or
	 *         <code>null</code>
	 */
	protected static SwtFXCanvas getSwtFXCanvas(Node node) {
		if (node == null) {
			return null;
		}
		return getSwtFXCanvas(node.getScene());
	}

	protected static SwtFXCanvas getSwtFXCanvas(Scene scene) {
		if (scene != null) {
			if (!(scene instanceof SwtFXScene)) {
				throw new IllegalArgumentException();
			}
			SwtFXCanvas fxCanvas = ((SwtFXScene) scene).getFXCanvas();
			return fxCanvas;
		}
		return null;
	}

	private SwtFXCanvas canvas;
	private ChangeListener<Scene> sceneChangeListener;
	private ChangeListener<SwtFXCanvas> sceneCanvasChangeListener = new ChangeListener<SwtFXCanvas>() {

		@Override
		public void changed(ObservableValue<? extends SwtFXCanvas> observable,
				SwtFXCanvas oldValue, SwtFXCanvas newValue) {
			setCanvas(newValue);
		}
	};

	public AbstractSwtFXControl() {
		sceneChangeListener = new ChangeListener<Scene>() {
			@Override
			public void changed(ObservableValue<? extends Scene> observable,
					Scene oldValue, Scene newValue) {
				setCanvas(getSwtFXCanvas(newValue));
				// hook/unhook scene listeners
				if (oldValue != null) {
					((SwtFXScene) oldValue).canvasProperty().removeListener(
							sceneCanvasChangeListener);
				}
				if (newValue != null) {
					((SwtFXScene) newValue).canvasProperty().addListener(
							sceneCanvasChangeListener);
				}
			}
		};
		sceneProperty().addListener(sceneChangeListener);

	}

	protected abstract T createControl(SwtFXCanvas fxCanvas);

	@Override
	public void dispose() {
		sceneProperty().removeListener(sceneChangeListener);
		super.dispose();
	}

	protected void setCanvas(SwtFXCanvas newCanvas) {
		if (this.canvas != null && this.canvas != newCanvas) {
			T oldControl = getControl();
			setControl(null);
			oldControl.dispose();
			oldControl = null;
		}
		if (newCanvas != null && this.canvas != newCanvas) {
			T newControl = createControl(newCanvas);
			setControl(newControl);
		}
	}

}