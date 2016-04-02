package com.sd.db;

/**
 * Created by Admin on 31.03.2016.
 */
public class SudokuTableObject {

    private int id;
    private int folderId;
    private int created;
    private int state;
    private int time;
    private int lastPlayed;
    private String data;
    private String puzzleNote;

    public SudokuTableObject(int id, int folderId, int created, int state, int time, int lastPlayed, String data, String puzzleNote) {
        this.id = id;
        this.folderId = folderId;
        this.created = created;
        this.state = state;
        this.time = time;
        this.lastPlayed = lastPlayed;
        this.data = data;
        this.puzzleNote = puzzleNote;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public int getCreated() {
        return created;
    }

    public void setCreated(int created) {
        this.created = created;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(int lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPuzzleNote() {
        return puzzleNote;
    }

    public void setPuzzleNote(String puzzleNote) {
        this.puzzleNote = puzzleNote;
    }
}
