package com.headmostlab.quickbarbell.model.database.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MIGRATION_3_4 extends Migration {
    private static final MIGRATION_3_4 ourInstance = new MIGRATION_3_4();

    public static MIGRATION_3_4 get() {
        return ourInstance;
    }

    private MIGRATION_3_4() {
        super(3, 4);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Bunch_weight` ON `Bunch` (`weight`)");
    }
}
