package moves;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public enum MoveType {

	TL("Top row, rotate left", "TR"),
	TR("Top row, rotate right", "TL"),
	BL("Bottom row, rotate left", "BR"),
	BR("Bottom row, rotate right", "BL"),
	TD("Top row, slide down", "MU"),
	MD("Middle row, slide down", "BU"),
	MU("Middle row, slide up", "TD"),
	BU("Bottom row, slide up", "MD");
	
	public static final Set<MoveType> slideMoves = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(TD, MD, MU, BU)));
	public static final Set<MoveType> rotateMoves = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(TL, TR, BL, BR)));
	
	private final String moveString;
	private final String oppositeMove;
	
	MoveType(String moveString, String oppositeMove) {
		this.moveString = moveString;
		this.oppositeMove = oppositeMove;
	}

	public String getMoveString() {
		return this.moveString;
	}
	
	public String getOppositeMove() {
		return oppositeMove;
	}
}
