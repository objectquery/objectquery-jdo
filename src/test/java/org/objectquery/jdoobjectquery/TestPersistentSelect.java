package org.objectquery.jdoobjectquery;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectquery.builder.GenericObjectQuery;
import org.objectquery.jdoobjectquery.domain.Person;

public class TestPersistentSelect {

	private PersistenceManager peristenceManager;

	@Before
	public void beforeTest() {
		peristenceManager = JDOTestHelper.getFactory().getPersistenceManager();
		peristenceManager.currentTransaction().begin();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleSelect() {
		GenericObjectQuery<Person> qp = new GenericObjectQuery<Person>(Person.class);
		Person target = qp.target();
		qp.eq(target.getName(), "tom");

		JDOQLQueryGenerator qg = JDOObjectQuery.jdoqlGenerator(qp);
		Query query= peristenceManager.newQuery(qg.getQuery());
		
		List<Person> res =(List<Person>)query.execute(qg.getParamenters()); 
		Assert.assertEquals(1, res.size());
		Assert.assertEquals(res.get(0).getName(), "tom");
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
