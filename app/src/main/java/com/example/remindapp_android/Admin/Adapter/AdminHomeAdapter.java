package com.example.remindapp_android.Admin.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.remindapp_android.Model.Users;
import com.example.remindapp_android.R;

import java.util.ArrayList;


public class AdminHomeAdapter extends RecyclerView.Adapter<AdminHomeAdapter.MyViewHolder> {

    Context context;
    ArrayList<Users> users;

    public AdminHomeAdapter(Context context, ArrayList<Users> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_admin, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Users user = users.get(position);
        holder.tv_userName.setText(user.getName());
        holder.tv_userEmail.setText(user.getEmail());
        holder.tv_userPhone.setText(user.getPhone());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_userName,tv_userEmail,tv_userPhone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_userName = itemView.findViewById(R.id.tv_userName);
            tv_userEmail = itemView.findViewById(R.id.tv_userEmail);
            tv_userPhone = itemView.findViewById(R.id.tv_userPhone);
        }
    }
}
