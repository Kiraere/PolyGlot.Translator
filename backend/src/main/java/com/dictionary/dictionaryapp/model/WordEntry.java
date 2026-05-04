//package com.dictionary.dictionaryapp.model;
//
//import jakarta.persistence.*;
//
//import java.util.List;
//
//@Entity
//@Table(name = "word_entries")
//public class WordEntry {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, unique = true)
//    private String word;
//
//    @ElementCollection
//    @CollectionTable(name = "word_translations", joinColumns = @JoinColumn(name = "word_id"))
//    @Column(name = "translation")
//    private List<String> translations;
//
//    @ElementCollection
//    @CollectionTable(name = "word_synonyms", joinColumns = @JoinColumn(name = "word_id"))
//    @Column(name = "synonym")
//    private List<String> synonyms;
//
//    public WordEntry() {
//    }
//
//    public WordEntry(Long id, String word, List<String> translations, List<String> synonyms) {
//        this.id = id;
//        this.word = word;
//        this.translations = translations;
//        this.synonyms = synonyms;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getWord() {
//        return word;
//    }
//
//    public void setWord(String word) {
//        this.word = word;
//    }
//
//    public List<String> getTranslations() {
//        return translations;
//    }
//
//    public void setTranslations(List<String> translations) {
//        this.translations = translations;
//    }
//
//    public List<String> getSynonyms() {
//        return synonyms;
//    }
//
//    public void setSynonyms(List<String> synonyms) {
//        this.synonyms = synonyms;
//    }
//
//    public static WordEntryBuilder builder() {
//        return new WordEntryBuilder();
//    }
//
//    public static class WordEntryBuilder {
//        private Long id;
//        private String word;
//        private List<String> translations;
//        private List<String> synonyms;
//
//        public WordEntryBuilder id(Long id) {
//            this.id = id;
//            return this;
//        }
//
//        public WordEntryBuilder word(String word) {
//            this.word = word;
//            return this;
//        }
//
//        public WordEntryBuilder translations(List<String> translations) {
//            this.translations = translations;
//            return this;
//        }
//
//        public WordEntryBuilder synonyms(List<String> synonyms) {
//            this.synonyms = synonyms;
//            return this;
//        }
//
//        public WordEntry build() {
//            return new WordEntry(id, word, translations, synonyms);
//        }
//    }
//}

package com.dictionary.dictionaryapp.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "word_entries")
public class WordEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String word;

    @ElementCollection
    @CollectionTable(name = "word_translations", joinColumns = @JoinColumn(name = "word_id"))
    @Column(name = "translation")
    private List<String> translations;

    @ElementCollection
    @CollectionTable(name = "word_synonyms", joinColumns = @JoinColumn(name = "word_id"))
    @Column(name = "synonym")
    private List<String> synonyms;

    // ДОДАНО: Українські синоніми
    @ElementCollection
    @CollectionTable(name = "word_ua_synonyms", joinColumns = @JoinColumn(name = "word_id"))
    @Column(name = "ua_synonym")
    private List<String> uaSynonyms;

    @ElementCollection
    @CollectionTable(name = "word_examples", joinColumns = @JoinColumn(name = "word_id"))
    @Column(name = "example")
    private List<String> examples;

    public WordEntry() {
    }

    public WordEntry(Long id, String word, List<String> translations, List<String> synonyms, List<String> uaSynonyms, List<String> examples) {
        this.id = id;
        this.word = word;
        this.translations = translations;
        this.synonyms = synonyms;
        this.uaSynonyms = uaSynonyms;
        this.examples = examples;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public List<String> getTranslations() { return translations; }
    public void setTranslations(List<String> translations) { this.translations = translations; }

    public List<String> getSynonyms() { return synonyms; }
    public void setSynonyms(List<String> synonyms) { this.synonyms = synonyms; }

    public List<String> getUaSynonyms() { return uaSynonyms; }
    public void setUaSynonyms(List<String> uaSynonyms) { this.uaSynonyms = uaSynonyms; }

    public List<String> getExamples() { return examples; }
    public void setExamples(List<String> examples) { this.examples = examples; }

    public static WordEntryBuilder builder() {
        return new WordEntryBuilder();
    }

    public static class WordEntryBuilder {
        private Long id;
        private String word;
        private List<String> translations;
        private List<String> synonyms;
        private List<String> uaSynonyms;
        private List<String> examples;

        public WordEntryBuilder id(Long id) { this.id = id; return this; }
        public WordEntryBuilder word(String word) { this.word = word; return this; }
        public WordEntryBuilder translations(List<String> translations) { this.translations = translations; return this; }
        public WordEntryBuilder synonyms(List<String> synonyms) { this.synonyms = synonyms; return this; }
        public WordEntryBuilder uaSynonyms(List<String> uaSynonyms) { this.uaSynonyms = uaSynonyms; return this; }
        public WordEntryBuilder examples(List<String> examples) { this.examples = examples; return this; }

        public WordEntry build() {
            return new WordEntry(id, word, translations, synonyms, uaSynonyms, examples);
        }
    }
}
