package hr.fer.zemris.diglog.qmc;

import hr.fer.zemris.diglog.qmc.element.*;
import hr.fer.zemris.diglog.qmc.observer.AreaImplicantObserver;
import hr.fer.zemris.diglog.qmc.observer.RectangleImplicantAreaObserver;
import icontract.Preconditions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by bmihaela.
 */
public class QMCApp extends JFrame {
	public static final Font DEFAULT_FONT = new Font("Sans", Font.PLAIN, 15);

	//TODO remove and edit for more variables
	private int[][] ktableNumbers;

	//TODO edit for mor vars
//	public static final int NUMBER_OF_VAR = 4;
	public int numberOfVariables;

	public static final Color DEFAULT_IMPLICANTS_COLOR = new Color(120, 134, 110, 1);

	private Box inputArea;
	private JTextField mintermsInputArea;
	private JTextField dontcareInputArea;

	private Box implicantsArea;
	private Box primaryImplicantsArea;

	private List<Integer> minterms;
	private List<Integer> dontCares;
	private List<Implicant> implicants;

	private List<Implicant> worthyImplicants;
	private List<Implicant> primaryImplicants;

	private DefaultListModel<Implicant> jListWorthyImplicantsModel;
	private DefaultListModel<Implicant> jListImplicantsModel;
	private DefaultListModel<Implicant> jListPrimaryImplicantsModel;

	private JList<Implicant> jListWorthyImplicants;
	private JList<Implicant> jListPrimaryImplicants;
	private JList<Implicant> jListImplicants;

	private int kTableRows;
	private int kTableColumns;

	private List<Implicant> lastSelectedImplicants = new ArrayList<>();

	private ZoomClickArea drawArea = new ZoomClickArea();

	private ActionListener minimize = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			System.out.println(checkPanel.getIndex());
			if (checkPanel.getIndex() == 0) {
				JOptionPane.showMessageDialog(QMCApp.this, "Molimo selektirajte broj varijabla", "Error", JOptionPane
						.ERROR_MESSAGE);
				return;
			}

			//Implicant creation
			if (mintermsInputArea.getText().isEmpty()) {
				JOptionPane.showMessageDialog(QMCApp.this, "Mora se unjeti barem jedan minterm", "Error", JOptionPane
						.ERROR_MESSAGE);
				return;
			}

			int tempVariables = checkPanel.getIndex();

			List<Integer> tempMinterms;
			List<Integer> tempDontCares;
			List<Implicant> tempImplicants;

			try {
				String mintermsTextInput = mintermsInputArea.getText();
				tempMinterms = Parser.getNumbers(mintermsTextInput);
				String dontCaresTextInput = dontcareInputArea.getText();
				tempDontCares = Parser.getNumbers(dontCaresTextInput);


				if (!dontCares.isEmpty() && QMCUtil.intersection(minterms, dontCares)) {
					throw new IllegalArgumentException("Mintermi i don't careovi ne mogu sadržavati iste vrijednosti");
				}

				tempImplicants = Implicant.turnToImplicant(tempMinterms, false, tempVariables);
				tempImplicants.addAll(Implicant.turnToImplicant(tempDontCares, true, tempVariables));

			} catch (IllegalArgumentException ex) {
				JOptionPane.showMessageDialog(QMCApp.this, ex.getMessage(), "Error", JOptionPane
						.ERROR_MESSAGE);
				return;
			}

			reset();

			implicants.addAll(tempImplicants);
			minterms.addAll(tempMinterms);
			dontCares.addAll(tempDontCares);
			numberOfVariables = tempVariables;
			int verticalNumber = numberOfVariables / 2;
			int horizontalNumber = numberOfVariables - verticalNumber;
			ktableNumbers = getKtable(verticalNumber, horizontalNumber);
			kTableRows = (int) Math.pow(2, verticalNumber);
			kTableColumns = (int) Math.pow(2, horizontalNumber);

			//Implicants simplifysation - one list

			List<Implicant> temporaryImplicants = new ArrayList<>();
			temporaryImplicants.addAll(implicants);
			List<Implicant> secondTemporapyImplicants = new ArrayList<>();

