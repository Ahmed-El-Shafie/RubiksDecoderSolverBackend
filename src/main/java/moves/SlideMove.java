package moves;

public class SlideMove implements Move {

	private final MoveType moveType;
	
	public SlideMove(MoveType moveType) {
		if (!MoveType.slideMoves.contains(moveType)) {
			throw new IllegalArgumentException("Move type must be slide");
		}
		this.moveType = moveType;
	}
	
	@Override
	public MoveType getMoveType() {
		return moveType;
	}
	
	@Override
	public String getMoveDescription() {
		return moveType.getMoveString();
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SlideMove)) {
			return false;
		}
		SlideMove otherSlideMove = (SlideMove) other;
		return moveType == otherSlideMove.getMoveType();
	}
	
	@Override
	public int hashCode() {
		return moveType.hashCode();
	}

}
