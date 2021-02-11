package com.gkemon.XMLtoPDF;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.text.TextUtils;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.core.content.ContextCompat;

import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdfGenerator {

    public static int a4HeightInPX = 3508;
    public static int a4WidthInPX = 2480;
    public static int a5HeightInPX = 1748;
    public static int a5WidthInPX = 2480;

    public static ContextStep getBuilder() {
        return new Builder();
    }

    public enum PageSize {
        A4, A5
    }


    public interface ContextStep {
        FromSourceStep setContext(Context context);
    }

    public interface FromSourceStep {
        LayoutXMLSourceIntakeStep fromLayoutXMLSource();

        ViewIDSourceIntakeStep fromViewIDSource();

        ViewSourceIntakeStep fromViewSource();
    }


    public interface ViewSourceIntakeStep {
        PageSizeStep fromView(View... viewList);

        PageSizeStep fromViewList(List<View> viewList);
    }

    public interface LayoutXMLSourceIntakeStep {
        PageSizeStep fromLayoutXML(@LayoutRes Integer... layoutXMLs);

        PageSizeStep fromLayoutXMLList(@LayoutRes List<Integer> layoutXMLList);
    }

    public interface ViewIDSourceIntakeStep {
        PageSizeStep fromViewID(Activity activity, @IdRes Integer... xmlResourceList);

        PageSizeStep fromViewIDList(Activity activity, @IdRes List<Integer> xmlResourceList);

    }


    public interface PageSizeStep {
        FileNameStep setDefaultPageSize(PageSize pageSize);

        /**
         * Need to improvement.
         *
         * @param widthInPX
         * @param heightInPX
         * @return
         */
        FileNameStep setCustomPageSize(int widthInPX, int heightInPX);
    }


    public interface FileNameStep {
        Build setFileName(String fileName);
    }

    public interface Build {
        void build(PdfGeneratorListener pdfGeneratorListener);

        Build setFolderName(String folderName);

        Build openPDFafterGeneration(boolean open);

    }


    public static class Builder implements Build, FileNameStep, PageSizeStep
            , LayoutXMLSourceIntakeStep, ViewSourceIntakeStep, ViewIDSourceIntakeStep
            , FromSourceStep, ContextStep {

        private static int NO_XML_SELECTED_YET = -1;
        private int pageWidthInPixel = a4WidthInPX;
        private int pageHeightInPixel = a4HeightInPX;
        private Context context;
        private PageSize pageSize;
        private PdfGeneratorListener pdfGeneratorListener;
        private List<View> viewList = new ArrayList<>();
        private String fileName;
        private String targetPdf;
        private boolean openPdfFile = true;
        private String folderName;
        private String directory_path;

        private void postFailure(String errorMessage) {
            FailureResponse failureResponse = new FailureResponse(errorMessage);
            postLog(errorMessage);
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onFailure(failureResponse);
        }

        private void postFailure(Throwable throwable) {
            FailureResponse failureResponse = new FailureResponse(throwable);
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onFailure(failureResponse);
        }

        private void postLog(String logMessage) {
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.showLog(logMessage);
        }

        private void postSuccess(PdfDocument pdfDocument, File file) {
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onSuccess(new SuccessResponse(pdfDocument, file));
        }

        private void openGeneratedPDF() {
            File file = new File(targetPdf);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(file);
                intent.setDataAndType(uri, "*/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    postFailure(e);
                }
            } else {
                String path = TextUtils.isEmpty(directory_path) ? "null" : directory_path;
                postFailure("PDF file is not existing in storage. Your Generated path is " + path);
            }
        }

        private void print() {

            try {
                if (context != null) {
                    PdfDocument document = new PdfDocument();

                    if (pageSize != null) {
                        if (pageSize == PageSize.A4) {
                            pageHeightInPixel = a4HeightInPX;
                            pageWidthInPixel = a4WidthInPX;
                        } else if (pageSize == PageSize.A5) {
                            pageHeightInPixel = a5HeightInPX;
                            pageWidthInPixel = a5WidthInPX;
                        }
                    } else {
                        postLog("Default page size is not found. Your custom page width is " +
                                pageWidthInPixel+" and custom page height is "+pageHeightInPixel);
                    }


                    if (viewList == null || viewList.size() == 0)
                        postLog("View list null or zero sized");
                    for (int i = 0; i < viewList.size(); i++) {

                        //https://stackoverflow.com/a/45529971/7200133
                        /*https://stackoverflow.com/questions/5536066/convert-view-to-bitmap-on-android
                        https://stackoverflow.com/questions/41356494/how-to-get-bitmap-of-a-view
                        https://stackoverflow.com/questions/44583285/scrollview-to-pdf-and-pdf-to-print-option-in-android-studio*/

                        View content = viewList.get(i);

                        /*These thresholds are for pixel to postScript unit*/
                        double thresholdInWidth = 0.75;
                        double thresholdInHeight = 0.75;

                        if (content instanceof ScrollView) {
                            content.measure(View.MeasureSpec.makeMeasureSpec(pageWidthInPixel, View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED);
                                /*For ignore the height size if it is a scrollview and view size is
                                  more then default A4 size page.*/
                            if (content.getMeasuredHeight() >= a4HeightInPX) {
                                pageHeightInPixel = content.getMeasuredHeight();
                                thresholdInHeight = 1.00;
                            } else {
                                // Otherwise standard A4 page height will be ignored.
                                pageHeightInPixel = a4HeightInPX;
                            }
                        } else content.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);

                        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder((int) (pageWidthInPixel * thresholdInWidth),
                                (int) (pageHeightInPixel * thresholdInHeight), i + 1).create();
                        PdfDocument.Page page = document.startPage(pageInfo);

                        pageHeightInPixel = page.getCanvas().getHeight();
                        pageWidthInPixel = page.getCanvas().getWidth();

                        content.measure(View.MeasureSpec.makeMeasureSpec(pageWidthInPixel, View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(pageHeightInPixel, View.MeasureSpec.EXACTLY));
                        content.layout(0, 0, pageWidthInPixel, pageHeightInPixel);
                        content.draw(page.getCanvas());

                        document.finishPage(page);

                    }

                    //This is for prevent crashing while opening generated PDF.
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    if (Environment.getExternalStorageDirectory() != null &&
                             !TextUtils.isEmpty(Environment.getExternalStorageDirectory().getPath())) {
                        directory_path = Environment.getExternalStorageDirectory().getPath();
                    } else if (context.getExternalFilesDir(null) != null &&
                            !TextUtils.isEmpty(context.getExternalFilesDir(null).getAbsolutePath())) {
                        postLog("Environment.getExternalStorageDirectory() is returning null");
                        directory_path = context.getExternalFilesDir(null).getAbsolutePath();
                    }

                    if (TextUtils.isEmpty(directory_path)) {
                        postFailure("Environment.getExternalStorageDirectory() and " +
                                "context.getExternalFilesDir()" +
                                " is returning null");
                        return;
                    }


                    directory_path = directory_path + "/" + folderName + "/";


                    File file = new File(directory_path);
                    if (!file.exists()) {
                        if (!file.mkdirs()) {
                            postLog("Folder is not created." +
                                    "file.mkdirs() is returning false");
                        }
                        //Folder is made here
                    }

                    targetPdf = directory_path + fileName + ".pdf";

                    File filePath = new File(targetPdf);
                    //File is created under the folder but not yet written.
                    try {
                        document.writeTo(new FileOutputStream(filePath));
                        //File writing is done.
                        postSuccess(document, filePath);
                    } catch (IOException e) {
                        postFailure(e);
                    }
                    document.close();

                    if (openPdfFile) {
                        openGeneratedPDF();
                    }
                } else {
                    postFailure("Context is null");
                }
            } catch (Exception e) {
                postFailure(e);
            }

        }

        @Override
        public void build(PdfGeneratorListener pdfGeneratorListener) {
            this.pdfGeneratorListener = pdfGeneratorListener;
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                print();
            } else {
                postLog("WRITE_EXTERNAL_STORAGE Permission is not given." +
                        " Permission taking popup (using https://github.com/Karumi/Dexter) is going " +
                        "to be shown");
                Dexter.withContext(context)
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                print();
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                postLog("WRITE_EXTERNAL_STORAGE Permission is denied by user.");
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest,
                                                                           PermissionToken permissionToken) {
                            }
                        })
                        .withErrorListener(new PermissionRequestErrorListener() {
                            @Override
                            public void onError(DexterError error) {
                                postLog("Error from Dexter (https://github.com/Karumi/Dexter) : " +
                                        error.toString());
                            }
                        }).check();
            }

        }


        @Override
        public PageSizeStep fromView(View... viewArrays) {
            viewList = new ArrayList<>(Arrays.asList(viewArrays));
            return this;
        }

        @Override
        public PageSizeStep fromViewList(List<View> viewList) {
            this.viewList = viewList;
            return this;
        }


        @Override
        public Build openPDFafterGeneration(boolean openPdfFile) {
            this.openPdfFile = openPdfFile;
            return this;
        }


        @Override
        public FromSourceStep setContext(Context context) {
            this.context = context;
            return this;
        }

        @Override
        public FileNameStep setDefaultPageSize(PageSize pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        @Override
        public FileNameStep setCustomPageSize(int widthInPX, int heightInPX) {
            this.pageWidthInPixel = widthInPX;
            this.pageHeightInPixel = heightInPX;
            return this;
        }

        @Override
        public Build setFolderName(String folderName) {
            this.folderName = folderName;
            return this;
        }

        @Override
        public Build setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }


        @Override
        public PageSizeStep fromViewID(Activity activity, @IdRes Integer... viewIDs) {
            viewList = Utils.getViewListFromID(activity, Arrays.asList(viewIDs));
            return this;
        }

        @Override
        public PageSizeStep fromViewIDList(Activity activity, List<Integer> viewIDList) {
            viewList = Utils.getViewListFromID(activity, viewIDList);
            return this;
        }


        @Override
        public PageSizeStep fromLayoutXML(@LayoutRes Integer... layouts) {
            viewList = Utils.getViewListFromLayout(context, pdfGeneratorListener, Arrays.asList(layouts));
            return this;
        }

        @Override
        public PageSizeStep fromLayoutXMLList(@LayoutRes List<Integer> layoutXMLList) {
            viewList = Utils.getViewListFromLayout(context, pdfGeneratorListener, layoutXMLList);
            return this;
        }

        @Override
        public LayoutXMLSourceIntakeStep fromLayoutXMLSource() {
            return this;
        }

        @Override
        public ViewIDSourceIntakeStep fromViewIDSource() {
            return this;
        }

        @Override
        public ViewSourceIntakeStep fromViewSource() {
            return this;
        }
    }


}
