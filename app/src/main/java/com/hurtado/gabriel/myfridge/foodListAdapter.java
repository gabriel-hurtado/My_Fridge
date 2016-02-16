package com.hurtado.gabriel.myfridge;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

class foodListAdapter extends ArrayAdapter<foodItem> {


    private final List<foodItem> items;
    private final int  layoutResourceId;
    private final Context context;
    private int listPosition = -1;
    private FoodHolder holder;

    private DbAdapter dbHelper;

    public foodListAdapter(Context context, List<foodItem> items) {
        super(context, R.layout.item, items);
        this.layoutResourceId = R.layout.item;
        this.context = context;
        this.items = items;

    }

    public void setDateSet(String dateSet) {
        View v = MainActivity.foodListView.getChildAt(listPosition-MainActivity.foodListView.getFirstVisiblePosition());
        holder.foodItem = items.get(listPosition);
        holder.date = (Button) v.findViewById(R.id.datePickerFrom);
        holder.foodItem.setDate(dateSet);
        String text=getContext().getString(R.string.Exp) + dateSet;
        holder.date.setText(text);
        dbHelper = new DbAdapter(getContext());
        dbHelper.open();
        if (dbHelper.updatedetails(holder.foodItem.getPosition(), holder.foodItem.getName(), holder.foodItem.getDate()) == 0) {
            holder.foodItem.setPosition(dbHelper.createDate(holder.foodItem.getName(), holder.foodItem.getDate()));
        }
        dbHelper.close();



    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new FoodHolder();

        holder.foodItem = items.get(position);

        holder.removePaymentButton = (ImageButton) row.findViewById(R.id.remove);
        holder.removePaymentButton.setTag(holder.foodItem);

        holder.name = (TextView) row.findViewById(R.id.name);


        setNameTextChangeListener(holder);

        holder.date = (Button) row.findViewById(R.id.datePickerFrom);


        holder.date
                .setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        listPosition = position;
                        MainActivity.showDatePickerDialog();


                    }
                });


        row.setTag(holder);

        setupItem(holder);
        return row;
    }


    private void setupItem(FoodHolder holder) {
        holder.name.setText(holder.foodItem.getName());
        String text=getContext().getString(R.string.expireAt) + holder.foodItem.getDate();
        holder.date.setText(text);

    }

    private void setNameTextChangeListener(final FoodHolder holder) {
       holder.name.addTextChangedListener(new TextWatcher() {

           @Override
           public void onTextChanged(CharSequence s, int start, int before, int count) {

           }

           @Override
           public void beforeTextChanged(CharSequence s, int start, int count, int after) {
           }


           @Override
           public void afterTextChanged(Editable s) {
               holder.foodItem.setName(s.toString());

               if (MainActivity.finishedLoading) {
                   dbHelper = new DbAdapter(getContext());
                   dbHelper.open();
                   if (dbHelper.updatedetails(holder.foodItem.getPosition(), holder.foodItem.getName(), holder.foodItem.getDate()) == 0) {
                       holder.foodItem.setPosition(dbHelper.createDate(holder.foodItem.getName(), holder.foodItem.getDate()));
                   }
                   dbHelper.close();
               }

           }

       });

    }


    public static class FoodHolder {
        foodItem foodItem;
        TextView name;
        Button date;
        ImageButton removePaymentButton;
    }

}