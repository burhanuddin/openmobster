/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.mobileCloud.api.ui.framework;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import net.rim.device.api.i18n.Locale;
import net.rim.device.api.xml.parsers.DocumentBuilder;
import net.rim.device.api.xml.parsers.DocumentBuilderFactory;

import org.openmobster.core.mobileCloud.rimos.util.GenericAttributeManager;
import org.openmobster.core.mobileCloud.rimos.util.IOUtil;
import org.openmobster.core.mobileCloud.rimos.errors.ErrorHandler;
import org.openmobster.core.mobileCloud.rimos.errors.SystemException;

/**
 * @author openmobster@gmail.com
 *
 */
public final class AppConfig 
{
	private static AppConfig singleton;
	
	private GenericAttributeManager attrMgr;
	
	private AppConfig()
	{		
	}
	
	public static AppConfig getInstance()
	{
		if(AppConfig.singleton == null)
		{
			synchronized(AppConfig.class)
			{
				if(AppConfig.singleton == null)
				{
					AppConfig.singleton = new AppConfig();
				}
			}
		}
		return AppConfig.singleton;
	}
	
	public synchronized void init()
	{
		try
		{
			if(this.attrMgr == null)
			{
				this.attrMgr = new GenericAttributeManager();
			
				//parse the /moblet-app/moblet-app.xml
				InputStream is = AppConfig.class.getResourceAsStream("/moblet-app/moblet-app.xml");
				String xml = new String(IOUtil.read(is));
				
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document root = builder.parse(new ByteArrayInputStream(xml.getBytes()));
				
				//parse app commands
				NodeList commands = root.getElementsByTagName("command");
				Hashtable registeredCommands = new Hashtable();
				if(commands != null)
				{					
					this.attrMgr.setAttribute("commands", registeredCommands);
					int size = commands.getLength();
					for(int i=0; i<size; i++)
					{
						Element command = (Element)commands.item(i);
						String commandId = command.getAttribute("id");
						String commandClassName = command.getFirstChild().getNodeValue().trim();
						registeredCommands.put(commandId, Class.forName(commandClassName).newInstance());						
					}					
				}
												
				//parse app locale, optional
				NodeList localeNode = root.getElementsByTagName("locale");
				if(localeNode != null && localeNode.getLength()>0)
				{					
					Element localeElem = (Element)localeNode.item(0);					
					
					Element languageElem = (Element)localeElem.getElementsByTagName("language-code").item(0);					
					String language = languageElem.getFirstChild().getNodeValue().trim();
					
					String country = null;
					NodeList countryNode = localeElem.getElementsByTagName("country-code");
					if(countryNode!=null && countryNode.getLength()>0)
					{
						Element countryElem = (Element)countryNode.item(0);
						country = countryElem.getFirstChild().getNodeValue().trim();
					}
					
					Locale locale = null;
					if(country != null)
					{
						locale = Locale.get(language, country);
					}
					else
					{
						locale = Locale.get(language); 
					}
					this.attrMgr.setAttribute("locale", locale);
				}
				
				//parse screens
				NodeList screens = root.getElementsByTagName("screen");
				Hashtable screenConfig = new Hashtable();
				if(screens !=null && screens.getLength()>0)
				{										
					this.attrMgr.setAttribute("screenConfig", screenConfig);
					int size = screens.getLength();
					for(int i=0; i<size; i++)
					{
						Element screen = (Element)screens.item(i);
						String screenClass = screen.getFirstChild().getNodeValue().trim();
						String screenId = screen.getAttribute("id");
						screenConfig.put(screenId, screenClass);
					}					
				}	
				
				//Validate the configuration
				//Must have a startup command
				if(!registeredCommands.containsKey("startup"))
				{
					throw new IllegalStateException("Startup Command is missing!!");
				}
				
				//Must have a home command
				if(!screenConfig.containsKey("home"))
				{
					throw new IllegalStateException("Home screen is missing!!");
				}
				
				//Parse the channels that this moblet-app is interested in				
				NodeList channels = root.getElementsByTagName("channel");
				Vector registeredChannels = new Vector();				
				if(channels!=null && channels.getLength()>0)
				{
					int size = channels.getLength();
					for(int i=0; i<size; i++)
					{
						Element channelElem = (Element)channels.item(i);
						String channel = channelElem.getFirstChild().getNodeValue().trim();
						if(!registeredChannels.contains(channel))
						{
							registeredChannels.addElement(channel);							
						}
					}
					this.attrMgr.setAttribute("channels", registeredChannels);
				}
			}
		}
		catch(Exception e)
		{
			SystemException syse = new SystemException(this.getClass().getName(), "init", new Object[]{
				"Exception: "+e.toString(),
				"Message: "+e.getMessage()
			});
			ErrorHandler.getInstance().handle(syse);
			
			this.attrMgr.setAttribute("frameworkBootstrapFailure", syse);
		}
	}
	//-------------------------------------------------------------------------------------------------------------------------------------------						
	public Locale getAppLocale()
	{
		return (Locale)this.attrMgr.getAttribute("locale");
	}
	
	public Hashtable getScreenConfig()
	{
		return (Hashtable)this.attrMgr.getAttribute("screenConfig");
	}
	
	public Hashtable getAppCommands()
	{
		return (Hashtable)this.attrMgr.getAttribute("commands");
	}
	
	public boolean isFrameworkActive()
	{
		return this.attrMgr.getAttribute("frameworkBootstrapFailure") == null;
	}	
	
	public Vector getChannels()
	{
		Vector registeredChannels = (Vector)this.attrMgr.getAttribute("channels");
		return registeredChannels;
	}
}
