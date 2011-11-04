package edu.rutgers.gse.ftapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.rutgers.gse.controllers.AccountManager;
import edu.rutgers.gse.controllers.DBAdapter;
import edu.rutgers.gse.controllers.SampleManager;
import edu.rutgers.gse.controllers.TripManager;
import edu.rutgers.gse.models.Sample;
import edu.rutgers.gse.models.Trip;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class Data extends Activity {
		ArrayList<Sample> samples = new ArrayList<Sample>();
		SamplesAdapter samplesAdapter;
		SampleManager sampleMgr;
		static ContentResolver cr; 
		DBAdapter dba;
		private LoadSamplesTask loadSamplesTask;
		LocationManager locationMgr;
		LocationListener locationListener;
		String latitude;
		String longitude;
		
		boolean lock = false;

		ArrayList<String> allTypes = new ArrayList<String>();
		String knownTypes[] = new String[]{"Plants", "Mammals", "Birds", "Fish", "Reptiles", "Amphibians", "Invertibrates", "Fungi", "Water","Soil"};
		Map<String, ArrayList<String>> allSubTypes = new HashMap<String, ArrayList<String>>();
		
		String knownSubTypes[][] = new String[][]{{"Grass", "Tree", "Bush"},
													{"Chipmonk","Cat","Dog","Rabbit"},
													{"Sparrow", "Pigeon", "Red winged black bird"},
													{"Salmon","Carp"},
													{"Salamander","Lizard"},
													{"Frog"},
													{"Butterfly"},
													{"Mushroom"},
													{"Lake","River","Puddle"},
													{"Mud","Clay"},
													};
		
		private class CommonListener implements OnClickListener {
			int id;
			public CommonListener(int id){
				this.id = id;
			}
			@Override
			public void onClick(View v) {
				if(lock == true)
					return;
				showDialog(id);
				
			}
			
		}
		
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.data);
	        
	        dba = new DBAdapter();
	        dba.open(this);
	        cr = getContentResolver();
	        sampleMgr = SampleManager.getInstance();
	        
	        AccountManager accountMgr = AccountManager.getInstance();
	        try{
		        if(accountMgr.getCurrentUser(this, dba) == null){
		        	finish();
		        	Intent i = new Intent(this, Start.class);
		        	startActivity(i);
		        	return;
		        }
	        }catch(IOException e){
	        	finish();
	        	Intent i = new Intent(this, Start.class);
	        	startActivity(i);
	        	return;
	        }
	        
	        TripManager tripMgr = TripManager.getInstance();
	        try{
		        if(tripMgr.getCurTrip(this, dba) == null){
		        	finish();
		        	Intent i = new Intent(this, TripList.class);
		        	startActivity(i);
		        	return;
		        }
	        }catch(IOException e){
	        	finish();
	        	Intent i = new Intent(this, TripList.class);
	        	startActivity(i);
	        	return;
	        }
	        
	        
	        Gallery gallery = (Gallery)findViewById(R.id.gallery);
	        samplesAdapter = new SamplesAdapter(this);
	        gallery.setAdapter(samplesAdapter);
	        
	        locationMgr = (LocationManager)getSystemService(
		    		  Context.LOCATION_SERVICE);
		    		
		    		locationListener = new MyLocationListener();
		    		
		    		locationMgr.requestLocationUpdates(
		    		        LocationManager.GPS_PROVIDER,
		    		        0,
		    		        0,
		    		        locationListener);
	       /* DisplayMetrics metrics = new DisplayMetrics();
	        getWindowManager().getDefaultDisplay().getMetrics(metrics);


	        MarginLayoutParams mlp = (MarginLayoutParams) gallery.getLayoutParams();
	        mlp.setMargins(-(metrics.widthPixels/2)+40, 
	                       mlp.topMargin, 
	                       mlp.rightMargin, 
	                       mlp.bottomMargin
	        );*/
	        gallery.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View v, int pos,
						long id) {
					Sample s = samples.get(pos);
					if(s != null && s.getId() != -1){
						Intent intent = new Intent(getApplicationContext(), SampleView.class);
		                intent.putExtra("SAMPLE_ID", s.getId());
		                startActivityForResult(intent, 0);
					}
				}
	        });
	        
	        
	        
	        ImageView[] buttons = new ImageView[11];
	        
	        buttons[0] = (ImageView)findViewById(R.id.b_zerozero);
	        buttons[1] = (ImageView)findViewById(R.id.b_zeroone);
	        buttons[2] = (ImageView)findViewById(R.id.b_zerotwo);
	        buttons[3] = (ImageView)findViewById(R.id.b_zerothree);
	        buttons[4] = (ImageView)findViewById(R.id.b_onezero);
	        buttons[5] = (ImageView)findViewById(R.id.b_oneone);
	        buttons[6] = (ImageView)findViewById(R.id.b_onetwo);
	        buttons[7] = (ImageView)findViewById(R.id.b_onethree);
	        buttons[8] = (ImageView)findViewById(R.id.b_twozero);
	        buttons[9] = (ImageView)findViewById(R.id.b_twoone);
	        buttons[10] = (ImageView)findViewById(R.id.b_twotwo);
	        
	        //ImageView twothree = (ImageView)findViewById(R.id.b_twothree);
	        
	        
	        CommonListener[] commonListener = new CommonListener[11];
	        for(int j = 0; j < 11; j++){
	        	commonListener[j] = new CommonListener(j);
	        	buttons[j].setOnClickListener(commonListener[j]);
	        }
	        
	        
	        
	        
	       /* final CharSequence[] plantItems = {"Grass", "Bushes", "Trees"};

	        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
	        builder1.setTitle("Pick a type of plant");
	        builder1.setItems(plantItems, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	                Intent intent = new Intent(getApplicationContext(), SampleView.class);
	                intent.putExtra("SAMPLE_ID", new Long(-1));
	                intent.putExtra("SAMPLE_TYPE","Plant");
	                intent.putExtra("SAMPLE_SUBTYPE",plantItems[item]);
	                startActivityForResult(intent, 0);
	            }
	        });
	        final AlertDialog plantAlert = builder1.create();
	        
	        
	        zerozero.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//plantAlert.show();
					showDialog(0);
				}
			});
	        
	        final CharSequence[] mammalItems = {"Chipmunk", "Bat", "Monkey", "Add new"};

	        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
	        builder2.setTitle("Pick a mammal type");
	        builder2.setItems(mammalItems, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int item) {
	                Intent intent = new Intent(getApplicationContext(), SampleView.class);
	                intent.putExtra("SAMPLE_ID", new Long(-1));
	                intent.putExtra("SAMPLE_TYPE","Mammals");
	                intent.putExtra("SAMPLE_SUBTYPE",mammalItems[item]);
	                startActivityForResult(intent, 0);
	            }
	        });
	        final AlertDialog mammalAlert = builder2.create();
	        
	        
	        zeroone.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mammalAlert.show();					
				}
			});*/
	        
	        
	        
	   }
	   private class MyLocationListener implements LocationListener{

	    	public void onLocationChanged(Location argLocation) {
	    	// TODO Auto-generated method stub
	    		String latitude;
	    		String longitude;
	    		latitude = String.valueOf(
	    					argLocation.getLatitude());
		    	longitude = String.valueOf(
		    	  argLocation.getLongitude());
		    	Log.d("VANCHI: Got Location:",""+latitude+","+longitude );
	    	}

	    	public void onProviderDisabled(String provider) {
	    	// TODO Auto-generated method stub
	    	}

	    	public void onProviderEnabled(String provider) {
	    	// TODO Auto-generated method stub
	    	}

	    	public void onStatusChanged(String provider,
	    	 int status, Bundle extras) {
	    	// TODO Auto-generated method stub
	    	}
		}
	    
	   @Override
	   protected void onPrepareDialog(int id, Dialog dialog) {
		   
		   if(id == 10){//Add custom
			   AlertDialog addAttrView = (AlertDialog)dialog;
			   final ArrayList<String> subtypes = new ArrayList<String>();
			   final Spinner spinner2= (Spinner) addAttrView.findViewById(R.id.spinner2);
	           final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, subtypes);
	             
			   
	            Spinner spinner1 = (Spinner) addAttrView.findViewById(R.id.spinner1);
	            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, allTypes);
	            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	            spinner1.setAdapter(adapter1);
	            spinner1.setTag(dialog);
	            spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int pos, long id) {
						AlertDialog createSampleDialog = (AlertDialog)parent.getTag();
						String type = allTypes.get(pos);
						
						EditText tv = (EditText)createSampleDialog.findViewById(R.id.type);
						if(tv != null){
							tv.setText(type);
							tv.setSelectAllOnFocus(true);
						}
							
						//EditText value = (EditText)createSampleDialog.findViewById(R.id.subtype);
						//if(value != null)
							//value.requestFocus();
						
						subtypes.clear();
						ArrayList<String> st = allSubTypes.get(type);
						if(st!=null){
							for(String s:st){
								subtypes.add(s);
							}
							adapter2.notifyDataSetChanged();
						}
						spinner2.setSelection(0);
						EditText tv2 = (EditText)createSampleDialog.findViewById(R.id.subtype);
						if(tv2 != null){
							tv2.setText(subtypes.get(0));
							tv2.setSelectAllOnFocus(true);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						AlertDialog createSampleDialog = (AlertDialog)parent.getTag();
						
						EditText tv = (EditText)createSampleDialog.findViewById(R.id.type);
						if(tv != null){
							tv.setSelectAllOnFocus(true);
						//	tv.requestFocus();
						}
							
						
					}
				});
	            
	            
	            //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	             //       this, R.array.attr_type_array, android.R.layout.simple_spinner_item);
	            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	            spinner2.setAdapter(adapter2);
	            spinner2.setTag(dialog);
	            spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int pos, long id) {
						AlertDialog createSampleDialog = (AlertDialog)parent.getTag();
						String subtype = subtypes.get(pos);
						
						EditText tv = (EditText)createSampleDialog.findViewById(R.id.subtype);
						if(tv != null){
							tv.setText(subtype);
							tv.setSelectAllOnFocus(true);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						AlertDialog createSampleDialog = (AlertDialog)parent.getTag();
						
						EditText tv = (EditText)createSampleDialog.findViewById(R.id.subtype);
						if(tv != null){
							tv.setSelectAllOnFocus(true);
//							tv.requestFocus();
						}
					}
				});
	            return;
		   }
		   else{
			   AlertDialog ad = (AlertDialog)dialog;
			   ad.getListView().setTag(new Integer(id));
			   ArrayAdapter<String> adapter = (ArrayAdapter<String>)ad.getListView().getAdapter();
			   adapter.clear();
			   ArrayList<String> subtypes = new ArrayList<String>(allSubTypes.get(knownTypes[id]));
			   for(String s:subtypes){
				   adapter.add(s);
			   }
		   }
	   }
	   @Override
	   protected Dialog onCreateDialog(int id) {
           
		   if(id == 10){
			    LayoutInflater li = LayoutInflater.from(this);
	            View addAttrView = li.inflate(R.layout.addtype, null);
	            
	            AlertDialog.Builder addAttrDialogBuilder = new AlertDialog.Builder(this);
	            addAttrDialogBuilder.setTitle("Add a Type/Subtype");
	            addAttrDialogBuilder.setView(addAttrView);
	            AlertDialog addAttrDialog = addAttrDialogBuilder.create();
	            
	            
	            addAttrDialog.setButton("OK", new DialogInterface.OnClickListener(){
	            
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	AlertDialog createSampleDialog = (AlertDialog)dialog;
		                EditText tv_type = (EditText)createSampleDialog.findViewById(R.id.type);
		                EditText tv_subtype = (EditText)createSampleDialog.findViewById(R.id.subtype);
		                String type = null;
		                if(tv_type.getText() != null){
		                	type = tv_type.getText().toString();
		                }
		                String subtype = null;
		                if(tv_subtype.getText() != null){
		                	subtype = tv_subtype.getText().toString();
		                }
		                Intent intent = new Intent(getApplicationContext(), SampleView.class);
		                intent.putExtra("SAMPLE_ID", new Long(-1));
		                intent.putExtra("SAMPLE_TYPE",type);
		                intent.putExtra("SAMPLE_SUBTYPE",subtype);
		                startActivityForResult(intent, 0);
		                dialog.dismiss();
		            }
	                
	            });
	            
	            addAttrDialog.setButton2("Cancel", new DialogInterface.OnClickListener(){
	            
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	dialog.dismiss();
		            }
	                
	            });
	            
	            
	            return addAttrDialog;
		   }else{
		   
	            AlertDialog.Builder addAttrDialogBuilder = new AlertDialog.Builder(this);
	            addAttrDialogBuilder.setTitle("Pick subtype of "+knownTypes[id]);
	            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item);
	 		   	addAttrDialogBuilder.setAdapter(adapter, new Dialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AlertDialog ad = (AlertDialog)dialog;
						Integer id = (Integer) ad.getListView().getTag();
						
						ArrayAdapter<String> adapter = (ArrayAdapter<String>)ad.getListView().getAdapter();
						
						String type = knownTypes[id];
						String subtype = adapter.getItem(which);
						
						Intent intent = new Intent(getApplicationContext(), SampleView.class);
		                intent.putExtra("SAMPLE_ID", new Long(-1));
		                intent.putExtra("SAMPLE_TYPE",type);
		                intent.putExtra("SAMPLE_SUBTYPE",subtype);
		                startActivityForResult(intent, 0);
		                dialog.dismiss();
					}
				});
	            
	            AlertDialog addAttrDialog = addAttrDialogBuilder.create();
	            
	            return addAttrDialog;
		   }
	   }
	  
	   @Override
	   public void onResume(){
		   super.onResume();
		   loadSamplesTask = new LoadSamplesTask();
		   loadSamplesTask.execute();
		   
	   }
	    @Override
		protected void onDestroy(){
			super.onDestroy();
			dba.close();
		}
	    private class LoadSamplesTask extends AsyncTask<Void, Integer, Integer>{
	    	
	    	@Override
	    	protected void onPreExecute(){
	    		lock = true;
	    	}

			@Override
			protected Integer doInBackground(Void... params) {
				
				ArrayList<Sample> temp;
				Trip curTrip=null;
				try {
					if(curTrip == null){
						TripManager tripMgr = TripManager.getInstance();
						curTrip = tripMgr.getCurTrip(getApplicationContext(), dba);
					}
					
					if(curTrip == null){
						return null;
					}
					
					
					SampleManager sampleMgr = SampleManager.getInstance();
					sampleMgr.loadSamples(dba, curTrip);
					
					if(isCancelled()){
						return null;
					}
					
					//Load all types
					dba.loadTypes(allTypes);
					//Mix it with known types
					for(int j = 0; j < 10; j++){
						allTypes.remove(knownTypes[j]);
					}
					//Add them as first ones
					for(int j = 9; j >=0; j--){
						allTypes.add(0,knownTypes[j]);
					}
					
					
					for(String type: allTypes){
						ArrayList<String> subtypes = allSubTypes.get(type);
						if(subtypes == null){
							subtypes = new ArrayList<String>();
						}
						dba.loadSubTypes(subtypes, type);
						allSubTypes.put(type, subtypes);
					}
					
					for(int j = 0; j < 10; j++){
						ArrayList<String> subtypes = allSubTypes.get(knownTypes[j]);
						
						for(String subtype: knownSubTypes[j]){
							subtypes.remove(subtype);
						}
						
						for(int k = knownSubTypes[j].length-1;  k >=0; k--){
							subtypes.add(0,knownSubTypes[j][k]);
						}
						
						allSubTypes.put(knownTypes[j], subtypes);
					}
					
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
	    	protected void onPostExecute(Integer result){
				
				sampleMgr.copySamplesTo(samples);
		        
		        if(samplesAdapter != null){
		        	samplesAdapter.notifyDataSetChanged();
		        }
		        
		        lock = false;
	    	}
	    	
	    }
	    
	    private void cancelLoadSamplesTask(){
	    	if(loadSamplesTask!=null){
	    		loadSamplesTask.cancel(true);
	    	}
	    }
	    private class SamplesAdapter extends ArrayAdapter<Sample> {
	    	//int itemBackground;
	    	SamplesAdapter(Context c) {
	    		super(c, R.layout.sample_grid_item, samples);
	    		//TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
	            //itemBackground = a.getResourceId(
	              //  R.styleable.Gallery1_android_galleryItemBackground, 0);
	            //a.recycle(); 
	    	}
	    	
	    	@Override
	    	public View getView(int position, View convertView,
	    											ViewGroup parent) {
	    		View row=convertView;
	    		SampleHolder holder=null;
	    		
	    		if (row==null) {													
	    			LayoutInflater inflater=getLayoutInflater();
	    			
	    			row=inflater.inflate(R.layout.sample_grid_item, parent, false);
	    			holder=new SampleHolder(row);
	    			row.setTag(holder);
	    		}
	    		else {
	    			holder=(SampleHolder)row.getTag();
	    		}
	    		
	    		//row.setFocusable(true);
	    		//holder.populateFrom(samples.get(position), itemBackground, position);
	    		holder.populateFrom(samples.get(position), 0, position);
	    		
	    		return(row);
	    	}
	    }

	    static class SampleHolder {
	    	private static final String TAG = "SampleHolder";
	    	//private TextView tripName=null;
	    	private ImageView thumbNail=null;
	    	private TextView name = null;
	    	
	    	SampleHolder(View row) {
	    		
	    		name=(TextView)row.findViewById(R.id.name);
	    		thumbNail=(ImageView)row.findViewById(R.id.thumb);
	    	}
	    	
	    	void populateFrom(Sample s, int itemBackground, int pos) {
	    		//thumbNail.setBackgroundResource(itemBackground);
	    		//tripName.setText(t.getName());
	    		name.setText(s.getName());
	    		String type = s.getType();
	    		if(type.equals("Plants")){
	    			thumbNail.setBackgroundResource(R.drawable.plant_type);
	    		}else if(type.equals("Birds")){
	    			thumbNail.setBackgroundResource(R.drawable.bird_type);
	    			
	    		}else if(type.equals("Mammals")){
	    			thumbNail.setBackgroundResource(R.drawable.mammal_type);
	    			
	    		}else if(type.equals("Fish")){
	    			thumbNail.setBackgroundResource(R.drawable.fish_type);
	    			
	    		}else if(type.equals("Reptiles")){
	    			thumbNail.setBackgroundResource(R.drawable.reptile_type);
	    			
	    		}else if(type.equals("Invertibrate")){
	    			thumbNail.setBackgroundResource(R.drawable.invertibrate_type);
	    			
	    		}else if(type.equals("Amphibians")){
	    			thumbNail.setBackgroundResource(R.drawable.amphibian_type);
	    			
	    		}else if(type.equals("Fungi")){
	    			thumbNail.setBackgroundResource(R.drawable.fungi_type);
	    			
	    		}else if(type.equals("Soil")){
	    			thumbNail.setBackgroundResource(R.drawable.soil_type);
	    			
	    		}else if(type.equals("Water")){
	    			thumbNail.setBackgroundResource(R.drawable.water_type);
	    			
	    		}else {
	    			thumbNail.setBackgroundResource(R.drawable.unknown_type);
	    			
	    		}
	    		
	    		/*{
	    			Uri selectedImage;
	    			File photo = new File(TripManager.CacheDirStr,  t.getTripPic());
	    			selectedImage = Uri.fromFile(photo);
	    			
			        Bitmap bitmap;
	                try {
	                     bitmap = android.provider.MediaStore.Images.Media
	                     .getBitmap(cr, selectedImage);

	                     tripThumbNail.setImageBitmap(bitmap);
	                    Toast.makeText(this, selectedImage.toString(),
	                            Toast.LENGTH_LONG).show();
	                } catch (Exception e) {
	                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
	                            .show();
	                   // Log.e("Camera", e.toString());
	                }
	    		}*/
	    		
	    	}
	    }

	   
	   
}
