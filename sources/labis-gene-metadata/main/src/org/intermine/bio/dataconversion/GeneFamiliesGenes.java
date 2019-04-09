package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Gene Families Genes
 * 
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class GeneFamiliesGenes extends NewObjectModel
{
	private String description;
    private GeneFamilies geneFamily;
    private Gene gene;

    public GeneFamiliesGenes() throws Exception {
        this.setClassName("GeneFamiliesGenes");
    }

    public void setDescription(String description) throws Exception {
    	this.description = description;
    }

    public String getDescription() throws Exception {
    	return this.description;
    }

	public void setGeneFamily(GeneFamilies geneFamily) throws Exception {
		this.geneFamily = geneFamily;
	}

	public GeneFamilies getGeneFamily() throws Exception {
		return this.geneFamily;
	}

	public String getGeneFamilyId() throws Exception {
		return this.geneFamily.getUniqueId();
	}

	public void setGene(Gene gene) throws Exception {
		this.gene = gene;
	}

	public Gene getGene() throws Exception {
		return this.gene;
	}

	public String getGeneId() throws Exception {
		return this.gene.getUniqueId();
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
        score.setReference("geneFamily", getGeneFamilyId());
        score.setReference("gene", getGeneId());
        String description = getDescription();
        if ("".equals(description)) {
            score.setAttributeToEmptyString("description");
        } else {
            score.setAttribute("description", description);
        }
        return score;
	}
}