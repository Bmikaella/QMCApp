package hr.fer.zemris.diglog.qmc.watcher;

import java.util.Objects;

/**
 * Created by bmihaela.
 */
public class StringWatcher {
	private int x;
	private int y;
	private int width;
	private int height;
	private String element;

	public StringWatcher(int x, int y, int width, int height,String element) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.element = element;
	}

	public String getElement() {
		return element;
	}

	public boolean contains(double x, double y){
		if(x >=this.x && x <= this.x+width && y >= this.y && y <= this.y+height){
			return true;
		}
		return false;
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StringWatcher that = (StringWatcher) o;
		return x == that.x &&
				y == that.y &&
				width == that.width &&
				height == that.height;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y, width, height);
	}

}
