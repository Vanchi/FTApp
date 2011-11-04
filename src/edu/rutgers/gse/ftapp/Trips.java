package edu.rutgers.gse.ftapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.rutgers.gse.controllers.AccountManager;
import edu.rutgers.gse.controllers.DBAdapter;
import edu.rutgers.gse.controllers.TripManager;
import edu.rutgers.gse.models.Trip;
import edu.rutgers.gse.models.User;



import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class Trips extends Activity {
	
	ArrayList<Trip> trips = new ArrayList<Trip>();
	User curUser;
	TripsAdapter tripsAdapter;
	TripManager tripManager = TripManager.getInstance();
	static ContentResolver cr; 
	DBAdapter dba;
	LoadTripsTask loadTripsTask;
	
	static LinearLayout tripCard;
	static TextView newTripHeader;
	
	static final int ADDTRIP = 100;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.trips);
        
        
        dba = new DBAdapter();
        dba.open(this);
        
        cr = getContentResolver();
        newTripHeader=(TextView)findViewById(R.id.newtripheader);
        tripCard = (LinearLayout)findViewById(R.id.trip_title_card);

        Gallery gallery = (Gallery)findViewById(R.id.gallery);
        tripsAdapter = new TripsAdapter(this);
        gallery.setAdapter(tripsAdapter);
        
        gallery.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos,
					long id) {
					if(pos == 0){
						Intent i = new Intent(getApplicationContext(), NewTrip.class);
						startActivityForResult(i,ADDTRIP);
						return;
					}
					
					Trip t = trips.get(pos);
					if(t != null){
						try{
							tripManager.setCurTrip(getApplicationContext(), dba, t);
							
						}catch (IOException e){
							Toast.makeText(getApplicationContext(), "Error selecting this trip", Toast.LENGTH_SHORT).show();
							return;
						}
					}
					
					Intent i = new Intent(getApplicationContext(), MainTab.class);
					startActivity(i);
			
				
			}
        	
        });
        gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View v,
					int position, long id) {
				if(position == 0){
					newTripHeader.setVisibility(View.VISIBLE);
					tripCard.setVisibility(View.GONE);
					return;
				}
				newTripHeader.setVisibility(View.GONE);
				tripCard.setVisibility(View.VISIBLE);
				TextView tripName=(TextView)findViewById(R.id.tripname);
				TextView tripLocation=(TextView)findViewById(R.id.triplocation);
				TextView tripDate=(TextView)findViewById(R.id.tripdate);
				Trip t = trips.get(position);
				tripName.setText(t.getName());
				tripLocation.setText(t.getLocationName());
				tripDate.setText(t.getDate().toString());
			}
		
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				LinearLayout tripCard = (LinearLayout)findViewById(R.id.trip_title_card);
				tripCard.setVisibility(View.GONE);
				
			}
		
        });
        
    }
    @Override
	protected void onDestroy(){
		super.onDestroy();
		dba.close();
	}
    @Override 
    public void onResume(){
    	super.onResume();
    	loadTripsTask = new LoadTripsTask();
        loadTripsTask.execute();
        
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case ADDTRIP:
            if (resultCode == Activity.RESULT_OK) {
            	tripManager.copyTripsTo(trips);
    	        trips.add(0, new Trip("New Trip",null));
    	        
    	        if(tripsAdapter != null){
    	        	tripsAdapter.notifyDataSetChanged();
    	        }
    	        //Start a new activity
				Intent i = new Intent(getApplicationContext(), MainTab.class);
				startActivity(i);
            }
            
        }
    }
    
    private class LoadTripsTask extends AsyncTask<Void, Integer, Integer>{
    	
    	@Override
    	protected void onPreExecute(){
    	}

		@Override
		protected Integer doInBackground(Void... params) {
			
			
			try {
				if(curUser == null){
					AccountManager accountMgr = AccountManager.getInstance();
					curUser = accountMgr.getCurrentUser(getApplicationContext(), dba);
				}
				
				if(curUser == null){
					return null;
				}
				
				tripManager.loadTrips(dba, curUser);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
    	protected void onPostExecute(Integer result){
			tripManager.copyTripsTo(trips);
	        trips.add(0, new Trip("New Trip",null));
	        
	        if(tripsAdapter != null){
	        	tripsAdapter.notifyDataSetChanged();
	        }
    	}
    	
    }
    
    
    private class TripsAdapter extends ArrayAdapter<Trip> {
    	int itemBackground;
    	TripsAdapter(Context c) {
    		super(c, R.layout.trip_grid_item, trips);
    		TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
            itemBackground = a.getResourceId(
                R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle(); 
    	}
    	
    	@Override
    	public View getView(int position, View convertView,
    											ViewGroup parent) {
    		View row=convertView;
    		TripHolder holder=null;
    		
    		if (row==null) {													
    			LayoutInflater inflater=getLayoutInflater();
    			
    			row=inflater.inflate(R.layout.trip_grid_item, parent, false);
    			holder=new TripHolder(row);
    			row.setTag(holder);
    		}
    		else {
    			holder=(TripHolder)row.getTag();
    		}
    		
    		holder.populateFrom(trips.get(position), itemBackground, position);
    		
    		return(row);
    	}
    }

    static class TripHolder {
    	private static final String TAG = "TripHolder";
    	//private TextView tripName=null;
    	private ImageView tripThumbNail=null;
    	
    	TripHolder(View row) {
    		
    		//tripName=(TextView)row.findViewById(R.id.label);
    		tripThumbNail=(ImageView)row.findViewById(R.id.thumb);
    	}
    	
    	void populateFrom(Trip t, int itemBackground, int pos) {
    		tripThumbNail.setBackgroundResource(itemBackground);
    		//tripName.setText(t.getName());
    		if(pos == 0){
    			tripThumbNail.setImageResource(R.drawable.tripplus);
    		}
    		else if(t.getTripPic() == null){
    			tripThumbNail.setImageResource(R.drawable.tripholder);
    		}else{
    			Uri selectedImage;
    			File photo = new File(TripManager.CacheDirStr,  t.getTripPic());
    			selectedImage = Uri.fromFile(photo);
    			
		        Bitmap bitmap;
                try {
                     bitmap = android.provider.MediaStore.Images.Media
                     .getBitmap(cr, selectedImage);

                     tripThumbNail.setImageBitmap(bitmap);
                    /*Toast.makeText(this, selectedImage.toString(),
                            Toast.LENGTH_LONG).show();*/
                } catch (Exception e) {
                    /*Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                            .show();*/
                   // Log.e("Camera", e.toString());
                }
    		}
    		
    	}
    }

}



