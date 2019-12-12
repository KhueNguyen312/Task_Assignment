package com.appsnipp.loginsamples.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.loginsamples.R;
import com.appsnipp.loginsamples.adapter.ChatAdaterUserList;
import com.appsnipp.loginsamples.adapter.ManagementUserItemClicked;
import com.appsnipp.loginsamples.model.API.APIClient;
import com.appsnipp.loginsamples.model.API.RequestAPI;
import com.appsnipp.loginsamples.model.User_model.ListUserModel;
import com.appsnipp.loginsamples.model.User_model.User;
import com.appsnipp.loginsamples.model.User_model.UserModelDetail;
import com.appsnipp.loginsamples.utils.SharedPrefs;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.appsnipp.loginsamples.HomeActivity.USER_MODEL_KEY;

public class ChatFragment extends Fragment implements ManagementUserItemClicked {
    private View view;
    private ProgressDialog progressDialog;
    private ArrayList<UserModelDetail> mUsermodelDetails;
    private ChatAdaterUserList mAdapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        return view;
    }

    private void setupRecyclerView() {
        RecyclerView mRecyclerView = view.findViewById(R.id.rv_list_user_chat);
        mRecyclerView.hasFixedSize();
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ChatAdaterUserList(new ArrayList<UserModelDetail>());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.itemClicked = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListUser();
        setupRecyclerView();
        showLoading();
    }

    private void getListUser() {
        User user = SharedPrefs.getInstance().get(USER_MODEL_KEY, User.class);
        String token = "";
        if (user != null){
            token = user.getToken();
        }

        RequestAPI service = APIClient.getClient().create(RequestAPI.class);
        service.getListUser(token)
                .enqueue(new Callback<ListUserModel>() {
                    @Override
                    public void onResponse(@NonNull Call<ListUserModel> call, @NonNull Response<ListUserModel> response) {
                        progressDialog.dismiss();
                        ListUserModel models = response.body();
                        if (models != null) {
                            mUsermodelDetails = models.getData();
                            mAdapter.userModelList = mUsermodelDetails;
                            mAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ListUserModel> call, @NonNull Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(view.getContext(), "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onItemClickeduser(int position, UserModelDetail model) {
        Toast.makeText(view.getContext(), model.getName(), Toast.LENGTH_SHORT).show();
        Intent i  = new Intent(getActivity(), ChatBoxActivity.class);
        startActivity(i);
    }

    private void showLoading() {
        progressDialog = new ProgressDialog(view.getContext());
        progressDialog.setMessage("Loading....");
        progressDialog.show();
    }
}
