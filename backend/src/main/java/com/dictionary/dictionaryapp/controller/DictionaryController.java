package com.dictionary.dictionaryapp.controller;

import com.dictionary.dictionaryapp.model.WordEntry;
import com.dictionary.dictionaryapp.service.DictionaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class DictionaryController {

    private final DictionaryService service;

    public DictionaryController(DictionaryService service) {
        this.service = service;
    }

    @PostMapping("/load")
    public ResponseEntity<String> load(@RequestBody List<WordEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty database provided");
        }
        service.loadEntries(entries);
        return ResponseEntity.ok("Database loaded successfully with " + entries.size() + " entries");
    }

    @GetMapping("/translate")
    public ResponseEntity<?> translate(@RequestParam String word, @RequestParam(defaultValue = "en-ua") String direction) {
        if (word == null || word.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Word is required");
        }
        List<WordEntry> results = service.translate(word.trim(), direction);
        if (results.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(results);
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> search(@RequestParam String query) {
        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(service.search(query.trim()));
    }
}
