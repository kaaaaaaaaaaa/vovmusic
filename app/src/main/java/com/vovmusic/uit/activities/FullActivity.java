package com.vovmusic.uit.activities;

import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kaushikthedeveloper.doublebackpress.DoubleBackPress;
import com.kaushikthedeveloper.doublebackpress.helper.DoubleBackPressAction;
import com.kaushikthedeveloper.doublebackpress.helper.FirstBackPressAction;
import com.squareup.picasso.Picasso;
import com.vovmusic.uit.R;
import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.animations.LoadingDialog;
import com.vovmusic.uit.animations.ScaleAnimation;
import com.vovmusic.uit.fragments.ChartFragment;
import com.vovmusic.uit.fragments.HomeFragment;
import com.vovmusic.uit.fragments.MiniPlayerFragment;
import com.vovmusic.uit.fragments.PersonalPlaylistFragment;
import com.vovmusic.uit.fragments.RadioFragment;
import com.vovmusic.uit.fragments.SettingFragment;
import com.vovmusic.uit.services.FullPlayerManagerService;
import com.vovmusic.uit.services.MyBroadcastReceiver;

import de.hdodenhof.circleimageview.CircleImageView;

public class FullActivity extends AppCompat {
    private static final String TAG = "FullActivity";

    private FirebaseAuth firebaseAuth;

    private MyBroadcastReceiver myBroadcastReceiver;

    private ScaleAnimation scaleAnimation;
    private LoadingDialog loadingDialog;

    private ImageView ivBell;

    private FrameLayout frameMiniPlayer;

    private MeowBottomNavigation meowBottomNavigation;
    private Fragment fragment;

    private FirstBackPressAction firstBackPressAction;
    private DoubleBackPressAction doubleBackPressAction;
    private DoubleBackPress doubleBackPress;
    private static final int TIME_DURATION = 2000;

    private CircleImageView circleImageView;
    private EditText editText;

    private static final int ID_PERSONAL = 1;
    private static final int ID_HOME = 2;
    private static final int ID_CHART = 3;

    private static final int ID_RADIO = 4;
    private static final int ID_SETTING = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full);

        DataLocalManager.init(this);
        myBroadcastReceiver = new MyBroadcastReceiver();
//        Toast.makeText(this, "User_ID: " + DataLocalManager.getUserID(), Toast.LENGTH_SHORT).show();

        Mapping();
        Event();

//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myBroadcastReceiver, intentFilter); // Đăng kí lắng nghe

        Check_Login();
//        ZaloSDK.Instance.isAuthenticate(new ValidateOAuthCodeCallback() {
//            @Override
//            public void onValidateComplete(boolean validated, int i, long l, String s) {
//                if (validated) {
//                    ZaloSDK.Instance.unauthenticate();
//                    Intent intent = new Intent(FullActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (FullPlayerManagerService.listCurrentSong != null) {
            loadFragmentMiniPlayer();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Check_Login();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(myBroadcastReceiver); // Hủy đăng kí lắng nghe sự kiện
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        unregisterReceiver(myBroadcastReceiver); // Hủy đăng kí lắng nghe sự kiện
    }

    private void Check_Login() {
        FirebaseUser currentUser = this.firebaseAuth.getCurrentUser();
        if (currentUser == null || DataLocalManager.getUserID().isEmpty()) {
            this.firebaseAuth.signOut();
            LoginManager.getInstance().logOut();
            DataLocalManager.deleteUserID();
            DataLocalManager.deleteUserAvatar();
            finish();
            Intent intent = new Intent(FullActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void Mapping() {
//        Toast.makeText(this, "Language: " + DataLocalManager.getLanguage(), Toast.LENGTH_SHORT).show();

//        this.loadingDialog = new LoadingDialog(this);
//        this.loadingDialog.Start_Loading();
        this.firebaseAuth = FirebaseAuth.getInstance();

        this.meowBottomNavigation = findViewById(R.id.bottomNavigation);

        this.ivBell = findViewById(R.id.ivBell);
        this.editText = findViewById(R.id.etSearch);
        this.frameMiniPlayer = findViewById(R.id.frameMiniPlayer);

        this.meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_PERSONAL, R.drawable.ic_music_note));
        this.meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_HOME, R.drawable.ic_home));
        this.meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_CHART, R.drawable.ic_chart));

//        this.meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_RADIO, R.drawable.ic_radio));
        this.meowBottomNavigation.add(new MeowBottomNavigation.Model(ID_RADIO, R.drawable.ic_setting));

        this.circleImageView = findViewById(R.id.civAvatar);
        Picasso.get()
                .load(DataLocalManager.getUserAvatar())
                .placeholder(R.drawable.logovov)
                .error(R.drawable.logovov)
                .into(this.circleImageView);