			boolean combinationOccured;
			do {
				combinationOccured = false;
				for (Implicant firstImplicant : temporaryImplicants) {
					for (Implicant secondImplicant : temporaryImplicants) {
						Implicant newImplicant = Implicant.simplifyImplicants(firstImplicant, secondImplicant);
						if (newImplicant != null) {
							firstImplicant.setIncluded(true);
							secondImplicant.setIncluded(true);
							if (newImplicant.isSorted()) {
								secondTemporapyImplicants.add(newImplicant);
								combinationOccured = true;
							}
						}
					}
				}

				implicants.addAll(secondTemporapyImplicants);
				temporaryImplicants.clear();
				temporaryImplicants.addAll(secondTemporapyImplicants);
				secondTemporapyImplicants.clear();

				if (!combinationOccured) {
					break;
				}

			} while (true);
			Collections.sort(implicants, Implicant.IMPLICANTS_COMPARE);


			for (Implicant implicant : implicants) {
				if (!implicant.isImplicantDontCare() && !implicant.isIncluded()) {
					primaryImplicants.add(implicant);
				}
			}
			Collections.sort(primaryImplicants, Implicant.IMPLICANTS_COMPARE);


			Implicant implicantOne = null;
			for (Integer minterm : minterms) {
				for (Implicant implicant : primaryImplicants) {
					if (implicant.contains(minterm)) {
						if (implicantOne != null) {
							implicantOne = null;
							break;
						}
						implicantOne = implicant;
					}
				}
				if (implicantOne != null && !worthyImplicants.contains(implicantOne)) {
					worthyImplicants.add(implicantOne);
					implicantOne = null;
				}
			}
			Collections.sort(worthyImplicants, Implicant.IMPLICANTS_COMPARE);


			addImplicantsToList();
			drawTables();

