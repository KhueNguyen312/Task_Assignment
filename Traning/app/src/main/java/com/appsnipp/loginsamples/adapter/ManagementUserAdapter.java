
package com.appsnipp.loginsamples.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.loginsamples.R;
import com.appsnipp.loginsamples.model.User_model.UserModelDetail;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.appsnipp.loginsamples.R.drawable.avtadmin;
import static com.appsnipp.loginsamples.R.drawable.avtchat;
import static com.appsnipp.loginsamples.R.drawable.avtnam;
import static com.appsnipp.loginsamples.R.drawable.avtnunv;
import static com.appsnipp.loginsamples.R.drawable.avtnupng;
import static com.appsnipp.loginsamples.R.drawable.avttre;
import static com.appsnipp.loginsamples.R.drawable.nvnu;
import static com.appsnipp.loginsamples.R.drawable.user_background_shape_status_active;
import static com.appsnipp.loginsamples.R.drawable.user_background_shape_status_inactive;

public class ManagementUserAdapter extends RecyclerView.Adapter<ManagementUserAdapter.MyViewHolder> {
    public ArrayList<UserModelDetail> userModelList;
    public ManagementUserItemClicked itemClicked;
    private Context context;

    public ManagementUserAdapter(Context context) {
        this.context = context;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_emp_name;
        TextView tv_role;
        LinearLayout ln_trangthaiuser;
        AppCompatImageView iv_avatar_user;


        RelativeTimeTextView end_date;

        MyViewHolder(final View view) {
            super(view);

            tv_emp_name = view.findViewById(R.id.tv_emp_name);
            ln_trangthaiuser = view.findViewById(R.id.ln_trangthaiuser);
            tv_role = view.findViewById(R.id.tv_role);
            iv_avatar_user = view.findViewById(R.id.iv_avatar_user);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    return true;
                }
            });
        }

        void initData(final UserModelDetail m) {

            if (m.getSex() == 0) {
                int namsinh = Integer.parseInt(m.getDob().substring(0, 4));
                if (m.getRole() == 0) {

                    iv_avatar_user.setImageResource(avtadmin);

                } else if (m.getRole() != 0 && namsinh < 2000) {

                    iv_avatar_user.setImageResource(avtchat);

                } else if (m.getRole() != 0 && namsinh < 1998) {

                    iv_avatar_user.setImageResource(avtnam);

                } else {

                    iv_avatar_user.setImageResource(avttre);
                }
            } else {
                if (m.getRole() == 0) {
                    iv_avatar_user.setImageResource(avtnupng);
                } else {
                    iv_avatar_user.setImageResource(nvnu);
                }

            }
            tv_emp_name.setText(m.getName());
            if(m.getRole()==0)

        {
            tv_role.setText("Admin");

        }else

        {
            tv_role.setText("User");

        }
            itemView.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick (View v){
            itemClicked.onItemClickeduser(getAdapterPosition(), m);
        }
        });
    }


}

    public ManagementUserAdapter(ArrayList<UserModelDetail> userModelList) {
        this.userModelList = userModelList;
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    @NonNull
    @Override
    public ManagementUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        return new ManagementUserAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ManagementUserAdapter.MyViewHolder holder, final int position) {

        UserModelDetail userModel = userModelList.get(position);
        holder.initData(userModel);
        if (userModel.getStatus() == 1) {
            holder.ln_trangthaiuser.setBackgroundResource(user_background_shape_status_active);
        } else {
            holder.ln_trangthaiuser.setBackgroundResource(user_background_shape_status_inactive);

        }

    }


    public Calendar setendate(String enddate) throws ParseException {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
        Date date1 = formater.parse(enddate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        return calendar;
    }


}