//        this.loadingDialog.Cancel_Loading();
    }

    private void Event() {
        this.scaleAnimation = new ScaleAnimation(FullActivity.this, this.ivBell);
        this.scaleAnimation.Event_ImageView();

        this.scaleAnimation = new ScaleAnimation(FullActivity.this, this.circleImageView);
        this.scaleAnimation.Event_CircleImageView();

        this.circleImageView.setOnClickListener(v -> {
            Intent intent = new Intent(FullActivity.this, PersonalPageActivity.class);
            startActivity(intent);
        });


        // Event for Bottom Navigation
        this.meowBottomNavigation.setOnClickMenuListener(item -> Log.d(TAG, "Fragment: " + item.getId()));

        this.meowBottomNavigation.setOnShowListener(item -> {
            fragment = null;
            switch (item.getId()) {
                case 1: {
                    fragment = new PersonalPlaylistFragment();
                    break;
                }
                case 2: {
                    fragment = new HomeFragment();
                    break;
                }
                case 3: {

                    fragment = new ChartFragment();
                    break;
                }
                case 4: {
                    fragment = new SettingFragment();
                    break;
                }
//                case 5: {
//                    fragment = new SettingFragment();
//                    break;
//                }
            }
            loadFragment(fragment);
        });

        this.meowBottomNavigation.setOnReselectListener(item -> {// I think, I should reload page in here
        });

        this.meowBottomNavigation.show(ID_HOME, true); // Default tab when open

        // Load info of User with Facebook
//        if (AccessToken.getCurrentAccessToken().getToken() != null && !DataLocalManager.getUserID().isEmpty()) {
//            GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), (object, response) -> {
//                try {
//                    String id = object.getString("id");
//                    String name = object.getString("name");
//                    String email = !object.getString("email").isEmpty() ? object.getString("email") : "Null";
//                    String avatarFacebook = !object.getJSONObject("picture").getJSONObject("data").getString("url").isEmpty() ? object.getJSONObject("picture").getJSONObject("data").getString("url") : "Null";
//                    String isDark = "0";
//                    String isEnglish = "0";
//
//                    Picasso.get()
//                            .load(avatarFacebook)
//                            .placeholder(R.drawable.ic_logo)
//                            .error(R.drawable.ic_logo)
//                            .into(this.circleImageView);
//
//                    DataLocalManager.setUserAvatar(avatarFacebook);
//
//                    Handle_User(id, name, email, avatarFacebook, isDark, isEnglish);
//
//                    Log.d(TAG, "User information (FACEBOOK): " + object);
//                } catch (Exception e) {
//                    e.printStackTrace();
////                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//            Bundle bundle = new Bundle();
//            bundle.putString("fields", "id, name, email, picture.width(1000).height(1000)");
//            graphRequest.setParameters(bundle);
//            graphRequest.executeAsync();
//        }


        // Event for load info of User with Zalo
//        String[] getData = {"id", "name", "picture"};
//        ZaloSDK.Instance.getProfile(this, new ZaloOpenAPICallback() {
//            @Override
//            public void onResult(JSONObject jsonObject) {
//                String avatarZalo = jsonObject.optJSONObject("picture").optJSONObject("data").optString("url");
//                Picasso.get()
//                        .load(avatarZalo)
//                        .placeholder(R.drawable.ic_logo)
//                        .error(R.drawable.ic_logo)
//                        .into(circleImageView);
//
//                LOGIN_TYPE = 2;
//
//                Log.d(LOG_TAG, "User information (ZALO): " + String.valueOf(jsonObject));
//            }
//        }, getData);


        // Event for Search
        this.editText.setOnClickListener(v -> {
            Intent intent_1 = new Intent(FullActivity.this, SearchActivity.class);
            startActivity(intent_1);
        });


        // Event for Press Back Twice To Exit App
        this.doubleBackPressAction = () -> {
            finish();
            moveTaskToBack(true);
            System.exit(0);
        };
        this.firstBackPressAction = () -> Toast.makeText(FullActivity.this, R.string.toast7, Toast.LENGTH_SHORT).show();
        this.doubleBackPress = new DoubleBackPress()
                .withDoublePressDuration(TIME_DURATION)
                .withFirstBackPressAction(this.firstBackPressAction)
                .withDoubleBackPressAction(this.doubleBackPressAction);
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commitAllowingStateLoss();
    }

    public void loadFragmentMiniPlayer() {
        frameMiniPlayer.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameMiniPlayer, new MiniPlayerFragment()).commitAllowingStateLoss();
        frameMiniPlayer.setOnClickListener(v -> {
        });
    }

//    private void Handle_User(String id, String name, String email, String img, String isDark, String isEnglish) {
//        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
//        Call<List<User>> callBack = dataService.addNewUser(id, name, email, img, isDark, isEnglish);
//        callBack.enqueue(new Callback<List<User>>() {
//            @Override
//            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
//                userArrayList = new ArrayList<>();
//                userArrayList = (ArrayList<User>) response.body();
//
//                if (userArrayList != null && userArrayList.size() > 0) {
////                    DataLocalManager.setUserID(id); // Lưu ID người dùng vào SharedPreferences
//                    loadingDialog.Cancel_Loading();
//                    Toast.makeText(FullActivity.this, R.string.toast1, Toast.LENGTH_SHORT).show();
//
//                    Log.d(TAG, "User_ID: " + userArrayList.get(0).getId());
//                } else {
//                    loadingDialog.Cancel_Loading();
//                    Toast.makeText(FullActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<User>> call, Throwable t) {
//                loadingDialog.Cancel_Loading();
//                Log.d(TAG, "Handle_User (Error): " + t.getMessage());
//            }
//        });
//    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        this.doubleBackPress.onBackPressed();
        Log.d(TAG, "Back Twice To Exit!");
    }
}