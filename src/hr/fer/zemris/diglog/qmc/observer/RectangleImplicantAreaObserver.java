package hr.fer.zemris.diglog.qmc.observer;

import hr.fer.zemris.diglog.qmc.Implicant;
import hr.fer.zemris.diglog.qmc.QMCApp;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by bmihaela.
 */
public abstract class RectangleImplicantAreaObserver implements AreaImplicantObserver {
	private int x;
	private int y;
	private int width;
	private int height;
	private java.util.List<Implicant> implicants;
	private boolean drawObserver;

	public RectangleImplicantAreaObserver(int x, int y, int width, int height, Implicant implicant) {
		this(x, y, width, height);
		implicants.add(implicant);
	}

	public RectangleImplicantAreaObserver(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.drawObserver = false;
		this.implicants = new ArrayList<>();
	}

	public RectangleImplicantAreaObserver(int x, int y, int width, int height, List<Implicant> implicants) {
		this(x, y, width, height);
		this.implicants.addAll(implicants);
	}

	public Implicant getImplicant() {
		return implicants.get(0);
	}

	@Override
	public int size() {
		return implicants.size();
	}

	@Override
	public List<Implicant> getImplicants() {
		return implicants;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void drawObserver(boolean draw){
		this.drawObserver = draw;
	}

	@Override
	public void draw(Graphics2D graphics2D) {
		if(drawObserver){
			if(implicants.size() > 1){
				graphics2D.setColor(QMCApp.DEFAULT_IMPLICANTS_COLOR);

			}else {
			graphics2D.setColor(implicants.get(0).getColor());
			}
			graphics2D.fillRect(x,y, width,height);
		}
	}

	@Override
	public boolean checkArea(int x, int y) {
		if(x >=this.x && x <= this.x+width && y >= this.y && y <= this.y+height){
			return true;
		}
		return false;
	}
}
