package RESTfulApi.entity;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "OBJECTS")
public class SuperAppObjectEntity {
    @Id
    private String objectId;
    private String type;
    private String alias;
    private boolean active;
    private Date creationTimestamp;
    private double lat;
    private double lng;
    private String createdBySuperapp;
    private String createdByEmail;
    private String objectDetails;

    // Default constructor
    public SuperAppObjectEntity() {
    }

    // Parameterized constructor
    public SuperAppObjectEntity(String objectId, String type, String alias, boolean active, Date creationTimestamp,
                                double lat, double lng, String createdBySuperapp, String createdByEmail, String objectDetails) {
        this.objectId = objectId;
        this.type = type;
        this.alias = alias;
        this.active = active;
        this.creationTimestamp = creationTimestamp;
        this.lat = lat;
        this.lng = lng;
        this.createdBySuperapp = createdBySuperapp;
        this.createdByEmail = createdByEmail;
        this.objectDetails = objectDetails;
    }

    // Getters and Setters
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
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

    public String getCreatedBySuperapp() {
        return createdBySuperapp;
    }

    public void setCreatedBySuperapp(String createdBySuperapp) {
        this.createdBySuperapp = createdBySuperapp;
    }

    public String getCreatedByEmail() {
        return createdByEmail;
    }

    public void setCreatedByEmail(String createdByEmail) {
        this.createdByEmail = createdByEmail;
    }

    public String getObjectDetails() {
        return objectDetails;
    }

    public void setObjectDetails(String objectDetails) {
        this.objectDetails = objectDetails;
    }
}