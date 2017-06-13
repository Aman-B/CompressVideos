package bewtechnologies.com.compressvideos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.sax.EndElementListener;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.R.id.list;
import static android.support.design.widget.Snackbar.make;
import static bewtechnologies.com.compressvideos.GetFilePathFromURI.getPath;

public class MainActivity extends AppCompatActivity implements
        SelectVideoFragment.OnFragmentInteractionListener,VideoSelectedFragment.OnFragmentInteractionListener,
        View.OnClickListener,CompressionFinishedFragment.OnFragmentInteractionListener,ListCompressedVideosFragment.OnFragmentInteractionListener{



    private FFmpeg ffmpeg;

    private  final String TAG="Compress videos : ";
    private ProgressDialog progressDialog;

    private TextView tv;
    private Button btn_filechoose,btn_shareVideo;


    private GetFilePathFromURI getFilePathFromURI;
    private String path = null;
    private String outputFilePath =null;
    private long videoLengthInSec;

    private  File inputFile;

    private  float percentProgress;

    private FragmentHandler fragmentHandler;
    private Bundle msavedInstanceState;
    private View fragmentContainer;

    private static final int FILE_SELECT_CODE = 0;

    //progressbar custom
    private DonutProgress donutProgress;
    private static final int PERMISSION_REQUEST_CODE = 200;

    View view;


    private String videoThumbnail,videoName;

    private VideoDetails[] videoDetailses;

    private VideoDetails videoDetails;

    private List<VideoDetails> listVideoDetails= new ArrayList<VideoDetails>();

    private ArrayList<String> list_thumbnails= new ArrayList<String>();
    private  ArrayList<String> list_videodetails= new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bewtechnologies.com.compressvideos.R.layout.activity_main);
        msavedInstanceState = savedInstanceState;

        Log.i("here","in main");
        view=findViewById(R.id.activityMain);

        getVideoFileList();

        printNamesToLogCat(getApplicationContext());

        if (!checkPermission()) {
            make(view, "Please grant permission.", Snackbar.LENGTH_SHORT).show();
            requestPermission();

        }


        fragmentHandler= new FragmentHandler();

        fragmentContainer=(View) findViewById(bewtechnologies.com.compressvideos.R.id.fragment_container);

        handleLaunchIntent();



        findViewById(bewtechnologies.com.compressvideos.R.id.btn_selectVideo).setOnClickListener(this);

        findViewById(R.id.btn_listcompressedvideos).setOnClickListener(this);


    }

    private void handleLaunchIntent() {

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("video/")) {
                handleSendVideo(intent); // Handle single image being sent
            }
        }



    }

    private void handleSendVideo(Intent intent) {

        Uri videoUri= (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);

        path=getPath(getApplicationContext(),videoUri);

        inputFile=new File(path);


        if(getFileSizeInMB(inputFile)<12)
        {
            showSnackBar("Can't compress. Please select a file with size greater than 12MB.","Alright!",Snackbar.LENGTH_INDEFINITE);
        }


    }

    public static void printNamesToLogCat(Context context) {
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Video.VideoColumns.DATA };
        Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
        int vidsCount = 0;
        if (c != null) {
            vidsCount = c.getCount();
            while (c.moveToNext()) {
                Log.d("VIDEO", c.getString(0));
            }
            c.close();
        }
    }

    private String[] getVideoFileList() {
        Log.i("here","called.");
        String[] fileList=null;
        File videoFiles = new File(Environment.getExternalStorageDirectory()+"/Pictures/CompressVideos");

        if(videoFiles.isDirectory())
        {

            fileList=videoFiles.list();
            Log.i("here c","list "+fileList[0]);
        }

        return fileList;

        /*Log.i("here File list" , " "+fileList);
        if (fileList != null) {
            for(int i=0;i<fileList.length;i++)
            {
                Log.i("here Video:"+i+" File name",fileList[i]);
            }
        }*/
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted)
                        make(view, "Permission Granted.", Snackbar.LENGTH_LONG).show();
                    else {

                        make(view, "Permission Denied, application won't work without the permissions.", Snackbar.LENGTH_LONG).show();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }
    }

    private void showFragment() {

        fragmentHandler.loadFragment(fragmentContainer,msavedInstanceState,getSupportFragmentManager());

    }

   /* private void compressVideo(String path) {


         outputFilePath= getOutputMediaFilePath(getApplicationContext(),MEDIA_TYPE_VIDEO);
        String cmd = "-y -i "+path+" -r 30 -vcodec mpeg4 -qscale:v 31 -acodec aac -q:a 6 -strict -2 "+outputFilePath;
        String[] command = cmd.split(" ");
        if (command.length != 0) {
            execFFmpegBinary(command);
        } else {
            Toast.makeText(getApplicationContext(), "No command", Toast.LENGTH_LONG).show();
        }
    }
*/

    private void execFFmpegBinary(final String[] command) {

        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                int i=0;
                @Override
                public void onFailure(String s) {
                    //addTextViewToLayout("Compression failed! ");
                }

                @Override
                public void onSuccess(String s) {
                    //addTextViewToLayout("Video compressed successfully : "+outputFilePath);
                    btn_shareVideo.setVisibility(View.VISIBLE);
                }

                @Override
                public void onProgress(String s) {
                   // Log.d(TAG, "Started "+s);

                    if(s.contains("time="))
                    {
                       percentProgress= calculateProgress(s);
                    }
                    Double d = new Double(percentProgress);
                    int progress=d.intValue();

                  //  donutProgress.setProgress(progress);
                }

                @Override
                public void onStart() {
                    showHideFragment(getSupportFragmentManager().findFragmentByTag("videoSelected"));

                    Log.d(TAG, "Started command : ffmpeg " + command);
                   // donutProgress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg "+command);
                  /*  donutProgress.setProgress(100);
                    donutProgress.setVisibility(View.INVISIBLE);*/
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
        }
    }


    private float calculateProgress(String s) {
            String test = s.trim();
            int indexOftime= test.indexOf("time=");
            int beginOftime=indexOftime+5;

            String hrs=test.substring(beginOftime,beginOftime+2);
            String mins=test.substring(beginOftime+3,beginOftime+5);
            String sec= test.substring(beginOftime+6,beginOftime+8);
            String milisec=test.substring(beginOftime+9,beginOftime+11);
            Log.i("here",""+ "hrs: "+hrs + "mins: "+mins+"sec: "+sec +" milisec: "+milisec);



        float compressVideoLengthInSec= Float.parseFloat(hrs)*3600
                + Float.parseFloat(mins)*60
                +Float.parseFloat(sec)
                +Float.parseFloat(milisec)/1000;


        float percentProgress = (float)compressVideoLengthInSec/videoLengthInSec*100;
        Log.i("percent pro ",""+percentProgress + " "+videoLengthInSec + " "+compressVideoLengthInSec);
        return percentProgress;

    }


    private void addTextViewToLayout(String text) {
        tv.setText(text);
    }




    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path



                    path = getPath(getApplicationContext(),uri);

                    /* Testing video quality get. */

                    MediaMetadataRetriever mediaMetadataRetriever= new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(getApplicationContext(),uri);

                    String bitrate= mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);





                    Log.d(TAG, "File Path: " + uri.toString() +"got path "+path + " got bitrate "+bitrate);
                    // Get the file instance
                    inputFile = new File(path);
                    String fname= inputFile.getAbsolutePath();

                    
                    
                    long fileSizeInMB = getFileSizeInMB(inputFile);
                    Log.d(TAG, "File name: " + fname + " Size: "+ fileSizeInMB) ;
                    //addTextViewToLayout("File choosen "+ path);
                    videoLengthInSec =getVideoTime(inputFile);
                    Log.i(TAG,"time : "+ videoLengthInSec);

                    if(fileSizeInMB<12)
                    {
                        
                        showSnackBar("Can't compress. Please select a file with size greater than 12MB.","Alright!",Snackbar.LENGTH_INDEFINITE);

                    }
                    else {
                        showNextFragment(1);

                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showSnackBar(String message, String actionString, int lengthIndefinite) {
        final Snackbar snackbar =Snackbar.make(view,message,lengthIndefinite);
        snackbar.setAction(actionString, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private long getFileSizeInMB(File inputFile) {
       long size= (inputFile.length())/(1024*1024);
        return size;
    }

    private void showNextFragment(int fragmentNumber) {

        Log.i("here",""+fragmentNumber);
        fragmentHandler.replaceFragment(getSupportFragmentManager(),fragmentNumber,path,videoLengthInSec,list_thumbnails,list_videodetails);

    }

    private long getVideoTime(File fileToShare) {

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
        retriever.setDataSource(getApplicationContext(), Uri.fromFile(fileToShare));
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long timeInSec = Long.parseLong(time )/1000;

        return timeInSec;

    }

    /*
    public  static String getOutputMediaFilePath(Context context,int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return  null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), context.getResources().getString(R.string.app_name));
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()) {
                Log.d("CompressVideos", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg";
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4";
        } else {
            return null;
        }

        return mediaFile;
    }
    */

    public void shareVideo(final String title, String path) {
        MediaScannerConnection.scanFile(getApplicationContext(), new String[] { path },
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        Intent shareIntent = new Intent(
                                android.content.Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(
                                android.content.Intent.EXTRA_SUBJECT, title);
                        shareIntent.putExtra(
                                android.content.Intent.EXTRA_TITLE, title);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        MainActivity.this.startActivity(Intent.createChooser(shareIntent,
                                "Share video : "));

                    }
                });
    }




    @Override
    public void onCompressionFinished(String path) {
        Toast.makeText(getApplicationContext(),"Compression Finshed!",Toast.LENGTH_SHORT).show();

        fragmentHandler.replaceFragment(getSupportFragmentManager(),2,path,0,null,null);
    }



    public void showHideFragment(final Fragment fragment){

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();


        if (fragment.isHidden()) {
            ft.show(fragment);
            Log.d("hidden","Show");
        } else {
            ft.hide(fragment);
            Log.d("Shown","Hide");
        }

        ft.commit();
    }


    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.btn_selectVideo:
                showFileChooser();
                break;

            case R.id.btn_listcompressedvideos:
                String[] listOfVideos = getVideoFileList();
                setVideoDetails(listOfVideos);
                showNextFragment(3);
                break;
        }

    }

    private void setVideoDetails(String[] listOfVideos) {
        if (listOfVideos != null) {
            for (String listOfVideo : listOfVideos) {
                File temp = new File(listOfVideo);



                String [] marray = new String[] {listOfVideo,temp.getName()};

                list_thumbnails.add(Environment.getExternalStorageDirectory()+"/Pictures/CompressVideos/"+listOfVideo);
                list_videodetails.add(temp.getName());






                Log.i("here", "setVideoDetails: "+videoDetailses);
            }

                Log.i("here "," list "+list_thumbnails + " d "+list_videodetails);
        }




    }

    @Override
    public void onFragmentInteraction(String path) {
        Log.i("hr","here"+path);
        shareVideo("Share video", path);

    }

    @Override
    public void onSelectVideoButtonClickListener() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
