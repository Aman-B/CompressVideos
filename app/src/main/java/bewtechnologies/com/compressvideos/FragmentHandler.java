package bewtechnologies.com.compressvideos;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amanbakshi on 31/05/17.
 */

class FragmentHandler {
    VideoSelectedFragment videoSelectedFragment;
    CompressionFinishedFragment compressionFinishedFragment;
    ListCompressedVideosFragment listCompressedVideosFragment;

    public void loadFragment(View mFragmentContainer, Bundle msavedInstanceState,

                             FragmentManager supportFragmentManager)
    {
        if ((mFragmentContainer) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (msavedInstanceState != null) {
                return;
            }

            SelectVideoFragment selectVideoFragment=new SelectVideoFragment();
            FragmentTransaction transaction = supportFragmentManager.beginTransaction();


            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back


            transaction.add(bewtechnologies.com.compressvideos.R.id.fragment_container, selectVideoFragment,"selectVideo");
            transaction.addToBackStack(null);

            // Commit the transaction
            transaction.commit();
        }
    }

    public void replaceFragment(
            FragmentManager supportFragmentManager, int fragmentNumber,
            String path, long videoLengthInSec, ArrayList<String> listthumbs, ArrayList<String> videodetails)
    {
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack so the user can navigate back


        switch (fragmentNumber)
        {
            case 1:
                videoSelectedFragment=VideoSelectedFragment.newInstance(path,videoLengthInSec);
                transaction.replace(bewtechnologies.com.compressvideos.R.id.fragment_container, videoSelectedFragment,"videoSelected");
                break;

            case 2:
                compressionFinishedFragment=CompressionFinishedFragment.newInstance(path);
                Log.i("start path ",""+path);
                transaction.replace(bewtechnologies.com.compressvideos.R.id.fragment_container,compressionFinishedFragment,"compressionFinished");
                break;

            case 3:
                Log.i("here","launch frag");
                listCompressedVideosFragment=ListCompressedVideosFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("thumbs",listthumbs);
                bundle.putStringArrayList("details",videodetails);
                listCompressedVideosFragment.setArguments(bundle);
                transaction.replace(R.id.fragment_container,listCompressedVideosFragment,"listCompressedVideos");
                break;


            default:
                Log.i("Not ","yet implemented!");
                break;
        }
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commitAllowingStateLoss();

    }
}
