package edu.rutgers.gse.ftapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.rutgers.gse.controllers.DBAdapter;
import edu.rutgers.gse.controllers.SampleManager;
import edu.rutgers.gse.controllers.TripManager;
import edu.rutgers.gse.models.Reading;
import edu.rutgers.gse.models.Sample;
import edu.rutgers.gse.models.Trip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SampleView extends Activity {
	static final private int ATTR_ADD = 1;
	static final private int SAMPLE_CREATE = 2;
	DBAdapter dba;
	SaveSampleTask saveSampleTask;
	LoadSampleTask loadSampleTask;
	ReadingAdapter readingAdapter;
	ArrayList<String> attributes = new ArrayList<String>();
	
	Sample sample = new Sample();
	int editPos = -1;
	
	ArrayList<Reading> readings = new ArrayList<Reading>();
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reading);
        
        dba = new DBAdapter();
        dba.open(this);
        
        long sample_id = getIntent().getLongExtra("SAMPLE_ID", -1);
        String type = getIntent().getStringExtra("SAMPLE_TYPE");
        String subtype = getIntent().getStringExtra("SAMPLE_SUBTYPE");
       
        
        if(sample_id == -1){
        	sample.setType(type);
        	sample.setSubType(subtype);
        	showDialog(SAMPLE_CREATE);
        	
        }else{
        	sample.setId(sample_id);
        	loadSampleTask = new LoadSampleTask();
        	loadSampleTask.execute(sample_id);
        }
       
        ListView lv = (ListView)findViewById(R.id.attrlist);
        
        readingAdapter = new ReadingAdapter(this, readings);
        lv.setAdapter(readingAdapter);
        
        lv.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos,
				long id) {
				String attr = (readings.get(pos)).getAttr();
				
				
				editPos = attributes.indexOf(attr);
				showDialog(ATTR_ADD);
		}
       });
       
        ImageView addattr = (ImageView)findViewById(R.id.b_addattr);
        addattr.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editPos = -1;
				showDialog(ATTR_ADD);
				
			}
		});
        
        ImageView setPicButton = (ImageView)findViewById(R.id.b_addpic);
        setPicButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				takePhoto(v);
				
			}
		});
        
        ImageView browsePicButton = (ImageView)findViewById(R.id.b_viewpics);
        browsePicButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), BrowsePics.class);
				//File f = new File(TripManager.CacheDirStr + "Pic6.jpg");
		        //intentquick.setDataAndType(Uri.fromFile(f),"image/*");

		        //intentquick.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("SAMPLE_ID", sample.getId());
		        startActivityForResult(i,0);		
			}
		});
        
        
   }
    @Override
	protected void onDestroy(){
		super.onDestroy();
		dba.close();
	}
	@Override
	protected Dialog onCreateDialog(int id) {
	    switch (id) {
	        case ATTR_ADD:
	            LayoutInflater li = LayoutInflater.from(this);
	            View addAttrView = li.inflate(R.layout.addattrdialog, null);
	            
	            AlertDialog.Builder addAttrDialogBuilder = new AlertDialog.Builder(this);
	            addAttrDialogBuilder.setTitle("Add a reading");
	            addAttrDialogBuilder.setView(addAttrView);
	            AlertDialog addAttrDialog = addAttrDialogBuilder.create();
	            
	            
	            addAttrDialog.setButton("OK", new DialogInterface.OnClickListener(){
	            
		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		            	AlertDialog createSampleDialog = (AlertDialog)dialog;
		                EditText tv_attr = (EditText)createSampleDialog.findViewById(R.id.attr);
		                EditText tv_value = (EditText)createSampleDialog.findViewById(R.id.value);
		                
		                String attr = null;
		                String value = null;
		                
		                attr = tv_attr.getText().toString();
		                value = tv_value.getText().toString();
		                sample.addAttr(attr, value);
		                
		                SaveSampleTask saveSampleTask = new SaveSampleTask();
            			saveSampleTask.execute();
            			dialog.dismiss();
		            }
	                
	            });
	            
	            
	            return addAttrDialog;
	        case SAMPLE_CREATE:
	        	 LayoutInflater li2 = LayoutInflater.from(this);
		            View createSampleView = li2.inflate(R.layout.createsampledialog, null);
		            
		            AlertDialog.Builder createSampleDialogBuilder = new AlertDialog.Builder(this);
		            createSampleDialogBuilder.setTitle("New data entry");
		            createSampleDialogBuilder.setView(createSampleView);
		            AlertDialog createSampleDialog = createSampleDialogBuilder.create();
		            
		            createSampleDialog.setCancelable(false);
		            createSampleDialog.setButton("OK", new DialogInterface.OnClickListener(){
		            
			            @Override
			            public void onClick(DialogInterface dialog, int which) {
			                AlertDialog createSampleDialog = (AlertDialog)dialog;
			                EditText tv = (EditText)createSampleDialog.findViewById(R.id.name_1);
			                if(tv.getText() == null || tv.getText().toString().equals("")){
			                	tv.setHint("Cannot be blank");
		            		}else{
		            			sample.setName(tv.getText().toString());
		            			sample.setDate(new Date());
		            			
		            			SaveSampleTask saveSampleTask = new SaveSampleTask();
		            			saveSampleTask.execute();
		            			dialog.dismiss();
		            		}
			           }
		                
		            });
		            
		            
		            return createSampleDialog;
	        default:
	            break;
	    }
	    return null;
	}
	
	private class LoadSampleTask extends AsyncTask<Long, Integer, Integer>{
    	Sample s;//Temp Sample
    	ArrayList<String> attributesTemp = new ArrayList<String>();
    	@Override
    	protected void onPreExecute(){
    	}

		@Override
		protected Integer doInBackground(Long... params) {
			
			Long id = params[0];
			
			if(id == -1)
				return null;
			try {
				SampleManager sampleMgr = SampleManager.getInstance();
				s = sampleMgr.fetchSample(dba, id);
				
				attributesTemp.clear();
				dba.loadAttributes(attributesTemp);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
		@Override
    	protected void onPostExecute(Integer result){
			sample.copy(s); // copy back to sample
			
			attributes.clear();
			for(String s:attributesTemp){
				attributes.add(s);
			}
			
			TextView tv_sampleName = (TextView)findViewById(R.id.samplename);
			TextView tv_sampleType = (TextView)findViewById(R.id.sampletype);
			
			if(sample!= null && sample.getId() != -1){
				tv_sampleName.setText(sample.getName());
				tv_sampleType.setText(sample.getType()+"/"+sample.getSubType());
			}
			sample.copyReadings(readings);
			readingAdapter.notifyDataSetChanged();
			
    	}
    	
    }
	
	   private class SaveSampleTask extends AsyncTask<Void, Integer, Integer>{
	    	Sample s;
	    	ArrayList<String> attributesTemp = new ArrayList<String>();
	    	
	    	@Override
	    	protected void onPreExecute(){
	    		s = new Sample();
	    		s.copy(sample); //Copy to s.
	    	}

			@Override
			protected Integer doInBackground(Void... params) {
				
				try {
					SampleManager sampleMgr = SampleManager.getInstance();
					TripManager tripMgr = TripManager.getInstance();
					if(s.getTripId() == -1){
						Trip curTrip = tripMgr.getCurTrip(getApplicationContext(), dba);
						if(curTrip == null){
							return null;
						}
						
						s.setTripId(curTrip.getId());
					}
					sampleMgr.saveSample(dba, s);
					
					attributesTemp.clear();
					dba.loadAttributes(attributesTemp);
					
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
	    	protected void onPostExecute(Integer result){
				sample.copy(s); // copy to sample
				
				attributes.clear();
				for(String s:attributesTemp){
					attributes.add(s);
				}
				
				TextView tv_sampleName = (TextView)findViewById(R.id.samplename);
				TextView tv_sampleType = (TextView)findViewById(R.id.sampletype);
				
				if(sample!= null && sample.getId() != -1){
					tv_sampleName.setText(sample.getName());
					tv_sampleType.setText(sample.getType()+"/"+sample.getSubType());
				}
				sample.copyReadings(readings);
				readingAdapter.notifyDataSetChanged();
	    	}
	    	
	    }

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
	    switch (id) {
	        case ATTR_ADD:
	            AlertDialog addAttrView = (AlertDialog)dialog;
	            Spinner spinner = (Spinner) addAttrView.findViewById(R.id.spinner);
	            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, attributes);
	            //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	             //       this, R.array.attr_type_array, android.R.layout.simple_spinner_item);
	            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	            spinner.setAdapter(adapter);
	            spinner.setTag(dialog);
	            if(editPos != -1){
	            	spinner.setSelection(editPos);
	            	
	            }
	            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent, View view,
							int pos, long id) {
						AlertDialog createSampleDialog = (AlertDialog)parent.getTag();
						String attr = attributes.get(pos);
						String value=null;
						
						EditText tv = (EditText)createSampleDialog.findViewById(R.id.attr);
						if(tv != null){
							tv.setText(attr);
							tv.setSelectAllOnFocus(true);
						}
						
						if(attr != null){
							for(Reading r: readings){
								if(r.getAttr().equals(attr)){
									value = r.getValue();
								}
							}
						}
						
						EditText tv_value = (EditText)createSampleDialog.findViewById(R.id.value);
						if(tv_value != null){
							tv_value.requestFocus();
							if(value != null){
								tv_value.setText(value);
							}
						}
							
						
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						AlertDialog createSampleDialog = (AlertDialog)parent.getTag();
						
						EditText tv = (EditText)createSampleDialog.findViewById(R.id.attr);
						if(tv != null){
							tv.setSelectAllOnFocus(true);
							tv.requestFocus();
						}
							
						
					}
				});
	            
	            break;
	        default:
	            break;
	    }
	}
	
	class ReadingAdapter extends ArrayAdapter<Reading> {
		ArrayList<Reading> readings;
		ReadingAdapter(Context cxt, ArrayList<Reading> readings) {
			super(cxt, R.layout.attr, readings);
			this.readings = readings;
		}
		
		@Override
		public View getView(int position, View convertView,
												ViewGroup parent) {
			View row=convertView;
			ReadingHolder holder=null;
			
			if (row==null) {													
				LayoutInflater inflater=getLayoutInflater();
				
				row=inflater.inflate(R.layout.attr, parent, false);
				holder=new ReadingHolder(row);
				row.setTag(holder);
			}
			else {
				holder=(ReadingHolder)row.getTag();
			}
			
			holder.populateFrom(readings.get(position));
			
			return(row);
		}
	}
	
	static class ReadingHolder {
		private TextView attrName=null;
		private TextView attrValue=null;
		private ImageView edit=null;
		
		ReadingHolder(View row) {
			
			attrName=(TextView)row.findViewById(R.id.attrname);
			attrValue=(TextView)row.findViewById(R.id.attrvalue);
			edit=(ImageView)row.findViewById(R.id.change);
		}
		
		void populateFrom(Reading e) {

			attrName.setText(e.getAttr());
			attrValue.setText(e.getValue());
			edit.setImageResource(R.drawable.listeditbutton);
		}
	}
	
	private Uri imageUri;
	private final static int TAKE_PICTURE = 100; 
	private String picPath;
    public void takePhoto(View view) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File dir = new File(TripManager.CacheDirStr);
        if(!dir.exists()){
        	dir.mkdirs();
        }
        long picId = -1;
        try {
			picId = dba.getNewPicId();
		} catch (IOException e) {
		}
        
        if(picId == -1)
        	return;
        
        picPath = "Pic"+picId+".jpg";
        
        File photo = new File(dir, picPath);
        
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
                //Uri selectedImage = imageUri;
                //getContentResolver().notifyChange(selectedImage, null);
                //ImageView imageView = (ImageView) findViewById(R.id.picture);
                //ContentResolver cr = getContentResolver();
                //Bitmap bitmap;
                try {
                 //    bitmap = android.provider.MediaStore.Images.Media
                   //  .getBitmap(cr, selectedImage);

                    //imageView.setImageBitmap(bitmap);
                    /*Toast.makeText(this, selectedImage.toString(),
                            Toast.LENGTH_LONG).show();*/
                    //tripPic = "Pic.jpg";
                	dba.saveSamplePic(picPath, sample);
                	
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                            .show();
                   // Log.e("Camera", e.toString());
                }
            }
        }
    }

	
}
