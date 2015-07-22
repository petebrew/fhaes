package org.fhaes.util;

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
 *     Peter Brewer
 ******************************************************************************/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

/**
 * Csv2Shape2 Class. This example reads data for point locations and associated attributes from a comma separated text (CSV) file and
 * exports them as a new shapefile. It illustrates how to build a feature type.
 * <p>
 * Note: to keep things simple in the code below the input file should not have additional spaces or tabs between fields.
 */
public class Csv2Shape2 {

	/**
	 * TODO
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		File file = JFileDataStoreChooser.showOpenFile("csv", null);
		if (file == null)
		{
			return;
		}

		/*
		 * We use the DataUtilities class to create a FeatureType that will describe the data in our shapefile.
		 * 
		 * See also the createFeatureType method below for another, more flexible approach.
		 */
		final SimpleFeatureType TYPE = createFeatureType();

		/*
		 * We create a FeatureCollection into which we will put each Feature created from a record in the input csv data file
		 */
		DefaultFeatureCollection collection = new DefaultFeatureCollection();

		/*
		 * GeometryFactory will be used to create the geometry attribute of each feature (a Point object for the location)
		 */
		GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);

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

					System.out.println("lat : " + latitude);
					System.out.println("lon : " + longitude);

					String name = tokens[2].trim();

					/* Longitude (= x coord) first ! */
					Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

					featureBuilder.add(point);
					featureBuilder.add(name);
					SimpleFeature feature = featureBuilder.buildFeature(null);
					collection.add(feature);
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
		newDataStore.createSchema(TYPE);

		/*
		 * You can comment out this line if you are using the createFeatureType method (at end of class file) rather than
		 * DataUtilities.createType
		 */
		newDataStore.forceSchemaCRS(DefaultGeographicCRS.WGS84);

		/*
		 * Write the features to the shapefile
		 */
		Transaction transaction = new DefaultTransaction("create");

		String typeName = newDataStore.getTypeNames()[0];
		SimpleFeatureSource featureSource = newDataStore.getFeatureSource(typeName);

		if (featureSource instanceof SimpleFeatureStore)
		{
			SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;

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
			System.exit(0); // success!
		}
		else
		{
			System.out.println(typeName + " does not support read/write access");
			System.exit(1);
		}
	}

	/**
	 * Prompt the user for the name and path to use for the output shapefile
	 * 
	 * @param csvFile the input csv file used to create a default shapefile name
	 * 
	 * @return name and path for the shapefile as a new File object
	 */
	private static File getNewShapeFile(File csvFile) {

		String path = csvFile.getAbsolutePath();
		String newPath = path.substring(0, path.length() - 4) + ".shp";

		JFileDataStoreChooser chooser = new JFileDataStoreChooser("shp");
		chooser.setDialogTitle("Save shapefile");
		chooser.setSelectedFile(new File(newPath));

		int returnVal = chooser.showSaveDialog(null);

		if (returnVal != JFileDataStoreChooser.APPROVE_OPTION)
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
	private static SimpleFeatureType createFeatureType() {

		SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
		builder.setName("Location");
		builder.setCRS(DefaultGeographicCRS.WGS84); // <- Coordinate reference system

		// add attributes in order
		builder.add("the_geom", Point.class);
		builder.length(15).add("Name", String.class); // <- 15 chars width for name field

		// build the type
		final SimpleFeatureType LOCATION = builder.buildFeatureType();

		return LOCATION;
	}

}
