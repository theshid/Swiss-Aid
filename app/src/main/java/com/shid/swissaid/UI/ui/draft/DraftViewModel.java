package com.shid.swissaid.UI.ui.draft;

import android.app.Application;

import com.shid.swissaid.Database.DraftRepository;
import com.shid.swissaid.Model.Draft;

import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DraftViewModel extends AndroidViewModel {
    private DraftRepository repository;
    private LiveData<List<Draft>> allDrafts;
    private MutableLiveData<List<Draft>> searchResults;

    public DraftViewModel (Application application) {
        super(application);
        repository = new DraftRepository(application);
        allDrafts = repository.getAllDrafts();
        searchResults = repository.getSearchResults();
    }

    MutableLiveData<List<Draft>> getSearchResults() {
        return searchResults;
    }

    LiveData<List<Draft>> getAllDrafts() {
        return allDrafts;
    }

    public void insertDraft(Draft draft) {
        repository.insertProduct(draft);
    }

    public void findDraft(String name) {
        repository.findDraft(name);
    }

    public void deleteDraft(String name) {
        repository.deleteDraft(name);
    }
}