			revalidate();
			repaint();
		}
	};

	private int[][] getKtable(int verticalVar, int horizontalVar) {

		List<Binary> horizontal;
		List<Binary> vertical;

		if(verticalVar == 0){
			vertical = new ArrayList<>();
			vertical.add(new Binary(0));
		}else {
			vertical = getGray(verticalVar);
		}horizontal = getGray(horizontalVar);
		int rows = vertical.size();
		int columns = horizontal.size();

		int[][] table = new int[rows][columns];
		for (int i = 0; i < columns; i++) {
			String beg = horizontal.get(i).getBinary(horizontalVar);
			for (int j = 0; j < rows; j++) {
				String end = "";
				if(verticalVar != 0){
					end = vertical.get(j).getBinary(verticalVar);
				}
				table[j][i] = Integer.parseInt(beg + end, 2);
			}
		}
		return table;
	}

	public static java.util.List<Binary> getGray(int n) {
		Preconditions.require(n > 0, "Can't get gray code for a negative or zero variableCount.");

		List<Binary> numbers = new ArrayList<>();
		int size = (int) Math.pow(2, n);

		int position = 1;
		int currentSize = 2;
		for (int i = 0; i < size; i++) {
			if (i + 1 > currentSize) {
				currentSize = currentSize * 2;
				position++;
			}
			if (i == 0) {
				numbers.add(new Binary(0));
			} else if (i == 1) {
				numbers.add(new Binary(1));
			} else {
				Binary number = new Binary(numbers.get(currentSize - 1 - i).getNumber());
				number.changeBit(position - 1, true);
				numbers.add(number);
			}

		}

		return numbers;
	}

	private CheckPanel checkPanel;


	public void reset() {
		implicants.clear();
		minterms.clear();
		dontCares.clear();
		worthyImplicants.clear();
		primaryImplicants.clear();
	}

	private void addImplicantsToList() {
		jListImplicantsModel.removeAllElements();
		Implicant previous = implicants.get(0);
		jListImplicantsModel.addElement(previous);
		int size = implicants.size();
		for (int i = 1; i < size; i++) {
			Implicant current = implicants.get(i);
			if (current.size() > previous.size()) {
				jListImplicantsModel.addElement(Implicant.FAKE);
			}
			jListImplicantsModel.addElement(current);
			previous = current;
		}
//		addImplicantsToList(implicants, jListImplicantsModel);
		addImplicantsToList(primaryImplicants, jListPrimaryImplicantsModel);
		addImplicantsToList(worthyImplicants, jListWorthyImplicantsModel);
	}

	private void addImplicantsToList(List<Implicant> implicants, DefaultListModel<Implicant>
			jListImplicantsModel) {
		jListImplicantsModel.removeAllElements();
		for (Implicant implicant : implicants) {
			jListImplicantsModel.addElement(implicant);
		}
	}


	//global
	private static final int MAIN_BARS_THICKNES = 2;
	private static final int SMALL_BARS_THICKNES = 1;
	private static final int MARGIN = 20;
	private static final int DEFAULT_IMPLICNATS_MARGIN = 10;

	//first table
	private int columnLenght;
	private static final int DEFAULT_XTABLE_MARGIN = 40;
	private int columnHeight;

	//second talbe
	private int secondColumnHeight = 30;
	private int secondColumnWidth = 70;

	//ktable
	private static final int KTABLE_OFFSET = 5;
	private int ktableColumnSize = 40;
	private int ktableInnerXoffset = 12;
	private int ktableInnerYoffset = 25;

	//begIndexes
	private int ktableBegX;
	private int ktableBegY;

	private int secondPhaseBegX;
	private int secondPhaseBegY;

	private int minShapeX;
	private int minShapeY;

	private List<Area> ktableRoundings = new ArrayList<>();


	private void drawTables() {
		drawArea.clearElements();
		drawArea.clearObservers();
		drawArea.resetTransformation();

		int numberOfColumns = 1;

		int tempNumberOfRows = 0;
		int maxNumberOfRows = 0;

		//First phase table drawing
		FontMetrics fm = getFontMetrics(getFont());
		int fontHeight = fm.getHeight();

		columnLenght = getWidth(implicants, fm) + DEFAULT_XTABLE_MARGIN;
		columnHeight = fontHeight + DEFAULT_IMPLICNATS_MARGIN;

		int numberOfImplicants = implicants.size();
		Implicant implicant = null;
		for (int i = 0; i < numberOfImplicants; i++) {

			if (i != 0) {
				if (implicant.size() < implicants.get(i).size()) {
					if (tempNumberOfRows > maxNumberOfRows) {
						maxNumberOfRows = tempNumberOfRows;
					}
					tempNumberOfRows = 0;
					numberOfColumns++;
				}
				if (implicant.countOnes() < implicants.get(i).countOnes()) {
					drawArea.addElement(new AreaRectangle(MARGIN + ((numberOfColumns - 1) * columnLenght),
							MARGIN + (tempNumberOfRows + 1) * columnHeight, columnLenght, SMALL_BARS_THICKNES));
				}
			}
			tempNumberOfRows++;

			implicant = implicants.get(i);
			String info = implicant.toString();
			int stringSize = fm.stringWidth(info);
			drawArea.addElement(new AreaString(MARGIN + (numberOfColumns * columnLenght - stringSize),
					MARGIN + (tempNumberOfRows + 1) * columnHeight - (columnHeight / 2 - fontHeight / 2), info));
			drawArea.addObserver(new RectangleImplicantAreaObserver(MARGIN + ((numberOfColumns - 1) * columnLenght),
					MARGIN + tempNumberOfRows * columnHeight, columnLenght, columnHeight, implicant) {
				@Override
				public void fire() {
					lastSelectedImplicants.clear();
					lastSelectedImplicants.add(this.getImplicant());
					implicantsSelected(Arrays.asList(lastSelectedImplicants.get(0)));
					revalidate();
					repaint();
				}
			});
		}


		drawArea.addElement(new AreaRectangle(MARGIN, MARGIN + columnHeight, numberOfColumns * columnLenght,
				MAIN_BARS_THICKNES)

		);

		for (int i = 1; i <= numberOfColumns; i++) {
			drawArea.addElement(new AreaRectangle(MARGIN + columnLenght * i, MARGIN, MAIN_BARS_THICKNES,
					columnHeight * (maxNumberOfRows + 1)));
		}


		//Second phase table drawing
		secondPhaseBegX = MARGIN;
		secondPhaseBegY = 3 * MARGIN + ((1 + maxNumberOfRows) * columnHeight > ktableColumnSize * kTableRows ? (1 + maxNumberOfRows) * columnHeight : ktableColumnSize * kTableRows);

		int primaryImplicantsSize = primaryImplicants.size();
		int mintermSize = minterms.size();
		for (int i = 0; i < primaryImplicantsSize + 2; i++) {
			//horizontal lines
			if (i == 0) {
				drawArea.addElement(new AreaRectangle(secondPhaseBegX + secondColumnWidth, secondPhaseBegY + i *
						secondColumnHeight, secondColumnWidth * (mintermSize),
						MAIN_BARS_THICKNES));
			} else {
				if (i > 1 && i < primaryImplicantsSize + 2) {
					String output = primaryImplicants.get(i - 2).getVariableString(numberOfVariables);
					int outputWidth = fm.stringWidth(output);
					drawArea.addElement(new AreaString(secondPhaseBegX + (secondColumnWidth / 2 - outputWidth / 2),
							secondPhaseBegY + i *
									secondColumnHeight - (secondColumnHeight / 2 - fontHeight / 2), output));
					drawArea.addObserver(new RectangleImplicantAreaObserver(secondPhaseBegX, secondPhaseBegY + (i -
							1) *
							secondColumnHeight, secondColumnWidth, secondColumnHeight, primaryImplicants.get(i -
							2)) {
						@Override
						public void fire() {
							lastSelectedImplicants.clear();
							lastSelectedImplicants.add(this.getImplicant());
							implicantsSelected(Arrays.asList(lastSelectedImplicants.get(0)));
							revalidate();
							repaint();
						}
					});
				}
				drawArea.addElement(new AreaRectangle(secondPhaseBegX, secondPhaseBegY + i *
						secondColumnHeight, secondColumnWidth * (mintermSize + 1), MAIN_BARS_THICKNES));
			}
		}
		for (int i = 0; i < mintermSize + 2; i++) {
			//vertical lines
			if (i == 0) {
				drawArea.addElement(new AreaRectangle(secondPhaseBegX + i * secondColumnWidth, secondColumnHeight
						+ secondPhaseBegY, MAIN_BARS_THICKNES, secondColumnHeight *
						(primaryImplicantsSize)));
			} else {
				if (i < mintermSize + 1) {
					String output = minterms.get(i - 1).toString();
					int outputWidth = fm.stringWidth(output);
					drawArea.addElement(new AreaString(secondPhaseBegX + (secondColumnWidth / 2 - outputWidth / 2) +
							i * secondColumnWidth, secondColumnHeight - (secondColumnHeight / 2 - fontHeight /
							2) +
							secondPhaseBegY, output));
				}
				drawArea.addElement(new AreaRectangle(secondPhaseBegX + i * secondColumnWidth, secondPhaseBegY,
						MAIN_BARS_THICKNES, secondColumnHeight * (primaryImplicantsSize + 1)));
			}
		}


		String markOutput = "\u2715";
		int markOutputWidth = fm.stringWidth(markOutput);
		for (int i = 0; i < primaryImplicantsSize; i++) {
			Implicant currentImplicant = primaryImplicants.get(i);
			for (int j = 0; j < mintermSize; j++) {
				if (currentImplicant.contains(minterms.get(j))) {
					drawArea.addElement(new AreaString(secondPhaseBegX + secondColumnWidth + j *
							secondColumnWidth +
							(secondColumnWidth / 2 - markOutputWidth / 2),
							secondPhaseBegY + secondColumnHeight + secondColumnHeight + i * secondColumnHeight -
									(secondColumnHeight / 2 - fontHeight /
											2), markOutput));
				}
			}
		}
		//checks for the table
		List<Integer> mintermsCovered = new ArrayList<>();
		String checkOutput = "\u2714";
		int checkOutputWidth = fm.stringWidth(checkOutput);
		for (int i = 0; i < mintermSize; i++) {
			int minterm = minterms.get(i);
			int worthyImplicantSize = worthyImplicants.size();
			for (int j = 0; j < worthyImplicantSize; j++) {
				Implicant current = worthyImplicants.get(j);
				if (current.contains(minterm)) {
					mintermsCovered.add(minterm);
					drawArea.addElement(new AreaString(secondPhaseBegX + secondColumnWidth + i * secondColumnWidth
							+ (secondColumnWidth / 2 - checkOutputWidth / 2), secondPhaseBegY + secondColumnHeight
							* (2 + primaryImplicantsSize) + (secondColumnHeight / 2 - fontHeight / 2), checkOutput));
					break;
				}
			}
		}


		//Ktable drawing
		ktableBegX = numberOfColumns * columnLenght + MARGIN * (numberOfVariables+1);
		ktableBegY = MARGIN * 2;

		for (int i = 0; i <= kTableRows; i++) {

			drawArea.addElement(new AreaRectangle(ktableBegX, i * ktableColumnSize + MARGIN + MARGIN,
					kTableColumns
							* ktableColumnSize, MAIN_BARS_THICKNES));
		}
		for (int i = 0; i <= kTableColumns; i++) {
			drawArea.addElement(new AreaRectangle(ktableBegX + i * ktableColumnSize, MARGIN + MARGIN,
					MAIN_BARS_THICKNES, kTableRows * ktableColumnSize));
		}

		for (int i = 0; i < kTableRows; i++) {
			for (int j = 0; j < kTableColumns; j++) {
				int number = ktableNumbers[i][j];
				if (minterms.contains(number) || dontCares.contains(number)) {
					String text = minterms.contains(number) ? "1" : "\u2715";
					drawArea.addElement(new AreaString(ktableBegX + ktableColumnSize * j + ktableInnerXoffset,
							ktableInnerYoffset + ktableBegY + ktableColumnSize * i, text));
				}
			}
		}

		//minimal shape

		minShapeY = secondPhaseBegY + secondColumnHeight * (primaryImplicantsSize + 2) + MARGIN * 3 + fontHeight * 2;
		minShapeX = MARGIN;


		drawArea.addElement(new AreaString(minShapeX, minShapeY - fontHeight * 2,
				"Minimalni oblici:"));

		List<MinimalShape> minimalShapes = getMinimalShapes();

		int minimalShapeSize = minimalShapes.size();
		for (int i = 0; i < minimalShapeSize; i++) {
			MinimalShape current = minimalShapes.get(i);
			drawArea.addElement(new AreaString(minShapeX, minShapeY + (DEFAULT_IMPLICNATS_MARGIN + fontHeight) * i,
					current.toString()));
			drawArea.addObserver(new RectangleImplicantAreaObserver(minShapeX, minShapeY + (DEFAULT_IMPLICNATS_MARGIN
					+ fontHeight) * (i
					- 1) + (DEFAULT_IMPLICNATS_MARGIN + fontHeight) / 2, fm.stringWidth(current.toString()),
					(DEFAULT_IMPLICNATS_MARGIN + fontHeight), current.getImplicants()) {
				@Override
				public void fire() {
					lastSelectedImplicants.clear();
					lastSelectedImplicants.addAll(getImplicants());
					implicantsSelected(lastSelectedImplicants);
					revalidate();
					repaint();
				}
			});
		}

	}

	private List<MinimalShape> getMinimalShapes() {
		List<MinimalShape> minimalShapes = new ArrayList<>();
		List<List<Implicant>> patricForm = new ArrayList<>();

		List<Integer> remainingMinterms = new ArrayList<>();
		remainingMinterms.addAll(minterms);

		int worthyImplicantSize = worthyImplicants.size();
		for (int i = 0; i < worthyImplicantSize; i++) {
			Implicant current = worthyImplicants.get(i);
			remainingMinterms.removeAll(current.getMintermsAndDontCares());
		}

		List<Implicant> remainingImplicants = new ArrayList<>();
		remainingImplicants.addAll(primaryImplicants);
		remainingImplicants.removeAll(worthyImplicants);

		int remainingImplicantsSize = remainingImplicants.size();
		int remainingMintermsSize = remainingMinterms.size();

		if (remainingMintermsSize == 0) {
			minimalShapes.add(new MinimalShape(worthyImplicants));
		} else {

			for (int i = 0; i < remainingMintermsSize; i++) {
				int currentMinterm = remainingMinterms.get(i);
				patricForm.add(new ArrayList<>());
				for (int j = 0; j < remainingImplicantsSize; j++) {
					Implicant current = remainingImplicants.get(j);
					if (current.contains(currentMinterm)) {
						patricForm.get(i).add(current);
					}
				}
			}


			List<Implicant> current = patricForm.get(0);
			List<MinimalShape> patricFormReduced = new ArrayList<>();
			for (int i = 0; i < current.size(); i++) {
				patricFormReduced.add(new MinimalShape(current.get(i)));
			}

			for (int i = 1; i < remainingMintermsSize; i++) {
				List<Implicant> currentImplicants = patricForm.get(i);

				for (int j = 0; j < patricFormReduced.size(); j++) {
					MinimalShape minimalShape = patricFormReduced.get(j);

					for (int k = 0; k < currentImplicants.size(); k++) {
						MinimalShape newMinimalShape = new MinimalShape(minimalShape);
						newMinimalShape.addImplicant(currentImplicants.get(k));
						if (!minimalShapes.contains(newMinimalShape)) {
							minimalShapes.add(newMinimalShape);
						}
					}
				}

				patricFormReduced.clear();
				patricFormReduced.addAll(minimalShapes);
				minimalShapes.clear();
			}

			int smallestNumberOfVArs = getNumberOfVars(patricFormReduced);
			for (int i = 0; i < patricFormReduced.size(); i++) {
				MinimalShape minimalShape = patricFormReduced.get(i);
				if (minimalShape.getSize() == smallestNumberOfVArs) {
					minimalShape.addImplicants(worthyImplicants);
					minimalShapes.add(minimalShape);

				}
			}
		}
		return minimalShapes;
	}


	private int getNumberOfVars(List<MinimalShape> patricFormReduced) {
		int size = patricFormReduced.size();
		int smallest = 0;
		boolean first = true;
		for (int i = 0; i < size; i++) {
			MinimalShape minimalShape = patricFormReduced.get(i);
			int current = minimalShape.getSize();
			if (first) {
				first = false;
				smallest = current;
				continue;
			}
			if (current < smallest) {
				smallest = current;
			}
		}

		return smallest;
	}

	private int getWidth(List<Implicant> implicants, FontMetrics fm) {
		int width = 0;
		int implicantsSize = implicants.size();
		for (int i = 0; i < implicantsSize; i++) {
			Implicant current = implicants.get(i);
			int currentWidth = fm.stringWidth(current.toString());
			if (currentWidth > width) {
				width = currentWidth;
			}
		}
		return width;
	}

	public void addRoundingsToKTable(List<Implicant> implicants) {
		for (Implicant implicant : implicants) {
			this.addRoundingToKTable(implicant);
		}
	}

	private void clearRoundings() {
		for (Area area : ktableRoundings) {
			drawArea.removeElement(area);
		}
	}

	//TODO
	public void addRoundingToKTable(Implicant implicant) {

		List<Pair> selected = new ArrayList<>();
		for (int i = 0; i < kTableRows; i++) {
			for (int j = 0; j < kTableColumns; j++) {
				int number = ktableNumbers[i][j];
				if (implicant.contains(number)) {
					Pair pair = new Pair(j, i);
					selected.add(pair);
				}
			}
		}

		Rounding selection = getRounding(selected);

		Color color = implicant.getColor();
		if (selection.isSplitHorizontal() && selection.isSplitVertical()) {
			AreaArch rounding = new AreaArch(ktableBegX + (selection.getX() - 1) * ktableColumnSize,
					ktableBegY + (selection.getY() - 1) * ktableColumnSize, ktableColumnSize * selection
					.getHorizontalUnitSize(),
					ktableColumnSize * selection.getVerticalUnitSize(), 270, 90, color);
			ktableRoundings.add(rounding);

			drawArea.addElement(rounding);
			AreaArch rounding2 = new AreaArch(ktableBegX + (selection.getX() - 1 + kTableColumns) * ktableColumnSize,
					ktableBegY + (selection.getY() - 1 + kTableRows) * ktableColumnSize, ktableColumnSize * selection
					.getHorizontalUnitSize(),
					ktableColumnSize * selection.getVerticalUnitSize(), 90, 90, color);
			ktableRoundings.add(rounding2);
			drawArea.addElement(rounding2);

			AreaArch rounding3 = new AreaArch(ktableBegX + (selection.getX() - 1 + kTableColumns) * ktableColumnSize,
					ktableBegY + (selection.getY() - 1) * ktableColumnSize, ktableColumnSize * selection
					.getHorizontalUnitSize(),
					ktableColumnSize * selection.getVerticalUnitSize(), 180, 90, color);
			ktableRoundings.add(rounding3);
			drawArea.addElement(rounding3);

			AreaArch rounding4 = new AreaArch(ktableBegX + (selection.getX() - 1) * ktableColumnSize,
					ktableBegY + (selection.getY() - 1 + kTableRows) * ktableColumnSize, ktableColumnSize * selection
					.getHorizontalUnitSize(),
					ktableColumnSize * selection.getVerticalUnitSize(), 0, 90, color);
			ktableRoundings.add(rounding4);
			drawArea.addElement(rounding4);


		} else if (selection.isSplitHorizontal()) {
			AreaArch rounding = new AreaArch(ktableBegX + selection.getX() * ktableColumnSize - ktableColumnSize,
					ktableBegY + selection.getY() * ktableColumnSize, ktableColumnSize * selection
					.getHorizontalUnitSize(),
					ktableColumnSize * selection.getVerticalUnitSize(), 270, 180, color);
			ktableRoundings.add(rounding);
			drawArea.addElement(rounding);

			AreaArch rouning2 = new AreaArch(ktableBegX + selection.getX() * ktableColumnSize +
					(kTableColumns - 1) * ktableColumnSize,
					ktableBegY + selection.getY() * ktableColumnSize, ktableColumnSize *
					selection.getHorizontalUnitSize(), ktableColumnSize * selection.getVerticalUnitSize(), 90, 180,
					color);
			drawArea.addElement(rouning2);
			ktableRoundings.add(rouning2);

		} else if (selection.isSplitVertical()) {
			AreaArch rounding = new AreaArch(ktableBegX + selection.getX() * ktableColumnSize,
					ktableBegY + (selection.getY() - 1) * ktableColumnSize, ktableColumnSize * selection
					.getHorizontalUnitSize(),
					ktableColumnSize * selection.getVerticalUnitSize(), 180, 180, color);
			drawArea.addElement(rounding);
			ktableRoundings.add(rounding);

			AreaArch rounding2 = new AreaArch(ktableBegX + selection.getX() * ktableColumnSize,
					ktableBegY + (selection.getY() + kTableRows - 1) * ktableColumnSize, ktableColumnSize *
					selection.getHorizontalUnitSize(), ktableColumnSize * selection.getVerticalUnitSize(), 0, 180,
					color);
			drawArea.addElement(rounding2);
			ktableRoundings.add(rounding2);


		} else {
			AreaOval rounding = new AreaOval(ktableBegX + (selection.getX()) * ktableColumnSize,
					ktableBegY + (selection.getY()) * ktableColumnSize, ktableColumnSize * selection
					.getHorizontalUnitSize(),
					ktableColumnSize * selection.getVerticalUnitSize(), color);
			drawArea.addElement(rounding);
			ktableRoundings.add(rounding);

		}

	}

	private Rounding getRounding(List<Pair> selected) {
		List<Integer> xComponents = new ArrayList<>();
		List<Integer> yComponents = new ArrayList<>();
		boolean first = true;
		Pair smallest = null;

		boolean splitVertical = false;
		boolean splitHorizontal = false;
		for (Pair pair : selected) {
			if (!xComponents.contains(pair.getX())) {
				xComponents.add(pair.getX());
			}
			if (!yComponents.contains(pair.getY())) {
				yComponents.add(pair.getY());
			}
			if (first) {
				smallest = pair;
				first = false;
			}
			if (pair.getX() < smallest.getX() && pair.getY() < smallest.getY()) {
				smallest = pair;
			}
		}

		if (xComponents.size() == 2 && (xComponents.get(0) + 1 != xComponents.get(1) && xComponents.get(0) !=
				xComponents.get(1) - 1) && kTableColumns > 2) {
			splitHorizontal = true;
		}
		if (yComponents.size() == 2 && (yComponents.get(0) + 1 != yComponents.get(1) && yComponents.get(0) !=
				yComponents.get(1) - 1) && kTableRows > 2) {
			splitVertical = true;
		}


		return new Rounding(yComponents.size(), xComponents.size(), smallest.getX(), smallest.getY(), splitHorizontal,
				splitVertical);
	}


	public QMCApp() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(0, 0, 1000, 700);
		setLayout(new BorderLayout());

		initialization();
		intiGUI();
	}

	private void initialization() {
		implicants = new ArrayList<>();
		dontCares = new ArrayList<>();
		minterms = new ArrayList<>();
		worthyImplicants = new ArrayList<>();
		primaryImplicants = new ArrayList<>();
		jListImplicantsModel = new DefaultListModel<>();
		jListPrimaryImplicantsModel = new DefaultListModel<>();
		jListWorthyImplicantsModel = new DefaultListModel<>();
	}

	private void intiGUI() {
		setUpInputOutputArea();
	}

	private void setUpInputOutputArea() {
		this.inputArea = Box.createHorizontalBox();
		this.add(inputArea, BorderLayout.PAGE_START);

		this.mintermsInputArea = new JTextField();
		this.dontcareInputArea = new JTextField();

		//check Panel
		checkPanel = new CheckPanel("1", "2", "3", "4");
		checkPanel.setPreferredSize(new Dimension(140, 0));
		inputArea.add(checkPanel);

		JLabel mintermLabel = new JLabel("Minterm:");
		mintermLabel.setFont(DEFAULT_FONT);

		inputArea.add(mintermLabel);
		inputArea.add(mintermsInputArea);

		JLabel dontcareLabel = new JLabel("Don't care:");
		dontcareLabel.setFont(DEFAULT_FONT);
		inputArea.add(dontcareLabel);
		inputArea.add(dontcareInputArea);

		//TODO: change style of the button
		JButton startButton = new JButton("Minimiziraj");
		startButton.setFont(DEFAULT_FONT);
		startButton.addActionListener(minimize);

		inputArea.add(startButton);
		inputArea.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

		implicantsArea = Box.createVerticalBox();
		implicantsArea.setPreferredSize(new Dimension(200, 200));
		this.add(implicantsArea, BorderLayout.LINE_START);

		primaryImplicantsArea = Box.createVerticalBox();
		primaryImplicantsArea.setPreferredSize(new Dimension(220, 200));
		this.add(primaryImplicantsArea, BorderLayout.LINE_END);

		this.add(drawArea, BorderLayout.CENTER);

		jListWorthyImplicants = new JList<>(jListWorthyImplicantsModel);
		jListPrimaryImplicants = new JList<>(jListPrimaryImplicantsModel);
		jListImplicants = new JList<>(jListImplicantsModel);

		jListSetup(jListImplicants);
		jListSetup(jListWorthyImplicants);
		jListSetup(jListPrimaryImplicants);


		JLabel implicantsLabel = new JLabel("Implikanti:");
		implicantsLabel.setFont(DEFAULT_FONT);
		implicantsArea.add(implicantsLabel);
		implicantsArea.add(new JScrollPane(jListImplicants));

		JLabel primaryImplicantsLabel = new JLabel("Primarni implikanti:");
		primaryImplicantsLabel.setFont(DEFAULT_FONT);
		primaryImplicantsArea.add(primaryImplicantsLabel);
		primaryImplicantsArea.add(new JScrollPane(jListPrimaryImplicants));

		JLabel worthyImplicantsLabel = new JLabel("Bitni implikanti:");
		worthyImplicantsLabel.setFont(DEFAULT_FONT);
		primaryImplicantsArea.add(worthyImplicantsLabel);
		primaryImplicantsArea.add(new JScrollPane(jListWorthyImplicants));

	}

	private void jListSetup(JList<Implicant> jList) {

		jList.setFont(DEFAULT_FONT);
		jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		jList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Implicant current = jList.getSelectedValue();
				if (current != Implicant.FAKE) {
					implicantsSelected(Arrays.asList(current));
					lastSelectedImplicants.clear();
					lastSelectedImplicants.add(current);
				} else {
					implicantsSelected(lastSelectedImplicants);
				}
				revalidate();
				repaint();
			}
		});


		DefaultListCellRenderer renderer = (DefaultListCellRenderer) jList.getCellRenderer();
		renderer.setHorizontalAlignment(JLabel.RIGHT);

	}


	private void implicantsSelected(List<Implicant> selectedValues) {
		clearRoundings();
		drawArea.resetObservers();
		setObserverToDraw(selectedValues);
		selectListValues(selectedValues);
		QMCApp.this.addRoundingsToKTable(selectedValues);

	}

	private void selectListValues(List<Implicant> selectedValues) {
		jListImplicants.clearSelection();
		jListPrimaryImplicants.clearSelection();
		jListWorthyImplicants.clearSelection();
		jListImplicants.setSelectedIndices(getSelectedIndices(selectedValues, implicants, jListImplicantsModel));

		int[] selection = getSelectedIndices(selectedValues, worthyImplicants, jListWorthyImplicantsModel);
		if (selection != null) {
			jListWorthyImplicants.setSelectedIndices(selection);
		}

		selection = getSelectedIndices(selectedValues, primaryImplicants, jListPrimaryImplicantsModel);
		if (selection != null) {
			jListPrimaryImplicants.setSelectedIndices(selection);
		}

//		for (Implicant implicant : selectedValues) {
//			jListImplicants.setSelectedValue(implicant, true);
//			jListPrimaryImplicants.setSelectedValue(implicant, true);
//			jListWorthyImplicants.setSelectedValue(implicant, true);
//		}
	}

	private int[] getSelectedIndices(List<Implicant> selectedValues, List<Implicant> implicants,
									 DefaultListModel<Implicant> model) {
		int[] selectedIndices = new int[selectedValues.size()];
		int size = implicants.size();
		boolean nothingSelected = true;
		for (int i = 0, j = 0; i < size; i++) {
			Implicant current = implicants.get(i);
			if (selectedValues.contains(current)) {
				int sizeModel = model.size();
				Implicant element;
				int k;
				for (k = 0; k < sizeModel; k++) {
					element = model.getElementAt(k);
					if (element.equals(current)) {
						nothingSelected = false;
						break;
					}
				}
				selectedIndices[j] = k;
				j++;
			}
		}
		if (nothingSelected) {
			return null;
		}
		return selectedIndices;
	}

	private void setObserverToDraw(List<Implicant> selectedValues) {
		List<AreaImplicantObserver> observers = drawArea.getObservers();
		Implicant implicant;
		for (AreaImplicantObserver observer : observers) {
			implicant = observer.getImplicant();
			for (Implicant givenImplicant : selectedValues) {
				if (implicant.equals(givenImplicant) && observer.size() == 1) {
					observer.drawObserver(true);
				}
			}
		}
	}


	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new QMCApp().setVisible(true));
	}
}
