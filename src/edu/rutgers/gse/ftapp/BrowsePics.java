package edu.rutgers.gse.ftapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.rutgers.gse.controllers.DBAdapter;
import edu.rutgers.gse.controllers.TripManager;
import edu.rutgers.gse.models.Sample;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class BrowsePics extends Activity{
	DBAdapter dba;
	static ContentResolver cr;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.browsepic);
	    cr = getContentResolver();
	    GridView gridview = (GridView) findViewById(R.id.gridview);
	    
	    long sample_id = getIntent().getLongExtra("SAMPLE_ID", -1);
	    Sample s = new Sample();
	    s.setId(sample_id);
	    
	    dba = new DBAdapter();
        dba.open(this);
	    
        final ArrayList<String> piclist = new ArrayList<String>();
        
        try {
			dba.fetchSamplePics(s, piclist);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        gridview.setAdapter(new ImageAdapter(this, piclist));
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	Intent intentquick = new Intent(Intent.ACTION_VIEW);
				File f = new File(TripManager.CacheDirStr + piclist.get(position));
		        intentquick.setDataAndType(Uri.fromFile(f),"image/*");

		        intentquick.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        startActivity(intentquick);	
	        }
	    });
	}
	@Override
	protected void onDestroy(){
		super.onDestroy();
		dba.close();
	}
	public class ImageAdapter extends BaseAdapter {
	    private Context mContext;
	    ArrayList<String> pics;
	    
	    public ImageAdapter(Context c, ArrayList<String> pics) {
	        mContext = c;
	        this.pics = pics;
	    }

	    @Override
	    public int getCount() {
	        return pics.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return pics.get(position);
	    }

	    @Override
	    public long getItemId(int position) {
	        return position;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(8, 8, 8, 8);
	        } else {
	            imageView = (ImageView) convertView;
	        }
	        Uri selectedImage;
			File photo = new File(TripManager.CacheDirStr,  pics.get(position));
			selectedImage = Uri.fromFile(photo);
			
	        Bitmap bitmap;
            try {
                 bitmap = android.provider.MediaStore.Images.Media
                 .getBitmap(cr, selectedImage);

                 imageView.setImageBitmap(bitmap);
            } catch (Exception e) {
            }
//	        imageView.setImageResource(mThumbIds[position]);
	        return imageView;
	    }

	   
	}
}
