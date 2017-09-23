package fizzyapps.mygame;

import android.app.Activity;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Game extends Activity {

    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Turn off title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Set to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(new GamePanel(this));

        mediaPlayer = MediaPlayer.create(this, R.raw.main_theme_01);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }
}
