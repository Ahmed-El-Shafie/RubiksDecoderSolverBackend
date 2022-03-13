package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.primitives.Chars;

import moves.Move;
import moves.MoveType;
import moves.RotateMove;
import moves.SlideMove;

public class Solver {

	public static final int NUM_ROWS = 3;
	public static final int NUM_COLS = 6;
	public static final Set<Character> CHAR_SET = Collections
			.unmodifiableSet(new HashSet<>(Arrays.asList('r', 'y', 'b', 'o', 'g', 'w', '-')));
	public static final List<Character> CHAR_LIST = Collections.unmodifiableList(
			Arrays.asList('r', 'r', 'r', 'y', 'y', 'y', 'b', 'b', 'b', 'o', 'o', 'o', 'g', 'g', 'g', 'w', 'w', '-'));

	public static final String WRONG_ROW_LENGTH = "Row %d must have exactly %d squares";
	public static final String WRONG_NUM_ROWS = "There must be exactly %d rows";
	public static final String INVALID_CHARACTER = "'%c' not in character set";
	public static final String ONE_BLANK_SQUARE_ONLY = "Need exactly 1 '-' squares, but there are ";
	public static final String TWO_WHITE_SQUARES_REQUIRED = "Need exactly 2 'w' squares, but there are ";
	public static final String THREE_SQUARES_PER_CHARACTER = "Need exactly 3 '%c' squares, but there are %d";

	private final Map<State, Float> stateHeuristics;

	public Solver() {
		stateHeuristics = new HashMap<>();
	}

	public List<Move> solveRubiksDecoder(char[][] inputArray) {
		List<String> errorMsgs = validateInput(inputArray);
		if (!errorMsgs.isEmpty()) {
			throw new InvalidConfigurationException(errorMsgs);
		}
		State startState = new State(inputArray, Collections.emptyList(), 0);
		List<Move> solutionPath = getSolutionPath(startState);
		shortenRotations(solutionPath);
		return solutionPath;
	}

	private List<Move> getSolutionPath(State startState) {
		float startHeuristic = computeHeuristic(startState);
		Map<State, Float> lowestStateCosts = new HashMap<>();
		stateHeuristics.put(startState, startHeuristic);
		lowestStateCosts.put(startState, startHeuristic);
		PriorityQueue<State> statesToCheck = new PriorityQueue<>(
				new AStarComparator().thenComparing(Comparator.comparingInt(State::getQueueInsertNum)));
		statesToCheck.add(startState);
		while (!statesToCheck.isEmpty()) {
			State currentState = statesToCheck.remove();
			List<Move> pathToCurrentState = currentState.getPathToState();
			float currentHeuristic = stateHeuristics.get(currentState);
			if (pathToCurrentState.size() + currentHeuristic <= lowestStateCosts.get(currentState)) {
				if (isSolution(currentState.getStateArray())) {
					return pathToCurrentState;
				}
				List<State> successors = getSuccessors(currentState);
				for (State newState : successors) {
					float newHeuristic;
					if (stateHeuristics.containsKey(newState)) {
						newHeuristic = stateHeuristics.get(newState);
					} else {
						newHeuristic = computeHeuristic(newState);
						stateHeuristics.put(newState, newHeuristic);
					}
					float newCost = newState.getPathToState().size() + newHeuristic;
					if (!lowestStateCosts.containsKey(newState) || newCost < lowestStateCosts.get(newState)) {
						statesToCheck.add(newState);
						lowestStateCosts.put(newState, newCost);
					}
				}
			}
		}
		throw new IllegalArgumentException("No solution found. Invalid input configuration");
	}

	/*
	 * Validation functions
	 */

