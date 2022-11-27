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
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.View;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PdfGenerator {

    public static double postScriptThreshold = 0.75;
    public final static int a4HeightInPX = 3508;
    public final static int a4WidthInPX = 2480;
    public final static int a5HeightInPX = 1748;
    public final static int a5WidthInPX = 2480;
    public final static String errorMessageOfXMLtoPDFLifecycleObserver = "uri is null from ''xmlToPDFLifecycleObserver'' ";
    public static int a4HeightInPostScript = (int) (a4HeightInPX * postScriptThreshold);
    public static int a4WidthInPostScript = (int) (a4WidthInPX * postScriptThreshold);

    public static int AS_LIKE_XML_WIDTH = 0, AS_LIKE_XML_HEIGHT = 0;

    public static ContextStep getBuilder() {
        return new Builder();
    }

    public enum PageSize {
        /**
         * For standard A4 size page
         *
         * @deprecated For printing well-formed ISO standard sized papers(like-A4,A5 sized pdf,you
         * don't need to be concerned about width and height.Please set width and height to the xml
         * with an aspect ratio 1:√2. For example if your xml width is 100 dp then the height of the
         * xml will be (100 X √2) = 142 dp. Finally when we print them with any kind of ISO standard
         * paper, then they will be auto scaled and fit into the specific paper.
         * Reference:http://tolerancing.net/engineering-drawing/paper-size.html
         */
        @Deprecated
        A4,

        /**
         * For standard A5 size page
         *
         * @deprecated For printing well-formed ISO standard sized papers(like-A4,A5 sized pdf,you
         * don't need to be concerned about width and height.Please set width and height to the xml
         * with an aspect ratio 1:√2. For example if your xml width is 100 dp then the height of the
         * xml will be (100 X √2) = 142 dp. Finally when we print them with any kind of ISO standard
         * paper, then they will be auto scaled and fit into the specific paper.
         * Reference:http://tolerancing.net/engineering-drawing/paper-size.html
         */
        @Deprecated
        A5,
        /**
         * For print the page as much as they are big.
         */
        AS_LIKE_XML
    }


    public interface ContextStep {
        FromSourceStep setContext(ComponentActivity context);
    }

    public interface FromSourceStep {
        LayoutXMLSourceIntakeStep fromLayoutXMLSource();

        ViewIDSourceIntakeStep fromViewIDSource();

        ViewSourceIntakeStep fromViewSource();
    }


    public interface ViewSourceIntakeStep {
        /**
         * @param viewList MUST NEED TO set android:layout_width A FIXED
         *                 VALUE INSTEAD OF "wrap_content" and "match_parent" OTHERWISE SIZING COULD BE MALFORMED
         *                 IN PDF FOR DIFFERENT DEVICE SCREEN
         */
        FileNameStep fromView(View... viewList);

        /**
         * @param viewList MUST NEED TO set android:layout_width A FIXED
         *                 VALUE INSTEAD OF "wrap_content" and "match_parent" OTHERWISE SIZING COULD BE MALFORMED
         *                 IN PDF FOR DIFFERENT DEVICE SCREEN
         */
        FileNameStep fromViewList(List<View> viewList);
    }

    public interface LayoutXMLSourceIntakeStep {
        FileNameStep fromLayoutXML(@LayoutRes Integer... layoutXMLs);

        FileNameStep fromLayoutXMLList(@LayoutRes List<Integer> layoutXMLList);
    }

    public interface ViewIDSourceIntakeStep {
        /**
         * @param containingActivity Host activity where all views reside.
         * @param xmlResourceList    The view ids which will be printed.
         */
        FileNameStep fromViewID(@NonNull Activity containingActivity,
                                @IdRes Integer... xmlResourceList);

        FileNameStep fromViewIDList(@NonNull Activity containingActivity,
                                    @IdRes List<Integer> xmlResourceList);
    }

    public interface PageSizeStep {
        FileNameStep setPageSize(PageSize pageSize);
    }

    public interface FileNameStep {
        Build setFileName(String fileName);
    }

    public interface Build {
        void build(PdfGeneratorListener pdfGeneratorListener);

        Build setFolderNameOrPath(String folderName);

        Build actionAfterPDFGeneration(ActionAfterPDFGeneration open);

        Build savePDFSharedStorage(XmlToPDFLifecycleObserver xmlToPDFLifecycleObserver);

    }

    public enum ActionAfterPDFGeneration {
        OPEN, SHARE, NONE
    }

    public static class Builder implements Build
            , FileNameStep
            , PageSizeStep
            , LayoutXMLSourceIntakeStep
            , ViewSourceIntakeStep
            , ViewIDSourceIntakeStep
            , FromSourceStep, ContextStep {
        private int pageWidthInPixel = AS_LIKE_XML_WIDTH;
        private int pageHeightInPixel = AS_LIKE_XML_HEIGHT;
        private Context context;
        private PdfGeneratorListener pdfGeneratorListener;
        private List<View> viewList = new ArrayList<>();
        private String fileName;
        private ActionAfterPDFGeneration actionAfterPDFGeneration = ActionAfterPDFGeneration.OPEN;
        private String folderName;
        private String directoryPath;
        private Disposable disposable;
        private XmlToPDFLifecycleObserver xmlToPDFLifecycleObserver;

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

        private void postOnGenerationStart() {
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onStartPDFGeneration();
        }

        private void postOnGenerationFinished() {
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onFinishPDFGeneration();
        }

        private void postSuccess(PdfDocument pdfDocument,
                                 File file,
                                 int widthInPS,
                                 int heightInPS) {
            if (pdfGeneratorListener != null)
                pdfGeneratorListener.onSuccess(
                        new SuccessResponse(
                                pdfDocument,
                                file,
                                widthInPS,
                                heightInPS));
        }

        private void dealAfterGeneration(ActionAfterPDFGeneration actionAfterPDFGeneration,
                                         File file) {
            try {
                if (actionAfterPDFGeneration != ActionAfterPDFGeneration.NONE) {
                    if (file.exists()) {
                        Intent intent;
                        if (actionAfterPDFGeneration == ActionAfterPDFGeneration.OPEN)
                            intent = new Intent(Intent.ACTION_VIEW);
                        else {
                            intent = new Intent(Intent.ACTION_SEND);
                        }
                        getPDFIntent(file, intent);
                        try {
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            postFailure(e);
                        }
                    } else {
                        String path = TextUtils.isEmpty(directoryPath) ? "null" : directoryPath;
                        postFailure("PDF file is not existing in storage. Your Generated path is " + path);
                    }
                } else {
                    postLog("PDF is generation done but as you set ActionAfterPDFGeneration.NONE" +
                            " so it is not dealing with it after generation");
                }
            } catch (Exception exception) {
                postFailure("Error occurred while opening the PDF. Error message : " + exception.getMessage());
            }
        }

        private void dealAfterSavingInSharedStore(ActionAfterPDFGeneration actionAfterPDFGeneration,
                                                  Uri uri) {
            try {
                if (actionAfterPDFGeneration != ActionAfterPDFGeneration.NONE) {
                    Intent intent;
                    if (actionAfterPDFGeneration == ActionAfterPDFGeneration.OPEN)
                        intent = new Intent(Intent.ACTION_VIEW);
                    else {
                        intent = new Intent(Intent.ACTION_SEND);
                    }
                    getPDFIntent(uri, intent);
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        postFailure(e);
                    }
                } else {
                    postLog("PDF is generation done but as you set ActionAfterPDFGeneration.NONE" +
                            " so it is not dealing with it after generation");
                }
            } catch (Exception exception) {
                postFailure("Error occurred while opening the PDF. Error message : " + exception.getMessage());
            }

        }

        private void getPDFIntent(File file, Intent intent) {
            Uri path = getUriForFile(file);
            getPDFIntent(path, intent);
        }

        private void getPDFIntent(Uri path, Intent intent) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, path);
            }
            intent.putExtra(Intent.EXTRA_STREAM, path);
            intent.setDataAndType(path, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

        private Uri getUriForFile(File file) {
            return FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".xmlToPdf.provider",
                    file);
        }

        /**
         * We should reset the value of the page otherwise page size might be differ for each page
         */
        private void resetValue() {
            pageWidthInPixel = AS_LIKE_XML_WIDTH;
            pageHeightInPixel = AS_LIKE_XML_HEIGHT;
            postScriptThreshold = 0.75;
            a4HeightInPostScript = (int) (a4HeightInPX * postScriptThreshold);
        }

        private void print() {

            try {
                if (context != null) {
                    PdfDocument document = new PdfDocument();
                    if (viewList == null || viewList.size() == 0)
                        postLog("View list null or zero sized");
                    for (int i = 0; i < viewList.size(); i++) {
                        resetValue();
                        View content = viewList.get(i);
                        if (pageWidthInPixel == AS_LIKE_XML_WIDTH &&
                                pageHeightInPixel == AS_LIKE_XML_HEIGHT) {
                            pageHeightInPixel = content.getHeight();
                            pageWidthInPixel = content.getWidth();

                            if (pageHeightInPixel == 0 && pageWidthInPixel == 0) {
                                //If view was inflated from XML then getHeight() and getWidth()
                                //So we need to then make it measured.
                                if (content.getMeasuredWidth() == 0 && content.getMeasuredHeight() == 0) {
                                    /*content.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);*/
                                    content.measure(View.MeasureSpec.makeMeasureSpec(pageWidthInPixel, View.MeasureSpec.UNSPECIFIED),
                                            View.MeasureSpec.makeMeasureSpec(pageHeightInPixel, View.MeasureSpec.UNSPECIFIED));
                                }
                                pageHeightInPixel = content.getMeasuredHeight();
                                pageWidthInPixel = content.getMeasuredWidth();
                            }

                            postScriptThreshold = 1.0;
                            a4HeightInPostScript = pageHeightInPixel;
                        }


                        /*Convert page size from pixel into post script because PdfDocument takes
                         * post script as a size unit*/
                        pageHeightInPixel = (int) (pageHeightInPixel * postScriptThreshold);
                        pageWidthInPixel = (int) (pageWidthInPixel * postScriptThreshold);


                        content.measure(View.MeasureSpec.makeMeasureSpec(pageWidthInPixel, View.MeasureSpec.EXACTLY), View.MeasureSpec.UNSPECIFIED);
                        pageHeightInPixel = (Math.max(content.getMeasuredHeight(), a4HeightInPostScript));


                        PdfDocument.PageInfo pageInfo =
                                new PdfDocument.PageInfo.Builder((pageWidthInPixel), (pageHeightInPixel), i + 1).create();
                        PdfDocument.Page page = document.startPage(pageInfo);

                        content.layout(0, 0, pageWidthInPixel, pageHeightInPixel);
                        content.draw(page.getCanvas());

                        document.finishPage(page);

                        /*Finally invalidate it and request layout for restore the previous state
                         * of the view as like as the xml. Otherwise for generating PDF by view id,
                         * the main view is being messed up because this a view is not cloneable and
                         * being modified in the above view related tasks for printing PDF. */
                        content.invalidate();
                        content.requestLayout();

                    }

                    //This is for prevent crashing while opening generated PDF.
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    setUpDirectoryPath(context);

                    if (TextUtils.isEmpty(directoryPath)) {
                        postFailure("Cannot find the storage path to create the pdf file.");
                        return;
                    }

                    if (TextUtils.isEmpty(folderName)) {
                        directoryPath = directoryPath + "/";
                    } else if (folderName.contains("/storage/emulated/")) {
                        directoryPath = folderName + "/";
                    } else
                        directoryPath = directoryPath + "/" + folderName + "/";

                    directoryPath = directoryPath.replace(" ", "_")
                            .replace(",", "")
                            .replace(":", "_");

                    File file = new File(directoryPath);
                    if (!file.exists()) {
                        if (!file.mkdirs()) {
                            postLog("Folder is not created." +
                                    "file.mkdirs() is returning false");
                        }
                        //Folder is made here
                    }

                    String targetPdf = directoryPath + fileName + ".pdf";

                    File fileFinalResult = new File(targetPdf);
                    //File is created under the folder but not yet written.

                    disposeDisposable();
                    postOnGenerationStart();
                    //When user want to save pdf in shared storage
                    if (xmlToPDFLifecycleObserver != null) {
                        xmlToPDFLifecycleObserver.setPdfSaveListener(uri -> {
                            if (uri != null)
                                writePDFOnSavedBlankPDFFile(document, uri);
                            else  {
                                postFailure(errorMessageOfXMLtoPDFLifecycleObserver);
                            }
                        });
                        Intent intent;
                        intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                        intent.putExtra(Intent.EXTRA_TITLE, fileName);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        getPDFIntent(fileFinalResult, intent);
                        xmlToPDFLifecycleObserver.launchPDFSaverPicker(intent);
                    } else {
                        writePDF(document, fileFinalResult);
                    }
                } else {
                    postFailure("Context is null");
                }
            } catch (Exception e) {
                postFailure(e);
            }

        }

        private void writePDFOnSavedBlankPDFFile(PdfDocument document, @NonNull Uri uri) {
            try {
                ParcelFileDescriptor pfd = context.getContentResolver().
                        openFileDescriptor(uri, "w");
                FileOutputStream fileOutputStream =
                        new FileOutputStream(pfd.getFileDescriptor());
                disposable = Completable.fromAction(() ->
                                document.writeTo(fileOutputStream))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doFinally(() -> {
                            document.close();
                            fileOutputStream.close();
                            pfd.close();
                            disposeDisposable();
                            postOnGenerationFinished();
                        })
                        .subscribe(() -> {
                            dealAfterSavingInSharedStore(actionAfterPDFGeneration, uri);
                            postSuccess(
                                    document,
                                    FileUtils.getFile(context, uri),
                                    pageWidthInPixel,
                                    pageHeightInPixel);
                        }, this::postFailure);
            } catch (IOException e) {
                postFailure(e);
            }
        }

        private void writePDF(PdfDocument document, File fileFinalResult) {
            disposable = Completable.fromAction(() ->
                            document.writeTo(new FileOutputStream(fileFinalResult)))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally(() -> {
                        document.close();
                        disposeDisposable();
                        postOnGenerationFinished();
                    })
                    .subscribe(() -> {
                        postSuccess(
                                document,
                                fileFinalResult,
                                pageWidthInPixel,
                                pageHeightInPixel);

                        dealAfterGeneration(actionAfterPDFGeneration, fileFinalResult);
                    }, this::postFailure);
        }


        private void disposeDisposable() {
            if (disposable != null && !disposable.isDisposed())
                disposable.dispose();
        }

        private void setUpDirectoryPath(Context context) {

            String state = Environment.getExternalStorageState();

            // Make sure it's available
            if (!TextUtils.isEmpty(state) && Environment.MEDIA_MOUNTED.equals(state)) {
                postLog("Your external storage is mounted");
                // We can read and write the media
                directoryPath = context.getExternalFilesDir(null) != null ?
                        context.getExternalFilesDir(null).getAbsolutePath() : "";

                if (TextUtils.isEmpty(directoryPath))
                    postLog("context.getExternalFilesDir().getAbsolutePath() is returning null.");

            } else {
                postLog("Your external storage is unmounted");
                // Load another directory, probably local memory
                directoryPath = context.getFilesDir() != null ? context.getFilesDir().getAbsolutePath() : "";
                if (TextUtils.isEmpty(directoryPath))
                    postFailure("context.getFilesDir().getAbsolutePath() is also returning null.");
                else postLog("PDF file creation path is " + directoryPath);
            }
        }

        private boolean hasAllPermission(Context context) {
            if (context == null) {
                postFailure("Context is null");
                return false;
            }
            return ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED;
        }

        @Override
        public void build(PdfGeneratorListener pdfGeneratorListener) {
            this.pdfGeneratorListener = pdfGeneratorListener;
            if (hasAllPermission(context) || (xmlToPDFLifecycleObserver != null && android.os.Build.VERSION.SDK_INT > 32)) {
                print();
            } else {
                if (android.os.Build.VERSION.SDK_INT >= 33) {
                    postFailure("Your current sdk is equal and greater then 33, so you need to set ''xmlToPDFLifecycleObserver'' ." +
                            "To see example please check this code - https://github.com/Gkemon/Android-XML-to-PDF-Generator/blob/master/sample/src/main/java/com/emon/exampleXMLtoPDF/MainActivity.java" +
                            ", line-67.");
                } else {
                    postLog("WRITE_EXTERNAL_STORAGE and READ_EXTERNAL_STORAGE Permission is not given." +
                            " Permission taking popup (using https://github.com/Karumi/Dexter) is going " +
                            "to be shown");
                }
                Dexter.withContext(context)
                        .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                                for (PermissionDeniedResponse deniedResponse : multiplePermissionsReport.getDeniedPermissionResponses()) {
                                    postLog("Denied permission: " + deniedResponse.getPermissionName());
                                }
                                for (PermissionGrantedResponse grantedResponse : multiplePermissionsReport.getGrantedPermissionResponses()) {
                                    postLog("Granted permission: " + grantedResponse.getPermissionName());
                                }
                                if (multiplePermissionsReport.areAllPermissionsGranted()) {
                                    print();
                                } else
                                    postLog("All necessary permission is not granted by user. Please do that first");

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                            }
                        })
                        .withErrorListener(error -> postLog("Error from Dexter (https://github.com/Karumi/Dexter) : " +
                                error.toString())).check();
            }

        }


        @Override
        public FileNameStep fromView(View... viewArrays) {
            this.viewList = Utils.getViewListMeasuringRoot(
                    new ArrayList<>(Arrays.asList(viewArrays)),
                    pdfGeneratorListener);
            return this;
        }

        @Override
        public FileNameStep fromViewList(List<View> viewList) {
            this.viewList = Utils.getViewListMeasuringRoot(viewList, pdfGeneratorListener);
            return this;
        }


        @Override
        public Build actionAfterPDFGeneration(ActionAfterPDFGeneration actionAfterPDFGeneration) {
            this.actionAfterPDFGeneration = actionAfterPDFGeneration;
            return this;
        }

        @Override
        public Build savePDFSharedStorage(XmlToPDFLifecycleObserver xmlToPDFLifecycleObserver) {
            this.xmlToPDFLifecycleObserver = xmlToPDFLifecycleObserver;
            return this;
        }


        @Override
        public FromSourceStep setContext(ComponentActivity context) {
            this.context = context;
            return this;
        }

        @Override
        public FileNameStep setPageSize(PageSize pageSize) {
            return this;
        }

        @Override
        public Build setFolderNameOrPath(String folderName) {
            this.folderName = folderName;
            return this;
        }

        @Override
        public Build setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }


        @Override
        public FileNameStep fromViewID(@NonNull Activity activity, @IdRes Integer... xmlResourceList) {
            this.viewList = Utils.getViewListFromID(activity, Arrays.asList(xmlResourceList), pdfGeneratorListener);
            return this;
        }

        @Override
        public FileNameStep fromViewIDList(@NonNull Activity activity, List<Integer> viewIDList) {
            this.viewList = Utils.getViewListFromID(activity, viewIDList, pdfGeneratorListener);
            return this;
        }


        @Override
        public FileNameStep fromLayoutXML(@LayoutRes Integer... layouts) {
            this.viewList = Utils.getViewListFromLayout(context, pdfGeneratorListener, Arrays.asList(layouts));
            return this;
        }

        @Override
        public FileNameStep fromLayoutXMLList(@LayoutRes List<Integer> layoutXMLList) {
            this.viewList = Utils.getViewListFromLayout(context, pdfGeneratorListener, layoutXMLList);
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

    interface PDFSaveListener {
        void onBlankPDFCreatedInSharedStorage(@Nullable Uri uri);
    }

    public static class XmlToPDFLifecycleObserver implements DefaultLifecycleObserver {
        private final ActivityResultRegistry mRegistry;
        private ActivityResultLauncher<Intent> mGetContent;
        private PDFSaveListener pdfSaveListener;

        public XmlToPDFLifecycleObserver(@NonNull ComponentActivity componentActivity) {
            mRegistry = componentActivity.getActivityResultRegistry();
        }

        @Override
        public void onCreate(@NonNull LifecycleOwner owner) {
            mGetContent = mRegistry.register("saved-pdf-from-xml", owner,
                    new ActivityResultContracts.StartActivityForResult()
                    , activityResult -> {
                        if (activityResult.getData() != null) {
                            pdfSaveListener.onBlankPDFCreatedInSharedStorage(activityResult.getData().getData());
                        } else pdfSaveListener.onBlankPDFCreatedInSharedStorage(null);
                    });
        }

        public void setPdfSaveListener(PDFSaveListener pdfSaveListener) {
            this.pdfSaveListener = pdfSaveListener;
        }

        public void launchPDFSaverPicker(Intent intent) {
            mGetContent.launch(intent);
        }
    }


}
