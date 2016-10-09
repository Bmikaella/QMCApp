package hr.fer.zemris.diglog.qmc.element;

import java.awt.*;
import java.util.Objects;

/**
 * Created by bmihaela.
 */
public class AreaRectangle implements Area {
	private int x;
	private int y;
	private int width;
	private int height;
	private Color color;

	public AreaRectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		color = Color.black;
	}

	public AreaRectangle(int x, int y, int width, int height, Color color) {
		this(x, y, width, height);
		this.color = color;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AreaRectangle that = (AreaRectangle) o;
		return x == that.x &&
				y == that.y &&
				width == that.width &&
				height == that.height;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, width, height);
	}

	@Override
	public void draw(Graphics2D graphics2D) {
		graphics2D.setColor(color);
		graphics2D.fillRect(x,y,width,height);
	}
}
