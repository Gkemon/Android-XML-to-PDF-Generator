package com.gkemon.XMLtoPDF;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.gkemon.XMLtoPDF.model.FailureResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gk Emon on 8/28/2020.
 */
public class Utils {
    public static List<View> getViewListFromID(Activity activity,
                                               @IdRes List<Integer> viewIDList,
                                               PdfGeneratorListener pdfGeneratorListener) {
        List<View> viewList = new ArrayList<>();
        try {
            if (activity != null) {
                for (int viewID : viewIDList) {
                    if (activity.findViewById(android.R.id.content) != null &&
                            activity.findViewById(android.R.id.content).findViewById(viewID) != null) {
                        viewList.add(activity.findViewById(android.R.id.content).findViewById(viewID));
                    } else if (pdfGeneratorListener != null) {
                        pdfGeneratorListener
                                .onFailure(new FailureResponse("Your provided activity is " +
                                        "not containing your desired view. Please make sure that you are using " +
                                        "right xml as activity content." +
                                        "Visit the doc for more clearance -" +
                                        " https://github.com/GkEmonGON/Android-XML-to-PDF-Generator/blob/master/README.md"));

                    }
                }
            } else if (pdfGeneratorListener != null) {
                pdfGeneratorListener
                        .onFailure(new FailureResponse("Please provide a valid activity." +
                                " You are providing null activity."));
            }

        } catch (Exception exception) {
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onFailure(new FailureResponse(exception, "Error is happening in" +
                        " getViewListFromID() while creating Java's view object(s) from view ids"));
        }

        return viewList;
    }

    /**
     * @param viewList
     * @param pdfGeneratorListener
     * @return return views after measuring their roots by UNSPECIFIED otherwise view size won't be
     * same in PDF as like as XML.
     */
    public static List<View> getViewListMeasuringRoot(@NonNull List<View> viewList,
                                                      PdfGeneratorListener pdfGeneratorListener) {
        List<View> result = new ArrayList<>();
        try {
            for (View view : viewList) {
                if (view != null) {
                    view.getRootView()
                            .measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    result.add(view);
                }
            }
        } catch (Exception exception) {
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onFailure(new FailureResponse(exception, "Error is happening in" +
                        " getViewListMeasuringRoot() while creating Java's view object(s) from view ids"));
        }

        return result;
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
                pdfGeneratorListener.onFailure(new FailureResponse(e,
                        "Error is happening in" +
                                " getViewListFromLayout() while creating Java's view object(s) from layout" +
                                " resources"));
        }
        return viewList;
    }
}
