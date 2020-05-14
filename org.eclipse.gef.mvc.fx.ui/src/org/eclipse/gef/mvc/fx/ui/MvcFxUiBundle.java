/*******************************************************************************
 * Copyright (c) 2015, 2017 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Alexander Nyßen (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.fx.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * The {@link MvcFxUiBundle} is the plug-in that integrates MVC.FX with the
 * Eclipse platform UI.
 *
 * @author anyssen
 *
 */
public class MvcFxUiBundle extends AbstractUIPlugin {

	/**
	 * The plug-in ID.
	 */
	public static final String PLUGIN_ID = "org.eclipse.gef.mvc.fx.ui"; //$NON-NLS-1$

	/**
	 * Symbolic name for the zoom-in icon image.
	 */
	public static final String IMG_ICONS_ZOOM_IN = "IMG_ICONS_ZOOM_IN";

	/**
	 * Symbolic name for the zoom-out icon image.
	 */
	public static final String IMG_ICONS_ZOOM_OUT = "IMG_ICONS_ZOOM_OUT";

	/**
	 * Symbolic name for the scroll-center icon image.
	 */
	public static final String IMG_ICONS_SCROLL_CENTER = "IMG_ICONS_SCROLL_CENTER";

	/**
	 * Symbolic name for the scroll-top-left icon image.
	 */
	public static final String IMG_ICONS_SCROLL_TOP_LEFT = "IMG_ICONS_SCROLL_TOP_LEFT";

	/**
	 * Symbolic name for the scroll-top-right icon image.
	 */
	public static final String IMG_ICONS_SCROLL_TOP_RIGHT = "IMG_ICONS_SCROLL_TOP_RIGHT";

	/**
	 * Symbolic name for the scroll-bottom-left icon image.
	 */
	public static final String IMG_ICONS_SCROLL_BOTTOM_LEFT = "IMG_ICONS_SCROLL_BOTTOM_LEFT";

	/**
	 * Symbolic name for the scroll-bottom-right icon image.
	 */
	public static final String IMG_ICONS_SCROLL_BOTTOM_RIGHT = "IMG_ICONS_SCROLL_BOTTOM_RIGHT";

	/**
	 * Symbolic name for the fit-to-viewport icon image.
	 */
	public static final String IMG_ICONS_FIT_TO_VIEWPORT = "IMG_ICONS_FIT_TO_VIEWPORT";

	/**
	 * Symbolic name for the fit-to-viewport-lock (i.e. toggle-button/check-box)
	 * icon image.
	 */
	public static final String IMG_ICONS_FIT_TO_VIEWPORT_LOCK = "IMG_ICONS_FIT_TO_VIEWPORT_LOCK";

	/**
	 * Symbolic name for the reset-zoom icon image.
	 */
	public static final String IMG_ICONS_ZOOM_RESET = "IMG_ICONS_ZOOM_RESET";

	// IMAGES map contains symbolic names and image paths
	private static final Map<String, String> IMAGES = new HashMap<>();

	static {
		IMAGES.put(IMG_ICONS_ZOOM_IN, "icons/zoomIn.png");
		IMAGES.put(IMG_ICONS_ZOOM_OUT, "icons/zoomOut.png");
		IMAGES.put(IMG_ICONS_ZOOM_RESET, "icons/zoomReset.png");
		IMAGES.put(IMG_ICONS_SCROLL_CENTER, "icons/scrollCenter.png");
		IMAGES.put(IMG_ICONS_SCROLL_TOP_LEFT, "icons/scrollTopLeft.png");
		IMAGES.put(IMG_ICONS_SCROLL_TOP_RIGHT, "icons/scrollTopRight.png");
		IMAGES.put(IMG_ICONS_SCROLL_BOTTOM_RIGHT,
				"icons/scrollBottomRight.png");
		IMAGES.put(IMG_ICONS_SCROLL_BOTTOM_LEFT, "icons/scrollBottomLeft.png");
		IMAGES.put(IMG_ICONS_FIT_TO_VIEWPORT, "icons/fitToViewport.png");
		IMAGES.put(IMG_ICONS_FIT_TO_VIEWPORT_LOCK,
				"icons/fitToViewportLock.png");
	}

	// The shared instance
	private static MvcFxUiBundle plugin;

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static MvcFxUiBundle getDefault() {
		return plugin;
	}

	/**
	 * The constructor
	 */
	public MvcFxUiBundle() {
	}

	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {
		// put action images into the registry
		Bundle bundle = getBundle();
		for (Entry<String, String> e : IMAGES.entrySet()) {
			reg.put(e.getKey(), ImageDescriptor.createFromURL(
					FileLocator.find(bundle, new Path(e.getValue()), null)));
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext )
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext )
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

}
