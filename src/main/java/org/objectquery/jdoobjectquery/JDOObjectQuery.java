package org.objectquery.jdoobjectquery;

import org.objectquery.builder.GenericObjectQuery;
import org.objectquery.builder.ObjectQuery;
import org.objectquery.builder.ObjectQueryException;

public class JDOObjectQuery {

	public static JDOQLQueryGenerator jdoqlGenerator(ObjectQuery<?> objectQuery) {
		if (objectQuery instanceof GenericObjectQuery<?>)
			return new JDOQLQueryGenerator((GenericObjectQuery<?>) objectQuery);
		throw new ObjectQueryException("The Object query instance of unconvertable implementation ", null);
	}
}
