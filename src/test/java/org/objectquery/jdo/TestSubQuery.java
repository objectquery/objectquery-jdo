package org.objectquery.jdo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.objectquery.BaseSelectQuery;
import org.objectquery.SelectQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.generic.ObjectQueryException;
import org.objectquery.jdo.domain.Person;

public class TestSubQuery {

	private static String getQueryString(SelectQuery<Person> query) {
		return JDOObjectQuery.jdoqlGenerator(query).getQuery();
	}

	@Test(expected = ObjectQueryException.class)
	public void testSubquerySimple() {
		SelectQuery<Person> query = new GenericSelectQuery<Person, Object>(Person.class);

		BaseSelectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getName(), "test");
		query.eq(query.target().getDud(), subQuery);

		assertEquals(
				"select A from org.objectquery.jdo.domain.Person A where A.dud  ==  (select AA0 from org.objectquery.jdo.domain.Person AA0 where AA0.name  ==  param_AA0_name) PARAMETERS String param_AA0_name",
				getQueryString(query));

	}

	@Test(expected = ObjectQueryException.class)
	public void testBackReferenceSubquery() {
		GenericSelectQuery<Person, Object> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = query.target();
		BaseSelectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getName(), target.getDog().getName());
		query.eq(query.target().getDud(), subQuery);

		assertEquals(
				"select A from org.objectquery.jdo.domain.Person A where A.dud  ==  (select AA0 from org.objectquery.jdo.domain.Person AA0 where AA0.name  ==  A.dog.name)",
				getQueryString(query));
	}

	@Test(expected = ObjectQueryException.class)
	public void testDoubleSubQuery() {

		GenericSelectQuery<Person, Object> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = query.target();
		BaseSelectQuery<Person> subQuery = query.subQuery(Person.class);
		query.eq(target.getDud(), subQuery);
		subQuery.eq(subQuery.target().getName(), target.getDog().getName());
		BaseSelectQuery<Person> doubSubQuery = subQuery.subQuery(Person.class);
		subQuery.eq(subQuery.target().getMom(), doubSubQuery);

		doubSubQuery.eq(doubSubQuery.target().getMom().getName(), subQuery.target().getMom().getName());
		doubSubQuery.eq(doubSubQuery.target().getMom().getName(), query.target().getMom().getName());

		assertEquals(
				"select A from org.objectquery.jdo.domain.Person A where A.dud  ==  (select AA0 from org.objectquery.jdo.domain.Person AA0 where AA0.name  ==  A.dog.name && AA0.mum  ==  (select AA0A0 from org.objectquery.jdo.domain.Person AA0A0 where AA0A0.mum.name  ==  AA0.mum.name && AA0A0.mum.name  ==  A.mum.name))",
				getQueryString(query));

	}

	@Test(expected = ObjectQueryException.class)
	public void testMultipleReferenceSubquery() {
		GenericSelectQuery<Person, Object> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = query.target();
		BaseSelectQuery<Person> subQuery = query.subQuery(Person.class);
		BaseSelectQuery<Person> subQuery1 = query.subQuery(Person.class);
		query.eq(target.getDud(), subQuery);
		query.eq(target.getMom(), subQuery1);

		assertEquals(
				"select A from org.objectquery.jdo.domain.Person A where A.dud  ==  (select AA0 from org.objectquery.jdo.domain.Person AA0) && A.mum  ==  (select AA1 from org.objectquery.jdo.domain.Person AA1)",
				getQueryString(query));

	}

	@Test(expected = ObjectQueryException.class)
	public void testProjectionSubquery() {
		GenericSelectQuery<Person, Object> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = query.target();
		BaseSelectQuery<Person> subQuery = query.subQuery(Person.class);
		subQuery.eq(subQuery.target().getDog().getOwner(), target.getDud());
		query.prj(subQuery);

		assertEquals("select (select AA0 from org.objectquery.jdo.domain.Person AA0 where AA0.dog.owner  ==  A.dud) from org.objectquery.jdo.domain.Person A",
				getQueryString(query));

	}

}
