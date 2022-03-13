package solver;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.primitives.Chars;

import moves.Move;
import moves.MoveType;
import moves.RotateMove;
import moves.SlideMove;

public class SolverTest {
	
	/*
	 * This tests to make sure the solution moves don't differ for the same configuration
	 * based on which column is first.
	 */
	@Test
	void solveRubiksDecoder_orientationIrrelevant() {
		char[][] inputArray = {
				{'-', 'y', 'y', 'g', 'w', 'g'},
				{'r', 'w', 'y', 'b', 'b', 'b'},
				{'r', 'o', 'g', 'o', 'o', 'r'}
		};
		List<Move> expectedMoves = new Solver().solveRubiksDecoder(inputArray);
		int i = 0;
		while(i < 5) {
			/*
			 * Rotates whole decoder left by 1
			 */
			for (char[] row : inputArray) {
				Solver.rotateLeft(row);
			}
			List<Move> moves = new Solver().solveRubiksDecoder(inputArray);
			assertEquals(expectedMoves, moves);
			i++;
		}
	}
	
	@Test
	void validateInput_wrongNumColumns() {
		char[][] inputArray = {
				{'-', 'y', 'y', 'g', 'w', 'g', 'r'},
				{'r', 'w', 'y', 'b', 'b', 'b', 'y'},
				{'r', 'o', 'g', 'o', 'o', 'r', 'y'}
		};
		List<String> result = Solver.validateInput(inputArray);
		assertThat(result, containsInAnyOrder(String.format(Solver.WRONG_ROW_LENGTH, 1, Solver.NUM_COLS),
				String.format(Solver.WRONG_ROW_LENGTH, 2, Solver.NUM_COLS),
				String.format(Solver.WRONG_ROW_LENGTH, 3, Solver.NUM_COLS),
				String.format(Solver.THREE_SQUARES_PER_CHARACTER, 'r', 4),
				String.format(Solver.THREE_SQUARES_PER_CHARACTER, 'y', 5)));
	}
	
	@Test
	void validateInput_wrongColumnLength() {
		char[][] inputArray = {
				{'-', 'y', 'y', 'g', 'w', 'g'},
				{'r', 'w', 'y', 'b', 'b', 'b'},
				{'r', 'o', 'g', 'o', 'o', 'r'},
				{'y'}
		};
		List<String> result = Solver.validateInput(inputArray);
		assertThat(result, containsInAnyOrder(String.format(Solver.WRONG_NUM_ROWS, Solver.NUM_ROWS),
				String.format(Solver.THREE_SQUARES_PER_CHARACTER, 'y', 4)));
	}
	
	@Test
	void validateInput_invalidCharacter() {
		char[][] inputArray = {
				{'-', 'y', 'y', 'g', 'w', 'g'},
				{'r', 'w', 'y', 'b', 'b', 'b'},
				{'r', 'o', 's', 'o', 'o', 'r'}
		};
		List<String> result = Solver.validateInput(inputArray);
		assertThat(result, containsInAnyOrder(String.format(Solver.INVALID_CHARACTER, 's'),
				String.format(Solver.THREE_SQUARES_PER_CHARACTER, 'g', 2)));
	}
	
	@Test
	void validateInput_notOneBlank() {
		char[][] inputArray = {
				{'-', 'y', 'y', 'g', 'w', 'g'},
				{'r', 'w', 'y', 'b', 'b', 'b'},
				{'r', 'o', 'g', 'o', 'o', '-'}
		};
		List<String> result = Solver.validateInput(inputArray);
		assertThat(result, containsInAnyOrder(Solver.ONE_BLANK_SQUARE_ONLY + 2,
				String.format(Solver.THREE_SQUARES_PER_CHARACTER, 'r', 2)));
	}
	
	@Test
	void validateInput_notTwoWhite() {
		char[][] inputArray = {
				{'-', 'y', 'y', 'g', 'w', 'g'},
				{'r', 'w', 'y', 'b', 'b', 'b'},
				{'r', 'o', 'g', 'o', 'o', 'w'}
		};
		List<String> result = Solver.validateInput(inputArray);
		assertThat(result, containsInAnyOrder(Solver.TWO_WHITE_SQUARES_REQUIRED + 3,
				String.format(Solver.THREE_SQUARES_PER_CHARACTER, 'r', 2)));
	}
	
