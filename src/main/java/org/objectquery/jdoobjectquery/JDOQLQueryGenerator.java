package org.objectquery.jdoobjectquery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.objectquery.generic.ConditionElement;
import org.objectquery.generic.ConditionGroup;
import org.objectquery.generic.ConditionItem;
import org.objectquery.generic.ConditionType;
import org.objectquery.generic.GenericInternalQueryBuilder;
import org.objectquery.generic.GenericObjectQuery;
import org.objectquery.generic.GroupType;
import org.objectquery.generic.ObjectQueryException;
import org.objectquery.generic.Order;
import org.objectquery.generic.OrderType;
import org.objectquery.generic.PathItem;
import org.objectquery.generic.Projection;
import org.objectquery.generic.ProjectionType;

public class JDOQLQueryGenerator {

	private Map<String, Object> parameters = new LinkedHashMap<String, Object>();
	private String query;

	JDOQLQueryGenerator(GenericObjectQuery<?> jpqlObjectQuery) {
		buildQuery(jpqlObjectQuery.getTargetClass(), (GenericInternalQueryBuilder) jpqlObjectQuery.getBuilder());
	}

	private void stringfyGroup(ConditionGroup group, StringBuilder builder) {
		if (!group.getConditions().isEmpty()) {
			Iterator<ConditionElement> eli = group.getConditions().iterator();
			while (eli.hasNext()) {
				ConditionElement el = eli.next();
				if (el instanceof ConditionItem) {
					stringfyCondition((ConditionItem) el, builder);
				} else if (el instanceof ConditionGroup) {
					builder.append(" ( ");
					stringfyGroup((ConditionGroup) el, builder);
					builder.append(" ) ");
				}
				if (eli.hasNext()) {
					builder.append(getGroupType(group.getType()));
				}
			}
		}
	}

	private String getGroupType(GroupType type) {
		switch (type) {
		case AND:
			return " && ";
		case OR:
			return " || ";
		}
		return "";
	}

	private String getConditionType(ConditionType type) {
		switch (type) {
		case CONTAINS:
			break;
		case EQUALS:
			return " == ";
		case IN:
			break;
		case LIKE:
			break;
		case MAX:
			return " > ";
		case MIN:
			return " < ";
		case MAX_EQUALS:
			return " >= ";
		case MIN_EQUALS:
			return " <= ";
		case NOT_CONTAINS:
			break;
		case NOT_EQUALS:
			return " != ";
		case NOT_IN:
			break;
		case NOT_LIKE:
			break;
		case LIKE_NOCASE:
			break;
		case NOT_LIKE_NOCASE:
			break;
		case BETWEEN:
			return " BETWEEN ";
		}
		return "";
	}

	private void buildName(PathItem item, StringBuilder sb) {
		sb.append("A");
		if (item.getParent() != null)
			sb.append(".");
		GenericInternalQueryBuilder.buildPath(item, sb);
	}

	private String buildParameterName(PathItem item, Object value) {
		StringBuilder name = new StringBuilder("param_");
		buildParameterName(item, name);
		int i = 1;
		String realName = name.toString();
		do {
			if (!parameters.containsKey(realName)) {
				parameters.put(realName, value);
				return realName;
			}
			realName = name.toString() + i++;
		} while (true);
	}

	private void stringfyCondition(ConditionItem cond, StringBuilder sb) {

		ConditionType type = cond.getType();
		if (type.equals(ConditionType.CONTAINS) || type.equals(ConditionType.NOT_CONTAINS)) {
			if (type.equals(ConditionType.NOT_CONTAINS))
				sb.append("!");
			buildName(cond.getItem(), sb);
			sb.append(".contains(");
			conditionValue(cond, sb);
			sb.append(")");
		} else if (type.equals(ConditionType.IN) || type.equals(ConditionType.NOT_IN)) {
			if (type.equals(ConditionType.NOT_IN))
				sb.append("!");
			conditionValue(cond, sb);
			sb.append(".contains(");
			buildName(cond.getItem(), sb);
			sb.append(")");
		} else if (type.equals(ConditionType.LIKE) || type.equals(ConditionType.NOT_LIKE) || type.equals(ConditionType.LIKE_NOCASE)
				|| type.equals(ConditionType.NOT_LIKE_NOCASE)) {
			if (type.equals(ConditionType.NOT_LIKE) || type.equals(ConditionType.NOT_LIKE_NOCASE))
				sb.append("!");
			buildName(cond.getItem(), sb);
			if (type.equals(ConditionType.LIKE_NOCASE) || type.equals(ConditionType.NOT_LIKE_NOCASE))
				sb.append(".toUpperCase()");
			sb.append(".matches(");
			conditionValue(cond, sb);
			if (type.equals(ConditionType.LIKE_NOCASE) || type.equals(ConditionType.NOT_LIKE_NOCASE))
				sb.append(".toUpperCase()");
			sb.append(")");
		} else {
			buildName(cond.getItem(), sb);
			sb.append(" ").append(getConditionType(type)).append(" ");
			conditionValue(cond, sb);
			if (cond.getType().equals(ConditionType.BETWEEN))
				throw new ObjectQueryException("Operator BETWEEN not supported by JDO datastore", null);

		}
	}

