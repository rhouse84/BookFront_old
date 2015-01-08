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

@Api(name = "bookendpoint", namespace = @ApiNamespace(ownerDomain = "booktracksapi.com", ownerName = "booktracksapi.com", packagePath = "entity"))
public class BookEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listBook")
	public CollectionResponse<Book> listBook(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		PersistenceManager mgr = null;
		Cursor cursor = null;
		List<Book> execute = null;

		try {
			mgr = getPersistenceManager();
			Query query = mgr.newQuery(Book.class);
			query.setOrdering("readDate desc");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				HashMap<String, Object> extensionMap = new HashMap<String, Object>();
				extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
				query.setExtensions(extensionMap);
			}

			if (limit != null) {
				query.setRange(0, limit);
			}

			execute = (List<Book>) query.execute();
			cursor = JDOCursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Book obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Book> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getBook")
	public Book getBook(@Named("id") Long id) {
		PersistenceManager mgr = getPersistenceManager();
		Book book = null;
		try {
			book = mgr.getObjectById(Book.class, id);
		} finally {
			mgr.close();
		}
		return book;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param book the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertBook",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public Book insertBook(Book book, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (book.getId() != null) {
				if (containsBook(book)) {
					throw new EntityExistsException("Object already exists");
				}
			}
			mgr.makePersistent(book);
		} finally {
			mgr.close();
		}
		return book;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param book the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateBook",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public Book updateBook(Book book, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			if (!containsBook(book)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.makePersistent(book);
		} finally {
			mgr.close();
		}
		return book;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeBook",  scopes = {Constants.EMAIL_SCOPE},
			clientIds = {Constants.WEB_CLIENT_ID, 
		    com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
	
	public void removeBook(@Named("id") Long id, User user) throws UnauthorizedException {
		if (user == null) throw new UnauthorizedException("User is Not Valid");
		
		PersistenceManager mgr = getPersistenceManager();
		try {
			Book book = mgr.getObjectById(Book.class, id);
			mgr.deletePersistent(book);
		} finally {
			mgr.close();
		}
	}

	private boolean containsBook(Book book) {
		PersistenceManager mgr = getPersistenceManager();
		boolean contains = true;
		try {
			mgr.getObjectById(Book.class, book.getId());
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
