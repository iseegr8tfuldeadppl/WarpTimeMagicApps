package magicapps.warptime;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import magicapps.warptime.SQLite.SQL;
import magicapps.warptime.SQLite.SQLSharing;

public class confirmdialog extends Dialog {

    private Activity c;

    confirmdialog(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.confirmdialog);

        Typeface font2 = Typeface.createFromAsset(c.getAssets(), "Tajawal-Medium.ttf");
        Button yes = findViewById(R.id.yes);
        Button no = findViewById(R.id.no);
        TextView title = findViewById(R.id.title);
        TextView text = findViewById(R.id.text);
        title.setTypeface(font2);
        text.setTypeface(font2);
        yes.setTypeface(font2);
        no.setTypeface(font2);
        yes.setTypeface(font2);
        no.setTypeface(font2);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sql();
                SQLSharing.mycursor.moveToFirst();
                String ID = SQLSharing.mycursor.getString(0);
                SQLSharing.mydb.updateData(ID, "yes");
                close_sql();
                Intent yes = new Intent(c, MainActivity.class);
                c.startActivity(yes);
                dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void sql() {
        if(SQLSharing.mycursor!=null)
            SQLSharing.mycursor.close();
        if(SQLSharing.mydb!=null)
            SQLSharing.mydb.close();
        SQLSharing.mydb = new SQL(c);
        SQLSharing.mycursor = SQLSharing.mydb.getAllDate();
    }

    private void close_sql() {
        if(SQLSharing.mycursor!=null)
            SQLSharing.mycursor.close();
        if(SQLSharing.mydb!=null)
            SQLSharing.mydb.close();
    }


}