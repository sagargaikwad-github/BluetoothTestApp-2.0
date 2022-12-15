package com.example.bluetoothupdated;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqliteData extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "density.db";

    public  SqliteData(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String qry = "Create table Density( Air_Temperature int Primary key,Air_Density int)";
        sqLiteDatabase.execSQL(qry);

        sqLiteDatabase.execSQL("insert into Density values(-20,1.4)");
        sqLiteDatabase.execSQL("insert into Density values(-19,1.4)");
        sqLiteDatabase.execSQL("insert into Density values(-18,1.4)");
        sqLiteDatabase.execSQL("insert into Density values(-17,1.4)");
        sqLiteDatabase.execSQL("insert into Density values(-16,1.4)");
        sqLiteDatabase.execSQL("insert into Density values(-15,1.4)");
        sqLiteDatabase.execSQL("insert into Density values(-14,1.4)");
        sqLiteDatabase.execSQL("insert into Density values(-13,1.35)");
        sqLiteDatabase.execSQL("insert into Density values(-12,1.35)");
        sqLiteDatabase.execSQL("insert into Density values(-11,1.34)");
        sqLiteDatabase.execSQL("insert into Density values(-10,1.34)");
        sqLiteDatabase.execSQL("insert into Density values(-9,1.34)");
        sqLiteDatabase.execSQL("insert into Density values(-8,1.33)");
        sqLiteDatabase.execSQL("insert into Density values(-7,1.33)");
        sqLiteDatabase.execSQL("insert into Density values(-6,1.32)");
        sqLiteDatabase.execSQL("insert into Density values(-5,1.32)");
        sqLiteDatabase.execSQL("insert into Density values(-4,1.31)");
        sqLiteDatabase.execSQL("insert into Density values(-3,1.31)");
        sqLiteDatabase.execSQL("insert into Density values(-2,1.30)");
        sqLiteDatabase.execSQL("insert into Density values(-1,1.3)");
        sqLiteDatabase.execSQL("insert into Density values(0,1.3)");
        sqLiteDatabase.execSQL("insert into Density values(1,1.3)");
        sqLiteDatabase.execSQL("insert into Density values(2,1.3)");
        sqLiteDatabase.execSQL("insert into Density values(3,1.3)");
        sqLiteDatabase.execSQL("insert into Density values(4,1.3)");
        sqLiteDatabase.execSQL("insert into Density values(5,1.3)");
        sqLiteDatabase.execSQL("insert into Density values(7,1.26)");
        sqLiteDatabase.execSQL("insert into Density values(8,1.25)");
        sqLiteDatabase.execSQL("insert into Density values(9,1.25)");
        sqLiteDatabase.execSQL("insert into Density values(10,1.25)");
        sqLiteDatabase.execSQL("insert into Density values(11,1.24)");
        sqLiteDatabase.execSQL("insert into Density values(12,1.24)");
        sqLiteDatabase.execSQL("insert into Density values(13,1.23)");
        sqLiteDatabase.execSQL("insert into Density values(14,1.23)");
        sqLiteDatabase.execSQL("insert into Density values(15,1.24)");
        sqLiteDatabase.execSQL("insert into Density values(16,1.22)");
        sqLiteDatabase.execSQL("insert into Density values(17,1.22)");
        sqLiteDatabase.execSQL("insert into Density values(18,1.21)");
        sqLiteDatabase.execSQL("insert into Density values(19, 1.21)");
        sqLiteDatabase.execSQL("insert into Density values(20,1.20)");
        sqLiteDatabase.execSQL("insert into Density values(21,1.2)");

        sqLiteDatabase.execSQL("insert into Density values(22,1.2)");
        sqLiteDatabase.execSQL("insert into Density values(23,1.2)");
        sqLiteDatabase.execSQL("insert into Density values(24,1.2)");
        sqLiteDatabase.execSQL("insert into Density values(25,1.2)");
        sqLiteDatabase.execSQL("insert into Density values(26,1.2)");
        sqLiteDatabase.execSQL("insert into Density values(27,1.2)");
        sqLiteDatabase.execSQL("insert into Density values(28,1.2)");
        sqLiteDatabase.execSQL("insert into Density values(29,1.2)");
        sqLiteDatabase.execSQL("insert into Density values(30,1.2)");

        sqLiteDatabase.execSQL("insert into Density values(31,1.24)");
        sqLiteDatabase.execSQL("insert into Density values(32,1.15)");
        sqLiteDatabase.execSQL("insert into Density values(33,1.15)");
        sqLiteDatabase.execSQL("insert into Density values(34,1.15)");
        sqLiteDatabase.execSQL("insert into Density values(35,1.15)");
        sqLiteDatabase.execSQL("insert into Density values(36,1.14)");
        sqLiteDatabase.execSQL("insert into Density values(37,1.14)");
        sqLiteDatabase.execSQL("insert into Density values(38,1.13)");
        sqLiteDatabase.execSQL("insert into Density values(39,1.13)");
        sqLiteDatabase.execSQL("insert into Density values(40,1.13)");

        sqLiteDatabase.execSQL("insert into Density values(41,1.12)");
        sqLiteDatabase.execSQL("insert into Density values(42,1.12)");
        sqLiteDatabase.execSQL("insert into Density values(43,1.12)");
        sqLiteDatabase.execSQL("insert into Density values(44,1.11)");
        sqLiteDatabase.execSQL("insert into Density values(45,1.11)");
        sqLiteDatabase.execSQL("insert into Density values(46,1.11)");
        sqLiteDatabase.execSQL("insert into Density values(47,1.10)");
        sqLiteDatabase.execSQL("insert into Density values(48,1.1)");
        sqLiteDatabase.execSQL("insert into Density values(49,1.1)");
        sqLiteDatabase.execSQL("insert into Density values(50,1.1)");


        String qry1 = "Create table EquationValues( Diameter_of_Orifice String ," +
                "Upstream_lateral_pipe_diameter String," +
                "Coefficient_of_discharge String," +
                "Expansion_factor String)";
        sqLiteDatabase.execSQL(qry1);
        sqLiteDatabase.execSQL("insert into EquationValues values(0.08,0.1,0.61,1.0)");



    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public Float getDensity(Float tempMain) {

        float density = 0;

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        String qry = "Select Air_Density from Density where Air_Temperature=" + tempMain;
        Cursor cursor = sqLiteDatabase.rawQuery(qry, null);

        if (cursor.moveToFirst()) {
            do {
                float val = cursor.getFloat(0);
                density = val;
            } while (cursor.moveToNext());
        } else {

        }
        return density;
    }

    public String get_Diameter_of_Orifice() {
        String DiameterOfOrifice = null;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String qry = "Select Diameter_of_Orifice from EquationValues";
        Cursor cursor = sqLiteDatabase.rawQuery(qry, null);
        if (cursor.moveToFirst()) {
            do {
                String val = cursor.getString(0);
                DiameterOfOrifice = val;
            } while (cursor.moveToNext());
        } else {

        }
        return DiameterOfOrifice;
    }
    public String get_Upstream_lateral_pipe_diameter() {
        String UpstreamLateralPipeDiameter = null;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String qry = "Select Upstream_lateral_pipe_diameter from EquationValues";
        Cursor cursor = sqLiteDatabase.rawQuery(qry, null);
        if (cursor.moveToFirst()) {
            do {
                String val = cursor.getString(0);
                UpstreamLateralPipeDiameter = val;
            } while (cursor.moveToNext());
        } else {

        }
        return UpstreamLateralPipeDiameter;
    }
    public String get_Coefficient_of_discharge() {
        String Coefficient_of_discharge = null;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String qry = "Select Coefficient_of_discharge from EquationValues";
        Cursor cursor = sqLiteDatabase.rawQuery(qry, null);
        if (cursor.moveToFirst()) {
            do {
                String val = cursor.getString(0);
                Coefficient_of_discharge = val;
            } while (cursor.moveToNext());
        } else {

        }
        return Coefficient_of_discharge;
    }
    public String get_Expansion_factor() {
        String Expansion_factor = null;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String qry = "Select Expansion_factor from EquationValues";
        Cursor cursor = sqLiteDatabase.rawQuery(qry, null);
        if (cursor.moveToFirst()) {
            do {
                String val = cursor.getString(0);
                Expansion_factor = val;
            } while (cursor.moveToNext());
        } else {

        }
        return Expansion_factor;
    }


    public boolean updateEquationValues(String diameterOfOrifee, String upstreamPipeDiameter, String coefficientOfDischarge, String exponentialFactor) {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put("Diameter_of_Orifice",diameterOfOrifee);
        cv.put("Upstream_lateral_pipe_diameter",upstreamPipeDiameter);
        cv.put("Coefficient_of_discharge",coefficientOfDischarge);
        cv.put("Expansion_factor",exponentialFactor);

        long res=sqLiteDatabase.update("EquationValues",cv,null,null);
        if (res == -1) {
            return false;
        } else {
            return true;
        }

    }
}
