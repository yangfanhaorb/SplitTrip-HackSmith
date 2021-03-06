package edu.amherst.fyang17.hacksmithbackend;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;


public class memberCheckboxingActivity extends ActionBarActivity {
    LinearLayout linearMain;
    CheckBox checkBox;
    boolean[] checked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_checkboxing);
        List<Persons> people = Persons.listAll(Persons.class);
        String[] str = new String[people.size()];
        for (int i=0;i<people.size();i++){
            str[i] = people.get(i).name;
        }
        linearMain = (LinearLayout) findViewById(R.id.linearMain);
        checked = new boolean[str.length];

        for(int i = 0; i < str.length; i++){
            checkBox = new CheckBox(this);
            checkBox.setId(i);
            checkBox.setText(str[i]);
            if (AddNewDues.memberList[i]==false){
                checkBox.setChecked(false);
            }
            else checkBox.setChecked(true);
            checkBox.setOnClickListener(getOnClickDoSomething(checkBox, str[i]));
            linearMain.addView(checkBox);
        }

        Button button = new Button(this);
        button.setText("OKAY");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                returnToAddDues(v);
            }
        });
        linearMain.addView(button);

    }
    View.OnClickListener getOnClickDoSomething(final Button button,final String p) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                List<Persons> people = Persons.listAll(Persons.class);
                for (int i=0;i<people.size();i++){
                    if (people.get(i).name.equals(p)){
                        if (AddNewDues.memberList[i]==false)
                            AddNewDues.memberList[i] = true;
                        else AddNewDues.memberList[i] = false;
                        break;
                    }
                }
            }
        };
    }


    public void selectAll(View view){
        for (int i=0;i<AddNewDues.memberList.length;i++) {
            CheckBox cb = (CheckBox) findViewById(i);
            cb.setChecked(true);
            AddNewDues.memberList[i] = true;
        }
    }

    public void returnToAddDues(View view){
        Intent intent = new Intent(this,AddNewDues.class);
        //This seems to resume the AddNewDues activity to its previous state when started. Not sure why...
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_member_checkboxing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
