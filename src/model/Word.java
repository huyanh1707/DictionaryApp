package model;

public class Word implements Comparable<Word> {
    String word;
    Meaning meaning;
    String phonetics;

    public Word() {
        this.meaning = new Meaning();
    }

    public Word(String word) {
        this.meaning = new Meaning();
        this.word = word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning.toString();
    }

    public String getPhonetics() {
        return phonetics;
    }

    public void setPhonetics(String phonetics) {
        this.phonetics = phonetics;
    }

    public void addToMeaning(String s) {
        this.meaning.Add(s);
    }

    @Override
    public int compareTo(Word word) {
        return this.getWord().compareTo(word.getWord());
    }

    @Override
    public String toString() {
        return this.word;
    }

}
