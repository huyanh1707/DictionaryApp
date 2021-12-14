package model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Dictionary {
    private ArrayList<Word> words;

    public ArrayList<Word> getWords() {
        return words;
    }

    public void loadFromFile(String fileName) {
        try {
            words = new ArrayList<>();
            Path path = Paths.get(fileName);
            List<String> dataList = Files.readAllLines(path);
            ListIterator<String> itr = dataList.listIterator();
            Word word = new Word();
            while (itr.hasNext()) {
                String p = itr.next();

                if (p.startsWith("#")) {
                    word = new Word();
                    String[] part = p.split("/", 2);

                    String s2 = part[0].substring(1).trim();
                    if (s2.startsWith("'") || s2.startsWith("-") || s2.startsWith("(")) {
                        s2 = s2.substring(1);
                    }
                    word.setWord(s2);

                    if (part.length < 2) {
                        word.setPhonetics("");
                    } else
                        word.setPhonetics("/" + part[1]);
                    while (itr.hasNext()) {
                        String p1 = itr.next();

                        if (!p1.startsWith("#")) {
                            word.addToMeaning(p1);
                        } else {
                            words.add(word);
                            itr.previous();
                            break;
                        }
                    }
                }
            }
            words.add(word);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sortWords() {
        Collections.sort(words);
    }

    public int search(String s) {
        Word toSearch = new Word(s.trim());
        return Collections.binarySearch(words, toSearch);
    }

}

