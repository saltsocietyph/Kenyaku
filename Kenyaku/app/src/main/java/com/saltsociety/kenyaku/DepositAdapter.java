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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class DepositAdapter extends ArrayAdapter<DepositItem> {
    private Context context;
    private List<DepositItem> depositList;

    public DepositAdapter(@NonNull android.content.Context context, @SuppressLint("SupportAnnotationUsage") @LayoutRes ArrayList<DepositItem> list) {
        super(context, 0 , list);
        this.context = context;
        depositList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.layout_deposit, parent,false);

        DepositItem deposit = depositList.get(position);

        TextView goalName = listItem.findViewById(R.id.goalNameText);
        goalName.setText(deposit.getGoalName());

        TextView date = listItem.findViewById(R.id.dateText);
        date.setText(deposit.getDepositDate());

        TextView amount = listItem.findViewById(R.id.amountText);
        amount.setText("Deposit Amount: " + deposit.getAmount());

        return listItem;
    }
}
