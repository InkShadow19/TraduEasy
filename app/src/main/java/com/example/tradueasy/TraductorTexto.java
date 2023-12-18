package com.example.tradueasy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tradueasy.Modelo.Idioma;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TraductorTexto extends AppCompatActivity {

    ImageButton volver;
    EditText editTextIdiomaOrigen;
    TextView textViewTraduccion;
    Button btnElegirIdioma, btnIdiomaElegido, buttonTraducir;

    private ProgressDialog progressDialog;
    private ArrayList<Idioma> IdiomasArrayList;
    private static final String REGISTROS = "Mis_Registros";

    private String codigo_idioma_origen = "es";
    private String titulo_idioma_origen = "Español";
    private String codigo_idioma_destino = "en";
    private String titulo_idioma_destino = "Inglés";

    private TranslatorOptions translatorOptions;
    private Translator translator;
    private String texto_idioma_origen = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traductor_texto);
        InicializarVistas();
        IdiomasDisponibles();

        EditText textoATraducirInput = findViewById(R.id.editTextIdiomaOrigen);

        Button traducir = findViewById(R.id.buttonTraducir);
        traducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoTraducir = textoATraducirInput.getText().toString();

                System.out.println("Texto a traducir: " + textoTraducir);
            }
        });

        volver = (ImageButton) findViewById(R.id.btnVolver1);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TraductorTexto.this, MenuPrincipal.class);
                startActivity(i);
            }
        });


        btnElegirIdioma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(TraductorTexto.this,"Elegir Idioma",Toast.LENGTH_SHORT).show();
                ElegirIdiomaOrigen();
            }
        });

        btnIdiomaElegido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(TraductorTexto.this,"Idioma Elegido",Toast.LENGTH_SHORT).show();
                ElegirIdiomaDestino();
            }
        });

        buttonTraducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(TraductorTexto.this,"Traducir",Toast.LENGTH_SHORT).show();
                ValidarDatos();
            }
        });

    }
    private void InicializarVistas(){
        editTextIdiomaOrigen = findViewById(R.id.editTextIdiomaOrigen);
        textViewTraduccion = findViewById(R.id.textViewTraduccion);
        btnElegirIdioma = findViewById(R.id.btnElegirIdioma);
        btnIdiomaElegido = findViewById(R.id.btnIdiomaElegido);
        buttonTraducir = findViewById(R.id.buttonTraducir);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Espere por favor");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void IdiomasDisponibles(){
        IdiomasArrayList = new ArrayList<>();
        List<String> ListaCodigoIdioma = TranslateLanguage.getAllLanguages();

        for (String codigo_lenguaje : ListaCodigoIdioma){
            String titulo_lenguaje = new Locale(codigo_lenguaje).getDisplayLanguage();

            //Log.d(REGISTROS, "IdiomasDisponibles: codigo_lenguaje: "+codigo_lenguaje);
            //Log.d(REGISTROS, "IdiomasDisponibles: titulo_lenguaje: "+titulo_lenguaje);

            Idioma modeloIdioma = new Idioma(codigo_lenguaje, titulo_lenguaje);
            IdiomasArrayList.add(modeloIdioma);
        }
    }

    private void ElegirIdiomaOrigen(){
        PopupMenu popupMenu = new PopupMenu(this, btnElegirIdioma);
        for (int i=0; i<IdiomasArrayList.size();i++){
            popupMenu.getMenu().add(Menu.NONE,i,i, IdiomasArrayList.get(i).getTitulo_idioma());
        }
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int position = item.getItemId();

                codigo_idioma_origen = IdiomasArrayList.get(position).getCodigo_idioma();
                titulo_idioma_origen = IdiomasArrayList.get(position).getTitulo_idioma();

                btnElegirIdioma.setText(titulo_idioma_origen);
                editTextIdiomaOrigen.setHint("Ingrese texto en: "+titulo_idioma_origen);

                Log.d(REGISTROS, "onMenuItemClick: codigo_idioma_origen: "+codigo_idioma_origen);
                Log.d(REGISTROS, "onMenuItemClick: titulo_idioma_origen: "+titulo_idioma_origen);

                return false;
            }
        });
    }

    private void ElegirIdiomaDestino(){
        PopupMenu popupMenu = new PopupMenu(this, btnIdiomaElegido);
        for (int i=0; i<IdiomasArrayList.size();i++){
            popupMenu.getMenu().add(Menu.NONE,i,i, IdiomasArrayList.get(i).getTitulo_idioma());
        }
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int position = item.getItemId();

                codigo_idioma_destino = IdiomasArrayList.get(position).getCodigo_idioma();
                titulo_idioma_destino = IdiomasArrayList.get(position).getTitulo_idioma();

                btnIdiomaElegido.setText(titulo_idioma_destino);

                Log.d(REGISTROS, "onMenuItemClick: codigo_idioma_destino: "+codigo_idioma_destino);
                Log.d(REGISTROS, "onMenuItemClick: titulo_idioma_destino: "+titulo_idioma_destino);

                return false;
            }
        });
    }
    private void ValidarDatos() {
        texto_idioma_origen = editTextIdiomaOrigen.getText().toString().trim();
        Log.d(REGISTROS, "ValidarDatos: texto_idioma_origen"+texto_idioma_origen);

        if(texto_idioma_origen.isEmpty()){
            Toast.makeText(this,"Ingrese texto",Toast.LENGTH_SHORT).show();
        }else{
            TraducirTexto();
        }
    }

    private void TraducirTexto(){
        progressDialog.setMessage("Procesando");
        progressDialog.show();

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(codigo_idioma_origen)
                .setTargetLanguage(codigo_idioma_destino)
                .build();

        translator = Translation.getClient(translatorOptions);

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //Los paquetes de traduccion se descargaron con exito
                        Log.d(REGISTROS,"onSuccess: El paquete se ha descragado con éxito");
                        progressDialog.setMessage("Traduciendo texto");

                        translator.translate(texto_idioma_origen)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String texto_traducido) {
                                        progressDialog.dismiss();
                                        Log.d(REGISTROS,"onSuccess: texto_traducido"+texto_traducido);
                                        textViewTraduccion.setText(texto_traducido);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Log.d(REGISTROS,"onFailure"+e);
                                        Toast.makeText(TraductorTexto.this,""+e,Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Los paquetes no se descragaron
                        progressDialog.dismiss();
                        Log.d(REGISTROS,"onFailure"+e);
                        Toast.makeText(TraductorTexto.this,""+e,Toast.LENGTH_SHORT).show();
                    }
                });
    }
}