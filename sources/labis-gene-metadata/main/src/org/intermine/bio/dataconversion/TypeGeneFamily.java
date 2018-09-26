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
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

/**
 *
 * @author
 */
public class TypeGeneFamily extends NewObjectModel
{
	private String name;
	private String dbLink;

    public TypeGeneFamily() throws Exception {
        this.setClassName("TypeGeneFamily");
    }

	public void setName(String name) throws Exception {
		this.name = name;
	}

	public String getName() throws Exception {
		return this.name;
	} 

	public void setDbLink(String dbLink) throws Exception {
		this.dbLink = dbLink;
	}

	public String getDbLink() throws Exception {
		return this.dbLink;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
        score.setAttribute("name", this.name);
        return score;
	}

}
