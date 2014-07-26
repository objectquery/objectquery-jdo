package org.objectquery.jdo;

import static org.junit.Assert.assertThat;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.objectquery.SelectMapQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.jdo.domain.Person;
import org.objectquery.jdo.domain.PersonDTO;

public class TestPersistentSelectMapQuery {
	private PersistenceManager peristenceManager;

	@Before
	public void beforeTest() {
		peristenceManager = JDOTestHelper.getFactory().getPersistenceManager();
		peristenceManager.currentTransaction().begin();
	}

	@Test
	public void testSimpleSelectMap() {
		SelectMapQuery<Person, PersonDTO> query = new GenericSelectQuery<Person, PersonDTO>(Person.class, PersonDTO.class);
		query.eq(query.target().getName(), "tom");
		query.prj(query.target().getName(), query.mapper().getName());

		List<PersonDTO> res = JDOObjectQuery.execute(query, peristenceManager);
		assertThat(res.size(), CoreMatchers.is(1));
		assertThat(res.get(0).getName(), CoreMatchers.is("tom"));
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
