package org.intermine.bio.dataconversion;

import org.apache.commons.lang.StringUtils;

/**
 * <h1>Described class of New Object Model</h1>
 * <p>
 * Labis - IQ, USP. São Paulo
 *
 * @author Rodrigo Dorado
 * @version 1.0
 * @since   2019-08-31
 */
public class NewObjectModel
{
    /**
     * The unique id of the element.
     */
	private String uniqueId;
    /**
     * The name of the class.
     */
	private String className;

    /**
     * Set the class name.
     * @param  className The name of the class.
     * @throws Exception
     */
	public void setClassName(String className) throws Exception {
    	this.className = className;
    }

    /**
     * Get the class name.
     * @return The class name, String.
     * @throws Exception
     */
	public String getClassName() throws Exception {
    	return this.className;
    }

    /**
     * Set the unique Id.
     * @param  uniqueId  The unique id.
     * @throws Exception
     */
    public void setUniqueId(String uniqueId) throws Exception {
    	this.uniqueId = uniqueId;
    }

    /**
     * Get the unique id.
     * @return The unique id, String.
     * @throws Exception
     */
    public String getUniqueId() throws Exception {
    	return this.uniqueId;
    } 
}