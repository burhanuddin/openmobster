/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.dev.tools.rimos;

import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioStatusListener;

import org.openmobster.core.mobileCloud.rimos.configuration.Configuration;
import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;
import org.openmobster.core.mobileCloud.kernel.DeviceContainer;
import org.openmobster.core.mobileCloud.rimos.module.connection.NotificationListener;

/**
 * Start a listener that monitors wirless network activity
 * 
 * TODO: Does not apply to WIFI connections...add WIFI support
 * TODO: Implement Battery Sensor
 * 
 * @author openmobster@gmail.com
 *
 */
public final class ActivationSensor implements RadioStatusListener
{
	//---NetworkSensor----------------------------------------------------------------------------------------------------------------------
	public synchronized void networkStarted(int networkId, int service) 
	{
		try
		{
			if(!DeviceContainer.getInstance().isContainerActive())
			{
				return;
			}
			
			if(!Configuration.getInstance().isActive())
			{
				ActivationUtil.activateDevice();				
			}
			
			if(service == RadioInfo.NETWORK_SERVICE_DATA)
			{
				NotificationListener notify = NotificationListener.getInstance();
				if(notify!=null)
				{
					notify.restart();
				}
			}
		}
		catch(Exception e)
		{														
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "networkStarted", new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage(),
						"NetworkId="+networkId,
						"Service="+service
					} 
			));
		}
	}

		
	public synchronized void radioTurnedOff() 
	{	
		try
		{
			if(!DeviceContainer.getInstance().isContainerActive())
			{
				return;
			}
			
			if(!Configuration.getInstance().isActive())
			{
				return;				
			}
			
			NotificationListener notify = NotificationListener.getInstance();
			if(notify != null)
			{
				notify.stop();
			}
		}
		catch(Exception e)
		{														
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "radioTurnedOff", new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage()
					} 
			));
		}
	}
	
	public synchronized void signalLevel(int level) 
	{	
		boolean nocoverage = false;
		try
		{
			if(!DeviceContainer.getInstance().isContainerActive())
			{
				return;
			}
			
			if(!Configuration.getInstance().isActive())
			{
				return;				
			}
			
			NotificationListener notify = NotificationListener.getInstance();
			if(level == RadioInfo.LEVEL_NO_COVERAGE)
			{			
				nocoverage = true;
				if(notify!=null && notify.isActive())
				{
					notify.stop();
				}
			}	
		}
		catch(Exception e)
		{														
			ErrorHandler.getInstance().handle(new SystemException(
					this.getClass().getName(), "signalLevel", new Object[]{
						"Exception="+e.toString(),
						"Message="+e.getMessage(),
						"Level="+level,
						"No_Coverage="+nocoverage
					} 
			));
		}
	}
	//---------------------------------------------------------------------------------------------------------------------------------------	
	public void networkStateChange(int state) 
	{		
	}

	public void pdpStateChange(int apn, int state, int cause) 
	{		
	}
	//----------------------------------------------------------------------------------------------------------------------------------------
	public void baseStationChange() 
	{		
	}
	
	public void networkServiceChange(int networkId, int service) 
	{		
	}
	
	public void networkScanComplete(boolean success) 
	{		
	}	
	//------------------------------------------------------------------------------------------------------------------------------------------
	public void batteryGood() 
	{				
	}

	public void batteryLow() 
	{				
	}

	public void batteryStatusChange(int status) 
	{				
	}	
	//-----------------------------------------------------------------------------------------------------------------------------------------
	public void backlightStateChange(boolean on) 
	{				
	}

	public void cradleMismatch(boolean mismatch) 
	{				
	}

	
	public void usbConnectionStateChange(int state) 
	{				
	}
}