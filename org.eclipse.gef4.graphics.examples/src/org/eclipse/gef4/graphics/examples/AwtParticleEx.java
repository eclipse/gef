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
package org.eclipse.gef4.graphics.examples;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.eclipse.gef4.geometry.convert.awt.AWT2Geometry;
import org.eclipse.gef4.geometry.euclidean.Vector;
import org.eclipse.gef4.geometry.planar.Ellipse;
import org.eclipse.gef4.geometry.planar.Point;
import org.eclipse.gef4.geometry.planar.Rectangle;
import org.eclipse.gef4.graphics.IGraphics;
import org.eclipse.gef4.graphics.awt.AwtGraphics;
import org.eclipse.gef4.graphics.color.Color;

public class AwtParticleEx extends JApplet {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("Particle Example");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JApplet applet = new AwtParticleEx();
		applet.init();
		frame.getContentPane().add(applet);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void init() {
		JPanel panel = new AwtParticleExPanel();
		getContentPane().add(panel);
	}
}

class AwtParticleExPanel extends JPanel implements MouseListener,
		MouseMotionListener {

	private static class Particle {
		private Color color;
		private double a;
		private long life, initLife;
		private Point s;
		private Vector v;
		private static final int RADIUS = 6;

		public Particle(Point position, Vector velocity, double acceleration,
				long life) {
			s = position.getCopy();
			v = velocity.getCopy();
			a = acceleration;
			this.life = initLife = life;
		}

		public void draw(IGraphics g) {
			g.setFillPatternColor(color);
			g.fill(new Ellipse(s.x - RADIUS, s.y - RADIUS, 2 * RADIUS,
					2 * RADIUS));
		}

		public boolean update(long deltaMillis) {
			life -= deltaMillis;
			if (life < 0) {
				return false;
			}

			double dt = deltaMillis / 1000d;

			s.translate(v.x * dt, v.y * dt);
			v.y += a * dt;

			color = new Color(16, 64, 228,
					(int) (255 * (double) (life) / initLife));

			return true;
		}
	}

	private static final long serialVersionUID = 1L;
	private List<Particle> particles = new LinkedList<Particle>();
	private boolean spawn = false;
	private long lastMillis = 0;
	private Point position;
	private Random rng;

	public AwtParticleExPanel() {
		rng = new Random(System.currentTimeMillis());
		setPreferredSize(new Dimension(640, 480));
		addMouseListener(this);
		addMouseMotionListener(this);
		new Timer(30, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		}).start();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		position = AWT2Geometry.toPoint(e.getPoint());
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		position = AWT2Geometry.toPoint(e.getPoint());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		spawn = true;
		position = AWT2Geometry.toPoint(e.getPoint());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		spawn = false;
	}

	@Override
	public void paint(Graphics graphics) {
		Graphics2D g2d = (Graphics2D) graphics;
		AwtGraphics g = new AwtGraphics(g2d);
		renderScene(g);
		g.cleanUp();
	}

	private void renderScene(IGraphics g) {
		if (lastMillis == 0) {
			lastMillis = System.currentTimeMillis();
		}

		long deltaMillis = System.currentTimeMillis() - lastMillis;
		lastMillis += deltaMillis;

		// clear screen
		g.setFill(new Color(255, 255, 255)).fill(
				new Rectangle(0, 0, getWidth(), getHeight()));

		if (spawn) {
			particles.add(new Particle(position, new Vector(
					rng.nextInt(150) - 75, -75 - rng.nextInt(150)), 150,
					1500 + rng.nextInt(1500)));
		}

		g.setFill(new Color(0, 255, 0));
		for (Iterator<Particle> i = particles.iterator(); i.hasNext();) {
			Particle p = i.next();
			if (!p.update(deltaMillis)) {
				i.remove();
				continue;
			}
			p.draw(g);
		}
	}

}
