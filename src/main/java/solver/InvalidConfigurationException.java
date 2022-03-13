package solver;

import java.util.List;

@SuppressWarnings("serial")
public class InvalidConfigurationException extends RuntimeException {
	
	List<String> errors;
	InvalidConfigurationException(List<String> errors) {
		super("Invalid input configuration" + errors);
		this.errors = errors;
	}
	
	public List<String> getErrors() {
		return this.errors;
	}
}
