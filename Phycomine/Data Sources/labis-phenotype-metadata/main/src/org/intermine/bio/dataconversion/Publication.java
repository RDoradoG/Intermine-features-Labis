package org.intermine.bio.dataconversion;

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

/**
 * <h1>Described class of Publications</h1>
 * <p>
 * Labis - IQ, USP. São Paulo
 *
 * @author Rodrigo Dorado
 * @version 1.0
 * @since   2019-08-31
 */
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