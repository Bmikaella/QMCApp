package hr.fer.zemris.diglog.qmc.draw;

import hr.fer.zemris.diglog.qmc.element.Area;
import hr.fer.zemris.diglog.qmc.observer.AreaImplicantObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.util.*;
import java.util.List;

/**
 * Created by bmihaela.
 */
public class ZoomClickArea extends JPanel {
	private static final float UNITS_SCROLLED_MAGNIFY = 1.3f;
	private static final float UNITS_SCROLLED_DECREASE = 0.7f;

	private MouseAdapter movementOfGraphics;
	private MouseWheelListener scalationOfGraphics;
	private Point mousePressPoint;
	private Point mouseReleasedPoint;
	private AffineTransform affineTransform;

	private List<Area> elements = new ArrayList<>();

	private List<AreaImplicantObserver> observers = new ArrayList<>();

	public ZoomClickArea() {
		affineTransform = new AffineTransform();
		movementOfGraphics = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				fire(transformX(e.getX()), transformY(e.getY()));
			}

			public void mousePressed(MouseEvent e) {
				mousePressPoint = e.getPoint();
				ZoomClickArea.this.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseReleasedPoint = e.getPoint();
				AffineTransform tempTransform = new AffineTransform();
				tempTransform.concatenate(affineTransform);
				tempTransform.translate(mouseReleasedPoint.getX() - mousePressPoint.getX(),
						mouseReleasedPoint.getY() - mousePressPoint.getY());
				affineTransform = tempTransform;
				repaint();
				ZoomClickArea.this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		};
		scalationOfGraphics = e -> {
			AffineTransform tempTransform = new AffineTransform();
			tempTransform.concatenate(affineTransform);
			if (e.getUnitsToScroll() < 0) {
				tempTransform.scale(UNITS_SCROLLED_DECREASE, UNITS_SCROLLED_DECREASE);
			} else {
				tempTransform.scale(UNITS_SCROLLED_MAGNIFY, UNITS_SCROLLED_MAGNIFY);
			}
			affineTransform = tempTransform;
			repaint();

		};
		this.addMouseWheelListener(scalationOfGraphics);
		this.addMouseListener(movementOfGraphics);
	}

	private int transformX(int x) {
		System.out.println(x);
		return (int) ((x - affineTransform.getTranslateX()) / affineTransform.getScaleX());
	}

	public void resetTransformation(){
		affineTransform = new AffineTransform();
	}

	private int transformY(int y) {
		return (int) ((y - affineTransform.getTranslateY()) / affineTransform.getScaleY());
	}

	public void addObserver(AreaImplicantObserver observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);

		}
	}

	public void removeObserver(AreaImplicantObserver observer) {
		observers.remove(observer);
	}

	public void clearObservers() {
		observers.clear();
	}

	public void addElement(Area element) {
		if (!elements.contains(element)) {
			elements.add(element);
		}
	}

	public void removeElement(Area element) {
		elements.remove(element);
	}

	public void clearElements(){
		elements.clear();
	}

	public void fire(int x, int y) {
		System.out.println(x);
		for (AreaImplicantObserver observer : observers) {
			if (observer.checkArea(x, y)) {
				observer.fire();
				break;
			}
		}
	}

	public List<AreaImplicantObserver> getObservers() {
		return observers;
	}

	public void resetObservers(){
		for(AreaImplicantObserver observer : observers){
			observer.drawObserver(false);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(Color.WHITE);
		Dimension size = this.getSize();
		g.fillRect(0, 0, (int) size.getWidth(), (int) size.getHeight());

		Graphics2D graphics2D = (Graphics2D) g;
		graphics2D.transform(affineTransform);

		//TODO: drawing of elements;
		graphics2D.setColor(Color.BLACK);
		for (Area element : elements) {
			element.draw(graphics2D);
		}

		for(AreaImplicantObserver observer : observers){
			observer.draw(graphics2D);
		}
	}
}
