package org.intermine.bio.dataconversion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

/**
 * <h1>Described class of Experiment Descriptions</h1>
 * <p>
 * Labis - IQ, USP. São Paulo
 *
 * @author Rodrigo Dorado
 * @version 1.0
 * @since   2019-08-31
 */
public class ExperimentDescription extends NewObjectModel
{
	private String name;
	private String description;
	private Map<String, Publication> publications = new HashMap<String, Publication>();
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

	public void addPublication(Publication publication) throws Exception {
		if (!publications.containsKey(publication.getUniqueId())) {
			this.publications.put(publication.getUniqueId(), publication);
		}
	}

	public Map<String, Publication> getPublications() throws Exception {
		return this.publications;
	}

	public List<String> getPublicationsIds() throws Exception {
		List<String> refIds = new ArrayList<String>();
		Iterator it = this.publications.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry e = (Map.Entry) it.next();
		    refIds.add((String) e.getKey());
		}
		return refIds;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("name", getName());
        score.setAttribute("description", getDescription());
        score.setCollection("publications", getPublicationsIds());
        score.setAttribute("experimentTitle", getExperimentTitle());
        score.setAttribute("accession", getAccession());
        return score;
	}

}