/*******************************************************************************
 * Copyright (c) 2008, 2017 IBM Corporation and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Matthias Wienand (itemis AG) - contribution for Bugzilla #355997
 *
 *******************************************************************************/
package org.eclipse.gef.geometry.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.gef.geometry.euclidean.Angle;
import org.eclipse.gef.geometry.euclidean.Vector;
import org.eclipse.gef.geometry.internal.utils.PrecisionUtils;
import org.eclipse.gef.geometry.planar.Point;
import org.junit.Test;

/**
 * Unit tests for {@link Vector}.
 *
 * @author aboyko
 * @author anyssen
 * @author mwienand
 *
 */
public class VectorTests {

	private interface VectorAction {
		void action(Vector a);
	}

	private abstract class VectorPairAction {
		public void action(Vector a, Vector b) {
		}

		public Vector alterA(Vector a) {
			return a;
		}
	}

	private static final double PRECISION_FRACTION = TestUtils
			.getPrecisionFraction();

	private static final double RECOGNIZABLE_FRACTION = PRECISION_FRACTION
			+ PRECISION_FRACTION / 10;

	private static final double UNRECOGNIZABLE_FRACTION = PRECISION_FRACTION
			- PRECISION_FRACTION / 10;

	private void forVectorPairs(VectorPairAction action) {
		for (double x1 = -2; x1 <= 2; x1 += 0.2) {
			for (double y1 = -2; y1 <= 2; y1 += 0.2) {
				Vector a = action.alterA(new Vector(x1, y1));
				for (double x2 = -2; x2 <= 2; x2 += 0.2) {
					for (double y2 = -2; y2 <= 2; y2 += 0.2) {
						action.action(a, new Vector(x2, y2));
					}
				}
			}
		}
	}

	private void forVectors(VectorAction action) {
		for (double x = -2; x <= 2; x += 0.2) {
			for (double y = -2; y <= 2; y += 0.2) {
				Vector a = new Vector(x, y);
				action.action(a);
			}
		}
	}

	@Test
	public void test_constructors() {
		Vector a = new Vector(1, 2);
		assertTrue(a.equals(new Vector(new Point(1, 2))));
		assertTrue(a.equals(new Vector(new Point(), new Point(1, 2))));
		assertTrue(a.equals(new Vector(new Point(1, 2), new Point(2, 4))));
		assertTrue(a.equals(new Vector(new Vector(0, 0), new Vector(1, 2))));
		assertTrue(a.equals(new Vector(new Vector(-1, -2), new Vector(0, 0))));
	}

	@Test
	public void test_copy() {
		Vector a = new Vector(1, 2);

		assertTrue(a.getCopy().equals(a));
		assertTrue(a.clone().equals(a));
		assertTrue(a.getCopy().equals(a.clone()));
	}

	@Test
	public void test_equals() {
		Vector a = new Vector(3, 2);
		Vector b = new Vector(2, -2);
		assertTrue(a.equals(a));
		assertFalse(a.equals(b));
		assertFalse(a.equals(new Point(3, 2)));
		assertTrue(a.equals(a.getAdded(new Vector(UNRECOGNIZABLE_FRACTION / 10,
				UNRECOGNIZABLE_FRACTION / 10))));
		assertFalse(a.equals(a.getAdded(
				new Vector(RECOGNIZABLE_FRACTION, RECOGNIZABLE_FRACTION))));
	}

