package ismael.t.npi.myapplication;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import pl.droidsonroids.gif.GifImageView;

public class AnimalDetails extends AppCompatActivity implements View.OnClickListener , AIListener ,SensorEventListener{

    // Elementos de la UI
    ImageView animalView, friendship, peculiarity;
    TextView info;
    FloatingActionButton speak;
    GifImageView gif;
    private TextView informacion;

    // Variables para la inicializacion de los servicios de Dialog
    private AIService aiService;
    private TextToSpeech speaker;
    private String Animal_activo;
    private int animal_vista=0;

    // Sensores
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private Sensor acelerometeSensor;

    private long proximityStap = 0, accelerometeStap = 0;
    private long Limite=2500;

    private AIRequest mensaje_dialog;
    private AIDataService datos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_details);

        //Cambiar la barra de estado a transparente
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        animalView = (ImageView) findViewById(R.id.animalimagen);
        friendship = (ImageView) findViewById(R.id.friendship);
        peculiarity = (ImageView) findViewById(R.id.peculiarity);
        speak = (FloatingActionButton) findViewById(R.id.followTalk);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        acelerometeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        //Introducir nuevos valores atravez del Bundle
        Bundle b = getIntent().getExtras();
        final ArrayList<String> palabra = b.getStringArrayList("key");

        int resID_1 = getResources().getIdentifier(palabra.get(0),
                "drawable", getPackageName());

        int resID_2 = getResources().getIdentifier(palabra.get(1),
                "drawable", getPackageName());


        animalView.setImageResource( resID_1);

        informacion=findViewById (R.id.info);
        informacion.setText(palabra.get(2));

        //Inicializacion de las funciones del text to speech y Dialog
        final AIConfiguration config = new AIConfiguration("23e6b35921f14ffeac0dfd9724403d75",
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        datos = new AIDataService( config);
        aiService.setListener(this);
        mensaje_dialog = new AIRequest();


        gif = (GifImageView ) findViewById(R.id.gifImageView);
        gif.setImageResource(R.drawable.ajolotegif);


        speaker = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                speaker.setPitch(2);
                speaker.setSpeechRate(2);
                if (palabra.get(3) !=null) {
                    speaker.speak(palabra.get(3), TextToSpeech.QUEUE_ADD, null, null);
                }
            }
        });

        speak.setOnClickListener(this);

        if(proximitySensor == null) {
            Toast.makeText(this, "Error no tiene sensor de proximidad", Toast.LENGTH_SHORT).show();
            finish(); // Close app
        }

        if(acelerometeSensor == null) {
            Toast.makeText(this, "Error no tiene sensor de acelerometro", Toast.LENGTH_SHORT).show();
            finish(); // Close app
        }

        sensorManager.registerListener( this,proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener( this,acelerometeSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onClick(View view) {

        ChequearPermisoAudio();

        speaker.stop();

        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork  == null) {
            speaker.speak("Por favor conéctate a internet", TextToSpeech.QUEUE_ADD, null, "msg");
        }
        else {
            aiService.startListening();
        }
    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();


        Animal_activo=response.getResult().getStringParameter("Type_of_animal");


        switch ( Animal_activo){
            case "lemur":

                Lemur_llamar_chatbox();

                break;

            case "nutria":

                Nutria_llamar_chatbox();
                break;

            case "anguila jardinera":

                Anguila_llamar_chatbox();
                break;

            case "ajolote":

                Ajolote_llamar_chatbox();
                break;
            case "rana cornuda":

                Rana_cornuda_llamar_chatbox();
                break;

            case "muntjac":

                Mutjac_llamar_chatbox();
                break;
        }



        speaker.speak(result.getFulfillment().getSpeech(), TextToSpeech.QUEUE_FLUSH, null, null);




    }



    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

    @Override
    public void onDestroy() {
        if (speaker.isSpeaking())
            speaker.stop();
        speaker.shutdown();


        super.onDestroy();
    }

    public void ChequearPermisoAudio() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    22);
        }
    }



    public void Lemur_llamar_chatbox() {


        int resID_1 = getResources().getIdentifier("lemur",
                "drawable", getPackageName());


        animalView.setImageResource( resID_1);

        informacion.setText("Es un primate de hábitos estrictamente diurnos que pasa la mayor parte del tiempo en los árboles, aunque también frecuenta el suelo. Es sociable y vive en grupos de 5 a 25 individuos. Es polígamo y usa su característica cola para hacer señales visuales y odoríferas. Cuando camina por el suelo mantiene erguida la cola para señalar su presencia al resto de sus congéneres. También se comunica por vocalizaciones, por actitudes corporales y por expresiones del rostro. Se alimenta de frutos, hojas, flores, cortezas y pequeños insectos.");




    }




    public void Nutria_llamar_chatbox() {


        int resID_1 = getResources().getIdentifier("nutria",
                "drawable", getPackageName());


        animalView.setImageResource( resID_1);


        informacion.setText("Es un animal muy sociable que vive en grupos familiares. Es monógama. Dedica gran parte del día a jugar y comunicarse entre ellas. Se han identificado hasta doce vocalizaciones diferentes además de señales visuales, hormonales y táctiles, como el acicalamiento social. Tiene gran agilidad en sus manos que las utiliza para cazar cangrejos, moluscos y peces por el tacto, levantar piedras o construir madrigueras en las orillas de los ríos.");


    }



    public void Anguila_llamar_chatbox() {


        int resID_1 = getResources().getIdentifier("anguila",
                "drawable", getPackageName());


        animalView.setImageResource( resID_1);



        informacion.setText("Pez anguiliforme sin escamas. Vive semienterrado en la arena del arrecife formando colonias donde se esconde a la mínima señal de peligro. Es pacífico y tímido. Se alimenta de zooplancton. Las larvas son planctónicos hasta que alcanzan el tamaño suficiente para hacer su madriguera");

    }




    public void Ajolote_llamar_chatbox() {


        int resID_1 = getResources().getIdentifier("ajolote",
                "drawable", getPackageName());


        animalView.setImageResource( resID_1);

        informacion.setText("Salamandra endémica de algunas lagunas mejicanas. Su desarrollo es muy peculiar ya que alcanza el estado adulto sin terminar la metamorfosis, es decir, mantiene la forma larvaria durante toda su vida. Tiene hábitos nocturnos. Se alimenta de invertebrados.");

    }



    public void Rana_cornuda_llamar_chatbox( ) {
        int resID_1 = getResources().getIdentifier("ranacornuda",
                "drawable", getPackageName());


        animalView.setImageResource( resID_1);

        informacion.setText("Rana robusta que se oculta entre las hojas muertas del suelo de los bosques húmedos utilizando su coloración críptica y apariencia de hoja. Sus ataques son explosivos. Salta sobre la presa y de inmediato la captura y engulle. Es muy voraz. Captura insectos y arácnidos.");

    }



    public void Mutjac_llamar_chatbox( ) {
        int resID_1 = getResources().getIdentifier("muntjac",
                "drawable", getPackageName());


        animalView.setImageResource( resID_1);

        informacion.setText("Pequeño ciervo de bosque que se mueve con agilidad entre la espesura de la jungla. El macho posee una pequeña cornamenta y colmillos que sobresalen del maxilar superior y la hembra una protuberancia con un mechón de pelo. Es de hábitos solitarios. Come hojas, frutas y pequeños animales.");


    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            ProximityChanged(event);
        }

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            AccelerometeChanged(event);
        }

    }



    public void AccelerometeChanged(SensorEvent event){
        long now = System.currentTimeMillis();

        if ((now - accelerometeStap) > Limite){
            accelerometeStap = now;


            if (event.values[0] > 10 ||
                event.values[1] > 10 ||
                event.values[2] > 10||
                event.values[0] < -10 ||
                event.values[1] < -10 ||
                event.values[2] < -10){

                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();

            }
        }


    }


    public  void CambioAnimal(String animal){

        mensaje_dialog.setQuery(animal);

        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {

                try {
                    final AIResponse response = datos.request(mensaje_dialog);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }
            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {

                }
            }
        }.execute(mensaje_dialog);

    }


    public void ProximityChanged(SensorEvent event){
        long now = System.currentTimeMillis();
        if((now - proximityStap) >Limite) {
            if (event.values[0] < proximitySensor.getMaximumRange()) {

                switch (animal_vista){
                    case 0:

                        Lemur_llamar_chatbox();
                        CambioAnimal("lemur");

                        break;
                   case 1:

                        Nutria_llamar_chatbox();
                        CambioAnimal("nutrias");
                        break;

                    case 2:

                        Anguila_llamar_chatbox();
                        CambioAnimal("anguila jardinera");
                        break;

                    case 3:

                        Ajolote_llamar_chatbox();
                        CambioAnimal("ajolote");
                        break;
                    case 4:

                        Rana_cornuda_llamar_chatbox();
                        CambioAnimal("rana cornuda");
                        break;

                    case 5:

                        Mutjac_llamar_chatbox();
                        CambioAnimal("muntjac");
                        break;
                }
                animal_vista+=1;

                if (animal_vista==6){
                    animal_vista=0;
                }



            }
            proximityStap=now;

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        gif.setImageResource(R.drawable.ajolotegif);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, acelerometeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (speaker.isSpeaking())
            speaker.stop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Codigo cancelado", Toast.LENGTH_SHORT).show();
            } else {

                switch ( result.getContents()){
                    case "lemur":

                        Lemur_llamar_chatbox();
                        animal_vista=0;
                        CambioAnimal("lemur");
                        break;

                    case "nutria":

                        Nutria_llamar_chatbox();
                        CambioAnimal("nutrias");
                        animal_vista=1;
                        break;

                    case "anguila jardinera":

                        Anguila_llamar_chatbox();
                        animal_vista=2;
                        CambioAnimal("anguila jardinera");
                        break;

                    case "ajolote":

                        Ajolote_llamar_chatbox();
                        CambioAnimal("ajolote");
                        animal_vista=3;
                        break;
                    case "rana cornuda":

                        Rana_cornuda_llamar_chatbox();
                        CambioAnimal("rana cornuda");
                        animal_vista=4;
                        break;

                    case "muntjac":

                        Mutjac_llamar_chatbox();
                        CambioAnimal("muntjac");
                        animal_vista=5;
                        break;
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }
}
