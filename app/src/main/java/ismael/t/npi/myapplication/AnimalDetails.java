package ismael.t.npi.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.tts.TextToSpeech;
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

import java.util.ArrayList;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class AnimalDetails extends AppCompatActivity implements View.OnClickListener , AIListener {

    // Elementos de la UI
    ImageView animalView, friendship, peculiarity;
    TextView info;
    FloatingActionButton speak;
    private TextView informacion;

    // Variables para la inicializacion de los servicios de Dialog
    private AIService aiService;
    private TextToSpeech speaker;
    private String Animal_activo;

    // Sensores
    private final SensorManager sManager;
    private final Sensor sGyroscope;



    public AnimalDetails(){
        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sGyroscope = sManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

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
        aiService.setListener(this);

        speaker = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                speaker.setPitch(2);
                speaker.setSpeechRate(2);
                speaker.speak(palabra.get(3), TextToSpeech.QUEUE_ADD, null, null);

            }
        });

        speak.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
       // Toast.makeText(this, "Aqui iria dialog", Toast.LENGTH_SHORT).show();
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


}
