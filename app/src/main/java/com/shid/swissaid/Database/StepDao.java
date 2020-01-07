package com.shid.swissaid.Database;

import com.shid.swissaid.Model.Draft;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface StepDao {

    @Insert
    void insertDraft(Draft draft);

    @Query("SELECT * FROM draft WHERE nameProject = :name")
    List<Draft> findDraft(String name);

    @Query("DELETE FROM draft WHERE nameProject = :name")
    void deleteDraft(String name);

    @Query("SELECT * FROM draft")
    LiveData<List<Draft>> getAllProjects();
}
