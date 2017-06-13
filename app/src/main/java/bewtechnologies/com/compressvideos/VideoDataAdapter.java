package bewtechnologies.com.compressvideos;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import static java.security.AccessController.getContext;

/**
 * Created by amanbakshi on 08/06/17.
 */

public class VideoDataAdapter extends RecyclerView.Adapter<VideoDataAdapter.ViewHolder> {

    Context mContext;
    VideoDetails[] mVideoDetails;

    VideoDetails [] dummyVideoDetails;

    Button shareVideo;


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
        Log.i("here", "onBindViewHolder:1 "+mVideoDetails[0].videoThumbnail + " details "+mVideoDetails[0].videoDetails);
        Log.i("here", "onBindViewHolder:1 "+mVideoDetails[1].videoThumbnail + " details "+mVideoDetails[1].videoDetails);
        Log.i("here", "onBindViewHolder:1 "+mVideoDetails[2].videoThumbnail + " details "+mVideoDetails[2].videoDetails);

        mContext=context;
    }

    @Override
    public VideoDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.video_details_card,parent,false);

        final ViewHolder viewHolder=new ViewHolder(v);

        shareVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVideoDetails!=null) {
                    shareVideo(mVideoDetails[viewHolder.getAdapterPosition()].videoThumbnail);
                }
                else
                {
                    Toast.makeText(mContext,"No videos to share!",Toast.LENGTH_SHORT).show();
                }
            }
        });



        return viewHolder;
    }

    @Override
    public void onBindViewHolder(VideoDataAdapter.ViewHolder holder,  int position) {


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

    private void shareVideo(String videoThumbnail) {
        String path = videoThumbnail; //should be local path of downloaded video

        ContentValues content = new ContentValues(4);
        content.put(MediaStore.Video.VideoColumns.DATE_ADDED,
                System.currentTimeMillis() / 1000);
        content.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        content.put(MediaStore.Video.Media.DATA, path);

        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, content);

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/*");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Look at this video compressed by compreesVideos app!");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Look at this video compressed by compreesVideos app!");
        sharingIntent.putExtra(Intent.EXTRA_STREAM,uri);
        mContext.startActivity(Intent.createChooser(sharingIntent,"Share Video"));
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
            shareVideo=(Button) itemView.findViewById(R.id.btn_sharecompressedVideo);
        }
    }
}
