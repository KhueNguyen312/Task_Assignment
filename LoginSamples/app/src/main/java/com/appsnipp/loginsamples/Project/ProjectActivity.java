package com.appsnipp.loginsamples.Project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.loginsamples.CHAT.ChatActivity;
import com.appsnipp.loginsamples.R;
import com.appsnipp.loginsamples.Task.TaskActivity;
import com.appsnipp.loginsamples.adapter.ProjectItemClicked;
import com.appsnipp.loginsamples.adapter.ProjectAdapter;
import com.appsnipp.loginsamples.model.APIClient;
import com.appsnipp.loginsamples.model.ProjectModel;
import com.appsnipp.loginsamples.model.RequestAPI;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProjectActivity extends AppCompatActivity implements ProjectItemClicked {

    private FloatingActionButton btn_add;
    private ProjectAdapter mAdapter;
    private ArrayList<ProjectModel> mProjectModelList;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Danh sách Project");
        toolbar.setTitleTextColor(this.getResources().getColor(R.color.whiteTextColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("Danh sách Project");

        setupRecyclerView();

        getProjectListData();
        showLoading();

        btn_add = (FloatingActionButton) findViewById(R.id.btn_add_project);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(ProjectActivity.this);
                View dialogViewAddProject = li.inflate(R.layout.dialog_add_project, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectActivity.this);
                alertDialogBuilder.setView(dialogViewAddProject);

                alertDialogBuilder.setCancelable(false).setPositiveButton("Create",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(ProjectActivity.this, "Bạn đã tạo project THÀNH CÔNG", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNeutralButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Toast.makeText(ProjectActivity.this, "Bạn đã HUỶ tạo project mới", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                });
                alertDialogBuilder.show();
            }
        });
    }

    private void setupToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Danh sách Project");
        toolbar.setTitleTextColor(this.getResources().getColor(R.color.whiteTextColor));
        setSupportActionBar(toolbar);
    }

    private void getProjectListData() {
        RequestAPI service = APIClient.getClient().create(RequestAPI.class);
        Call<ArrayList<ProjectModel>> call = service.getALLProject();
        call.enqueue(new Callback<ArrayList<ProjectModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<ProjectModel>> call, @NonNull Response<ArrayList<ProjectModel>> response) {
                progressDialog.dismiss();
//                generateDataList(response.body());
                ArrayList<ProjectModel> models = response.body();
                if (models != null){
                    mProjectModelList = models;
                    mAdapter.projectModelList = mProjectModelList;
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<ProjectModel>> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProjectActivity.this, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading() {
        progressDialog = new ProgressDialog(ProjectActivity.this);
        progressDialog.setMessage("Loading....");
        progressDialog.show();
    }

    private void setupRecyclerView() {
        RecyclerView mRecyclerView = findViewById(R.id.rv_project);
        mRecyclerView.hasFixedSize();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ProjectAdapter(new ArrayList<ProjectModel>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.itemClicked=this;
    }

    @Override
    public void onItemClicked(int position, ProjectModel model) {

        Toast.makeText(ProjectActivity.this, "cccc!"+model.getId(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, TaskActivity.class);
        intent.putExtra("projectModel", model);
        startActivity(intent);
    }
    public void addprojectClicked(View view){
        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
    }
}
