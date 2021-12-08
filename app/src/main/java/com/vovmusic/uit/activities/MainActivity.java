package com.vovmusic.uit.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.kaushikthedeveloper.doublebackpress.DoubleBackPress;
import com.kaushikthedeveloper.doublebackpress.helper.DoubleBackPressAction;
import com.kaushikthedeveloper.doublebackpress.helper.FirstBackPressAction;
import com.vovmusic.uit.API.APIService;
import com.vovmusic.uit.API.DataService;
import com.vovmusic.uit.SharedPreferences.DataLocalManager;
import com.vovmusic.uit.animations.LoadingDialog;
import com.vovmusic.uit.animations.ScaleAnimation;
import com.vovmusic.uit.R;
import com.vovmusic.uit.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompat {
    private static final String TAG = "MainActivity";
    private static final String TAG_1 = "LOGIN WITH FACEBOOK";
    private static final String TAG_2 = "LOGIN WITH GOOGLE";
//    private static final String LOG_TAG_3 = "LOGIN WITH ZALO";

    private FirebaseAuth firebaseAuth;
    private CallbackManager callbackManager;
    private GoogleSignInClient googleSignInClient;

    private ScaleAnimation scaleAnimation;

    private Animation topAnimation, bottomAnimation;
    private LoadingDialog loadingDialog;

    private ImageView imvLogo;
    private Button btnLoginFacebook;
    private Button btnLoginGoogle;

    private ArrayList<User> userArrayList;
//    private Button btnLoginZL;

//    private OAuthCompleteListener oAuthCompleteListener;

    private FirstBackPressAction firstBackPressAction;
    private DoubleBackPressAction doubleBackPressAction;
    private DoubleBackPress doubleBackPress;
    private static final int TIME_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DataLocalManager.init(this);

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String hashKey = new String(Base64.encode(md.digest(), 0));
//                Log.d("KEY", "printHashKey() Hash Key: " + hashKey);
//            }
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("Key", "printHashKey()", e);
//        } catch (Exception e) {
//            Log.e("Key", "printHashKey()", e);
//        }

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                //Toast.makeText(getApplicationContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }
        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

        Mapping();
        Event();
        Login_Facebook();
        Login_Google();
//        Login_Zalo();
    }

    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = this.firebaseAuth.getCurrentUser();
        if (currentUser != null && !DataLocalManager.getUserID().isEmpty()) {
            Update_UI();
        }
//        if (!ZaloSDK.Instance.getOAuthCode().isEmpty()) {
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void Mapping() {
//        this.loadingDialog = new LoadingDialog(this);

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.callbackManager = CallbackManager.Factory.create();

        this.imvLogo = findViewById(R.id.imvLogo);
        this.btnLoginFacebook = findViewById(R.id.btnLoginFacebook);
        this.btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
//        this.btnLoginZL = findViewById(R.id.btnLoginZL);

        this.topAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.top_animation);
        this.bottomAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bottom_animation);
    }

    private void Event() {
        this.imvLogo.setAnimation(this.topAnimation);
        this.btnLoginFacebook.setAnimation(this.bottomAnimation);
        this.btnLoginGoogle.setAnimation(this.bottomAnimation);
//        this.btnLoginZL.setAnimation(this.bottomAnimation);

        this.scaleAnimation = new ScaleAnimation(MainActivity.this, this.btnLoginFacebook);
        this.scaleAnimation.Event_Button();
        this.scaleAnimation = new ScaleAnimation(MainActivity.this, this.btnLoginGoogle);
        this.scaleAnimation.Event_Button();
//        this.scaleAnimation = new ScaleAnimation(MainActivity.this, this.btnLoginZL);
//        this.scaleAnimation.Event_Button();

        this.doubleBackPressAction = () -> {
            finish();
            moveTaskToBack(true);
            System.exit(0);
        };
        this.firstBackPressAction = () -> Toast.makeText(this, R.string.toast7, Toast.LENGTH_SHORT).show();
        this.doubleBackPress = new DoubleBackPress()
                .withDoublePressDuration(TIME_DURATION)
                .withFirstBackPressAction(this.firstBackPressAction)
                .withDoubleBackPressAction(this.doubleBackPressAction);
    }

    private void Login_Facebook() {
        this.btnLoginFacebook.setOnClickListener(v -> {
            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG_1, "facebook: onSuccess: " + loginResult.getAccessToken());

                    AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                    firebaseAuth.signInWithCredential(credential)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG_1, "signInWithCredential:success");
                                        FirebaseUser user = firebaseAuth.getCurrentUser();

                                        if (user != null) {
                                            Log.d(TAG_1, "User Information: " + user.toString());

                                            String id = user.getUid();
//                                            String name = user.getDisplayName();
                                            String name= "vovmusic";
                                            String email;
                                            if (user.getEmail() != null) {
//                                                email = user.getEmail();
                                                email = "vovmusic@gmail.com";
                                            } else {
                                                email = "vovmusic@gmail.com";
                                            }

                                            String avatarFacebook;
                                            if (user.getPhotoUrl() != null) {
//                                                avatarFacebook = user.getPhotoUrl() + "?height=500&access_token=" + loginResult.getAccessToken().getToken();
                                                avatarFacebook="https://www.google.com/url?sa=i&url=https%3A%2F%2Fcodelearn.io%2Fsharing%2Fcac-thanh-phan-co-ban-trong-android-la-gi&psig=AOvVaw2m_RWqdN9fwAmohmCTRtYw&ust=1638971189991000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCKDn587p0fQCFQAAAAAdAAAAABAD";
                                            } else {
                                                avatarFacebook = "Null";
                                            }
                                            String isDark = "0";
                                            String isEnglish = "0";

//                                            loadingDialog.Start_Loading();
                                            Handle_User(id, name, email, avatarFacebook, isDark, isEnglish);
                                        }
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG_1, "signInWithCredential:failure", task.getException());
                                        Toast.makeText(MainActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
//                    GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), (object, response) -> {
//                        try {
//                            Log.d(LOG_TAG_1, "User information (FACEBOOK): " + object);
//
//                            String id = object.getString("id");
//                            String name = object.getString("name");
//                            String email = !object.getString("email").isEmpty() ? object.getString("email") : "Null";
//                            String avatarFacebook = !object.getJSONObject("picture").getJSONObject("data").getString("url").isEmpty() ? object.getJSONObject("picture").getJSONObject("data").getString("url") : "Null";
//                            String isDark = "0";
//                            String isEnglish = "0";
//
//                            loadingDialog.Start_Loading();
//                            Handle_User(id, name, email, avatarFacebook, isDark, isEnglish);
//                        } catch (Exception e) {
//                            Log.d(LOG_TAG_1, "Get_User_Information(Error):" + e.getMessage());
//                            Toast.makeText(MainActivity.this, R.string.toast10, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    Bundle bundle = new Bundle();
//                    bundle.putString("fields", "id, name, email, picture.width(1000).height(1000)");
//                    graphRequest.setParameters(bundle);
//                    graphRequest.executeAsync(); // Thực thi không đồng bộ
                }

                @Override
                public void onCancel() {
                    Log.d(TAG_1, "facebook: onCancel");
                    Toast.makeText(MainActivity.this, R.string.toast2, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG_1, "facebook: onError ", error);
                    Toast.makeText(MainActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void Login_Google() {
        // Configure Google Sign In
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        this.googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        this.btnLoginGoogle.setOnClickListener(v -> {
            activityResultLauncher.launch(new Intent(googleSignInClient.getSignInIntent()));
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent intent = result.getData();

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        Log.d(TAG_2, "firebaseAuthWithGoogle:" + account.getId());

                        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG_2, "signInWithCredential:success");

                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    if (user != null) {
                                        Log.d(TAG_2, "User Information: " + user.toString());

                                        String id = user.getUid();
                                        String name = user.getDisplayName();
                                        String email = !Objects.requireNonNull(user.getEmail()).isEmpty() ? user.getEmail() : "Null";
                                        String avatarGoogle = !Objects.requireNonNull(user.getPhotoUrl()).toString().isEmpty() ? user.getPhotoUrl().toString().replace("s96-c", "s500-c") : "Null";
                                        String isDark = "0";
                                        String isEnglish = "0";

//                                        loadingDialog.Start_Loading();
                                        Handle_User(id, name, email, avatarGoogle, isDark, isEnglish);
                                    }
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG_2, "signInWithCredential:failure", task.getException());
                                    Toast.makeText(MainActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Log.w(TAG_2, "Google sign in failed", e);
                    Toast.makeText(MainActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
                }
            }
        }
    });

