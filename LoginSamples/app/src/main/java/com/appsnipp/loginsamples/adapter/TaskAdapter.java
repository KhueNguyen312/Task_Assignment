package com.appsnipp.loginsamples.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appsnipp.loginsamples.R;
import com.appsnipp.loginsamples.model.ProjectModel;
import com.github.curioustechizen.ago.RelativeTimeTextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {
    public ArrayList<ProjectModel> projectModelList;
   public MainItemClicked itemClicked;
    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameproject;
        TextView detailproject;
        TextView card_name_project;
        RelativeTimeTextView end_date;
        Boolean isItemClicked = false;
        MyViewHolder(View view) {
            super(view);

            nameproject = view.findViewById(R.id.tv_ten);
            detailproject = view.findViewById(R.id.tv_detail);
            card_name_project = view.findViewById(R.id.card_name_project);
            end_date = view.findViewById(R.id.tv_time_stamp);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isItemClicked){
                        isItemClicked = true;
                        itemView.setBackgroundColor(itemView.getContext().getColor(R.color.green));
                    }else {
                        isItemClicked = false;
                        itemView.setBackgroundColor(itemView.getContext().getColor(R.color.whiteCardColor));
                    }
                    itemClicked.onItemClicked(getAdapterPosition());
                }
            });
        }


    }

    // in this adaper constructor we add the list of messages as a parameter so that
// we will passe  it when making an instance of the adapter object in our activity
    public TaskAdapter(ArrayList<ProjectModel> ProjectModelList) {
        this.projectModelList = ProjectModelList;
    }

    @Override
    public int getItemCount() {
        return projectModelList.size();
    }

    @NonNull
    @Override
    public TaskAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);

        return new TaskAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TaskAdapter.MyViewHolder holder, final int position) {

        ProjectModel m = projectModelList.get(position);
        holder.detailproject.setText(m.getId());
        holder.nameproject.setText(m.getName());
        String fisrt_name = m.getName().substring(0, 1);
        holder.card_name_project.setText(fisrt_name);
        try {
            holder.end_date.setReferenceTime(setendate(m.getEndTime()).getTimeInMillis());
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    public Calendar setendate(String enddate) throws ParseException {
        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        Date date1 = formater.parse(enddate);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        return calendar;
    }


}