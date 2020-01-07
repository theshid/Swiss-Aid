package com.shid.swissaid.Database;

import android.app.Application;
import android.os.AsyncTask;

import com.shid.swissaid.Model.Draft;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DraftRepository  {

    private MutableLiveData<List<Draft>> searchResults = new MutableLiveData<>();
    private LiveData<List<Draft>> allDraft;

    private StepDao stepDao;

    public DraftRepository(Application application) {
        DraftRoomDatabase db;
        db = DraftRoomDatabase.getDatabase(application);
        stepDao = db.stepDao();
        allDraft = stepDao.getAllProjects();
    }

    public void insertProduct(Draft newDraft) {
        InsertAsyncTask task = new InsertAsyncTask(stepDao);
        task.execute(newDraft);
    }

    public void deleteDraft(String name) {
        DeleteAsyncTask task = new DeleteAsyncTask(stepDao);
        task.execute(name);
    }

    public void findDraft(String name) {
        QueryAsyncTask task = new QueryAsyncTask(stepDao);
        task.delegate = this;
        task.execute(name);
    }

    private void asyncFinished(List<Draft> results) {
        searchResults.setValue(results);
    }

    public LiveData<List<Draft>> getAllDrafts() {
        return allDraft;
    }

    public MutableLiveData<List<Draft>> getSearchResults() {
        return searchResults;
    }

    private static class QueryAsyncTask extends
            AsyncTask<String, Void, List<Draft>> {

        private StepDao asyncTaskDao;
        private DraftRepository delegate = null;

        QueryAsyncTask(StepDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected List<Draft> doInBackground(final String... params) {
            return asyncTaskDao.findDraft(params[0]);
        }

        @Override
        protected void onPostExecute(List<Draft> result) {
            delegate.asyncFinished(result);
        }
    }

    private static class InsertAsyncTask extends AsyncTask<Draft, Void, Void> {

        private StepDao asyncTaskDao;

        InsertAsyncTask(StepDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Draft... params) {
            asyncTaskDao.insertDraft(params[0]);
            return null;
        }
    }

    private static class DeleteAsyncTask extends AsyncTask<String, Void, Void> {

        private StepDao asyncTaskDao;

        DeleteAsyncTask(StepDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            asyncTaskDao.deleteDraft(params[0]);
            return null;
        }
    }
}
