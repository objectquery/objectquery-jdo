package org.objectquery.jdo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.objectquery.BaseSelectQuery;
import org.objectquery.SelectMapQuery;
import org.objectquery.SelectQuery;
import org.objectquery.generic.GenericInternalQueryBuilder;
import org.objectquery.generic.GenericSelectQuery;
import org.objectquery.generic.ObjectQueryException;
import org.objectquery.generic.Projection;

public class JDOObjectQuery {

	public static JDOQLQueryGenerator jdoqlGenerator(BaseSelectQuery<?> objectQuery) {
		if (objectQuery instanceof GenericSelectQuery<?, ?>)
			return new JDOQLQueryGenerator((GenericSelectQuery<?, ?>) objectQuery);
		throw new ObjectQueryException("The Object query instance of unconvertable implementation ", null);
	}

	public static Object execute(SelectQuery<?> objectQuery, PersistenceManager persistenceManager) {
		JDOQLQueryGenerator qg = JDOObjectQuery.jdoqlGenerator(objectQuery);
		Query query = persistenceManager.newQuery(qg.getQuery());
		query.setClass(((GenericSelectQuery<?, ?>) objectQuery).getTargetClass());
		return query.executeWithMap(qg.getParameters());
	}

	@SuppressWarnings("unchecked")
	public static <M> List<M> execute(SelectMapQuery<?, M> objectQuery, PersistenceManager persistenceManager) {
		JDOQLQueryGenerator qg = JDOObjectQuery.jdoqlGenerator(objectQuery);
		GenericSelectQuery<?, M> gq = (GenericSelectQuery<?, M>) objectQuery;
		Query query = persistenceManager.newQuery(qg.getQuery());
		query.setClass(((GenericSelectQuery<?, ?>) objectQuery).getTargetClass());
		List<?> res = (List<?>) query.executeWithMap(qg.getParameters());

		List<Projection> projections = ((GenericInternalQueryBuilder) gq.getBuilder()).getProjections();
		List<M> realR = new ArrayList<>();
		if (projections.size() == 1) {
			Projection prj = projections.get(0);
			StringBuilder builder = new StringBuilder();
			GenericInternalQueryBuilder.buildAlias(prj, builder);
			String name = builder.toString();
			Map<String, Object> values = new HashMap<String, Object>();
			for (Object value : (List<Object>) res) {
				values.put(name, value);
				realR.add(GenericInternalQueryBuilder.setMapping(gq.getMapperClass(), projections, values));
			}
		} else {
			for (Map<String, Object> values : (List<Map<String, Object>>) res) {
				realR.add(GenericInternalQueryBuilder.setMapping(gq.getMapperClass(), projections, values));
			}
		}
		return realR;

		// return null;
	}
}
