/* 
 * Copyright (C) 2012 Yee Young Han <websearch@naver.com> (http://blog.naver.com/websearch)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 */

package com.example.testandroidsipstack;

import com.cppsipstack.SipCallRtp;
import com.cppsipstack.SipServerInfo;
import com.cppsipstack.SipStackSetup;
import com.cppsipstack.SipUserAgent;
import com.cppsipstack.SipUserAgentCallBack;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;

public class MainActivity extends Activity implements OnClickListener, SipUserAgentCallBack 
{
	EditText m_txtPhoneNumber;
	TextView m_txtLog;
	String m_strCallId = "";
	Handler	 m_clsHandler;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_main );
		
		m_txtPhoneNumber = (EditText)findViewById( R.id.txtPhoneNumber );
		m_txtLog = (TextView)findViewById( R.id.txtLog );
		
		findViewById( R.id.btnStartCall ).setOnClickListener( this );
		findViewById( R.id.btnStopCall ).setOnClickListener( this );
		findViewById( R.id.btnAcceptCall ).setOnClickListener( this );
		
		m_clsHandler = new Handler()
    {
    	public void handleMessage( Message m )
    	{
    		Bundle b = m.getData( );
    		
    		String strCommand = (String)b.get( "command" );
    		String strData = (String)b.get( "data" );
    		
    		if( strCommand.equals( "log" ) )
    		{
    			m_txtLog.setText( strData );
    		}
    	}
    };
    
    m_txtPhoneNumber.setText( "1001" );
		
		SipServerInfo clsInfo = new SipServerInfo();
		
		clsInfo.m_strIp = "192.168.0.7";
		clsInfo.m_iPort = 5060;
		clsInfo.m_strDomain = "192.168.0.7";
		clsInfo.m_strUserId = "1010";
		clsInfo.m_strPassWord = "1234";
	
		SipUserAgent.InsertRegisterInfo( clsInfo );
		SipUserAgent.SetCallBack( this );
		
		SipStackSetup clsSetup = new SipStackSetup();
		
		SipUserAgent.Start( clsSetup );
	}
	
	@Override
	protected void onDestroy()
	{
		SipUserAgent.Stop( );
		
		super.onDestroy( );
	}

	@Override
	public void onClick( View v )
	{
		switch( v.getId( ) )
		{
		case R.id.btnStartCall:
			{
				String strPhoneNumber = m_txtPhoneNumber.getText( ).toString( );
				SipCallRtp clsRtp = new SipCallRtp();
				
				clsRtp.m_strIp = "127.0.0.1";
				clsRtp.m_iPort = 10000;
				clsRtp.m_iCodec = 0;

				m_strCallId = SipUserAgent.StartCall( "1010", strPhoneNumber, clsRtp );
				Log.d( "test", "callid(" + m_strCallId + ")" );
			}
			break;
		case R.id.btnStopCall:
			{
				SipUserAgent.StopCall( m_strCallId, 200 );
			}
			break;
		case R.id.btnAcceptCall:
			{
				SipCallRtp clsRtp = new SipCallRtp();
				
				clsRtp.m_strIp = "127.0.0.1";
				clsRtp.m_iPort = 10000;
				clsRtp.m_iCodec = 0;
				
				SipUserAgent.AcceptCall( m_strCallId, clsRtp );
			}
			break;
		}
	}

	@Override
	public void EventRegister( SipServerInfo clsInfo, int iStatus )
	{
		Log.d( "debug", "EventRegister(" + iStatus + ")" );
		
		Send( "log", "EventRegister(" + iStatus + ")" );
	}

	@Override
	public void EventIncomingCall( String strCallId, String strFrom, String strTo, SipCallRtp clsRtp )
	{
		Send( "log", "EventIncomingCall " + strFrom );
		
		m_strCallId = strCallId;
	}

	@Override
	public void EventCallRing( String strCallId, int iSipStatus, SipCallRtp clsRtp )
	{
		Send( "log", "EventCallRing(" + iSipStatus + ")" );
	}

	@Override
	public void EventCallStart( String strCallId, SipCallRtp clsRtp )
	{
		Send( "log", "EventCallStart" );
	}

	@Override
	public void EventCallEnd( String strCallId, int iSipStatus )
	{
		Send( "log", "EventCallEnd" );
	}
	
	void Send( String strCommand, String strData )
  {
  	Bundle b = new Bundle();
  	
  	Message m = m_clsHandler.obtainMessage( );
  	b.putString( "command", strCommand );
  	b.putString( "data", strData );
  	m.setData( b );
  	m_clsHandler.sendMessage( m );
  }
}