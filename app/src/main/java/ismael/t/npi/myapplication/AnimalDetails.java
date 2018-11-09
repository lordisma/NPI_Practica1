package ismael.t.npi.myapplication;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
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
import android.view.MotionEvent;
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
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import pl.droidsonroids.gif.GifImageView;

public class AnimalDetails extends AppCompatActivity implements View.OnClickListener , AIListener ,SensorEventListener{

    // Elementos de la UI
    ImageView animalView, animalTags;
    TextView info;
    FloatingActionButton speak;
    GifImageView gif;
    private TextView informacion;
    private boolean ampliation ;
    private Animal currentAnimal;
    private String Animal_activo;

    // Variables para la inicializacion de los servicios de Dialog
    private AIService aiService;
    private TextToSpeech speaker;

    // Sensores
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private Sensor acelerometeSensor;

    private long proximityStap = 0, accelerometeStap = 0;
    private long Limite=1500;

    // Dialog
    private AIRequest mensaje_dialog;
    private AIDataService datos;

    // Mapa
    public  boolean mapa =false;


    /**
     * @brief Creación de la actividad
     * @param savedInstanceState información para crear la actividad
     * @return
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_details);

        ampliation = true;
        mapa=false;

        //Cambiar la barra de estado a transparente
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        animalView = (ImageView) findViewById(R.id.animalimagen);
        animalTags = (ImageView) findViewById(R.id.etiquetas);
        gif = ( GifImageView ) findViewById(R.id.gifImageView);
        informacion = (TextView) findViewById (R.id.info);

        gif.setImageResource(R.drawable.ajolotegif);

        //Introducir nuevos valores atravez del Bundle
        Bundle b = getIntent().getExtras();
        final ArrayList<String> palabra = b.getStringArrayList("key");

        currentAnimal = new Animal(palabra, Integer.parseInt(palabra.get(4)));
        UpdateAnimal();
       // CambioAnimal(currentAnimal.getName());

        //Inicializacion de Dialog y el TextToSpeech
        if (palabra.get(3) != null) {
            AInitializer(palabra.get(3));
        }else{
            Toast.makeText(this,"Un error ha ocurrido con DialogFlow", Toast.LENGTH_LONG);
            finish();
        }

        //Inicializamos los Sensores
        SensorInitializer();

        //Animacion de la imagen principal
        animalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimaImagen(view);
            }
        });
    }

    ////////////////////////PRIVADOS/////////////////////////////////

    /**
     * @brief Función para inicializar los sensores que controlamos
     */

    private void SensorInitializer(){
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        acelerometeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        if(proximitySensor == null || acelerometeSensor == null) {
            Toast.makeText(this, "Error no tiene sensor de proximidad o el acelerometro", Toast.LENGTH_SHORT).show();
            finish(); // Close app
        }

        sensorManager.registerListener( this,proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener( this,acelerometeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * @brief Fución que inicializa el dialogflow y el texttospeak
     * @param palabraBundle Frase inicial del texttospeak
     * @return
     */

    private void AInitializer(final String palabraBundle){
        //Inicializacion de las funciones del text to speech y Dialog
        final AIConfiguration config = new AIConfiguration("23e6b35921f14ffeac0dfd9724403d75",
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);
        aiService = AIService.getService(this, config);
        datos = new AIDataService( config);
        aiService.setListener(this);
        mensaje_dialog = new AIRequest();

        speaker = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                speaker.setPitch(2);
                speaker.setSpeechRate(2);
                speaker.speak(palabraBundle, TextToSpeech.QUEUE_ADD, null, null);

            }
        });

        gif.setOnClickListener(this);
    }

    /**
     * @brief Fución que gestiona la animación de la vista
     * @param view vista
     * @return
     */

