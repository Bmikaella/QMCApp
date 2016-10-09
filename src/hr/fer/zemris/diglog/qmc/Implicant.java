package hr.fer.zemris.diglog.qmc;

import icontract.Preconditions;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by bmihaela.
 */
public class Implicant {

	private static final char BEG_VAR = 'A';
//	public static final int DEFAULT_NUMBER_OF_VARIABLES = 4;
	private static final int SIMPLIFY_MARK = 3;
	private int numberOfVariables;
	private static final Random random = new Random();

	private static final int OPACITY = 130;

	public static final Implicant FAKE = new Implicant();

	private List<Integer> mintermsAndDontCares;
	private boolean dontCare;
	private boolean included;
	private short[] binaryRepresentation;
	private Color color;
	private boolean selected;

	public Implicant(int number, boolean dontCare, int numberOfVariables) {
		this();
		Preconditions.require(number < Math.pow(2, numberOfVariables), "Implikanti moraju biti manji od"+(int)Math.pow(2,numberOfVariables) +
				" " +
				"jer raspolazemo s "+numberOfVariables+" varijable.");
		Preconditions.require(number >= 0, "Broj mora biti pozitivan.");
		this.dontCare = dontCare;
		mintermsAndDontCares.add(number);
		this.numberOfVariables = numberOfVariables;
		binaryRepresentation = convertToBinary(number);
	}

	private Implicant() {
		this.color = new Color(random.nextInt(254),random.nextInt(254),random.nextInt(254), OPACITY);
		included = false;
		selected = false;
		mintermsAndDontCares = new ArrayList<>();
	}

	public int getNumberOfVariables() {
		return numberOfVariables;
	}

	private short[] convertToBinary(int number) {
		short[] digits = new short[numberOfVariables];

		for (short j = 0; j < numberOfVariables; ++j) {
			digits[j] = (short) (number & 0x1);
			number >>= 1;
		}

		return digits;
	}
	public int size(){
		return mintermsAndDontCares.size();
	}

	public Color getColor() {
		return color;
	}

	public boolean isImplicantDontCare() {
		return dontCare;
	}

	public void setIncluded(boolean included) {
		this.included = included;
	}

	public boolean isIncluded(){
		return included;
	}

	public boolean contains (int minterm){
		return mintermsAndDontCares.contains(minterm);
	}

	public List<Integer> getMintermsAndDontCares() {
		return new ArrayList<>(mintermsAndDontCares);
	}

	public int countOnes(){
		int countOnes = 0;
		for (int i = 0; i < numberOfVariables; i++) {
			if (this.binaryRepresentation[i] == 1) {
				countOnes++;
			}
		}
		return countOnes;

	}

	public static Implicant simplifyImplicants(Implicant first, Implicant second) {
		Preconditions.require(first != null, "First entry can't be null.");
		Preconditions.require(second != null, "Second entry can't be null.");

		boolean combine = false;
		int position = 0;

		for (int i = 0; i < first.numberOfVariables; i++) {
			int firstInput = first.binaryRepresentation[i];
			int secondInput = second.binaryRepresentation[i];

			if (firstInput != secondInput) {

				if (firstInput == SIMPLIFY_MARK || secondInput == SIMPLIFY_MARK) {
					combine = false;
					break;
				}

				if (combine) {
					combine = false;
					break;
				} else {
					combine = true;
					position = i;

				}
			}
		}

		if (combine) {
			Implicant implicant = new Implicant();
			implicant.numberOfVariables = first.numberOfVariables;
			implicant.mintermsAndDontCares.addAll(first.mintermsAndDontCares);
			implicant.mintermsAndDontCares.addAll(second.mintermsAndDontCares);
			implicant.dontCare = first.dontCare && second.dontCare;

			implicant.binaryRepresentation = Arrays.copyOf(first.binaryRepresentation, first.binaryRepresentation
					.length);
			implicant.binaryRepresentation[position] = SIMPLIFY_MARK;

			return implicant;
		}

		return null;
	}

