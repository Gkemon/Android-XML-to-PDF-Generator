package com.emon.drawinpdf;

import android.content.Context;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);


         View content = inflater.inflate(R.layout.layout_main_page, null);
         List<View> views =new ArrayList<>();

         views.add(content);

         PdfGenerator.getBuilder().setContext(this)
                 .fromViewSource()
                 .multipleViews()
                 .fromViewList(views)
                 .setPageSize(PdfGenerator.PageSize.A4)
                 .setTargetStorage(PdfGenerator.TargetStorage.INTERNAL_MEMORY)
                 .setFileName("TestPDF")
                 .setFolderName("Test PDF folder")
                 .openPDFafterGeneration(true)
                 .build();


    }

    private void createPdf(String sometext){

        PdfDocument document = new PdfDocument();



        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(PdfGenerator.a4WidthDp, PdfGenerator.a4HeightDp, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);


        LayoutInflater inflater = (LayoutInflater)
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.layout_main_page, null);

        content.measure(PdfGenerator.a4WidthDp, PdfGenerator.a4HeightDp);
        content.layout(0,0, PdfGenerator.a4WidthDp, PdfGenerator.a4HeightDp);


        int measureWidth = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getWidth(), View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(page.getCanvas().getHeight(), View.MeasureSpec.EXACTLY);

        content.measure(measureWidth, measuredHeight);
        content.layout(0, 0, page.getCanvas().getWidth(), page.getCanvas().getHeight());


        content.draw(page.getCanvas());
        document.finishPage(page);

        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/mypdf/";

        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        
        String targetPdf = directory_path+"test-3.pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Done", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error "+e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
    }




}