    private void AnimaImagen(View view){
        ObjectAnimator scaleDownY,moveDownY;
        AnimatorSet scaleDown = new AnimatorSet();

        if(ampliation) {
            moveDownY = ObjectAnimator.ofFloat(view, "translationY", 115f);
            scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.5f);
            moveDownY.setDuration(100);
            scaleDownY.setDuration(100);
            scaleDown.play(scaleDownY).with(moveDownY);
            ampliation = false;
        }else{
            moveDownY = ObjectAnimator.ofFloat(view, "translationY", 0f);
            scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f);
            moveDownY.setDuration(100);
            scaleDownY.setDuration(100);
            scaleDown.play(scaleDownY).with(moveDownY);
            ampliation = true;
        }

        scaleDown.start();
    }

    /**
     * @brief Función que crea un objeto de la clase animal
     *        y actualiza la actividad junto con el animal activo
     * @param animal Animal
     * @return
     */

    private void ModifyAnimal(String animal){

        switch ( animal){
            case "lemur":

                ChangeAnimal(  new Animal(   new ArrayList<String>() {{
                    add("lemur");
                    add("lemuretiquetas");
                    add("Es un primate de hábitos estrictamente diurnos que pasa la mayor parte del tiempo en los árboles," +
                            " aunque también frecuenta el suelo. Es sociable y vive en grupos de 5 a 25 individuos. " +
                            "Es polígamo y usa su característica cola para hacer señales visuales y odoríferas. Cuando " +
                            "camina por el suelo mantiene erguida la cola para señalar su presencia al resto de sus congéneres. " +
                            "También se comunica por vocalizaciones, por actitudes corporales y por expresiones del rostro. Se " +
                            "alimenta de frutos, hojas, flores, cortezas y pequeños insectos.");   }}
                        , 1)
                );

                break;

            case "nutria":

                ChangeAnimal(  new Animal(   new ArrayList<String>() {{
                    add("nutria");
                    add("nutriaetiquetas");
                    add(    " Es un animal muy sociable que vive en grupos familiares. Es monógama. Dedica gran parte del día a jugar y comunicarse entre ellas." +
                            " Se han identificado hasta doce vocalizaciones diferentes además de señales visuales, hormonales y táctiles, como el acicalamiento" +
                            " social. Tiene gran agilidad en sus manos que las utiliza para cazar cangrejos, moluscos y peces por el tacto, levantar piedras o" +
                            " construir madrigueras en las orillas de los ríos." );   }}
                        , 2)
                );
                break;

            case "anguila jardinera":

                ChangeAnimal(  new Animal(   new ArrayList<String>() {{
                    add("anguila");
                    add("angilaetiquetas");
                    add(    " Pez anguiliforme sin escamas. Vive semienterrado en la arena del arrecife formando colonias donde se esconde a la mínima señal de peligro." +
                            " Es pacífico y tímido. Se alimenta de zooplancton. Las larvas son planctónicos hasta que alcanzan el tamaño suficiente" +
                            " para hacer su madriguera");   }}
                        , 3)
                );
                break;

            case "ajolote":

                ChangeAnimal(  new Animal(   new ArrayList<String>() {{
                    add("ajolote");
                    add("ajoloteetiquetas");
                    add(    " Salamandra endémica de algunas lagunas mejicanas. Su desarrollo es muy peculiar ya que alcanza el estado adulto sin terminar la" +
                            " metamorfosis, es decir, mantiene la forma larvaria durante toda su vida. Tiene hábitos nocturnos. Se alimenta de invertebrados.");   }}
                        , 4)
                );
                break;
            case "rana cornuda":

                ChangeAnimal(  new Animal(   new ArrayList<String>() {{
                    add("ranacornuda");
                    add("ranacornudaetiquetas");
                    add(    " Rana robusta que se oculta entre las hojas muertas del suelo de los bosques húmedos utilizando su coloración críptica y apariencia de hoja." +
                            " Sus ataques son explosivos. Salta sobre la presa y de inmediato la captura y engulle. Es muy voraz. Captura insectos y arácnidos.");   }}
                        , 5)
                );
                break;

            case "muntjac":

                ChangeAnimal(  new Animal(   new ArrayList<String>() {{
                    add("muntjac");
                    add("muntjacetiquetas");
                    add(    " Pequeño ciervo de bosque que se mueve con agilidad entre la espesura de la jungla. El macho posee una pequeña cornamenta y colmillos que" +
                            " sobresalen del maxilar superior y la hembra una protuberancia con un mechón de pelo. Es de hábitos solitarios. " +
                            " Come hojas, frutas y pequeños animales.");   }}
                        , 0)
                );
                break;
        }


    }

    ///////////////////////PROTECTED////////////////////////////////

    /**
     * @brief Función que reactiva la actividad y los sensores
     */

    @Override
    protected void onResume() {
        super.onResume();
        gif.setImageResource(R.drawable.ajolotegif);
        sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, acelerometeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * @brief Función del segundo plano de la aplicación
     */

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (speaker.isSpeaking())
            speaker.stop();
        mapa=false;
    }

    /**@brief  Función que recoge el contenido del QR (en nuestro caso solo texto) y en caso de
     *         ser uno de los animales disponibles ejecuta la nueva actividad.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Codigo cancelado", Toast.LENGTH_SHORT).show();
            } else {

                ModifyAnimal(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    /**
     * @brief Fución que actualiza la vista de la actividad
     */

    protected void UpdateAnimal(){
        int resID_1 = getResources().getIdentifier(currentAnimal.getName(),
                "drawable", getPackageName());
        int resID_2 = getResources().getIdentifier(currentAnimal.getImage(),
                "drawable", getPackageName());


        animalView.setImageResource( resID_1 );
        animalTags.setImageResource( resID_2 );

        informacion.setText(currentAnimal.getInfo());

    }

    /**
     * @brief Función que cambia el animal activo y actualiza la vista
     * @param newAnimal Animal
     * @return
     */

    protected void ChangeAnimal(Animal newAnimal){
        currentAnimal.Copy(newAnimal);
        UpdateAnimal();
        CambioAnimal(currentAnimal.getName());
    }

    ////////////////////////////PUBLICOS////////////////////////////////////////

    /**
     * @brief Función que se lanza a la hora de hablar la cual chequea los permisos,
     *        chequear la conexión a internet y en caso de tenerlo
     *        graba un mensaje de audio y se lo envia dialogflow
     * @param view vista
     */

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

    /**@brief Método para reconocer los eventos que ocurren en la aplicacion
     *        en nuestro caso que se pulse con más de 2 dedos la pantalla y se muevan
     * @param event  evento de entrada
     * @return true o evento (por si es un evento ontouch)
     */

    public boolean dispatchTouchEvent(MotionEvent event) {

        int action = event.getAction();



        if (event.getPointerCount() >=2){

            mapa=true;
        }

        if (action== MotionEvent.ACTION_UP) {

            if (mapa == true) {

                Intent animal_activity = new Intent(this, Mapa.class);
                startActivity(animal_activity);

                return true;

            }
            return super.dispatchTouchEvent(event);

        }
        else{
            return  super.dispatchTouchEvent(event);

        }

    }

    /**
     * @brief Recibe la respuesta del dialogflow y la reproduce
     *        en caso de haber cambio de animal actualiza la vista
     * @param response Respuesta de dialogflow
     * @return
     */

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();

        Animal_activo=response.getResult().getStringParameter("Type_of_animal");
        ModifyAnimal(Animal_activo);
        speaker.speak(result.getFulfillment().getSpeech(), TextToSpeech.QUEUE_FLUSH, null, null);

    }

    /**
     * @brief Fución que termina la actividad y apaga el speaker
     */

    @Override
    public void onDestroy() {
        if (speaker.isSpeaking())
            speaker.stop();
        speaker.shutdown();


        super.onDestroy();
    }

    /**
     * @brief Fución que chequea el permiso de Audio y en caso de no estar lo pide
     */

    public void ChequearPermisoAudio() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                    22);
        }
    }

    /**
     * @brief Chequea si el evento es de alguno de los dos tipos que nos interesa
     * @param event Evento de entrada
     */

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY){
            ProximityChanged(event);
        }

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION){
            AccelerometeChanged(event);
        }

    }

    /**
     * @brief Funcion que gestiona los cambios del acelerometro y en caso de cumplir
     *        los límites lanza la actividad del QR
     * @param event acelerometro
     */

    public void AccelerometeChanged(SensorEvent event){
        long now = System.currentTimeMillis();

        if ((now - accelerometeStap) > Limite){
            accelerometeStap = now;


            if (event.values[0] > 15 ||
                event.values[1] > 15 ||
                event.values[2] > 15||
                event.values[0] < -15 ||
                event.values[1] < -15 ||
                event.values[2] < -15){

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

    /**@brief Método para cambiar el contexto del animal en dialogflow
     * @param animal Animal de contexto que tiene que activar
     */

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

    /**
     * @brief Funcion que gestiona los cambios del evento de proximidad y si es necesario
     *        actualiza la vista
     * @param event sensor de proximidad
     */

    public void ProximityChanged(SensorEvent event){
        long now = System.currentTimeMillis();
        if((now - proximityStap) >Limite) {
            if (event.values[0] < proximitySensor.getMaximumRange()) {

                switch (currentAnimal.getId()){
                    case 0:

                        ChangeAnimal(  new Animal(   new ArrayList<String>() {{
                                        add("lemur");
                                        add("lemuretiquetas");
                                        add("Es un primate de hábitos estrictamente diurnos que pasa la mayor parte del tiempo en los árboles," +
                                        " aunque también frecuenta el suelo. Es sociable y vive en grupos de 5 a 25 individuos. " +
                                        "Es polígamo y usa su característica cola para hacer señales visuales y odoríferas. Cuando " +
                                        "camina por el suelo mantiene erguida la cola para señalar su presencia al resto de sus congéneres. " +
                                        "También se comunica por vocalizaciones, por actitudes corporales y por expresiones del rostro. Se " +
                                        "alimenta de frutos, hojas, flores, cortezas y pequeños insectos.");   }}
                                        , 1)
                                    );
                        break;
                   case 1:
                       ChangeAnimal(  new Animal(   new ArrayList<String>() {{
                           add("nutria");
                           add("nutriaetiquetas");
                           add(    " Es un animal muy sociable que vive en grupos familiares. Es monógama. Dedica gran parte del día a jugar y comunicarse entre ellas." +
                                   " Se han identificado hasta doce vocalizaciones diferentes además de señales visuales, hormonales y táctiles, como el acicalamiento" +
                                   " social. Tiene gran agilidad en sus manos que las utiliza para cazar cangrejos, moluscos y peces por el tacto, levantar piedras o" +
                                   " construir madrigueras en las orillas de los ríos." );   }}
                               , 2)
                       );
                        break;

                    case 2:
                        ChangeAnimal(  new Animal(   new ArrayList<String>() {{
                            add("anguila");
                            add("angilaetiquetas");
                            add(    " Pez anguiliforme sin escamas. Vive semienterrado en la arena del arrecife formando colonias donde se esconde a la mínima señal de peligro." +
                                    " Es pacífico y tímido. Se alimenta de zooplancton. Las larvas son planctónicos hasta que alcanzan el tamaño suficiente" +
                                    " para hacer su madriguera");   }}
                                , 3)
                        );
                        break;

                    case 3:
                        ChangeAnimal(  new Animal(   new ArrayList<String>() {{
                            add("ajolote");
                            add("ajoloteetiquetas");
                            add(    " Salamandra endémica de algunas lagunas mejicanas. Su desarrollo es muy peculiar ya que alcanza el estado adulto sin terminar la" +
                                    " metamorfosis, es decir, mantiene la forma larvaria durante toda su vida. Tiene hábitos nocturnos. Se alimenta de invertebrados.");   }}
                                , 4)
                        );
                        break;
                    case 4:
                        ChangeAnimal(  new Animal(   new ArrayList<String>() {{
                            add("ranacornuda");
                            add("ranacornudaetiquetas");
                            add(    " Rana robusta que se oculta entre las hojas muertas del suelo de los bosques húmedos utilizando su coloración críptica y apariencia de hoja." +
                                    " Sus ataques son explosivos. Salta sobre la presa y de inmediato la captura y engulle. Es muy voraz. Captura insectos y arácnidos.");   }}
                                , 5)
                        );
                        break;

                    case 5:
                        ChangeAnimal(  new Animal(   new ArrayList<String>() {{
                            add("muntjac");
                            add("muntjacetiquetas");
                            add(    " Pequeño ciervo de bosque que se mueve con agilidad entre la espesura de la jungla. El macho posee una pequeña cornamenta y colmillos que" +
                                    " sobresalen del maxilar superior y la hembra una protuberancia con un mechón de pelo. Es de hábitos solitarios. " +
                                    " Come hojas, frutas y pequeños animales.");   }}
                                , 0)
                        );
                        break;

                        default:
                            ChangeAnimal(  new Animal(   new ArrayList<String>() {{
                                add("lemur");
                                add("lemuretiquetas");
                                add("Es un primate de hábitos estrictamente diurnos que pasa la mayor parte del tiempo en los árboles," +
                                        " aunque también frecuenta el suelo. Es sociable y vive en grupos de 5 a 25 individuos. " +
                                        "Es polígamo y usa su característica cola para hacer señales visuales y odoríferas. Cuando " +
                                        "camina por el suelo mantiene erguida la cola para señalar su presencia al resto de sus congéneres. " +
                                        "También se comunica por vocalizaciones, por actitudes corporales y por expresiones del rostro. Se " +
                                        "alimenta de frutos, hojas, flores, cortezas y pequeños insectos.");   }}
                                    , 1)
                            );
                }

            }
            proximityStap=now;

        }
    }

////////////////USELESS///////////////////////////

    /**
     * @brief Funciones que deben de estar por los diferentes implements
     */

    @Override
    public void onError(AIError error) {}
    @Override
    public void onAudioLevel(float level) {}
    @Override
    public void onListeningStarted() {}
    @Override
    public void onListeningCanceled() {}
    @Override
    public void onListeningFinished() {}
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
