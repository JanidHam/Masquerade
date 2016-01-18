package com.digma.masquerade.digma.ui;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.digma.masquerade.digma.FromXML;
import com.digma.masquerade.digma.R;
import com.digma.masquerade.digma.adapter.InfoCarnavalAdapter;
import com.digma.masquerade.digma.domain.InfoCarnaval;
import com.digma.masquerade.digma.util.Config;
import com.digma.masquerade.digma.util.MySingleton;
import com.digma.masquerade.digma.util.PrefManager;
import com.facebook.model.GraphUser;
import com.github.gorbin.asne.core.AccessToken;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.listener.OnRequestAccessTokenCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestDetailedSocialPersonCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnRequestSocialPersonCompleteListener {
    public static final String NETWORK_ID = "NETWORK_ID";
    public static final String AVATAR_URL = "AVATAR_URL";
    public static final String PERSON_NAME = "PERSON_NAME";
    public static final String PERSON_EMAIL = "PERSON_EMAIL";
    public static final String DATA_SEND = "DATA_SEND";
    public static final String GENERO_USER = "GENERO_USER";

    public static final String TAG = "MainActivity";

    public static Context ctx;
    private ImageView bannerBottom, bannerMain, imgAvatar;
    private ImageButton closeBannerMain;
    private Button btnUseCamera;
    private FloatingActionButton btnStartCamera;
    public static SocialNetwork socialNetwork;

    public int networkId;
    private boolean requestProfile = false;

    private RecyclerView mRecycler;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager linearLayoutManager;

    public String currentToken, currentID;

    PrefManager prefManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setRecycler();

        Intent i = getIntent();
        networkId = i.getIntExtra(NETWORK_ID, 0);
        socialNetwork = MainFragment.mSocialNetworkManager.getSocialNetwork(networkId);
        socialNetwork.setOnRequestCurrentPersonCompleteListener(this);

        ctx = getApplicationContext();

        prefManager = new PrefManager(this);

        imgAvatar = (ImageView) findViewById(R.id.img_avatar);

        bannerBottom = (ImageView) findViewById(R.id.img_banner_bottom);
        bannerMain = (ImageView) findViewById(R.id.img_banner_main);

        btnUseCamera = (Button) findViewById(R.id.btn_use_camera);
        btnStartCamera = (FloatingActionButton) findViewById(R.id.btn_start_camera);

        closeBannerMain = (ImageButton) findViewById(R.id.btn_close_banner_main);

        TextView cameraDescriptionTextView = (TextView) findViewById(R.id.text_view_camera_description);

        if (cameraNotDetected()) {
            String message = "No camera detected, clicking the button below will have unexpected behaviour.";
            cameraDescriptionTextView.setVisibility(View.VISIBLE);
            cameraDescriptionTextView.setText(message);
        }

        closeBannerMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissBanner();
            }
        });

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        dismissBanner();
                    }
                },
                5000);

        btnUseCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, CameraActivity.class);
                startActivity(intent);
            }
        });

        btnStartCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Toast toast = Toast.makeText(ctx, "Autorice el uso de la camara en las configuraciones del dispositivo primero.", Toast.LENGTH_LONG);
                        toast.show();
                        return;
                    }
                }

                Intent intent = new Intent(ctx, CameraActivity.class);
                startActivity(intent);
            }
        });

        bannerMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.digma.mx"));
                startActivity(browserIntent);
            }

        });

    }

    private void setRecycler() {
        // Obtener el Recycler
        mRecycler = (RecyclerView) findViewById(R.id.recycler_view);
        //recycler.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        linearLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(linearLayoutManager);

        this.setRecyclerViewAdapter(null);

    }

    private void setRecyclerViewAdapter(List<InfoCarnaval> items) {
        if (items == null) items = new ArrayList<>();

        Date today = new Date();
        int month  = today.getMonth() + 1, date = today.getDate();
        List<InfoCarnaval> toRemove = new ArrayList<>();

        items.add(new InfoCarnaval(1, 22, 1, 0, "Precarnavalesco", "Se realizará en el centro de convencions", " - 21:00hrs"));
        items.add(new InfoCarnaval(2, 24, 1, 0, "Comparsas DIF estatal", "Se realizará en el centro de convencions", " - 17:00hrs"));
        items.add(new InfoCarnaval(3, 27, 1, 0, "Baile femenil de Cursillistas", "Se realizará en el ex cine Alhambra", " - 16:00hrs"));
        items.add(new InfoCarnaval(4, 28, 1, 0, "Funeral del mal humor", "Salida Obelisco de los marinos hasta el muelle de los pescadores de san Román", " - 19:00hrs"));
        items.add(new InfoCarnaval(5, 28, 1, 0, "Quema del mal humor", "Muelle de los pecadores - Elenco artístico: EDSON ZUÑIGA 'EL NORTEÑO'", " - 19:00hrs"));
        items.add(new InfoCarnaval(6, 29, 1, 0, "Coronación reyes del INAPAM", "Elenco artístico: SON DE MAR", " - 19:00hrs"));
        items.add(new InfoCarnaval(7, 30, 1, 0, "Coronación reyes de la UAC", "Foro Ah Kim Pech", " - 19:00hrs"));
        items.add(new InfoCarnaval(8, 31, 1, 0, "Coronación reyes del Instituto Campechano", "Foro Ah Kim Pech - Elenco artístico: BILBAO SHOW", " - 19:00hrs"));
        items.add(new InfoCarnaval(9, 01, 2, 0, "Coronación reyes del carnaval de personas con discapacidad", "Foro Ah Kim Pech - Elenco artístico: Gente de zona", " - 19:00hrs"));
        items.add(new InfoCarnaval(10, 02, 2, 0, "Coronación reyes infantiles", "Foro Ah Kim Pech - Elenco artístico INNA y JUAN MAGAN", " - 19:00hrs"));
        items.add(new InfoCarnaval(11, 03, 2, 0, "Coronación reyes del carnaval", "Foro Ah Kim Peche - Elenco artístico: OV7 y KABAH", " - 20:00hrs"));
        items.add(new InfoCarnaval(12, 04, 2, 0, "Paseo de las flores", "Salida: Obelisco de los Marinos / Derrotero: Av. Ruiz Cortines hasta el 'Justo Sierra'", " - 17:00hrs"));
        items.add(new InfoCarnaval(13, 04, 2, 0, "Coronación reina del espectáculo", "Foro Ah Kim Pech, artista Vanessa Arias y Fernando Cermeño - Elenco artístico: Farruko", " - 20:00hrs"));
        items.add(new InfoCarnaval(14, 05, 2, 0, "Corso infantil", "Se realizará en el centro de convencions", " - 16:00hrs"));
        items.add(new InfoCarnaval(15, 05, 2, 0, "Noche de comparsas", "Foro Ah Kim Pech", " - 19:00hrs"));
        items.add(new InfoCarnaval(16, 06, 2, 0, "Sábado de bando", "Salida plaza 4 de octubre / Derrotero: Av. Ruiz Cortinez / Concluye en el 'Justo Sierra'", " - 17:00hrs"));

        for (int i = 0; i < items.size(); ++i) {
            if ( (items.get(i).getDay() < date) & (items.get(i).getMonth() < month) ) {
                toRemove.add(items.get(i));
            } else if ( (items.get(i).getDay() < date) & (items.get(i).getMonth() == month) ) {
                toRemove.add(items.get(i));
            }
        }

        items.removeAll(toRemove);

        mAdapter = null;
        mAdapter = new InfoCarnavalAdapter(items);
        mRecycler.setAdapter(mAdapter);
    }

    private boolean isDataSend() {
        SharedPreferences settings = getPreferences(0);
        return settings.getBoolean(DATA_SEND, false);
    }

    private String getGeneroUser(String gender) {
        return gender.toLowerCase().equals("male") ? "0" : "1";
    }

    private void sendDataUserServer(String name, String email, String gender, String age) {
        prefManager.setDataSend(true);

        final String userName = name, userEmail = email, userAge = age, userGender = getGeneroUser(gender);

        StringRequest strRequest = new StringRequest(Request.Method.POST, Config.SAVE_DATA_USER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        prefManager.setDataSend(false);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", userName != null ? userName : "");
                params.put("email", userEmail != null ? userEmail : "");
                params.put("app", Config.APP_NAME);
                params.put("genero", userGender != null ? userGender : "");
                params.put("edad", userAge != null ? userAge : "");

                return params;
            }
        };
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(strRequest);
    }


    private void dismissBanner() {
        if (requestProfile) return;

        requestProfile = true;

        socialNetwork.requestAccessToken(new OnRequestAccessTokenCompleteListener() {
            @Override
            public void onRequestAccessTokenComplete(int socialNetworkID, AccessToken accessToken) {
                currentToken = accessToken.token;

                socialNetwork.requestCurrentPerson();
            }

            @Override
            public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {

            }
        });

        bannerMain.setVisibility(View.GONE);
        closeBannerMain.setVisibility(View.GONE);
        mRecycler.setVisibility(View.VISIBLE);

        findViewById(R.id.layout_profile).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_start_camera).setVisibility(View.VISIBLE);
    }

    private void sendRequestUserProfile(String url, final String tmpAvatarURL, final String tmpName) {


        StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String result = response.substring(response.indexOf("{") + 1, response.lastIndexOf("}"));
                        result = result.replaceAll(",", ":");

                        String[] values = result.split(":");
                        String email = "", age = "", gender = "";

                        for (int i = 0; i < values.length; ++i) {
                            if (i == 1) {
                                values[i] = values[i].replace("\\u0040", "@");

                                values[i] = values[i].replace("\"", "");

                                email = values[i];
                            }

                            if (i == 4) {
                                values[i] = values[i].replace("}", "");
                                age = values[i];
                            }

                            if (i == 6) {
                                values[i] = values[i].replace("\"", "");

                                gender = values[i];
                            }
                        }

                        prefManager.setUserProfile(tmpAvatarURL, tmpName, email, gender, age);

                        sendDataUserServer(tmpName, email, gender, age);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };

        MySingleton.getInstance(ctx).addToRequestQueue(strRequest);
    }

    private boolean cameraNotDetected() {
        return !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //String url = "http://www.passwithjohn.co.uk/images%5CWebBottomBanner4.jpg";
        //ImageRequest imageRequest = ImageBannerDownload.getBannerBottom(url, bannerBottom);

        //MySingleton.getInstance(this).addToRequestQueue(imageRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                if (socialNetwork != null) {
                    socialNetwork.logout();
                    Intent intent = new Intent(this, SocialActivity.class);
                    startActivity(intent);
                    finish();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setAvatar(String url, String name) {
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imgAvatar.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                    }
                });

        MySingleton.getInstance(this).addToRequestQueue(request);

        TextView textView = (TextView) findViewById(R.id.txt_welcome_message);

        if (name != null)
            textView.setText("Hola " + name + ", bienvenido a Masquerade Digma, la aplicación donde tomarse fotos nunca fue tan divertida.");
    }

    @Override
    public void onRequestSocialPersonSuccess(int socialNetworkId, SocialPerson socialPerson) {
        setAvatar(socialPerson.avatarURL, socialPerson.name);

        currentID = socialPerson.id;

        if (prefManager.getDataSend()) return;

        if (!getSocialNetwork()) {
            sendDataUserServer(socialPerson.name, "", "", "");
            return;
        }

        if (currentToken == null) return;

        String url = String.format("%s%s?access_token=%s&fields=%s", Config.URL_GRAPH_API_FACEBOOK, currentID, currentToken, Config.PARAMS_FIELDS_FACEBOOK_INFO);

        sendRequestUserProfile(url, socialPerson.avatarURL, socialPerson.name);
    }

    @Override
    public void onError(int socialNetworkID, String requestID, String errorMessage, Object data) {
        Toast toast = Toast.makeText(ctx, "Hubo un error al cargar su foto", Toast.LENGTH_LONG);
        toast.show();
    }

    private boolean getSocialNetwork() {
        return socialNetwork.getID() == FacebookSocialNetwork.ID ? true : false;
    }
}
