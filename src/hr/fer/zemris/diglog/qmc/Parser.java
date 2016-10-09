package hr.fer.zemris.diglog.qmc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by bmihaela.
 */
public class Parser {

	private static  final Pattern INPUT_PATTERN = Pattern.compile("^\\s*\\d+(\\s*,\\s*\\d+)*\\s*$");
	private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");


	public static List<Integer> getNumbers(String text){
		List<Integer> numbers = new ArrayList<>();
		if(text.isEmpty()){
			return  numbers;
		}
		Matcher matcher = INPUT_PATTERN.matcher(text);

		if(!matcher.matches()){
			throw  new IllegalArgumentException("Moraju se unijet brojevi odvojeni zarezom. Provjerite unos pažljivo.");
		}

		matcher = NUMBER_PATTERN.matcher(text);
		while(matcher.find()){
			int number = Integer.parseInt(matcher.group());
			if(numbers.contains(number)){
				throw new IllegalArgumentException("Ne mogu se vise puta unjeti isti brojevi u isto polje.");
			}
			numbers.add(number);
		}

		return numbers;
	}

}
