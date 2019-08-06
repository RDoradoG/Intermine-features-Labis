package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Organisms
 *
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class Organism extends NewObjectModel
{

	private String taxonId;

	public Organism(String taxonId) throws Exception {
        this.setClassName("Organism");
        this.setTaxonId(taxonId);
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