package RESTfulApi.object;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import RESTfulApi.entity.SuperAppObjectEntity;
import jakarta.annotation.PostConstruct;
import RESTfulApi.user.UserId;

@Component
public class ObjectsConverter {
    private ObjectMapper jackson;

    @PostConstruct
    public void init() {
        this.jackson = new ObjectMapper();
    }

    public SuperAppObjectEntity toEntity(ObjectBoundary objectBoundary) {
        SuperAppObjectEntity entity = new SuperAppObjectEntity();

        if (objectBoundary.getObjectId() != null && objectBoundary.getObjectId().getSuperapp() != null
                && objectBoundary.getObjectId().getInternalObjectId() != null) {
            entity.setObjectId(objectIdToString(objectBoundary.getObjectId()));
        }
        entity.setType(objectBoundary.getType());
        entity.setAlias(objectBoundary.getAlias());
        entity.setActive(objectBoundary.getActive());
        entity.setCreationTimestamp(objectBoundary.getCreationTimestamp());

        if (objectBoundary.getLocation() != null) {
            entity.setLat(objectBoundary.getLocation().getLat());
            entity.setLng(objectBoundary.getLocation().getLng());
        }
        entity.setCreatedBySuperapp(objectBoundary.getCreatedBy().getUserId().getSuperapp());
        entity.setCreatedByEmail(objectBoundary.getCreatedBy().getUserId().getEmail());
        //entity.setObjectDetails(toEntity(objectBoundary.getObjectDetails()));
        
        try {
            entity.setObjectDetails(this.jackson.writeValueAsString(objectBoundary.getObjectDetails()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return entity;
    }
    public String toEntity(Map<String, Object> data) {
        try {
           return this.jackson.writeValueAsString(data);
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }

    public ObjectBoundary toBoundary(SuperAppObjectEntity entity) {
        ObjectBoundary objectBoundary = new ObjectBoundary();
        objectBoundary.setType(entity.getType());
        objectBoundary.setAlias(entity.getAlias());
        objectBoundary.setActive(entity.isActive());
        objectBoundary.setCreationTimestamp(entity.getCreationTimestamp());
        objectBoundary.setLocation(new Location(entity.getLat(), entity.getLng()));
        objectBoundary.setCreatedBy(new CreatedBy(new UserId(entity.getCreatedBySuperapp(), entity.getCreatedByEmail())));
        objectBoundary.setObjectId(objectIdToBoundary(entity.getObjectId()));
        try {
            Map<String, Object> detailsMap = this.jackson.readValue(entity.getObjectDetails(), new TypeReference<Map<String, Object>>(){});
            objectBoundary.setObjectDetails(detailsMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return objectBoundary;
    }

    private String objectIdToString(ObjectId objectId) {
        return objectId.getSuperapp() + "#" + objectId.getInternalObjectId();
    }
    
    public ObjectId objectIdToBoundary(String entity_id) {
    	String[] id = entity_id.split("#");
    	return new ObjectId(id[0], id[1]);
    }
}