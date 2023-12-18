package com.example.tradueasy;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

public class TraductorVoz extends AppCompatActivity {

    TextView textViewVoz;
    TextToSpeech textToSpeech;

    ImageButton volver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traductor_voz);
        textViewVoz = findViewById(R.id.textViewVoz);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR){
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        volver = (ImageButton) findViewById(R.id.btnVolver2);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TraductorVoz.this, MenuPrincipal.class);
                startActivity(i);
            }
        });

    }

    ActivityResultLauncher<Intent> lanzadorIntent = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent info = result.getData();
                        ArrayList<String> data = info.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        TranslatorOptions options = new TranslatorOptions.Builder()
                                .setSourceLanguage(TranslateLanguage.SPANISH)
                                .setTargetLanguage(TranslateLanguage.ENGLISH)
                                .build();
                        final Translator spanishEnglishTranslator = Translation.getClient(options);

                        DownloadConditions conditions = new DownloadConditions.Builder()
                                .requireWifi()
                                .build();

                        spanishEnglishTranslator.downloadModelIfNeeded(conditions)
                                .addOnSuccessListener(
                                        new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                //Se descargo el modelo de traduccion
                                                spanishEnglishTranslator.translate(data.get(0))
                                                        .addOnSuccessListener(
                                                                new OnSuccessListener<String>() {
                                                                    @Override
                                                                    public void onSuccess(String s) {
                                                                        textViewVoz.setText(s);
                                                                        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
                                                                    }
                                                                }
                                                        ).addOnFailureListener(
                                                                new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getApplicationContext(), "Error al Traducir",
                                                                                Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                        );
                                            }
                                        }
                                ).addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Hubo un error al descargar el modelo de traduccion
                                                Toast.makeText(getApplicationContext(), "Error al descargar modelo",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
                    }
                }
            }
    );

    public void BtnHablar(View v){
        Intent intentReconocimiento = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentReconocimiento.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "SPANISH");
        try{
            lanzadorIntent.launch(intentReconocimiento);
        } catch (ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(), "Error al Iniciar el Reconocimiento de Voz",
                    Toast.LENGTH_SHORT).show();
        }
    }

}
