package com.gkemon.XMLtoPDF;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;

import com.gkemon.XMLtoPDF.model.FailureResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gk Emon on 8/28/2020.
 */
public class Utils {
    public static List<View> getViewListFromID(Activity activity, @LayoutRes Integer relatedParentLayout, @IdRes List<Integer> viewIDList, PdfGeneratorListener pdfGeneratorListener) {
        List<View> viewList = new ArrayList<>();
        try {
            if (activity != null) {
                LayoutInflater inflater = (LayoutInflater)
                        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                for (int viewID : viewIDList) {
                    View generatedView = inflater.inflate(relatedParentLayout, null);
                    viewList.add(generatedView.findViewById(viewID));
                }
            }
        } catch (Exception exception) {
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onFailure(new FailureResponse(exception, "Error is happening in" +
                        " getViewListFromID() while creating Java's view object(s) from view ids"));
        }

        return viewList;
    }

    public static List<View> getViewListFromLayout(Context context,
                                                   PdfGeneratorListener pdfGeneratorListener,
                                                   List<Integer> layoutList) {
        List<View> viewList = new ArrayList<>();

        try {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            for (int layout : layoutList) {
                View generatedView = inflater.inflate(layout, null);
                viewList.add(generatedView);
            }

        } catch (Exception e) {
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onFailure(new FailureResponse(e, "Error is happening in" +
                        " getViewListFromLayout() while creating Java's view object(s) from layout resources"));
        }
        return viewList;
    }
}
