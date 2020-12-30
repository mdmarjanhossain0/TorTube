package com.victoriya.tortube.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.victoriya.tortube.R;
import com.victoriya.tortube.model.Files;
import com.victoriya.tortube.ui.main.MainFragment;

import java.util.ArrayList;
import java.util.List;

public class TorrentAdapter extends RecyclerView.Adapter<TorrentAdapter.TorrentViewHolder> {


    private List<Files> filesList=new ArrayList<>();
    private MainFragment fragment;


    public TorrentAdapter(MainFragment fragment){
        this.fragment=fragment;
    }
    @NonNull
    @Override
    public TorrentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_torrent, parent, false);
        return new TorrentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TorrentViewHolder holder, int position) {
        if(holder!=null){
            bindView(holder,position);
        }
    }

    private void bindView(TorrentViewHolder holder, int position) {
        holder.fileName.setText(filesList.get(position).getFileName());
        holder.fileSize.setText("File Size: "+filesList.get(position).getFileSize()+" MB");
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.adapterClickEvent(filesList.get(position).getMagnetLink());
            }
        });
    }

    @Override
    public int getItemCount() {
        return filesList!=null? filesList.size():0;
    }

    public Files getTorrentItem(int adapterPosition) {
        return filesList.get(adapterPosition);
    }

    public class TorrentViewHolder extends RecyclerView.ViewHolder{
        View layout;
        TextView fileName;
        TextView fileSize;
        public TorrentViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName=itemView.findViewById(R.id.item_text);
            layout=itemView.findViewById(R.id.item_layout);
            fileSize=itemView.findViewById(R.id.item_file_size);
        }
    }


    public void submitList(List<Files> files){
            this.filesList=files;
            notifyDataSetChanged();
    }
}
