package bewtechnologies.com.compressvideos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VideoSelectedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VideoSelectedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoSelectedFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //FRAGMENT NUMBER =1
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Context mContext;



    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private String outputFilePath =null;
    private long videoLengthInSec;

    private  final String TAG="Compress videos : ";

    //progressbar custom
    private AnimatedCircleLoadingView animatedCircleLoadingView;


    private  float percentProgress;
    private FFmpeg ffmpeg;

    private ImageView imageView;
    private FancyButton btn_compressVideo;


    public VideoSelectedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param videoLengthInSec
     * @return A new instance of fragment VideoSelectedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoSelectedFragment newInstance(String param1, long videoLengthInSec) {
        VideoSelectedFragment fragment = new VideoSelectedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putLong(ARG_PARAM2,videoLengthInSec);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            videoLengthInSec=getArguments().getLong(ARG_PARAM2);


        }




    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageView =(ImageView) getActivity().findViewById(bewtechnologies.com.compressvideos.R.id.videoThumb);

        btn_compressVideo=(FancyButton) getActivity().findViewById(bewtechnologies.com.compressvideos.R.id.btn_compressVideo);
        btn_compressVideo.setOnClickListener(this);


        Log.i("Got path", " "+mParam1);
        Log.i("onactivity","created");
        Glide.with(getContext())
                .load(mParam1) // or URI/path
                .into(imageView);
        /*if(mParam1.contains(" "))
        {
            mParam1=  "\""+mParam1+"\"";
            Log.i("params ", ""+mParam1);
        }*/

        animatedCircleLoadingView=(AnimatedCircleLoadingView) getActivity().findViewById(R.id.circle_loading_view);



        animatedCircleLoadingView.setVisibility(View.INVISIBLE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(bewtechnologies.com.compressvideos.R.layout.fragment_video_selected, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void CompressionDone(String mParam1) {
        if (mListener != null) {
            mListener.onCompressionFinished(mParam1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("here ","resume");

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        Log.i("onattach ","view");
        mContext=context;

        initffmpeg(mContext);

    }

    private void hideShowUIComponents() {
        animatedCircleLoadingView.setVisibility(View.INVISIBLE);
    }

    private void initffmpeg(final Context context) {
        ffmpeg = FFmpeg.getInstance(context);
        try {
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                @Override
                public void onStart() {}

                @Override
                public void onFailure() {
                    Toast.makeText(mContext,"Initialization failure!",Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccess() {}

                @Override
                public void onFinish() {

                    Toast.makeText(mContext,"Initialized!",Toast.LENGTH_LONG).show();
                }
            });
        } catch (FFmpegNotSupportedException e) {
            // Handle if FFmpeg is not supported by device

            Toast.makeText(mContext,"Unsupported"+e.toString(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
       // CompressionDone(mParam1);
        compressVideo(mParam1);
    }

    private void compressVideo(String path) {


        outputFilePath= getOutputMediaFilePath(mContext,MEDIA_TYPE_VIDEO);

        Log.i("here "+path,"are "+outputFilePath);
        ArrayList<String> arrayList = new ArrayList<>();
            /*arrayList.add("ffmpeg");*/
            arrayList.add("-y");
            arrayList.add("-i");
            arrayList.add(path);
            arrayList.add("-vcodec");
            arrayList.add("mpeg4");
            arrayList.add("-qscale:v");
            arrayList.add("31");
            arrayList.add("-acodec");
            arrayList.add("aac");
            arrayList.add("-q:a");
            arrayList.add("6");
            arrayList.add("-strict");
            arrayList.add("-2");
            arrayList.add(outputFilePath);

       // path="\""+path+"\"";
        String cmd = "-y -i "+path+" -vcodec mpeg4 -qscale:v 31 -acodec aac -q:a 6 -strict -2 "+outputFilePath;
       String[] command = new String[arrayList.size()];
       //String[] command = cmd.split(" ");
        command = arrayList.toArray(command);
        Log.i("cmd "," "+command);
        if (command.length != 0) {
            execFFmpegBinary(command);
        } else {

            Toast.makeText(mContext, "No command", Toast.LENGTH_LONG).show();
        }
    }


    private void execFFmpegBinary(final String[] command) {

        try {
            ffmpeg.execute(command, new ExecuteBinaryResponseHandler() {
                int i=0;
                @Override
                public void onFailure(String s) {
                    //addTextViewToLayout("Compression failed! ");
                    Toast.makeText(mContext,"compression failed!"+s,Toast.LENGTH_LONG).show();
                    Log.i("failed ",""+s);
                }

                @Override
                public void onSuccess(String s) {
                    //addTextViewToLayout("Video compressed successfully : "+outputFilePath);
                   // btn_shareVideo.setVisibility(View.VISIBLE);

                }

                @Override
                public void onProgress(String s) {
                    // Log.d(TAG, "Started "+s);

                    if(s.contains("time="))
                    {
                        percentProgress= calculateProgress(s);
                    }


                    String p= String.valueOf(percentProgress);

                    int progress= Math.round(percentProgress);
//                    donutProgress.setDonut_progress(p);
                    animatedCircleLoadingView.setPercent(progress);
                }

                @Override
                public void onStart() {

                   Log.d(TAG, "Started command : ffmpeg " + command);

                    imageView.setVisibility(View.INVISIBLE);
                    btn_compressVideo.setVisibility(View.INVISIBLE);


                    animatedCircleLoadingView.setVisibility(View.VISIBLE);
                    animatedCircleLoadingView.startDeterminate();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Finished command : ffmpeg "+command);
                    animatedCircleLoadingView.setPercent(100);
                    animatedCircleLoadingView.setVisibility(View.INVISIBLE);
                    CompressionDone(outputFilePath);
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
    public  static String getOutputMediaFilePath(Context context,int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return  null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), context.getResources().getString(bewtechnologies.com.compressvideos.R.string.app_name));
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



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onCompressionFinished(String path);
    }
}
