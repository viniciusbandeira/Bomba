package bandeira.bomba;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class TutorialActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;

    private ImageButton startTutorialIB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        startTutorialIB = (ImageButton) findViewById(R.id.start_tutorial_ib);
        startTutorialIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playInstructions();
            }
        });

        ImageButton skipTutorialB = (ImageButton) findViewById(R.id.skip_ib);
        skipTutorialB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }

                startGame();
            }
        });
    }

    private void playInstructions() {
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            startTutorialIB.setImageResource(R.drawable.play);
        } else {
            mediaPlayer = MediaPlayer.create(TutorialActivity.this, R.raw.tutorial);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    startTutorialIB.setImageResource(R.drawable.play);
                    mediaPlayer.release();
                    showAlert();
                }
            });
            mediaPlayer.start();
            isPlaying = true;
            startTutorialIB.setImageResource(R.drawable.stop);
        }
    }

    private void showAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(TutorialActivity.this);
        builder.setMessage("O jogo vai começar.");
        builder.setPositiveButton("INICIAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startGame();
            }
        });
        builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startGame() {
        Intent intent = new Intent(TutorialActivity.this, DefuseActivity.class);
        startActivity(intent);
        finish();
    }
}
