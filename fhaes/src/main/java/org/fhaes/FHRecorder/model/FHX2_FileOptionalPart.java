/**************************************************************************************************
 * Fire History Analysis and Exploration System (FHAES), Copyright (C) 2015
 * 
 * Contributors: Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble, and Peter Brewer
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
package org.fhaes.FHRecorder.model;

import java.io.Serializable;

/**
 * FHX2_FileOptionalPart Class. This class contains the optional part of the FHX file data.
 * 
 * @author Alex Beatty, Clayton Bodendein, Kyle Hartmann, Scott Goble
 */
public class FHX2_FileOptionalPart implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static int NUMBER_OF_FIELDS = 30;
	
	private String nameOfSite, siteCode, collectionDate, collectors, crossdaters;
	private String numberSamples, speciesName, commonName, habitatType, country;
	private String state, county, parkMonument, nationalForest, rangerDistrict;
	private String township, range, section, quarterSection, utmEasting;
	private String utmNorthing, latitude, longitude, topographicMap, lowestElev;
	private String highestElev, slope, aspect, areaSampled, substrateType;
	private String comments;
	
	private Boolean doneLoadingFile = false;
	
	private int numberOfFieldsLoaded = 0;
	
	/**
	 * Default Constructor for FHX2_FileOptionalPart. The constructor initializes the variables to null.
	 */
	public FHX2_FileOptionalPart() {
		
		nameOfSite = "";
		siteCode = "";
		collectionDate = "";
		collectors = "";
		crossdaters = "";
		numberSamples = "";
		speciesName = "";
		commonName = "";
		habitatType = "";
		country = "";
		state = "";
		county = "";
		parkMonument = "";
		nationalForest = "";
		rangerDistrict = "";
		township = "";
		range = "";
		section = "";
		quarterSection = "";
		utmEasting = "";
		utmNorthing = "";
		latitude = "";
		longitude = "";
		topographicMap = "";
		lowestElev = "";
		highestElev = "";
		slope = "";
		aspect = "";
		areaSampled = "";
		substrateType = "";
		comments = "";
	}
	
	/**
	 * Returns a value indicating whether or not all header values were present during file load. This function is used when determining
	 * whether or not the file being loaded is a composite file.
	 * 
	 * @return true if all were present, false otherwise
	 */
	public boolean fileHasValidHeader() {
		
		if (numberOfFieldsLoaded == NUMBER_OF_FIELDS)
			return true;
		else
			return false;
	}
	
	/**
	 * Checks whether or not the FHX file contains data in the non-required part.
	 * 
	 * @return true if any field has values, false if all fields are empty as it will OR all zeros to return false
	 */
	public boolean hasValues() {
		
		return !nameOfSite.equals("") || !siteCode.equals("") || !collectionDate.equals("") || !collectors.equals("")
				|| !crossdaters.equals("") || !numberSamples.equals("") || !speciesName.equals("") || !commonName.equals("")
				|| !habitatType.equals("") || !country.equals("") || !state.equals("") || !county.equals("") || !parkMonument.equals("")
				|| !nationalForest.equals("") || !rangerDistrict.equals("") || !township.equals("") || !range.equals("")
				|| !section.equals("") || !quarterSection.equals("") || !utmEasting.equals("") || !utmNorthing.equals("")
				|| !latitude.equals("") || !longitude.equals("") || !topographicMap.equals("") || !lowestElev.equals("")
				|| !highestElev.equals("") || !slope.equals("") || !aspect.equals("") || !areaSampled.equals("")
				|| !substrateType.equals("") || !comments.equals("");
	}
	
	/**
	 * Sets the doneLoadingFile flag to true.
	 */
	public void setDoneLoadingFile() {
		
		doneLoadingFile = true;
	}
	
	/**
	 * ALL GETTER METHODS: Gets a string representing the meta-data field for <name_of_field>.
	 * 
	 * @return <name_of_field>
	 */
	
	public String getNameOfSite() {
		
		return nameOfSite;
	}
	
	public String getSiteCode() {
		
		return siteCode;
	}
	
	public String getCollectionDate() {
		
		return collectionDate;
	}
	
	public String getCollectors() {
		
		return collectors;
	}
	
	public String getCrossdaters() {
		
		return crossdaters;
	}
	
	public String getNumberSamples() {
		
		return numberSamples;
	}
	
	public String getSpeciesName() {
		
		return speciesName;
	}
	
	public String getCommonName() {
		
		return commonName;
	}
	
	public String getHabitatType() {
		
		return habitatType;
	}
	
	public String getCountry() {
		
		return country;
	}
	
	public String getState() {
		
		return state;
	}
	
	public String getCounty() {
		
		return county;
	}
	
	public String getParkMonument() {
		
		return parkMonument;
	}
	
	public String getNationalForest() {
		
		return nationalForest;
	}
	
	public String getRangerDistrict() {
		
		return rangerDistrict;
	}
	
	public String getTownship() {
		
		return township;
	}
	
	public String getRange() {
		
		return range;
	}
	
	public String getSection() {
		
		return section;
	}
	
	public String getQuarterSection() {
		
		return quarterSection;
	}
	
	public String getUtmEasting() {
		
		return utmEasting;
	}
	
	public String getUtmNorthing() {
		
		return utmNorthing;
	}
	
	public String getLatitude() {
		
		return latitude;
	}
	
	public String getLongitude() {
		
		return longitude;
	}
	
	public String getTopographicMap() {
		
		return topographicMap;
	}
	
	public String getLowestElev() {
		
		return lowestElev;
	}
	
	public String getHighestElev() {
		
		return highestElev;
	}
	
	public String getSlope() {
		
		return slope;
	}
	
	public String getAspect() {
		
		return aspect;
	}
	
	public String getAreaSampled() {
		
		return areaSampled;
	}
	
	public String getSubstrateType() {
		
		return substrateType;
	}
	
	public String getComments() {
		
		return comments;
	}
	
	/**
	 * ALL SETTER METHODS: Updates the meta-data field for <name_of_field> with the information in the parameter.
	 * 
	 * @param <name_of_field>
	 */
	public void setNameOfSite(String nameOfSite) {
		
		this.nameOfSite = nameOfSite;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setSiteCode(String siteCode) {
		
		this.siteCode = siteCode;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setCollectionDate(String collectionDate) {
		
		this.collectionDate = collectionDate;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setCollectors(String collectors) {
		
		this.collectors = collectors;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setCrossdaters(String crossdaters) {
		
		this.crossdaters = crossdaters;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setNumberSamples(String numberSamples) {
		
		this.numberSamples = numberSamples;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setSpeciesName(String speciesName) {
		
		this.speciesName = speciesName;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setCommonName(String commonName) {
		
		this.commonName = commonName;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setHabitatType(String habitatType) {
		
		this.habitatType = habitatType;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setCountry(String country) {
		
		this.country = country;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setState(String state) {
		
		this.state = state;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setCounty(String county) {
		
		this.county = county;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setParkMonument(String parkMonument) {
		
		this.parkMonument = parkMonument;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setNationalForest(String nationalForest) {
		
		this.nationalForest = nationalForest;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setRangerDistrict(String rangerDistrict) {
		
		this.rangerDistrict = rangerDistrict;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setTownship(String township) {
		
		this.township = township;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setRange(String range) {
		
		this.range = range;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setSection(String section) {
		
		this.section = section;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setQuarterSection(String quarterSection) {
		
		this.quarterSection = quarterSection;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setUtmEasting(String utmEasting) {
		
		this.utmEasting = utmEasting;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setUtmNorthing(String utmNorthing) {
		
		this.utmNorthing = utmNorthing;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setLatitude(String latitude) {
		
		this.latitude = latitude;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setLongitude(String longitude) {
		
		this.longitude = longitude;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setTopographicMap(String topographicMap) {
		
		this.topographicMap = topographicMap;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setLowestElev(String lowestElev) {
		
		this.lowestElev = lowestElev;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setHighestElev(String highestElev) {
		
		this.highestElev = highestElev;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setSlope(String slope) {
		
		this.slope = slope;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setAspect(String aspect) {
		
		this.aspect = aspect;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setAreaSampled(String areaSampled) {
		
		this.areaSampled = areaSampled;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setSubstrateType(String substrateType) {
		
		this.substrateType = substrateType;
		
		if (!doneLoadingFile)
			numberOfFieldsLoaded++;
	}
	
	public void setComments(String comments) {
		
		this.comments = comments;
	}
}
