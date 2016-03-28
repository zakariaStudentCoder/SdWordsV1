package com.sd.translator;

import java.io.Serializable;

/**
 * Created by Admin on 25.02.2016.
 */
public class WordWrapper implements Serializable {

    private static final long serialVersionUID = -111291789422560370L;

    private long Id;
    private String word;
    private String definition;

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public WordWrapper(long id , String word, String definition) {
        this.Id = id;
        setWord(word);
        this.definition = definition;
    }
    public WordWrapper(String word, String definition) {
        setWord(word);
        this.definition = definition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WordWrapper that = (WordWrapper) o;

        return !(word != null ? !word.equals(that.word) : that.word != null);

    }

    @Override
    public int hashCode() {
        int result = word != null ? word.hashCode() : 0;
        result = 31 * result + (definition != null ? definition.hashCode() : 0);
        return result;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        if(word == null) {
            this.word = "";
        }
        else
        {
            this.word = word.toUpperCase();
        }
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
