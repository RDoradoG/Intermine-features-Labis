package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Publications
 *
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class Publication extends NewObjectModel
{
	private String pubMedId;

	public Publication() throws Exception {
        this.setClassName("Publication");
    }

    public void setPubMedId(String pubMedId) throws Exception {
		this.pubMedId = pubMedId;
	}

	public String getPubMedId() throws Exception {
		return this.pubMedId;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("pubMedId", getPubMedId());
        return score;
	}
}