package org.fastcode.util;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;

public class GenericTypeCache {
	Set<IMethod>					stringMethods		= new HashSet<IMethod>();
	Set<IMethod>					collectionMethods	= new HashSet<IMethod>();
	IMethod[]					stringMethodsArr;
	IMethod[]				collectionMethodsArr;
	private static GenericTypeCache	genericTypeCache		= new GenericTypeCache();

	public Set<IMethod> getStringMethods() {
		return this.stringMethods;
	}

	public Set<IMethod> getCollectionMethods() {
		return this.collectionMethods;
	}

	/**
	 *
	 * @return
	 */
	public static GenericTypeCache getInstance() {
		return genericTypeCache;
	}

	public IMethod[] getStringMethodsArr() {
		return this.stringMethodsArr;
	}

	public IMethod[] getCollectionMethodsArr() {
		return this.collectionMethodsArr;
	}

	public void setStringMethodsArr(final IMethod[] stringMethodsArr) {
		this.stringMethodsArr = stringMethodsArr;
	}

	public void setCollectionMethodsArr(final IMethod[] collectionMethodsArr) {
		this.collectionMethodsArr = collectionMethodsArr;
	}

}
