package com.appsnipp.loginsamples;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.appsnipp.loginsamples.attendance.AttendanceDetailActivity;
import com.appsnipp.loginsamples.chat.ChatUserActivity;
import com.appsnipp.loginsamples.conclude.ConcludeActivity;
import com.appsnipp.loginsamples.login.LoginActivity;
import com.appsnipp.loginsamples.model.API.APIClient;
import com.appsnipp.loginsamples.model.API.RequestAPI;
import com.appsnipp.loginsamples.model.Attendance.Attendance;
import com.appsnipp.loginsamples.model.Attendance.Attendance_checkout;
import com.appsnipp.loginsamples.model.Attendance.Check;
import com.appsnipp.loginsamples.model.Attendance.Data;
import com.appsnipp.loginsamples.model.User_model.ListUserModel;
import com.appsnipp.loginsamples.model.User_model.User;
import com.appsnipp.loginsamples.model.User_model.UserModelDetail;
import com.appsnipp.loginsamples.personal_information.ProfileActivity;
import com.appsnipp.loginsamples.project.ProjectActivity;
import com.appsnipp.loginsamples.user.UserActivity;
import com.appsnipp.loginsamples.utils.LocationTrack;
import com.appsnipp.loginsamples.utils.LoginUser;
import com.appsnipp.loginsamples.utils.SharedPrefs;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class HomeActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{

    DrawerLayout drawer;
    User userModel;
    UserModelDetail modeluser;
    CardView car_summary,admin_home;
    TextView tv_username_main,tv_gmail_main;
    Data modelAttendance;
    public ProgressDialog progressDialog;
    private ArrayList<UserModelDetail> mUsermodelDetails;
    public static final String USER_MODEL_KEY = "user_model";
    public static final String USER_EMAIL_KEY = "USER_EMAIL_KEY";
    public static final String USER_PASSWORD_KEY = "USER_PASSWORD_KEY";

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkLogin();


        if(!SharedPrefs.getInstance().get(USER_EMAIL_KEY, String.class).isEmpty()){
            getDataIntent();

            if(getlocaltion()==true){
                checkInUser();
            }


            getprofileuser();
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            drawer = findViewById(R.id.drawer_layout);

            toolbar.setNavigationIcon(R.drawable.ic_list_shit_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View header = navigationView.getHeaderView(0);
            tv_username_main=header.findViewById(R.id.tv_username_main);
            tv_username_main.setText(userModel.getName());
            tv_gmail_main=header.findViewById(R.id.tv_gmail_main);
            tv_gmail_main.setText(userModel.getEmail());
            toolbar.setTitleTextColor(this.getResources().getColor(R.color.whiteTextColor));
            car_summary=findViewById(R.id.car_summary);
            admin_home=findViewById(R.id.admin_home);
            int role = SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class).getRole();
            if (role == 0) {
                admin_home.setVisibility(View.VISIBLE);
                car_summary.setVisibility(View.VISIBLE);
            } else {
            }

        }

    }
    private boolean getlocaltion() {
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
        locationTrack = new LocationTrack(HomeActivity.this);


        if (locationTrack.canGetLocation()) {

            double la=10.8734;
            double  lon=106.7980;

            double longitude = locationTrack.getLongitude();
            double latitude = locationTrack.getLatitude();

            double lontemp=Math.abs(longitude-lon);
            double latemp=Math.abs(latitude-la);


            int R = 6371; // Radius of the earth in km
            double dLat = deg2rad(latemp);  // deg2rad below
            double dLon = deg2rad(lontemp);
            double a =
                    Math.sin(dLat/2) * Math.sin(dLat/2) +
                            Math.cos(deg2rad(la)) * Math.cos(deg2rad(latitude)) *
                                    Math.sin(dLon/2) * Math.sin(dLon/2)
                    ;
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
            double d = R * c*1000; // Distance in k

            if(d<40){

               return true;
            }

        } else {

            locationTrack.showSettingsAlert();
            return false;
        }

        return false;
    }

    public double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }
    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(HomeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    private void checkLogin() {
        String email = SharedPrefs.getInstance().get(USER_EMAIL_KEY, String.class);
        String password = SharedPrefs.getInstance().get(USER_PASSWORD_KEY, String.class);
        if (email.isEmpty() || password.isEmpty()){
            goToLogin();

        }else {
            new LoginUser(this).login(email, password, false);
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showdialogCheckin(){


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle("Confirm ...");
        alertDialog.setMessage("You want to check in");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        checkAttendance();

                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();


    }

    private void checkAttendance() {
        showLoading();
        String token = SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class).getToken();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);
        Date today = new Date();
        String date = dateFormatter.format(today);

        RequestAPI service = APIClient.getClient().create(RequestAPI.class);
        Call<Attendance> call = service.Attendance(token,date);
        call.enqueue(new Callback<Attendance>() {
            @Override
            public void onResponse(@NonNull Call<Attendance> call, @NonNull Response<Attendance> response) {

                progressDialog.dismiss();
                Attendance models = response.body();
                if (models != null) {
                    modelAttendance=models.getData();
                    Toast.makeText(HomeActivity.this, models.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Attendance> call, @NonNull Throwable t) {
                Toast.makeText(HomeActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getprofileuser() {
        String token = SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class).getToken();
        String email = SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class).getEmail();
        RequestAPI service = APIClient.getClient().create(RequestAPI.class);
        service.viewprofile(token,email)
                .enqueue(new Callback<ListUserModel>() {
                    @Override
                    public void onResponse(@NonNull Call<ListUserModel> call, @NonNull Response<ListUserModel> response) {
                        ListUserModel models = response.body();
                        if (models != null) {
                            mUsermodelDetails = models.getData();
                            // Toast.makeText(ProfileActivity.this, models.getMessage(), Toast.LENGTH_SHORT).show();
                            modeluser = mUsermodelDetails.get(0);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ListUserModel> call, @NonNull Throwable t) {
                        Toast.makeText(HomeActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void checkoutAttendance() {
        showLoading();
        String token = SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class).getToken();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);
        Date today = new Date();
        String date = dateFormatter.format(today);

        RequestAPI service = APIClient.getClient().create(RequestAPI.class);
        Call<Attendance_checkout> call = service.Attendancecheckout(token,date);
        call.enqueue(new Callback<Attendance_checkout>() {
            @Override
            public void onResponse(@NonNull Call<Attendance_checkout> call, @NonNull Response<Attendance_checkout> response) {
                progressDialog.dismiss();
                Attendance_checkout models = response.body();
                if (models != null) {
                    Toast.makeText(HomeActivity.this, models.getMessage(), Toast.LENGTH_SHORT).show();

                }else {
                    try {
                        Gson gson = new Gson();
                        JSONObject object = new JSONObject(response.errorBody().string());
                        Attendance_checkout model = gson.fromJson(object.toString(), Attendance_checkout.class);
                        Toast.makeText(HomeActivity.this, model.getMessage(), Toast.LENGTH_SHORT).show();
                        if(!model.getMessage().equals("user already checkout")){
                            showdialogCheckin();

                        }

                    } catch (Exception e) {
                        Toast.makeText(HomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Attendance_checkout> call, @NonNull Throwable t) {
                Toast.makeText(HomeActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });



    }
    private void checkInUser() {
        showLoading();
        String token = SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class).getToken();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        dateFormatter.setLenient(false);
        Date today = new Date();
        String date = dateFormatter.format(today);

        RequestAPI service = APIClient.getClient().create(RequestAPI.class);
        Call<Check> call = service.checkin(token,date);
        call.enqueue(new Callback<Check>() {
            @Override
            public void onResponse(@NonNull Call<Check> call, @NonNull Response<Check> response) {
                progressDialog.dismiss();
                Check models = response.body();
                if (models != null && !models.getMessage().toString().equals("True")) {
                        showdialogCheckin();

                }else {
                   // Toast.makeText(HomeActivity.this,"user already checkin", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Check> call, @NonNull Throwable t) {
                Toast.makeText(HomeActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });



    }
    public void chatClicked(View view){
        Intent intent = new Intent(this, ChatUserActivity.class);
        startActivity(intent);
    }
    public void ProjectClicked(View view){
        Intent intent = new Intent(this, ProjectActivity.class);
        startActivity(intent);
    }
    public void ConcludeClicked(View view){
        Intent intent = new Intent(this, ConcludeActivity.class);
        startActivity(intent);
    }

    public void AdminClicked(View view){
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_profile: {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                intent.putExtra("usermodel", (Serializable) userModel);
                startActivity(intent);
                break;
            }
            case R.id.nav_attendance: {
                Intent intent = new Intent(HomeActivity.this, AttendanceDetailActivity.class);
                intent.putExtra("usermodel", (Serializable) modeluser);
                startActivity(intent);
                break;
            }
            case R.id.nav_checkout: {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                alertDialog.setTitle("Confirm ...");
                alertDialog.setMessage("You want to Check out");
                alertDialog.setIcon(R.mipmap.ic_launcher);
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

//                                Toast.makeText(HomeActivity.this, "You have been check out", Toast.LENGTH_SHORT).show();
                                checkoutAttendance();
                            }
                        });
                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();

                break;
            }
            case R.id.nav_logout: {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                alertDialog.setTitle("Confirm ...");
                alertDialog.setMessage("You want to log out");
                alertDialog.setIcon(R.mipmap.ic_launcher);
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPrefs.getInstance().clear();
                                Toast.makeText(HomeActivity.this, "You have been logged out", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }
                        });
                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
                break;

            }
            default: return false;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void getDataIntent() {
        userModel = SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class);
    }

    private void showLoading() {
        progressDialog = new ProgressDialog(HomeActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
    }
}
