package hr.fer.zemris.diglog.qmc.element;

import java.awt.*;
import java.util.Objects;

/**
 * Created by bmihaela.
 */
public class AreaString implements Area {
	private int x;
	private int y;
	private String text;
	private Color color;

	public AreaString(int x, int y, String text) {
		this.x = x;
		this.y = y;
		this.text = text;
		color = Color.black;
	}

	public AreaString(int x, int y, String text, Color color) {
		this(x, y, text);
		this.color = color;
	}

	@Override
	public void draw(Graphics2D graphics2D) {
		graphics2D.setColor(color);
		graphics2D.drawString(text,x,y);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AreaString that = (AreaString) o;
		return x == that.x &&
				y == that.y &&
				Objects.equals(text, that.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, text);
	}
}
