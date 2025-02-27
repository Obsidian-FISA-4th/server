package Obsidian.demo.Controller;

import Obsidian.demo.domain.common.TempEntity;
import Obsidian.demo.service.TempService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/temp")
@RequiredArgsConstructor
public class TempController {
    private final TempService tempService;

    @GetMapping
    public ResponseEntity<List<TempEntity>> getAll() {
        return ResponseEntity.ok(tempService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TempEntity> getById(@PathVariable Long id) {
        return tempService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TempEntity> create(@RequestBody TempEntity tempEntity) {
        return ResponseEntity.ok(tempService.create(tempEntity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TempEntity> update(@PathVariable Long id, @RequestBody TempEntity tempEntity) {
        return ResponseEntity.ok(tempService.update(id, tempEntity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tempService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
