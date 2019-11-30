package com.rdi.shareqr;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.rdi.shareqr.generator.GeneratorHost;


public class InputFragment extends Fragment {
    private View btnGenerate;
    private TextView textForGenerateQr;

    public InputFragment() {
        super(R.layout.fragment_input);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = super.onCreateView(inflater, container, savedInstanceState);

        btnGenerate = root.findViewById(R.id.btn_generate);
        textForGenerateQr = root.findViewById(R.id.edit_text);

        textForGenerateQr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnGenerate.setEnabled(s.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Такой трюк убирает неприятное мерцание IME (клавиатуры).
                // Скроем её с экрана, а потом запустим нужный фрагмент с крошечной задержкой
                InputMethodManager inputService = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputService.hideSoftInputFromWindow(textForGenerateQr.getWindowToken(), 0);

                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((GeneratorHost) requireActivity()).proceedToGeneration(textForGenerateQr.getText().toString());
                    }
                }, 50);
            }
        });
        return root;
    }
}
