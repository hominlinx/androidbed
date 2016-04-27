package com.uperone.zxing.ui;

import android.content.Intent;
import android.view.View;

import com.uperone.zxing.R;

public class MainActivity extends BaseActivity {
	@Override
	public void setContentView() {
		setContentView( R.layout.activity_main_layout );
	}

	@Override
	public void findViews() {
		
	}

	@Override
	public void getData() {
		
	}

	@Override
	public void showConent() {
		
	}
	
	public void onClick( View v ){
		switch( v.getId( ) ){
		case R.id.generateBtnId:{
			enterGenerate( );
		}
		break;
		case R.id.captureBtnId:{
			enterCapture( );
		}
		break;
		default:{
			
		}
		break;
		}
	}
	
	private void enterGenerate( ){
		startActivity( new Intent( this, GenerateActivity.class ) );
	}
	
	private void enterCapture( ){
		startActivity( new Intent( this, CaptureActivity.class ) );
	}
}
