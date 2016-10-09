package hr.fer.zemris.diglog.qmc;

import java.util.List;

/**
 * Created by bmihaela.
 */
public class QMCUtil {
	public static boolean intersection(List<Integer> first, List<Integer> second) {
		for(Integer number : first){
			for(Integer secondNumber : second){
				if(number.equals(secondNumber)){
					return true;
				}
			}
		}
		return false;
	}
}
