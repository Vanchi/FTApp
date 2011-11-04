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
import android.app.ListActivity;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class TripList extends ListActivity {
	
	ArrayList<Trip> trips = new ArrayList<Trip>();
	User curUser;
	TripsAdapter tripsAdapter;
	TripManager tripManager = TripManager.getInstance();
	static ContentResolver cr; 
	DBAdapter dba;
	LoadTripsTask loadTripsTask;
	
	
	static final int ADDTRIP = 100;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    
    	super.onCreate(savedInstanceState);
        
        
        dba = new DBAdapter();
        dba.open(this);
        
        cr = getContentResolver();

        tripsAdapter = new TripsAdapter(this);
        setListAdapter(tripsAdapter);
        setTitle("Your Trips");
    }
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if(position == 0){
			Intent i = new Intent(getApplicationContext(), NewTrip.class);
			startActivityForResult(i,ADDTRIP);
			return;
		}
		
		Trip t = trips.get(position);
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
            	trips.clear();
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
			trips.clear();
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
    		
    		holder.populateFrom(trips.get(position), position);
    		
    		return(row);
    	}
    }

    static class TripHolder {
    	private static final String TAG = "TripHolder";
    	//private TextView tripName=null;
    	private ImageView tripThumbNail=null;
    	private TextView tripName=null;
    	private TextView tripLocation=null;
    	
    	TripHolder(View row) {
    		
    		tripName=(TextView)row.findViewById(R.id.name);
    		tripLocation=(TextView)row.findViewById(R.id.type);
    		tripThumbNail=(ImageView)row.findViewById(R.id.thumb);
    	}
    	
    	void populateFrom(Trip t, int pos) {
    		tripName.setText(t.getName());
    		if(t.getLocationName()!= null)
    			tripLocation.setText(t.getLocationName());
    		if(pos == 0){
    			tripName.setText("New trip ...");
        		tripLocation.setText("Where are you going today?");
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
                   
                } catch (Exception e) {
                   
                }
    		}
    		
    	}
    }

}



