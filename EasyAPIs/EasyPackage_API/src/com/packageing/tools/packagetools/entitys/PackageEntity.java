package com.packageing.tools.packagetools.entitys;

abstract class PackageEntity<T> {

	private T entity;
	
	public PackageEntity(T entity){
		this.entity = entity;
	}
	
	public T getEntity() {
		return this.entity;
	}
	
}
