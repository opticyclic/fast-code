package org.fastcode.common;

public class FastCodeEntityHolder {

	private String	entityName;
	private Object	fastCodeEntity; //List<FCMethod>/List<FCField>/FCType/FCFile

	/**
	 *
	 * @param entityName
	 * @param fastCodeEntity
	 */
	public FastCodeEntityHolder(final String entityName, final Object fastCodeEntity) {
		this.entityName = entityName;
		this.fastCodeEntity = fastCodeEntity;
	}

	/**
	 *
	 * getter method for entityName
	 * @return
	 *
	 */
	public String getEntityName() {
		return this.entityName;
	}

	/**
	 *
	 * setter method for entityName
	 * @param entityName
	 *
	 */
	public void setEntityName(final String entityName) {
		this.entityName = entityName;
	}

	/**
	 *
	 * getter method for fastCodeEntity
	 * @return
	 *
	 */
	public Object getFastCodeEntity() {
		return this.fastCodeEntity;
	}

	/**
	 *
	 * setter method for fastCodeEntity
	 * @param fastCodeEntity
	 *
	 */
	public void setFastCodeEntity(final Object fastCodeEntity) {
		this.fastCodeEntity = fastCodeEntity;
	}
}
