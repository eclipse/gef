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
package org.eclipse.gef4.swtfx.layout;

/**
 * Union of {@link VPos} and {@link HPos} describing positioning/alignment.
 * 
 * @author mwienand
 * 
 */
public enum Pos {

	BASELINE_CENTER,

	BASELINE_LEFT,

	BASELINE_RIGHT,

	CENTER,

	CENTER_LEFT,

	CENTER_RIGHT,

	TOP_CENTER,

	TOP_LEFT,

	TOP_RIGHT,

	BOTTOM_CENTER,

	BOTTOM_LEFT,

	BOTTOM_RIGHT;

	public static Pos construct(HPos hpos, VPos vpos) {
		if (hpos != HPos.CENTER && hpos != HPos.LEFT && hpos != HPos.RIGHT) {
			throw new IllegalStateException(
					"Cannot construct Pos from unknown HPos <" + hpos + ">");
		}

		switch (vpos) {
		case BASELINE:
			switch (hpos) {
			case CENTER:
				return BASELINE_CENTER;
			case LEFT:
				return BASELINE_LEFT;
			case RIGHT:
				return BASELINE_RIGHT;
			}
		case BOTTOM:
			switch (hpos) {
			case CENTER:
				return BOTTOM_CENTER;
			case LEFT:
				return BOTTOM_LEFT;
			case RIGHT:
				return BOTTOM_RIGHT;
			}
		case CENTER:
			switch (hpos) {
			case CENTER:
				return CENTER;
			case LEFT:
				return CENTER_LEFT;
			case RIGHT:
				return CENTER_RIGHT;
			}
		case TOP:
			switch (hpos) {
			case CENTER:
				return TOP_CENTER;
			case LEFT:
				return TOP_LEFT;
			case RIGHT:
				return TOP_RIGHT;
			}
		default:
			throw new IllegalStateException("Cannot construct Pos from VPos <"
					+ vpos + ">");
		}
	}

	public HPos getHPos() {
		switch (this) {
		case BASELINE_CENTER:
		case CENTER:
		case TOP_CENTER:
		case BOTTOM_CENTER:
			return HPos.CENTER;
		case BASELINE_LEFT:
		case CENTER_LEFT:
		case TOP_LEFT:
		case BOTTOM_LEFT:
			return HPos.LEFT;
		case BASELINE_RIGHT:
		case CENTER_RIGHT:
		case TOP_RIGHT:
		case BOTTOM_RIGHT:
			return HPos.RIGHT;
		default:
			throw new IllegalStateException("Cannot extract HPos from Pos <"
					+ this + ">");
		}
	}

	public VPos getVPos() {
		switch (this) {
		case BASELINE_CENTER:
		case BASELINE_LEFT:
		case BASELINE_RIGHT:
			return VPos.BASELINE;
		case CENTER:
		case CENTER_LEFT:
		case CENTER_RIGHT:
			return VPos.CENTER;
		case TOP_CENTER:
		case TOP_LEFT:
		case TOP_RIGHT:
			return VPos.TOP;
		case BOTTOM_CENTER:
		case BOTTOM_LEFT:
		case BOTTOM_RIGHT:
			return VPos.BOTTOM;
		default:
			throw new IllegalStateException("Cannot extract HPos from Pos <"
					+ this + ">");
		}
	}

}
