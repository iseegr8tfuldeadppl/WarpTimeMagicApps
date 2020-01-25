package magicapps.warptime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import magicapps.warptime.SQLite.SQL;
import magicapps.warptime.SQLite.SQLSharing;

public class Tutorial extends AppCompatActivity {

    @Override
    public void onBackPressed() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        TextView title = findViewById(R.id.title);
        Typeface font = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonter));
        Typeface font2 = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.anothafont));
        title.setTypeface(font);
        TextView ft = findViewById(R.id.firsttitle);
        TextView fs = findViewById(R.id.firstsub);
        TextView st = findViewById(R.id.secondtitle);
        TextView ss = findViewById(R.id.secondsub);
        TextView tt = findViewById(R.id.thirdtitle);
        TextView ts = findViewById(R.id.thirdsub);
        TextView ftt = findViewById(R.id.fourthtitle);
        TextView fts = findViewById(R.id.fourthsub);
        TextView fvt = findViewById(R.id.fifthtitle);
        TextView fvs = findViewById(R.id.fifthsub);
        TextView sxt = findViewById(R.id.sixthtitle);
        TextView sxs = findViewById(R.id.sixthsub);
        TextView svt = findViewById(R.id.seventhtitle);
        TextView svs = findViewById(R.id.seventhsub);
        TextView et = findViewById(R.id.eighthtitle);
        TextView es = findViewById(R.id.eighthsub);
        TextView nt = findViewById(R.id.ninetitle);
        TextView ns = findViewById(R.id.ninesub);
        TextView tnt = findViewById(R.id.tentitle);
        TextView tns = findViewById(R.id.tensub);
        TextView tntf = findViewById(R.id.importanttitle);
        TextView tnsff = findViewById(R.id.importanttext);
        Button yes = findViewById(R.id.yes);
        Button no = findViewById(R.id.no);
        ft.setTypeface(font2);
        tntf.setTypeface(font2);
        tnsff.setTypeface(font2);
        fs.setTypeface(font2);
        st.setTypeface(font2);
        ss.setTypeface(font2);
        tt.setTypeface(font2);
        ts.setTypeface(font2);
        ftt.setTypeface(font2);
        fts.setTypeface(font2);
        fvt.setTypeface(font2);
        fvs.setTypeface(font2);
        sxt.setTypeface(font2);
        sxs.setTypeface(font2);
        svt.setTypeface(font2);
        svs.setTypeface(font2);
        et.setTypeface(font2);
        es.setTypeface(font2);
        nt.setTypeface(font2);
        ns.setTypeface(font2);
        tnt.setTypeface(font2);
        tns.setTypeface(font2);
        yes.setTypeface(font2);
        no.setTypeface(font2);

        sql();
        if(SQLSharing.mycursor.getCount()<=0){
            SQLSharing.mydb.insertData(getResources().getString(R.string.no));
            close_sql();
        } else {
            SQLSharing.mycursor.moveToFirst();
            String SKIP = SQLSharing.mycursor.getString(1);
            if(SKIP.equals(getResources().getString(R.string.yas)))
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
