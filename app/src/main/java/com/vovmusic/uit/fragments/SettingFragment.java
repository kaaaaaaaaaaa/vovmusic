package com.vovmusic.uit.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.R;
import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.activities.FullActivity;
import com.vovmusic.uit.animations.LoadingDialog;
import com.vovmusic.uit.animations.ScaleAnimation;
import com.vovmusic.uit.models.Status;
import com.vovmusic.uit.services.SettingLanguage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingFragment extends Fragment {
    private static final String TAG = "SettingFragment";

    private AdView avSettingFragment;

    private SettingLanguage settingLanguage;

    private LottieAnimationView btnSwitchTheme;
    private TextView tvLanguage;
    private TextView tvVietNamese;
    private TextView tvEnglish;
    private Button btnRating;

    private ScaleAnimation scaleAnimation;
    private LoadingDialog loadingDialog;

    private ArrayList<Status> statusArrayList;

    private final String VIETNAMESE = "vi";
    private final String ENGLISH = "en";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DataLocalManager.init(getContext());

        Mapping(view);
        Event();

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });
//        this.avSettingFragment = view.findViewById(R.id.avSettingFragment);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        this.avSettingFragment.loadAd(adRequest);
//
//        this.avSettingFragment.setAdListener(new AdListener() {
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
////                Toast.makeText(getContext(), R.string.toast11, Toast.LENGTH_SHORT).show();
////                Log.d("AD", loadAdError.getMessage());
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
//            }
//
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//            }
//
//            @Override
//            public void onAdClicked() {
//                super.onAdClicked();
//            }
//
//            @Override
//            public void onAdImpression() {
//                super.onAdImpression();
//            }
//        });
    }

    public void Mapping(View view) {
        this.settingLanguage = new SettingLanguage(getContext());
        this.loadingDialog = new LoadingDialog(getActivity());

        this.btnSwitchTheme = view.findViewById(R.id.btnSwitchTheme);
        this.tvLanguage = view.findViewById(R.id.tvLanguage);
        this.tvVietNamese = view.findViewById(R.id.tvVietNamese);
        this.tvEnglish = view.findViewById(R.id.tvEnglish);
        this.btnRating = view.findViewById(R.id.btnRating);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            this.tvLanguage.setVisibility(View.GONE);
            this.tvVietNamese.setVisibility(View.GONE);
            this.tvEnglish.setVisibility(View.GONE);
//            Toast.makeText(getContext(), "Can't change another language, because the device is out of date!", Toast.LENGTH_LONG).show();
        }

        if (DataLocalManager.getTheme()) {
            this.btnSwitchTheme.setMinAndMaxProgress(0.5f, 1.0f); // Tối
        } else {
//            this.btnSwitchTheme.setMinAndMaxProgress(0.1f, 0.5f); // Sáng
            this.btnSwitchTheme.setMinAndMaxProgress(0.5f, 1.0f); // Tối
        }

        if (DataLocalManager.getLanguage().equals(VIETNAMESE)) {
//            this.tvVietNamese.setTextColor(getResources().getColor(R.color.colorMain3));
//            this.tvEnglish.setTextColor(getResources().getColor(R.color.colorLight7));
        } else {
            this.tvEnglish.setTextColor(getResources().getColor(R.color.secondColor));
//            this.tvVietNamese.setTextColor(getResources().getColor(R.color.colorLight7));
        }
    }

    private void Event() {
        this.btnSwitchTheme.setOnClickListener(v -> {
            if (DataLocalManager.getTheme()) {
                DataLocalManager.setTheme(false);
                btnSwitchTheme.setClickable(false); // Ngăn người dùng bấm nhiều lần, bấm kiểu triệt hạ vler
                btnSwitchTheme.setMinAndMaxProgress(0.5f, 1.0f); // Sáng
                btnSwitchTheme.playAnimation();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        btnSwitchTheme.setClickable(true);
                    }
                }, 1800);
            } else {
                DataLocalManager.setTheme(true);
                btnSwitchTheme.setClickable(false); // Ngăn người dùng bấm nhiều lần, bấm kiểu triệt hạ vler
                btnSwitchTheme.setMinAndMaxProgress(0.1f, 0.5f); // Tối
                btnSwitchTheme.playAnimation();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        btnSwitchTheme.setClickable(true);
                    }
                }, 1800);
            }
        });

