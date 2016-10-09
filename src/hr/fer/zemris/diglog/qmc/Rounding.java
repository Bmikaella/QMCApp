package hr.fer.zemris.diglog.qmc;

/**
 * Created by bmihaela.
 */
public class Rounding {
	private int verticalUnitSize;
	private int horizontalUnitSize;
	private int x;
	private int y;
	private boolean splitHorizontal;
	private boolean splitVertical;

	public Rounding(int verticalUnitSize, int horizontalUnitSize, int xUnitPosition, int yUnitPosition, boolean
			splitHorizontal, boolean splitVertical) {
		this.verticalUnitSize = verticalUnitSize;
		this.horizontalUnitSize = horizontalUnitSize;
		this.x = xUnitPosition;
		this.y = yUnitPosition;
		this.splitHorizontal = splitHorizontal;
		this.splitVertical = splitVertical;
	}

	public int getVerticalUnitSize() {
		return verticalUnitSize;
	}


	public int getHorizontalUnitSize() {
		return horizontalUnitSize;
	}

	public boolean isSplitHorizontal() {
		return splitHorizontal;
	}

	public boolean isSplitVertical() {
		return splitVertical;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

}
