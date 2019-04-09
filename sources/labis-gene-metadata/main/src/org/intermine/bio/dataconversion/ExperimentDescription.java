package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Experiment Descriptions  
 * 
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class ExperimentDescription extends NewObjectModel
{
	private String name;
	private String description;
	private Publication publication;
	private String accession;
	private String experimentTitle;

	public ExperimentDescription() throws Exception {
        this.setClassName("ExperimentDescription");
    }

	public void setName(String name) throws Exception {
		this.name = name;
	}

	public String getName() throws Exception {
		return this.name;
	}

	public void setDescription(String description) throws Exception {
		this.description = description;
	}

	public String getDescription() throws Exception {
		return this.description;
	}

	public void setPublication(Publication publication) throws Exception {
		this.publication = publication;
	}

	public Publication getPublication() throws Exception {
		return this.publication;
	}

	public String getPublicationId() throws Exception {
		return this.publication.getUniqueId();
	}

	public void setAccession(String accession) throws Exception {
		this.accession = accession;
	}

	public String getAccession() throws Exception {
		return this.accession;
	}

	public void setExperimentTitle(String experimentTitle) throws Exception {
		this.experimentTitle = experimentTitle;
	}

	public String getExperimentTitle() throws Exception {
		return this.experimentTitle;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("name", getName());
        score.setAttribute("description", getDescription());
        score.setReference("publication", getPublicationId());
        score.setAttribute("experimentTitle", getExperimentTitle());
        score.setAttribute("accession", getAccession());
        return score;
	}

}