//        this.tvVietNamese.setOnClickListener(v -> {
//            this.tvVietNamese.setTextColor(getResources().getColor(R.color.colorMain3));
//            this.tvEnglish.setTextColor(getResources().getColor(R.color.colorLight7));
//
//            settingLanguage.Update_Language(VIETNAMESE);
//
//            Intent intent = new Intent(getContext(), FullActivity.class);
//            startActivity(intent);
//
//            Log.d(TAG, "VietNam: " + DataLocalManager.getLanguage());
//        });
//
//        this.tvEnglish.setOnClickListener(v -> {
//            this.tvEnglish.setTextColor(getResources().getColor(R.color.colorMain3));
//            this.tvVietNamese.setTextColor(getResources().getColor(R.color.colorLight7));
//
//            settingLanguage.Update_Language(ENGLISH);
//
//            Intent intent = new Intent(getContext(), FullActivity.class);
//            startActivity(intent);
//
//            Log.d(TAG, "English: " + DataLocalManager.getLanguage());
//        });

        this.scaleAnimation = new ScaleAnimation(getContext(), this.btnRating);
        this.scaleAnimation.Event_Button();
        this.btnRating.setOnClickListener(v -> {
            Open_Feedback_Dialog();
        });
    }

    private void Open_Feedback_Dialog() {
        final Dialog dialog = new Dialog(getContext());

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_feedback_app);

        Window window = (Window) dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Set màu mờ mờ cho background dialog, che đi activity chính, nhưng vẫn có thể thấy được một phần

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        windowAttributes.windowAnimations = R.style.DialogAnimation;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(true); // Bấm ra chỗ khác sẽ thoát dialog

        TextView tvFeedbackTitle = dialog.findViewById(R.id.tvFeedbackTitle);
        tvFeedbackTitle.setSelected(true);
        RatingBar rbRating = dialog.findViewById(R.id.rbRating);

        TextView tvShowStar = dialog.findViewById(R.id.tvShowStar);
        tvShowStar.setSelected(true);
        tvShowStar.setText(String.valueOf(rbRating.getRating()));

        EditText etFeedbackContent = dialog.findViewById(R.id.etFeedbackContent);
        etFeedbackContent.requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Button btnFeedbackCancel = dialog.findViewById(R.id.btnFeedbackCancel);
        Button btnSendFeedback = dialog.findViewById(R.id.btnSendFeedback);

        rbRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            tvShowStar.setText(String.valueOf(rating));
        });

        this.scaleAnimation = new ScaleAnimation(getContext(), btnFeedbackCancel);
        this.scaleAnimation.Event_Button();
        btnFeedbackCancel.setOnClickListener(v -> dialog.dismiss());

        this.scaleAnimation = new ScaleAnimation(getContext(), btnSendFeedback);
        this.scaleAnimation.Event_Button();
        btnSendFeedback.setOnClickListener(v -> {
            float star = rbRating.getRating();
            String content = !etFeedbackContent.getText().toString().trim().isEmpty() ? etFeedbackContent.getText().toString().trim() : "Null";
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String time = dateFormat.format(date);

            if (content.length() > 500) {
                Toast.makeText(v.getContext(), R.string.toast34, Toast.LENGTH_LONG).show();
            } else {
//                loadingDialog.Start_Loading();

                DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
                Call<List<Status>> callBack = dataService.addFeedback(DataLocalManager.getUserID(), star, content, time);
                callBack.enqueue(new Callback<List<Status>>() {
                    @Override
                    public void onResponse(Call<List<Status>> call, Response<List<Status>> response) {
                        statusArrayList = new ArrayList<>();
                        statusArrayList = (ArrayList<Status>) response.body();

                        if (statusArrayList != null) {
                            if (statusArrayList.get(0).getStatus() == 1) {
//                                loadingDialog.Cancel_Loading();

                                Toast.makeText(v.getContext(), R.string.toast35, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } else if (statusArrayList.get(0).getStatus() == 2) {
//                                loadingDialog.Cancel_Loading();

                                Toast.makeText(v.getContext(), R.string.toast34, Toast.LENGTH_SHORT).show();
                            } else {
//                                loadingDialog.Cancel_Loading();

                                Toast.makeText(v.getContext(), R.string.toast11, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
//                        loadingDialog.Cancel_Loading();
                    }

                    @Override
                    public void onFailure(Call<List<Status>> call, Throwable t) {
                        Log.d(TAG, "Handle_Add_Feedback(Error): " + t.getMessage());
                    }
                });
            }
        });

        dialog.show();
    }
}