package Repositories;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import AppStart.DatabaseConnector;
import Entities.AbstractEntity;

public abstract class GenericRepository <EntityType extends AbstractEntity> {

	List<EntityType> list;
	private int indexCount;
	private String tableName;
	
	public GenericRepository(String tableName) {
		this.indexCount = 1;
		this.tableName = tableName;
		this.list = new LinkedList<EntityType>();
	}
	
	protected abstract EntityType resultToObject(ResultSet rs);
	protected abstract String objectToInsertQuery(EntityType e);
	protected abstract String objectToUpdateQuery(EntityType e);
	protected abstract void createTable();
	
	public EntityType getById(int id) {
		ResultSet rs = DatabaseConnector.executeQuery("SELECT * FROM " +tableName + " WHERE id =" + id);
		try {
			if(!rs.next()) return null;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.resultToObject(rs);
	}
	
	public List<EntityType> getAll() {
		List<EntityType> list = new ArrayList<EntityType>();
		ResultSet rs = DatabaseConnector.executeQuery("SELECT * FROM " +tableName);
		try {
			while(rs.next()) {
				EntityType e = resultToObject(rs);
				list.add(e);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}
	
	public EntityType create(EntityType entity) {
		String query = this.objectToInsertQuery(entity);
		entity.id = DatabaseConnector.executeInsert(query);
		return entity;
	}
	
	public EntityType update(EntityType entity) {
		String query = this.objectToUpdateQuery(entity);
		int updatedRecords = DatabaseConnector.executeUpdate(query);
		return updatedRecords > 0 ? entity : null;
	}
	
	public boolean delete(EntityType entity) {
		int deleted = DatabaseConnector.executeUpdate("DELETE FROM " +tableName + " WHERE id = "+ entity.id);
		return deleted > 0;
	}
	
	public boolean deleteAll() {
		return DatabaseConnector.executeUpdate("DELETE FROM " +tableName) > 0;
	}
	
	public List<EntityType> findByFieldValue(String fieldName, Object value) {
		
		if(this.list.size() == 0) {
			return null;
		}
		
		Class elementClass = this.list.get(0).getClass();
		try {
			Field specifiedField = elementClass.getField(fieldName);
			Type specifiedFieldType = specifiedField.getType();
			Type searchValueType = value.getClass();
			if(specifiedFieldType.equals(searchValueType)) {
				// object contains field with specified name, and type of value matches object's field type
				
				List<ExtendedFilterPredicate<EntityType>> extendedList = new LinkedList();
				Iterator<EntityType> iterator = list.iterator();
				while(iterator.hasNext()) {
					extendedList.add(new ExtendedFilterPredicate(iterator.next(), fieldName, searchValueType));
				}
				
				Stream<ExtendedFilterPredicate<EntityType>> filteredStream = extendedList.stream().filter(extendedElement->{
						
					Class extendedElementClass = extendedElement.element.getClass();
					Field extendedElementField;
					Object elementValue;
					try {
						extendedElementField = extendedElementClass.getField(extendedElement.fieldname);
						elementValue = extendedElementField.get(extendedElement.element);
						return elementValue.equals(extendedElement.value);
					} catch (NoSuchFieldException | SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return false;

					});
				
				
				List<EntityType> filteredList = filteredStream.map(extendedElement->{
					return extendedElement.element;
				}).toList();
				
				return filteredList;
				
			}
		}
		catch(NoSuchFieldException ex) {
			return null;
		}
		return null;
	}
}

	class ExtendedFilterPredicate <Type>{
		Type element;
		String fieldname;
		Object value;
		public ExtendedFilterPredicate(Type element, String fieldname, Object value) {
			this.element = element;
			this.fieldname = fieldname;
			this.value = value;
		}
	}

