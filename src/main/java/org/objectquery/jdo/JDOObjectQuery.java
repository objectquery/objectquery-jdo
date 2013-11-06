package org.objectquery.jdo;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.objectquery.SelectQuery;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.generic.ObjectQueryException;

public class JDOObjectQuery {

	public static JDOQLQueryGenerator jdoqlGenerator(SelectQuery<?> objectQuery) {
		if (objectQuery instanceof GenericSelectQuery<?>)
			return new JDOQLQueryGenerator((GenericSelectQuery<?>) objectQuery);
		throw new ObjectQueryException("The Object query instance of unconvertable implementation ", null);
	}

	public static Object execute(SelectQuery<?> objectQuery, PersistenceManager persistenceManager) {
		JDOQLQueryGenerator qg = JDOObjectQuery.jdoqlGenerator(objectQuery);
		Query query = persistenceManager.newQuery(qg.getQuery());
		query.setClass(((GenericSelectQuery<?>) objectQuery).getTargetClass());
		return query.executeWithMap(qg.getParameters());
	}
}
