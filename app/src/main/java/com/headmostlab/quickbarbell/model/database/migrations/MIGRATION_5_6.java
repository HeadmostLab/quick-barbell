package com.headmostlab.quickbarbell.model.database.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MIGRATION_5_6 extends Migration {
    private static final MIGRATION_5_6 ourInstance = new MIGRATION_5_6();

    public static MIGRATION_5_6 get() {
        return ourInstance;
    }

    private MIGRATION_5_6() {
        super(5, 6);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `WeightHistory` (`id` INTEGER PRIMARY KEY AUTOINCREMENT " +
                "NOT NULL, `templateId` INTEGER NOT NULL, `date` INTEGER NOT NULL, `weight` INTEGER NOT " +
                "NULL, `unit` INTEGER NOT NULL, FOREIGN KEY(`templateId`) REFERENCES `WeightTemplate`(`id`) " +
                "ON UPDATE CASCADE ON DELETE CASCADE )");

        db.execSQL("CREATE INDEX IF NOT EXISTS `index_WeightHistory_templateId` ON `WeightHistory` (`templateId`)");

        db.execSQL("INSERT INTO WeightHistory (templateId, date, weight, unit) SELECT id, " +
                "strftime('%s','now')*1000, weight, unit FROM WeightTemplate");
    }
}
