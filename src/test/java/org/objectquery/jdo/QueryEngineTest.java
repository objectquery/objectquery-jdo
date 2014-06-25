package org.objectquery.jdo;

import static org.junit.Assert.assertTrue;

import javax.jdo.PersistenceManager;

import org.junit.Test;
import org.objectquery.QueryEngine;

public class QueryEngineTest {

	@Test
	public void testFactory() {
		QueryEngine<PersistenceManager> instance = QueryEngine.instance(PersistenceManager.class);
		assertTrue(instance instanceof JDOQueryEngine);
	}

	@Test
	public void testDefalutFactory() {
		QueryEngine<PersistenceManager> instance = QueryEngine.defaultInstance();
		assertTrue(instance instanceof JDOQueryEngine);
	}
}
