package bewtechnologies.com.compressvideos;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import static java.security.AccessController.getContext;

/**
 * Created by amanbakshi on 08/06/17.
 */

public class VideoDataAdapter extends RecyclerView.Adapter<VideoDataAdapter.ViewHolder> {

    Context mContext;
    VideoDetails[] mVideoDetails;

    VideoDetails [] dummyVideoDetails;




    public VideoDataAdapter(VideoDetails[] videoDetails, Context context) {
        super();
        if(videoDetails!=null){
            Log.i("here ","videodet"+videoDetails);
            mVideoDetails=videoDetails;
        }
        else
        {

            dummyVideoDetails=  new VideoDetails[1];
            Log.i("here ", "VideoDataAdapter: "+dummyVideoDetails.length  );
        }

        mContext=context;
    }

    @Override
    public VideoDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.video_details_card,parent,false);

        ViewHolder viewHolder=new ViewHolder(v);


        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VideoDataAdapter.ViewHolder holder, int position) {


        if(mVideoDetails!=null)
        {
            Log.i("here", "onBindViewHolder: "+mVideoDetails[position].videoThumbnail + " details "+mVideoDetails[position].videoDetails);
            Glide.with(mContext)
                    .load(mVideoDetails[position].videoThumbnail) // or URI/path
                    .into(holder.videoThumbnail);
            holder.videoDetails.setText(mVideoDetails[position].videoDetails);
        }
        else {

            holder.videoDetails.setText("No videos found!");
        }

    }

    @Override
    public int getItemCount() {
        // Return the size of your dataset (invoked by the layout manager)

            if(mVideoDetails!=null) {
                return mVideoDetails.length;
            }
            return 1;

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView videoThumbnail;
        TextView videoDetails;

        public ViewHolder(View itemView) {
            super(itemView);
            videoThumbnail= (ImageView) itemView.findViewById(R.id.video_thumbnail);
            videoDetails=(TextView) itemView.findViewById(R.id.video_details);
        }
    }
}
