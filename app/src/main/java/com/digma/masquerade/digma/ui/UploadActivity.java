package com.digma.masquerade.digma.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.digma.masquerade.digma.R;
import com.digma.masquerade.digma.util.BitmapFileWorkerTask;
import com.digma.masquerade.digma.util.Config;
import com.digma.masquerade.digma.util.Helpers;
import com.digma.masquerade.digma.util.MySingleton;
import com.github.gorbin.asne.core.listener.OnPostingCompleteListener;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by janidham on 06/01/16.
 */
public class UploadActivity extends AppCompatActivity {

    public String filePath = null;
    public static String photoName = null;
    public static String encodedImage = null;
    private ImageView imgPreview;
    private Button btnUploadAll;
    public static Context context;
    private ProgressDialog pd;
    private int postingCount;
    private String socialNetwork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        context = this;

        socialNetwork = getSocialNetwork();

        Button upload = (Button) findViewById(R.id.btn_upload_all);
        upload.setText(String.format("Compartir con %s", socialNetwork));

        btnUploadAll = (Button) findViewById(R.id.btn_upload_all);

        imgPreview = (ImageView) findViewById(R.id.imgPreview);

        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity
        filePath = i.getStringExtra(CameraActivity.EXTRA_IMAGE_PATH);

        if (filePath != null) {
            displayImage(filePath);
        }

        postingCount = 0;

        btnUploadAll.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                renderDialog();
                /*
                showProgress("Posteando foto...");

                postOnFacebook();
                postOnServer();*/
            }
        });
    }

    private String getSocialNetwork() {
        return MainActivity.socialNetwork.getID() == FacebookSocialNetwork.ID ? "Facebook" : "Twitter";
    }

    private void renderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Escribe tus comentarios..");
        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    showProgress("Posteando foto...");

                    postOnFacebook(input.getText().toString());
                    postOnServer();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    hideProgress();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    protected void showProgress(String message) {
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage(message);
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    protected void hideProgress() {
        pd.dismiss();
        postingCount = 0;
        Helpers.renderMessage("Foto posteada con éxito.", Toast.LENGTH_SHORT, context);
    }

    private boolean postCompleted(int count) {
        if (count == 2) return true;

        return false;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //finishAndRemoveTask();
    }

    private boolean validSocialPost(File photoFile) {
        if (MainActivity.socialNetwork == null) {
            Helpers.renderMessage("No inició sesión correctamente.", Toast.LENGTH_SHORT, context);
            return false;
        }

        if (photoFile.isFile()) return true;

        Helpers.renderMessage("Hubo un error posteando en facebook.", Toast.LENGTH_SHORT, context);

        if (postingCount == 1) postingCount = 0;

        return false;
    }

    private boolean validServer() {
        if (encodedImage != null)
            if (photoName != null)
                return true;

        Helpers.renderMessage("Hubo un error guardando la foto.", Toast.LENGTH_SHORT, context);

        if (postingCount == 1) postingCount = 0;

        return false;
    }

    private void postOnFacebook(String comment) {
        //Helpers.renderMessage("Iniciando post en facebook..", Toast.LENGTH_SHORT, context);
        File photo = new File(filePath);

        if (!validSocialPost(photo)) {
            return;
        }
        //Config.TEXT_POST_FACEBOOK
        MainActivity.socialNetwork.requestPostPhoto(photo, comment, new OnPostingCompleteListener() {
            @Override
            public void onPostSuccessfully(int socialNetworkID) {
                //Helpers.renderMessage("Post en Facebook con éxito.", Toast.LENGTH_SHORT, context);
                ++postingCount;
                if (postCompleted(postingCount)) hideProgress();
            }

            @Override
            public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
                Log.i("FACEBOOK", errorMessage);
                hideProgress();
                Helpers.renderMessage("Hubo un error posteando en facebook.", Toast.LENGTH_LONG, context);
            }
        });
    }

    private void postOnServer() {
        //Helpers.renderMessage("Iniciando post en Servidor..", Toast.LENGTH_SHORT, context);
        if (!validServer()) return;

        StringRequest strRequest = new StringRequest(Request.Method.POST, Config.FILE_UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Helpers.renderMessage("Imagen enviada con éxito al servidor", Toast.LENGTH_SHORT, context);
                        ++postingCount;
                        if (postCompleted(postingCount)) hideProgress();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Response", error.toString());
                        hideProgress();
                        //Helpers.renderMessage("Hubo un error al enviar la imagen: " + error.toString(), Toast.LENGTH_LONG, context);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("image", encodedImage);
                params.put("name", photoName);
                return params;
            }
        };
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(context).addToRequestQueue(strRequest);
    }

    private void postOnTwitter() {
        //pass
    }

    public void displayImage(String path) {
        BitmapFileWorkerTask bmpTask = new BitmapFileWorkerTask(imgPreview);
        bmpTask.execute(path);
    }

}