	@Test
	public void test_getAdded() {
		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				assertTrue(
						a.getAdded(b).equals(new Vector(a.x + b.x, a.y + b.y)));
			}
		});
	}

	@Test
	public void test_getAngle() {
		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				assertTrue(PrecisionUtils.equal(a.getDotProduct(b),
						a.getLength() * b.getLength()
								* Math.cos(a.getAngle(b).rad())));
			}
		});

		// test for the ArithmeticException is case of a null-vector
		for (Vector a : new Vector[] { new Vector(1, 2), new Vector(0, 0) }) {
			for (Vector b : new Vector[] { new Vector(0, 0),
					new Vector(1, 2) }) {
				boolean thrown = a.getLength() != 0 && b.getLength() != 0;
				try {
					a.getAngle(b);
				} catch (ArithmeticException x) {
					thrown = true;
				}
				assertTrue(thrown);
			}
		}
	}

	@Test
	public void test_getAngleCCW() {
		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				Angle alpha = a.getAngle(b);
				Angle alphaCCW = a.getAngleCCW(b);

				if (a.getCrossProduct(b) > 0) {
					alpha = alpha.getOppositeFull();
				}

				assertTrue(alpha.equals(alphaCCW));
			}
		});
	}

	@Test
	public void test_getAngleCW() {
		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				Angle alphaCW = a.getAngleCW(b);

				Angle alpha = a.getAngle(b);
				if (a.getCrossProduct(b) < 0) {
					alpha = alpha.getOppositeFull();
				}

				assertTrue(alpha.equals(alphaCW));
			}
		});
	}

	@Test
	public void test_getAveraged() {
		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				assertTrue(a.getAveraged(b)
						.equals(a.getAdded(b).getMultiplied(0.5)));
			}
		});
	}

	@Test
	public void test_getCrossProduct() {
		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				assertTrue(PrecisionUtils.equal(a.getCrossProduct(b),
						a.x * b.y - a.y * b.x));
			}
		});
	}

	@Test
	public void test_getDissimilarity() {
		Vector vx = new Vector(5, 0);
		Vector vy = new Vector(0, 5);
		Vector vxy = new Vector(5, 5);

		// cross product uses vectors's lengths!
		assertTrue(vx.getDissimilarity(vy) > vx.getDissimilarity(vxy));
		// note: no PrecisionUtils here, because we need "is really greater"

		// TODO: normalize the vectors first, so that they get comparable.

		/*
		 * the description of the method is mistakable:
		 *
		 * 1) does it mean that an angle of 180 degrees returns the same
		 * dissimilarity as an angle of 0 degrees?
		 *
		 * 2) or does it mean that an angle of 180 degrees returns the highest
		 * dissimilarity?
		 *
		 * the following code expects the first case
		 */

		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				assertTrue(PrecisionUtils.equal(a.getDissimilarity(b), Math.abs(
						a.getNormalized().getCrossProduct(b.getNormalized()))));
			}
		});
	}

	@Test
	public void test_getDivided() {
		forVectors(new VectorAction() {
			@Override
			public void action(Vector a) {
				for (double r = -2; r <= 2; r += 0.2) {
					// TODO: no PrecisionUtils in getDivided()
					assertTrue(r != 0);
					assertTrue(a.getDivided(r)
							.equals(new Vector(a.x / r, a.y / r)));
				}

				boolean thrown = false;
				try {
					a.getDivided(0);
				} catch (ArithmeticException e) {
					thrown = true;
				}
				assertTrue(thrown);
			}
		});
	}

	@Test
	public void test_getDotProduct() {
		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				assertTrue(PrecisionUtils.equal(a.getDotProduct(b),
						a.x * b.x + a.y * b.y));
			}
		});
	}

	@Test
	public void test_getLength() {
		assertEquals(new Vector(3, 4).getLength(), 5.0d, 0.0d);

		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				if (a.isNull()) {
					assertTrue(PrecisionUtils.equal(a.getLength(), 0));
				} else if (b.isNull()) {
					assertTrue(PrecisionUtils.equal(b.getLength(), 0));
				} else {
					assertTrue(PrecisionUtils.equal(
							a.getDivided(a.getLength()).getLength(),
							(b.getDivided(b.getLength()).getLength())));
				}
			}
		});
	}

	@Test
	public void test_getMultiplied() {
		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				assertTrue(a.getMultiplied(b.x)
						.equals(new Vector(a.x * b.x, a.y * b.x)));
				assertTrue(a.getMultiplied(b.y)
						.equals(new Vector(a.x * b.y, a.y * b.y)));
			}
		});
	}

	@Test
	public void test_getNormalized() {
		final Vector x = new Vector(1, 0);

		forVectors(new VectorAction() {
			@Override
			public void action(Vector a) {
				assertTrue(a.x != 0);
				assertTrue(a.y != 0);

				Vector normalized = a.getNormalized();
				Angle origAlpha = a.getAngleCW(x);
				Angle normAlpha = normalized.getAngleCW(x);

				assertTrue(PrecisionUtils.equal(normalized.getLength(), 1));
				assertTrue(
						PrecisionUtils.equal(origAlpha.rad(), normAlpha.rad()));
			}
		});
	}

	@Test
	public void test_getOrthoComplement() {
		Vector a = new Vector(3, -5);
		assertTrue(a.getOrthogonalComplement().equals(new Vector(5, 3)));

		forVectors(new VectorAction() {
			@Override
			public void action(Vector a) {
				assertTrue(PrecisionUtils.equal(
						a.getOrthogonalComplement().getDotProduct(a), 0));
			}
		});
	}

	@Test
	public void test_getSimilarity() {
		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				assertTrue(PrecisionUtils.equal(a.getSimilarity(b),
						Math.abs(a.getDotProduct(b))));
			}
		});
	}

	@Test
	public void test_getSubtracted() {
		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				assertTrue(a.getSubtracted(b)
						.equals(new Vector(a.x - b.x, a.y - b.y)));
			}
		});
	}

	@Test
	public void test_isHorizontal() {
		forVectors(new VectorAction() {
			@Override
			public void action(Vector a) {
				assertTrue(a.isHorizontal() == (!PrecisionUtils.equal(a.x, 0)
						&& PrecisionUtils.equal(a.y, 0)));
			}
		});
	}

	@Test
	public void test_isNull() {
		forVectors(new VectorAction() {
			@Override
			public void action(Vector a) {
				assertTrue(a.isNull() == (PrecisionUtils.equal(a.x, 0)
						&& PrecisionUtils.equal(a.y, 0)));
			}
		});
	}

	@Test
	public void test_isOrthogonalTo() {
		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				assertTrue(a.isOrthogonalTo(b) == PrecisionUtils
						.equal(a.getDotProduct(b), 0));
			}
		});
	}

	@Test
	public void test_isParallelTo() {
		forVectorPairs(new VectorPairAction() {
			@Override
			public void action(Vector a, Vector b) {
				// TODO: rewrite this test!
				assertTrue(a.isParallelTo(b) == PrecisionUtils
						.equal(a.getDissimilarity(b), 0));
			}
		});
	}

	@Test
	public void test_isVertical() {
		forVectors(new VectorAction() {
			@Override
			public void action(Vector a) {
				assertTrue(a.isVertical() == (!PrecisionUtils.equal(a.y, 0)
						&& PrecisionUtils.equal(a.x, 0)));
			}
		});
	}

	@Test
	public void test_rotateCCW() {
		forVectors(new VectorAction() {
			@Override
			public void action(Vector a) {
				for (double alpha = 0; alpha <= 4 * Math.PI; alpha += 0.1) {
					Angle angle = Angle.fromRad(alpha);
					Vector rotated = a.getRotatedCCW(angle);
					double nAlpha = angle.getOppositeFull().rad();
					double x = a.x * Math.cos(nAlpha) - a.y * Math.sin(nAlpha);
					double y = a.x * Math.sin(nAlpha) + a.y * Math.cos(nAlpha);
					assertTrue(rotated.equals(new Vector(x, y)));

					a.rotateCCW(angle);
					assertTrue(rotated.equals(a));
				}
			}
		});
	}

	@Test
	public void test_rotateCW() {
		forVectors(new VectorAction() {
			@Override
			public void action(Vector a) {
				for (double alpha = 0; alpha <= 4 * Math.PI; alpha += 0.1) {
					Angle angle = Angle.fromRad(alpha);
					Vector rotated = a.getRotatedCW(angle);
					double x = a.x * Math.cos(alpha) - a.y * Math.sin(alpha);
					double y = a.x * Math.sin(alpha) + a.y * Math.cos(alpha);
					assertTrue(rotated.equals(new Vector(x, y)));

					a.rotateCW(angle);
					assertTrue(rotated.equals(a));
				}
			}
		});
	}

	@Test
	public void test_toPoint() {
		forVectors(new VectorAction() {
			@Override
			public void action(Vector a) {
				Point p = a.toPoint();
				assertTrue(PrecisionUtils.equal(a.x, p.x));
				assertTrue(PrecisionUtils.equal(a.y, p.y));
			}
		});
	}

	@Test
	public void test_toString() {
		Vector a = new Vector(0, 0);
		assertEquals("Vector: [0.0,0.0]", a.toString());
	}

}
