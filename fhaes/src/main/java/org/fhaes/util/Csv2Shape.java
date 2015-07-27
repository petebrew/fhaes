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
package org.fhaes.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import org.geotools.data.DataUtilities;
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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * Csv2Shape Class. This example reads data for point locations and associated attributes from a comma separated text (CSV) file and exports
 * them as a new shapefile. It illustrates how to build a feature type.
 * <p>
 * Note: to keep things simple in the code below the input file should not have additional spaces or tabs between fields.
 */
public class Csv2Shape {
	
	/**
	 * TODO
	 * 
	 * @param file
	 * @throws Exception
	 */
	public static void saveToShapefile(File file) throws Exception {
		
		/*
		 * We use the DataUtilities class to create a FeatureType that will describe the data in our shapefile.
		 * 
		 * See also the createFeatureType method below for another, more flexible approach.
		 */
		final SimpleFeatureType TYPE = DataUtilities.createType("Location", "the_geom:Point:srid=4326," + // <- the geometry attribute:
		// Point type
		"name:String," + // <- a String attribute
				"number:Integer" // a number attribute
		);
		System.out.println("TYPE:" + TYPE);
		
		/*
		 * A list to collect features as we create them.
		 */
		List<SimpleFeature> features = new ArrayList<SimpleFeature>();
		
		/*
		 * GeometryFactory will be used to create the geometry attribute of each feature, using a Point object for the location.
		 */
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
		
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(TYPE);
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		try
		{
			/* First line of the data file is the header */
			String line = reader.readLine();
			System.out.println("Header: " + line);
			
			for (line = reader.readLine(); line != null; line = reader.readLine())
			{
				if (line.trim().length() > 0)
				{ // skip blank lines
					String tokens[] = line.split("\\,");
					
					double latitude = Double.parseDouble(tokens[0]);
					double longitude = Double.parseDouble(tokens[1]);
					String name = tokens[2].trim();
					int number = Integer.parseInt(tokens[3].trim());
					
					/* Longitude (= x coord) first ! */
					Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
					
					featureBuilder.add(point);
					featureBuilder.add(name);
					featureBuilder.add(number);
					SimpleFeature feature = featureBuilder.buildFeature(null);
					features.add(feature);
				}
			}
		}
		finally
		{
			reader.close();
		}
		
		/*
		 * Get an output file name and create the new shapefile
		 */
		File newFile = getNewShapeFile(file);
		
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
	
	/**
	 * Prompt the user for the name and path to use for the output shapefile.
	 * 
	 * @param csvFile the input csv file used to create a default shapefile name
	 * 			
	 * @return name and path for the shapefile as a new File object
	 */
	private static File getNewShapeFile(File csvFile) {
		
		String path = csvFile.getAbsolutePath();
		String newPath = path.substring(0, path.length() - 4) + ".shp";
		
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Save shapefile");
		chooser.setSelectedFile(new File(newPath));
		
		int returnVal = chooser.showSaveDialog(null);
		
		if (returnVal != JFileChooser.APPROVE_OPTION)
		{
			// the user cancelled the dialog
			System.exit(0);
		}
		
		File newFile = chooser.getSelectedFile();
		if (newFile.equals(csvFile))
		{
			System.out.println("Error: cannot replace " + csvFile);
			System.exit(0);
		}
		
		return newFile;
	}
	
	/**
	 * Here is how you can use a SimpleFeatureType builder to create the schema for your shapefile dynamically.
	 * <p>
	 * This method is an improvement on the code used in the main method above (where we used DataUtilities.createFeatureType) because we
	 * can set a Coordinate Reference System for the FeatureType and a a maximum field length for the 'name' field dddd
	 */
	@SuppressWarnings("unused")
	private static SimpleFeatureType createFeatureType() {
		
		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("Location");
		builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system
		
		// add attributes in order
		builder.add("Location", Point.class);
		builder.length(15).add("Name", String.class); // <- 15 chars width for name field
		
		// build the type
		final SimpleFeatureType LOCATION = builder.buildFeatureType();
		
		return LOCATION;
	}
}
