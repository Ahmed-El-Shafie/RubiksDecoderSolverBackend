package solver;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import moves.Move;

@SpringBootApplication
@RestController
public class SolverAPI {
	
	private static final String corsOrigin = "www.rubiksdecodersolver.com";
	
	public static void main(String[] args) {
		SpringApplication.run(SolverAPI.class, args);
	}
	
	@CrossOrigin(origins = corsOrigin)
	@GetMapping("/solve")
	public List<Move> solveRubiksDecoder(@RequestParam(value = "rows") char[][] rows) {
		return new Solver().solveRubiksDecoder(rows);
	}
	
	@CrossOrigin(origins = corsOrigin)
	@GetMapping("/ping")
	public String getRandomConfiguration() {
		return "Hello!";
	}
}
