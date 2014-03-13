package com.example.compassapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	TextView txtAngle;
	private ImageView imgCompass;

	// guarda el angulo (grado) actual del compass
	private float currentDegree = 0f;

	// El sensor manager del dispositivo
	private SensorManager mSensorManager;
	// Los dos sensores que son necesarios porque TYPE_ORINETATION esta deprecated
	Sensor accelerometer;
	Sensor magnetometer;
	
	float degree;
	float azimut;
	float[] mGravity;
	float[] mGeomagnetic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Se guardan en variables los elementos del layout
		imgCompass = (ImageView) findViewById(R.id.imgViewCompass);		
		txtAngle = (TextView) findViewById(R.id.txtAngle);

		// Se inicializa los sensores del dispositivo android
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
	    accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	    magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	    
	    mGravity = null;
	    mGeomagnetic = null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		// Se registra un listener para los sensores del accelerometer y el magnetometer
		mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
	    mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// Se detiene el listener para no malgastar la bateria
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {		
		
		// Se comprueba que tipo de sensor está activo en cada momento
		switch (event.sensor.getType()) {
			case Sensor.TYPE_ACCELEROMETER:
				mGravity = event.values.clone();
				break;
			case Sensor.TYPE_MAGNETIC_FIELD:
				mGeomagnetic = event.values.clone();
				break;
		}
				
		if ((mGravity != null) && (mGeomagnetic != null)) {
		  	float RotationMatrix[] = new float[16];
		   	boolean success = SensorManager.getRotationMatrix(RotationMatrix, null, mGravity, mGeomagnetic);
		   	if (success) {
		   		float orientation[] = new float[3];
		   		SensorManager.getOrientation(RotationMatrix, orientation);
		   		azimut = orientation[0] * (180 / (float) Math.PI);		   				   		
		   	}
        }
		degree = azimut;
		txtAngle.setText("Angle: " + Float.toString(degree) + " degrees");
		// se crea la animacion de la rottacion (se revierte el giro en grados, negativo)
		RotateAnimation ra = new RotateAnimation(
				currentDegree, 
				degree,
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF,
				0.5f);
		// el tiempo durante el cual la animación se llevará a cabo
		ra.setDuration(1000);
		// establecer la animación después del final de la estado de reserva
		ra.setFillAfter(true);
		// Inicio de la animacion
		imgCompass.startAnimation(ra);
		currentDegree = -degree;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// no se usa
	}
}