	@Test
	void validateInput_notThreeCharacter() {
		char[][] inputArray = {
				{'-', 'b', 'b', 'g', 'w', 'g'},
				{'r', 'w', 'b', 'b', 'b', 'b'},
				{'r', 'o', 'g', 'o', 'o', 'r'}
		};
		List<String> result = Solver.validateInput(inputArray);
		assertThat(result, containsInAnyOrder(String.format(Solver.THREE_SQUARES_PER_CHARACTER, 'b', 6),
				String.format(Solver.THREE_SQUARES_PER_CHARACTER, 'y', 0)));
	}
	
	@Test
	void validateInput_valid() {
		char[][] inputArray = {
				{'-', 'y', 'y', 'g', 'w', 'g'},
				{'r', 'w', 'y', 'b', 'b', 'b'},
				{'r', 'o', 'g', 'o', 'o', 'r'}
		};
		List<String> result = Solver.validateInput(inputArray);
		List<String> expected = Collections.emptyList();
		assertEquals(result, expected);
	}
	
	@Test
	void getRotation_1_1_0() {
		int result = Solver.getRotation(0, 0);
		assertEquals(0, result);
	}
	
	@Test
	void getRotation_1_3_2() {
		int result = Solver.getRotation(1, 3);
		assertEquals(2, result);
	}
	
	@Test
	void getRotation_3_1_neg2() {
		int result = Solver.getRotation(3, 1);
		assertEquals(-2, result);
	}
	
	@Test
	void getRotation_1_4_3() {
		int result = Solver.getRotation(1, 4);
		assertEquals(3, result);
	}
	
	@Test
	void getRotation_4_1_3() {
		int result = Solver.getRotation(4, 1);
		assertEquals(3, result);
	}
	
	@Test
	void getRotation_1_5_neg2() {
		int result = Solver.getRotation(1, 5);
		assertEquals(-2, result);
	}
	
	@Test
	void getRotation_5_1_2() {
		int result = Solver.getRotation(5, 1);
		assertEquals(2, result);
	}
	
	@Test
	void isSolution_oneWhiteInCol() {
		char[][] stateArray = {
				{'-', 'r', 'r', 'y', 'w', 'o'},
				{'y', 'y', 'g', 'g', 'b', 'o'},
				{'b', 'w', 'o', 'g', 'b', 'r'}
		};
		boolean result = Solver.isSolution(stateArray);
		assertEquals(false, result);
	}
	
	@Test
	void isSolution_twoWhiteInCol() {
		char[][] stateArray = {
				{'-', 'r', 'r', 'y', 'w', 'o'},
				{'y', 'y', 'g', 'g', 'b', 'o'},
				{'w', 'b', 'o', 'g', 'b', 'r'}
		};
		boolean result = Solver.isSolution(stateArray);
		assertEquals(false, result);
	}
	
	@Test
	void isSolution_valid() {
		char[][] stateArray = {
				{'-', 'r', 'o', 'g', 'b', 'y'},
				{'w', 'r', 'o', 'g', 'b', 'y'},
				{'w', 'r', 'o', 'g', 'b', 'y'}
		};
		boolean result = Solver.isSolution(stateArray);
		assertEquals(true, result);
	}
	
	@Test
	void addMoveToPath_empty() {
		List<Move> path = new ArrayList<>();
		List<Move> result = Solver.addMoveToPath(path, new SlideMove(MoveType.BU));
		List<Move> expected = Arrays.asList(new SlideMove(MoveType.BU));
		assertEquals(expected, result);
	}
	
	@Test
	void addMoveToPath_onlySlides() {
		List<Move> path = Arrays.asList(new SlideMove(MoveType.MU));
		List<Move> result = Solver.addMoveToPath(path, new SlideMove(MoveType.BU));
		List<Move> expected = Arrays.asList(new SlideMove(MoveType.MU), new SlideMove(MoveType.BU));
		assertEquals(expected, result);
	}
	
	@Test
	void addMoveToPath_sameRotations() {
		List<Move> path = Arrays.asList(new RotateMove(MoveType.TR, 2));
		List<Move> result = Solver.addMoveToPath(path, new RotateMove(MoveType.TR, 1));
		List<Move> expected = Arrays.asList(new RotateMove(MoveType.TR, 3));
		assertEquals(expected, result);
	}
	
