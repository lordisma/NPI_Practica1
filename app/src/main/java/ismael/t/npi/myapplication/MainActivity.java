package ismael.t.npi.myapplication;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

     LinearLayout logolayaout, forest;
     ImageView logoimg;
     Animation uptodown,downtoup;
     CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logoimg = findViewById(R.id.imagenlogo);
        logolayaout = (LinearLayout) findViewById(R.id.logologin);
        forest = (LinearLayout) findViewById(R.id.forestlogin);

        logoimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Start();

            }
        });

        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        logolayaout.setAnimation(uptodown);
        forest.setAnimation(downtoup);

        //Cambiar la barra de estado a transparente
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        timer = new CountDownTimer(2000, 10000) {
            @Override
            public void onTick(long l) { }

            public void onFinish() { Start(); }
        };

        timer.start();
    }

    protected void Start(){
        Intent prin_activity = new Intent(this, principal.class);
        startActivity(prin_activity);
    }
}
