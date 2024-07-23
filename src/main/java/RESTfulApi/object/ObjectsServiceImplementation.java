package RESTfulApi.object;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Comparator;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import RESTfulApi.BadRequestException;
import RESTfulApi.entity.SuperAppObjectEntity;
import RESTfulApi.user.RoleEnum;

@Service
public class ObjectsServiceImplementation implements AllObjectServices {
    private ObjectsCrud objectsCrud;
    private ObjectsConverter objectsConverter;
    private String superApp;
	private CheckFields checkFields;

    @Autowired
    public ObjectsServiceImplementation(ObjectsCrud objectsCrud,
        ObjectsConverter objectsConverter, CheckFields checkFields) {
        this.objectsCrud = objectsCrud;
        this.objectsConverter = objectsConverter;
		this.checkFields=checkFields;

    }

    @Value("${spring.application.name}")
    public void setConfigurableMessage(String superApp) {
        this.superApp = superApp;
    }

    @Override
    @Transactional
    public ObjectBoundary createObject(ObjectBoundary object) {
        if (object.getCreatedBy().getUserId().getEmail() == null || object.getAlias() == null || object.getType() == null) {
            throw new RuntimeException("Missing mandatory fields");
        }
        object.setObjectId(new ObjectId(superApp, UUID.randomUUID().toString()));
        if (object.getActive() == null) {
            object.setActive(true);
        }
        object.setCreationTimestamp(new Date());

        object.getCreatedBy().getUserId().setSuperapp(superApp);
       
        if(object.getLocation().getLat() == 0) 
        	object.getLocation().setLat(99); //defined as a default value 
        if(object.getLocation().getLng() == 0) 
        	object.getLocation().setLng(99); //defined as a default value 
        SuperAppObjectEntity entity = objectsConverter.toEntity(object);
        entity = objectsCrud.save(entity);
        
        return objectsConverter.toBoundary(entity);
    }

    @Override
    @Transactional
    public void updateObject(String objectSuperApp, String internalObjectId, ObjectBoundary update) {
        SuperAppObjectEntity entity = objectsConverter.toEntity(getObjectBoundaryById(objectSuperApp + '#' + internalObjectId));

        if (update.getType() != null) {
            entity.setType(update.getType());
        }

        if (update.getAlias() != null) {
            entity.setAlias(update.getAlias());
        }

        if (update.getActive() != null) {
            entity.setActive(update.getActive());
        }

        if (update.getLocation() != null) {
            if (update.getLocation().getLat() != 0.0) {
                entity.setLat(update.getLocation().getLat());
            }
            if (update.getLocation().getLng() != 0.0) {
                entity.setLng(update.getLocation().getLng());
            }
        }
 

        objectsCrud.save(entity);
    }

    @Override
    @Transactional
    public ObjectBoundary getObject(String superApp, String internalObjectId) {
    		
    	return getObjectBoundaryById(superApp+'#'+internalObjectId);
    }
    
    @Override 
    @Transactional
    public List<ObjectBoundary> getAllObjects() {
    	List<SuperAppObjectEntity> entities = (List<SuperAppObjectEntity>) this.objectsCrud.findAll();
    	List<ObjectBoundary> list_objects = new ArrayList<>();
    	for(SuperAppObjectEntity entity: entities) { 
    		list_objects.add(this.objectsConverter.toBoundary(entity));
    	}
    	return list_objects;
    }
    
    

	@Override
	@Transactional 
	public void deleteAllObjects() {
		objectsCrud.deleteAll();
	}
	 
    
    public ObjectBoundary getObjectBoundaryById(String id) {
    	Optional<SuperAppObjectEntity> opt = this.objectsCrud.findById(id);
    		
    		if (opt.isPresent()) {
    			SuperAppObjectEntity entity = opt.get();
    			return objectsConverter.toBoundary(entity);
    		}else {
    			throw new RuntimeException("Could not find the object: " + id);
    		}}
    
    
    //add for the sprint 3 
    
    
	@Override
	public List<ObjectBoundary> getAllObjects(String userSuperapp, String userEmail, int size, int page) {
		if(checkFields.getUserRole(userSuperapp, userEmail) == RoleEnum.SUPERAPP_USER)
			return this.objectsCrud.findAll(PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConverter::toBoundary).collect(Collectors.toList());
		else if(checkFields.getUserRole(userSuperapp, userEmail) == RoleEnum.MINIAPP_USER) {
			return this.objectsCrud.findAllByActiveTrue(PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConverter::toBoundary).collect(Collectors.toList());
		}
		else
			throw new BadRequestException("Only superapp user can get all objects");
	}
	
    
    
