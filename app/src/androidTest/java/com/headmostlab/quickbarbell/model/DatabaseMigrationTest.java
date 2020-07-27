package com.headmostlab.quickbarbell.model;


import com.headmostlab.quickbarbell.model.database.AppDatabase;
import com.headmostlab.quickbarbell.model.database.migrations.MIGRATION_1_2;
import com.headmostlab.quickbarbell.model.database.migrations.MIGRATION_2_3;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

@RunWith(AndroidJUnit4.class)
public class DatabaseMigrationTest {

    private static final String TEST_DB = "migration-test";

    @Rule
    public MigrationTestHelper helper;

    public DatabaseMigrationTest() {
        helper = new MigrationTestHelper(InstrumentationRegistry.getInstrumentation(),
                AppDatabase.class.getCanonicalName(),
                new FrameworkSQLiteOpenHelperFactory());
    }

    @Test
    public void migrate1To2() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 1);
        db.execSQL("INSERT INTO Bar ('id','weight','barType') VALUES (1,6.5,1)");
        db.execSQL("INSERT INTO Bar ('id','weight','barType') VALUES (2,9.0,0)");
        db.execSQL("INSERT INTO Bar ('id','weight','barType') VALUES (3,2.3,2)");
        db.execSQL("INSERT INTO Disk ('id','weight') VALUES (1,1.25)");
        db.execSQL("INSERT INTO Disk ('id','weight') VALUES (2,1.25)");
        db.execSQL("INSERT INTO Disk ('id','weight') VALUES (3,2.5)");
        db.execSQL("INSERT INTO WeightTemplate ('id','barid','weight','percent','comment') VALUES (1,1,52.0,100.0,'Biceps')");
        db.execSQL("INSERT INTO WeightTemplate ('id','barid','weight','percent','comment') VALUES (2,1,94.0,100.0,'Back')");
        db.execSQL("INSERT INTO WeightTemplate ('id','barid','weight','percent','comment') VALUES (5,2,79.0,100.0,'Bench press')");
        db.execSQL("INSERT INTO WeightTemplate ('id','barid','weight','percent','comment') VALUES (6,2,87.0,100.0,'Squat')");
        db.execSQL("INSERT INTO WeightTable ('id','weight','disksCount','disks') VALUES (5,5.0,2,'2.5 2.5')");
        db.execSQL("INSERT INTO WeightTable ('id','weight','disksCount','disks') VALUES (83,41.25,7,'1.25 2.5 2.5 2.5 2.5 15.0 15.0')");
        db.execSQL("INSERT INTO WeightTable ('id','weight','disksCount','disks') VALUES (119,66.25,9,'1.25 2.5 2.5 2.5 2.5 5.0 15.0 15.0 20.0')");
        db.close();
        helper.runMigrationsAndValidate(TEST_DB, 2, true, MIGRATION_1_2.get());
    }

    @Test
    public void migrate2To3() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 2);
        db.execSQL("INSERT INTO Bar ('id','weight','unit','barType') VALUES (1,650,0,1)");
        db.execSQL("INSERT INTO Bar ('id','weight','unit','barType') VALUES (2,900,0,0)");
        db.execSQL("INSERT INTO Bar ('id','weight','unit','barType') VALUES (3,230,0,2)");
        db.execSQL("INSERT INTO Disk ('id','weight','unit') VALUES (1,125,0)");
        db.execSQL("INSERT INTO Disk ('id','weight','unit') VALUES (2,125,0)");
        db.execSQL("INSERT INTO Disk ('id','weight','unit') VALUES (3,250,0)");
        db.execSQL("INSERT INTO WeightTemplate ('id','barid','weight','unit','percent','comment') VALUES (1,1,5200,0,100.0,'Biceps')");
        db.execSQL("INSERT INTO WeightTemplate ('id','barid','weight','unit','percent','comment') VALUES (2,1,9400,0,100.0,'Back')");
        db.execSQL("INSERT INTO WeightTemplate ('id','barid','weight','unit','percent','comment') VALUES (5,2,7900,0,100.0,'Bench press')");
        db.execSQL("INSERT INTO WeightTemplate ('id','barid','weight','unit','percent','comment') VALUES (6,2,8700,0,100.0,'Squat')");
        db.execSQL("INSERT INTO WeightTable ('id','weight','disksCount','disks') VALUES (5,5.0,2,'2.5 2.5')");
        db.execSQL("INSERT INTO WeightTable ('id','weight','disksCount','disks') VALUES (83,41.25,7,'1.25 2.5 2.5 2.5 2.5 15.0 15.0')");
        db.execSQL("INSERT INTO WeightTable ('id','weight','disksCount','disks') VALUES (119,66.25,9,'1.25 2.5 2.5 2.5 2.5 5.0 15.0 15.0 20.0')");
        db.close();
        helper.runMigrationsAndValidate(TEST_DB, 3, true, MIGRATION_2_3.get());
    }

    @Test
    public void migrateAll() throws IOException {
        SupportSQLiteDatabase db = helper.createDatabase(TEST_DB, 1);
        db.close();

        AppDatabase appDb = Room.databaseBuilder(InstrumentationRegistry.getInstrumentation().getTargetContext(),
                AppDatabase.class, TEST_DB).addMigrations(ALL_MIGRATIONS).build();
        appDb.getOpenHelper().getWritableDatabase();
        appDb.close();
    }

    private static final Migration[] ALL_MIGRATIONS = {MIGRATION_1_2.get(), MIGRATION_2_3.get()};
}
