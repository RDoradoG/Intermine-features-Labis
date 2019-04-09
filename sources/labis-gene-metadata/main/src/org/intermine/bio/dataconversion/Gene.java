package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Genes  
 * 
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class Gene extends NewObjectModel
{
	private String primaryIdentifier;
	private Organism organism = null;
	private Ortholog ortholog = null;

	public Gene() throws Exception {
        this.setClassName("Gene");
    }

    public void setPrimaryIdentifier(String primaryIdentifier) throws Exception {
		this.primaryIdentifier = primaryIdentifier;
	}

	public String getPrimaryIdentifier() throws Exception {
		return this.primaryIdentifier;
	}

	public void setOrganism(Organism organism) throws Exception {
		this.organism = organism;
	}

	public Organism getOrganism() throws Exception {
		return this.organism;
	}

	public String getOrganismId() throws Exception {
		return this.organism.getUniqueId();
	}

	public void setOrtholog(Ortholog ortholog) throws Exception {
		this.ortholog = ortholog;
	}

	public Ortholog getOrtholog() throws Exception {
		return this.ortholog;
	}

	public String getOrthologId() throws Exception {
		return this.ortholog.getUniqueId();
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("primaryIdentifier", getPrimaryIdentifier());
		Organism organism = getOrganism();
		if (organism != null) {
			score.setReference("organism", getOrganismId());
		}

		Ortholog ortholog = getOrtholog();
		if (ortholog != null) {
			score.setReference("ortholog", getOrthologId());
		}
        return score;
	}
}