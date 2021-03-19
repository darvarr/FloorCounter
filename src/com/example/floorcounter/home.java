package com.example.floorcounter;

import com.example.floorcounter.R;

import android.app.Fragment;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class home extends Fragment implements OnClickListener {
    
    private static final String ARG_SECTION_NUMBER = "section_number";

    private TextView greenS, pinkS, blueS;
    
    private Button play_pauseB;
	private Button stopB;
	private Button lockB;
	
	private long timeWhenStopped = 0;
	
	public Chronometer chrono;
	
	private boolean unlock = true;
	private boolean play = true;

    public static home newInstance(int sectionNumber) {
        home fragment = new home();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public home() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab1, container, false);
        
        /*----Binding of the TextViews with the variables previously defined----*/
        greenS = (TextView) v.findViewById(R.id.green_sphere);
        pinkS = (TextView) v.findViewById(R.id.pink_sphere);
        
        /*----Binding of the Buttons with the variables previously defined----*/
        play_pauseB = (Button) v.findViewById(R.id.pause_play);
        stopB = (Button) v.findViewById(R.id.stop);
        lockB = (Button) v.findViewById(R.id.lock);
        
        chrono = (Chronometer) v.findViewById(R.id.blue_sphere); 
        startChrono();
        
        play_pauseB.setOnClickListener(this);
        stopB.setOnClickListener(this);
        lockB.setOnClickListener(this);
        
        return v;
    }
    
    public void set_textView_text(int text_id, String s){
    	switch(text_id){
    		case R.id.green_sphere:
    			greenS.setText(s);
    			break;
    		case R.id.pink_sphere:
    			pinkS.setText(s);
    			break;
    		case R.id.blue_sphere:
    			blueS.setText(s);
    			break;
    		default:
    			break;
    	}
    }
    
    public void set_button_text(int butt_id, String s){
    	switch(butt_id){
    		case R.id.pause_play:
    			play_pauseB.setText(s);
    			break;
    		case R.id.lock:
    			lockB.setText(s);
    			break;
    		case R.id.stop:
    			stopB.setText(s);
    			break;
    		default:
    			break;
    	}
    }

    @Override
	public void onClick(View v) {
		if(v.getId() == R.id.pause_play){
			pause_play();
		}
		else if(v.getId() == R.id.lock){
			lock();
		}
		else if(v.getId() == R.id.stop){
			stop();
		}
	}
	
	public void pause_play(){
		MainActivity m = (MainActivity)getActivity();
        
		if (play == true){
			m.unregister();
			play_pauseB.setText("Start");
			play = false;
			stopChrono();
		}
		else{
			m.register();
			play_pauseB.setText("Pause");
			play = true;
			startChrono();
		}
	}
	
	/*----Management of the Lock/Unlock button----*/
	public void lock(){
		
		if (unlock == true){
			unlock = false;
			play_pauseB.setClickable(false);
			stopB.setClickable(false);
			lockB.setText("Unlock");
		}
		else{
			unlock = true;
			play_pauseB.setClickable(true);
			stopB.setClickable(true);
			lockB.setText("Lock");
		}
	}
	
	/*----The stop button is used to reset all the data----*/
	public void stop(){
		MainActivity m = (MainActivity)getActivity();
		m.reset();
		play = true;
		chrono.setBase(SystemClock.elapsedRealtime());
		timeWhenStopped = 0;
	}
	
	public void startChrono(){
		chrono.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
		chrono.start();
	}
	
	public void stopChrono(){
		timeWhenStopped = chrono.getBase() - SystemClock.elapsedRealtime();
		chrono.stop();
	}
}
