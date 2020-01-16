package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. Sao Paulo
 *
 * Described class of Transcripts
 *
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class Transcript extends NewObjectModel
{
	private String primaryIdentifier;
	private String description;

	public Transcript() throws Exception {
        this.setClassName("MRNA");
    }

    public void setPrimaryIdentifier(String primaryIdentifier) throws Exception {
		this.primaryIdentifier = primaryIdentifier;
	}

	public String getPrimaryIdentifier() throws Exception {
		return this.primaryIdentifier;
	}

	public void setDescription(String description) throws Exception {
		this.description = description;
	}

	public String getDescription() throws Exception {
		return this.description;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("primaryIdentifier", getPrimaryIdentifier());
		score.setAttribute("description", getDescription());
        return score;
	}
}