	public static List<Implicant> turnToImplicant(List<Integer> numbers, boolean dontCare, int numberOfVariables) {
		List<Implicant> implicants = new ArrayList<>();
		for (Integer number : numbers) {
			implicants.add(new Implicant(number, dontCare, numberOfVariables));
		}
		return implicants;
	}

	public boolean isSorted() {
		int size = mintermsAndDontCares.size() - 1;
		for (int i = 0; i < size; i++) {
			if (mintermsAndDontCares.get(i) > mintermsAndDontCares.get(i + 1)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Implicant implicant = (Implicant) o;
		return Objects.equals(mintermsAndDontCares, implicant.mintermsAndDontCares);
	}

	@Override
	public int hashCode() {
		return Objects.hash(mintermsAndDontCares);
	}

	@Override
	public String toString() {
		if(this == FAKE){
			return  " ";
		}

		StringBuilder builder = new StringBuilder();

		builder.append(getImplicantNumbers()).append("| ").append(getImplicantInfo());

		return builder.toString();
	}

	public String getImplicantNumbers(){
		StringBuilder builder = new StringBuilder();

		boolean first = true;
		for (Integer number : mintermsAndDontCares) {
			if (first) {
				first = false;
				builder.append(number);
			} else {
				builder.append(",").append(number);
			}
		}
		return builder.toString();
	}

	public String getVariableString(int numberOfVar){
		Preconditions.require(numberOfVar <= numberOfVariables, "Implicant size is limited therefore implicant can't be shown with more variables then is has.");
		StringBuilder builder = new StringBuilder();
		if(mintermsAndDontCares.size() == Math.pow(2,numberOfVariables)){
			return "T";
		}

		char varValue = BEG_VAR;
		for(int i = numberOfVar-1; i>=0; i--){
			int repValue = binaryRepresentation[i];
			builder.append(repValue != SIMPLIFY_MARK ? varValue: "").append(repValue == 0 ? "'":"");
			varValue = (char) (varValue+1);
		}
		return builder.toString();
	}

	public String getImplicantInfo(){
		StringBuilder builder = new StringBuilder();

		StringBuilder binary = new StringBuilder();
		for (int i = binaryRepresentation.length - 1; i >= 0; i--) {
			binary.append(binaryRepresentation[i] == SIMPLIFY_MARK ? "\u2715" : binaryRepresentation[i]);
		}

		builder.append(getBinary(numberOfVariables));

		builder.append("|").append(dontCare ? " 1 " : " 0 ").append("|").append(included ? " \u2714 " : " \u2715 ");

		return builder.toString();
	}

	public String getBinary(int size) {
		Preconditions.require(size > 0, "Size must be bigger then 0.");
		StringBuilder builder = new StringBuilder();
		boolean beginning = true;

		for (int i = numberOfVariables - 1; i >= 0; i--) {
			if(i+1 <= size){
				beginning = false;
			}
			if (binaryRepresentation[i] == 0) {
				builder.append("1");
				beginning = false;
			} else if (binaryRepresentation[i] == SIMPLIFY_MARK) {
				builder.append("X");
				beginning = false;
			} else {
				if (!beginning) {
					builder.append("0");
				}
			}
		}
		return builder.toString();
	}

	public static final Comparator<Implicant> IMPLICANTS_COMPARE = (o1, o2) -> {
		if (o1 == o2) {
			return 0;
		}

		if (o1.equals(o2)) {
			return 0;
		}

		int firstSize = o1.mintermsAndDontCares.size();
		int secondSize = o2.mintermsAndDontCares.size();
		if(firstSize != secondSize){
			return firstSize - secondSize;
		}

		int countFirstOnes = o1.countOnes();
		int countSecondOnes = o2.countOnes();

		int result = countFirstOnes - countSecondOnes;
		if (result == 0) {
			return o1.mintermsAndDontCares.iterator().next().compareTo(o2.mintermsAndDontCares.iterator().next());
		} else {
			return result;
		}
	};

}
