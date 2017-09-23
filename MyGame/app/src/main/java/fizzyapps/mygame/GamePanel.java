package fizzyapps.mygame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Wes on 4/27/2017.
 */

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback
{

    public static final int WIDTH = 960;
    public static final int HEIGHT = 540;
    public static final int MOVESPEED = -5;
    private long missileStartTime;
    private long demonStartTime;
    private MainThread thread;
    private Background bg;
    private Player player;
    private Demon demon;
    private ArrayList<Missile> missiles;
    private Random rand = new Random();
    private boolean newGameCreated;

    public GamePanel(Context context){
        super(context);

        //add the callback to the surfaceholder to intercept events
        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        //make gamePanel,
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){
            //Place holder for now
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){

        boolean retry = true;
        int counter = 0;
        while(retry && counter < 1000){
            counter++;
            try{thread.setRunning(false);
                thread.join();
                retry = false;
            }catch(InterruptedException e){e.printStackTrace();}
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){

        bg = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.snowbgv2));
        player = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.mon3_sprite_base), 60, 36, 5);
        missiles = new ArrayList<>();
        missileStartTime = System.nanoTime();
        demonStartTime = System.nanoTime();

        //We can safely start the game loop
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(!player.getPlaying()){
                player.setPlaying(true);
                player.setUp(true);
            }else{
                player.setUp(true);
            }
            return true;
        }

        if(event.getAction() == MotionEvent.ACTION_UP){
            player.setUp(false);
            return true;
        }

        return super.onTouchEvent(event);
    }

    //Updates game each iteration after initial creation
    public void update(){

        if(player.getPlaying()){
            bg.update();
            player.update();


            //Check for borders
            if(player.getY() >= HEIGHT || player.getY() <= 0){
                player.setPlaying(false);
            }

            long missilesElapsed = (System.nanoTime() - missileStartTime) / 1000000;
            long demonElapsed = (System.nanoTime() - demonStartTime) / 1000000;

            if(demonElapsed >(4000 - player.getScore()/4)){}

            //Controls missile spawn time
            if(missilesElapsed >(1500 - player.getScore()/4)){

                missiles.add(new Missile(BitmapFactory.decodeResource(getResources(),R.drawable.missile), WIDTH + 10,
                            (int)(rand.nextDouble()*HEIGHT),45, 15, player.getScore(), 13));

                missileStartTime = System.nanoTime();
            }

            //Loops through each missiles in array to check for collisions
            for(int i = 0; i < missiles.size(); i++){
                missiles.get(i).update();
                if(collision(missiles.get(i),player)){
                    missiles.remove(i);
                    player.setPlaying(false);
                    break;
                }

                //Removes missiles as they get off the screen
                if(missiles.get(i).getX()<-100){
                    missiles.remove(i);
                    break;
                }
            }
        }else{

            newGameCreated = false;
            if(!newGameCreated)
            newGame();
        }
    }

    public boolean collision(GameObject a, GameObject b){

        if(Rect.intersects(a.getRectangle(),b.getRectangle())){
            return true;
        }
        return false;
    }

    @Override
    public void draw(Canvas canvas){
        final float scaleFactorX = getWidth()/(WIDTH*1.f);
        final float scaleFactorY = getHeight()/(HEIGHT*1.f);

        if(canvas != null) {

            final int savedState = canvas.save();
            canvas.scale(scaleFactorX,scaleFactorY);
            bg.draw(canvas);
            player.draw(canvas);

            for(Missile m: missiles){

                m.draw(canvas);
            }

            canvas.restoreToCount(savedState);
        }
    }

    public void newGame(){

        missiles.clear();
        player.resetDY();
        player.resetScore();
        player.setY(HEIGHT/2);

        newGameCreated = true;
        }


}
