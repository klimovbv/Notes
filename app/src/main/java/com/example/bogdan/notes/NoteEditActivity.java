package com.example.bogdan.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Bogdan on 22.12.14.
 */
public class NoteEditActivity extends Activity {
    private EditText noteTextEdit = null;
    private EditText noteNameEdit = null;
    public final static String NameN = "note Name";
    public final static String TextN = "note Text";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_note_edit);
        noteNameEdit = (EditText)findViewById(R.id.noteNameEdit);
        noteTextEdit = (EditText)findViewById(R.id.noteTextEdit);
        Intent intent = getIntent();
        noteNameEdit.setText(intent.getStringExtra("noteName"));
        noteTextEdit.setText(intent.getStringExtra("noteText"));

    }
    public void onClick (View view){
        Intent nameIntent = new Intent();
        nameIntent.putExtra(NameN,noteNameEdit.getText().toString());
        nameIntent.putExtra(TextN,noteTextEdit.getText().toString());
        setResult(RESULT_OK,nameIntent);
        finish();


    }
}
