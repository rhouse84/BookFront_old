package com.booktracksapi.entity;

import com.booktracksapi.entity.PMF;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.users.User;
import com.google.appengine.datanucleus.query.JDOCursorHelper;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

@Api(name = "topicendpoint", namespace = @ApiNamespace(ownerDomain = "booktracksapi.com", ownerName = "booktracksapi.com", packagePath = "entity"))
public class TopicEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listTopic")
	public CollectionResponse<Topic> listTopic(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Topic> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Topic.class);
			query.setOrdering("description asc");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<Topic>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Topic obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Topic> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getTopic")
	public Topic getTopic(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		Topic topic = null;
		try {
			topic = mgr.getObjectById(Topic.class, id);
		} finally {
			mgr.close();
		}
		return topic;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param topic the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertTopic",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public Topic insertTopic(Topic topic, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (topic.getId() != null) {
				if (containsTopic(topic)) {
					throw new EntityExistsException("Object already exists");
				}
			}
			mgr.makePersistent(topic);
		} finally {
			mgr.close();
		}
		return topic;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param topic the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateTopic",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public Topic updateTopic(Topic topic, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsTopic(topic)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(topic);
		} finally {
			mgr.close();
		}
		return topic;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeTopic",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public void removeTopic(@Named("id") Long id, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			Topic topic = mgr.getObjectById(Topic.class, id);
			mgr.deletePersistent(topic);
		} finally {
			mgr.close();
		}
	}

	private boolean containsTopic(Topic topic) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Topic.class, topic.getId());
		} catch (javax.jdo.JDOObjectNotFoundException ex) {
			contains = false;
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}

}
