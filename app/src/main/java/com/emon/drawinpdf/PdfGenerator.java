package com.emon.drawinpdf;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.LayoutRes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PdfGenerator {

    public static int a4HeightDp = 3508;
    public static int a4WidthDp = 2480;
    public static int a5HeightDp = 1748;
    public static int a5WidthDp = 2480;

    public enum PageSize {
        A4, A5
    }

    public enum TargetStorage {
        SD_CARD, INTERNAL_MEMORY;
    }


    public static ContextStep getBuilder(){
        return new Builder();
    }

    public interface ContextStep{
        FromSourceStep setContext(Context context);
    }

    public interface FromSourceStep{
        XMLQuantityStep fromXMLSource();

        ViewQuantityStep fromViewSource();
    }

    public interface ViewQuantityStep{
        ViewMultipleSourceIntakeStep multipleViews();

        ViewSingleSourceIntakeStep singleView();
    }


    public interface XMLQuantityStep{
        XMLMultipleSourceIntakeStep multipleXML();
        XMLSingleSourceIntakeStep singleXML();
    }

    public interface ViewMultipleSourceIntakeStep{
        PageSizeStep fromViewList(View... viewList);
        PageSizeStep fromViewList(List<View> viewList);
    }

    public interface ViewSingleSourceIntakeStep{
        PageSizeStep fromView(View view);
    }

    public interface XMLMultipleSourceIntakeStep{
        PageSizeStep fromXMLList(@LayoutRes Integer... xmlResourceList);
    }

    public interface XMLSingleSourceIntakeStep{
        PageSizeStep fromXML(@LayoutRes Integer xmlResource);
    }


    public interface PageSizeStep {
        TargetStorageStep setPageSize(PageSize pageSize);
    }

    public  interface TargetStorageStep {
        FileNameStep setTargetStorage(TargetStorage targetStorage);
    }

    public interface FileNameStep {
        Build setFileName(String fileName);
    }

    public interface Build {
       void build();
        Build setFolderName(String folderName);
        Build openPDFafterGeneration(boolean open);
    }


    public static class Builder implements Build, FileNameStep,PageSizeStep
    ,XMLMultipleSourceIntakeStep,XMLSingleSourceIntakeStep,ViewMultipleSourceIntakeStep,ViewSingleSourceIntakeStep
    ,XMLQuantityStep,ViewQuantityStep,FromSourceStep,ContextStep{

        private int pageWidthInPixel;
        private int pageHeightInPixel;
        private Context context;
        private PageSize pageSize;
        private boolean isFromXML;
        private boolean isFromView;
        private List<View> viewList;
        private View targetView;
        private Integer targetXML;
        private List<Integer> targetXmlList;
        private TargetStorage targetStorage;
        private String fileName;
        private String targetPdf;
        private boolean isMultipleTargetSource;
        private boolean openPdfFile = true;
        private String folderName;

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
                    Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void build() {


            PdfDocument document = new PdfDocument();


            if (pageSize == PageSize.A4) {
                pageHeightInPixel = a4HeightDp;
                pageWidthInPixel = a4WidthDp;
            } else if (pageSize == PageSize.A5) {
                pageHeightInPixel = a5HeightDp;
                pageWidthInPixel = a5WidthDp;
            }



            for (int i = 0; i < viewList.size(); i++) {

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidthInPixel, pageHeightInPixel, i + 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);


                View content = viewList.get(i);

                content.measure(pageWidthInPixel, pageHeightInPixel);
                content.layout(0, 0, pageWidthInPixel, pageHeightInPixel);

                int measureWidth = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getWidth(), View.MeasureSpec.EXACTLY);
                int measuredHeight = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getHeight(), View.MeasureSpec.EXACTLY);

                content.measure(measureWidth, measuredHeight);
                content.layout(0, 0, page.getCanvas().getWidth(), page.getCanvas().getHeight());
                content.draw(page.getCanvas());

                document.finishPage(page);

            }


            String directory_path;

            if (targetStorage == TargetStorage.SD_CARD)
                directory_path = context.getFilesDir().getPath();
            else
                directory_path = Environment.getExternalStorageDirectory().getPath();

            directory_path = directory_path + "/" + folderName + "/";

            File file = new File(directory_path);
            if (!file.exists()) {
                file.mkdirs();
            }

            targetPdf = directory_path + fileName + ".pdf";

            File filePath = new File(targetPdf);
            try {
                document.writeTo(new FileOutputStream(filePath));
                Toast.makeText(context, "Done", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(context, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
            }
            document.close();

            if (openPdfFile) {
                openGeneratedPDF();
            }

        }


        @Override
        public XMLQuantityStep fromXMLSource() {
            isFromXML=true;
            return this;
        }

        @Override
        public ViewQuantityStep fromViewSource() {
            isFromView=true;
            return null;
        }


        @Override
        public PageSizeStep fromViewList(View... viewArrays) {
            viewList = new ArrayList<>(Arrays.asList(viewArrays));
            return this;
        }

        @Override
        public PageSizeStep fromViewList(List<View> viewList) {
            this.viewList=viewList;
            return this;
        }

        @Override
        public PageSizeStep fromView(View targetView) {
            this.targetView=targetView;
            return this;
        }

        @Override
        public PageSizeStep fromXMLList(Integer... xmlResourceList) {
            targetXmlList= new ArrayList<>(Arrays.asList(xmlResourceList));
            return this;
        }

        @Override
        public PageSizeStep fromXML(@LayoutRes Integer xmlResource) {
            targetXML=xmlResource;
            return this;
        }


        @Override
        public ViewMultipleSourceIntakeStep multipleViews() {
            isMultipleTargetSource =true;
            return this;
        }

        @Override
        public ViewSingleSourceIntakeStep singleView() {
            isMultipleTargetSource =false;
            return this;
        }

        @Override
        public XMLMultipleSourceIntakeStep multipleXML() {
            isMultipleTargetSource= true;
            return this;
        }

        @Override
        public XMLSingleSourceIntakeStep singleXML() {
            isMultipleTargetSource= false;
            return this;
        }

        @Override
       public Build openPDFafterGeneration(boolean openPdfFile){

            return this;
        }

        @Override
        public FromSourceStep setContext(Context context) {
            this.context=context;
            return this;
        }

        @Override
        public TargetStorageStep setPageSize(PageSize pageSize) {
            return null;
        }

        @Override
        public Build setFolderName(String folderName) {

            return this;
        }

        @Override
        public Build setFileName(String fileName) {
            return null;
        }
    }


}
