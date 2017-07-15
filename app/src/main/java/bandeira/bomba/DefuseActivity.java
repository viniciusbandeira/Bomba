package bandeira.bomba;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DefuseActivity extends AppCompatActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private final long FIFTEEN_MINUTES = 900000;
    @SuppressWarnings("FieldCanBeLocal")
    private final long ONE_SECOND = 1000;
    @SuppressWarnings("FieldCanBeLocal")
    private final int MAX_ATTEMPTS = 3;
    @SuppressWarnings("FieldCanBeLocal")
    private final String PASSWORD = "2833";
    @SuppressWarnings("FieldCanBeLocal")
    private final String FORMAT = "%02d:%02d";

    private int wrongGuesses = 0;
    private ArrayList<ImageView> guessesCounter;
    private MediaPlayer mediaPlayer;
    private CountDownTimer timer;

    private TextView timeTV;
    private TextView restartGameTV;
    private ImageView firstMissIV;
    private ImageView secondMissTV;
    private ImageView thirdMissTV;
    private EditText passwordET;
    private GridLayout keyboardGL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_defuse);

        getViews();
        configureKeyboard();
        configureViews();
        playMusic(R.raw.soundtrack);

        guessesCounter = new ArrayList<>();
        guessesCounter.add(firstMissIV);
        guessesCounter.add(secondMissTV);
        guessesCounter.add(thirdMissTV);

        timer = new CountDownTimer(FIFTEEN_MINUTES, ONE_SECOND) {
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

    @Override
    public void onBackPressed() {

    }

    private void getViews() {
        timeTV = (TextView) findViewById(R.id.time_tv);
        restartGameTV = (TextView) findViewById(R.id.reset_tv);
        firstMissIV = (ImageView) findViewById(R.id.first_miss_iv);
        secondMissTV = (ImageView) findViewById(R.id.second_miss_iv);
        thirdMissTV = (ImageView) findViewById(R.id.third_miss_iv);
        passwordET = (EditText) findViewById(R.id.password_et);
        keyboardGL = (GridLayout) findViewById(R.id.keyboard_gl);
    }

    private void configureKeyboard() {
        final String[] buttonsValue = getResources().getStringArray(R.array.keyboard_values);

        for (int i = 0; i < buttonsValue.length; i++) {
            final int j = i;
            keyboardGL.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    passwordET.append(buttonsValue[j]);
                }
            });
        }

        keyboardGL.getChildAt(keyboardGL.getChildCount() - 2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int length = passwordET.getText().length();
                if (length > 0) {
                    passwordET.getText().delete(length - 1, length);
                }
            }
        });

        keyboardGL.getChildAt(keyboardGL.getChildCount() - 1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPassword();
            }
        });
    }

    private void configureViews() {
        Typeface displayFont = Typeface.createFromAsset(getAssets(), "font/open-24-display-st.ttf");

        restartGameTV.setTypeface(displayFont);
        restartGameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DefuseActivity.this, TutorialActivity.class);
                startActivity(intent);
                finish();
            }
        });

        keyboardGL.getChildAt(keyboardGL.getChildCount() - 1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPassword();
            }
        });

        timeTV.setTypeface(displayFont);
        passwordET.setTypeface(displayFont);
    }

    public void playMusic(int resouceId) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        mediaPlayer = MediaPlayer.create(DefuseActivity.this, resouceId);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mediaPlayer.release();
            }
        });
        mediaPlayer.start();
    }

    private void checkPassword() {
        if (passwordET.getText().toString().equals(PASSWORD)) {
            keyboardGL.setBackgroundResource(R.drawable.green_keyboard_light);
            timer.cancel();
            mediaPlayer.stop();
            mediaPlayer.release();
            showVictoryDialog();
            restartGameTV.setVisibility(View.VISIBLE);
            keyboardGL.getChildAt(keyboardGL.getChildCount() - 1).setEnabled(false);
        } else {
            wrongGuesses++;
            displayWrongGuesses(wrongGuesses);

            if (wrongGuesses == MAX_ATTEMPTS) {
                keyboardGL.setBackgroundResource(R.drawable.red_keyboard_light);
                timer.cancel();
                playMusic(R.raw.explosion);
                showDefeatDialog();
                restartGameTV.setVisibility(View.VISIBLE);
                keyboardGL.getChildAt(keyboardGL.getChildCount() - 1).setEnabled(false);
            }
        }
    }

    private void displayWrongGuesses(int guesses) {
        for (int i = 0; i < guesses && i < 3; i++) {
            guessesCounter.get(i).setImageResource(R.drawable.light_on);
        }
    }

    private void showVictoryDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(DefuseActivity.this);
        builder.setTitle("Parabéns!!!");
        builder.setMessage("Você conseguiu desarmar a bomba.\nContate o responsável pela sala.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDefeatDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(DefuseActivity.this);
        builder.setTitle("Derrota!!!");
        builder.setMessage("Você não conseguiur desarmar a bomba à tempo.\nContate o responsável pela sala.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