    @Override
	public List<ObjectBoundary> getObjectsByType(String userSuperapp, String userEmail, String type, int size,
			int page) {
		RoleEnum userRole = checkFields.getUserRole(userSuperapp, userEmail);
	    if (userRole == RoleEnum.SUPERAPP_USER || userRole == RoleEnum.ADMIN  ) {
	        return objectsCrud
	                .findAllByType(type, PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
	                .stream()
	                .map(this.objectsConverter::toBoundary)
	                .collect(Collectors.toList());
	    } else if (userRole == RoleEnum.MINIAPP_USER) {
	        return objectsCrud
//	                .findAllByTypeAndActiveTrue(type, PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
	                .findAllByType(type, PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
	                .stream()
	                .map(this.objectsConverter::toBoundary)
	                .collect(Collectors.toList());
	        }
//	    } else if (userRole == RoleEnum.ADMIN) {
//	        throw new BadRequestException("Only superapp user can get objects by type");
//    }

	    return new ArrayList<>(); // Return an empty list 
	}
    
    @Override
	public List<ObjectBoundary> getObjectsByAlias(String userSuperapp, String userEmail, String alias, int size,
			int page) {
		RoleEnum userRole = checkFields.getUserRole(userSuperapp, userEmail);
	    if (userRole == RoleEnum.SUPERAPP_USER) {
	        return objectsCrud
					.findAllByAlias(alias, PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
	                .stream()
	                .map(this.objectsConverter::toBoundary)
	                .collect(Collectors.toList());
	    } else if (userRole == RoleEnum.MINIAPP_USER) {
	        return objectsCrud
					.findAllByAliasAndActiveTrue(alias, PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
	                .stream()
	                .map(this.objectsConverter::toBoundary)
	                .collect(Collectors.toList());
	    } else if (userRole == RoleEnum.ADMIN) {
	        throw new BadRequestException("Only superapp user can get objects by type");
	    }

	    return new ArrayList<>(); // Return an empty list 
	}
    
    
    @Override
    public List<ObjectBoundary> getObjectsByAliasPattern(String userSuperapp, String userEmail, String pattern, int size, int page) {
        RoleEnum userRole = checkFields.getUserRole(userSuperapp, userEmail);
        String likePattern = convertToLikePattern(pattern);

        if (userRole == RoleEnum.SUPERAPP_USER) {
            return objectsCrud
                    .findAllByAliasPattern(likePattern, PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
                    .stream()
                    .map(this.objectsConverter::toBoundary)
                    .collect(Collectors.toList());
        } else if (userRole == RoleEnum.MINIAPP_USER) {
            return objectsCrud
                    .findAllByAliasPattern(pattern, PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
                    .stream()
                    .map(this.objectsConverter::toBoundary)
                    .collect(Collectors.toList());
        } else if (userRole == RoleEnum.ADMIN) {
            throw new BadRequestException("Only superapp user can get objects by alias pattern");
        }

        return new ArrayList<>(); // Return an empty list
    }

    @Override
	public List<ObjectBoundary> getObjectsByLocation(String userSuperapp, String userEmail, double lat,
			double lng, double distance, int size, int page) {
		
		RoleEnum userRole = checkFields.getUserRole(userSuperapp, userEmail);
	    if (userRole == RoleEnum.SUPERAPP_USER) {
	        return objectsCrud
	        		.findAllByLatBetweenAndLngBetween(lat - distance, lat + distance, lng - distance, lng + distance,
							PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConverter::toBoundary).collect(Collectors.toList());
	    } else if (userRole == RoleEnum.MINIAPP_USER) {
	        return objectsCrud
	    			.findAllByLatBetweenAndLngBetweenAndActive(lat - distance, lat + distance, lng - distance, lng + distance,
							true,PageRequest.of(page, size, Direction.ASC, "creationTimestamp", "objectId"))
					.stream().map(this.objectsConverter::toBoundary).collect(Collectors.toList());
	    } else if (userRole == RoleEnum.ADMIN) {
	        throw new BadRequestException("Only superapp user can get objects by type");
	    }

	    return new ArrayList<>(); // Return an empty list 
	
	}
    
 
    //we must add circle location  (bonus)
    @Override
    public List<ObjectBoundary> getObjectsByCircleLocation(String userSuperapp, String userEmail, double lat,double lng, double distance, int size, int page){
		if(checkFields.getUserRole(userSuperapp, userEmail) != RoleEnum.SUPERAPP_USER && checkFields.getUserRole(userSuperapp, userEmail) != RoleEnum.MINIAPP_USER)
			throw new BadRequestException("Only superapp user can get objects by circle location");
		
		List<SuperAppObjectEntity> list = objectsCrud.findAll();
		List<ObjectBoundary> rv = new ArrayList<>();
		list.sort(Comparator.comparing(SuperAppObjectEntity::getCreationTimestamp).thenComparing(SuperAppObjectEntity::getObjectId)); // Sort the list by creationTimestamp and objectId
		if(checkFields.getUserRole(userSuperapp, userEmail) == RoleEnum.SUPERAPP_USER) {
			for (SuperAppObjectEntity superAppObjectEntity : list) {
				if (CheckFields.checkIfInTheCircle(lat, lng, superAppObjectEntity.getLat(), superAppObjectEntity.getLng(),
						distance)) {
					rv.add(objectsConverter.toBoundary(superAppObjectEntity));
				}
			}
		}
		else {
			for (SuperAppObjectEntity superAppObjectEntity : list) {
				if (CheckFields.checkIfInTheCircle(lat, lng, superAppObjectEntity.getLat(), superAppObjectEntity.getLng(),distance) && superAppObjectEntity.isActive()) {
					rv.add(objectsConverter.toBoundary(superAppObjectEntity));
				}
			}
		}

		int startIndex = Math.max(0, size * page); // Calculate the start index based on page and size
		int endIndex = Math.min(startIndex + size, rv.size()); // Calculate the end index
		endIndex = Math.min(endIndex + 1, rv.size()); // Adjust the end index to include the element at endIndex

		if(startIndex > endIndex) // Want to get index after end
			return new ArrayList<>();
		
		return rv.subList(startIndex, endIndex);
    }
    
    
    @Override
    public void deleteAllObjects(String userSuperapp, String userEmail) {
    	if(checkFields.getUserRole(userSuperapp, userEmail) != RoleEnum.ADMIN)
    		throw new BadRequestException("only the admin can delete the objects");
    	objectsCrud.deleteAll();
    }
    
    
    public String convertToLikePattern(String pattern) {
        StringBuilder likePattern = new StringBuilder("%");
        for (char ch : pattern.toCharArray()) {
            likePattern.append(ch).append('%');
        }
        return likePattern.toString();
    }
    
    
    
    @Override
	public ObjectBoundary getSpecificObject(String objectSuperApp, String internalObjectId, String userSuperapp,
			String userEmail) {
		if(checkFields.getUserRole(userSuperapp, userEmail) == RoleEnum.SUPERAPP_USER)
			return getObjectBoundaryById(objectSuperApp + '#' + internalObjectId);
		else if(checkFields.getUserRole(userSuperapp, userEmail) == RoleEnum.MINIAPP_USER) {
			if(getObjectBoundaryById(objectSuperApp + '#' + internalObjectId).getActive())
				return getObjectBoundaryById(objectSuperApp + '#' + internalObjectId);
			else throw new BadRequestException("Miniapp user can get only active object");
		}
		throw new BadRequestException("Only superapp user and miniapp user can get specific object");
	}

    @Override
	public ObjectBoundary updateObject(String objectSuperApp, String internalObjectId, ObjectBoundary update,
			String userSuperapp, String userEmail) {
		if(checkFields.getUserRole(userSuperapp, userEmail) != RoleEnum.SUPERAPP_USER)
			throw new BadRequestException("Only superapp user can update object");
		
		SuperAppObjectEntity entity = objectsConverter
				.toEntity(getObjectBoundaryById(objectSuperApp + '#' + internalObjectId));
		
		// ObjectId
		if (update.getObjectId() != null) {
			if (update.getObjectId().getSuperapp() != null) {

			}
			if (update.getObjectId().getInternalObjectId() != null) {

			}
		}

		// Type
		if (update.getType() != null || !update.getType().isBlank()) {
			entity.setType(update.getType());
		}

		// Alias
		if (update.getAlias() != null || !update.getAlias().isBlank()) {
			entity.setAlias(update.getAlias());
		}

		// Active
		if (update.getActive() != null) {
			entity.setActive(update.getActive());
		}

		// CreationTimestamp
		if (update.getCreationTimestamp() != null) {

		}

		// CreatedBy
		if (update.getCreatedBy() != null) {

		}

		// Location
		if (update.getLocation() != null) {
			if (update.getLocation().getLat() != 0.0) {
				entity.setLat(update.getLocation().getLat());
			}
			if (update.getLocation().getLng() != 0.0) {
				entity.setLng(update.getLocation().getLng());
			}
		}
 

		entity = objectsCrud.save(entity);
		return objectsConverter.toBoundary(entity);
	}



}