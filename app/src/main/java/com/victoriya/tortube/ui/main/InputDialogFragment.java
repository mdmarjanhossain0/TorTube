package com.victoriya.tortube.ui.main;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.victoriya.tortube.R;
import com.victoriya.tortube.service.ServiceHandler;
import com.victoriya.tortube.viewmodel.LinkShareViewModel;

public class InputDialogFragment extends DialogFragment {

    private static final String TAG=InputDialogFragment.class.getSimpleName();


//    private LinkShareViewModel shareViewModel;
    private EditText addMagnetLink;
//    private ServiceHandler serviceHandler;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        serviceHandler= ServiceHandler.getInstance((MainActivity)getActivity());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

//        shareViewModel=new ViewModelProvider(getActivity()).get(LinkShareViewModel.class);

        View view=getActivity().getLayoutInflater().inflate(R.layout.fragment_input_dialog,null);
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getContext());
        addMagnetLink = view.findViewById(R.id.link);

        alertDialog.setView(view)
                .setTitle("Add Magnet Link")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(addMagnetLink.getText()!=null){

                            String stringLink=addMagnetLink.getText().toString().trim();
                            ((MainActivity)getActivity()).serviceStarter(stringLink);
                            navigateCheckDialogFragment();
                            addMagnetLink.setText(null);
                        }
                        dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getDialog().dismiss();
                    }
                });

        AlertDialog create = alertDialog.create();
        create.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return create;
    }


    private void navigateCheckDialogFragment() {
        NavDirections action = InputDialogFragmentDirections.actionInputDialogFragmentToCheckingDialogFragment();
        Navigation.findNavController(getActivity(),R.id.nav_host_fragment)
                .navigate(action);
    }
}