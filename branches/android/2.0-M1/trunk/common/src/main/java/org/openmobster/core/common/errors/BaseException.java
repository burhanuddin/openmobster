/**
 * Copyright (c) {2003,2009} {openmobster@gmail.com} {individual contributors as indicated by the @authors tag}.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openmobster.core.common.errors;

/**
 * @author openmobster@gmail.com
 *
 */
public abstract class BaseException extends Exception
{
	public BaseException(String message)
	{
		super(message);
	}
	
	public BaseException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
