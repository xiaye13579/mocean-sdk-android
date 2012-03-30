package com.MASTAdView;

public class AutoDetectParameters {
	private static AutoDetectParameters instance;
	private String latitude; 
	private String longitude;
	private String carrier;
	private String country;
	private String ua;
	private String version;
	private Integer connectionSpeed;

	private AutoDetectParameters() {
	}

	public AutoDetectParameters(String latitude, String longitude,
			String carrier, String country, String ua, String version,
			Integer connectionSpeed) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.carrier = carrier;
		this.country = country;
		this.ua = ua;
		this.version = version;
		this.connectionSpeed = connectionSpeed;
	}

	public static synchronized AutoDetectParameters getInstance() {
		if(instance == null) {
			instance = new AutoDetectParameters();
		}
		return instance;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getUa() {
		return ua;
	}

	public void setUa(String ua) {
		this.ua = ua;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Integer getConnectionSpeed() {
		return connectionSpeed;
	}

	public void setConnectionSpeed(Integer connectionSpeed) {
		this.connectionSpeed = connectionSpeed;
	}

}
