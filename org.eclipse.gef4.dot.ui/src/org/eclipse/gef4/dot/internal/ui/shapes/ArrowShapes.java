/************************************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tamas Miklossy (itemis AG)   - Add support for arrowType edge decorations (bug #477980)
 *
 ***********************************************************************************************/
package org.eclipse.gef4.dot.internal.ui.shapes;

import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;

public class ArrowShapes {

	public static class Box extends Polygon {
		public Box() {
			super(-size / 2, -size / 2, size / 2, -size / 2, size / 2, size / 2,
					-size / 2, size / 2);
		}
	}

	public static class Crow extends Polygon {
		public Crow() {
			super(size / 2, 0, -size / 2, -size / 2, 0, 0, -size / 2, size / 2);
		}
	}

	public static class Curve extends Arc {
		public Curve() {
			super(0, 0, size / 2, size / 2, 90, 180);
		}
	}

	public static class Diamond extends Polygon {
		public Diamond() {
			super(-size / 2, 0, 0, -size / 2, size / 2, 0, 0, size / 2);
		}
	}

	public static class Dot extends Circle {
		public Dot() {
			super(0, 0, size / 2);
		}
	}

	public static class ICurve extends Arc {
		public ICurve() {
			super(0, 0, size / 2, size / 2, -90, 180);
		}
	}

	public static class Inv extends Polygon {
		public Inv() {
			super(size / 2, 0, -size / 2, -size / 2, -size / 2, size / 2);
		}
	}

	public static class LBox extends Polygon {
		public LBox() {
			super(-size / 2, 0, size / 2, 0, size / 2, size / 2, -size / 2,
					size / 2);
		}
	}

	public static class LCrow extends Polygon {
		public LCrow() {
			super(size / 2, 0, -size / 2, size / 2, 0, 0);
		}
	}

	public static class LCurve extends Arc {
		public LCurve() {
			super(0, 0, size / 2, size / 2, 180, 90);
		}
	}

	public static class LDiamond extends Polygon {
		public LDiamond() {
			super(-size / 2, 0, 0, size / 2, size / 2, 0);
		}
	}

	public static class LICurve extends Arc {
		public LICurve() {
			super(0, 0, size / 2, size / 2, 270, 90);
		}
	}

	public static class LInv extends Polygon {
		public LInv() {
			super(size / 2, 0, -size / 2, 0, -size / 2, size / 2);
		}
	}

	public static class LNormal extends Polygon {
		public LNormal() {
			super(-size / 2, 0, size / 2, 0, size / 2, size / 2);
		}
	}

	public static class LTee extends Polygon {
		public LTee() {
			super(0, 0, size / 4, 0, size / 4, size / 2, 0, size / 2);
		}
	}

	public static class LVee extends Polygon {
		public LVee() {
			super(-size / 2, 0, size / 2, 0, 0, 0, size / 2, size / 2);
		}
	}

	public static class Normal extends Polygon {
		public Normal() {
			super(-size / 2, 0, size / 2, -size / 2, size / 2, size / 2);
		}
	}

	public static class RBox extends Polygon {
		public RBox() {
			super(-size / 2, 0, -size / 2, -size / 2, size / 2, -size / 2,
					size / 2, 0);
		}
	}

	public static class RCrow extends Polygon {
		public RCrow() {
			super(size / 2, 0, -size / 2, -size / 2, 0, 0);
		}
	}

	public static class RCurve extends Arc {
		public RCurve() {
			super(0, 0, size / 2, size / 2, 90, 90);
		}
	}

	public static class RDiamond extends Polygon {
		public RDiamond() {
			super(-size / 2, 0, 0, -size / 2, size / 2, 0);
		}
	}

	public static class RICurve extends Arc {
		public RICurve() {
			super(0, 0, size / 2, size / 2, 0, 90);
		}
	}

	public static class RInv extends Polygon {
		public RInv() {
			super(size / 2, 0, -size / 2, -size / 2, -size / 2, 0);
		}
	}

	public static class RNormal extends Polygon {
		public RNormal() {
			super(-size / 2, 0, size / 2, -size / 2, size / 2, 0);
		}
	}

	public static class RTee extends Polygon {
		public RTee() {
			super(0, -size / 2, size / 4, -size / 2, size / 4, 0, 0, 0);
		}
	}

	public static class RVee extends Polygon {
		public RVee() {
			super(-size / 2, 0, size / 2, -size / 2, 0, 0, size / 2, 0);
		}
	}

	public static class Tee extends Polygon {
		public Tee() {
			super(0, -size / 2, size / 4, -size / 2, size / 4, size / 2, 0,
					size / 2);
		}
	}

	public static class Vee extends Polygon {
		public Vee() {
			super(-size / 2, 0, size / 2, -size / 2, 0, 0, size / 2, size / 2);
		}
	}

	private static int size = 10;

}
