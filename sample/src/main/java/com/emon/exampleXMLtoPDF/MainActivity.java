package com.emon.exampleXMLtoPDF;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.emon.exampleXMLtoPDF.demoInvoice.DemoInvoiceFragment;
import com.emon.exampleXMLtoPDF.dummyList.DummyContent;
import com.emon.exampleXMLtoPDF.dummyList.DummyItemRecyclerViewAdapter;
import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;

public class MainActivity extends AppCompatActivity {
    Button btnPrint, btnPrintMultiPage, btnInvoice, btnDemoBarcode, btnPrintDemoList,
            btnPrintMultiPageDynamic;
    private Fragment demoInvoiceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPrint = findViewById(R.id.bt_print);
        btnPrintMultiPage = findViewById(R.id.bt_print_multi_page);
        btnInvoice = findViewById(R.id.bt_invoice);
        btnDemoBarcode = findViewById(R.id.bt_demo_barcode);
        btnPrintDemoList = findViewById(R.id.bt_print_list);
        btnPrintMultiPageDynamic = findViewById(R.id.bt_print_multi_page_dynamic);
        demoInvoiceFragment = new DemoInvoiceFragment();
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * MUST NEED TO set android:layout_width A FIXED
                 * VALUE INSTEAD OF "wrap_content" and "match_parent" OTHERWISE SIZING COULD BE MALFORMED
                 * IN PDF FOR DIFFERENT DEVICE SCREEN
                 */

                PdfGenerator.getBuilder()
                        .setContext(MainActivity.this)
                        .fromViewIDSource()
                        .fromViewID(MainActivity.this, R.id.tv_print_area)
                        .setFileName("Demo-Text")
                        .setFolderNameOrPath("MyFolder/MyDemoText/")
                        .openPDAfterGeneration(true)
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

        btnPrintMultiPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * MUST NEED TO set android:layout_width A FIXED
                 * VALUE INSTEAD OF "wrap_content" and "match_parent" OTHERWISE SIZING COULD BE MALFORMED
                 * IN PDF FOR DIFFERENT DEVICE SCREEN
                 */

                PdfGenerator.getBuilder()
                        .setContext(MainActivity.this)
                        .fromViewIDSource()
                        .fromViewID(MainActivity.this,
                                R.id.tv_print_area, R.id.tv_print_area, R.id.tv_print_area)
                        .setFileName("Demo-Text-Multi-Page")
                        .setFolderNameOrPath("MyFolder/MyDemoTextMultiPage/")
                        .openPDAfterGeneration(true)
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
        btnPrintMultiPageDynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = new EditText(v.getContext());

                new AlertDialog.Builder(v.getContext())
                        .setTitle("Input dynamic text")
                        .setMessage("Please input your demo text. It will populate a multi " +
                                "paged pdf with your demo text")
                        .setView(editText)
                        .setPositiveButton("Input", (dialog, whichButton) -> {
                            LayoutInflater inflater = (LayoutInflater)
                                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                            /**
                             * MUST NEED TO set android:layout_width A FIXED
                             * VALUE INSTEAD OF "wrap_content" and "match_parent" OTHERWISE SIZING COULD BE MALFORMED
                             * IN PDF FOR DIFFERENT DEVICE SCREEN
                             */
                            View content = inflater.inflate(R.layout.activity_main, null);
                            TextView tvDynamicText = content.findViewById(R.id.tv_print_area);
                            tvDynamicText.setText(editText.getText().toString());

                            PdfGenerator.getBuilder()
                                    .setContext(MainActivity.this)
                                    .fromViewSource()
                                    .fromView(tvDynamicText, tvDynamicText, tvDynamicText)
                                    .setFileName("Demo-Text-Multi-Page-With-Dynamic-Text")
                                    .setFolderNameOrPath("MyFolder/MyDemoTextMultiPageDynamic/")
                                    .openPDAfterGeneration(true)
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
                        })
                        .setNegativeButton("Cancel", (dialog, whichButton) -> {
                            dialog.dismiss();
                        })
                        .show();

            }
        });


        btnDemoBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                /**
                 * MUST NEED TO set android:layout_width A FIXED
                 * VALUE INSTEAD OF "wrap_content" and "match_parent" OTHERWISE SIZING COULD BE MALFORMED
                 * IN PDF FOR DIFFERENT DEVICE SCREEN
                 */
                View content = inflater.inflate(R.layout.demo_barcode, null);
                PdfGenerator.getBuilder()
                        .setContext(MainActivity.this)
                        .fromViewSource()
                        .fromView(content,content,content)
                        .setFileName("Demo-Barcode")
                        .setFolderNameOrPath("MyFolder/MyDemoBarcode/")
                        .openPDAfterGeneration(true)
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

        btnPrintDemoList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View content = inflater.inflate(R.layout.rv_item_list, null);

                RecyclerView recyclerView = content.findViewById(R.id.list);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                recyclerView.setAdapter(new DummyItemRecyclerViewAdapter(DummyContent.ITEMS));

                PdfGenerator.getBuilder()
                        .setContext(MainActivity.this)
                        .fromViewSource()
                        .fromView(content)
                        .setFileName("Demo-List")
                        .setFolderNameOrPath("MyFolder/MyDemoList/")
                        .openPDAfterGeneration(true)
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

        btnInvoice.setOnClickListener(v -> {
            if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(android.R.id.content,demoInvoiceFragment)
                        .commit();
            }
        });

    }
    @Override
    public void onBackPressed() {
        getSupportFragmentManager().beginTransaction().remove(demoInvoiceFragment).commit();
    }

}
