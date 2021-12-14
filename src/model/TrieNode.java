package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TrieNode {
    char data;
    LinkedList<TrieNode> children;
    TrieNode parent;
    boolean isEnd;

    public TrieNode(char c) {
        data = c;
        children = new LinkedList<>();
        isEnd = false;
    }

    public TrieNode getChild(char c) {
        if (children != null)
            for (TrieNode eachChild : children)
                if (eachChild.data == c)
                    return eachChild;
        return null;
    }

    protected List<String> getWords() {
        List<String> list = new ArrayList<>();
        if (isEnd) {
            list.add(toString());
        }

        if (children != null) {
            for (TrieNode child : children) {
                if (child != null) {
                    list.addAll(child.getWords());
                }
            }
        }
        return list;
    }

    public String toString() {
        if (parent == null) {
            return "";
        } else {
            return parent + String.valueOf(data);
        }
    }
}
