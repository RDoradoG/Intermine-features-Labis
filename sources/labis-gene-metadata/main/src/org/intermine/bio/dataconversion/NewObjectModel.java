package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2016 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author
 */
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