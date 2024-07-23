package RESTfulApi.object;

public class Location {
	private double lat;
	private double lng;
	
	public Location() {
		this.lng = 0;
		this.lat = 0;
	}
	public Location(double lng,double lat) {
		this.lng = lng;
		this.lat = lat;
	}
	
	public double getLat() {
		return lat;
	}
	
	public void setLat(double lat) {
		this.lat = lat;
	}
	
	public double getLng() {
		return lng;
	}
	
	public void setLng(double lng) {
		this.lng = lng;
	}
	
	@Override
	public String toString() {
		return "[lat=" + lat + ", lng=" + lng + "]";
	}
}