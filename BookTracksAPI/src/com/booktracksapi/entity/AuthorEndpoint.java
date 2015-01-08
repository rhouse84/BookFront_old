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

@Api(name = "authorendpoint", namespace = @ApiNamespace(ownerDomain = "booktracksapi.com", ownerName = "booktracksapi.com", packagePath = "entity"))
public class AuthorEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listAuthor")
	public CollectionResponse<Author> listAuthor(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Author> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Author.class);
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

			execute = (List<Author>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Author obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Author> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getAuthor")
	public Author getAuthor(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		Author author = null;
		try {
			author = mgr.getObjectById(Author.class, id);
		} finally {
			mgr.close();
		}
		return author;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param author the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertAuthor",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public Author insertAuthor(Author author, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (author.getId() != null) {
				if (containsAuthor(author)) {
					throw new EntityExistsException("Object already exists");
				}
			}
			mgr.makePersistent(author);
		} finally {
			mgr.close();
		}
		return author;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param author the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateAuthor",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public Author updateAuthor(Author author, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsAuthor(author)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(author);
		} finally {
			mgr.close();
		}
		return author;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeAuthor",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public void removeAuthor(@Named("id") Long id, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			Author author = mgr.getObjectById(Author.class, id);
			mgr.deletePersistent(author);
		} finally {
			mgr.close();
		}
	}

	private boolean containsAuthor(Author author) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Author.class, author.getId());
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
