<!-- ⚠️ This README has been generated from the file(s) "blueprint.md" link - https://github.com/andreasbm/readme ⚠️--><p align="center">
  <img src="https://github.com/Gkemon/Android-XML-to-PDF-Generator/blob/master/logo.png" alt="Logo" width="150" height="150"  />
</p>
<h1 align="center">XML to PDF Generator For Android</h1>
 <p align="center">
		<a href="https://github.com/Gkemon/XML-to-PDF-generator"><img alt="Maintained" src="https://img.shields.io/badge/Maintained%3F-yes-green.svg" height="20"/></a>
	<a href="https://github.com/Gkemon/XML-to-PDF-generator"><img alt="Maintained" src="https://cdn.rawgit.com/sindresorhus/awesome/d7305f38d29fed78fa85652e3a63e154dd8e8829/media/badge.svg" height="20"/></a>
	</p>
	 <p align="center">
	<a href="https://android-arsenal.com/details/1/8165"><img alt="Maintained" src="https://img.shields.io/badge/Android%20Arsenal-Android--XML--to--PDF--Generator-green.svg?style=flat" height="20"/></a>
	</p>

</p>

<p align="center">
  <b>Automatically generate PDF file from XML file or Java's View object in Android</b></br>
  <p align="center"> Make PDF from Android layout resources (e.g - R.layout.myLayout,R.id.viewID), Java's view ids or directly views objects <p>
</p>
 <p align="center">Run the <a href="https://github.com/Gkemon/Android-XML-to-PDF-Generator/tree/master/sample">sample app</a> and see it's or <a href="https://youtu.be/zD9krZedi3M">youtube video</a> below for getting more clearance.</p>

<p align="center">
<img src="https://github.com/Gkemon/Android-XML-to-PDF-Generator/blob/master/demo-home-screen-ss.png" alt="Demo" height="600" width="300" /> 
</p>

* **Simple**: Extremely simple to use. For using <b>Step Builder Design Patten</b> undernath,here IDE greatly helps developers to complete the steps for creating a PDF from XMLs.
* **Powerful**: Customize almost everything.
* **Transparent**: It shows logs,success-responses, failure-responses , that's why developer will nofity any event inside the process. 

<details>
<summary>📖 Table of Contents</summary>
<br />

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#table-of-contents)

## ➤ Table of Contents

