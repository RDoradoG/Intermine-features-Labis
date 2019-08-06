package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of New Object Model
 *
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;

public class NewObjectModel
{
	private String uniqueId;
	private String className;

	public void setClassName(String className) throws Exception {
    	this.className = className;
    }

	public String getClassName() throws Exception {
    	return this.className;
    }

    public void setUniqueId(String uniqueId) throws Exception {
    	this.uniqueId = uniqueId;
    }

    public String getUniqueId() throws Exception {
    	return this.uniqueId;
    }
}