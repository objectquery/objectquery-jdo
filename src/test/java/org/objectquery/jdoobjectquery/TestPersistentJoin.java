package org.objectquery.jdoobjectquery;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectquery.ObjectQuery;
import org.objectquery.generic.GenericObjectQuery;
import org.objectquery.generic.JoinType;
import org.objectquery.generic.ObjectQueryException;
import org.objectquery.jdoobjectquery.domain.Person;

public class TestPersistentJoin {

	private PersistenceManager persistenceManager;

	@Before
	public void beforeTest() {
		persistenceManager = JDOTestHelper.getFactory().getPersistenceManager();
		persistenceManager.currentTransaction().begin();
	}

	@Test(expected=ObjectQueryException.class)
	@SuppressWarnings("unchecked")
	public void testSimpleJoin() {
		ObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);
		Person joined = query.join(Person.class);
		query.eq(query.target().getMom(), joined);

		List<Person> persons = (List<Person>) JDOObjectQuery.execute(query, persistenceManager);
		Assert.assertEquals(1, persons.size());
	}

	@Test(expected=ObjectQueryException.class)
	@SuppressWarnings("unchecked")
	public void testTypedJoin() {
		ObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);
		Person joined = query.join(Person.class, JoinType.LEFT);
		query.eq(query.target().getMom(), joined);

		List<Person> persons = (List<Person>) JDOObjectQuery.execute(query, persistenceManager);
		Assert.assertEquals(1, persons.size());
	}

	@Test(expected=ObjectQueryException.class)
	@SuppressWarnings("unchecked")
	public void testTypedPathJoin() {
		ObjectQuery<Person> query = new GenericObjectQuery<Person>(Person.class);
		Person joined = query.join(query.target().getMom(), Person.class, JoinType.LEFT);
		query.eq(joined.getName(), "tommum");

		List<Person> persons = (List<Person>) JDOObjectQuery.execute(query, persistenceManager);
		Assert.assertEquals(1, persons.size());
	}

	@After
	public void afterTest() {
		if (persistenceManager != null) {
			persistenceManager.currentTransaction().commit();
			persistenceManager.close();
		}
		persistenceManager = null;
	}
}
