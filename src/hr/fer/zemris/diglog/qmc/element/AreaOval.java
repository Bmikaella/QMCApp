package hr.fer.zemris.diglog.qmc.element;

import java.awt.*;

/**
 * Created by bmihaela.
 */
public class AreaOval implements Area {
	private int x;
	private int y;
	private int width;
	private int height;
	private Color color;

	public AreaOval(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.color = Color.black;
	}

	public AreaOval(int x, int y, int width, int height, Color color) {
		this(x, y, width, height);
		this.color = color;
	}

	@Override
	public void draw(Graphics2D graphics2D) {
		graphics2D.setColor(color);
		graphics2D.fillOval(x,y,width,height);
	}
}
