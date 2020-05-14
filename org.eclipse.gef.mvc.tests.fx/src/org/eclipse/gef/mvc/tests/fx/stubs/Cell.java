/*******************************************************************************
 * Copyright (c) 2016 itemis AG and others.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Matthias Wienand (itemis AG) - initial API and implementation
 *
 *******************************************************************************/
package org.eclipse.gef.mvc.tests.fx.stubs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Cell {
	/**
	 * Creates a complex structure of {@link Cell}s from the given
	 * {@link String} representation.
	 * <p>
	 * Example tree:
	 *
	 * <pre>
	 * Root (R)
	 *  +- Content 0 (C0)
	 *  |   +- Sub Content 0.0 (C00)
	 *  |   +- Sub Content 0.1 (C10)
	 *  |
	 *  +- Content 1 (C1)
	 *  |   +- Sub Content 1.0 (C10)
	 *  |
	 *  +- Content 2 (C2)
	 *      +- Sub Content 2.0 (C20)
	 *      +- Sub Content 2.1 (C21)
	 * </pre>
	 *
	 * Corresponding string representation:
	 *
	 * <pre>
	 * R-C0-C00
	 * R-C0-C01
	 * R-C1-C10
	 * R-C2-C20
	 * R-C2-C21
	 * </pre>
	 *
	 * A mapping from the names (e.g. "R", "C0", "C1", "C2", "C00", etc.) to the
	 * generated {@link Cell}s is put into the given nameToCellMap.
	 *
	 * @param repr
	 *            String that describes the tree of cells to create.
	 * @param nameToCellMap
	 *            Map that is used to store cell name to Cell object mappings.
	 * @return Root {@link Cell}.
	 */
	public static Cell createCellTree(String repr, Map<String, Cell> nameToCellMap) {
		Cell root = new Cell("R");
		nameToCellMap.put("R", root);

		String[] lines = repr.split("\n");
		for (String line : lines) {
			String[] cells = line.split("-");
			Cell c = nameToCellMap.get(cells[0]);
			for (String sc : cells) {
				if (!sc.equals(c.name)) {
					Cell subCell = null;
					if (nameToCellMap.containsKey(sc)) {
						subCell = nameToCellMap.get(sc);
					} else {
						subCell = new Cell(sc);
						nameToCellMap.put(sc, subCell);
					}
					if (!c.children.contains(subCell)) {
						c.children.add(subCell);
					}
					c = subCell;
				}
			}
		}

		return root;
	}

	public String name = "X";
	public List<Cell> children = new ArrayList<>();

	public Cell(String name) {
		this.name = name;
	}

	public Cell(String name, Cell... children) {
		this.name = name;
		this.children.addAll(Arrays.asList(children));
	}

	@Override
	public String toString() {
		return "{" + name + ": " + children + "}";
	}
}