	@VisibleForTesting
	static List<String> validateInput(char[][] inputArray) {
		List<String> errorMsgs = new ArrayList<>();
		if (inputArray.length != NUM_ROWS) {
			errorMsgs.add(String.format(WRONG_NUM_ROWS, NUM_ROWS));
		}
		Map<Character, Integer> charCounts = new HashMap<>();
		for (int rowNum = 0; rowNum < inputArray.length; rowNum++) {
			char[] row = inputArray[rowNum];
			if (rowNum < NUM_ROWS && row.length != NUM_COLS) {
				errorMsgs.add(String.format(WRONG_ROW_LENGTH, rowNum + 1, NUM_COLS));
			}
			for (char square : row) {
				if (!CHAR_SET.contains(square)) {
					errorMsgs.add(String.format(INVALID_CHARACTER, square));
				}
				charCounts.put(square, charCounts.getOrDefault(square, 0) + 1);
			}
		}
		for (char character : CHAR_SET) {
			int count = charCounts.getOrDefault(character, 0);
			if (character == '-' && count != 1) {
				errorMsgs.add(ONE_BLANK_SQUARE_ONLY + count);
			}
			if (character == 'w' && count != NUM_ROWS - 1) {
				errorMsgs.add(TWO_WHITE_SQUARES_REQUIRED + count);
			}
			if (character != 'w' && character != '-' && count != NUM_ROWS) {
				errorMsgs.add(String.format(THREE_SQUARES_PER_CHARACTER, character, count));
			}
		}
		return errorMsgs;
	}

	@VisibleForTesting
	static boolean isSolution(char[][] stateArray) {
		for (int colNum = 0; colNum < stateArray[0].length; colNum++) {
			Set<Character> uniqueColSquares = new HashSet<>();
			for (int rowNum = 0; rowNum < stateArray.length; rowNum++) {
				char square = stateArray[rowNum][colNum];
				if (square == '-') {
					square = 'w'; // Treat blank as white when matching
				}
				uniqueColSquares.add(square);
			}
			if (uniqueColSquares.size() != 1) {
				return false;
			}
		}
		return true;
	}

	/*
	 * Heuristic functions
	 */

	private class AStarComparator implements Comparator<State> {

		@Override
		public int compare(State state1, State state2) {
			List<Move> path1 = state1.getPathToState();
			List<Move> path2 = state2.getPathToState();
			return Float.compare(path1.size() + stateHeuristics.get(state1),
					path2.size() + stateHeuristics.get(state2));
		}

	}
	
	private static float computeHeuristic(State state) {
		char[][] stateArray = state.getStateArray();
		float heuristicVal = 0;
		for (char[] row : stateArray) {
			Set<Character> rowCharSet = new HashSet<>(Chars.asList(row));
			heuristicVal += (NUM_COLS - rowCharSet.size()) * 3;
		}
		Map<Character, List<Integer>> colorComparisonColumn = new HashMap<>();
		for (int colNum = 0; colNum < stateArray[0].length; colNum++) {
			char square = stateArray[1][colNum];
			if (square == '-') { // We treat blank tile as white when matching
				square = 'w';
			}
			if (!colorComparisonColumn.containsKey(square)) {
				colorComparisonColumn.put(square, new ArrayList<Integer>());
			}
			colorComparisonColumn.get(square).add(colNum);
		}
		for (int rowNum = 0; rowNum < stateArray.length; rowNum += 2) {
			Set<Integer> requiredRotations = new HashSet<>();
			for (int colNum = 0; colNum < stateArray[0].length; colNum++) {
				char square = stateArray[rowNum][colNum];
				if (square == '-') { // We treat blank tile as white when matching
					square = 'w';
				}
				if (colorComparisonColumn.containsKey(square)) {
					int requiredRotation = Integer.MAX_VALUE;
					for (int compColumn : colorComparisonColumn.get(square)) {
						int rotation = getRotation(colNum, compColumn);
						requiredRotation = Math.abs(rotation) < Math.abs(requiredRotation) ? rotation
								: requiredRotation;
					}
					if (requiredRotation != 0) {
						requiredRotations.add(requiredRotation);
					}
				}
			}
			heuristicVal += getRotationsHeuristic(requiredRotations);
		}
		return heuristicVal;
	}

	@VisibleForTesting
	static int getRotation(int colNum, int compColumn) {
		int rightRotation = Math.floorMod(compColumn - colNum, NUM_COLS);
		int leftRotation = rightRotation - NUM_COLS;
		return rightRotation <= Math.abs(leftRotation) ? rightRotation : leftRotation;
	}

	private static class PosThenNeg implements Comparator<Integer> {

		@Override
		public int compare(Integer rot1, Integer rot2) {
			if (rot1 > 0) {
				return rot2 < 0 ? -1 : 0;
			} else {
				return rot2 < 0 ? 0 : 1;
			}
		}

	}

