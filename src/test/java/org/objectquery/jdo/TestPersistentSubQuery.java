package org.objectquery.jdo;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectquery.BaseSelectQuery;
import org.objectquery.SelectQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.generic.ObjectQueryException;
import org.objectquery.generic.ProjectionType;
import org.objectquery.jdo.domain.Dog;
import org.objectquery.jdo.domain.Person;

public class TestPersistentSubQuery {

	private PersistenceManager peristenceManager;

	@Before
	public void beforeTest() {
		peristenceManager = JDOTestHelper.getFactory().getPersistenceManager();
		peristenceManager.currentTransaction().begin();
	}

	@Test(expected = ObjectQueryException.class)
	@SuppressWarnings("unchecked")
	public void testSubquerySimple() {
		SelectQuery<Person> query = new GenericSelectQuery<Person, Object>(Person.class);

		BaseSelectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getName(), "tomdud");
		query.eq(query.target().getDud(), subQuery);

		List<Person> res = (List<Person>) JDOObjectQuery.execute(query, peristenceManager);
		assertEquals(1, res.size());
		assertEquals(res.get(0).getName(), "tom");
	}

	@Test(expected = ObjectQueryException.class)
	@SuppressWarnings("unchecked")
	public void testBackReferenceSubquery() {
		GenericSelectQuery<Person, Object> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = query.target();
		BaseSelectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getDog().getName(), target.getDog().getName());
		subQuery.notEq(subQuery.target(), target);
		query.eq(query.target().getDud(), subQuery);

		List<Person> res = (List<Person>) JDOObjectQuery.execute(query, peristenceManager);
		assertEquals(1, res.size());
		assertEquals(res.get(0).getName(), "tom");
	}

	@Test(expected = ObjectQueryException.class)
	@SuppressWarnings("unchecked")
	public void testDoubleSubQuery() {

		GenericSelectQuery<Person, Object> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = query.target();
		BaseSelectQuery<Person> subQuery = query.subQuery(Person.class);
		query.eq(target.getDud(), subQuery);
		subQuery.eq(subQuery.target().getDog().getName(), target.getDog().getName());
		BaseSelectQuery<Dog> doubSubQuery = subQuery.subQuery(Dog.class);
		subQuery.eq(subQuery.target().getDog(), doubSubQuery);

		doubSubQuery.notEq(doubSubQuery.target().getOwner(), subQuery.target());
		doubSubQuery.notEq(doubSubQuery.target().getOwner(), query.target().getMom());

		List<Person> res = (List<Person>) JDOObjectQuery.execute(query, peristenceManager);
		assertEquals(1, res.size());
		assertEquals(res.get(0).getName(), "tom");

	}

	@Test(expected = ObjectQueryException.class)
	@SuppressWarnings("unchecked")
	public void testMultipleReferenceSubquery() {
		GenericSelectQuery<Person, Object> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = query.target();
		BaseSelectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getName(), "tomdud");
		BaseSelectQuery<Person> subQuery1 = query.subQuery(Person.class);
		subQuery1.eq(subQuery1.target().getName(), "tommum");
		query.eq(target.getDud(), subQuery);
		query.eq(target.getMom(), subQuery1);

		List<Person> res = (List<Person>) JDOObjectQuery.execute(query, peristenceManager);
		assertEquals(1, res.size());
		assertEquals(res.get(0).getName(), "tom");

	}

	@SuppressWarnings("unchecked")
	@Test(expected = ObjectQueryException.class)
	public void testProjectionSubquery() {
		GenericSelectQuery<Person, Object> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = query.target();
		BaseSelectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getDog().getOwner(), target.getDud());
		query.prj(subQuery);

		List<Person> res = (List<Person>) JDOObjectQuery.execute(query, peristenceManager);
		assertEquals(3, res.size());
		assertEquals(res.get(0), null);
		assertEquals(res.get(1), null);
		assertEquals(res.get(1), null);

	}

	@Test(expected = ObjectQueryException.class)
	public void testOrderSubquery() {
		GenericSelectQuery<Person, Object> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = query.target();
		BaseSelectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getDog().getOwner(), target.getDud());
		query.order(subQuery);

		JDOObjectQuery.execute(query, peristenceManager);
	}

	@Test(expected = ObjectQueryException.class)
	public void testHavingSubquery() {
		GenericSelectQuery<Person, Object> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = query.target();
		BaseSelectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getDog().getOwner(), target.getDud());
		query.having(subQuery, ProjectionType.COUNT).eq(3D);

		JDOObjectQuery.execute(query, peristenceManager);
	}

	@After
	public void afterTest() {
		if (peristenceManager != null) {
			peristenceManager.currentTransaction().commit();
			peristenceManager.close();
		}
		peristenceManager = null;
	}

}