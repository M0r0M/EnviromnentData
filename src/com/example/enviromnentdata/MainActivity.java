package com.example.enviromnentdata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	// accelerometer
	TextView accelerationX_TV;
	TextView accelerationY_TV;
	TextView accelerationZ_TV;
	float xAxis_lateral = 0;
	float yAxis_longitudinal = 0;
	float zAxis_vertical = 0;
	
	//gyroscope
	TextView gyroX_TV;
	TextView gyroY_TV;
	TextView gyroZ_TV;
	float xAxis_azimut = 0;
	float yAxis_tangage = 0;
	float zAxis_roulis = 0;
	
	//magnetometer
	TextView magnetoX_TV;
	TextView magnetoY_TV;
	TextView magnetoZ_TV;
	float xAxisMag = 0;
	float yAxisMag = 0;
	float zAxisMag = 0;
	
	// pressure
	TextView pressure_TV;
	float pressure = 0;
	
	// date
	String dateFormat = "yyyy-MM-dd\tHH:mm:ss.SSS";
	SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	
	// interface
	int isRecording = 0;
	Button startStopButton;
	
	// misc
	final String resultsFile = "results.txt";
	Timer updateTimer;
	
	// Global variable to hold the current location
    Location mCurrentLocation;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startStopButton = (Button)findViewById(R.id.StartStopButton);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_send_report:
	            sendReport();
	            return true;
	        case R.id.action_settings:
	            
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void updateUI() {
		runOnUiThread(new Runnable() {
			public void run() {
				updateAccelerometer();
				updateGyroscope();
				updateMagnetometer();
				updatePressure();
			}
		});
	}
	
	private void updateFile(final String FILE_NAME) {
		new String();
		String dataToWrite = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n", 
				sdf.format(new Date()), 
				xAxis_lateral, yAxis_longitudinal, zAxis_vertical,
				xAxis_azimut, yAxis_tangage, zAxis_roulis,
				xAxisMag, yAxisMag, zAxisMag, 
				pressure);
		try {
			FileOutputStream outputStream = openFileOutput(FILE_NAME, Context.MODE_APPEND);
			outputStream.write(dataToWrite.getBytes());
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// accelerometer
	private void handleAccelerometer() {
		accelerationX_TV = (TextView)findViewById(R.id.AccLateralValue);
		accelerationY_TV = (TextView)findViewById(R.id.AccLongitudinalValue);
		accelerationZ_TV = (TextView)findViewById(R.id.AccVerticalValue);
		
		SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(accelerometerEventListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	final SensorEventListener accelerometerEventListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent sensorEvent) {
			if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				xAxis_lateral = sensorEvent.values[0];
				yAxis_longitudinal = sensorEvent.values[1];
				zAxis_vertical = sensorEvent.values[2];
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
		
	};
	
	private void updateAccelerometer() {
		String lateral = String.valueOf(xAxis_lateral);
		accelerationX_TV.setText(lateral);
		accelerationX_TV.invalidate();
		String longitudinal = String.valueOf(yAxis_longitudinal);
		accelerationY_TV.setText(longitudinal);
		accelerationY_TV.invalidate();
		String vertical = String.valueOf(zAxis_vertical);
		accelerationZ_TV.setText(vertical);
		accelerationZ_TV.invalidate();
	}
	
	// gyroscope
	private void handleGyroscope() {
		gyroX_TV = (TextView)findViewById(R.id.GyroXValue);
		gyroY_TV = (TextView)findViewById(R.id.GyroYValue);
		gyroZ_TV = (TextView)findViewById(R.id.GyroZValue);
		
		SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		sensorManager.registerListener(gyroscopeEventListener, gyroscope, SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	final SensorEventListener gyroscopeEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
				xAxis_azimut = event.values[0];
				yAxis_tangage = event.values[1];
				zAxis_roulis = event.values[2];
			}
		}
		
	};
	
	public void updateGyroscope() {
		String azimut = String.valueOf(xAxis_azimut);
		gyroX_TV.setText(azimut);
		gyroX_TV.invalidate();
		String tangage = String.valueOf(yAxis_tangage);
		gyroY_TV.setText(tangage);
		gyroY_TV.invalidate();
		String roulis = String.valueOf(zAxis_roulis);
		gyroZ_TV.setText(roulis);
		gyroZ_TV.invalidate();
	}
	
	//magnetometer
	public void handleMagnetometer() {
		magnetoX_TV = (TextView)findViewById(R.id.MagnetoXValue);
		magnetoY_TV = (TextView)findViewById(R.id.MagnetoYValue);
		magnetoZ_TV = (TextView)findViewById(R.id.MagnetoZValue);
		
		SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		sensorManager.registerListener(magnetometerEventListener, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
	}
	
	final SensorEventListener magnetometerEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				xAxisMag = event.values[0];
				yAxisMag = event.values[1];
				zAxisMag = event.values[2];
			}
		}
	};
	
	public void updateMagnetometer() {
		String xMag = String.valueOf(xAxisMag);
		magnetoX_TV.setText(xMag);
		magnetoX_TV.invalidate();
		String yMag = String.valueOf(yAxisMag);
		magnetoY_TV.setText(yMag);
		magnetoY_TV.invalidate();
		String zMag = String.valueOf(zAxisMag);
		magnetoZ_TV.setText(zMag);
		magnetoZ_TV.invalidate();
	}
	
	//Pressure
	public void handlePressure() {
		pressure_TV = (TextView)findViewById(R.id.PressureValue);
		
		SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		Sensor pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
		sensorManager.registerListener(pressureEventListener, pressure, SensorManager.SENSOR_DELAY_FASTEST); 
	}
	
	final SensorEventListener pressureEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}

		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
				pressure = event.values[0];
			}
		}
	};
	
	public void updatePressure() {
		String p = String.valueOf(pressure);
		pressure_TV.setText(p);
		pressure_TV.invalidate();
	}
	
	/** Called when the user clicks the button */
	public void startStopRecording(View view) {
		
		TimerTask timerTask = new TimerTask() {
			public void run() {
				updateUI();
				updateFile(resultsFile);
			}
		};
		
	    if (isRecording == 0) {
	    	isRecording = 1;
	    	startStopButton.setText("Stop Recoding");
	    	
	    	handleAccelerometer();
			handleGyroscope();
			handleMagnetometer();
			handlePressure();
			
			updateTimer = new Timer();
			updateTimer.schedule(timerTask, 0, 100);
	    } else {
	    	isRecording = 0;
	    	startStopButton.setText("Start Recoding");
	    	
	    	updateTimer.cancel();
	    }
	}
	
	public void deleteRecording(View view) {
		File results = new File(getApplicationContext().getFilesDir() + "/"+ resultsFile);
		if (results.exists()) {
			Toast.makeText(getApplicationContext(), "Data File Removed", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(), "No Data File To Remove", Toast.LENGTH_SHORT).show();
		}
		results.delete();
	}
	
	public void sendReport() {
		
		String aEmailList[] = { "matthieu.moro@me.com" };
		
		String resultsString = "";
		
		// put the file content into a string
		InputStream inputStream;
		try {
			inputStream = openFileInput(resultsFile);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			// READ STRING OF UNKNOWN LENGTH
			StringBuilder sb = new StringBuilder();
		    char[] inputBuffer = new char[2048];
		    int l;
		    // FILL BUFFER WITH DATA
		    while ((l = inputStreamReader.read(inputBuffer)) != -1) {
		        sb.append(inputBuffer, 0, l);
		    }
		    resultsString = sb.toString();
		    inputStream.close();
		} catch (Exception e) {
				
		}
		
		// send the mail
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.setType("message/rfc822");
		sendIntent.putExtra(Intent.EXTRA_EMAIL, aEmailList);
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, String.format("Data recording %s", sdf.format(new Date())));
		sendIntent.putExtra(Intent.EXTRA_TEXT, resultsString);
				
		startActivity(Intent.createChooser(sendIntent, "Send mail..."));
	}
}
