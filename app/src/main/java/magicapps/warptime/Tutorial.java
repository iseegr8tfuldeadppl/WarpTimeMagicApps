package magicapps.warptime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import magicapps.warptime.SQLite.SQL;
import magicapps.warptime.SQLite.SQLSharing;

public class Tutorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        sql();
        if(SQLSharing.mycursor.getCount()<=0){
            SQLSharing.mydb.insertData("no");
            close_sql();
        } else {
            SQLSharing.mycursor.moveToFirst();
            String SKIP = SQLSharing.mycursor.getString(1);
            if(SKIP.equals("yes"))
                get_out();
            else
                close_sql();
        }

    }

    public void notutorialClicked(View view) {
        confirmdialog dialog = new confirmdialog(this);
        dialog.show();
    }

    public void YesTutorialClicked(View view) {
        get_out();
    }

    private void get_out() {
        Intent tutorialed = new Intent(this, MainActivity.class);
        startActivity(tutorialed);
        finish();
    }

    private void sql() {
        if(SQLSharing.mycursor!=null)
            SQLSharing.mycursor.close();
        if(SQLSharing.mydb!=null)
            SQLSharing.mydb.close();
        SQLSharing.mydb = new SQL(this);
        SQLSharing.mycursor = SQLSharing.mydb.getAllDate();
    }

    private void close_sql() {
        if(SQLSharing.mycursor!=null)
            SQLSharing.mycursor.close();
        if(SQLSharing.mydb!=null)
            SQLSharing.mydb.close();
    }
}
