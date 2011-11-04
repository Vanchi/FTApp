package edu.rutgers.gse.ftapp;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import edu.rutgers.gse.controllers.AccountManager;
import edu.rutgers.gse.controllers.DBAdapter;
import edu.rutgers.gse.controllers.TripManager;
import edu.rutgers.gse.models.Trip;
import edu.rutgers.gse.models.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NewTrip extends Activity {
	
	private static final int TAKE_PICTURE = 100;
	String tripName;
	String tripLocation;
	String tripDate;
	NewTripTask newTripTask;
	Button createButton;
	String tripPic;
	DBAdapter dba;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newtrip);
        
        dba = new DBAdapter();
        dba.open(this);
        
        setTitle("Create a new Trip");
        
        tripPic = null;
        createButton = (Button) findViewById(R.id.start_button);
        createButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView tv_tripName = (TextView)findViewById(R.id.trip_name);
		        TextView tv_tripLocation = (TextView)findViewById(R.id.trip_location);
		        TextView tv_tripDate = (TextView)findViewById(R.id.trip_date);
				Trip t = new Trip();
				
				t.setName(tv_tripName.getText().toString());
				t.setDate(new Date());//XXX
				t.setLocationName(tv_tripLocation.getText().toString());
				t.setTripPic(tripPic);
				newTripTask = new NewTripTask();
				newTripTask.execute(t);
			}
		});
        Button setPicButton = (Button)findViewById(R.id.setpic_button);
        setPicButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				takePhoto(v);
				
			}
		});
      
        
    }
    @Override
	protected void onDestroy(){
		super.onDestroy();
		dba.close();
	}
    private class NewTripTask extends AsyncTask<Trip, Integer, Boolean> {
    	
		@Override
		protected Boolean doInBackground(Trip... params) {
			Trip t = params[0];
			//XXX
			TripManager tripManager = TripManager.getInstance();
			AccountManager accountMgr =  AccountManager.getInstance();
			try{
				User curUser = accountMgr.getCurrentUser(getApplicationContext(), dba);
				if(t.getTripPic() != null){
					long picId = dba.getNewPicId();
					t.setTripPic("Pic"+picId+".jpg");
				}
				t.setUserId(curUser.getId());
				
				if(tripManager.addTrip(dba, t) == false){
					return false;
				}
				
				tripManager.setCurTrip(getApplicationContext(), dba, t);
				if(t.getTripPic() != null){
					//move Pic.jpg to that path
					File dir = new File(TripManager.CacheDirStr);
					File photo1 = new File(dir,  "Pic.jpg");
					File photo2 = new File(dir, t.getTripPic());
					photo1.renameTo(photo2);
					
				}
			}catch (IOException e){
				return false;
			}
			return true;
		}
		
		
		@Override 
		protected void onPostExecute(Boolean result){
		
			if(result == true){
				
				TextView tv_newTripError = (TextView)findViewById(R.id.new_trip_result);
		        tv_newTripError.setText("Success");
		        setResult(RESULT_OK);
		        finish();
			}else{
		        TextView tv_newTripError = (TextView)findViewById(R.id.new_trip_result);
		        tv_newTripError.setText("Error while creating trip");
				
			}
		}
    	
    }
    private Uri imageUri;

    public void takePhoto(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File dir = new File(TripManager.CacheDirStr);
        if(!dir.exists()){
        	dir.mkdirs();
        }
        File photo = new File(dir,  "Pic.jpg");
        
      //  File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case TAKE_PICTURE:
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = imageUri;
                getContentResolver().notifyChange(selectedImage, null);
                ImageView imageView = (ImageView) findViewById(R.id.picture);
                ContentResolver cr = getContentResolver();
                Bitmap bitmap;
                try {
                     bitmap = android.provider.MediaStore.Images.Media
                     .getBitmap(cr, selectedImage);

                    imageView.setImageBitmap(bitmap);
                    /*Toast.makeText(this, selectedImage.toString(),
                            Toast.LENGTH_LONG).show();*/
                    tripPic = "Pic.jpg";
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                            .show();
                   // Log.e("Camera", e.toString());
                }
            }
        }
    }


    
}