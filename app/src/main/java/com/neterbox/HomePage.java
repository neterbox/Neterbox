package com.neterbox;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.neterbox.jsonpojo.followerlist.Followerlist;
import com.neterbox.jsonpojo.followerlist.FollowerlistDatum;
import com.neterbox.jsonpojo.friend_list.FriendListDatum;
import com.neterbox.jsonpojo.friend_list.FriendListPojo;
import com.neterbox.jsonpojo.get_profile.GetProfile;
import com.neterbox.jsonpojo.get_profile.GetProfileDatum;
import com.neterbox.jsonpojo.updateqb.UpdateQB;
import com.neterbox.jsonpojo.uploadpic.Uploadpic;
import com.neterbox.qb.ChatHelper;
import com.neterbox.retrofit.APIClient;
import com.neterbox.retrofit.APIInterface;
import com.neterbox.utils.Constants;
import com.neterbox.utils.Helper;
import com.neterbox.utils.Sessionmanager;
import com.quickblox.auth.session.QBSettings;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.ServiceZone;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.messages.services.SubscribeService;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePage extends Activity {

    TextView taddfriend, tlogout,tprofilename;
    LinearLayout lph;
    RelativeLayout relative_following, relative_follower, relative_frnd, relative_settings;
    ImageView iback1, iback2, iback3, iback4, ichat, icircle, iplay;
    CircleImageView profile_image;
    List<FriendListDatum> friendListData;
    List<FollowerlistDatum> followerlistData;

    String Loginname,index,user_id;

    public static final int GALLARY_REQUEST=2;
    public static final int CAMERA_REQUEST=1;
    public static final int MY_PERMISSIONS_REQUEST_GALLARY=11;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA=12;

    Context context;
    Sessionmanager sessionmanager;
    SharedPreferences sharedPreferences;
    APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

    private GPSTracker mGPS;
    public double latitude1;
    public double longitude1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        context = this;
        sessionmanager = new Sessionmanager(this);
        sharedPreferences = context.getSharedPreferences(Constants.mypreference, Context.MODE_PRIVATE);
        initquickblox();
        updateqbid(sessionmanager.getValue(Sessionmanager.Id),sessionmanager.getValue(Sessionmanager.Quickbox_Id));
        FriendList(sessionmanager.getValue(Sessionmanager.Id));
        followerlist(sessionmanager.getValue(Sessionmanager.Id));
        this.Loginname =Loginname;

        statusCheck();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mGPS = new GPSTracker(context);
                if (mGPS.canGetLocation) {
                    latitude1 = mGPS.getLatitude();
                    longitude1 = mGPS.getLongitude();
                    Constants.shareLoc = "http://maps.google.com/maps?saddr=" +latitude1+","+longitude1;

                }
            } else {
                checkLocationPermission();
            }
        }

        idMapping();
        Listener();


        profile_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                        showPictureDialog();

                    } else {
                        //Request Location Permission
                        checkCameraPermission();
                        checkStoragePermission();
                    }
                } else {
                    showPictureDialog();
                }
            }
        });
    }

    private void Listener() {
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, EditProfile.class);
                startActivity(i);
                finish();
            }
        });

        final String profilepic = sessionmanager.getValue(Sessionmanager.profile);
        // set dummy profile if profile pic is not selected
        if(  new Sessionmanager(context).getValue(Sessionmanager.profile) != null)
        {
            Glide.with(context).load(new Sessionmanager(context).getValue(Sessionmanager.profile)).placeholder(R.drawable.dummy).into(profile_image);

        }
        // Store username in Homepage
        Loginname = sessionmanager.getValue(Sessionmanager.Name);
        if(!(tprofilename.getText().toString().equals(""))) {
            tprofilename.setText(Loginname);
        }

        lph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, UserProfile.class);
                i.putExtra("name", Loginname);
                i.putExtra("profile_pic",profilepic);
                startActivity(i);
                finish();
            }
        });
        taddfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, SearchGroupFriend.class);
                startActivity(i);
                finish();
            }
        });
        relative_frnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, FriendList.class);
                startActivity(i);
                finish();
            }
        });
        relative_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, SearchFollowings.class);
                startActivity(i);
                finish();
            }
        });
        relative_follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, search_followers.class);
                startActivity(i);
                finish();
            }
        });
        relative_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, Settings.class);
                startActivity(i);
                finish();
            }
        });
        tlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionmanager.logoutUser();
                Intent i = new Intent(HomePage.this, LoginPage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();


            }
        });

        ichat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, ChatModule.class);
                startActivity(i);
                finish();
            }
        });
        icircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, Circles.class);
                startActivity(i);
                finish();
            }
        });
        iplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HomePage.this, PlayGridview.class);
                startActivity(i);
                finish();
            }
        });


    }

    private void idMapping() {
        taddfriend = (TextView) findViewById(R.id.taddfriend);
        tprofilename = (TextView) findViewById(R.id.tprofilename);
        lph = (LinearLayout) findViewById(R.id.lph);
        iback1 = (ImageView) findViewById(R.id.iback1);
        iback2 = (ImageView) findViewById(R.id.iback2);
        iback3 = (ImageView) findViewById(R.id.iback3);
        iback4 = (ImageView) findViewById(R.id.iback4);
        tlogout = (TextView) findViewById(R.id.tlogout);
        relative_following = (RelativeLayout) findViewById(R.id.relative_following);
        relative_follower = (RelativeLayout) findViewById(R.id.relative_follower);
        relative_frnd = (RelativeLayout) findViewById(R.id.relative_frnd);
        relative_settings = (RelativeLayout) findViewById(R.id.relative_settings);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        ichat = (ImageView) findViewById(R.id.ichat);
        icircle = (ImageView) findViewById(R.id.icircle);
        iplay = (ImageView) findViewById(R.id.iplay);

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.support.v7.app.AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions((Activity) context,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        Constants.MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        Constants.MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
    }

    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }
    private void checkStoragePermission() {


        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_GALLARY);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_GALLARY);
            }
        }
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                    android.Manifest.permission.CAMERA)
                    ) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{android.Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{android.Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLARY_REQUEST);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLARY_REQUEST) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);