	private static double getRotationsHeuristic(Set<Integer> requiredRotations) {
		if (requiredRotations.isEmpty()) {
			return 0;
		}
		List<Integer> rotationsList = new ArrayList<>(requiredRotations);
		Collections.sort(rotationsList, new PosThenNeg().thenComparing(Comparator.naturalOrder()));
		List<Integer> posRotations = rotationsList.stream().filter(n -> n > 0).collect(Collectors.toList());
		List<Integer> negRotations = rotationsList.stream().filter(n -> n < 0).collect(Collectors.toList());
		if (!posRotations.isEmpty() && !negRotations.isEmpty()) {
			if (posRotations.get(posRotations.size() - 1) == 1) {
				return 2 - negRotations.get(0) * 2.5;
			}
			if (negRotations.get(0) == -1) {
				return 2 + posRotations.get(posRotations.size() - 1) * 2.5;
			}
		}
		int counterClockwise = Math.floorMod(rotationsList.get(rotationsList.size() - 1), NUM_COLS);
		int clockwise = Math.floorMod(NUM_COLS - rotationsList.get(0), NUM_COLS);
		return Math.min(counterClockwise, clockwise) * 2.5;
	}

	/*
	 * New state generator functions
	 */

	private static List<State> getSuccessors(State state) {
		List<State> successors = new ArrayList<>();
		char[][] stateArray = state.getStateArray();
		List<Move> pathToState = state.getPathToState();
		int queueInsertNum = state.getQueueInsertNum();

		char[][] stateArrayTL = copyStateArray(stateArray);
		rotateLeft(stateArrayTL[0]);
		RotateMove moveTL = new RotateMove(MoveType.TL, 1);
		State stateTL = new State(stateArrayTL, addMoveToPath(pathToState, moveTL), queueInsertNum + 1);
		successors.add(stateTL);

		char[][] stateArrayTR = copyStateArray(stateArray);
		rotateRight(stateArrayTR[0]);
		RotateMove moveTR = new RotateMove(MoveType.TR, 1);
		State stateTR = new State(stateArrayTR, addMoveToPath(pathToState, moveTR), queueInsertNum + 2);
		successors.add(stateTR);

		char[][] stateArrayBL = copyStateArray(stateArray);
		rotateLeft(stateArrayBL[2]);
		RotateMove moveBL = new RotateMove(MoveType.BL, 1);
		State stateBL = new State(stateArrayBL, addMoveToPath(pathToState, moveBL), queueInsertNum + 3);
		successors.add(stateBL);

		char[][] stateArrayBR = copyStateArray(stateArray);
		rotateRight(stateArrayBR[2]);
		RotateMove moveBR = new RotateMove(MoveType.BR, 1);
		State stateBR = new State(stateArrayBR, addMoveToPath(pathToState, moveBR), queueInsertNum + 4);
		successors.add(stateBR);

		int[] blankPosition = getBlankPosition(state.getStateArray());
		int blankRow = blankPosition[0];
		int blankCol = blankPosition[1];
		if (blankRow == 0) {
			char[][] stateArrayMU = copyStateArray(stateArray);
			slide(stateArrayMU, blankCol, 0, 1);
			SlideMove moveMU = new SlideMove(MoveType.MU);
			State stateMU = new State(stateArrayMU, addMoveToPath(pathToState, moveMU), queueInsertNum + 5);
			successors.add(stateMU);
		} else if (blankRow == 1) {
			char[][] stateArrayTD = copyStateArray(stateArray);
			slide(stateArrayTD, blankCol, 1, 0);
			SlideMove moveTD = new SlideMove(MoveType.TD);
			State stateTD = new State(stateArrayTD, addMoveToPath(pathToState, moveTD), queueInsertNum + 5);
			successors.add(stateTD);

			char[][] stateArrayBU = copyStateArray(stateArray);
			slide(stateArrayBU, blankCol, 1, 2);
			SlideMove moveBU = new SlideMove(MoveType.BU);
			State stateBU = new State(stateArrayBU, addMoveToPath(pathToState, moveBU), queueInsertNum + 6);
			successors.add(stateBU);
		} else if (blankRow == 2) {
			char[][] stateArrayMD = copyStateArray(stateArray);
			slide(stateArrayMD, blankCol, 2, 1);
			SlideMove moveMD = new SlideMove(MoveType.MD);
			State stateMD = new State(stateArrayMD, addMoveToPath(pathToState, moveMD), queueInsertNum + 5);
			successors.add(stateMD);
		}
		return successors;
	}

