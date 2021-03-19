package com.example.floorcounter;

import java.util.Locale;

import com.example.floorcounter.R;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;


public class MainActivity extends Activity implements ActionBar.TabListener, SensorEventListener {

    
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    
    /*----Sensors managing variables----*/
    private SensorManager sensorManager;
	private Sensor sensorPress;
	private Sensor sensorLinear;
	
	//Threshold for the changes in the gravity on the phone
	private double gravity_threshold = 20;
			
	//Threshold for the changes in the pressure
	private double press_threshold = 0.18;
			
	private int nStep = 0;
	private int floor = 0;
	private float g;
	private long startTime, endTime;
	private long prevTime;
	public double step_length = 0;
	private double meters = 0;
	private double MET = 4;
	private double calories = 0;
	
	public int weight, height;
			
	//thresholds for the time during the steps monitoring --> 1000(milliseconds in a seconds)*n(number of seconds)
	private long step_secs = 5000;
			
	//flags used to check if the change in the gravity on the phone is a step
	private boolean flag1 = true;
	private boolean flag2 = false;
	private boolean flag_step = false;
			
	//used to calculate the stopping time during the user go upstairs or downstairs
	private long stop_duration = 0;
	
	//flag used to check if the user is moving
	private boolean move = false;
	
	private float prev_pressure;
	private boolean flag_pressure = true;
	private long press_secs = 10000;
	
	private home tab1;
	private stats tab2;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        
        tab1 = new home();
        tab2 = new stats();
        
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), tab1, tab2);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        
        register();
        
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

    	Fragment tab1;
    	Fragment tab2;
    	
        public SectionsPagerAdapter(FragmentManager fm, Fragment f1, Fragment f2) {
            super(fm);
            tab1 = f1;
            tab2 = f2;
        }

        @Override
        public Fragment getItem(int position) {
           switch(position){
            	case 0:
            		return tab1;
            	case 1:
            		return tab2;
            	default:
            		return tab1;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// TODO Auto-generated method stub
		mViewPager.setCurrentItem(tab.getPosition());
	}
	
	public void reset(){
		flag1 = true;
		flag2 = false;
		flag_step = false;
		flag_pressure = true;
		floor = 0;
		move = false;
		nStep = 0;
		tab2.set_textView_text(R.id.step_counter, "0");
		tab2.set_textView_text(R.id.calories, "0");
		tab2.set_textView_text(R.id.meters, "0");
		tab2.set_textView_text(R.id.upFloor_no, "0");
		tab2.set_textView_text(R.id.downFloor_no, "0");
	}

	public void unregister(){
		sensorManager.unregisterListener(this, sensorLinear);
		sensorManager.unregisterListener(this, sensorPress);
	}
	
	public void register(){
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        
        /*----Listen for the Linear Acceleration Sensor----*/
        sensorLinear = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorManager.registerListener(this, sensorLinear, SensorManager.SENSOR_DELAY_FASTEST);
        
        /*----Listen for the Barometer Sensor----*/
        sensorPress = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sensorManager.registerListener(this, sensorPress, SensorManager.SENSOR_DELAY_NORMAL);
	}


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void onSensorChanged(SensorEvent event) {
		/* Step Counter */
		
		
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			
			/* Estimation of the acceleration of the phone */
			g = event.values[0] * event.values[0] + event.values[1] * event.values[1] + event.values[2] * event.values[2];
			
			if (g>1 && g<25){
				if (flag1 == true && g > gravity_threshold){
					prevTime = System.currentTimeMillis();
					flag1 = false;
					flag2 = true;
				}
				else if (flag2 == true && g < gravity_threshold){
					flag_step = true;
					flag2 = false;
				}
				else if(flag_step == true && g > gravity_threshold){
					long time = System.currentTimeMillis();

					//checks if the gravity goes under the threshold in step_secs seconds
					if (Math.abs(time-prevTime)<step_secs){		
						nStep++;
						meters += step_length/100;
						calories += (weight*MET)/3600;
						move = true;
						tab2.set_textView_text(R.id.step_counter, ""+nStep);
						tab2.set_double_text(R.id.meters, meters);
						tab2.set_double_text(R.id.calories, calories);
						stop_duration = 0;
					}
					flag1 = true;
					flag_step = false;
				}
				else {
					move = false;
				}
			}
			else{
				move = false;
			}
	    }
		
		/* Pressure Analyzer */
		if ((event.sensor.getType() == Sensor.TYPE_PRESSURE) && (move == true)){
			
			if (flag_pressure == true){
				startTime = System.currentTimeMillis();
				flag_pressure = false;
				prev_pressure = event.values[0];
			}
			
			endTime = System.currentTimeMillis();
			if (Math.abs(endTime - startTime - stop_duration)>=press_secs){
				flag_pressure = true;
				if (Math.abs(prev_pressure-event.values[0])>press_threshold){
					if((prev_pressure-event.values[0])>0){
						tab1.set_textView_text(R.id.pink_sphere, "Up");
						floor++;
						tab2.set_textView_text(R.id.upFloor_no, ""+(Integer.parseInt(tab2.get_text(R.id.upFloor_no).toString())+1));
						tab1.set_textView_text(R.id.green_sphere, ""+floor);
					}
					else if ((prev_pressure-event.values[0])<0){
						tab1.set_textView_text(R.id.pink_sphere, "Down");
						floor--;
						tab2.set_textView_text(R.id.downFloor_no, ""+(Integer.parseInt(tab2.get_text(R.id.downFloor_no).toString())+1));
						tab1.set_textView_text(R.id.green_sphere, ""+floor);
					}
				}
			}
		}
		
		/* to compute the number of mSec of the stops of the users along the stair */
		if (move == false){
			long stop_startTime = System.currentTimeMillis();
			stop_duration = stop_startTime - stop_duration;
		}
	}
}
