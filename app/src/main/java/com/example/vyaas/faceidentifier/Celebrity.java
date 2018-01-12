package com.example.vyaas.faceidentifier;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by vyaas on 12/8/17.
 */
@Entity(tableName = "CELEBRITY")
public class Celebrity {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "persistedId")
    private String persistedId;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "bio")
    private String bio;

    @ColumnInfo(name = "twitter")
    private String twitter;

    @ColumnInfo(name = "wikipedia")
    private String wikipedia;


    public String getPersistedId() {
        return persistedId;
    }

    public void setPersistedId(String persistedId) {
        this.persistedId = persistedId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public void setWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
    }

}
