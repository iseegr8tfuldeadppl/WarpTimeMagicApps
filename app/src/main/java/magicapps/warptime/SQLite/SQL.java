package magicapps.warptime.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQL extends SQLiteOpenHelper {

    //variables
    private static final String DATABASE_NAME = "warptimemagicapps.db"; //not case sensitive
    private static final String COL_1 = "_ID";
    private static final String COL_2 = "_SETTING";

    //constructor functions (selected 1st one)
    //Database creator function
    public SQL(Context context) {
        super(context, DATABASE_NAME, null, 1);

        //line just for checking
        //SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    }

    //impelment functions
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + "warptimedata" + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2 + " TEXT" + ");");
    }

    public void delete(Context context){
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + "warptimedata");
        onCreate(sqLiteDatabase);
    }

    //inputting data
    public boolean insertData(String _SETTING){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, _SETTING);
        long result = sqLiteDatabase.insert("warptimedata", null, contentValues); //returns -1 if failed to add
        if(result == -1) return false;
        else return true;
    }

    //outputting data
    public Cursor getAllDate() {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + "warptimedata" + "(" + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_2 + " TEXT" + ");");
        //instance
        return sqLiteDatabase.rawQuery("select * from " + "warptimedata" + ";", null);
    }

    //modify data
    public boolean updateData(String _ID, String _SETTING){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1, _ID);
        contentValues.put(COL_2, _SETTING);
        sqLiteDatabase.update("warptimedata", contentValues, COL_1 + "=?", new String[] { _ID });
        return true;
    }

}
