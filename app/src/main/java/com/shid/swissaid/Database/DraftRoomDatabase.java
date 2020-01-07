package com.shid.swissaid.Database;

import android.content.Context;

import com.shid.swissaid.Model.Draft;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Draft.class}, version = 1)
public abstract class DraftRoomDatabase extends RoomDatabase {
    public abstract StepDao stepDao();
    private static DraftRoomDatabase INSTANCE;


    static DraftRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (DraftRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    DraftRoomDatabase.class,
                                    "draft_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
