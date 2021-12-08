package com.vovmusic.uit.activities;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.R;
import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.animations.LoadingDialog;
import com.vovmusic.uit.animations.ScaleAnimation;
import com.vovmusic.uit.models.User;
import com.vovmusic.uit.services.FullPlayerManagerService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalPageActivity extends AppCompat {
    private static final String TAG = "PersonalPageActivity";

    private InterstitialAd interstitialAd;

    private FirebaseAuth firebaseAuth;

    private ImageView ivBack;
    private ImageView civAvatarFrame;
    private TextView tvPersonalName;
    private TextView tvYourInfoName;
    private TextView tvYourInfoEmail;
    private Button btnLogout;

    private ScaleAnimation scaleAnimation;
    private LoadingDialog loadingDialog;

    private ArrayList<User> userArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_page);

        DataLocalManager.init(this);

        Mapping();
        Event();

//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
//                Load_Ad();
//            }
//        });
    }

    private void Load_Ad() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-8151267593746824/2227269288", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd minterstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                interstitialAd = minterstitialAd;
                Log.i(TAG, "onAdLoaded");

                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        // Called when fullscreen content failed to show.
                        Log.d(TAG, "The ad failed to show.");
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        // Called when fullscreen content is shown.
                        // Make sure to set your reference to null so you don't
                        // show it a second time.
                        interstitialAd = null;
                        Log.d(TAG, "The ad was shown.");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        // Called when fullscreen content is dismissed.
                        Log.d(TAG, "The ad was dismissed.");
                        finish();
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                Log.d(TAG, loadAdError.getMessage());
                interstitialAd = null;
            }
        });
    }

    @Override
    public void finish() {
        if (interstitialAd != null) {
            interstitialAd.show(this);
            try {
                if (FullPlayerManagerService.mediaPlayer != null)
                    FullPlayerManagerService.mediaPlayer.pause();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.");
            super.finish();
        }
    }

    private void Mapping() {
        this.firebaseAuth = FirebaseAuth.getInstance();

        this.loadingDialog = new LoadingDialog(this);
//        this.loadingDialog.Start_Loading();

        this.ivBack = findViewById(R.id.ivBack);
        this.civAvatarFrame = findViewById(R.id.civAvatarFrame);

        this.tvPersonalName = findViewById(R.id.tvPersonalName);
        this.tvPersonalName.setSelected(true); // Text will be moved

        this.tvYourInfoName = findViewById(R.id.tvYourInfoName);
        this.tvYourInfoName.setSelected(true); // Text will be moved

        this.tvYourInfoEmail = findViewById(R.id.tvYourInfoEmail);
        this.tvYourInfoEmail.setSelected(true); // Text will be moved

        this.btnLogout = findViewById(R.id.btnLogout);

        Handler_Display_Info_User(DataLocalManager.getUserID());
    }

    private void Event() {
        this.scaleAnimation = new ScaleAnimation(PersonalPageActivity.this, this.ivBack);
        this.scaleAnimation.Event_ImageView();
        this.ivBack.setOnClickListener(v -> finish());

        this.scaleAnimation = new ScaleAnimation(PersonalPageActivity.this, this.btnLogout);
        this.scaleAnimation.Event_Button();
        this.btnLogout.setOnClickListener(v -> {
            Open_Dialog(Gravity.CENTER);
        });
    }

    private void Open_Dialog(int gravity) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_textview_dialog);

        Window window = (Window) dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set màu mờ mờ cho background dialog, che đi activity chính, nhưng vẫn có thể thấy được một phần

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true); // Bấm ra chỗ khác sẽ thoát dialog

        TextView tvDialogTitle = dialog.findViewById(R.id.tvDialogTitle);
        tvDialogTitle.setSelected(true); // Text will be moved
        tvDialogTitle.setText(R.string.tvDialogTitle1);

        TextView tvDialogContent = dialog.findViewById(R.id.tvDialogContent);
        tvDialogContent.setText(R.string.tvDialogContent1);

        Button btnDialogCancel = dialog.findViewById(R.id.btnDialogCancel);
        btnDialogCancel.setText(R.string.btnDialogCancel1);

        Button btnDialogAction = dialog.findViewById(R.id.btnDialogAction);
        btnDialogAction.setText(R.string.btnDialogAction1);

        this.scaleAnimation = new ScaleAnimation(PersonalPageActivity.this, btnDialogCancel);
        this.scaleAnimation.Event_Button();
        btnDialogCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        this.scaleAnimation = new ScaleAnimation(PersonalPageActivity.this, btnDialogAction);
        this.scaleAnimation.Event_Button();
        btnDialogAction.setOnClickListener(v -> {
            dialog.dismiss();
            this.firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            DataLocalManager.deleteUserID();
            DataLocalManager.deleteUserAvatar();
            finish();
            moveTaskToBack(true);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, R.string.toast9, Toast.LENGTH_SHORT).show();
        });

        dialog.show(); // câu lệnh này sẽ hiển thị Dialog lên
    }

    private void Handler_Display_Info_User(String userID) {
        DataService dataService = APIService.getService();
        Call<List<User>> callBack = dataService.getUserFromID(userID);
        callBack.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                userArrayList = (ArrayList<User>) response.body();
                if (userArrayList != null) {
                    Picasso.get()
                            .load(userArrayList.get(0).getImg())
                            .placeholder(R.drawable.logovov)
                            .error(R.drawable.logovov)
                            .into(civAvatarFrame);
                    tvPersonalName.setText(userArrayList.get(0).getName());
                    tvYourInfoName.setText(userArrayList.get(0).getName());
                    tvYourInfoEmail.setText(userArrayList.get(0).getEmail());

//                    loadingDialog.Cancel_Loading();

                    Log.d(TAG, "User Infomation: " + userArrayList.get(0).getName());
                }
//                loadingDialog.Cancel_Loading();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
//                loadingDialog.Cancel_Loading();
                Log.d(TAG, "Handler_Display_Info_User(Error): " + t.getMessage());
            }
        });
    }
}