//    private void Login_Zalo() {
//        this.btnLoginZL.setOnClickListener(v -> {
//            oAuthCompleteListener = new OAuthCompleteListener() {
//                @Override
//                public void onAuthenError(int errorCode, String message) {
//                    Toast.makeText(MainActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
//                    Log.d(LOG_TAG_2, errorCode + " | " + message);
//                }
//
//                @Override
//                public void onGetOAuthComplete(OauthResponse response) {
//                    String code = response.getOauthCode();
//                    if (!code.isEmpty()) {
//                        if (!DataLocalManager.getLogin()) {
//                            DataLocalManager.setLogin(true);
//                        }
//
//                        Intent intent = new Intent(MainActivity.this, FullActivity.class);
//                        intent.putExtra("LOGIN_ZALO", "Zalo");
//                        startActivity(intent);
//
//                        Toast.makeText(MainActivity.this, R.string.toast1, Toast.LENGTH_SHORT).show();
//                        Log.d(LOG_TAG_2, "Code ZALO: " + code);
//                    }
//                }
//            };
//            ZaloSDK.Instance.authenticate(MainActivity.this, APP_OR_WEB, this.oAuthCompleteListener);
//        });
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
//        ZaloSDK.Instance.onActivityResult(this, requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void Handle_User(String id, String name, String email, String img, String isDark, String isEnglish) {
        DataService dataService = APIService.getService(); // Khởi tạo Phương thức để đẩy lên
        Call<List<User>> callBack = dataService.addNewUser(id, name, email, img, isDark, isEnglish);
        callBack.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                userArrayList = new ArrayList<>();
                userArrayList = (ArrayList<User>) response.body();

                if (userArrayList != null && userArrayList.size() > 0) {
                    DataLocalManager.setUserID(id);
                    DataLocalManager.setUserAvatar(img);

                    if (!DataLocalManager.getUserID().isEmpty()) {
                        Update_UI();

//                        loadingDialog.Cancel_Loading();
                        Toast.makeText(MainActivity.this, R.string.toast1, Toast.LENGTH_SHORT).show();
                    }

                    Log.d(TAG, "User_ID: " + userArrayList.get(0).getId());
                } else {
//                    loadingDialog.Cancel_Loading();
                    Toast.makeText(MainActivity.this, R.string.toast3, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.d(TAG, "Handle_User (Error): " + t.getMessage());
            }
        });
    }

    private void Update_UI() {
        Intent intent = new Intent(MainActivity.this, FullActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        this.doubleBackPress.onBackPressed();
        Log.d(TAG, "Back Twice To Exit!");
    }
}