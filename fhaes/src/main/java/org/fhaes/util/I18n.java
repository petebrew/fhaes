package org.fhaes.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.tridas.io.DefaultResourceBundle;

public class I18n {
	
	private I18n() {
	
		// don't instantiate me
	}
	
	// the resource bundle to use
	private final static ResourceBundle msg;
	
	static
	{
		ResourceBundle bundle;
		try
		{
			bundle = ResourceBundle.getBundle("locale/locale");
		}
		catch (MissingResourceException mre)
		{
			try
			{
				bundle = ResourceBundle.getBundle("locale");
			}
			catch (MissingResourceException mre2)
			{
				mre2.printStackTrace();
				bundle = new DefaultResourceBundle();
			}
		}
		msg = bundle;
	}
	
	/**
	 * Get the text for this key. The text has no special control characters in it, and can be presented to the user.
	 * <p>
	 * For example, if the localization file has the line <code>copy = &amp;Copy [accel C]</code>, the string "Copy" is returned.
	 * </p>
	 * 
	 * @param key the key to look up in the localization file
	 * @return the text
	 */
	public static String getText(String key) {
	
		String value = null;
		
		try
		{
			value = msg.getString(key);
		}
		catch (MissingResourceException e)
		{
			System.err.println("Unable to find the translation for the key: " + key);
			return key;
		}
		;
		
		StringBuffer buf = new StringBuffer();
		
		int n = value.length();
		boolean ignore = false;
		for (int i = 0; i < n; i++)
		{
			char c = value.charAt(i);
			switch (c)
			{
				case '&':
					continue;
				case '[':
					ignore = true;
					break;
				case ']':
					ignore = false;
					break;
				default:
					if (!ignore)
					{
						buf.append(c);
					}
			}
		}
		
		return buf.toString().trim();
	}
	
}
