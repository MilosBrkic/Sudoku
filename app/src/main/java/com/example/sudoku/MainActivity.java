package com.example.sudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.os.AsyncTask;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;
    TextView textTime;
    CountDownTimer timer;
    Button[][] tabla = new Button[9][9];
    RelativeLayout layout;
    Button selected = null;
    ProgressBar bar;
    Spinner difficulty;
    String[] levels = new String[]{"Easy", "Medium", "Hard"};
    int level = 1;
    Baza baza;

    FragmentIgra igra;
    FragmentRekord rekord;
    FragmentManager manager;
    BottomNavigationView meni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        igra = new FragmentIgra();
        rekord = new FragmentRekord();
        manager = getSupportFragmentManager();

        manager.beginTransaction().add(R.id.frame, igra , "Igra").commit();
        manager.beginTransaction().add(R.id.frame, rekord, "Rekord").hide(rekord).commit();

        meni = findViewById(R.id.bottomMenu);
        meni.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    //Proveravamo koja komponenta menija je pritisnuta i u zavisnosti od toga prikazujemo odredjeni fragment
                    case R.id.igra:
                        igra.onResume();
                        manager.beginTransaction().show(igra).hide(rekord).commit();
                        return true;

                    case R.id.score:
                        igra.onPause();
                        rekord.onResume();
                        manager.beginTransaction().show(rekord).hide(igra).commit();
                        return true;

                }
                return false;
            }
        });


        //igra.postavi(3);
        /*preferences = getSharedPreferences("timer",MODE_PRIVATE);
        textTime = findViewById(R.id.textTime);
        baza = new Baza(this);
        bar = findViewById(R.id.progressBar);
        //bar.setVisibility(View.INVISIBLE);
        difficulty = findViewById(R.id.difficulty);
        layout = findViewById(R.id.tablaLayout);

        layout.post(new Runnable() {
            public void run() {
                int width = layout.getWidth();
                postavi(width);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, levels);
        //difficulty.setAdapter(adapter);

        //Timer timer = new Timer(textTime);
        //timer.start();
        //setTimer();*/
    }


    public void postavi(int width) {

        int sirina = width / 9;

        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 9; y++) {
                Button but = new Button(this);
                tabla[x][y] = but;
                layout.addView(but);
                ViewGroup.LayoutParams param = but.getLayoutParams();
                param.width = sirina - 5;
                param.height = sirina - 5;
                but.setTextSize(sirina / 4);
                but.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                but.setPadding(0, 0, 0, 0);
                but.setBackgroundColor(Color.WHITE);


                if (x < 3)
                    but.setX(x * sirina);
                if (x >= 3 && x < 6)
                    but.setX(x * sirina + 5);
                if (x >= 6)
                    but.setX(x * sirina + 10);


                if (y < 3)
                    but.setY(y * sirina);
                if (y >= 3 && y < 6)
                    but.setY(y * sirina + 5);
                if (y >= 6)
                    but.setY(y * sirina + 10);


                but.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (selected != null)
                            selected.setBackgroundColor(Color.WHITE);

                        selected = (Button) v;
                        selected.setBackgroundColor(Color.YELLOW);
                    }
                });

            }
        loadTable();
    }

    public void novaIgra() {

        //String s = "{\"response\":true,\"size\":\"9?level=3\",\"squares\":[{\"x\":0,\"y\":3,\"value\":1},{\"x\":0,\"y\":4,\"value\":2},{\"x\":0,\"y\":5,\"value\":5},{\"x\":0,\"y\":7,\"value\":7},{\"x\":0,\"y\":8,\"value\":9},{\"x\":1,\"y\":1,\"value\":7},{\"x\":1,\"y\":2,\"value\":1},{\"x\":1,\"y\":5,\"value\":4},{\"x\":1,\"y\":6,\"value\":5},{\"x\":1,\"y\":7,\"value\":8},{\"x\":2,\"y\":0,\"value\":5},{\"x\":2,\"y\":1,\"value\":2},{\"x\":2,\"y\":5,\"value\":7},{\"x\":2,\"y\":6,\"value\":1},{\"x\":3,\"y\":0,\"value\":3},{\"x\":3,\"y\":3,\"value\":7},{\"x\":3,\"y\":6,\"value\":2},{\"x\":3,\"y\":7,\"value\":9},{\"x\":3,\"y\":8,\"value\":6},{\"x\":4,\"y\":1,\"value\":9},{\"x\":4,\"y\":3,\"value\":6},{\"x\":4,\"y\":4,\"value\":4},{\"x\":4,\"y\":5,\"value\":1},{\"x\":5,\"y\":0,\"value\":7},{\"x\":5,\"y\":1,\"value\":5},{\"x\":5,\"y\":2,\"value\":6},{\"x\":5,\"y\":8,\"value\":8},{\"x\":6,\"y\":0,\"value\":2},{\"x\":6,\"y\":7,\"value\":3},{\"x\":6,\"y\":8,\"value\":7},{\"x\":7,\"y\":1,\"value\":8},{\"x\":7,\"y\":2,\"value\":7},{\"x\":7,\"y\":3,\"value\":4},{\"x\":7,\"y\":6,\"value\":6},{\"x\":7,\"y\":7,\"value\":2},{\"x\":8,\"y\":2,\"value\":5},{\"x\":8,\"y\":3,\"value\":8},{\"x\":8,\"y\":4,\"value\":7},{\"x\":8,\"y\":5,\"value\":2},{\"x\":8,\"y\":6,\"value\":9}]}";

        /*String s = (String) difficulty.getSelectedItem();
        switch (s) {
            case "Easy":
                level = 1;
                break;
            case "Medium":
                level = 2;
                break;
            case "Hard":
                level = 3;
                break;
        }*/
        level = difficulty.getSelectedItemPosition()+1;

        GetSudoku gs = new GetSudoku();
        bar.setVisibility(View.VISIBLE);
        gs.execute();

        setTimer(0);
    }


    /*public void numericClick(View v) {
        String broj = ((Button) v).getText().toString();
        if (selected != null)
            selected.setText(broj);
    }

    public void obrisi(View v) {
        if (selected != null)
            selected.setText("");
    }*/

    public void proveriClick() {
        //if(error != null)
            //error.setTextColor(Color.BLACK);

        removeError();

        if (provera()) {
            Toast.makeText(this, "Uspesno", Toast.LENGTH_SHORT).show();
            timer.cancel();
        }
        else
            Toast.makeText(this, "Neispravno resenje", Toast.LENGTH_SHORT).show();

        //if(error != null)
           // error.setTextColor(Color.RED);
    }

    public boolean provera() {

        for (int x = 0; x < 9; x++)//proverava da li ima praznih polja
            for (int y = 0; y < 9; y++) {
                if (tabla[x][y].getText().toString().equals("")) {
                    Toast.makeText(this, "Nisu popunjena sva polja", Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

        for (int x = 0; x < 9; x++) //proverava kolone
            for (int y = 0; y < 8; y++) {
                String broj = tabla[x][y].getText().toString();

                for (int i = y + 1; i < 9; i++)
                    if (tabla[x][i].getText().toString().equals(broj)) {
                        tabla[x][i].setTextColor(Color.RED);
                        tabla[x][y].setTextColor(Color.RED);
                        return false;
                    }
            }


        for (int y = 0; y < 9; y++) //proverava redove
            for (int x = 0; x < 8; x++) {
                String broj = tabla[x][y].getText().toString();

                for (int i = x + 1; i < 9; i++)
                    if (tabla[i][y].getText().toString().equals(broj)) {
                        tabla[x][i].setTextColor(Color.RED);
                        tabla[x][y].setTextColor(Color.RED);
                        return false;
                    }
            }


        //provera kvadrata 3x3
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)//prolazak kroz 9 kvadrata
            {
                for (int x = 0; x < 3; x++)
                    for (int y = 0; y < 3; y++)//prolazak unutar kvadrata 3x3
                    {
                        String broj = tabla[x + 3 * i][y + 3 * j].getText().toString();

                        for (int xp = 0; xp < 3; xp++)//provera izabranog broja sa svim brojevima unutar tog kvadrata
                            for (int yp = 0; yp < 3; yp++)
                                if ((x != xp || y != yp) && tabla[xp + 3 * i][yp + 3 * j].getText().toString().equals(broj)) {
                                    tabla[xp + 3 * i][yp + 3 * j].setTextColor(Color.RED);
                                    tabla[x + 3 * i][y + 3 * j].setTextColor(Color.RED);
                                    return false;
                                }
                    }

            }

        return true;
    }

    private void removeError(){
        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 9; y++)
                tabla[x][y].setTextColor(Color.BLACK);
    }


    public class GetSudoku extends AsyncTask<String, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {
            try {

                URL url = new URL("http://www.cs.utep.edu/cheon/ws/sudoku/new/?size=9?level=" + level);
                HttpURLConnection konekcija = (HttpURLConnection) url.openConnection();
                konekcija.setRequestMethod("GET");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(konekcija.getInputStream()));

                if (konekcija.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String s = "{\"response\":true,\"size\":\"9?level=3\",\"squares\":[{\"x\":0,\"y\":3,\"value\":1},{\"x\":0,\"y\":4,\"value\":2},{\"x\":0,\"y\":5,\"value\":5},{\"x\":0,\"y\":7,\"value\":7},{\"x\":0,\"y\":8,\"value\":9},{\"x\":1,\"y\":1,\"value\":7},{\"x\":1,\"y\":2,\"value\":1},{\"x\":1,\"y\":5,\"value\":4},{\"x\":1,\"y\":6,\"value\":5},{\"x\":1,\"y\":7,\"value\":8},{\"x\":2,\"y\":0,\"value\":5},{\"x\":2,\"y\":1,\"value\":2},{\"x\":2,\"y\":5,\"value\":7},{\"x\":2,\"y\":6,\"value\":1},{\"x\":3,\"y\":0,\"value\":3},{\"x\":3,\"y\":3,\"value\":7},{\"x\":3,\"y\":6,\"value\":2},{\"x\":3,\"y\":7,\"value\":9},{\"x\":3,\"y\":8,\"value\":6},{\"x\":4,\"y\":1,\"value\":9},{\"x\":4,\"y\":3,\"value\":6},{\"x\":4,\"y\":4,\"value\":4},{\"x\":4,\"y\":5,\"value\":1},{\"x\":5,\"y\":0,\"value\":7},{\"x\":5,\"y\":1,\"value\":5},{\"x\":5,\"y\":2,\"value\":6},{\"x\":5,\"y\":8,\"value\":8},{\"x\":6,\"y\":0,\"value\":2},{\"x\":6,\"y\":7,\"value\":3},{\"x\":6,\"y\":8,\"value\":7},{\"x\":7,\"y\":1,\"value\":8},{\"x\":7,\"y\":2,\"value\":7},{\"x\":7,\"y\":3,\"value\":4},{\"x\":7,\"y\":6,\"value\":6},{\"x\":7,\"y\":7,\"value\":2},{\"x\":8,\"y\":2,\"value\":5},{\"x\":8,\"y\":3,\"value\":8},{\"x\":8,\"y\":4,\"value\":7},{\"x\":8,\"y\":5,\"value\":2},{\"x\":8,\"y\":6,\"value\":9}]}";
                    String response = bufferedReader.readLine();
                    JSONObject object = new JSONObject(s);

                    publishProgress(100);
                    return object;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            if (values[0] == 100)
                bar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onPostExecute(JSONObject object) {
            super.onPostExecute(object);

            clearTable();

            try {
                JSONArray niz = new JSONArray(object.getString("squares"));

                for (int i = 0; i < niz.length(); i++) {
                    JSONObject polje = niz.getJSONObject(i);
                    int x = polje.getInt("x");
                    int y = polje.getInt("y");
                    int value = polje.getInt("value");

                    tabla[x][y].setText(value + "");
                    tabla[x][y].setClickable(false);
                    tabla[x][y].setBackgroundColor(Color.rgb(224, 224, 224));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    public void clearTable() {

        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 9; y++) {
                tabla[x][y].setText("");
                tabla[x][y].setClickable(true);
                tabla[x][y].setBackgroundColor(Color.WHITE);
            }

    }


    public void saveTable() {

        baza.clear();

        for (int x = 0; x < 9; x++)
            for (int y = 0; y < 9; y++) {
                String broj = tabla[x][y].getText().toString();
                if (!broj.equals("")) {
                    Polje polje = new Polje(x, y, broj, !tabla[x][y].isClickable());
                    baza.dodajPolje(polje);
                }
            }

    }

    public void loadTable() {
        LinkedList<Polje> polja = baza.ucitajPolja();

        if (polja.isEmpty())
            return;

        for (Polje p : polja) {
            tabla[p.getX()][p.getY()].setText(p.getBroj());
            if (p.isFixed()) {
                tabla[p.getX()][p.getY()].setClickable(false);
                tabla[p.getX()][p.getY()].setBackgroundColor(Color.rgb(224, 224, 224));
            } else {
                tabla[p.getX()][p.getY()].setClickable(true);
                tabla[p.getX()][p.getY()].setBackgroundColor(Color.WHITE);
            }
        }
    }


    private void setTimer(long time){
        if(timer != null)
            timer.cancel();

        timer = new CountDownTimer(3600000-time, 1000) {

            public void onTick(long millisUntilFinished) {
                long time = (3600000-millisUntilFinished);
                Date vreme = new Date(time);
                textTime.setText(new SimpleDateFormat("mm:ss").format(vreme));
            }

            public void onFinish() {
                textTime.setText("59:59");
            }


        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //long time = preferences.getLong("time",0);
        //setTimer(time);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(igra != null)
            igra.saveTable();

        /*long time = 0;
        SharedPreferences.Editor editor = preferences.edit();
        try {
            time = new SimpleDateFormat("mm:ss").parse(textTime.getText().toString()).getTime();
        } catch (ParseException e) { }

        editor.putLong("time", time);
        editor.apply();*/
    }
}