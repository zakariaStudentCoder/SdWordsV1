package com.sd.db;

/**
 * Created by Admin on 31.03.2016.
 */
public class SudokuTableObject {

    private long id;
    private int folderId;
    private long created;
    private int state;
    private long time;
    private long lastPlayed;
    private String data;
    private String puzzleNote;

    public SudokuTableObject(long id, int folderId, long created, int state, long time, long lastPlayed, String data, String puzzleNote) {
        this.id = id;
        this.folderId = folderId;
        this.created = created;
        this.state = state;
        this.time = time;
        this.lastPlayed = lastPlayed;
        this.data = data;
        this.puzzleNote = puzzleNote;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public long getCreated() {
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

    public long getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public long getLastPlayed() {
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
