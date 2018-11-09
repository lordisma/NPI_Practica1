package ismael.t.npi.myapplication;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;



public class Mapa extends AppCompatActivity  implements ScaleGestureDetector.OnScaleGestureListener {

    //Componentes de la vista
    ImageView fotomapa;

    // Componentes del Escalado
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;

    /**
     * @brief Creamos la actividad y inicializamos la imagen
     * @param savedInstanceState información para crear la actividad
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        //Cambiar la barra de estado a transparente
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        fotomapa = findViewById(R.id.mapa);
        fotomapa.setImageResource(R.drawable.mapa);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    /**
     * @brief Cuando tocamos con 2 dedos la pantalla llama al reescalado
     */

    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);

        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return false;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    /**
     * @brief Sub_Clase para proceder al Reescalado de la imgen
     *        Funcion que se encarga de reescalar la imagen en base a unos límites
     */

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.7f, Math.min(mScaleFactor, 2.0f));
            fotomapa.setScaleX(mScaleFactor);
            fotomapa.setScaleY(mScaleFactor);

            return true;
        }
    }

}
