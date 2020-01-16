package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. Sao Paulo
 *
 * Described class of Orthologs
 *
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class Ortholog extends NewObjectModel
{

	private String name;

	public Ortholog(String name) throws Exception {
        this.setClassName("Ortholog");
        this.setName(name);
    }

    public void setName(String name) throws Exception {
    	this.name = name;
    }

    public String getName() throws Exception {
    	return this.name;
    }

    public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("name", this.getName());
        return score;
	}
}