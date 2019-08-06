package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Pathway Categories
 *
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class PathwayCategory extends NewObjectModel
{
	private String category;

	public PathwayCategory() throws Exception {
        this.setClassName("PathwayCategory");
    }

    public void setCategory(String category) throws Exception {
		this.category = category;
	}

	public String getCategory() throws Exception {
		return this.category;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("category", getCategory());
        return score;
	}

}