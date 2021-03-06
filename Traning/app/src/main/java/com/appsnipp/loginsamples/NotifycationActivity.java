package com.appsnipp.loginsamples;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.appsnipp.loginsamples.adapter.TaskAdapter;
import com.appsnipp.loginsamples.adapter.TaskItemClicked;
import com.appsnipp.loginsamples.adapter.TaskItemnotifiClicked;
import com.appsnipp.loginsamples.adapter.TaskNotifiAdapter;
import com.appsnipp.loginsamples.adapter.TaskbyUserAdapter;
import com.appsnipp.loginsamples.model.API.APIClient;
import com.appsnipp.loginsamples.model.API.RequestAPI;
import com.appsnipp.loginsamples.model.Project_model.ProjectModel;
import com.appsnipp.loginsamples.model.Project_model.Project_edit_model;
import com.appsnipp.loginsamples.model.Task_model.DataTasknotify;
import com.appsnipp.loginsamples.model.Task_model.NotifyTask;
import com.appsnipp.loginsamples.model.Task_model.TaskListResponse;
import com.appsnipp.loginsamples.model.Task_model.TaskModel;
import com.appsnipp.loginsamples.model.User_model.User;
import com.appsnipp.loginsamples.task.AddingTaskActivity;
import com.appsnipp.loginsamples.task.DetailTaskActivity;
import com.appsnipp.loginsamples.task.TaskActivity;
import com.appsnipp.loginsamples.utils.SharedPrefs;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.appsnipp.loginsamples.HomeActivity.USER_MODEL_KEY;

public class NotifycationActivity  extends AppCompatActivity implements TaskItemnotifiClicked {
    private TaskNotifiAdapter mAdapter;
    private TaskbyUserAdapter mAdapteruser;
    private RecyclerView recyclerView;
    private NotifyTask mTaskModelList;
    private TaskModel taskModel;
    private ProgressDialog progressDialog;
    private TextView title;
    private static final int RESULT = 1;
    private TextInputEditText tv_enddate_addproject, tv_full_name_project_addproject;
    private Spinner spinner;
    String[] status_item = {"OPEN", "DONE"};
    private int mYear, mMonth, mDay;
    ArrayAdapter<String> spinnerAdapter;
    int status_id = 0;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifycation);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        title = findViewById(R.id.toolbar_title_task);
        title.setText("Notification");

        toolbar.setTitleTextColor(this.getResources().getColor(R.color.whiteTextColor));
        setSupportActionBar(toolbar);


        final int role = SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class).getRole();

        setupRecyclerView();
        getTaskListData();


        showLoading();


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showLoading();
                getTaskListData();
                swipeContainer.setRefreshing(false);

            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }



    /*    private void getTaskListDatauser() {
            String token = SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class).getToken();
            String id = SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class).getId();

            RequestAPI service = APIClient.getClient().create(RequestAPI.class);
            Call<TaskListResponse> call = service.getTasktofUser(token,id, model.getId());
            call.enqueue(new Callback<TaskListResponse>() {
                @Override
                public void onResponse(@NonNull Call<TaskListResponse> call, @NonNull Response<TaskListResponse> response) {
                    progressDialog.dismiss();
                    TaskListResponse models = response.body();
                    if (models != null) {
                        mTaskModelList = models;
                        mAdapteruser.taskModelList = mTaskModelList.getDataTask();
                        mAdapteruser.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TaskListResponse> call, @NonNull Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(TaskActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                }
            });
        }*/
    private void getTaskListData() {
        String token = SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class).getToken();
        String userid = SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class).getId();


        RequestAPI service = APIClient.getClient().create(RequestAPI.class);
        Call<NotifyTask> call = service.gettask_notify(token,userid );
        call.enqueue(new Callback<NotifyTask>() {
            @Override
            public void onResponse(@NonNull Call<NotifyTask> call, @NonNull Response<NotifyTask> response) {
                progressDialog.dismiss();
                NotifyTask models = response.body();
                if (models != null) {
                    mTaskModelList = models;
                    mAdapter.taskModelList = mTaskModelList.getDataTask();
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NotifyTask> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(NotifycationActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading() {
        progressDialog = new ProgressDialog(NotifycationActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
    }

    private void setupRecyclerView() {
        RecyclerView mRecyclerView = findViewById(R.id.rv_project);
        //mRecyclerView.hasFixedSize();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new TaskNotifiAdapter(new ArrayList<DataTasknotify>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.itemClicked = this;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT && resultCode == Activity.RESULT_OK) {
            assert data != null;
            String temp = data.getStringExtra("taskmodel");
            assert temp != null;
            if (temp.equals("what do you want")) {

            }
        }
    }



    @Override
    public void onItemClickedTasknotify(int position, DataTasknotify model) {

        Intent intent = new Intent(this, DetailTaskActivity.class);
        TaskModel modeltask =new TaskModel();
        modeltask.setId(model.getId());
        modeltask.setContent(model.getContent());
        modeltask.setCreatedDate("");
        modeltask.setStatus(model.getStatus());
        modeltask.setName(model.getName());
        modeltask.setEndDate(model.getEndDate());


        intent.putExtra("taskmodel", modeltask);
        intent.putExtra("UserNameOfTask", SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class).getName());
        startActivityForResult(intent, RESULT);
    }

    public void backProject_Task(View view) {
        finish();
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
//        setupRecyclerView();
        getTaskListData();


        //Refresh your stuff here
    }





    private String setendate(String trim) {
        String[] date = trim.split("-");
        String dob = date[2] + "-" + date[1] + "-" + date[0];
        return dob;
    }

    private String getdate(String trim) {
        String[] date = trim.split("-");
        String dob = date[2].substring(0, 2) + "-" + date[1] + "-" + date[0];
        return dob;
    }

    public void back_project_task(View view) {

        finish();
    }


}

