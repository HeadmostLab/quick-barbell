package com.headmostlab.quickbarbell.model.database.migrations;

public class Migrations {
    public static androidx.room.migration.Migration[] ALL = {
            MIGRATION_1_2.get(),
            MIGRATION_2_3.get(),
            MIGRATION_3_4.get(),
            MIGRATION_4_5.get(),
            MIGRATION_5_6.get(),
    };
}