	@Test
	void addMoveToPath_differentRotations() {
		List<Move> path = Arrays.asList(new RotateMove(MoveType.TR, 1));
		List<Move> result = Solver.addMoveToPath(path, new RotateMove(MoveType.BL, 1));
		List<Move> expected = Arrays.asList(new RotateMove(MoveType.TR, 1), new RotateMove(MoveType.BL, 1));
		assertEquals(expected, result);
	}
	
	@Test
	void addMoveToPath_slideAndRotation() {
		List<Move> path = Arrays.asList(new RotateMove(MoveType.TR, 1));
		List<Move> result = Solver.addMoveToPath(path, new SlideMove(MoveType.MU));
		List<Move> expected = Arrays.asList(new RotateMove(MoveType.TR, 1), new SlideMove(MoveType.MU));
		assertEquals(expected, result);
	}
	
	@Test
	void addMoveToPath_threeRotations() {
		List<Move> path = Arrays.asList(new RotateMove(MoveType.TR, 2), new RotateMove(MoveType.BR, 1));
		List<Move> result = Solver.addMoveToPath(path, new RotateMove(MoveType.TR, 1));
		List<Move> expected = Arrays.asList(new RotateMove(MoveType.TR, 3), new RotateMove(MoveType.BR, 1));
		assertEquals(expected, result);
	}
	
	@Test
	void addMoveToPath_rotateSlideRotate() {
		List<Move> path = Arrays.asList(new RotateMove(MoveType.TR, 2), new SlideMove(MoveType.MU));
		List<Move> result = Solver.addMoveToPath(path, new RotateMove(MoveType.TR, 1));
		List<Move> expected = Arrays.asList(new RotateMove(MoveType.TR, 2), new SlideMove(MoveType.MU), new RotateMove(MoveType.TR, 1));
		assertEquals(expected, result);
	}
	
	public static char[][] generateRandomInput() {
		List<Character> charList = new ArrayList<>(Solver.CHAR_LIST); // Modifiable copy
		Collections.shuffle(charList);
		char[][] inputArray = new char[Solver.NUM_ROWS][Solver.NUM_COLS];
		for (int row = 0; row < Solver.NUM_ROWS; row++) {
			inputArray[row] = Chars.toArray(charList.subList(6 * row, 6 * (row + 1)));
		}
		return inputArray;
	}
	
	public static void averagePerformanceTest(int numTests) {
		long totalTime = 0;
		int totalMoves = 0;
		for(int i = 0; i < numTests; i++) {
			System.out.println("Test " + (i + 1) + "/" + numTests);
			char[][] inputArray = generateRandomInput();
			long startTime = System.nanoTime();
			List<Move> solutionPath = new Solver().solveRubiksDecoder(inputArray);
			long endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			int numMoves = solutionPath.size();
			totalTime += timeElapsed;
			totalMoves += numMoves;
		}
		double averageSeconds = totalTime / (double) numTests / 1e9;
		float averageMoves = totalMoves / (float) numTests;
		System.out.println("Average time = " + averageSeconds + " seconds");
		System.out.println("Average moves = " + averageMoves);
	}
	
	public static void solveAndDisplay(char[][] startStateArray) {
		long startTime = System.nanoTime();
		List<Move> solutionPath = new Solver().solveRubiksDecoder(startStateArray);
		long endTime = System.nanoTime();
		double secondsElapsed = (endTime - startTime) / 1e9;
		if (solutionPath.isEmpty()) {
			System.out.println("Already solved");
		}
		else {
			System.out.println(String.format("Time taken: %f seconds", secondsElapsed));
			System.out.println("# of moves: " + solutionPath.size());
			System.out.println();
			for (int i = 0; i < solutionPath.size(); i++) {
				Move step = solutionPath.get(i);
				System.out.println((i + 1) + ". " + step.getMoveDescription());
				System.out.println();
			}
			System.out.println("Puzzle Solved!");
		}
	}
	
	public static void main(String[] args) {
		char[][] inputArray = {
				{'-', 'y', 'y', 'g', 'w', 'g'},
				{'r', 'w', 'y', 'b', 'b', 'b'},
				{'r', 'o', 'g', 'o', 'o', 'r'}
		};
		solveAndDisplay(inputArray);
		averagePerformanceTest(20);
	}
}
