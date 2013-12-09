package org.objectquery.jdo;

import javax.jdo.PersistenceManager;

import org.junit.Assert;
import org.junit.Test;
import org.objectquery.QueryEngine;

public class QueryEngineTest {

	@Test
	public void testFactory() {
		QueryEngine<PersistenceManager> instance = QueryEngine.instance(PersistenceManager.class);
		Assert.assertTrue(instance instanceof JDOQueryEngine);
	}

	@Test
	public void testDefalutFactory() {
		QueryEngine<PersistenceManager> instance = QueryEngine.defaultInstance();
		Assert.assertTrue(instance instanceof JDOQueryEngine);
	}
}
