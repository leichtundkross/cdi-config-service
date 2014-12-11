package com.exxeta.configservice;

import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import com.exxeta.configservice.ConfigurationEntity.Entry;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBObject;

/**
 * This is a kind of workaround: Since {@link Entry} stores a generic value <T> morphia seems to have problems decoding the concrete type.
 */
public final class EntityConverter extends TypeConverter implements SimpleValueConverter {

	public EntityConverter() {
		super(Entry.class);
	}

	@Override
	public DBObject encode(Object value, MappedField optionalExtraInfo) {
		Entry<?> entry = (Entry<?>) value;
		BasicDBObjectBuilder builder = BasicDBObjectBuilder.start("value", entry.getValue());
		if (entry.getValidFrom() != null) {
			builder.add("validFrom", entry.getValidFrom());
		}

		if (entry.getValidFrom() != null) {
			builder.add("validTo", entry.getValidTo());
		}

		return builder.get();
	}

	@Override
	public Entry<?> decode(Class<?> targetClass, Object fromDBObject, MappedField optionalExtraInfo) {
		BasicDBObject dbo = (BasicDBObject) fromDBObject;
		return new Entry<>(dbo.get("value"), dbo.getDate("validFrom"), dbo.getDate("validTo"));
	}
}
