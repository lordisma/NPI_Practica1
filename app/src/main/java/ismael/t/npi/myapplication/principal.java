package ismael.t.npi.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class principal extends AppCompatActivity {

    // Elementos de la UI
    CardView readerQR, speaker;

    // Variables para el mapa
    public  boolean mapa =false;

    // Variables para la inicializacion de los servicios de Dialog
    private AIRequest mensaje_dialog;
    private AIDataService datos;


    /**
     * @brief En el metodo onCreate inicializamor el lector QR
              y el speaker para el uso de estos con las
     *        funcionalidades de DialogFlow y ZXING
     * @param savedInstanceState información para crear la actividad
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        final Activity activity = this;

        //Declaramos los botones de la interfaz y definimos su funcionalidad
        readerQR = (CardView) findViewById(R.id.readerQR);
        speaker  = (CardView) findViewById(R.id.speaker);

        readerQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Start();
            }
        });

        // Inicialización del mapa y dialogflow
        mapa=false;
        final AIConfiguration config = new AIConfiguration("23e6b35921f14ffeac0dfd9724403d75",
                AIConfiguration.SupportedLanguages.Spanish,
                AIConfiguration.RecognitionEngine.System);
        datos = new AIDataService( config);
        mensaje_dialog = new AIRequest();

    }

    ////////////////////////////PUBLICOS////////////////////////////////////////

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
            protected void onPostExecute(AIResponse aiResponse) {}
        }.execute(mensaje_dialog);
    }

    /**@brief Funciones que cargan la informacion de los diferentes animales para la nueva actividad
     *
     */

    public void Lemur_llamar_chatbox() {

        Intent animal_activity = new Intent(this, AnimalDetails.class);
        Bundle b = new Bundle();
        ArrayList<String> Animal1 = new ArrayList<String>();

        Animal1.add("lemur");
        Animal1.add("lemuretiquetas");
        Animal1.add("Es un primate de hábitos estrictamente diurnos que pasa la mayor parte del tiempo en los árboles, aunque también frecuenta el suelo. Es sociable y vive en grupos de 5 a 25 individuos. Es polígamo y usa su característica cola para hacer señales visuales y odoríferas. Cuando camina por el suelo mantiene erguida la cola para señalar su presencia al resto de sus congéneres. También se comunica por vocalizaciones, por actitudes corporales y por expresiones del rostro. Se alimenta de frutos, hojas, flores, cortezas y pequeños insectos.");
        Animal1.add("Aqui puedes ver al lemur");
        Animal1.add("0");
        b.putStringArrayList("key",Animal1 );

        animal_activity.putExtras(b);
        startActivity(animal_activity);

    }

    public void Nutria_llamar_chatbox() {

        Intent animal_activity = new Intent(this, AnimalDetails.class);
        Bundle b = new Bundle();
        ArrayList<String> Animal1 = new ArrayList<String>();

        Animal1.add("nutria");
        Animal1.add("lemuretiquetas");
        Animal1.add("Es un animal muy sociable que vive en grupos familiares. Es monógama. Dedica gran parte del día a jugar y comunicarse entre ellas. Se han identificado hasta doce vocalizaciones diferentes además de señales visuales, hormonales y táctiles, como el acicalamiento social. Tiene gran agilidad en sus manos que las utiliza para cazar cangrejos, moluscos y peces por el tacto, levantar piedras o construir madrigueras en las orillas de los ríos.");
        Animal1.add("Aqui puedes ver a la nutria");
        Animal1.add("1");
        b.putStringArrayList("key",Animal1 );

        animal_activity.putExtras(b);
        startActivity(animal_activity);
    }

    public void Anguila_llamar_chatbox() {

        Intent animal_activity = new Intent(this, AnimalDetails.class);
        Bundle b = new Bundle();
        ArrayList<String> Animal1 = new ArrayList<String>();

        Animal1.add("anguila");
        Animal1.add("lemuretiquetas");
        Animal1.add("Pez anguiliforme sin escamas. Vive semienterrado en la arena del arrecife formando colonias donde se esconde a la mínima señal de peligro. Es pacífico y tímido. Se alimenta de zooplancton. Las larvas son planctónicos hasta que alcanzan el tamaño suficiente para hacer su madriguera");
        Animal1.add("Aqui puedes ver a la anguila jardinera");
        Animal1.add("2");
        b.putStringArrayList("key",Animal1 );

        animal_activity.putExtras(b);
        startActivity(animal_activity);

    }

    public void Ajolote_llamar_chatbox() {

        Intent animal_activity = new Intent(this, AnimalDetails.class);
        Bundle b = new Bundle();
        ArrayList<String> Animal1 = new ArrayList<String>();

        Animal1.add("ajolote");
        Animal1.add("lemuretiquetas");
        Animal1.add("Salamandra endémica de algunas lagunas mejicanas. Su desarrollo es muy peculiar ya que alcanza el estado adulto sin terminar la metamorfosis, es decir, mantiene la forma larvaria durante toda su vida. Tiene hábitos nocturnos. Se alimenta de invertebrados.");
        Animal1.add("Aqui puedes ver al ajolote");
        Animal1.add("3");
        b.putStringArrayList("key",Animal1 );

        animal_activity.putExtras(b);
        startActivity(animal_activity);

    }

    public void Rana_cornuda_llamar_chatbox( ) {

        Intent animal_activity = new Intent(this, AnimalDetails.class);
        Bundle b = new Bundle();
        ArrayList<String> Animal1 = new ArrayList<String>();

        Animal1.add("ranacornuda");
        Animal1.add("lemuretiquetas");
        Animal1.add("Rana robusta que se oculta entre las hojas muertas del suelo de los bosques húmedos utilizando su coloración críptica y apariencia de hoja. Sus ataques son explosivos. Salta sobre la presa y de inmediato la captura y engulle. Es muy voraz. Captura insectos y arácnidos.");
        Animal1.add("Aqui puedes ver a la sorprendente rana cornuda");
        Animal1.add("4");
        b.putStringArrayList("key",Animal1 );

        animal_activity.putExtras(b);
        startActivity(animal_activity);

    }

    public void Mutjac_llamar_chatbox( ) {

        Intent animal_activity = new Intent(this, AnimalDetails.class);
        Bundle b = new Bundle();
        ArrayList<String> Animal1 = new ArrayList<String>();

        Animal1.add("muntjac");
        Animal1.add("lemuretiquetas");
        Animal1.add("Pequeño ciervo de bosque que se mueve con agilidad entre la espesura de la jungla. El macho posee una pequeña cornamenta y colmillos que sobresalen del maxilar superior y la hembra una protuberancia con un mechón de pelo. Es de hábitos solitarios. Come hojas, frutas y pequeños animales.");
        Animal1.add("Aqui puedes ver el muntjac tambien conocido como ciervo raton");
        Animal1.add("5");
        b.putStringArrayList("key",Animal1 );

        animal_activity.putExtras(b);
        startActivity(animal_activity);
    }

    /**@brief Funciones para lanzar los eventos de navegador (telegram y youtube).
     */

    public  void telegram(View view){

        Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse("https://t.me/BiodomoBot"));
        startActivity(telegram);

    }
    public  void Videos_informativos(View view){

        Intent telegram = new Intent(Intent.ACTION_VIEW , Uri.parse("https://www.youtube.com/playlist?list=PLd4iYJozvaoP7p5lAcuQG_0ax6axqWv_6&app=desktop"));
        startActivity(telegram);

    }

    ////////////////////////////PROTECTED////////////////////////////////////////

    /**@brief Funcion lanzada con el boton de speaker la cual nos lleva a la ventana
              de Detalles de los animales e inicializa esta con valores de presentacion
              los cuales sirven de tutorial
     */

    protected void Start(){
        Intent animal_activity = new Intent(this, AnimalDetails.class);
        Bundle b = new Bundle();

        ArrayList<String> Animal1 = new ArrayList<String>();
        Animal1.add("biodomo");
        Animal1.add("lemuretiquetas");
        Animal1.add("WELCOME TO BIODOMO\nAnimales disponibles: ajolotes, nutrias, lemures, muntjacs, ranas cornudas y anguilas jardineras \nPreguntas:que les gusta comer, donde viven, como se reproducen, como son, cuanto miden, cuanto pesan");
        Animal1.add("Hola. Bienvenido a Biodomo. Soy Wooper y durante esta aventura yo seré tu guía. Puedes preguntarme sobre ajolotes, nutrias, lemures, muntjacs, ranas cornudas y anguilas jardineras. ¡Sé mucho sobre ellos! Conozco lo que les gusta comer, donde viven, como se reproducen, como son, cuanto miden, cuanto pesan... Llevo mucho tiempo observándolos. ");
        Animal1.add("0");
        b.putStringArrayList("key",Animal1 );
        animal_activity.putExtras(b);


        startActivity(animal_activity);
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

                switch ( result.getContents()){
                    case "lemur":

                        Lemur_llamar_chatbox();
                        CambioAnimal("lemur");
                        break;

                    case "nutria":

                        Nutria_llamar_chatbox();
                        CambioAnimal("nutrias");
                        break;

                    case "anguila jardinera":

                        Anguila_llamar_chatbox();
                        CambioAnimal("anguila jardinera");
                        break;

                    case "ajolote":

                        Ajolote_llamar_chatbox();
                        CambioAnimal("ajolote");
                        break;

                    case "rana cornuda":

                        Rana_cornuda_llamar_chatbox();
                        CambioAnimal("rana cornuda");
                        break;

                    case "muntjac":

                        Mutjac_llamar_chatbox();
                        CambioAnimal("muntjac");
                        break;
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

    /**
     * @brief Función del segundo plano de la aplicación
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapa=false;
    }

}
