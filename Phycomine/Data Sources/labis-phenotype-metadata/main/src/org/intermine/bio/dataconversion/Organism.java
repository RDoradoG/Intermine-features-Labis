package org.intermine.bio.dataconversion;

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

/**
 * <h1>Described class of Organisms</h1>
 * <p>
 * Labis - IQ, USP. São Paulo
 *
 * @author Rodrigo Dorado
 * @version 1.0
 * @since   2019-08-31
 */
public class Organism extends NewObjectModel
{

	private String taxonId;

	public Organism() throws Exception {
        this.setClassName("Organism");
    }

    public void setTaxonId(String taxonId) throws Exception {
    	this.taxonId = taxonId;
    }

    public String getTaxonId() throws Exception {
    	return this.taxonId;
    }

    public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("taxonId", getTaxonId());	
        return score;
	}
}