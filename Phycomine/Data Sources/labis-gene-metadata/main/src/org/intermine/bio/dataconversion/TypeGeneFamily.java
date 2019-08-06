package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Type Gene Families
 *
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

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
