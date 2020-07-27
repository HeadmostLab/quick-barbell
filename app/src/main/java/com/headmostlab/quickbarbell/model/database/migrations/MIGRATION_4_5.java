package com.headmostlab.quickbarbell.model.database.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MIGRATION_4_5 extends Migration {
    private static final MIGRATION_4_5 ourInstance = new MIGRATION_4_5();

    public static MIGRATION_4_5 get() {
        return ourInstance;
    }

    private MIGRATION_4_5() {
        super(4, 5);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {

        db.execSQL("PRAGMA foreign_keys = OFF");

        // Bunch
        db.execSQL("ALTER TABLE `Bunch` RENAME TO `Bunch_old`");
        db.execSQL("DROP INDEX index_Bunch_weight");
        db.execSQL("CREATE TABLE IF NOT EXISTS `Bunch` (`bunchId` INTEGER PRIMARY KEY " +
                "AUTOINCREMENT NOT NULL, `weight` INTEGER NOT NULL, `count` INTEGER NOT NULL, `balanced` INTEGER NOT NULL)");
        db.execSQL("INSERT INTO Bunch (bunchid, weight, count, balanced) SELECT bunchid, weight, count, 1 as balanced FROM Bunch_old");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Bunch_weight` ON `Bunch` (`weight`)");

        // WeightTemplate
        db.execSQL("ALTER TABLE `WeightTemplate` RENAME TO `WeightTemplate_old`");
        db.execSQL("DROP INDEX index_WeightTemplateNew_barid");
        db.execSQL("CREATE TABLE IF NOT EXISTS `WeightTemplate` (`id` INTEGER PRIMARY KEY AUTOINCREMENT " +
                "NOT NULL, `barid` INTEGER NOT NULL, `weight` INTEGER, `unit` INTEGER, `percent` REAL " +
                "NOT NULL, `comment` TEXT, `balanced` INTEGER NOT NULL, FOREIGN KEY(`barid`) REFERENCES " +
                "`Bar`(`id`) ON UPDATE CASCADE ON DELETE CASCADE )");
        db.execSQL("INSERT INTO WeightTemplate (id, barid, weight, unit, percent, comment, balanced) " +
                "SELECT id, barid, weight, unit, percent, comment, 1 as balanced FROM WeightTemplate_old");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_WeightTemplate_barid` ON `WeightTemplate` (`barid`)");

        db.execSQL("DROP TABLE Bunch_old");
        db.execSQL("DROP TABLE WeightTemplate_old");

        db.execSQL("PRAGMA foreign_keys = ON");
    }
}
