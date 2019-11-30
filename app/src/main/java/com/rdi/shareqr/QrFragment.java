package com.rdi.shareqr;


import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.rdi.shareqr.model.QrData;
import com.rdi.shareqr.model.QrGenerator;
import com.rdi.shareqr.model.QrLiveData;


public class QrFragment extends Fragment {

    private static final String ARG_SOURCE = "source";
    private String mSourceText;


    private ImageView mQrCode;
    private View mShareButton;

    private QrLiveData mQrLiveData;


    public QrFragment() {
        super(R.layout.fragment_qr);
    }

    public static QrFragment newInstance(String source) {

        QrFragment fragment = new QrFragment();

        Bundle args = new Bundle();
        args.putString(ARG_SOURCE, source);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSourceText = getArguments().getString(ARG_SOURCE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);


        mQrCode = view.findViewById(R.id.image_for_qr);
        mShareButton = view.findViewById(R.id.btn_share);
        mShareButton.setEnabled(false);


        mQrLiveData = QrGenerator.getInstance(requireContext().getFilesDir()).generate(mSourceText);
        mQrLiveData.observe(this, new Observer<QrData>() {
            @Override
            public void onChanged(QrData qrData) {
                if (qrData != null) {
                    mQrCode.setImageBitmap(qrData.getQrBitmaop());
                    mShareButton.setEnabled(true);
                }
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = QrProvider.getUriForFile(requireContext(),
                        "com.rdi.shareqr",
                        mQrLiveData.getValue().getFile()
                );

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                // Все возможные данные для всех возможных версий ОС и приложений
                shareIntent.setDataAndType(uri, requireContext().getContentResolver().getType(uri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setClipData(ClipData.newUri(requireContext().getContentResolver(), getString(R.string.app_name), uri));
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(shareIntent);
            }
        });
        return view;
    }
}
