package org.intermine.bio.dataconversion;

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

/**
 * <h1>Described class of Expression Type Diccionaries</h1>
 * <p>
 * Labis - IQ, USP. São Paulo
 *
 * @author Rodrigo Dorado
 * @version 1.0
 * @since   2019-08-31
 */
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