package edu.rutgers.gse.ftapp;

import java.io.IOException;
import java.util.ArrayList;

import edu.rutgers.gse.controllers.DBAdapter;
import edu.rutgers.gse.controllers.SampleManager;
import edu.rutgers.gse.controllers.TripManager;
import edu.rutgers.gse.models.Sample;
import edu.rutgers.gse.models.Trip;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Visualization extends ListActivity {
		ArrayList<Sample> samples = new ArrayList<Sample>();
		SamplesAdapter samplesAdapter;
		SampleManager sampleMgr;
		static ContentResolver cr; 
		DBAdapter dba;
		private LoadSamplesTask loadSamplesTask;
		boolean lock = false;

		
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	       // setContentView(R.layout.visualize);
	        
	        dba = new DBAdapter();
	        dba.open(this);
	        sampleMgr = SampleManager.getInstance();
	        
	        samplesAdapter = new SamplesAdapter(this);
	        setListAdapter(samplesAdapter);
	        
	   }
	   @Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);
			Sample s = samples.get(position);
			if(s != null && s.getId() != -1){
				Intent intent = new Intent(getApplicationContext(), SampleView.class);
                intent.putExtra("SAMPLE_ID", s.getId());
                startActivityForResult(intent, 0);
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
	    
	    private class SamplesAdapter extends ArrayAdapter<Sample> {
	    	//int itemBackground;
	    	SamplesAdapter(Context c) {
	    		super(c, R.layout.samples_list_item, samples);
	    	}
	    	
	    	@Override
	    	public View getView(int position, View convertView,
	    											ViewGroup parent) {
	    		View row=convertView;
	    		SampleHolder holder=null;
	    		
	    		if (row==null) {													
	    			LayoutInflater inflater=getLayoutInflater();
	    			
	    			row=inflater.inflate(R.layout.samples_list_item, parent, false);
	    			holder=new SampleHolder(row);
	    			row.setTag(holder);
	    		}
	    		else {
	    			holder=(SampleHolder)row.getTag();
	    		}
	    		
	    		//row.setFocusable(true);
	    		holder.populateFrom(samples.get(position), 0, position);
	    		
	    		return(row);
	    	}
	    }

	    static class SampleHolder {
	    	private static final String TAG = "SampleHolder";
	    	private ImageView thumbNail=null;
	    	private TextView name = null;
	    	private TextView type_tv = null;
	    	SampleHolder(View row) {
	    		
	    		name=(TextView)row.findViewById(R.id.name);
	    		type_tv=(TextView)row.findViewById(R.id.type);
	    		
	    		thumbNail=(ImageView)row.findViewById(R.id.thumb);
	    	}
	    	
	    	void populateFrom(Sample s, int itemBackground, int pos) {
	    		//thumbNail.setBackgroundResource(itemBackground);
	    		//tripName.setText(t.getName());
	    		name.setText(s.getName());
	    		type_tv.setText(s.getType()+"/"+s.getSubType());
	    		
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
	    		thumbNail.setClickable(false);
	    		thumbNail.setFocusable(false);
	    		
	    		
	    	}
	    }

	   
	   
}
