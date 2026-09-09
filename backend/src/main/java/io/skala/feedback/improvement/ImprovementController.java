package io.skala.feedback.improvement;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('ADMIN')")
public class ImprovementController {

	private final ImprovementService improvementService;

	public ImprovementController(ImprovementService improvementService) {
		this.improvementService = improvementService;
	}

	@GetMapping("/api/improvements")
	public List<ImprovementDto> listAll() {
		return improvementService.listAll();
	}

	@PostMapping("/api/improvements")
	@ResponseStatus(HttpStatus.CREATED)
	public ImprovementDto create(@Valid @RequestBody NewImprovementRequest request) {
		return improvementService.create(request);
	}

	@DeleteMapping("/api/improvements/{id}")
	public void delete(@PathVariable Long id) {
		improvementService.delete(id);
	}
}
