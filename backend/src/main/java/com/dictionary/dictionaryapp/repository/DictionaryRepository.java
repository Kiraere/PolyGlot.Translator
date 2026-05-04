//package com.dictionary.dictionaryapp.repository;
//
//import com.dictionary.dictionaryapp.model.WordEntry;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface DictionaryRepository extends JpaRepository<WordEntry, Long> {
//
//    Optional<WordEntry> findByWordIgnoreCase(String word);
//
//    @Query("SELECT w FROM WordEntry w JOIN w.translations t WHERE LOWER(t) = LOWER(:translation)")
//    List<WordEntry> findByTranslationIgnoreCase(@Param("translation") String translation);
//
//    @Query("SELECT w FROM WordEntry w JOIN w.synonyms s WHERE LOWER(s) = LOWER(:synonym)")
//    List<WordEntry> findBySynonymIgnoreCase(@Param("synonym") String synonym);
//
//    @Query("SELECT DISTINCT w.word FROM WordEntry w WHERE LOWER(w.word) LIKE LOWER(CONCAT(:query, '%'))")
//    List<String> findWordsStartingWith(@Param("query") String query);
//
//    @Query("SELECT DISTINCT t FROM WordEntry w JOIN w.translations t WHERE LOWER(t) LIKE LOWER(CONCAT(:query, '%'))")
//    List<String> findTranslationsStartingWith(@Param("query") String query);
//}

package com.dictionary.dictionaryapp.repository;

import com.dictionary.dictionaryapp.model.WordEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DictionaryRepository extends JpaRepository<WordEntry, Long> {

    Optional<WordEntry> findByWordIgnoreCase(String word);

    // Шукаємо в головному англійському слові АБО в англійських синонімах
    @Query("SELECT DISTINCT w FROM WordEntry w LEFT JOIN w.synonyms s WHERE LOWER(w.word) = LOWER(:word) OR LOWER(s) = LOWER(:word)")
    List<WordEntry> findByWordOrSynonym(@Param("word") String word);

    // Шукаємо в українських перекладах АБО в українських синонімах
    @Query("SELECT DISTINCT w FROM WordEntry w LEFT JOIN w.translations t LEFT JOIN w.uaSynonyms u WHERE LOWER(t) = LOWER(:word) OR LOWER(u) = LOWER(:word)")
    List<WordEntry> findByTranslationOrUaSynonym(@Param("word") String word);

    // --- Запити для випадаючої підказки (автозаповнення) ---
    @Query("SELECT w.word FROM WordEntry w WHERE LOWER(w.word) LIKE LOWER(CONCAT(:query, '%'))")
    List<String> findWordsStartingWith(@Param("query") String query);

    @Query("SELECT t FROM WordEntry w JOIN w.translations t WHERE LOWER(t) LIKE LOWER(CONCAT(:query, '%'))")
    List<String> findTranslationsStartingWith(@Param("query") String query);

    @Query("SELECT s FROM WordEntry w JOIN w.synonyms s WHERE LOWER(s) LIKE LOWER(CONCAT(:query, '%'))")
    List<String> findSynonymsStartingWith(@Param("query") String query);

    @Query("SELECT u FROM WordEntry w JOIN w.uaSynonyms u WHERE LOWER(u) LIKE LOWER(CONCAT(:query, '%'))")
    List<String> findUaSynonymsStartingWith(@Param("query") String query);
}