	private static int[] getBlankPosition(char[][] stateArray) {
		for (int rowNum = 0; rowNum < stateArray.length; rowNum++) {
			int blankCol = Chars.indexOf(stateArray[rowNum], '-');
			if (blankCol != -1) {
				return new int[] { rowNum, blankCol };
			}
		}
		throw new IllegalArgumentException("Cannot find blank square in state");
	}

	private static char[][] copyStateArray(char[][] stateArray) {
		char[][] stateArrayCopy = new char[NUM_ROWS][NUM_COLS];
		for (int rowNum = 0; rowNum < stateArray.length; rowNum++) {
			stateArrayCopy[rowNum] = Arrays.copyOf(stateArray[rowNum], stateArray[rowNum].length);
		}
		return stateArrayCopy;
	}

	@VisibleForTesting
	public static void rotateLeft(char[] row) {
		char tempChar = row[0];
		System.arraycopy(row, 1, row, 0, row.length - 1);
		row[row.length - 1] = tempChar;
	}

	private static void rotateRight(char[] row) {
		char tempChar = row[row.length - 1];
		System.arraycopy(row, 0, row, 1, row.length - 1);
		row[0] = tempChar;
	}

	private static void slide(char[][] stateArray, int blankCol, int blankRow, int nonBlankRow) {
		stateArray[blankRow][blankCol] = stateArray[nonBlankRow][blankCol];
		stateArray[nonBlankRow][blankCol] = '-';
	}

	/*
	 * Incorporates the new move to the path. Handles redundant rotations.
	 * Function assumes newMove rotationAmount is exactly 1.
	 */

	@VisibleForTesting
	static List<Move> addMoveToPath(List<Move> path, Move newMove) {
		List<Move> newPath = new ArrayList<>(path);
		if (path.isEmpty() || (!(newMove instanceof RotateMove))) {
			newPath.add(newMove);
			return newPath;
		}
		Move lastMove = newPath.get(newPath.size() - 1);
		if (!(lastMove instanceof RotateMove)) {
			newPath.add(newMove);
			return newPath;
		}
		RotateMove lastRotateMove = (RotateMove) lastMove;
		RotateMove secondLastRotateMove = null;
		if (newPath.size() > 1 && (newPath.get(newPath.size() - 2) instanceof RotateMove)) {
			secondLastRotateMove = (RotateMove) newPath.get(newPath.size() - 2);
		}
		if (lastMove.getMoveType() == newMove.getMoveType()) {
			handleRedundantRotations(newPath, newPath.size() - 1, lastRotateMove.getRotateAmount(), newMove.getMoveType());
		} else if (secondLastRotateMove != null && secondLastRotateMove.getMoveType() == newMove.getMoveType()) {
			handleRedundantRotations(newPath, newPath.size() - 2, secondLastRotateMove.getRotateAmount(), newMove.getMoveType());
		} else {
			newPath.add(newMove);
		}
		return newPath;
	}
	
	private static void handleRedundantRotations(List<Move> path, int currentMoveIndex, int currentRotateAmount, MoveType newMoveType) {
		if (currentRotateAmount + 1 == NUM_COLS) {
			path.remove(currentMoveIndex);
		} else {
			RotateMove combinedMove = new RotateMove(newMoveType, (currentRotateAmount + 1) % NUM_COLS);
			path.set(currentMoveIndex, combinedMove);
		}
	}

	/*
	 * Post-processing. This accounts for a flaw in my algorithm where the cost
	 * function (path length + heuristic) for each state misleads it into looking at
	 * a longer rotation before a shorter one in the opposite direction.
	 */
	private static void shortenRotations(List<Move> solutionPath) {
		for (int i = 0; i < solutionPath.size(); i++) {
			Move move = solutionPath.get(i);
			if (!(move instanceof RotateMove)) {
				continue;
			}
			RotateMove rotateMove = (RotateMove) move;
			if (rotateMove.getRotateAmount() > NUM_COLS / 2) {
				MoveType oppositeMoveType = MoveType.valueOf(rotateMove.getMoveType().getOppositeMove());
				RotateMove oppositeMove = new RotateMove(oppositeMoveType, NUM_COLS - rotateMove.getRotateAmount());
				solutionPath.set(i, oppositeMove);
			}
		}
	}
}
