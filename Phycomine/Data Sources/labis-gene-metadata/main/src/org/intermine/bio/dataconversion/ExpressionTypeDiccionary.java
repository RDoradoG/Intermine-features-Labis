package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. Sao Paulo
 *
 * Described class of Expression Type Diccionaries
 *
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class ExpressionTypeDiccionary extends NewObjectModel
{
	private String name;

	public ExpressionTypeDiccionary() throws Exception {
        this.setClassName("ExpressionTypeDiccionary");
    }

	public void setName(String name) throws Exception {
		this.name = name;
	}

	public String getName() throws Exception {
		return this.name;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("name", getName());
        return score;
	}
}