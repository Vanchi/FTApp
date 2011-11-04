package edu.rutgers.gse.ftapp;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class MainTab extends TabActivity {
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Resources res = getResources(); // Resource object to get Drawables


	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    intent = new Intent().setClass(this, Data.class);
	    spec = tabHost.newTabSpec("Data").setIndicator("Add Data",
	    		res.getDrawable(R.drawable.datatabbutton))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    intent = new Intent().setClass(this, Visualization.class);
	    spec = tabHost.newTabSpec("Visualize").setIndicator("Visualize",
	    		res.getDrawable(R.drawable.visualizetabbutton))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    
	    intent = new Intent().setClass(this, Question.class);
	    spec = tabHost.newTabSpec("Questions").setIndicator("Ask",
	    		res.getDrawable(R.drawable.asktabbutton))
	                  .setContent(intent);
	    tabHost.addTab(spec);
	    intent = new Intent().setClass(this, FieldGuide.class);
	    spec = tabHost.newTabSpec("Help").setIndicator("Help",
	    		res.getDrawable(R.drawable.helptabbutton))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    tabHost.setBackgroundColor(Color.WHITE);
	    tabHost.getTabWidget().setBackgroundColor(Color.BLACK);
	    
	    

	   /* // hack to set font size
	    LinearLayout ll = (LinearLayout) tabHost.getChildAt(0);
	    TabWidget tw = (TabWidget) ll.getChildAt(0);

	    // first tab
	    RelativeLayout rllf = (RelativeLayout) tw.getChildAt(0);
	    TextView lf = (TextView) rllf.getChildAt(1);
	    lf.setTextSize(18);
	    lf.setPadding(0, 0, 0, 6);

	    // second tab
	    RelativeLayout rlrf = (RelativeLayout) tw.getChildAt(1);
	    TextView rf = (TextView) rlrf.getChildAt(1);
	    rf.setTextSize(18);
	    rf.setPadding(0, 0, 0, 6);*/

	    
        
    
    }
    
   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
    	Intent i;
        switch (item.getItemId()) {
        case R.id.trips:
            i = new Intent(this, TripList.class);
            startActivity(i);
            finish();
            
            return true;
        case R.id.logout:
        	i = new Intent(this, Start.class);
            startActivity(i);
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    
}