/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.db.testdrive.util;

import net.rim.device.api.synchronization.UIDGenerator;
import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.io.Base64InputStream;

/**
 * @author openmobster@gmail.com
 *
 */
public final class GeneralTools
{
	public static long generateUniqueId()
	{
		long uid = 0;
		
		int value = UIDGenerator.getUID();
		int scope = UIDGenerator.getUniqueScopingValue();
		
		uid = UIDGenerator.makeLUID(scope, value);
		
		return uid;
	}
	
	public static String encodeBinaryData(byte[] data)
	{
		try
		{
			if(data == null)
			{
				return null;
			}
			
			return Base64OutputStream.encodeAsString(data, 0, data.length, false, false);
		}
		catch(Exception ioe)
		{
			return null;
		}
	}
	
	public static byte[] decodeBinaryData(String input)
	{
		try
		{
			if(input == null)
			{
				return null;
			}
			
			return Base64InputStream.decode(input);
		}
		catch(Exception ioe)
		{
			System.out.println("Decoding Error-------------------------------------------");
			System.out.println("Exception: "+ioe.toString());
			System.out.println("Exception: "+ioe.getMessage());
			return null;
		}
	}
}
