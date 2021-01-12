package com.victoriya.tortube.Adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.victoriya.tortube.R;
import com.victoriya.tortube.model.SelectionFile;
import com.victoriya.tortube.ui.main.checking.CheckingDialogFragment;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.SelectionViewHolder> {

    private static final String TAG=SelectionAdapter.class.getSimpleName();

    private List<SelectionFile> fileList=new ArrayList<>();
    private CheckingDialogFragment fragment;


    public SelectionAdapter(CheckingDialogFragment fragment){
        this.fragment=fragment;
    }


    @NonNull
    @Override
    public SelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selection, parent, false);
        return new SelectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectionViewHolder holder, int position) {
        Log.d(TAG,fileList.get(position).toString());
        holder.name.setText(fileList.get(position).getName());
//        holder.size.setText((fileList.get(position).getSize()/(1024*1024))+" MB");
        holder.size.setText(fixedFileSize(fileList.get(position).getSize()));

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.name.setTextColor(Color.BLUE);
                holder.size.setTextColor(Color.BLUE);
                fragment.onIndexSelectionEvent(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG,"selection count "+fileList.size());
        return fileList.size();
    }

    public class SelectionViewHolder extends RecyclerView.ViewHolder{
        TextView name,size;
        public SelectionViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.selection_file_name);
            size=itemView.findViewById(R.id.selection_file_size);
        }
    }

    public void submitList(List<SelectionFile> files){
        if(files!=null){
            this.fileList=files;
            notifyDataSetChanged();
            Log.d(TAG,"selection list submit");
        }
    }

    private String fixedFileSize(long preSize){
        if(preSize<1024 ){
            return preSize+" B";
        }
        else if(preSize>=1024 && (preSize/1024)<1024){
            return preSize/1024+" KB";
        }
        else if((preSize/1024)>=1024 && (preSize/(1024*1024))<1024){
            return (preSize/(1024*1024))+" MB";
        }
        else if((preSize/(1024*1024))>=1024 && (preSize/(1024*1024*1024))<1024){
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            return df.format(((float)preSize/((float)1024*1024*1024)))+" GB";
        }
        return "UNKNOWN";
    }
}
