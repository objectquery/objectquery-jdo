package org.objectquery.jdo;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.objectquery.DeleteQuery;
import org.objectquery.InsertQuery;
import org.objectquery.QueryEngine;
import org.objectquery.SelectMapQuery;
import org.objectquery.SelectQuery;
import org.objectquery.UpdateQuery;

public class JDOQueryEngine extends QueryEngine<PersistenceManager> {

	@SuppressWarnings("unchecked")
	@Override
	public <RET extends List<?>> RET execute(SelectQuery<?> query, PersistenceManager engineSession) {
		return (RET) JDOObjectQuery.execute(query, engineSession);
	}

	@Override
	public int execute(DeleteQuery<?> dq, PersistenceManager engineSession) {
		throw new UnsupportedOperationException("JDO Persistence Engine doesn't support delete query");
	}

	@Override
	public boolean execute(InsertQuery<?> ip, PersistenceManager engineSession) {
		throw new UnsupportedOperationException("JDO Persistence Engine doesn't support insert query");
	}

	@Override
	public int execute(UpdateQuery<?> query, PersistenceManager engineSession) {
		throw new UnsupportedOperationException("JDO Persistence Engine doesn't support update query");
	}

	@Override
	public <RET extends List<M>, M> RET execute(SelectMapQuery<?, M> query, PersistenceManager engineSession) {
		// TODO Auto-generated method stub
		return null;
	}

}
