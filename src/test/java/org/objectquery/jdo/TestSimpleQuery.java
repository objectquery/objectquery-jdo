package org.objectquery.jdo;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.objectquery.SelectQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.generic.ObjectQueryException;
import org.objectquery.generic.OrderType;
import org.objectquery.generic.ProjectionType;
import org.objectquery.jdo.domain.Home;
import org.objectquery.jdo.domain.Person;

public class TestSimpleQuery {

	@Test
	public void testBaseCondition() {

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getName(), "tom");

		assertEquals("select A from org.objectquery.jdo.domain.Person A where A.name  ==  param_A_name PARAMETERS String param_A_name", JDOObjectQuery
				.jdoqlGenerator(qp).getQuery());

	}

	@Test
	public void testDupliedPath() {

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getName(), "tom");
		qp.eq(target.getName(), "tom3");

		assertEquals(
				"select A from org.objectquery.jdo.domain.Person A where A.name  ==  param_A_name && A.name  ==  param_A_name1 PARAMETERS String param_A_name,String param_A_name1",
				JDOObjectQuery.jdoqlGenerator(qp).getQuery());

	}

	@Test
	public void testDottedPath() {

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getDog().getName(), "tom");
		qp.eq(target.getDud().getName(), "tom3");

		assertEquals(
				"select A from org.objectquery.jdo.domain.Person A where A.dog.name  ==  param_A_dog_name && A.dud.name  ==  param_A_dud_name PARAMETERS String param_A_dog_name,String param_A_dud_name",
				JDOObjectQuery.jdoqlGenerator(qp).getQuery());

	}

	@Test
	public void testProjection() {

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.prj(target.getName());
		qp.eq(target.getDog().getName(), "tom");

		assertEquals("select A.name from org.objectquery.jdo.domain.Person A where A.dog.name  ==  param_A_dog_name PARAMETERS String param_A_dog_name",
				JDOObjectQuery.jdoqlGenerator(qp).getQuery());

	}

	@Test
	public void testProjectionCountThis() {

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.prj(target, ProjectionType.COUNT);
		qp.eq(target.getDog().getName(), "tom");

		assertEquals("select  COUNT(A) from org.objectquery.jdo.domain.Person A where A.dog.name  ==  param_A_dog_name PARAMETERS String param_A_dog_name",
				JDOObjectQuery.jdoqlGenerator(qp).getQuery());

	}

	@Test
	public void testSelectOrder() {

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getDog().getName(), "tom");
		qp.order(target.getName());

		assertEquals(
				"select A from org.objectquery.jdo.domain.Person A where A.dog.name  ==  param_A_dog_name order by A.name PARAMETERS String param_A_dog_name",
				JDOObjectQuery.jdoqlGenerator(qp).getQuery());

	}

	@Test
	public void testOrderAsc() {

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getDog().getName(), "tom");
		qp.order(target.getName(), OrderType.ASC);

		assertEquals(
				"select A from org.objectquery.jdo.domain.Person A where A.dog.name  ==  param_A_dog_name order by A.name ascending PARAMETERS String param_A_dog_name",
				JDOObjectQuery.jdoqlGenerator(qp).getQuery());

	}

	@Test
	public void testOrderDesc() {

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getDog().getName(), "tom");
		qp.order(target.getName(), OrderType.DESC);
		qp.order(target.getDog().getName(), OrderType.DESC);

		assertEquals(
				"select A from org.objectquery.jdo.domain.Person A where A.dog.name  ==  param_A_dog_name order by A.name descending,A.dog.name descending PARAMETERS String param_A_dog_name",
				JDOObjectQuery.jdoqlGenerator(qp).getQuery());

	}

	@Test
	public void testAllSimpleConditions() {

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getName(), "tom");
		qp.like(target.getName(), "tom");
		qp.gt(target.getName(), "tom");
		qp.lt(target.getName(), "tom");
		qp.gtEq(target.getName(), "tom");
		qp.ltEq(target.getName(), "tom");
		qp.notEq(target.getName(), "tom");
		qp.notLike(target.getName(), "tom");
		qp.likeNc(target.getName(), "tom");
		qp.notLikeNc(target.getName(), "tom");

		assertEquals(
				"select A from org.objectquery.jdo.domain.Person A where A.name  ==  param_A_name && A.name.matches(param_A_name1) && A.name  >  param_A_name2 && "
						+ "A.name  <  param_A_name3 && A.name  >=  param_A_name4 && A.name  <=  param_A_name5 && A.name  !=  param_A_name6 && !A.name.matches(param_A_name7) && A.name.toUpperCase().matches(param_A_name8.toUpperCase()) && !A.name.toUpperCase().matches(param_A_name9.toUpperCase()) "
						+ "PARAMETERS String param_A_name,String param_A_name1,String param_A_name2,String param_A_name3,String param_A_name4,String param_A_name5,String param_A_name6,String param_A_name7,String param_A_name8,String param_A_name9",
				JDOObjectQuery.jdoqlGenerator(qp).getQuery());

	}

	@Test
	public void testINCondition() {

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		List<String> pars = new ArrayList<String>();
		qp.in(target.getName(), pars);
		qp.notIn(target.getName(), pars);

		assertEquals(
				"select A from org.objectquery.jdo.domain.Person A where param_A_name.contains(A.name) && !param_A_name1.contains(A.name) PARAMETERS java.util.Collection param_A_name,java.util.Collection param_A_name1",
				JDOObjectQuery.jdoqlGenerator(qp).getQuery());

	}

	@Test
	public void testContainsCondition() {

		GenericSelectQuery<Person, Object> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		Person p = new Person();
		qp.contains(target.getFriends(), p);
		qp.notContains(target.getFriends(), p);

		assertEquals(
				"select A from org.objectquery.jdo.domain.Person A where A.friends.contains(param_A_friends) && !A.friends.contains(param_A_friends1) PARAMETERS Person param_A_friends,Person param_A_friends1",
				JDOObjectQuery.jdoqlGenerator(qp).getQuery());

	}

	@Test
	public void testProjectionGroup() {

		SelectQuery<Home> qp = new GenericSelectQuery<Home, Object>(Home.class);
		Home target = qp.target();
		qp.prj(target.getAddress());
		qp.prj(qp.box(target.getPrice()), ProjectionType.MAX);
		qp.order(target.getAddress());

		assertEquals("select A.address, MAX(A.price) from org.objectquery.jdo.domain.Home A group by A.address order by A.address", JDOObjectQuery
				.jdoqlGenerator(qp).getQuery());

	}

	@Test(expected = ObjectQueryException.class)
	public void testProjectionGroupHaving() {

		SelectQuery<Home> qp = new GenericSelectQuery<Home, Object>(Home.class);
		Home target = qp.target();
		qp.prj(target.getAddress());
		qp.prj(qp.box(target.getPrice()), ProjectionType.MAX);
		qp.order(target.getAddress());
		qp.having(qp.box(target.getPrice()), ProjectionType.MAX).eq(0D);

		assertEquals(
				"select A.address, MAX(A.price) from org.objectquery.jdo.domain.Home A PARAMETERS Double param_A_price group by A.address having MAX(A.price) == param_A_price order by A.address",
				JDOObjectQuery.jdoqlGenerator(qp).getQuery());

	}

	@Test(expected = ObjectQueryException.class)
	public void testBetweenCondition() {
		SelectQuery<Home> qp = new GenericSelectQuery<Home, Object>(Home.class);
		Home target = qp.target();
		qp.between(qp.box(target.getPrice()), 20D, 30D);

		assertEquals("", JDOObjectQuery.jdoqlGenerator(qp).getQuery());

	}
}
