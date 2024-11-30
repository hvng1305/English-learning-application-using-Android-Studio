package com.example.appeng;

import java.util.List;

public class WordResult {
    private String word;
    private String phonetic;
    private List<Meaning> meanings;

    // Constructor
    public WordResult(String word, String phonetic, List<Meaning> meanings) {
        this.word = word;
        this.phonetic = phonetic;
        this.meanings = meanings;
    }

    // Getters and Setters
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getPhonetic() {
        return phonetic;
    }

    public void setPhonetic(String phonetic) {
        this.phonetic = phonetic;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<Meaning> meanings) {
        this.meanings = meanings;
    }
}

