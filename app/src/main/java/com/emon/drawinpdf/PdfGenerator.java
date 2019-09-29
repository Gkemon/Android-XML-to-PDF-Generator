package com.emon.drawinpdf;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfGenerator {

    public static int a4HeightDp =3508;
    public static int a4WidthDp =2480;
    public enum PageSize {
        A4
    }
    public enum StorageInfo {
        SD_CARD,INTERNAL_MEMORY;
    }

    public static class Builder{

        private int pageWidthInPixel;
        private int pageHeightInPixel;
        private Context context;
        private PageSize  pageSize;
        private List<View> viewList;
        private StorageInfo storageInfo;
        private String fileName;
        private String targetPdf;
        private boolean openPdfFile=true;

        public String getFolderName() {
            return folderName;
        }

        public Builder setOpenPdfFile(boolean open) {
            this.openPdfFile = open;
            return this;
        }

        public Builder setFolderName(String folderName) {
            this.folderName = folderName;
            return this;
        }

        private String folderName;

        public static Builder getBuilder()
        {
            return new Builder();
        }

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }
        public Builder setPageSize(PageSize pageSize){
            this.pageSize=pageSize;
            return this;
        }

        public Builder setViewList(List<View> layoutResourceList){
            this.viewList =layoutResourceList;
            return this;
        }
        public Builder setStorageInfo(StorageInfo storageInfo){
            this.storageInfo = storageInfo;
            return this;
        }
        public Builder setFileName(String fileName){
            this.fileName=fileName;
            return this;
        }

        private void openGeneratedPDF(){
            File file = new File(targetPdf);
            if (file.exists())
            {
                Intent intent=new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(file);
                intent.setDataAndType(uri,"*/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                try
                {
                    context.startActivity(intent);
                }
                catch(ActivityNotFoundException e)
                {
                    Toast.makeText(context, "No Application available to view pdf", Toast.LENGTH_LONG).show();
                }
            }
        }

        public void build(){




            PdfDocument document = new PdfDocument();


            if(pageSize==PageSize.A4){
                pageHeightInPixel=a4HeightDp;
                pageWidthInPixel=a4WidthDp;
            }


            for(int i = 0; i< viewList.size(); i++) {

                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidthInPixel, pageHeightInPixel, i+1).create();
                PdfDocument.Page page = document.startPage(pageInfo);


                View content = viewList.get(i);

                content.measure(pageWidthInPixel,pageHeightInPixel);
                content.layout(0, 0, pageWidthInPixel,pageHeightInPixel);

                int measureWidth = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getWidth(), View.MeasureSpec.EXACTLY);
                int measuredHeight = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getHeight(), View.MeasureSpec.EXACTLY);

                content.measure(measureWidth, measuredHeight);
                content.layout(0, 0, page.getCanvas().getWidth(), page.getCanvas().getHeight());
                content.draw(page.getCanvas());

                document.finishPage(page);

            }


            String directory_path;

            if(storageInfo==StorageInfo.SD_CARD)
              directory_path =  context.getFilesDir().getPath();
            else
              directory_path = Environment.getExternalStorageDirectory().getPath();

            directory_path= directory_path+"/"+folderName+"/";

            File file = new File(directory_path);
            if (!file.exists()) {
                file.mkdirs();
            }

            targetPdf = directory_path+fileName+".pdf";

            File filePath = new File(targetPdf);
            try {
                document.writeTo(new FileOutputStream(filePath));
                Toast.makeText(context, "Done", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(context, "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
            }
            document.close();

            if(openPdfFile){
                openGeneratedPDF();
            }

        }


    }



}
