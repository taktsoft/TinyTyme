package com.taktsoft.tinytime;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.zxing.integration.android.*;

public class Settings extends PreferenceActivity {
	protected Settings thisInstance = this;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		
		Preference importFromQrBtn = (Preference)findPreference("importFromQr");
		importFromQrBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {
				IntentIntegrator.initiateScan(thisInstance);
				return true;
			}
        	
        });
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		Log.i("scan dialog res.", "Ive got data for you!");
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null && scanResult.getContents() != "") {
			
			
			
			
			
			String jsonResult = scanResult.getContents();
			String servername = null;
			String authtoken = null;
			try{
				JSONObject data = new JSONObject(jsonResult); 
				servername = data.getString("host");
				authtoken = data.getString("auth_token");
				/*AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			    alertDialog.setTitle("JSON Results:");
			    alertDialog.setMessage("host: "+ data.getString("host") + ", auth: " + data.getString("auth_token"));
			    alertDialog.setButton("OK", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) { return; } }); 
			    alertDialog.show();*/
			} catch(Exception e) {
				AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			    alertDialog.setTitle("JSON exception");
			    alertDialog.setMessage(e.getMessage());
			    alertDialog.setButton("OK", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) { return; } }); 
			    alertDialog.show();
			}
			if(servername != null && authtoken != null){
				PreferenceManager.getDefaultSharedPreferences(this).edit().putString("servername", servername).commit();
				PreferenceManager.getDefaultSharedPreferences(this).edit().putString("authtoken", authtoken).commit();
				startActivity(getIntent());
				AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			    alertDialog.setTitle("QR Code decrypted:");
			    alertDialog.setMessage("Server Host: "+ servername + "\nToken: " + authtoken);
			    alertDialog.setButton("OK", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) { return; } }); 
			    alertDialog.show();
			}
			
			
			Log.i("scan dialog res.", "RESULT IS: " + jsonResult);
			
			/*AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		    alertDialog.setTitle("Received Input!");
		    alertDialog.setMessage("Input was: " + jsonResult);
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) { return; } }); 
		    alertDialog.show();*/
			
			
		} else {
			Log.i("scan dialog res.", "NO RESULT");
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		    alertDialog.setTitle("No Input");
		    alertDialog.setMessage("Could not get QR-Code!");
		    alertDialog.setButton("OK", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int which) { return; } }); 
		    alertDialog.show();
		}
	}
	
	

}
