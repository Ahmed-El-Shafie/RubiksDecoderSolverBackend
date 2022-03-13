package solver;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class InvalidConfigurationAdvice {
	@ResponseBody
	@ExceptionHandler(InvalidConfigurationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	List<String> employeeNotFoundHandler(InvalidConfigurationException ex) {
		return ex.getErrors();
	}
}
