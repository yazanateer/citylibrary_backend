package RESTfulApi.mini_app;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;

import RESTfulApi.BadRequestException;
import RESTfulApi.API.SuperAppObjectAPIController;
import RESTfulApi.entity.MiniAppCommandEntity;
import RESTfulApi.entity.SuperAppObjectEntity;
import RESTfulApi.object.CheckFields;
import RESTfulApi.object.CreatedBy;
import RESTfulApi.object.ObjectBoundary;
import RESTfulApi.object.ObjectsServiceImplementation;
import RESTfulApi.user.RoleEnum;
import RESTfulApi.user.UserBoundary;
import RESTfulApi.user.UserId;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class MiniAppCommandServiceImplementaion implements AllMiniAppServices {
	private MiniAppCommandCrud miniAppCommandCrud;
	private MiniAppCommandConverter converter;
	private String nameFromSpring;

	@Autowired
	public MiniAppCommandServiceImplementaion(MiniAppCommandCrud miniAppCommandCrud,
			MiniAppCommandConverter converter) {
		this.miniAppCommandCrud = miniAppCommandCrud;
		this.converter = converter;
	}

	@Value("${spring.application.name}")
	public void setConfigurableMessage(String nameFromSpring) {
		this.nameFromSpring = nameFromSpring;
	}

	@Override
	@Transactional
	public Object InvokeCommand(MiniAppCommandBoundary commandBoundary) {
		if (commandBoundary.getCommand() == null || commandBoundary.getInvokedBy().getEmail() == null
				|| commandBoundary.getTargetObject().getInternalObjectId() == null) {
			throw new BadRequestException("Invalid command data provided");
		}

		commandBoundary.setInvocationTimestamp(new Date());
		commandBoundary.getCommandId().setSuperapp(nameFromSpring);
		commandBoundary.getCommandId().setId(UUID.randomUUID().toString());

		commandBoundary.getInvokedBy().setSuperapp(nameFromSpring);
		commandBoundary.getTargetObject().setSuperapp(nameFromSpring);

		// Route command based on the mini app type
		switch (commandBoundary.getCommandId().getMiniapp().toLowerCase()) {
		case "patron_miniapp":
			return handleBookSearchApp(commandBoundary);
		case "librarian_miniapp":
			return handleLibrarianApp(commandBoundary);
		case "rooms_miniapp":
		        return handleRoomApp(commandBoundary);
		default:
			throw new BadRequestException("Invalid miniapp type provided");
		}
	}

	private Object handleBookSearchApp(MiniAppCommandBoundary commandBoundary) {
		Map<String, Object> attributes = commandBoundary.getCommandAttributes();
		String userEmail = commandBoundary.getInvokedBy().getEmail();
		System.err.println(userEmail);
		switch (commandBoundary.getCommand().toLowerCase()) {
		case "searchbooks":
			return searchBooks(attributes);
		case "borrowbook":
			return handleBook(attributes, userEmail, "borrow");
		default:
			throw new BadRequestException("Invalid command for BookSearchApp");
		}
	}

	private Object handleLibrarianApp(MiniAppCommandBoundary commandBoundary) {
		Map<String, Object> attributes = commandBoundary.getCommandAttributes();
		// String userEmail = commandBoundary.getInvokedBy().getEmail();
		switch (commandBoundary.getCommand().toLowerCase()) {
		case "searchbooks":
			return searchBooks(attributes);
		case "addbook":
			return addBook(attributes, commandBoundary.getInvokedBy().getEmail());
		case "removebook":
			return handleBook(attributes, commandBoundary.getInvokedBy().getEmail(),
					"remove");
		case "trackstatus":
			return trackStatusBook(commandBoundary.getInvokedBy().getEmail());
		default:
			throw new BadRequestException("Invalid command for LibrarianApp");
		}
	}
	
	private Object handleRoomApp(MiniAppCommandBoundary commandBoundary) {
	    Map<String, Object> attributes = commandBoundary.getCommandAttributes();
	    String userEmail = commandBoundary.getInvokedBy().getEmail();
	    switch (commandBoundary.getCommand().toLowerCase()) {
	    case "reserveroom":
	        return handleRoomReservation(attributes, userEmail, "reserve");
	    case "cancelreservation":
	        return handleRoomReservation(attributes, userEmail, "cancel");
	    default:
	        throw new BadRequestException("Invalid command for RoomApp");
	    }
	}
	
	
	private Object trackStatusBook(String userEmail) {
		 return "track status books";
	}
	
	
	

	@Transactional
	public ObjectBoundary handleRoomReservation(Map<String, Object> parameters, String userEmail, String operationType) {
	    RestTemplate restTemplate = new RestTemplate();
	    String updateUserUrl = "http://localhost:8084/superapp/users/" + "citylibrary" + "/" + userEmail;
	    System.err.println(userEmail);
	    // Prepare the updated user object with SUPERAPP_USER role
	    UserBoundary updatedUser = new UserBoundary();
	    updatedUser.setRole(RoleEnum.SUPERAPP_USER);
	    restTemplate.put(updateUserUrl, updatedUser);

	    int roomId =  (int) parameters.get("roomId");
	    System.err.println(roomId);
	    if (roomId == 0) {
	        throw new BadRequestException("Room ID is required.");
	    }

	    // Construct the URL to fetch the room by ID
	    String fetchUrl = "http://localhost:8084/superapp/objects/search/byAlias/" + roomId + "?userSuperapp="
	            + "citylibrary" + "&userEmail=" + userEmail + "&size=10&page=0";

	    try {
	        // Fetch the rooms using the existing GET API
	        ResponseEntity<ObjectBoundary[]> response = restTemplate.getForEntity(fetchUrl, ObjectBoundary[].class);
	        if (response.getBody() == null || response.getBody().length == 0) {
	            throw new BadRequestException("No room found with the given ID.");
	        }

	        List<ObjectBoundary> rooms = Arrays.asList(response.getBody());

	        ObjectBoundary room = null;
	        if (operationType.equalsIgnoreCase("reserve")) {
	            // Filter active rooms
	            List<ObjectBoundary> availableRooms = rooms.stream()
	                    .filter(ObjectBoundary::getActive)
	                    .collect(Collectors.toList());

	            if (availableRooms.isEmpty()) {
	                throw new BadRequestException("No active room found with the given ID.");
	            }

	            room = availableRooms.get(0); // Choose the first available room

	            if (!room.getActive()) {
	                throw new BadRequestException("The room is not available for reservation.");
	            }

	            // Reserve the room
	            room.setActive(false);
	            room.setType("Reserved_room");
	        } else if (operationType.equalsIgnoreCase("cancel")) {
	            // Filter reserved rooms
	            List<ObjectBoundary> reservedRooms = rooms.stream()
	                    .filter(r -> "Reserved_room".equals(r.getType()))
	                    .collect(Collectors.toList());

	            if (reservedRooms.isEmpty()) {
	                throw new BadRequestException("The room is not currently reserved.");
	            }

	            room = reservedRooms.get(0); // Choose the first reserved room

	            if (!"Reserved_room".equals(room.getType())) {
	                throw new BadRequestException("The room is not currently reserved.");
	            }

	            // Cancel the reservation
	            room.setActive(true);
	            room.setType("Room");
	        } else {
	            throw new BadRequestException("Unsupported operation type: " + operationType);
	        }

	        // Construct the URL to update the room
	        String updateUrl = "http://localhost:8084/superapp/objects/citylibrary/"
	                + room.getObjectId().getInternalObjectId() + "?userSuperapp=" + "citylibrary" + "&userEmail="
	                + userEmail;

	        // Sending a PUT request to update the room
	        restTemplate.put(updateUrl, room, ObjectBoundary.class);

	        // Update user role back to MINIAPP_USER
	        updatedUser.setRole(RoleEnum.SUPERAPP_USER);
	        restTemplate.put(updateUserUrl, updatedUser);

	        return room;

	    } catch (HttpClientErrorException ex) {
	        throw new BadRequestException("Error processing your request: " + ex.getResponseBodyAsString());
	    }
	}

	@Transactional
	private Object handleBook(Map<String, Object> parameters, String userEmail, String operationType) {
		RestTemplate restTemplate = new RestTemplate();
		String updateUserUrl = "http://localhost:8084/superapp/users/" + "citylibrary" + "/" + userEmail;
		
		
		
		// Prepare the updated user object with SUPERAPP_USER role
		UserBoundary updatedUser = new UserBoundary();
		updatedUser.setRole(RoleEnum.SUPERAPP_USER);
		restTemplate.put(updateUserUrl, updatedUser);

		String title = (String) parameters.get("title");

		if (title == null || title.isEmpty()) {
			throw new BadRequestException("Alias is required for " + operationType + " a book.");
		}

		// Construct the URL to fetch the books by alias
		String fetchUrl = "http://localhost:8084/superapp/objects/search/byAlias/" + title + "?userSuperapp="
				+ "citylibrary" + "&userEmail=" + userEmail + "&size=10&page=0";

		try {
			// Fetch the books using the existing GET API
			ResponseEntity<ObjectBoundary[]> response = restTemplate.getForEntity(fetchUrl, ObjectBoundary[].class);
			if (response.getBody() == null || response.getBody().length == 0) {
				throw new BadRequestException("No book found with the given alias.");
			}

			// Filter active books
			List<ObjectBoundary> availableBooks = Arrays.stream(response.getBody()).filter(ObjectBoundary::getActive)
					.collect(Collectors.toList());

			if (availableBooks.isEmpty()) {
				throw new BadRequestException("No active book found with the given alias.");
			}

			// Handle the first available book based on operation type
			ObjectBoundary book = availableBooks.get(0);
			book.setActive(false);
			book.getObjectDetails().put("borrowedBy", userEmail);  // Record the user who borrowed the book

			switch (operationType.toLowerCase()) {
			case "borrow":
				
				book.setType("borrowed_book");
				System.err.println(book.getObjectDetails());
				break;
			case "remove":
				book.setType("removed_book");
				break;
			default:
				throw new BadRequestException("Unsupported operation type");
			}

			// Construct the URL to update the book
			String updateUrl = "http://localhost:8084/superapp/objects/citylibrary/"
					+ book.getObjectId().getInternalObjectId() + "?userSuperapp=" + "citylibrary" + "&userEmail="
					+ userEmail;

			// Sending a PUT request to update the book
			restTemplate.put(updateUrl, book, ObjectBoundary.class);

			// Update user role back to MINIAPP_USER
			if(operationType.toLowerCase() == "borrow") {
			updatedUser.setRole(RoleEnum.MINIAPP_USER);
			}else if(operationType.toLowerCase() == "remove"){
				updatedUser.setRole(RoleEnum.ADMIN);
			}
			restTemplate.put(updateUserUrl, updatedUser);

			return "Book successfully " + (operationType.equalsIgnoreCase("borrow") ? "borrowed" : "removed");

		} catch (HttpClientErrorException ex) {
			throw new BadRequestException("Error processing your request: " + ex.getResponseBodyAsString());
		}
	}

	private List<Map<String, Object>> searchBooks(Map<String, Object> parameters) {
		String title = (String) parameters.get("title");
		String author = (String) parameters.get("author");
		String genre = (String) parameters.get("genre");

		// Ensure at least one search parameter is provided
		if ((title == null || title.isEmpty()) && (author == null || author.isEmpty())
				&& (genre == null || genre.isEmpty())) {
			throw new BadRequestException("At least one search parameter must be provideddddd");
		}

		StringBuilder query = new StringBuilder();
		if (title != null && !title.isEmpty())
			query.append("intitle:").append(title);
		if (author != null && !author.isEmpty())
			query.append(query.length() > 0 ? "+" : "").append("inauthor:").append(author);
		if (genre != null && !genre.isEmpty())
			query.append(query.length() > 0 ? "+" : "").append("subject:").append(genre);

		String url = "https://www.googleapis.com/books/v1/volumes?q=" + query.toString() + "&maxResults=10";
		RestTemplate restTemplate = new RestTemplate();
		Map<String, Object> response = restTemplate.getForObject(url, Map.class);

		if (response == null || !response.containsKey("items")) {
			return new ArrayList<>();
		}

		List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");
		if (items != null) {
			return items;
		} else {
			return new ArrayList<>();
		}
	}

	private String limit_description(String description) {
		if (description == null) {
			return "No description available";
		}
		String[] words = description.split(" ");
		if (words.length > 7) {
			return String.join(" ", Arrays.copyOfRange(words, 0, 7)) + "...";
		} else {
			return description;
		}
	}

	private String truncate(String str, int maxLength) {
		return str.length() > maxLength ? str.substring(0, maxLength) : str;
	}

	private Object addBook(Map<String, Object> parameters, String userEmail) {
		// Extracting parameters with safety checks

		Map<String, Object> objectDetails = (Map<String, Object>) parameters.get("objectDetails");
		String title = (String) objectDetails.get("title");
		List<String> authors = (List<String>) parameters.get("authors");
		List<String> categories = (List<String>) parameters.get("categories");
		String description = (String) parameters.get("description");

		// Ensure all required parameters are provided
		if (title == null || title.isEmpty() || userEmail == null || userEmail.isEmpty()) {
			throw new BadRequestException("Missing required book or user details");
		}
		// Prepare the object boundary
		Map<String, Object> objectBoundary = new HashMap<>();
		objectBoundary.put("objectId", Map.of("superapp", userEmail, "internalObjectId", "yazan"));
		objectBoundary.put("type", "Book");
		objectBoundary.put("alias", truncate(title, 100));
		objectBoundary.put("location", Map.of("lat", 0.0, "lng", 0.0));
		objectBoundary.put("active", true);
		objectBoundary.put("createdBy", Map.of("userId", Map.of("superapp", "Superapp", "email", userEmail)));

		// Preparing objectDetails with a check for nulls in authors and categories
		Map<String, Object> objectDetailss = new HashMap<>();
		objectDetailss.put("title", title);
		objectDetailss.put("authors", authors != null ? authors : List.of());
		objectDetailss.put("categories", categories != null ? categories : List.of());
		objectDetailss.put("description", limit_description(description));

		objectBoundary.put("objectDetails", objectDetails);

		// Invoke API or save the objectBoundary to your system
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = "http://localhost:8084/superapp/objects";
			restTemplate.postForObject(url, objectBoundary, Map.class);
			return "Book added successfully";
		} catch (Exception e) {
			throw new BadRequestException("Error adding book: " + e.getMessage());
		}

	}

	@Override
	@Transactional
	public void deleteAllCommands() {
		miniAppCommandCrud.deleteAll();
	}

	@Override
	@Transactional
	public List<MiniAppCommandBoundary> getAllCommands() {
		List<MiniAppCommandEntity> entities = this.miniAppCommandCrud.findAll();
		List<MiniAppCommandBoundary> boundaries = new ArrayList<>();
		for (MiniAppCommandEntity me : entities) {
			boundaries.add(this.converter.toBoundary(me));
		}
		return boundaries;
	}

	@Override
	@Transactional
	public List<MiniAppCommandBoundary> getSpecificCommands(String miniAppName) {
		List<MiniAppCommandBoundary> boundaries = getAllCommands();
		List<MiniAppCommandBoundary> specific_commands = new ArrayList<>();

		for (MiniAppCommandBoundary miniapp : boundaries) {
			if (miniapp.getCommandId().getMiniapp().equalsIgnoreCase(miniAppName))
				specific_commands.add(miniapp);
		}

		return specific_commands;

	}

	// add for the sprint 3

	// Get all commands new
	@Override
	public List<MiniAppCommandBoundary> getAllCommands(String userSuperapp, String userEmail, int size, int page) {
		if (CheckFields.getUserRole(userSuperapp, userEmail) != RoleEnum.ADMIN)
			throw new BadRequestException("Only admin can get all commands");
		return this.miniAppCommandCrud
				.findAll(PageRequest.of(page, size, Direction.ASC, "command", "invokationTimeStamp", "commandId"))
				.stream() // Stream<CommandEntity>
				.map(this.converter::toBoundary) // Stream<CommandBoundary>
				.toList(); // List<CommandBoundary>
	}

	// Get all mini app commands new
	@Override
	public List<MiniAppCommandBoundary> getSpecificMiniAppCommands(String miniAppName, String userSuperapp,
			String userEmail, int size, int page) {
		if (CheckFields.getUserRole(userSuperapp, userEmail) != RoleEnum.ADMIN)
			throw new BadRequestException("Only admin can get all commands");
		return this.miniAppCommandCrud
				.findAllByMiniApp(miniAppName,
						PageRequest.of(page, size, Direction.ASC, "command", "invokationTimeStamp", "commandId"))
				.stream() // Stream<CommandEntity>
				.map(this.converter::toBoundary) // Stream<CommandBoundary>
				.toList(); // List<CommandBoundary>
	}

	// Delete all commands new
	@Override
	public void deleteAllCommands(String userSuperapp, String userEmail) {
		if (CheckFields.getUserRole(userSuperapp, userEmail) != RoleEnum.ADMIN)
			throw new BadRequestException("Only admin can delete all commands");
		miniAppCommandCrud.deleteAll();
	}

}
