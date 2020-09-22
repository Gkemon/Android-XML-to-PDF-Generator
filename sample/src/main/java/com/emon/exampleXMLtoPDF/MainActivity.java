package com.emon.exampleXMLtoPDF;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button btnPrint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnPrint=findViewById(R.id.bt_print);
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View content = inflater.inflate(R.layout.layout_print, null);

                List<View> views =new ArrayList<>();
                views.add(content);

                List<Integer> integers = new ArrayList<>(Arrays.asList(R.layout.layout_print,R.layout.layout_print));

                PdfGenerator.getBuilder()
                        .setContext(MainActivity.this)
                        .fromViewIDSource()
                        .fromViewID(MainActivity.this,R.id.tv_print_area,R.id.tv_print_area)
                        .setDefaultPageSize(PdfGenerator.PageSize.A4)
                        .setFileName("TestPDF")
                        .setFolderName("Test-PDF-folder")
                        .openPDFafterGeneration(true)
                        .build(new PdfGeneratorListener() {
                            @Override
                            public void onFailure(FailureResponse failureResponse) {
                                super.onFailure(failureResponse);
                            }

                            @Override
                            public void showLog(String log) {
                                super.showLog(log);
                            }

                            @Override
                            public void onSuccess(SuccessResponse response) {
                                super.onSuccess(response);
                            }
                        });

            }
        });

    }

}
