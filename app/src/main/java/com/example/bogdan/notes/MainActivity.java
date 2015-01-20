package com.example.bogdan.notes;

        import android.app.Activity;
        import android.content.ContentValues;
        import android.content.Context;
        import android.content.Intent;
        import android.database.Cursor;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.ContextMenu;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.AdapterView;
        import android.widget.AdapterView.OnItemClickListener;
        import android.widget.ListView;
        import android.widget.SimpleAdapter;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Map;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

        public class MainActivity extends Activity {

            private ListView lvMain = null;
            // имена атрибутов для Map
            final String ATTRIBUTE_NAME_NOTE = "NOTE";
            final String ATTRIBUTE_NAME_NAME = "NAME";
            final String ATTRIBUTE_ID ="ID_attribute";
            private ArrayList<Map<String,String>> data = null;
            private Map<String,String> m,n;
            private SimpleAdapter adAdapter=null;
            static final private int CREATE  = 0;
            private static final int CM_DELETE_ID =1;
            private int NUMBER = (-1);
            DBHelper dbHelper;
            ContentValues cv;
            SQLiteDatabase db;
            int ID_COUNT=1;
            String LOG = "myLogs";
            int idForUpd;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                data = new ArrayList<Map<String, String>>();
                dbHelper = new DBHelper(this);
                db = dbHelper.getWritableDatabase();
                //Вставляем значения из БД в список
                Cursor c = db.query("mytable",null,null,null,null,null,null);
                if (c.moveToFirst()) {
                    int nameColIndex = c.getColumnIndex("name");
                    int noteColIndex = c.getColumnIndex("note");
                    int idColIndex = c.getColumnIndex("id");
                    do {
                        m = new HashMap<String, String>();
                        m.put(ATTRIBUTE_NAME_NOTE, c.getString(noteColIndex));
                        m.put(ATTRIBUTE_NAME_NAME, c.getString(nameColIndex));
                        m.put(ATTRIBUTE_ID, Integer.toString(c.getInt(idColIndex)));
                        data.add(m);
                    } while (c.moveToNext());
                } else Log.d(LOG,"0 rows in DB");
                c.close();
                dbHelper.close();
                lvMain = (ListView) findViewById(R.id.listView);
                String[] from = {ATTRIBUTE_NAME_NOTE,ATTRIBUTE_NAME_NAME};
                int[] to = {R.id.textView1,R.id.textView2};
                adAdapter = new SimpleAdapter(this, data, R.layout.item, from, to);
                lvMain.setAdapter(adAdapter);
                registerForContextMenu(lvMain);
                lvMain.setOnItemClickListener(new OnItemClickListener() {
                     @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

                            String noteName = (data.get(arg2).get(ATTRIBUTE_NAME_NAME));
                            String noteText =(data.get(arg2).get(ATTRIBUTE_NAME_NOTE));
                            String idNote = (data.get(arg2).get(ATTRIBUTE_ID));


                            NUMBER = arg2;
                            idForUpd = Integer.valueOf(idNote);

                            Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
                            intent.putExtra("noteName",noteName);
                            intent.putExtra("noteText",noteText);
                            startActivityForResult(intent, CREATE);
                        }
                    });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        if (id == R.id.menu_add) {
            Intent intent = new Intent(MainActivity.this, NoteEditActivity.class);
            startActivityForResult(intent,CREATE);
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
      protected void onActivityResult (int requestCode, int resultCode, Intent data2){
        super.onActivityResult(requestCode,resultCode, data2);
        if (requestCode == CREATE){
            if (resultCode == RESULT_OK){
                String NN = data2.getStringExtra(NoteEditActivity.NameN);
                String NT = data2.getStringExtra(NoteEditActivity.TextN);
                //Если было вызвано редактирование заметки
                if (NUMBER != (-1)) {
                n = new HashMap <String,String>();
                n.put(ATTRIBUTE_NAME_NAME, NN);
                n.put(ATTRIBUTE_NAME_NOTE,NT);
                String id = Integer.toString(idForUpd);
                n.put(ATTRIBUTE_ID,id);
                db = dbHelper.getWritableDatabase();
                cv = new ContentValues();
                cv.put("name",NN);
                cv.put("note",NT);
                db.update("mytable",cv, "id = ?", new String[]{id});
                dbHelper.close();
                Log.d(LOG,"item edited "+id);
                data.set(NUMBER, n);
                adAdapter.notifyDataSetChanged();
                ReadBD();
                }
                //Если было создание новой заметки
                else {
                    //обновляем заметку в отображаемом списке
                    n = new HashMap <String,String>();
                    n.put(ATTRIBUTE_NAME_NAME, NN);
                    n.put(ATTRIBUTE_NAME_NOTE,NT);
                    n.put(ATTRIBUTE_ID,Integer.toString(ID_COUNT));
                    data.add(n);
                    adAdapter.notifyDataSetChanged();
                    //меняем значение по ID заметки в БД
                    db = dbHelper.getWritableDatabase();
                    cv = new ContentValues();
                    cv.put("name",NN);
                    cv.put("note",NT);
                    db.insert("mytable",null,cv);
                    dbHelper.close();
                    Log.d("myLogs","item saved in DB");
                    ID_COUNT++;
                    ReadBD();
            }
                NUMBER = (-1);
        }}
    }
    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,CM_DELETE_ID,0,"Удалить запись");

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId()==CM_DELETE_ID){
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
            String ID_TO_DEL =(data.get(acmi.position).get(ATTRIBUTE_ID));
            db = dbHelper.getWritableDatabase();
            //удаляем заметку в БД
            db.delete("mytable","id = "+Integer.valueOf(ID_TO_DEL),null);
            Log.d(LOG,"deleted from DB "+ID_TO_DEL+" "+Integer.valueOf(ID_TO_DEL));
            dbHelper.close();
            //удаляем заметку в отображаемом списке
            data.remove(acmi.position);
            adAdapter.notifyDataSetChanged();
            ReadBD();
            return true;
        }
        return super.onContextItemSelected(item);
    }

    class DBHelper extends SQLiteOpenHelper {
        public DBHelper(Context context){
            super(context, "myDB", null,1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table mytable ("+"id integer primary key autoincrement,"
            + "name text,"+"note text"+");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }

        public void ReadBD(){
            Log.d(LOG, "--- Rows in mytable: ---");
            db = dbHelper.getWritableDatabase();
            Cursor c = db.query("mytable", null, null, null, null, null, null);
            if (c.moveToFirst()) {
            int idColIndex2 = c.getColumnIndex("id");
            int nameColIndex2 = c.getColumnIndex("name");
            int noteColIndex2 = c.getColumnIndex("note");
            do { Log.d(LOG,
                "ID = " + c.getInt(idColIndex2) +
                ", name = " + c.getString(nameColIndex2) +
                ", note = " + c.getString(noteColIndex2));
                 } while (c.moveToNext());
            } else
                    Log.d(LOG, "0 rows");
            c.close();}

}

