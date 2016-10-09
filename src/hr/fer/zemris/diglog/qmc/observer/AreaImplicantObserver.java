package hr.fer.zemris.diglog.qmc.observer;

import hr.fer.zemris.diglog.qmc.Implicant;
import hr.fer.zemris.diglog.qmc.element.Area;

import java.util.List;

/**
 * Created by bmihaela.
 */
public interface AreaImplicantObserver extends Area {

	void fire();

	boolean checkArea(int x, int y);

	List<Implicant> getImplicants();
	Implicant getImplicant();

	void drawObserver(boolean draw);

	int size();
}
