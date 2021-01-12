package com.victoriya.tortube.ui.main.input;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.victoriya.tortube.R;
//import com.victoriya.tortube.ui.main.InputDialogFragmentDirections;
import com.victoriya.tortube.ui.main.MainActivity;

public class InputDialogFragment extends DialogFragment {

    private static final String TAG=InputDialogFragment.class.getSimpleName();


//    private LinkShareViewModel shareViewModel;
    private EditText addMagnetLink;
//    private StreamingHandler serviceHandler;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        serviceHandler= StreamingHandler.getInstance((MainActivity)getActivity());
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

//        shareViewModel=new ViewModelProvider(getActivity()).get(LinkShareViewModel.class);

        View view=getActivity().getLayoutInflater().inflate(R.layout.fragment_input_dialog,null);
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getContext());
        addMagnetLink = view.findViewById(R.id.link);


        LinearLayout titleLinearLayout = new LinearLayout(getContext());

        titleLinearLayout.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        titleLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView title = new TextView(getContext());
        title.setText("Add Magnet Link");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        title.setPadding(40, 40, 40, 40);

        titleLinearLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent_cyan));
        titleLinearLayout.addView(title);
        titleLinearLayout.setGravity(LinearLayout.TEXT_ALIGNMENT_CENTER);


        alertDialog.setCustomTitle(titleLinearLayout);

        alertDialog.setView(view)
//                .setTitle("Add Magnet Link")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(addMagnetLink.getText().length()>3){

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