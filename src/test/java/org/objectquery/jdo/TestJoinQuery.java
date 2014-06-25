package org.objectquery.jdo;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.objectquery.SelectQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.generic.JoinType;
import org.objectquery.generic.ObjectQueryException;
import org.objectquery.jdo.domain.Person;

public class TestJoinQuery {

	@Test(expected = ObjectQueryException.class)
	public void testSimpleJoin() {
		SelectQuery<Person> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person joined = query.join(Person.class);
		query.eq(query.target().getMom(), joined);

		assertEquals("select A from org.objectquery.jdo.domain.Person A  INNER JOIN  org.objectquery.jdo.domain.Person AB0 where A.mom  ==  AB0",
				JDOObjectQuery.jdoqlGenerator(query).getQuery());
	}

	@Test(expected = ObjectQueryException.class)
	public void testTypedJoin() {
		SelectQuery<Person> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person joined = query.join(Person.class, JoinType.LEFT);
		query.eq(query.target().getMom(), joined);

		assertEquals("select A from org.objectquery.jdo.domain.Person A  LEFT JOIN  org.objectquery.jdo.domain.Person AB0 where A.mom  ==  AB0", JDOObjectQuery
				.jdoqlGenerator(query).getQuery());
	}

	@Test(expected = ObjectQueryException.class)
	public void testTypedPathJoin() {
		SelectQuery<Person> query = new GenericSelectQuery<Person, Object>(Person.class);
		Person joined = query.join(query.target().getMom(), Person.class, JoinType.LEFT);
		query.eq(joined.getName(), "test");

		assertEquals(
				"select A from org.objectquery.jdo.domain.Person A  LEFT JOIN  org.objectquery.jdo.domain.Person AB0 on A.mom == AB0 where AB0.name  ==  param_AB0_name PARAMETERS String param_AB0_name",
				JDOObjectQuery.jdoqlGenerator(query).getQuery());
	}

}
