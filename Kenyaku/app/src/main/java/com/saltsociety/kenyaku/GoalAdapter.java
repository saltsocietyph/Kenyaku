package com.saltsociety.kenyaku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GoalAdapter extends ArrayAdapter<GoalItem> {
    private Context context;
    private List<GoalItem> goalList = new ArrayList<>();

    public GoalAdapter(@NonNull Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<GoalItem> list) {
        super(context, 0 , list);
        this.context = context;
        goalList  = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.layout_goal_item, parent,false);

        GoalItem goal = goalList.get(position);

        ImageView goalTypeIcon = listItem.findViewById(R.id.goalTypeIcon);
        goalTypeIcon.setImageResource(goal.getGoalTypeIcon());

        TextView goalName = listItem.findViewById(R.id.goalNameText);
        goalName.setText(goal.getGoalName());

        TextView goalCreatedDate = listItem.findViewById(R.id.dateText);
        goalCreatedDate.setText(goal.getDateGoalCreated());

        return listItem;
    }
}
