package bandeira.bomba;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DefuseActivity extends AppCompatActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private final long FIVE_MINUTES = 300000;
    @SuppressWarnings("FieldCanBeLocal")
    private final long ONE_SECOND = 1000;
    @SuppressWarnings("FieldCanBeLocal")
    private final int MAX_ATTEMPTS = 3;
    private final String FORMAT = "%02d:%02d";

    private String passsword;
    private int wrongGuesses;
    private ArrayList<ImageView> guessesCounter;

    private TextView timeTV;
    private ImageView firstMissIV;
    private ImageView secondMissTV;
    private ImageView thirdMissTV;
    private EditText passwordET;
    private ImageButton checkPasswordB;
    private GridLayout keyboardGL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defuse);

        passsword = "1234";
        wrongGuesses = 0;

        getViews();
        createKeyboard();
        configureViews();

        guessesCounter = new ArrayList<>();
        guessesCounter.add(firstMissIV);
        guessesCounter.add(secondMissTV);
        guessesCounter.add(thirdMissTV);

        new CountDownTimer(FIVE_MINUTES, ONE_SECOND) {
            @Override
            public void onTick(long l) {
                String time = String.format(Locale.getDefault(),
                        FORMAT,
                        TimeUnit.MILLISECONDS.toMinutes(l),
                        TimeUnit.MILLISECONDS.toSeconds(l) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(l)));

                timeTV.setText(time);
            }

            @Override
            public void onFinish() {
                Toast.makeText(DefuseActivity.this, "Xablau!! Você morreu! :(", Toast.LENGTH_SHORT).show();
            }
        }.start();
    }

    private void getViews() {
        timeTV = (TextView) findViewById(R.id.time_tv);
        firstMissIV = (ImageView) findViewById(R.id.first_miss_iv);
        secondMissTV = (ImageView) findViewById(R.id.second_miss_iv);
        thirdMissTV = (ImageView) findViewById(R.id.third_miss_iv);
        passwordET = (EditText) findViewById(R.id.password_et);
        checkPasswordB = (ImageButton) findViewById(R.id.check_b);
        keyboardGL = (GridLayout) findViewById(R.id.keyboard_gl);
    }

    private void createKeyboard() {
        String[] buttonsText = getResources().getStringArray(R.array.keyboard);

        for (final String buttonText : buttonsText) {
            Button button = new Button(DefuseActivity.this);
            button.setText(buttonText);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DefuseActivity.this, buttonText, Toast.LENGTH_SHORT).show();
                }
            });

            keyboardGL.addView(button);
        }
    }

    private void configureViews() {
        checkPasswordB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPassword();
            }
        });
    }

    private void checkPassword() {
        if (passwordET.getText().toString().equals(passsword)) {
            Toast.makeText(DefuseActivity.this, "Parabéns! Você acertou", Toast.LENGTH_SHORT).show();
        } else {
            wrongGuesses++;
            displayWrongGuesses(wrongGuesses);

            if (wrongGuesses == MAX_ATTEMPTS) {
                checkPasswordB.setEnabled(false);
                Toast.makeText(DefuseActivity.this, "Você Perdeu", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayWrongGuesses(int guesses) {
        for (int i = 0; i < guesses; i++) {
            guessesCounter.get(i).setImageResource(R.drawable.wrong_guess);
        }
    }
}
