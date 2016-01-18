package com.digma.masquerade.digma.ui;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.digma.masquerade.digma.R;
import com.github.gorbin.asne.core.SocialNetwork;
import com.github.gorbin.asne.core.SocialNetworkManager;
import com.github.gorbin.asne.core.listener.OnLoginCompleteListener;
import com.github.gorbin.asne.core.listener.OnRequestSocialPersonCompleteListener;
import com.github.gorbin.asne.core.persons.SocialPerson;
import com.github.gorbin.asne.facebook.FacebookSocialNetwork;
import com.github.gorbin.asne.twitter.TwitterSocialNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by janidham on 05/01/16.
 */
public class MainFragment extends Fragment implements SocialNetworkManager.OnInitializationCompleteListener, OnLoginCompleteListener {
    public static SocialNetworkManager mSocialNetworkManager;

    private Button facebook, twitter, termsCondition;

    public static SocialNetwork socialNetwork;

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment, container, false);
        ((SocialActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        // init buttons and set Listener
        facebook = (Button) rootView.findViewById(R.id.facebook);
        twitter = (Button) rootView.findViewById(R.id.twitter);
        //termsCondition = (Button) rootView.findViewById(R.id.terms_and_condition);

        facebook.setOnClickListener(loginClick);
        twitter.setOnClickListener(loginClick);
        //termsCondition.setOnClickListener(termsConditionClick);

        //Get Keys for initiate SocialNetworks
        String TWITTER_CONSUMER_KEY = getActivity().getString(R.string.twitter_consumer_key);
        String TWITTER_CONSUMER_SECRET = getActivity().getString(R.string.twitter_consumer_secret);
        String TWITTER_CALLBACK_URL = "oauth://ASNE";

        //Chose permissions
        ArrayList<String> fbScope = new ArrayList<String>();
        fbScope.addAll(Arrays.asList("public_profile, email, user_friends, publish_actions"));

        //Use manager to manage SocialNetworks
        mSocialNetworkManager = (SocialNetworkManager) getFragmentManager().findFragmentByTag(SocialActivity.SOCIAL_NETWORK_TAG);

        //Check if manager exist
        if (mSocialNetworkManager == null) {
            mSocialNetworkManager = new SocialNetworkManager();

            //Init and add to manager FacebookSocialNetwork
            FacebookSocialNetwork fbNetwork = new FacebookSocialNetwork(this, fbScope);
            mSocialNetworkManager.addSocialNetwork(fbNetwork);

            //Init and add to manager TwitterSocialNetwork
            TwitterSocialNetwork twNetwork = new TwitterSocialNetwork(this, TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, TWITTER_CALLBACK_URL);
            mSocialNetworkManager.addSocialNetwork(twNetwork);

            //Initiate every network from mSocialNetworkManager
            getFragmentManager().beginTransaction().add(mSocialNetworkManager, SocialActivity.SOCIAL_NETWORK_TAG).commit();
            mSocialNetworkManager.setOnInitializationCompleteListener(this);

        } else {
            //if manager exist - get and setup login only for initialized SocialNetworks
            if(!mSocialNetworkManager.getInitializedSocialNetworks().isEmpty()) {
                List<SocialNetwork> socialNetworks = mSocialNetworkManager.getInitializedSocialNetworks();
                for (SocialNetwork socialNetwork : socialNetworks) {
                    socialNetwork.setOnLoginCompleteListener(this);
                    initSocialNetwork(socialNetwork);
                }
            }
        }
        return rootView;
    }

    private void initSocialNetwork(SocialNetwork sn){
        if(sn.isConnected()){
            switch (sn.getID()){
                case FacebookSocialNetwork.ID:
                    startProfile(FacebookSocialNetwork.ID);
                    facebook.setText("Show Facebook profile");
                    break;
                case TwitterSocialNetwork.ID:
                    startProfile(TwitterSocialNetwork.ID);
                    twitter.setText("Show Twitter profile");
                    break;
            }
        }
    }
    @Override
    public void onSocialNetworkManagerInitialized() {
        //when init SocialNetworks - get and setup login only for initialized SocialNetworks
        for (SocialNetwork socialNetwork : mSocialNetworkManager.getInitializedSocialNetworks()) {
            socialNetwork.setOnLoginCompleteListener(this);
            initSocialNetwork(socialNetwork);
        }
    }

    //Login listener

    private View.OnClickListener loginClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int networkId = 0;

            switch (view.getId()){
                case R.id.facebook:
                    networkId = FacebookSocialNetwork.ID;
                    break;
                case R.id.twitter:
                    networkId = TwitterSocialNetwork.ID;
                    break;
            }

            SocialNetwork socialNetwork = mSocialNetworkManager.getSocialNetwork(networkId);
            if(!socialNetwork.isConnected()) {
                if(networkId != 0) {
                    socialNetwork.requestLogin();
                    SocialActivity.showProgress("Iniciando sesion..");
                } else {
                    Toast.makeText(getActivity(), "Wrong networkId", Toast.LENGTH_LONG).show();
                }
            } else {
                startProfile(socialNetwork.getID());
            }
        }
    };

    private View.OnClickListener termsConditionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), TermsConditionActivity.class);

            PendingIntent pendingIntent =
                    TaskStackBuilder.create(getActivity())
                            .addNextIntentWithParentStack(intent)
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
            builder.setContentIntent(pendingIntent);

            startActivity(intent);
        }
    };

    @Override
    public void onLoginSuccess(int networkId) {
        SocialActivity.hideProgress();
        //startProfile(networkId);
    }

    @Override
    public void onError(int networkId, String requestID, String errorMessage, Object data) {
        SocialActivity.hideProgress();
        Toast.makeText(getActivity(), "ERROR: " + errorMessage, Toast.LENGTH_LONG).show();
    }

    private void startProfile(int networkId){
        Log.i("Login", "Success");
        initMainActivity(networkId);
    }

    private void initMainActivity(int networkId) {
        Intent i = new Intent(getActivity(), MainActivity.class);

        i.putExtra(MainActivity.NETWORK_ID, networkId);

        startActivity(i);

        getActivity().finish();
    }
}