	private void conditionValue(ConditionItem cond, StringBuilder sb) {
		if (cond.getValue() instanceof PathItem) {
			buildName((PathItem) cond.getValue(), sb);
		} else {
			sb.append(buildParameterName(cond.getItem(), cond.getValue()));
		}
	}

	private String resolveFunction(ProjectionType projectionType) {
		switch (projectionType) {
		case AVG:
			return "AVG";
		case MAX:
			return "MAX";
		case MIN:
			return "MIN";
		case COUNT:
			return "COUNT";
		case SUM:
			return "SUM";
		}
		return "";
	}

	public void buildQuery(Class<?> clazz, GenericInternalQueryBuilder query) {
		parameters.clear();
		List<Projection> groupby = new ArrayList<Projection>();
		boolean grouped = false;
		StringBuilder builder = new StringBuilder();
		builder.append("select ");
		if (!query.getProjections().isEmpty()) {
			Iterator<Projection> projections = query.getProjections().iterator();
			while (projections.hasNext()) {
				Projection proj = projections.next();
				if (proj.getType() != null) {
					builder.append(" ").append(resolveFunction(proj.getType())).append("(");
					grouped = true;
				} else
					groupby.add(proj);
				buildName(proj.getItem(), builder);
				if (proj.getType() != null)
					builder.append(")");
				if (projections.hasNext())
					builder.append(",");
			}
		} else
			builder.append("A");
		builder.append(" from ").append(clazz.getName()).append(" A");
		if (!query.getConditions().isEmpty()) {
			builder.append(" where ");
			stringfyGroup(query, builder);
		}

		StringBuilder havingBuilder = new StringBuilder();
		if (!query.getHavings().isEmpty()) {
			throw new ObjectQueryException("Operation not supported by jdo datastore", null);
			/*
			havingBuilder.append(" having");
			Iterator<Having> havings = query.getHavings().iterator();
			while (havings.hasNext()) {
				Having having = havings.next();
				havingBuilder.append(" ").append(resolveFunction(having.getProjectionType())).append('(');
				buildName(having.getItem(), havingBuilder);
				havingBuilder.append(')').append(getConditionType(having.getConditionType()));
				havingBuilder.append(buildParameterName(having.getItem(), having.getValue()));
				if (havings.hasNext())
					havingBuilder.append(" ,");
			}*/
		}

		if (!parameters.isEmpty()) {
			builder.append(" PARAMETERS ");
			Iterator<Map.Entry<String, Object>> parami = parameters.entrySet().iterator();
			while (parami.hasNext()) {
				Map.Entry<String, Object> param = parami.next();
				if (param.getValue() != null) {
					if (Collection.class.isAssignableFrom(param.getValue().getClass()))
						builder.append("java.util.Collection");
					else
						builder.append(param.getValue().getClass().getSimpleName());
					builder.append(" ").append(param.getKey());
				}
				if (parami.hasNext())
					builder.append(",");
			}
		}
		boolean orderGrouped = false;
		for (Order ord : query.getOrders()) {
			if (ord.getProjectionType() != null) {
				orderGrouped = true;
				break;
			}
		}

		if ((orderGrouped || grouped) && !groupby.isEmpty()) {
			builder.append(" group by ");
			Iterator<Projection> projections = groupby.iterator();
			while (projections.hasNext()) {
				Projection proj = projections.next();
				buildName(proj.getItem(), builder);
				if (projections.hasNext())
					builder.append(",");
			}
		} else if (orderGrouped && query.getProjections().isEmpty()) {
			builder.append(" group by A ");
		}
		builder.append(havingBuilder);

		if (!query.getOrders().isEmpty()) {
			builder.append(" order by ");
			Iterator<Order> orders = query.getOrders().iterator();
			while (orders.hasNext()) {
				Order ord = orders.next();
				if (ord.getProjectionType() != null)
					builder.append(" ").append(resolveFunction(ord.getProjectionType())).append("(");
				buildName(ord.getItem(), builder);
				if (ord.getProjectionType() != null)
					throw new ObjectQueryException("Unsupported operation count in order by clause by jdoql", null);
				if (ord.getType() != null)
					builder.append(" ").append(getOrderType(ord.getType()));
				if (orders.hasNext())
					builder.append(',');
			}
		}

		this.query = builder.toString();
	}

	public String getOrderType(OrderType type) {
		switch (type) {
		case ASC:
			return "ascending";
		case DESC:
			return "descending";
		}
		return "";
	}

	private void buildParameterName(PathItem conditionItem, StringBuilder builder) {
		GenericInternalQueryBuilder.buildPath(conditionItem, builder, "_");
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public String getQuery() {
		return query;
	}
}
