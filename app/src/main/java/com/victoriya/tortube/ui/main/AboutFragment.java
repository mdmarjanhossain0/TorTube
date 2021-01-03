package com.victoriya.tortube.ui.main;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.victoriya.tortube.R;

public class AboutFragment extends DialogFragment {

    public AboutFragment() {
    }

    public static AboutFragment newInstance(String version,String description){

        AboutFragment fragment=new AboutFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view=getActivity().getLayoutInflater().inflate(R.layout.fragment_about,null);
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getContext());

        setVersion(view.findViewById(R.id.about_version));
        setDescription(view.findViewById(R.id.about_description));

        alertDialog.setView(view)
                .setTitle("About TorrentTube")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        AlertDialog create = alertDialog.create();
        create.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return create;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void setDescription(TextView descriptionViewId) {
        descriptionViewId.setText(getString(R.string.about_description));
    }

    private void setVersion(TextView versionViewId) {
        versionViewId.setText("1.0");
    }
}