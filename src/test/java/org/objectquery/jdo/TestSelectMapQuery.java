package org.objectquery.jdo;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.objectquery.SelectMapQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.jdo.domain.Person;
import org.objectquery.jdo.domain.PersonDTO;

public class TestSelectMapQuery {

	@Test
	public void testSimpleSelectMap() {
		SelectMapQuery<Person, PersonDTO> query = new GenericSelectQuery<Person, PersonDTO>(Person.class, PersonDTO.class);
		query.prj(query.target().getName(), query.mapper().getName());

		assertThat(JDOObjectQuery.jdoqlGenerator(query).getQuery(), CoreMatchers.is("select A.name as name from org.objectquery.jdo.domain.Person A"));

	}

}
