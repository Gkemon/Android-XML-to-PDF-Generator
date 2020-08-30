package com.gkemon.XMLtoPdf;

import com.gkemon.XMLtoPdf.model.FailureResponse;
import com.gkemon.XMLtoPdf.model.SuccessResponse;

interface PdfGeneratorContract {
    void onSuccess(SuccessResponse response);

    void showLog(String log);

    void onFailure(FailureResponse failureResponse);
}

public abstract class PdfGeneratorListener implements PdfGeneratorContract {
    @Override
    public void showLog(String log) {

    }

    @Override
    public void onSuccess(SuccessResponse response) {

    }

    @Override
    public void onFailure(FailureResponse failureResponse) {

    }
}
