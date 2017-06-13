package bewtechnologies.com.compressvideos;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CompressionFinishedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CompressionFinishedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompressionFinishedFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String outputfilePath;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private  View view;
    public CompressionFinishedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param path Parameter 1.
     * @return A new instance of fragment CompressionFinishedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompressionFinishedFragment newInstance(String path) {
        CompressionFinishedFragment fragment = new CompressionFinishedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, path);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            outputfilePath = getArguments().getString(ARG_PARAM1);


        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        view=inflater.inflate(bewtechnologies.com.compressvideos.R.layout.fragment_compression_finished, container, false);

        view.findViewById(bewtechnologies.com.compressvideos.R.id.btn_shareVideo).setOnClickListener(this);

        Snackbar.make(view, "Compressed Video saved at : "+outputfilePath,Snackbar.LENGTH_LONG).show();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String path) {
        if (mListener != null) {
            Log.i("path", ""+outputfilePath);
            mListener.onFragmentInteraction(path);
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

    @Override
    public void onClick(View v) {
        onButtonPressed(outputfilePath);
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
        void onFragmentInteraction(String path);
    }
}
