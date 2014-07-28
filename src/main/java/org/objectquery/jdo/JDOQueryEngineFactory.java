package org.objectquery.jdo;

import javax.jdo.PersistenceManager;

import org.objectquery.QueryEngine;
import org.objectquery.QueryEngineFactory;

public class JDOQueryEngineFactory implements QueryEngineFactory {

	@Override
	public <S> QueryEngine<S> createQueryEngine(Class<S> targetSession) {
		if (PersistenceManager.class.equals(targetSession))
			return createDefaultQueryEngine();
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> QueryEngine<T> createDefaultQueryEngine() {
		return (QueryEngine<T>) new JDOQueryEngine();
	}

}
