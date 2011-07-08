package com.taktsoft.tinytime;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class TinyTymeMain extends Activity {
	Button btnXML;
	Button btnJSON;
	TextView tvData;
	private Button btnCreate;
	private EditText inputTaskName;
	private DatePicker dpDate;
	private TimePicker tpTime;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		tvData = (TextView) findViewById(R.id.txtData);
		btnJSON = (Button) findViewById(R.id.btnJSON);
		btnJSON.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				String taskJson = fetchTaskData();
				examineJSONFile(taskJson);
			}
		});
		dpDate = (DatePicker) findViewById(R.id.dpDate);
		tpTime = (TimePicker) findViewById(R.id.tpTime);
		inputTaskName = (EditText) findViewById(R.id.inputTaskName);
		btnCreate = (Button) findViewById(R.id.btnCreate);
		btnCreate.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				Date createdAt = new Date();
				createdAt.setYear(dpDate.getYear());
				createdAt.setMonth(dpDate.getMonth());
				createdAt.setDate(dpDate.getDayOfMonth());
				createdAt.setHours(tpTime.getCurrentHour());
				createdAt.setMinutes(tpTime.getCurrentMinute());

				createNewTask(createdAt, inputTaskName.getText().toString());
			}
		});

	}

	protected void createNewTask(Date createdAt, String name) {
		String url = new String(
				"http://chomsky.bonn.taktsoft.com:3000/tasks.json?auth_token=VxoGQbV3pXYEGjGm13jo");
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yy-MM-dd'T'HH:mm:ssZ");

		try {
			int TIMEOUT_MILLISEC = 10000; // = 10 seconds
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					TIMEOUT_MILLISEC);
			HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
			HttpClient client = new DefaultHttpClient(httpParams);

			JSONObject json = new JSONObject();
			json.put("start", dateFormat.format(createdAt));
			json.put("name", name);

			Log.i("JSON", json.toString());

			HttpPost request = new HttpPost(url);

			request.setHeader("Accept", "application/json");
			request.setHeader("Content-type", "application/json");
			
			request.setEntity(new ByteArrayEntity(json.toString().getBytes(
					"UTF8")));
			
			HttpResponse response = client.execute(request);
			Log.i("HTTP", response.getStatusLine().toString());
		} catch (Exception e) {
			// TODO: handle exception
		}

		/*
		 * try {
		 * 
		 * SimpleDateFormat dateFormat = new
		 * SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		 * 
		 * // Add your data JSONObject holder = new JSONObject();
		 * holder.put("start",dateFormat.format(createdAt));
		 * holder.put("name",name); Log.i("JSON",holder.toString());
		 * httpPost.setEntity(new StringEntity(holder.toString()));
		 * 
		 * 
		 * Log.i("HTTP","entitiy " + httpPost.getEntity().getContent());
		 * Log.i("HTTP"
		 * ,convertStreamToString(httpPost.getEntity().getContent()));
		 * httpPost.setHeader("Accept", "application/json");
		 * httpPost.setHeader("Content-type", "text/json");
		 * 
		 * response = httpClient.execute(httpPost); Log.i("HTTP",
		 * response.getStatusLine().toString()); HttpEntity entity =
		 * response.getEntity(); if (entity != null) { String result =
		 * convertStreamToString(entity.getContent()); Log.i("HTTP",result);
		 * tvData.setText(tvData.getText() + "Task Created"); } } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (JSONException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */

	}

	String fetchTaskData() {
		String url = new String(
				"http://chomsky.bonn.taktsoft.com:3000/tasks.json?auth_token=VxoGQbV3pXYEGjGm13jo");
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response;
		try {
			response = httpClient.execute(httpGet);
			Log.i("HTTP", response.getStatusLine().toString());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String result = convertStreamToString(entity.getContent());
				Log.i("HTTP", result);
				return result;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	void examineJSONFile(String taskJson) {
		try {
			String x = "";
			// InputStream is = this.getResources().openRawResource(
			// R.raw.tasks);
			// byte[] buffer = new byte[is.available()];
			// while (is.read(buffer) != -1)
			// ;
			String jsontext = taskJson;
			JSONArray entries = new JSONArray(jsontext);

			x = "JSON parsed.\nThere are [" + entries.length() + "]\n\n";

			int i;
			for (i = 0; i < entries.length(); i++) {
				JSONObject post = entries.getJSONObject(i);
				x += "------------\n";
				x += "Date:" + post.getString("created_at") + "\n";
				x += "Name:" + post.getString("name") + "\n";
				x += "Duration:" + post.getString("duration") + "\n";
				x += "Projekt Id:" + post.getString("project_id") + "\n\n";

			}
			tvData.setText(x);
		} catch (Exception je) {
			tvData.setText("Error w/file: " + je.getMessage());
		}
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}