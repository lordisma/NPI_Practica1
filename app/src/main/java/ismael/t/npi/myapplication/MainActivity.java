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

     // Componentes de la vista
     LinearLayout logolayaout, forest;
     ImageView logoimg;
     Animation uptodown,downtoup;
     CountDownTimer timer;

    /**
     * @brief Método que inicializa la primera pantalla de aplición y al pasar el
              tiempo del timer pasa a la siguiente.
     @param savedInstanceState información para crear la actividad
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicialización de los componentes de la vista y timer
        logoimg = findViewById(R.id.imagenlogo);
        logolayaout = (LinearLayout) findViewById(R.id.logologin);
        forest = (LinearLayout) findViewById(R.id.forestlogin);

        uptodown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downtoup = AnimationUtils.loadAnimation(this,R.anim.downtoup);
        logolayaout.setAnimation(uptodown);
        forest.setAnimation(downtoup);

        //Cambiar la barra de estado a transparente
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // evento para el cambio de actividad
        timer = new CountDownTimer(1600, 10000) {
            @Override
            public void onTick(long l) { }

            public void onFinish() { Start(); }
        };

    }

    /**
     * @brief Funcion que inicializa  la siguiente pantalla
     */

    protected void Start(){
        Intent prin_activity = new Intent(this, principal.class);
        startActivity(prin_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        timer.start();
    }

    @Override
    protected void onPause(){
        super.onPause();
        timer.cancel();
    }
}
