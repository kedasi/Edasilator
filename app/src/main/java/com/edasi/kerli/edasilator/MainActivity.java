package com.edasi.kerli.edasilator;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.HorizontalScrollView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;


public class MainActivity extends AppCompatActivity {
    private int[] numBtns = {R.id.button0, R.id.button1, R.id.button2, R.id.button3, R.id.button4,
            R.id.button5, R.id.button6, R.id.button7, R.id.button8, R.id.button9};
    private int[] opBtns = {R.id.buttonDel, R.id.buttonDiv, R.id.buttonDot, R.id.buttonAdd, R.id.buttonSub,
            R.id.buttonEquals, R.id.buttonMult};
    private TextView calcField;
    private boolean lastNum; //viimane number
    private boolean stateErr; //ekraaniseis, kas ekraanil on midagi
    private boolean lastDot; // viimane punkt
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreate called");
        }
        //lisan textview-le custom fonti
        calcField = (TextView) findViewById(R.id.textViewCalc);
        Typeface tf = Typeface.createFromAsset(getAssets(), "digital-7.ttf");
        calcField.setTypeface(tf);

        calcField.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);

        if (savedInstanceState != null) {
            calcField.setText( savedInstanceState.getString("calcV", calcField.toString()));
        }


        calcField.setHorizontallyScrolling(true);
        calcField.setMaxLines(1);
        calcField.setMarqueeRepeatLimit(-1);
        calcField.setSelected(true);

        setNumOnClickListener();
        setOpOnClickListener();
    }



    private void setNumOnClickListener() {

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button btn = (Button) view;

                if (stateErr) { //kui pole midagi kirjutatud
                    calcField.setText(btn.getText()); //muuda buttoni tekst ekraanitekstiks
                    stateErr = false; //ekraaniseis täidetud
                } else {  //kui ekraanile on midagi kirjutatud
                    calcField.setGravity(Gravity.BOTTOM | Gravity.RIGHT);

                    calcField.append(btn.getText()); //lisa ekraanil olevale tekstile buttoni sisu juurde
                }
                lastNum = true; //ekraanil viimane sisestus on number

            }
        };
        for (int x : numBtns) { //käin läbi numbrite listi
            findViewById(x).setOnClickListener(listener); //lisan klikitavuse kõikidele nuppudele
        }
    }

    private void setOpOnClickListener() {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastNum && !stateErr) { //kui ekraanil on number ja ekraanile on midagi kirjutatud
                    Button btn = (Button) v;
                    calcField.append(btn.getText()); //lisa ekraanile buttoni sisu
                    lastNum = false; //enam pole viimane sisestus number
                    lastDot = false; //numbris pole punkti
                } 
            }
        };
        for (int x : opBtns) {
            findViewById(x).setOnClickListener(listener);
        }
        findViewById(R.id.buttonDot).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastNum && !stateErr && !lastDot) { //kui viimane sisestus on number, ekraanil pole tühi ja viimane sisestus pole punkt
                    calcField.append("."); //lisa ekraanile punkt
                    lastNum = false; //viimane sisestus pole number
                    lastDot = true; //numbris on punkt
                }
            }
        });
        findViewById(R.id.buttonDel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calcField.setText("");  // muuda sisu tühjaks
                lastNum = false; //viimane sisestus pole number
                stateErr = false;
                lastDot = false;
            }
        });

        findViewById(R.id.buttonEquals).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEqual();
            }
        });
    }

    private void onEqual() {
        //kui viimane sisestus on arv ja ekraan pole tühi
        if (lastNum && !stateErr) {
            //võta ekraanil olev tekst ja muudan stringiks
            String txt = calcField.getText().toString();
            Expression expression = new ExpressionBuilder(txt).build();
            try {
                //arvutan vastuse ja näitan ekraanile
                double result = expression.evaluate();
                calcField.setText(Double.toString(result));
                lastDot = true; //numbris on punkt
               // calcField.setTextAlignment();

            } catch (ArithmeticException ex) {
                // kui on vale sisend
                calcField.setText("Error");
                stateErr = true;
                lastNum = false;
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        String txt = calcField.getText().toString();
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("saveTxt", txt);

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onSave called" + txt);
        }
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        calcField.setText(savedInstanceState.getString("saveTxt"));
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onRestore called, got: " + savedInstanceState.getString("saveTxt"));
        }
    }
}
