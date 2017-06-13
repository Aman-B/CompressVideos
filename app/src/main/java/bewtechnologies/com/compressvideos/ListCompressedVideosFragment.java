package bewtechnologies.com.compressvideos;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListCompressedVideosFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListCompressedVideosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListCompressedVideosFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    private VideoDetails[] videoDetailses;

    private ArrayList<String> listthumbs;
    private  ArrayList<String> listvideodetails;



    public ListCompressedVideosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ListCompressedVideoFragment.
     */
    // TODO: Rename and change types and number of parameters


    public static ListCompressedVideosFragment newInstance() {
        ListCompressedVideosFragment fragment = new ListCompressedVideosFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras=this.getArguments();


        if (extras!= null) {

            listthumbs=extras.getStringArrayList("thumbs");
            listvideodetails=extras.getStringArrayList("details");

            videoDetailses=new VideoDetails[listthumbs.size()];
            Log.i("here "," "+listthumbs.size());

            Log.i("here", "onlistcompress: "+listvideodetails+ "thummbs "+listthumbs + " length"+videoDetailses.length);
            int i=0;
            for(String thumbs:listthumbs)
            {

                    VideoDetails videoDetails = new VideoDetails(thumbs,listvideodetails.get(i));
                    videoDetailses[i]=videoDetails;


                    Log.i("here ", "onlist:  "+videoDetailses[i].videoDetails+ " i "+i);
                    i++;

            }
            Log.i("here ", "onl  "+videoDetailses[0].videoDetails+ " i "+0);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_list_compressed_video, container, false);

        RecyclerView mRecyclerView= (RecyclerView)view.findViewById(R.id.my_recycler_view);

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(linearLayoutManager);


//        Log.i("here "," length "+videoDetailses[0].videoThumbnail);
        Log.i("here ", "onlist:1  "+videoDetailses[2].videoDetails+ " i "+2);

        VideoDataAdapter mVideoDataAdapter= new VideoDataAdapter(videoDetailses,getContext());

        mRecyclerView.setAdapter(mVideoDataAdapter);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onFragmentInteraction(Uri uri);
    }
}
