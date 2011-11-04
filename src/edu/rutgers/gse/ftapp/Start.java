package edu.rutgers.gse.ftapp;

import java.io.IOException;

import edu.rutgers.gse.controllers.AccountManager;
import edu.rutgers.gse.controllers.DBAdapter;
import edu.rutgers.gse.models.AppPreferences;
import edu.rutgers.gse.models.User;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Start extends Activity {
	
	DBAdapter dba;
	String userName;
	String wiseUserName;
	String wisePassword;
	LoginTask loginTask;
	Button loginButton;
	ProgressDialog loadingDialog;
	TextView loginResult; 
	AccountManager accountMgr = AccountManager.getInstance();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        dba = new DBAdapter();
        dba.open(this);
        
        loginResult = (TextView)findViewById(R.id.loginResult);
        loginButton = (Button) findViewById(R.id.startButton);
        
              
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("    Logging in    ");
        loadingDialog.setCancelable(false);
        loadingDialog.setButton("Cancel",new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				cancelLoginTask();
			}
        	
        });
        
        loginButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				TextView tv_userName = (TextView)findViewById(R.id.studentName);
		        TextView tv_wiseUserName = (TextView)findViewById(R.id.wiseUserName);
		        TextView tv_wisePassword = (TextView)findViewById(R.id.wisePasswd);
		        if(tv_userName.getText() != null)
		        	userName = tv_userName.getText().toString();
		        
		        if(tv_wiseUserName.getText() != null)
		        	wiseUserName = tv_wiseUserName.getText().toString();
		        
		        if(tv_wisePassword.getText() != null)
		        	wisePassword = tv_wisePassword.getText().toString();
		        
				loginTask = new LoginTask();
				loginTask.execute();
			}
		});

       //Load the current User
       LoadUserTask uTask = new LoadUserTask();
       uTask.execute();
    }
    
    @Override
	protected void onDestroy(){
		super.onDestroy();
		dba.close();
	}
    
    private class LoadUserTask extends AsyncTask<Void, Integer, Integer>{
    	User u;

		@Override
		protected Integer doInBackground(Void... params) {
			try {
				u = null;
	        	u = accountMgr.getCurrentUser(getApplicationContext(), dba);

	        } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 0;
		}
		
		@Override 
		protected void onPostExecute(Integer result){
			if(u != null){
				TextView tv_userName = (TextView)findViewById(R.id.studentName);
		        TextView tv_wiseUserName = (TextView)findViewById(R.id.wiseUserName);
	        	tv_userName.setText(u.getName());
	        	tv_wiseUserName.setText(u.getWiseUser());
	        }
		}
    	
    }
    
    
    private class LoginTask extends AsyncTask<Void, Integer, LoginResult> {
    	
    	@Override
    	protected void onPreExecute(){
    		loadingDialog.show();
    	}

		@Override
		protected LoginResult doInBackground(Void... params) {
			if(userName == null){
				return LoginResult.LoginFailed;
			}
			User u = null;
			try {
				
				u = accountMgr.fetchUser(wiseUserName, dba);
				
			} catch (IOException e) {
				e.printStackTrace();
			}

			Boolean newUser = false;
			if(u == null){
				u = new User(userName, wiseUserName, null);
				newUser = true;
			}
			
			
			
			AccountManager accountMgr = AccountManager.getInstance();
			boolean result = accountMgr.loginUser(u,wisePassword);
			
			
			//XXX Validate the credentials
			//XXX Login
			
			if(isCancelled()){
				return LoginResult.LoginCancelled;
			}
			
			//Save the new user to DB.
			if(newUser){
				try {
					accountMgr.saveUser(u, dba);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			
			if(result == true){
				accountMgr.setCurrentUser(u, getApplicationContext());
				return LoginResult.LoginSuccess;
			}
			return LoginResult.LoginFailed;
		}
		
		@Override
		protected void onCancelled(){
			loginResult.setText("Login Cancelled");
		}
		
		@Override 
		protected void onPostExecute(LoginResult result){
			if(loadingDialog != null && loadingDialog.isShowing()==true){
				loadingDialog.cancel();
			}
			if(result == LoginResult.LoginFailed){
				loginResult.setText("Login failed");
			}
			if(result == LoginResult.LoginSuccess){
				//Start a new activity
				Intent i = new Intent(getApplicationContext(), TripList.class);
				startActivity(i);
				finish();
			}
		}
		
		
    	
    }
    private void cancelLoginTask(){
    	if(loginTask!=null){
    		loginTask.cancel(true);
    	}
    }
    
	   private enum LoginResult{
	    	LoginSuccess,
	    	LoginCancelled,
	    	LoginFailed,
	    }
    
}