package org.objectquery.jdo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectquery.SelectQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.generic.ObjectQueryException;
import org.objectquery.generic.OrderType;
import org.objectquery.generic.ProjectionType;
import org.objectquery.jdo.domain.Home;
import org.objectquery.jdo.domain.Person;

public class TestPersistentSelect {

	private PersistenceManager persitenceManager;

	@Before
	public void beforeTest() {
		persitenceManager = JDOTestHelper.getFactory().getPersistenceManager();
		persitenceManager.currentTransaction().begin();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleSelect() {
		SelectQuery<Person> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getName(), "tom");

		List<Person> res = (List<Person>) (List<Person>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(1, res.size());
		assertEquals(res.get(0).getName(), "tom");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleSelectWithutCond() {
		SelectQuery<Person>  qp = new GenericSelectQuery<Person, Object>(Person.class);
		List<Person> res = (List<Person>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(3, res.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectPathValue() {
		SelectQuery<Person> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getDud().getHome(), target.getMom().getHome());
		List<Person> res = (List<Person>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(1, res.size());
		assertEquals(res.get(0).getDud().getHome(), res.get(0).getMom().getHome());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectPathParam() {
		SelectQuery<Person> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getDud().getName(), "tomdud");
		List<Person> res = (List<Person>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(1, res.size());
		assertEquals(res.get(0).getDud().getName(), "tomdud");
	}

	@Test
	public void testSelectCountThis() {
		SelectQuery<Person> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.prj(target, ProjectionType.COUNT);
		Long res = (Long) JDOObjectQuery.execute(qp, persitenceManager);
		assertNotNull(res);
		assertEquals(new Long(3), res);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectPrjection() {
		SelectQuery<Person> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.prj(target.getName());
		qp.prj(target.getHome());
		qp.eq(target.getName(), "tom");
		List<Object[]> res = (List<Object[]>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(1, res.size());
		assertEquals("tom", res.get(0)[0]);
		assertEquals("homeless", ((Home) res.get(0)[1]).getAddress());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectOrder() {
		SelectQuery<Person> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.prj(target.getName());
		qp.order(target.getName());
		List<Object[]> res = (List<Object[]>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(3, res.size());
		assertEquals("tom", res.get(0));
		assertEquals("tomdud", res.get(1));
		assertEquals("tommum", res.get(2));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectOrderDesc() {
		SelectQuery<Person> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.prj(target.getName());
		qp.order(target.getName(), OrderType.DESC);
		List<Object[]> res = (List<Object[]>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(3, res.size());
		assertEquals("tommum", res.get(0));
		assertEquals("tomdud", res.get(1));
		assertEquals("tom", res.get(2));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectSimpleConditions() {

		SelectQuery<Person> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.eq(target.getName(), "tom");
		qp.like(target.getName(), "tom");
		qp.notLike(target.getName(), "tom");
		qp.gt(target.getName(), "tom");
		qp.lt(target.getName(), "tom");
		qp.gtEq(target.getName(), "tom");
		qp.ltEq(target.getName(), "tom");
		qp.notEq(target.getName(), "tom");
		qp.likeNc(target.getName(), "tom");
		qp.notLikeNc(target.getName(), "tom");
		List<Object[]> res = (List<Object[]>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(0, res.size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectINCondition() {

		SelectQuery<Person> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();

		List<String> pars = new ArrayList<String>();
		pars.add("tommy");
		qp.in(target.getName(), pars);
		qp.notIn(target.getName(), pars);

		List<Object[]> res = (List<Object[]>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(0, res.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectContainsCondition() {

		SelectQuery<Person> qp0 = new GenericSelectQuery<Person, Object>(Person.class);
		Person target0 = qp0.target();
		qp0.eq(target0.getName(), "tom");

		List<Person> res0 = (List<Person>) JDOObjectQuery.execute(qp0, persitenceManager);
		assertEquals(1, res0.size());
		Person p = res0.get(0);

		SelectQuery<Person> qp = new GenericSelectQuery<Person, Object>(Person.class);
		Person target = qp.target();
		qp.contains(target.getFriends(), p);
		qp.notContains(target.getFriends(), p);

		List<Object[]> res = (List<Object[]>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(0, res.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSelectFunctionGrouping() {

		SelectQuery<Home> qp = new GenericSelectQuery<Home, Object>(Home.class);
		Home target = qp.target();
		qp.prj(target.getAddress());
		qp.prj(qp.box(target.getPrice()), ProjectionType.MAX);
		qp.order(target.getAddress());

		List<Object[]> res = (List<Object[]>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(res.size(), 3);
		assertEquals(res.get(0)[1], 0d);
		assertEquals(res.get(1)[1], 0d);
		assertEquals(res.get(2)[1], 1000000d);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ObjectQueryException.class)
	public void testSelectOrderGrouping() {

		SelectQuery<Home> qp = new GenericSelectQuery<Home, Object>(Home.class);
		Home target = qp.target();
		qp.order(qp.box(target.getPrice()), ProjectionType.MAX, OrderType.ASC);

		List<Home> res = (List<Home>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(3, res.size());
		assertEquals(0d, res.get(0).getPrice(), 0);
		assertEquals(0d, res.get(1).getPrice(), 0);
		assertEquals(1000000d, res.get(2).getPrice(), 0);

	}

	@SuppressWarnings("unchecked")
	@Test(expected = ObjectQueryException.class)
	public void testSelectOrderGroupingPrj() {

		SelectQuery<Home> qp = new GenericSelectQuery<Home, Object>(Home.class);
		Home target = qp.target();
		qp.prj(target.getAddress());
		qp.prj(qp.box(target.getPrice()), ProjectionType.MAX);
		qp.order(qp.box(target.getPrice()), ProjectionType.MAX, OrderType.DESC);

		List<Object[]> res = (List<Object[]>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(3, res.size());
		assertEquals((Double) res.get(0)[1], 1000000d, 0);
		assertEquals((Double) res.get(1)[1], 0d, 0);
		assertEquals((Double) res.get(2)[1], 0d, 0);
	}

	@SuppressWarnings("unchecked")
	@Test(expected = ObjectQueryException.class)
	public void testSelectGroupHaving() {
		SelectQuery<Home> qp = new GenericSelectQuery<Home, Object>(Home.class);
		Home target = qp.target();
		qp.prj(target.getAddress());
		qp.prj(qp.box(target.getPrice()), ProjectionType.MAX);
		qp.having(qp.box(target.getPrice()), ProjectionType.MAX).eq(1000000d);

		List<Object[]> res = (List<Object[]>) JDOObjectQuery.execute(qp, persitenceManager);
		assertEquals(1, res.size());
		assertEquals((Double) res.get(0)[1], 1000000d, 0);
	}

	@Test(expected = ObjectQueryException.class)
	public void testSelectBetweenCondition() {
		SelectQuery<Home> qp = new GenericSelectQuery<Home, Object>(Home.class);
		Home target = qp.target();
		qp.between(qp.box(target.getPrice()), 100000D, 2000000D);

		JDOObjectQuery.execute(qp, persitenceManager);
	}

	@After
	public void afterTest() {
		if (persitenceManager != null) {
			persitenceManager.currentTransaction().commit();
			persitenceManager.close();
		}
		persitenceManager = null;
	}

}
