package hr.fer.zemris.diglog.qmc.element;

import java.awt.*;

/**
 * Created by bmihaela.
 */
public class AreaArch implements Area {

	private int x;
	private int y;
	private int width;
	private int height;
	private int startArch;
	private int endArch;
	private Color color;

	public AreaArch(int x, int y, int width, int height,int startArch, int endArch) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.startArch = startArch;
		this.endArch = endArch;
		this.color = Color.black;
	}

	public AreaArch(int x, int y, int width, int height,int startArch, int endArch, Color color) {
		this(x, y, width, height, startArch, endArch);
		this.color = color;
	}

	@Override
	public void draw(Graphics2D graphics2D) {
		graphics2D.setColor(color);
		graphics2D.fillArc(x,y,width,height,startArch,endArch);
	}

}
