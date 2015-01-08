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

@Api(name = "directorendpoint", namespace = @ApiNamespace(ownerDomain = "booktracksapi.com", ownerName = "booktracksapi.com", packagePath = "entity"))
public class DirectorEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listDirector")
	public CollectionResponse<Director> listDirector(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Director> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Director.class);
			query.setOrdering("namelc asc");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<Director>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Director obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Director> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getDirector")
	public Director getDirector(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		Director director = null;
		try {
			director = mgr.getObjectById(Director.class, id);
		} finally {
			mgr.close();
		}
		return director;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param director the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertDirector",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public Director insertDirector(Director director, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (director.getId() != null) {
				if (containsDirector(director)) {
					throw new EntityExistsException("Object already exists");
				}
			}
			mgr.makePersistent(director);
		} finally {
			mgr.close();
		}
		return director;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param director the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateDirector",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public Director updateDirector(Director director, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsDirector(director)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(director);
		} finally {
			mgr.close();
		}
		return director;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeDirector",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public void removeDirector(@Named("id") Long id, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			Director director = mgr.getObjectById(Director.class, id);
			mgr.deletePersistent(director);
		} finally {
			mgr.close();
		}
	}

	private boolean containsDirector(Director director) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Director.class, director.getId());
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
