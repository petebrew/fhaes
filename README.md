# FHAES
![FHAES Logo](https://raw.githubusercontent.com/petebrew/fhaes/master/fhaes/src/main/resources/images/fhaes-application.png "Fire History Analysis and Exploration System")

This repository contains the source code for the Fire History Analysis and Exploration System.  If you are simply wanting to download and use FHAES then please go to the main download pages: http://download.fhaes.org

The repository contains several modules:
 * __FHAES__ - the main FHAES GUI 
 * __FHUtil__ - utility module containing various packages useful to other FHAES modules are fire history analysis generally
 * __FHSampleSize__ - module for the analysis of sample size
 
 
## Developing FHAES

FHAES is open source software and we actively encourage collaboration and assistance from others in the community.  There is always lots to do, even for people with little or no programming experience.  Please get in touch with the development team as we'd love to hear from you.

### Development environment

The IDE of choice of the main FHAES developers is [Eclipse](http://www.eclipse.org). There are many other IDEs around and there is no reason you can't use them instead.  Either way, the following instructions will hopefully be of use.  We have successfully developed FHAES on Mac, Windows and Linux computers.  The methods for setting up are almost identical.  

The first step is to install Eclipse, Java JDK (currently we are using Java 7), Git, Maven, RPM and Install4J.  These are all readily available from their respective websites.  On Ubuntu they can be install from the command line easily as follows:

```sudo apt-get install eclipse git default-jdk maven2 rpm```

Once installed, you can then launch Eclipse.  To access the FHAES source code you may need to install one or more Git plugins. Go to ```Help > Install new software``` then select the main Update site in the 'Work with' box, then locate the git plugins under collaboration. You will also need to install the m2e Maven plugin to Eclipse.  You may need to add the Maven update site as this plugin may not be available in the main Eclipse repository.  You can do this by clicking the 'Add' button and using the URL ```http://m2eclipse.sonatype.org/sites/m2e```.  Once installed you will need to restart Eclipse before continuing.

Next you need to get the FHAES source code.  Go to ```File > Import > Git > Projects from Git```, then in the dialog select 'clone from URI'.  Enter the URI of the FHAES repository: ```https://github.com/petebrew/fhaes.git```, select the master branch and 'import existing projects' from the subsequent pages.

FHAES is comprised of a number of projects which are stored as seperate folders within the Github reposotory.  This enables us to work and package different aspects of the system separately.  As and when these separate projects mature and the convenience for keeping them separate becomes a hinderance, they are then merged into the main FHAES project.  An examples of this is the data entry module (FHRecorder) which is now packaged within the main FHAES project.  You will need to set up separate projects in Eclipse for each of the modules mentioned at the top of this page.

The FHAES project is the main application that most users use to access the functionality of the other modules. Although FHAES depends upon the other modules mentioned above, the Maven configuration should retrieve these modules from our Maven repository if you don't intend to work with them.  This means you shouldn't need to configure all modules in Eclipse to get up and running.

To launch FHAES from within Eclipse you will need to go to  ```Run > Run Java application```.  Create a new run configuration with the main class set to 'org.fhaes.gui.MainWindow'.       


### Dependencies
FHAES is built with Maven.  One of the main benefits of Maven is that it handles dependencies efficiently.  All the libraries that FHAES relies upon are configured within the Maven pom.xml file and installed seamlessly as part of the build process. Maven handles transient dependencies (i.e. dependencies of dependencies) automatically.  Therefore if a developer knows he needs the functions within a particular library, he simply needs to supply the details of this library without having to worry about the other libraries that this new library is in turn dependent on.  Maven also manages versions efficiently.  If a library is dependent on a particular version of another library this is specified within the Maven build mechanism.  This means it is much easier to keep dependencies up-to-date without having to worry about the cascading issues that upgrades often have.  In short, Maven is intended to save developers from `JAR hell'.

For the record, FHAES currently depends upon the libraries listed in the table below.  The table also specifies the licenses that these libraries are made available under.

|Library | License |
| ---- | ----- |
|Apache Commons Lang | Apache 2.0 |
|Apache Batik | Apache 2.0 |
|Apache Commons Math | Apache 2.0 |
|Apache ODF Toolkit | Apache 2.0|
|Apache POI | Apache 2.0|
|DendroFileIO | Apache 2.0|
|Geotools | LGPL |
|iText | GAPL|
|Java Simple MVC | MIT|
|JFreeChart | LGPL |
|JMapViewer | GPL |
|JScience | Apache 2.0 |
|Log4J | Apache 2.0|
|MigLayout | BSD|
|Natty | Apache 2.0|
|OpenCSV | Apache 2.0 |
|SLF4J | MIT|
|SwingX | LGPL |
|TridasJLib | Apache 2.0 |

TODO TABLES

### Multimedia resources

FHAES includes infrastructure for multimedia resources such as icons and images within the Maven resource folder `src/main/resources' of the FHUtil module.  The icons are accessed via the static Builder class.  This has various accessor functions which take the filename and return the icon itself or a URI of the icon from within the Jar.

### Logging

Logging in FHAES is handled by the SLF4J and Log4J packages.  Rather than write debug notes directly to System.out, Log4J handles logging in a more intelligent way.  First of all, each log message is assigned a log level which are (in order of severity) fatal, error, warn, info, debug and trace.  Through a log4j.xml configuration file contained within the FHUtil resources folder, we can control the level at which messages are displayed.  For instance while we develop we would likely show all messages up to and including 'trace', but when we deploy we might only want to show messages up to and including 'warn'. 

Log4J also enables us to log to several places (known as appenders), e.g. console, log file or a component within our application.  It is also possible to change the level of logging depending on the log type, so minimal messages can be sent to the console but verbose messages to the log file.  FHAES has the following appenders configured:

 * Standard log file (fhaes.log) that rolls over up to 2mb of messages
 * Console -- standard messages to the console when launched from command line
 * Help console -- simple console component available from the FHAES application menu ```Help > Error log viewer```

To alter the way these appenders are configured you need to edit the log4j.xml file.  See the Log4J documentation for further information.

Using the logging framework is very simple.  Just define a Logger as a static variable in your class like this:

 ```private final static Logger log = LoggerFactory.getLogger(MyClass.class);```
 
where MyClass is the name of the current class.  Then you can log messages simply by calling:
 
 ```
 log.warn('My message')
 log.debug('My message') 
 ```

Before managed logging was introduced to FHAES, debugging was often handled through the use of System.out and System.err messages.  To ensure that these messages are not lost we use another package called SysOutOverSLF4J.  This redirects messages sent to System.out and System.err to the logging system.  This is a temporary solution so when working on older classes, please take the time to transition these older calls to the proper logging calls.  We can then remove the need for SysOutOverSLF4J.


### Preferences

It is helpful to remember certain user preferences e.g. last folder opened etc so that they don't have to do tasks repeatedly.  This is achieved through the use of the FHAESPreferences class.  This stores preferences in a specific place on the users computer depending on the operating system they are using.  For instance in Windows preferences are stored in the registry and in Linux they are stored in a hidden file within the users home folder.

The preferences are accessed from the static member as follows:
```\code{FHAESPreferences.setPref(PrefKey.PREFKEY, "value");}```

where PrefKey.PREFKEY is an enum containing a unique string to identify the preference, and the second value is the string value to set.  

To retrieve a preference, you use a similar syntax:
```\code{FHAESPreferences.getPref(PrefKey.PREFKEY, "default value");}```

When you get a preference the second parameter contains the default value to return if no preference is found. There are a number of variations on the setPref and getPref functions which ensure the data type of the preference you are saving/retrieving e.g. setIntPref, setBooleanPref etc.  


### Build script

FHAES is built using Maven and is controlled through the pom.xml file stored in the base of the FHAES source code.  It would be possible to distribute FHAES as an executable JAR file, however, we we have chosen to also produce native installers for the major platforms.  This gives us the opportunity to tightly integrate FHAES into the users operating system making it behave more like the native applications they are used to using.  For instance, it means that we can associate the .FHX file extension in Windows with FHAES enabling users to double click on files and have them open automatically.  It also means the application has its own icon rather than the generic Java coffee icon.  In MacOSX it also means the menu bar is placed at the top of the screen rather than in the window.

While you develop, Maven should automatically build FHAES for you in the background.  Specific build commands are only required as you approach a release.  We use the standard Maven 'life cycle' for building, packaging and deploying FHAES.  The method for doing this in Eclipse is by right clicking on the pom.xml file and selecting ```Run as > Maven package``` etc.  If the option you want is not displayed, you will need to create an entry in the build menu by going to ```Run > Run configurations```, then create a new Maven Build with the required 'goal'.   The main goals are as follows:

* __clean__ - This deletes any previously compiled classes and packages in the target folder.  It should only be necessary to run this occassionally if Maven has got a bit confused.  If this is the case you may also need to force Eclipse to clean too by going to ```Project > Clean...```
* __package__ - This compiles FHAES and builds a single executable JAR containing all dependencies (thanks to the maven-shade-plugin) along with native Windows, MacOSX and Linux packages.  These are all placed in structured folders within `target\\Binaries' ready for deploying on the website.  
* __deploy__ - This uploads the compiled jar into the maven.tridas.org repository.  Note that you will need to either run this phase from the command line or by setting up a customer run configuration in Eclipse.



### Creating native installers

A pragmatic decision was made to use the closed source Install4J install for packaging.  Although an open source tool is preferable (NSIS was used for some time), Install4J is the only tool we've found that gives us the one click package and coding signing for all three target platforms (Windows, OSX and Linux).  Install4J is a commercial tool although the distributing company (EJ Technologies) provides free licenses to approved open source projects.  The lead developed (Peter Brewer) has license keys to run Install4J for FHAES.  The drawback with this approach is that only Peter can produce the final releases.

Install4J includes a GUI application for generating and editing the configuration files used to compile the final packages.  The configuration is saved in a the fhaes.install4j file within the Native folder.  During the `package' phase of the life cycle, Maven runs Install4J and produces Linux Deb, Linux RPM, OSX, Windows, and Unix installer packages.



### Code signing

From Windows Vista and MacOSX 10.7 (Lion) onwards code signing has become important.  Windows applications that are not signed result in terrifying warning messages, and in OSX by default they cannot even be run.  The idea behind code signing is that it provides some level of security for the user as code signing certificates can be revoked if an application is deemed to be malicious.  

To sign an application you first need a code signing certificate.  In fact you need two: one for Windows and Java; and another for OSX as Apple only support certificates issued by themselves.  A generic Windows and Java certificate can be purchased from a variety of suppliers (e.g. Verisign etc) the Apple certificate must be purchased through the Apple Developer Connection.  

Code signing the Java jars and Windows installers is handled within Maven and Install4J.  The current set up in the Maven pom.xml file is configured for Ubuntu Linux.  If you are developing on another platform you will need to make changes.  

The Java jars are signed using a Java utility called jarsigner that comes with your JDK so there is nothing to install.  The pom.xml is currently hard coded to access the certificate and key files stored on the lead FHAES development machine.  You will need to alter these settings accordingly.  

With the inclusion of the GateKeeper technology in OSX 10.7, code signing has become almost essential in OSX.  The jar wrapped in native apps will fail to load under default security settings and the OS reports the file as 'damaged' because the JavaApplicationStub used has an existing signature and the contents of the package has changed.  This signature must be removed or replaced with another before the application can be launched.  If a self-signed or third party certificate is used then the GateKeeper will block the application saying that it isn't from a trusted developer.  It is possible for users to go to the ```System Properties > Security and Privacy``` and set the application security level to 'any developer' but this is long and involved for novice users and results in a lot of warnings.  

The best way to fix the issue is to sign the application using an Apple certified Developer ID certificate and GateKeeper will allow the application to launch with the default security settings. Obtaining this certificate is a fairly involved process and requires an annual subscription to Apple Developer Connection.  Note that your certificate must begin with 'Developer ID' to work.  Other Apple-provided certificates are used for distributing your application through the Mac App store and will not work.  

The OSX code signing is only possible within OSX or on other platforms using the Install4J application.  No other cross-platform code signing is currently available.  This is one of the primary reasons for choosing Install4J to package FHAES.  Assuming you have your Apple certification correctly installed the OSX code signing is performed automatically by Install4J during the Maven `package' life cycle.


### Developing graphical interfaces

The graphical interfaces in FHAES have mostly been designed using the Google WindowBuilder Pro tool with Eclipse.  Unlike other graphical design tools WindowBuilder has the benefit of being able to parse (most) hand written layout code, so it is possible to manually edit the layouts if you prefer.  However, most of the interfaces are built using the MigLayout layout manager which can be a little tricky to code by hand. 



### Writing documentation

The documentation in FHAES is written in the well established typesetting language {\LaTeXe}.  {\LaTeX} is a great tool for producing high quality documentation with a good structure and style.  Unlike standard WYSIWYG (what you see is what you get) word processing applications like Microsoft Word, {\LaTeX} uses simple plain text code to layout a document so that it is often described as WYSIWYM (what you see is what you mean)!  The style of a {\LaTeX} document is handled separated enabling the author to concentrate on content.  By removing the possibility for authors to tinker with font sizes etc, {\LaTeX} forces you to create clear, well structured documents.  For further details see the [LaTeX Wikibook](http://en.wikibooks.org/wiki/LaTeX/).

{\LaTeX} has fantastic cross-referencing and citation functionality built in.  Please follow the lead of the existing documentation!



### Making a new release
Making a new release should be a relatively quick and simply process, but there are still a few things to remember:

 * Make sure this documentation is up-to-date. 
 * Update the logging appenders to an appropriate level so that the user is not swamped by debug messages.
 * Increment the build version number(s) in the pom.xml of FHAES and any of the other modules as applicable.
 * Check the code in Eclipse and eliminate as many warnings as possible.
 * Make sure the developers metadata is correct in the pom.xml.  Add any new developers that have joined the project since the last release.
 * Run Maven deploy on the secondary packages.
 * Run Maven package on the FHAES project itself.
 * __TEST__!  Make sure all packages are working on the major operating systems being supported.
 * Copy the packages to the website and change the most recent build number on the website.  This will inform users of the new release the next time they load FHAES.