* [➤ Installation](#-installation)
* [➤ Getting Started](#-getting-started)
* [➤ License](#-license)
</details>


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#installation)

## ➤ Installation

**Step 1**. Add the JitPack repository to your root ```build.gradle``` at the end of repositories
```
android {
 .
 .
  
   /*Need Java version 1.8 as Rx java is used for file write underneath for preventing UI freezing*/
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
 .
 .
}
 .
 .
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
.
.
```

**Step 2**. Add the dependency
```
dependencies {
        implementation 'com.github.Gkemon:Android-XML-to-PDF-Generator:2.6.7'
}
```	
[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#getting-started-quick)



## ➤ Getting Started

You can generate <b>PDF</b> from many sources.
* Layout resources (i.e: ```R.layout.myLayout```)
* View ids (i.e: ```R.id.viewID```)
* Java view objects (i.e ```View```,```TextView```,```LinearLayout```) because sometimes we need to change the content of the XML and then dealing this java view object is only way to do this.

## ➤ Important Note
![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)
![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)
![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)
For better output and make the PDF more responsive,please try to set ```android:layout_width``` of the top most view of XML a fixed value in pixel or ```px``` (Recommandation is not use ```dp``` as it depends on device screen) instead of ```wrap_content``` and ```match_parent``` otherwise sizing could be malformed in PDF for different device screen.Suppose if you want to print an A4 sized pdf so you can see my example from [where](https://github.com/Gkemon/Android-XML-to-PDF-Generator/blob/master/sample/src/main/res/layout/demo_a4_sized_page.xml). You just need to make your XML's  width:hight aspected ratio 1:1.14142. Referece is [here](https://www.papersizes.org/sp/a/a4#:~:text=The%20aspect%20ratio%20(width%3Aheight,(1%3A%E2%88%9A2) ).
![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)
![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)
![#f03c15](https://via.placeholder.com/15/f03c15/f03c15.png)

### From layout resources : 
( Only static content in XML will be printed by this approach. If you want to change the content of the XML ,suppose there is a
text view in the XML and you want to populate it with a data then try the [approach](https://github.com/Gkemon/Android-XML-to-PDF-Generator/edit/master/README.md#from-views) ) 


```java
 PdfGenerator.getBuilder()
                        .setContext(context)
                        .fromLayoutXMLSource()
                        .fromLayoutXML(R.layout.layout_print,R.layout.layout_print)
			/* "fromLayoutXML()" takes array of layout resources.
			 * You can also invoke "fromLayoutXMLList()" method here which takes list of layout resources instead of array. */
                        .setFileName("Test-PDF")
			/* It is file name */
                        .setFolderName("FolderA/FolderB/FolderC")
			/* It is folder name. If you set the folder name like this pattern (FolderA/FolderB/FolderC), then
			 * FolderA creates first.Then FolderB inside FolderB and also FolderC inside the FolderB and finally
			 * the pdf file named "Test-PDF.pdf" will be store inside the FolderB. */
                        .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.SHARE)
			/*If you want to save your pdf in shared storage (where other apps can also see your pdf even after the app is uninstall).
			 * You need to pass an xmt to pdf lifecycle observer by the following method. To get complete overview please see the MainActivity of 'sample' folder */
			.savePDFSharedStorage(xmlToPDFLifecycleObserver)
			/* It true then the generated pdf will be shown after generated. */
                        .build(new PdfGeneratorListener() {
                            @Override
                            public void onFailure(FailureResponse failureResponse) {
                                super.onFailure(failureResponse);
				/* If pdf is not generated by an error then you will findout the reason behind it
				 * from this FailureResponse. */
                            }
			      @Override
                            public void onStartPDFGeneration() {
                                /*When PDF generation begins to start*/
                            }

                            @Override
                            public void onFinishPDFGeneration() {
                                /*When PDF generation is finished*/
                            }

                            @Override
                            public void showLog(String log) {
                                super.showLog(log);
				/*It shows logs of events inside the pdf generation process*/ 
                            }

                            @Override
                            public void onSuccess(SuccessResponse response) {
                                super.onSuccess(response);
				/* If PDF is generated successfully then you will find SuccessResponse 
				 * which holds the PdfDocument,File and path (where generated pdf is stored)*/
				
                            }
                        });
```

### From view IDs :

```java
    PdfGenerator.getBuilder()
                        .setContext(context)
                        .fromViewIDSource()
                        .fromViewID(R.layout.hostLayout,activity,R.id.tv_print_area,R.id.tv_print_area)
			/* "fromViewID()" takes array of view ids and the host layout xml where the view ids are belonging.
			 * You can also invoke "fromViewIDList()" method here which takes list of view ids instead of array.*/
                        .setFileName("Test-PDF")
                        .setFolderName("Test-PDF-folder")
                        .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
                        .build(new PdfGeneratorListener() {
                            @Override
                            public void onFailure(FailureResponse failureResponse) {
                                super.onFailure(failureResponse);
                            }
			    
			       @Override
                            public void onStartPDFGeneration() {
                                /*When PDF generation begins to start*/
                            }

                            @Override
                            public void onFinishPDFGeneration() {
                                /*When PDF generation is finished*/
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
```

### From views:
( This approach is perfect when you need to change the XML content. You can change the content getting them by ```findViewById``` and change them and finally print them. Other example is [here](https://github.com/Gkemon/Android-XML-to-PDF-Generator/issues/4#issuecomment-712690701) )
```java 


TextView tvText = view.findViewByID(R.id.tv_text_1);
tvText.setText("My changed content");
//By the following statements, we are changing the text view inside of our target "view" which is going to be changed.
//So if we now print the "view" then you will see the changed text in the pdf.

PdfGenerator.getBuilder()
                        .setContext(MainActivity.this)
                        .fromViewSource()
                        .fromView(view)
                        .setFileName("Test-PDF")
                        .setFolderName("Test-PDF-folder")
                        .actionAfterPDFGeneration(PdfGenerator.ActionAfterPDFGeneration.OPEN)
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
                            public void onStartPDFGeneration() {
                                /*When PDF generation begins to start*/
                            }

                            @Override
                            public void onFinishPDFGeneration() {
                                /*When PDF generation is finished*/ 
                            }
			    
                            @Override
                            public void onSuccess(SuccessResponse response) {
                                super.onSuccess(response);
                            }
                        });
```	

### Multi-paged PDF creation:
Users of the library, sometimes have doubts that how to create multi-paged PDF. Though I mentioned it above but I need to show it again for more clearance.
You can insert multiple xml or views object even view id in the parameter of the following methods to create multi-paged pdf: 


If you want create multi-paged pdf from xmls-

`.fromLayoutXML(R.layout.layout_1,R.layout.layout_2)`

 If you want create multi-paged pdf from view ids -
 
`.fromViewID(activity,R.id.viewId1,R.id.viewId2)`

 If you want create multi-paged pdf from views-
 
`.fromViewID(view1,view1)`

### How to print an Invoice Or Report ? 
Sometimes people gets stuck to print invoice or report via this library.So I wrote an example invoice/report printing fragment to visualise how to print an <b>Invoice</b> or <b>Report</b>. Here is [the link also with an important documentation](https://github.com/Gkemon/Android-XML-to-PDF-Generator/blob/master/sample/src/main/java/com/emon/exampleXMLtoPDF/demoInvoice/DemoInvoiceFragment.java) 


### How to deal with generated PDF? 
With a method calling named `openPDFafterGeneration(true)`, the generated file will be automatically opened automatically.So you <b>DON'T NEED TO BE BOTHER FOR IT</b>. [FileProvider](https://developer.android.com/reference/androidx/core/content/FileProvider) is used to open file here. To get a good insight about it please see the [tutorial](https://vladsonkin.com/how-to-share-files-with-android-fileprovider/). The `android:authorities` name in the app is `${applicationId}.xmlToPdf.provider` which might be needed if you want to deal with generated file <b>CUSTOMLY</b>,not letting the app open the generated file. you will get the generated file path in `onSuccess(SuccessResponse response)` response.

### Troubleshoot
* Try to avoid to provide `match_parent` and `wrap_content` height/width in XML. So it specifically. 
* If any of your footer view is not placed the footer position then you need adjust it using `marginTop` and keep it in a `ScrollView`.For example this [issue](https://github.com/Gkemon/Android-XML-to-PDF-Generator/issues/16) is fixed by rearranging XML like [this](https://github.com/Gkemon/Android-XML-to-PDF-Generator/blob/master/sample/src/main/res/layout/layout_test_invoice.xml)

So if you find any trouble,then you are also welcomed again to knock me.Thank you so much. 
		

[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#templates)

<p>
  <a href="https://www.linkedin.com/in/gk-mohammad-emon-0301b7104" rel="nofollow noreferrer">
    <img src="https://i.stack.imgur.com/gVE0j.png" alt="linkedin"> LinkedIn
  </a> &nbsp; 
  <a href="emon.info2013@gmail.com">
   <img width="20" src="https://user-images.githubusercontent.com/5141132/50740364-7ea80880-1217-11e9-8faf-2348e31beedd.png" alt="inbox"> Inbox
  </a>
</p>

#### Logo credit: [kirillmazin](https://www.behance.net/kirillmazin)

## ➤ License

The source code is licensed under the [Apache License 2.0](https://github.com/Gkemon/XML-to-PDF-generator/blob/master/LICENSE). 


[![-----------------------------------------------------](https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/colored.png)](#license)


## ➤ App using it 
* [Codeddit Programmer Community](
https://play.google.com/store/apps/details?id=com.codedditapp.codeddit)
* [হাজিরা খাতা - Attendance sheet - HaziraKhata](
https://play.google.com/store/apps/details?id=com.Teachers.HaziraKhataByGk)
* [Trust Axiata Pay](
https://play.google.com/store/apps/details?id=com.tad.bdkepler&hl=en&gl=US)
* [Prideboard](https://play.google.com/store/apps/details?id=com.pridesys.prideboard)

* [Treatbook - Your Health Coach and Medical Tracker](
https://play.google.com/store/apps/details?id=com.newlit.treatbook&hl=en&gl=US)

* [Mobflow](
https://play.google.com/store/apps/details?id=com.brazzo.mobflow)
