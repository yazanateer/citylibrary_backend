package RESTfulApi.object;

import java.util.Date;
import java.util.Map;

public class ObjectBoundary {
    private ObjectId objectId;
    private String type;
    private String alias;
    private Location location;
    private Boolean active;
    private Date creationTimestamp;
    private CreatedBy createdBy;
    private Map<String, Object> objectDetails;

    // Default constructor
    public ObjectBoundary() {
    }

    // Parameterized constructor
    public ObjectBoundary(ObjectId objectId, String type, String alias, Boolean active, Date creationTimestamp,
                          Location location, CreatedBy createdBy, Map<String, Object> objectDetails) {
        this.objectId = objectId;
        this.type = type;
        this.alias = alias;
        this.location = location;
        this.active = active;
        this.creationTimestamp = creationTimestamp;
        this.createdBy = createdBy;
        this.objectDetails = objectDetails;
    }

    // Getters and Setters
    public ObjectId getObjectId() {
        return objectId;
    }

    public void setObjectId(ObjectId objectId) {
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(Date creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public CreatedBy getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(CreatedBy createdBy) {
        this.createdBy = createdBy;
    }

    public Map<String, Object> getObjectDetails() {
        return objectDetails;
    }

    public void setObjectDetails(Map<String, Object> objectDetails) {
        this.objectDetails = objectDetails;
    }
}