package com.breezytechdevelopers.healthapp.ui.firstAid;

import androidx.fragment.app.DialogFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.breezytechdevelopers.healthapp.R;
import com.breezytechdevelopers.healthapp.database.entities.FirstAidTip;

public class FirstAidInfoFragment extends DialogFragment {

    private static InfoCallback infoCallback;

    private FirstAidTip firstAidTip;
    private TextView header;
    private TextView causes_body;
    private TextView symptoms_body;
    private TextView dos_body;
    private TextView dont_body;
    private ImageButton backBtn;
    private Button messageBtn;

    public void setContents(FirstAidTip firstAidTip) {
        this.firstAidTip = firstAidTip;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first_aid_info, container, false);

        header = view.findViewById(R.id.header);
        causes_body = view.findViewById(R.id.causes_body);
        symptoms_body = view.findViewById(R.id.symptoms_body);
        dos_body = view.findViewById(R.id.dos_body);
        dont_body = view.findViewById(R.id.dont_body);
        backBtn = view.findViewById(R.id.back);
        messageBtn = view.findViewById(R.id.messageBtn);
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Widget_PopupMenu);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            infoCallback = (FirstAidInfoFragment.InfoCallback) getActivity();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(this.infoCallback.toString()
                    + " must implement NoticeDialogListener");
        }

        header.setText(firstAidTip.getAilment());
        causes_body.setText(firstAidTip.getCauses());
        symptoms_body.setText(firstAidTip.getSymptoms());
        dos_body.setText(firstAidTip.getDos());
        dont_body.setText(firstAidTip.getDonts());
        backBtn.setOnClickListener(view -> {
            infoCallback.onInfoCanceled(false);
            dismiss();
        });

        messageBtn.setOnClickListener(view -> {
            infoCallback.onInfoCanceled(true);
            dismiss();
        });
    }

    public interface InfoCallback {
        void onInfoCanceled(Boolean messageDoctor);
    }
}
