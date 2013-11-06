package org.objectquery.jdo;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.objectquery.DeleteQuery;
import org.objectquery.InsertQuery;
import org.objectquery.QueryEngine;
import org.objectquery.SelectQuery;
import org.objectquery.UpdateQuery;

public class JDOQueryEngine extends QueryEngine<PersistenceManager> {

	@Override
	public <RET extends List<?>> RET execute(SelectQuery<?> query, PersistenceManager engineSession) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int execute(DeleteQuery<?> dq, PersistenceManager engineSession) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean execute(InsertQuery<?> ip, PersistenceManager engineSession) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int execute(UpdateQuery<?> query, PersistenceManager engineSession) {
		// TODO Auto-generated method stub
		return 0;
	}

}
