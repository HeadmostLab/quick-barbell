package com.headmostlab.quickbarbell.model.database.migrations;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class MIGRATION_2_3 extends Migration {
    private static final MIGRATION_2_3 ourInstance = new MIGRATION_2_3();

    public static MIGRATION_2_3 get() {
        return ourInstance;
    }

    private MIGRATION_2_3() {
        super(2, 3);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {
        // create tables
        db.execSQL("CREATE TABLE IF NOT EXISTS `DiskNew` (`diskId` INTEGER PRIMARY KEY AUTOINCREMENT " +
                "NOT NULL, `weight` INTEGER, `unit` INTEGER, `hidden` INTEGER NOT NULL DEFAULT 0)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `Bunch` (`bunchId` INTEGER PRIMARY KEY AUTOINCREMENT " +
                "NOT NULL, `weight` INTEGER NOT NULL, `count` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `BunchDiskLink` (`bunchId` INTEGER NOT NULL, `diskId` " +
                "INTEGER NOT NULL, PRIMARY KEY(`bunchId`, `diskId`), FOREIGN KEY(`diskId`) REFERENCES " +
                "`Disk`(`diskId`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`bunchId`) " +
                "REFERENCES `Bunch`(`bunchId`) ON UPDATE CASCADE ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_BunchDiskLink_bunchId` ON `BunchDiskLink` (`bunchId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_BunchDiskLink_diskId` ON `BunchDiskLink` (`diskId`)");

        // fill in tables
        db.execSQL("INSERT INTO DiskNew (diskId, weight, unit, hidden) SELECT id, weight, unit, 0 as hidden FROM Disk");

        // drop tables
        db.execSQL("DROP TABLE WeightTable");
        db.execSQL("DROP TABLE Disk");

        // rename tables
        db.execSQL("ALTER TABLE DiskNew RENAME TO Disk");
    }
}
