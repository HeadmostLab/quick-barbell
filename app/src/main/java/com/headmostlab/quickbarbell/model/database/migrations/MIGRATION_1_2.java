package com.headmostlab.quickbarbell.model.database.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MIGRATION_1_2 extends Migration {
    private static final MIGRATION_1_2 ourInstance = new MIGRATION_1_2();

    public static MIGRATION_1_2 get() {
        return ourInstance;
    }

    private MIGRATION_1_2() {
        super(1, 2);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {
        // create tables
        db.execSQL("CREATE TABLE IF NOT EXISTS `BarNew` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT " +
                "NULL, `weight` INTEGER, `unit` INTEGER, `barType` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `DiskNew` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT " +
                "NULL, `weight` INTEGER, `unit` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `WeightTemplateNew` (`id` INTEGER PRIMARY KEY " +
                "AUTOINCREMENT NOT NULL, `barid` INTEGER NOT NULL, `weight` INTEGER, `unit` INTEGER," +
                " `percent` REAL NOT NULL, `comment` TEXT, FOREIGN KEY(`barid`) REFERENCES `Bar`(`id`)" +
                " ON UPDATE CASCADE ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_WeightTemplateNew_barid` ON `WeightTemplateNew` (`barid`)");

        // fill in tables
        db.execSQL("INSERT INTO BarNew (id, weight, unit, barType) SELECT id, ROUND(weight*100) as weight, 0 as unit, barType FROM Bar");
        db.execSQL("INSERT INTO DiskNew (id, weight, unit) SELECT id, ROUND(weight*100), 0 AS unit FROM Disk");
        db.execSQL("DELETE FROM WeightTemplate WHERE barid NOT IN (SELECT id FROM Bar)");
        db.execSQL("INSERT INTO WeightTemplateNew (id, barid, weight, unit, percent, comment) " +
                "SELECT id, barid, ROUND(weight*100) as weight, 0 as unit, percent, comment FROM WeightTemplate");

        // drop tables
        db.execSQL("DROP TABLE WeightTemplate");
        db.execSQL("DROP TABLE Bar");
        db.execSQL("DROP TABLE Disk");

        // rename tables
        db.execSQL("ALTER TABLE WeightTemplateNew RENAME TO WeightTemplate");
        db.execSQL("ALTER TABLE BarNew RENAME TO Bar");
        db.execSQL("ALTER TABLE DiskNew RENAME TO Disk");
    }
}
