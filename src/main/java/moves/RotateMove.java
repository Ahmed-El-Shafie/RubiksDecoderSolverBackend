package moves;

public class RotateMove implements Move {

	private final MoveType moveType;
	private final int rotateAmount;
	
	public RotateMove(MoveType moveType, int rotateAmount) {
		if (!MoveType.rotateMoves.contains(moveType)) {
			throw new IllegalArgumentException("Move type must be rotation");
		}
		this.moveType = moveType;
		if (rotateAmount < 1) {
			throw new IllegalArgumentException("Rotation amount must be at least 1");
		}
		this.rotateAmount = rotateAmount;
	}
	
	@Override
	public MoveType getMoveType() {
		return moveType;
	}

	public int getRotateAmount() {
		return rotateAmount;
	}
	
	@Override
	public String getMoveDescription() {
		return String.format("%s by %d", moveType.getMoveString(), rotateAmount);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RotateMove)) {
			return false;
		}
		RotateMove otherRotateMove = (RotateMove) other;
		return getMoveDescription().equals(otherRotateMove.getMoveDescription());
	}

	@Override
	public int hashCode() {
		return getMoveDescription().hashCode();
	}
}
