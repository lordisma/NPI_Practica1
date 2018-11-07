package ismael.t.npi.myapplication;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import android.view.ViewGroup;
import android.widget.ImageView;



public class Mapa extends AppCompatActivity {

    ImageView fotomapa;

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);

        fotomapa = findViewById(R.id.mapa);
        fotomapa.setImageResource(R.drawable.mapa);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            fotomapa.setScaleX(mScaleFactor);
            fotomapa.setScaleY(mScaleFactor);

            return true;
        }
    }


    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
}
