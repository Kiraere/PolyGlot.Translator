//package com.dictionary.dictionaryapp.service;
//
//import com.dictionary.dictionaryapp.model.WordEntry;
//import com.dictionary.dictionaryapp.repository.DictionaryRepository;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class DictionaryService {
//
//    private final DictionaryRepository repository;
//
//    public DictionaryService(DictionaryRepository repository) {
//        this.repository = repository;
//    }
//
//    @Transactional
//    @CacheEvict(value = {"translations", "search"}, allEntries = true)
//    public void loadEntries(List<WordEntry> entries) {
//        for (WordEntry entry : entries) {
//            Optional<WordEntry> existing = repository.findByWordIgnoreCase(entry.getWord());
//            if (existing.isPresent()) {
//                WordEntry current = existing.get();
//                current.setTranslations(entry.getTranslations());
//                current.setSynonyms(entry.getSynonyms());
//                repository.save(current);
//            } else {
//                repository.save(entry);
//            }
//        }
//    }
//
//    @Cacheable(value = "translations", key = "#word + #direction")
//    public List<WordEntry> translate(String word, String direction) {
//        if ("en-ua".equalsIgnoreCase(direction)) {
//            return repository.findByWordIgnoreCase(word)
//                    .map(List::of)
//                    .orElse(Collections.emptyList());
//        } else if ("ua-en".equalsIgnoreCase(direction)) {
//            return repository.findByTranslationIgnoreCase(word);
//        }
//        return Collections.emptyList();
//    }
//
//    @Cacheable(value = "search", key = "#query")
//    public List<String> search(String query) {
//        List<String> results = repository.findWordsStartingWith(query);
//        results.addAll(repository.findTranslationsStartingWith(query));
//        return results.stream().distinct().limit(10).collect(Collectors.toList());
//    }
//}

package com.dictionary.dictionaryapp.service;

import com.dictionary.dictionaryapp.model.WordEntry;
import com.dictionary.dictionaryapp.repository.DictionaryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DictionaryService {

    private final DictionaryRepository repository;

    public DictionaryService(DictionaryRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @CacheEvict(value = {"translations", "search"}, allEntries = true)
    public void loadEntries(List<WordEntry> entries) {
        for (WordEntry entry : entries) {
            Optional<WordEntry> existing = repository.findByWordIgnoreCase(entry.getWord());
            if (existing.isPresent()) {
                WordEntry current = existing.get();
                // Тепер оновлюються ВСІ поля бази даних
                current.setTranslations(entry.getTranslations());
                current.setSynonyms(entry.getSynonyms());
                current.setUaSynonyms(entry.getUaSynonyms());
                current.setExamples(entry.getExamples());
                repository.save(current);
            } else {
                repository.save(entry);
            }
        }
    }

    @Cacheable(value = "translations", key = "#word + #direction")
    public List<WordEntry> translate(String word, String direction) {
        if ("en-ua".equalsIgnoreCase(direction)) {
            // Шукає серед англійських слів та синонімів
            return repository.findByWordOrSynonym(word.trim());
        } else if ("ua-en".equalsIgnoreCase(direction)) {
            // Шукає серед українських перекладів та синонімів
            return repository.findByTranslationOrUaSynonym(word.trim());
        }
        return Collections.emptyList();
    }

    @Cacheable(value = "search", key = "#query")
    public List<String> search(String query) {
        // Збирає підказки звідусіль
        List<String> results = repository.findWordsStartingWith(query);
        results.addAll(repository.findTranslationsStartingWith(query));
        results.addAll(repository.findSynonymsStartingWith(query));
        results.addAll(repository.findUaSynonymsStartingWith(query));

        return results.stream().distinct().limit(10).collect(Collectors.toList());
    }
}