//                    Toast.makeText(context, "Image Saved!", Toast.LENGTH_SHORT).show();
                    profile_image.setImageBitmap(bitmap);
                    File fileGallery=new File(path);

                    if(Helper.isConnectingToInternet(context)){
                        Uploadpic(new Sessionmanager(context).getValue(Sessionmanager.Id),fileGallery);
                    }
                    else {
                        Helper.showToastMessage(context,"No Internet Connection");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }else if (requestCode == CAMERA_REQUEST) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            profile_image.setImageBitmap(thumbnail);
            String imagePath=saveImage(thumbnail);
            if (Helper.isConnectingToInternet(context)) {
                File fileCamera=new File(imagePath);

                Uploadpic(new Sessionmanager(context).getValue(Sessionmanager.Id),fileCamera);
            }
            else
            {
                Toast.makeText(context, "", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private String saveImage(Bitmap thumbnail) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "IMAGE_DIRECTORY");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File f = new File(path, "DemoPicture.jpg");
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.e("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public void Uploadpic(String Id, File fileCamera){
        final ProgressDialog progressDialog = Helper.showProgressDialog(context);

        RequestBody loginIdReqBody = RequestBody.create(MediaType.parse("text/plain"), Id);
        Log.e("login_id",""+Id);
        if (fileCamera!=null)
        {
            final RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), fileCamera);
            MultipartBody.Part userProfile = MultipartBody.Part.createFormData("profile_pic", fileCamera.getName(), requestFile);

            Call<Uploadpic> uploadProfileCall = apiInterface.uploadPic(loginIdReqBody, userProfile);
            uploadProfileCall.enqueue(new Callback<Uploadpic>() {
                @Override
                public void onResponse(Call<Uploadpic> uploadImageCall, Response<Uploadpic> response) {
                    if (response.body().getStatus().equals("Success")) {
                        new Sessionmanager(HomePage.this).putSessionValue(Sessionmanager.profile,response.body().getData().getUser().getProfilePic());

                        progressDialog.dismiss();
                        Glide.with(context).load(response.body().getData().getUser().getProfilePic()).into(profile_image);
                    } else {
                        progressDialog.dismiss();

                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(Call<Uploadpic> call, Throwable t) {
                    progressDialog.dismiss();

//                    Helper.showToastMessage(context,t.getMessage());
                }
            });
        }
    }

   /* public void getprofile(String index,String user_id)
    {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setMessage("Please Wait...");
        dialog.show();

        Call<GetProfile> getProfileCall = apiInterface.getprofilepojo(index, user_id);
        getProfileCall.enqueue(new Callback<GetProfile>() {
            @Override
            public void onResponse(Call<GetProfile> call, Response<GetProfile> response)
            {
                dialog.dismiss();
                if(response.body().getStatus().equalsIgnoreCase("Success")){
                    sessionmanager.createSession_userprofile((response.body().getData()));

                }
            }
            @Override
            public void onFailure(Call<GetProfile> call, Throwable t) {
                dialog.dismiss();
            }
        });

    }*/

    public void initquickblox()
    {
        QBSettings.getInstance().init(getApplicationContext(), getString(R.string.application_id), getString(R.string.authorization_key), getString(R.string.authorization_secret));
        QBSettings.getInstance().setAccountKey(getString(R.string.account_key));
        QBSettings.getInstance().setEndpoints("https://api.quickblox.com", "chat.quickblox.com", ServiceZone.DEVELOPMENT);
        QBSettings.getInstance().setZone(ServiceZone.DEVELOPMENT);
        final QBChatService chatService = QBChatService.getInstance();

        QBChatService.setDebugEnabled(true); // enable chat logging
        QBChatService.setDefaultPacketReplyTimeout(30000);
        QBChatService.setDefaultConnectionTimeout(30000);

        QBChatService.ConfigurationBuilder chatServiceConfigurationBuilder = new QBChatService.ConfigurationBuilder();
        chatServiceConfigurationBuilder.setSocketTimeout(60);
        chatServiceConfigurationBuilder.setKeepAlive(true);
        chatServiceConfigurationBuilder.setUseTls(true);
        QBChatService.setConfigurationBuilder(chatServiceConfigurationBuilder);

        final QBUser user = new QBUser(sessionmanager.getValue(sessionmanager.Name),sessionmanager.getValue(sessionmanager.Password),sessionmanager.getValue(sessionmanager.Email));
        Log.e("QB USER ",sessionmanager.getValue(sessionmanager.Name));

         /*TODO For QBUser SignUp*/
        QBUsers.signUp(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(final QBUser user, Bundle args) {
                // success
                sharedPreferences.edit().putString(sessionmanager.Quickbox_Id, String.valueOf(user.getId())).apply();
//                Toast.makeText(HomePage.this, "Welcome Quuickblox", Toast.LENGTH_SHORT).show();
                Log.e("QB USER ",sessionmanager.Name);
                Log.e("QBID", ":" +user.getId());

                user.setPassword(sessionmanager.getValue(sessionmanager.Password));
                user.setLogin(sessionmanager.getValue(sessionmanager.Email));
                user.setFullName(sessionmanager.getValue(sessionmanager.Name));

                chatService.login(user, new QBEntityCallback() {
                    @Override
                    public void onSuccess(Object o, Bundle bundle) {
                        Log.e("obje", ":" + new Gson().toJson(o));
//                        ProgressDialogFragment.hide(getSupportFragmentManager);
//                        Toast.makeText(HomePage.this, "Progress dialog fragment", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(QBResponseException e) {
//                        Toast.makeText(HomePage.this, "Error login Progress dialog fragment", Toast.LENGTH_SHORT).show();
                    }
                });


                ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void result, Bundle bundle) {
//                        Toast.makeText(HomePage.this, "Progress dialog fragment", Toast.LENGTH_SHORT).show();

                        user.setPassword(sessionmanager.getValue(sessionmanager.Password));
                        user.setLogin(sessionmanager.getValue(sessionmanager.Email));
                        user.setFullName(sessionmanager.getValue(sessionmanager.Name));
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        e.printStackTrace();
//                        Toast.makeText(HomePage.this, "Error instance Progress dialog fragment", Toast.LENGTH_SHORT).show();
                    }
                });
                SubscribeService.subscribeToPushes(context, true);
            }

            @Override
            public void onError(QBResponseException error) {

                // error
                /*TODO For QBUser SignIn (if error is occur it will signin)*/
                // Login
                QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(final QBUser user, Bundle args) {
                        // success
                        sharedPreferences.edit().putString(sessionmanager.Quickbox_Id, String.valueOf(user.getId())).apply();
//                        Toast.makeText(HomePage.this, "Welcome Quuickblox", Toast.LENGTH_SHORT).show();
//                        Log.e("QB USER ",sessionmanager.Name);
//                        Log.e("QBID", ":" +user.getId());

                        user.setPassword(sessionmanager.getValue(sessionmanager.Password));
                        user.setLogin(sessionmanager.getValue(sessionmanager.Email));
                        user.setFullName(sessionmanager.getValue(sessionmanager.Name));

                        chatService.login(user, new QBEntityCallback() {
                            @Override
                            public void onSuccess(Object o, Bundle bundle) {
//                                Log.e("obje", ":" + new Gson().toJson(o));
//                                Toast.makeText(HomePage.this, "Progress dialog fragment", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(QBResponseException e) {
//                                Toast.makeText(HomePage.this, "Error login Progress dialog fragment", Toast.LENGTH_SHORT).show();
                            }
                        });

                        ChatHelper.getInstance().login(user, new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void result, Bundle bundle) {
//                                Toast.makeText(HomePage.this, "Progress dialog fragment", Toast.LENGTH_SHORT).show();
                                user.setPassword(sessionmanager.getValue(sessionmanager.Password));
                                user.setLogin(sessionmanager.getValue(sessionmanager.Email));
                                user.setFullName(sessionmanager.getValue(sessionmanager.Name));
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                e.printStackTrace();
  //                              Toast.makeText(HomePage.this, "Error instance Progress dialog fragment", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(QBResponseException error) {
                        // error
                    }
                });
            }
        });
    }
    public void updateqbid(String sender_id ,String sender_qb_id){

    Call<UpdateQB> updateQBCall = apiInterface.updateqbidpojo(sender_id, sender_qb_id);
        updateQBCall.enqueue(new Callback<UpdateQB>() {
        @Override
        public void onResponse(Call<UpdateQB> call, Response<UpdateQB> response)
        {
            if (response.body().getStatus().equalsIgnoreCase("Success"))
            {
                sessionmanager.createSession_updateqb((response.body().getData()));
            }
            else
            {
                Toast.makeText(HomePage.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onFailure(Call<UpdateQB> call, Throwable t) {
        }
    });
}

    public void followerlist(String follower_id) {
        Call<Followerlist> followerlistCall = apiInterface.followerlistpojo(follower_id);

        followerlistCall.enqueue(new Callback<Followerlist>() {
            @Override
            public void onResponse(Call<Followerlist> call, Response<Followerlist> response) {
                if (response.body().getStatus().equals("Success")) {
                    Log.e("Followerlist REPONSE",new Gson().toJson(response.body().getData()));
                    followerlistData = new ArrayList<FollowerlistDatum>();
                    followerlistData = response.body().getData();
                    if(Constants.followerlistData!=null)
                    {
                        Constants.followerlistData.clear();
                    }
                    Constants.followerlistData.addAll(response.body().getData());

                    for(int i=0;i<followerlistData.size();i++)
                    {
                        sessionmanager.createSession_followerlist(followerlistData.get(i));
                    }
                    followerlistData = new ArrayList<FollowerlistDatum>();
                    followerlistData = response.body().getData();
                } else {
                }
            }

            @Override
            public void onFailure(Call<Followerlist> call, Throwable t) {
            }
        });
    }

    public void FriendList(String login_id)
    {
        final Call<FriendListPojo> friendListPojoCall = apiInterface.friendlistpojo(login_id);
        friendListPojoCall.enqueue(new Callback<FriendListPojo>() {
            @Override
            public void onResponse(Call<FriendListPojo> call, Response<FriendListPojo> response) {
                if (response.body().getStatus().equals("Success")) {
                    friendListData = new ArrayList<FriendListDatum>();
                    friendListData = response.body().getData();
                    for (int i=0;i<friendListData.size();i++)
                    {
                        sessionmanager.createSession_frienddata(friendListData.get(i));
                    }
                    friendListData = new ArrayList<FriendListDatum>();
                    friendListData = response.body().getData();
                } else {
                }
            }
            @Override
            public void onFailure(Call<FriendListPojo> call, Throwable t) {
            }
        });
    }
}
