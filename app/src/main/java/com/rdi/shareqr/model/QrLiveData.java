package com.rdi.shareqr.model;

import androidx.lifecycle.LiveData;

import com.rdi.shareqr.model.QrData;

public class QrLiveData extends LiveData<QrData> {
    @Override
    protected void postValue(QrData value) {
        super.postValue(value);
    }
}
