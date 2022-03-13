package solver;

import java.util.Arrays;
import java.util.List;

import moves.Move;

public class State {

	private final char[][] stateArray;
	private final List<Move> pathToState;
	private final int queueInsertNum; /*
										 * Used as a tie breaker when 2 state heuristic values are equal to help
										 * ensure that SolverTest.solveRubiksDecoder_orientationIrrelevant succeeds
										 */

	public State(char[][] stateArray, List<Move> pathToState, int queueInsertNum) {
		this.stateArray = stateArray;
		this.pathToState = pathToState;
		this.queueInsertNum = queueInsertNum;
	}

	public char[][] getStateArray() {
		return stateArray;
	}

	public List<Move> getPathToState() {
		return pathToState;
	}

	public int getQueueInsertNum() {
		return queueInsertNum;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof State)) {
			return false;
		}
		State otherState = (State) obj;
		char[][] otherStateArray = otherState.getStateArray();
		return Arrays.deepEquals(stateArray, otherStateArray);
	}

	@Override
	public int hashCode() {
		return Arrays.deepHashCode(stateArray);
	}
}
