/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Peter Brewer
 * 
 * 		This program is free software: you can redistribute it and/or modify it under the terms of
 * 		the GNU General Public License as published by the Free Software Foundation, either version
 * 		3 of the License, or (at your option) any later version.
 * 
 * 		This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * 		without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 		See the GNU General Public License for more details.
 * 
 * 		You should have received a copy of the GNU General Public License along with this program.
 * 		If not, see <http://www.gnu.org/licenses/>.
 * 
 *************************************************************************************************/
package org.fhaes.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.plexus.util.FileUtils;
import org.fhaes.analysis.FHMatrix;
import org.fhaes.filefilter.SHPFileFilter;
import org.fhaes.model.SortedListModel;
import org.fhaes.preferences.App;
import org.fhaes.preferences.FHAESPreferences.PrefKey;
import org.fhaes.preferences.wrappers.RadioButtonWrapper;
import org.fhaes.util.Builder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import edu.emory.mathcs.backport.java.util.Arrays;
import net.miginfocom.swing.MigLayout;

/**
 * ShapeFileDialog Class.
 */
@SuppressWarnings("rawtypes")
public class ShapeFileDialog extends JDialog implements ActionListener, DocumentListener {
	
	private static final Logger log = LoggerFactory.getLogger(ShapeFileDialog.class);
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField txtFilename;
	private JList lstAvailableYears;
	private JList lstSelectedYears;
	private FHMatrix fhm;
	private SortedListModel availableYearsModel = new SortedListModel();
	private SortedListModel selectedYearsModel = new SortedListModel();
	private Component parent;
	private JButton btnOK;
	private JLabel lblSelectedYears;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JRadioButton radStyle1;
	private JRadioButton radStyle2;
	
	/**
	 * Create the dialog.
	 * 
	 * @throws Exception
	 */
	public ShapeFileDialog(Component parent, FHMatrix fhm) throws NullPointerException {
		
		if (fhm == null)
			throw new NullPointerException("FHMatrix cannot be null");
		this.fhm = fhm;
		initGUI();
		populate();
	}
	
