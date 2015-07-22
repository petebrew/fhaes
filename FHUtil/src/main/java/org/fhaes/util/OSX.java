/*******************************************************************************
 * Copyright (C) 2013 Peter Brewer
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 * 	   Ken Harris
 *     Peter Brewer
 ******************************************************************************/

package org.fhaes.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * OSX Class. Class for handling MacOSX specific GUI features such as the About and Quit menu options.
 * 
 * @author pbrewer
 */
public class OSX {

	/**
	 * Uses the other methods of this class to set up the About, Preferences, and Quit menu items.
	 * 
	 * <p>
	 * "About" calls "new AboutBox()". Preferences calls "PrefsDialog.showPreferences()". Quit calls "TellervoMainWindow.quit();
	 * System.exit(0);".
	 * </p>
	 * 
	 * <p>
	 * If this system is not a Mac, does nothing.
	 * </p>
	 */
	public static void configureMenus(FHAESAction aboutAction, FHAESAction quitAction) {

		if (Platform.isOSX())
		{
			// register "about" menuitem
			OSX.registerAboutHandler(aboutAction);

			// and "quit"
			OSX.registerQuitHandler(quitAction);
		}
	}

	// assumes:
	// -- this is actually a mac we're running on
	// -- about.run() throws no exceptions
	// what i do: basically the same as:
	/*
	 * import com.apple.mrj.*; ... MRJApplicationUtils.registerAboutHandler(new MRJAboutHandler() { public void handleAbout() { about.run();
	 * } });
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void registerAboutHandler(final FHAESAction about) {

		try
		{
			InvocationHandler handler = new InvocationHandler() {

				public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {

					about.perform(about);
					return null;

				}
			};

			Class appUtils = Class.forName("com.apple.mrj.MRJApplicationUtils");
			Class paramTypes[] = new Class[] { Class.forName("com.apple.mrj.MRJAboutHandler") };
			Object aboutHandler = Proxy.newProxyInstance(getClassLoader(), paramTypes, handler);
			Method register = appUtils.getMethod("registerAboutHandler", paramTypes);
			register.invoke(appUtils.newInstance(), new Object[] { aboutHandler });
		}
		catch (Exception e)
		{

		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void registerQuitHandler(final FHAESAction action) {

		try
		{
			InvocationHandler handler = new InvocationHandler() {

				public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable {

					action.perform(action);
					System.exit(0);
					return null;
				}
			};

			Class appUtils = Class.forName("com.apple.mrj.MRJApplicationUtils");
			Class paramTypes[] = new Class[] { Class.forName("com.apple.mrj.MRJQuitHandler") };
			Object quitHandler = Proxy.newProxyInstance(getClassLoader(), paramTypes, handler);
			Method register = appUtils.getMethod("registerQuitHandler", paramTypes);
			register.invoke(appUtils.newInstance(), new Object[] { quitHandler });
		}
		catch (Exception e)
		{

		}
	}

	// --------------------------------------------------

	// same thing, but for quit:
	/*
	 * import com.apple.mrj.*; ... MRJApplicationUtils.registerQuitHandler(new MRJQuitHandler() { public void handleQuit() { (new Thread() {
	 * // needs to run in its own thread, for reasons i don't entirely understand. public void run() { try { XCorina.quit(); System.exit(0);
	 * } catch (IllegalStateException ise) { // don't do anything } } }).start(); } });
	 */

	// -- why does it have to be run in its own thread?
	// -- what's with the ISEx? is that how i cancel? if yes, say so.
	/*
	 * @SuppressWarnings("unchecked") public static void registerQuitHandler(Runnable quit) { final Runnable glue = quit;
	 * 
	 * try { InvocationHandler handler = new InvocationHandler() { public Object invoke(Object proxy, Method method, Object[] args) { if
	 * (method.getName().equals("handleQuit")) { // needs to run in its own thread, for reasons i don't entirely understand. (new Thread() {
	 * 
	 * @Override public void run() { try { glue.run(); } catch (IllegalStateException ise) { // don't quit -- i guess this doesn't need to
	 * be rethrown, // though again, no idea why. } } }).start(); } return null; } };
	 * 
	 * Class appUtils = Class.forName("com.apple.mrj.MRJApplicationUtils"); Class paramTypes[] = new Class[] {
	 * Class.forName("com.apple.mrj.MRJQuitHandler") }; Object quitHandler = Proxy.newProxyInstance(getClassLoader(), paramTypes, handler);
	 * Method register = appUtils.getMethod("registerQuitHandler", paramTypes); register.invoke(appUtils.newInstance(), new Object[] {
	 * quitHandler }); } catch (Exception e) { // can't happen <=> bug! new Bug(e); } }
	 * 
	 * // -------------------------------------------------- // finally, for prefs: /* import com.apple.mrj.*; ...
	 * MRJApplicationUtils.registerPrefsHandler(new MRJPrefsHandler() { public void handlePrefs() { prefs.run(); }
	 */

	/*
	 * @SuppressWarnings("unchecked") public static void registerPrefsHandler(Runnable prefs) { final Runnable glue = prefs;
	 * 
	 * try { InvocationHandler handler = new InvocationHandler() { public Object invoke(Object proxy, Method method, Object[] args) { if
	 * (method.getName().equals("handlePrefs")) glue.run(); return null; } };
	 * 
	 * Class appUtils = Class.forName("com.apple.mrj.MRJApplicationUtils"); Class paramTypes[] = new Class[] {
	 * Class.forName("com.apple.mrj.MRJPrefsHandler") }; Object prefsHandler = Proxy.newProxyInstance(getClassLoader(), paramTypes,
	 * handler); Method register = appUtils.getMethod("registerPrefsHandler", paramTypes); register.invoke(appUtils.newInstance(), new
	 * Object[] { prefsHandler }); } catch (Exception e) { // can't happen <=> bug! new Bug(e); } }
	 */
	// --------------------------------------------------
	// common code:
	private static ClassLoader getClassLoader() {

		return org.fhaes.util.OSX.class.getClassLoader();
	}
}
