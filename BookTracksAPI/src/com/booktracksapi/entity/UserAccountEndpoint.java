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

@Api(name = "useraccountendpoint", namespace = @ApiNamespace(ownerDomain = "booktracksapi.com", ownerName = "booktracksapi.com", packagePath = "entity"))
public class UserAccountEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listUserAccount")
	public CollectionResponse<UserAccount> listUserAccount(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<UserAccount> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(UserAccount.class);
			query.setOrdering("name asc");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<UserAccount>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (UserAccount obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<UserAccount> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getUserAccount")
	public UserAccount getUserAccount(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		UserAccount useraccount = null;
		try {
			useraccount = mgr.getObjectById(UserAccount.class, id);
		} finally {
			mgr.close();
		}
		return useraccount;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param useraccount the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertUserAccount",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public UserAccount insertUserAccount(UserAccount useraccount, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (useraccount.getId() != null) {
				if (containsUserAccount(useraccount)) {
					throw new EntityExistsException("Object already exists");
				}
			}
			mgr.makePersistent(useraccount);
		} finally {
			mgr.close();
		}
		return useraccount;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param useraccount the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateUserAccount",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public UserAccount updateUserAccount(UserAccount useraccount, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsUserAccount(useraccount)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(useraccount);
		} finally {
			mgr.close();
		}
		return useraccount;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeUserAccount",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public void removeUserAccount(@Named("id") Long id, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			UserAccount useraccount = mgr.getObjectById(UserAccount.class, id);
			mgr.deletePersistent(useraccount);
		} finally {
			mgr.close();
		}
	}

	private boolean containsUserAccount(UserAccount useraccount) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(UserAccount.class, useraccount.getId());
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