	/**
	 * TODO
	 */
	private void populate() {
		
		Integer startyear = fhm.getEarliestYearInOutput();
		Integer endyear = fhm.getLatestEndYearInOutput();
		
		log.debug("Start year = " + startyear);
		log.debug("End year = " + endyear);
		
		if (startyear != null & endyear != null)
		{
			for (Integer year = fhm.getEarliestYearInOutput(); year <= fhm.getLatestEndYearInOutput(); year++)
			{
				availableYearsModel.addElement(year);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void addSelectedYear() {
		
		if (!this.lstAvailableYears.isSelectionEmpty())
		{
			List<Integer> values = Arrays.asList(lstAvailableYears.getSelectedValues());
			
			if (radStyle1.isSelected() && values.size() + this.selectedYearsModel.getSize() > 255)
			{
				JOptionPane.showMessageDialog(this, "Shapefiles with this attribute table style can only contain data for 255 years",
						"Too many years", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			for (Integer val : values)
			{
				selectedYearsModel.addElement(val);
			}
			
			if (lstAvailableYears.getSelectedIndices().length > 0)
			{
				List selected = Arrays.asList(lstAvailableYears.getSelectedValues());
				for (Object item : selected)
				{
					this.availableYearsModel.removeElement(item);
				}
			}
			
		}
		
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private void removeSelectedYear() {
		
		if (!this.lstSelectedYears.isSelectionEmpty())
		{
			List<Integer> values = Arrays.asList(lstSelectedYears.getSelectedValues());
			
			for (Integer val : values)
			{
				availableYearsModel.addElement(val);
			}
			
			if (lstSelectedYears.getSelectedIndices().length > 0)
			{
				List selected = Arrays.asList(lstSelectedYears.getSelectedValues());
				for (Object item : selected)
				{
					this.selectedYearsModel.removeElement(item);
				}
			}
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initGUI() {
		
		setBounds(100, 100, 582, 333);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[right][grow]", "[][][][grow]"));
		{
			JLabel lblOutputFilename = new JLabel("Output filename:");
			contentPanel.add(lblOutputFilename, "cell 0 0,alignx trailing");
		}
		{
			txtFilename = new JTextField();
			txtFilename.getDocument().addDocumentListener(this);
			contentPanel.add(txtFilename, "flowx,cell 1 0,growx");
			txtFilename.setColumns(10);
		}
		{
			JButton btnBrowse = new JButton("Browse");
			btnBrowse.setActionCommand("Browse");
			btnBrowse.addActionListener(this);
			contentPanel.add(btnBrowse, "cell 1 0");
		}
		{
			JLabel lblAttributeTableStyle = new JLabel("Attribute table style:");
			contentPanel.add(lblAttributeTableStyle, "cell 0 1,alignx trailing");
		}
		{
			radStyle1 = new JRadioButton("One marker per site with multiple year attributes");
			radStyle1.setActionCommand("Style1");
			radStyle1.addActionListener(this);
			// radStyle1.setSelected(true);
			
			buttonGroup.add(radStyle1);
			contentPanel.add(radStyle1, "cell 1 1");
		}
		{
			radStyle2 = new JRadioButton("Multiple markers per site, one for each year");
			radStyle2.setActionCommand("Style2");
			radStyle2.addActionListener(this);
			buttonGroup.add(radStyle2);
			
			new RadioButtonWrapper(buttonGroup, PrefKey.SHAPEFILE_OUTPUT_STYLE, "Style1");
			
			contentPanel.add(radStyle2, "cell 1 2");
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, "cell 0 3,alignx right,growy");
			panel.setLayout(new MigLayout("insets n n n 0", "[119px]", "[15px]"));
			{
				JLabel lblYearsToInclude = new JLabel("Years to include:");
				panel.add(lblYearsToInclude, "cell 0 0,alignx left,aligny top");
			}
		}
		{
			JPanel panel = new JPanel();
			contentPanel.add(panel, "cell 1 3,alignx left,growy");
			panel.setLayout(new MigLayout("", "[160px:160px][][160px:160px]", "[][grow]"));
			{
				JLabel lblAvailableYears = new JLabel("Available:");
				panel.add(lblAvailableYears, "cell 0 0,alignx center");
			}
			{
				lblSelectedYears = new JLabel("Selected:");
				panel.add(lblSelectedYears, "cell 2 0,alignx center");
			}
			{
				JScrollPane scrollPane = new JScrollPane();
				panel.add(scrollPane, "cell 0 1,grow");
				{
					
					lstAvailableYears = new JList();
					lstAvailableYears.setModel(availableYearsModel);
					scrollPane.setViewportView(lstAvailableYears);
				}
			}
			{
				JButton btnRemove = new JButton("<");
				btnRemove.setActionCommand("removeYear");
				btnRemove.addActionListener(this);
				{
					JButton btnAdd = new JButton(">");
					btnAdd.setActionCommand("addYear");
					btnAdd.addActionListener(this);
					panel.add(btnAdd, "flowy,cell 1 1");
				}
				panel.add(btnRemove, "cell 1 1");
			}
			{
				JScrollPane scrollPane = new JScrollPane();
				panel.add(scrollPane, "cell 2 1,grow");
				{
					lstSelectedYears = new JList();
					lstSelectedYears.setModel(selectedYearsModel);
					selectedYearsModel.addListDataListener(new ListDataListener() {
						
						@Override
						public void contentsChanged(ListDataEvent arg0) {
							
							pingLayout();
						}
						
						@Override
						public void intervalAdded(ListDataEvent arg0) {
							
							pingLayout();
							
						}
						
						@Override
						public void intervalRemoved(ListDataEvent arg0) {
							
							pingLayout();
							
						}
						
					});
					scrollPane.setViewportView(lstSelectedYears);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnOK = new JButton("OK");
				btnOK.setActionCommand("OK");
				btnOK.addActionListener(this);
				buttonPane.add(btnOK);
				getRootPane().setDefaultButton(btnOK);
				btnOK.setEnabled(false);
			}
			{
				JButton btnCancel = new JButton("Cancel");
				btnCancel.setActionCommand("Cancel");
				btnCancel.addActionListener(this);
				buttonPane.add(btnCancel);
			}
		}
		
		this.setLocationRelativeTo(parent);
		this.setIconImage(Builder.getApplicationIcon());
		this.setTitle("Generate shapefile");
		pingLayout();
	}
	
	public JList getList() {
		
		return lstAvailableYears;
	}
	
	public JList getList2() {
		
		return lstSelectedYears;
	}
	
	private void pingLayout() {
		
		if (this.txtFilename.getText() != null && this.txtFilename.getText().length() > 0)
		{
		
		}
		else
		{
			btnOK.setEnabled(false);
			return;
		}
		
		if (this.lstSelectedYears.getModel().getSize() == 0)
		{
			btnOK.setEnabled(false);
			return;
		}
		
		btnOK.setEnabled(true);
		
		if (radStyle1.isSelected())
		{
			this.lblSelectedYears.setText("Selected (max. 255)");
		}
		else
		{
			this.lblSelectedYears.setText("Selected");
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {
		
		if (evt.getActionCommand().equals("Cancel"))
		{
			dispose();
		}
		else if (evt.getActionCommand().equals("Browse"))
		{
			File filename = this.getOutputFile(new SHPFileFilter());
			
			if (filename != null)
			{
				txtFilename.setText(filename.getAbsolutePath());
			}
			
		}
		else if (evt.getActionCommand().equals("OK"))
		{
			try
			{
				if (radStyle1.isSelected())
				{
					doProcessing();
				}
				else
				{
					doProcessingStyle2();
				}
			}
			catch (MalformedURLException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dispose();
		}
		else if (evt.getActionCommand().equals("removeYear"))
		{
			this.removeSelectedYear();
		}
		else if (evt.getActionCommand().equals("addYear"))
		{
			this.addSelectedYear();
		}
		else if (evt.getActionCommand().equals("Style1"))
		{
			if (selectedYearsModel.getSize() > 255)
			{
				JOptionPane.showMessageDialog(this, "Shapefiles with this attribute table style can only contain data for 255 years.\n"
						+ "Remove some years and try again", "Too many years", JOptionPane.ERROR_MESSAGE);
						
				radStyle2.setSelected(true);
				return;
			}
			
			this.lblSelectedYears.setText("Selected (max. 255)");
		}
		else if (evt.getActionCommand().equals("Style2"))
		{
			this.lblSelectedYears.setText("Selected");
			
		}
	}
	
	@SuppressWarnings("unchecked")
	private void doProcessing() throws IOException {
		
		/*
		 * We use the DataUtilities class to create a FeatureType that will describe the data in our shapefile.
		 * 
		 * See also the createFeatureType method below for another, more flexible approach.
		 */
		final SimpleFeatureType TYPE = createFeatureType(selectedYearsModel.getAllElements());
		
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
		
		/*
		 * GeometryFactory will be used to create the geometry attribute of each feature (a Point object for the location)
		 */
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(fhm.getFileSiteResult()));
			
			/* First line of the data file is the header */
			String line = reader.readLine();
			String tokens2[] = line.split("\\,");
			Integer colcount = tokens2.length;
			reader.close();
			
			ArrayList yearsToInclude = new ArrayList();
			
			for (int i = 0; i < lstSelectedYears.getModel().getSize(); i++)
			{
				yearsToInclude.add(lstSelectedYears.getModel().getElementAt(i));
			}
			
			for (int col = 1; col < colcount; col++)
			{
				reader = new BufferedReader(new FileReader(fhm.getFileSiteResult()));
				int linecount = -1;
				SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
				String name = "";
				double latitude = 0;
				double longitude = 0;
				Point point;
				Integer year = 0;
				for (line = reader.readLine(); line != null; line = reader.readLine())
				{
					linecount++;
					
					if (line.trim().length() > 0)
					{ // skip blank lines
						String tokens[] = line.split("\\,");
						
						if (linecount == 0)
						{
							name = tokens[col].trim();
						}
						else if (linecount == 1)
						{
							longitude = Double.parseDouble(tokens[col]);
						}
						else if (linecount == 2)
						{
							latitude = Double.parseDouble(tokens[col]);
							/* Longitude (= x coord) first ! */
							point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
							featureBuilder.add(point);
							featureBuilder.add(name);
						}
						else
						{
							year = Integer.parseInt(tokens[0]);
							if (ArrayUtils.contains(yearsToInclude.toArray(new Integer[yearsToInclude.size()]), year))
							{
								featureBuilder.add(Integer.parseInt(tokens[col].trim()));
							}
							else
							{
								log.debug("Not storing info for year = " + year);
							}
							
						}
					}
				}
				SimpleFeature feature = featureBuilder.buildFeature(null);
				features.add(feature);
				reader.close();
			}
		}
		catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*
		 * Get an output file name and create the new shapefile
		 */
		File newFile = new File(txtFilename.getText());
		
		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
		
		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("url", newFile.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);
		
		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		
		/*
		 * TYPE is used as a template to describe the file contents
		 */
		newDataStore.createSchema(TYPE);
		/*
		 * Write the features to the shapefile
		 */
		Transaction transaction = new DefaultTransaction("create");
		
		String typeName = newDataStore.getTypeNames()[0];
		SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
		SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();
		/*
		 * The Shapefile format has a couple limitations: - "the_geom" is always first, and used for the geometry attribute name -
		 * "the_geom" must be of type Point, MultiPoint, MuiltiLineString, MultiPolygon - Attribute names are limited in length - Not all
		 * data types are supported (example Timestamp represented as Date)
		 * 
		 * Each data store has different limitations so check the resulting SimpleFeatureType.
		 */
		System.out.println("SHAPE:" + SHAPE_TYPE);
		
		if (featureSource instanceof SimpleFeatureStore)
		{
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
			/*
			 * SimpleFeatureStore has a method to add features from a SimpleFeatureCollection object, so we use the ListFeatureCollection
			 * class to wrap our list of features.
			 */
			SimpleFeatureCollection collection = new ListFeatureCollection(TYPE, features);
			featureStore.setTransaction(transaction);
			try
			{
				featureStore.addFeatures(collection);
				transaction.commit();
			}
			catch (Exception problem)
			{
				problem.printStackTrace();
				transaction.rollback();
			}
			finally
			{
				transaction.close();
			}
		}
		else
		{
			System.out.println(typeName + " does not support read/write access");
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void doProcessingStyle2() throws IOException {
		
		/*
		 * We use the DataUtilities class to create a FeatureType that will describe the data in our shapefile.
		 * 
		 * See also the createFeatureType method below for another, more flexible approach.
		 */
		final SimpleFeatureType TYPE2 = createStyle2FeatureType();
		
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
		
		/*
		 * GeometryFactory will be used to create the geometry attribute of each feature (a Point object for the location)
		 */
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
		
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(fhm.getFileSiteResult()));
			
			/* First line of the data file is the header */
			String line = reader.readLine();
			String tokens2[] = line.split("\\,");
			Integer colcount = tokens2.length;
			reader.close();
			
			ArrayList yearsToInclude = new ArrayList();
			
			for (int i = 0; i < lstSelectedYears.getModel().getSize(); i++)
			{
				yearsToInclude.add(lstSelectedYears.getModel().getElementAt(i));
			}
			
			for (int col = 1; col < colcount; col++)
			{
				reader = new BufferedReader(new FileReader(fhm.getFileSiteResult()));
				int linecount = -1;
				SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE2);
				String name = "";
				double latitude = 0;
				double longitude = 0;
				Point point = null;
				Integer year = 0;
				for (line = reader.readLine(); line != null; line = reader.readLine())
				{
					linecount++;
					
					if (line.trim().length() > 0)
					{ // skip blank lines
						String tokens[] = line.split("\\,");
						
						if (linecount == 0)
						{
							name = tokens[col].trim();
						}
						else if (linecount == 1)
						{
							longitude = Double.parseDouble(tokens[col]);
						}
						else if (linecount == 2)
						{
							latitude = Double.parseDouble(tokens[col]);
							/* Longitude (= x coord) first ! */
							point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
						}
						else
						{
							year = Integer.parseInt(tokens[0]);
							if (ArrayUtils.contains(yearsToInclude.toArray(new Integer[yearsToInclude.size()]), year))
							{
								featureBuilder.add(point);
								featureBuilder.add(name);
								featureBuilder.add(year);
								featureBuilder.add(Integer.parseInt(tokens[col].trim()));
								SimpleFeature feature = featureBuilder.buildFeature(null);
								features.add(feature);
								featureBuilder = new SimpleFeatureBuilder(TYPE2);
							}
							else
							{
								log.debug("Not storing info for year = " + year);
							}
							
						}
					}
					
				}
				
				reader.close();
			}
		}
		catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			try
			{
				reader.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*
		 * Get an output file name and create the new shapefile
		 */
		File newFile = getOutputFile();
		
		ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();
		
		Map<String, Serializable> params = new HashMap<String, Serializable>();
		params.put("url", newFile.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);
		
		ShapefileDataStore newDataStore = (ShapefileDataStore) dataStoreFactory.createNewDataStore(params);
		
		/*
		 * TYPE is used as a template to describe the file contents
		 */
		newDataStore.createSchema(TYPE2);
		/*
		 * Write the features to the shapefile
		 */
		Transaction transaction = new DefaultTransaction("create");
		
		String typeName = newDataStore.getTypeNames()[0];
		SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);
		SimpleFeatureType SHAPE_TYPE = featureSource.getSchema();
		/*
		 * The Shapefile format has a couple limitations: - "the_geom" is always first, and used for the geometry attribute name -
		 * "the_geom" must be of type Point, MultiPoint, MuiltiLineString, MultiPolygon - Attribute names are limited in length - Not all
		 * data types are supported (example Timestamp represented as Date)
		 * 
		 * Each data store has different limitations so check the resulting SimpleFeatureType.
		 */
		System.out.println("SHAPE:" + SHAPE_TYPE);
		
		if (featureSource instanceof SimpleFeatureStore)
		{
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
			/*
			 * SimpleFeatureStore has a method to add features from a SimpleFeatureCollection object, so we use the ListFeatureCollection
			 * class to wrap our list of features.
			 */
			SimpleFeatureCollection collection = new ListFeatureCollection(TYPE2, features);
			featureStore.setTransaction(transaction);
			try
			{
				featureStore.addFeatures(collection);
				transaction.commit();
			}
			catch (Exception problem)
			{
				problem.printStackTrace();
				transaction.rollback();
			}
			finally
			{
				transaction.close();
			}
		}
		else
		{
			System.out.println(typeName + " does not support read/write access");
			
		}
		
	}
	
	/**
	 * Get the output filename. Checks and enforces that the specified filename ends with .shp
	 * 
	 * @return
	 */
	private File getOutputFile() {
		
		if (txtFilename.getText() == null || txtFilename.getText().length() == 0)
		{
			return null;
		}
		else if (!txtFilename.getText().toLowerCase().endsWith(".shp"))
		{
			txtFilename.setText(txtFilename.getText() + ".shp");
		}
		
		return new File(txtFilename.getText());
		
	}
	
	private static SimpleFeatureType createFeatureType(List<Integer> years) {
		
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("FHAES");
		builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system
		
		// add attributes in order
		builder.add("the_geom", Point.class);
		builder.add("name", String.class);
		for (Integer i : years)
		{
			builder.add(i + "", Integer.class);
		}
		
		// build the type
		final SimpleFeatureType FHAES = builder.buildFeatureType();
		
		return FHAES;
	}
	
	private static SimpleFeatureType createStyle2FeatureType() {
		
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("FHAES");
		builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system
		
		// add attributes in order
		builder.add("the_geom", Point.class);
		builder.add("name", String.class);
		builder.add("year", Integer.class);
		builder.add("value", Integer.class);
		
		// build the type
		final SimpleFeatureType FHAES = builder.buildFeatureType();
		
		return FHAES;
	}
	
	/**
	 * Prompt the user for an output filename
	 * 
	 * @param filter
	 * @return
	 */
	private File getOutputFile(FileFilter filter) {
		
		String lastVisitedFolder = App.prefs.getPref(PrefKey.PREF_LAST_EXPORT_FOLDER, null);
		File outputFile;
		
		// Create a file chooser
		final JFileChooser fc = new JFileChooser(lastVisitedFolder);
		
		fc.setAcceptAllFileFilterUsed(true);
		
		if (filter != null)
		{
			fc.addChoosableFileFilter(filter);
			fc.setFileFilter(filter);
		}
		
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle("Save as...");
		
		// In response to a button click:
		int returnVal = fc.showOpenDialog(this);
		
		if (returnVal == JFileChooser.APPROVE_OPTION)
		{
			outputFile = fc.getSelectedFile();
			
			if (FileUtils.getExtension(outputFile.getAbsolutePath()) == "")
			{
				log.debug("Output file extension not set by user");
				
				if (fc.getFileFilter().getDescription().equals(new SHPFileFilter().getDescription()))
				{
					log.debug("Adding shp extension to output file name");
					outputFile = new File(outputFile.getAbsolutePath() + ".shp");
				}
			}
			else
			{
				log.debug("Output file extension set my user to '" + FileUtils.getExtension(outputFile.getAbsolutePath()) + "'");
			}
			
			App.prefs.setPref(PrefKey.PREF_LAST_EXPORT_FOLDER, outputFile.getAbsolutePath());
		}
		else
		{
			return null;
		}
		
		if (outputFile.exists())
		{
			Object[] options = { "Overwrite", "No", "Cancel" };
			int response = JOptionPane.showOptionDialog(this,
					"The file '" + outputFile.getName() + "' already exists.  Are you sure you want to overwrite?", "Confirm",
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, // do not use a custom Icon
					options, // the titles of buttons
					options[0]); // default button title
					
			if (response != JOptionPane.YES_OPTION)
			{
				return null;
			}
		}
		
		return outputFile;
	}
	
	@Override
	public void changedUpdate(DocumentEvent arg0) {
		
		pingLayout();
		
	}
	
	@Override
	public void insertUpdate(DocumentEvent arg0) {
		
		pingLayout();
		
	}
	
	@Override
	public void removeUpdate(DocumentEvent arg0) {
		
		pingLayout();
		
	}
	
}
