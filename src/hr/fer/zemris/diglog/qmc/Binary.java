package hr.fer.zemris.diglog.qmc;

import icontract.Preconditions;

/**
 * Created by bmihaela.
 */
public class Binary {

	private static final int BINARY_RANGE = 32;
	private boolean[] binaryNumber;
	private int number;

	public Binary(int number) {
		binaryNumber = convertToBinary(number);
		this.number = number;
	}

	private static boolean[] convertToBinary(int number) {
		boolean[] digits = new boolean[BINARY_RANGE];

		for (short j = 0; j < BINARY_RANGE; ++j) {
			digits[j] = (short) (number & 0x1) == 1;
			number >>= 1;
		}

		return digits;
	}

	/**
	 * Changes the bit of an number on the given index to one i true an to zero if false
	 *
	 * @param index number that will be changed
	 * @param one   changes to one if true and to zero if false
	 */
	public void changeBit(int index, boolean one) {
		Preconditions.require(index < binaryNumber.length, "Index out of range. The binary number is not long enough" +
				".");
		Preconditions.require(index >= 0, "Index can't be negative.");
		binaryNumber[index] = one;
		number = convertToInteger(binaryNumber);
	}

	private static int convertToInteger(boolean[] binary) {
		Preconditions.require(binary.length <= BINARY_RANGE, "Given binary number is to big for conversion.");
		int number = 0;
		for (int i = 0; i < binary.length; i++) {
			if (binary[i]) {
				number += Math.pow(2, i);
			}
		}

		return number;
	}

	public int getNumber() {
		return number;
	}

	/**
	 * If the number is smaller then the size empty spaces will be filled with zeros and if it is bigger it will
	 * ignore size and take how much space it needs. If size is bigger then 32 it will ignore it.
	 * If size is 1 it will allocate as much as space it is needed.
	 *
	 * @param size number of spaces
	 * @return binary representation
	 */
	public String getBinary(int size) {
		Preconditions.require(size > 0, "Size must be bigger then 0.");
		StringBuilder builder = new StringBuilder();
		boolean beginning = true;

		for (int i = BINARY_RANGE - 1; i >= 0; i--) {
			if(i+1 <= size){
				beginning = false;
			}
			if (binaryNumber[i]) {
				builder.append("1");
				beginning = false;
			} else {
				if (!beginning) {
					builder.append("0");
				}
			}
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return getBinary(1);
	}
}
