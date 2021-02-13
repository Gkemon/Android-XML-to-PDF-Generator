package com.emon.exampleXMLtoPDF;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;

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

                    /*Test for RecyclerView*/
                    /*RecyclerView recyclerView = (RecyclerView) content.findViewById(R.id.list);
                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    recyclerView.setAdapter(new DummyItemRecyclerViewAdapter(DummyContent.ITEMS));*/


                PdfGenerator.getBuilder()
                        .setContext(MainActivity.this)
                        .fromViewSource()
                        .fromView(content)
                        .setDefaultPageSize(PdfGenerator.PageSize.WRAP_CONTENT)
                        .setFileName("TestPDF")
                        .setFolderName("Test-PDF-folder")
                        .openPDFafterGeneration(true)
                        .build(new PdfGeneratorListener() {
                            @Override
                            public void onFailure(FailureResponse failureResponse) {
                                super.onFailure(failureResponse);
                            }

                            @Override
                            public void onStartPDFGeneration() {

                            }

                            @Override
                            public void onFinishPDFGeneration() {

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
