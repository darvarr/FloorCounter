package com.example.floorcounter;

import com.example.floorcounter.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class stats extends Fragment implements OnClickListener {
    
    private static final String ARG_SECTION_NUMBER = "section_number";

    private TextView step_counter, upFloor, calories, downFloor, meters;
    private EditText height, weight;
    private Button submit;
    
    private RadioGroup radioGroup;
    
    public Chronometer chrono;
    
    private GridLayout grid;
    private LinearLayout bottomL;
    
    public static stats newInstance(int sectionNumber) {
        stats fragment = new stats();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public stats() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab2, container, false);
        
        step_counter = (TextView) v.findViewById(R.id.step_counter);
        upFloor = (TextView) v.findViewById(R.id.upFloor_no);
        downFloor = (TextView) v.findViewById(R.id.downFloor_no);
        
        submit = (Button) v.findViewById(R.id.submit);
        
        grid = (GridLayout) v.findViewById(R.id.gridLayout);
        bottomL = (LinearLayout) v.findViewById(R.id.bottom_layout);
        
        height = (EditText) v.findViewById(R.id.height);
        weight = (EditText) v.findViewById(R.id.weight);
        
        radioGroup = (RadioGroup) v.findViewById(R.id.radioG);
        
        submit.setOnClickListener(this);
        
        chrono = (Chronometer) v.findViewById(R.id.chrono);
        chrono.start();
        
        return v;
    }
    
    public void set_double_text(int text_id, double s){
    	switch(text_id){
    		case R.id.calories:
    			try{
    				calories.setText(String.format( "%.2f", s));
        			} catch (NullPointerException e){
        				return;
        			}
    			break;
    		case R.id.meters:
    			try{
    			meters.setText(String.format( "%.2f", s));;
    			} catch (NullPointerException e){
    				return;
    			}
    			break;
    		default:
    			break;
    	}
    }
    
    public void set_textView_text(int text_id, String s){
    	switch(text_id){
    		case R.id.step_counter:
    			step_counter.setText(s);
    			break;
    		case R.id.upFloor_no:
    			upFloor.setText(s);
    			break;
    		case R.id.downFloor_no:
    			downFloor.setText(s);
    			break;
    		default:
    			break;
    	}
    }
    
    public String get_text(int text_id){
    	String s = null;
    	switch(text_id){
		case R.id.step_counter:
			s = (String) step_counter.getText();
			break;
		case R.id.upFloor_no:
			s = (String) upFloor.getText();
			break;
		case R.id.downFloor_no:
			s = (String) downFloor.getText();
			break;
		case R.id.calories:
			try{
				s = (String) calories.getText();
			} catch (NullPointerException e){
				Toast toast=Toast.makeText(getActivity(),"Height, Weight and Gender not yet initializated.",Toast.LENGTH_LONG);
				toast.show();
			}
			break;
		case R.id.meters:
			try{
				s = (String) meters.getText();
			} catch (NullPointerException e){
				Toast toast=Toast.makeText(getActivity(),"Height, Weight and Gender not yet initializated.",Toast.LENGTH_LONG);
				toast.show();
			}
			break;
		default:
			break;
    	}
    	return s;
    }

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.submit){
			MainActivity m = (MainActivity)getActivity();
			/*----Check if the values inserted are integers----*/
			try{
				m.height = Integer.parseInt(height.getText().toString());
				m.weight = Integer.parseInt(weight.getText().toString());
			} catch(NumberFormatException e){
				Toast toast=Toast.makeText(getActivity(),"One between Height and Weight (or both) is not a correct value, check them.",Toast.LENGTH_LONG);
				toast.show();
				return;
			}
			
			/*----Radio Button to determine the gender----*/
			int gender = radioGroup.getCheckedRadioButtonId();
			if (gender == R.id.M)
				m.step_length = m.height*0.415;
			else if (gender == R.id.F)
				m.step_length = m.height*0.413;
			else{
				//if no RadioButton selected
				Toast toast=Toast.makeText(getActivity(),"Please select your gender.",Toast.LENGTH_LONG);
				toast.show();
				return;
			}
			
			modifyView();
		}
	}
	
	private void modifyView(){
		
		bottomL.removeAllViews();
		
		final TextView cal_title = new TextView(getActivity());
		final TextView cal = new TextView(getActivity());
		final TextView met_title = new TextView(getActivity());
		final TextView met = new TextView(getActivity());
		
		final LinearLayout cal_container = new LinearLayout(getActivity());
		final LinearLayout met_container = new LinearLayout(getActivity());
		
		cal_container.setOrientation(LinearLayout.VERTICAL);
		met_container.setOrientation(LinearLayout.VERTICAL);
		
		cal_title.setId(R.id.cal_title);
		cal.setId(R.id.calories);
		met_title.setId(R.id.met_title);
		met.setId(R.id.meters);
		
		cal_title.setText("Calories");
		cal.setText("0");
		met_title.setText("Meters");
		met.setText("0");
		
		/*----Applying the styles to the textViews----*/
		cal_title.setTextAppearance(getActivity(), R.style.stats_title);
		cal.setTextAppearance(getActivity(), R.style.stats_values);
		met_title.setTextAppearance(getActivity(), R.style.stats_title);
		met.setTextAppearance(getActivity(), R.style.stats_values);
		
		/*----Adding the TextViews to the Layouts'----*/
		cal_container.addView(cal_title);
		cal_container.addView(cal);
		met_container.addView(met_title);
		met_container.addView(met);
		
		/*----Adding the 'cal' layout to the GridLayout'----*/
		GridLayout.Spec row = GridLayout.spec(2);
		GridLayout.Spec coloumn = GridLayout.spec(0);
		GridLayout.LayoutParams params = new GridLayout.LayoutParams(row, coloumn);
		params.setMargins(0, 80, 0, 0);
		grid.addView(cal_container, params);
		
		/*----Adding the 'met' layout to the GridLayout'----*/
		row = GridLayout.spec(2);
		coloumn = GridLayout.spec(1);
		params = new GridLayout.LayoutParams(row, coloumn);
		params.setMargins(0, 80, 0, 0);
		grid.addView(met_container, params);
		
		/*----Keeping references to the TextViews----*/
        calories = cal;
        meters = met;
	}
}
