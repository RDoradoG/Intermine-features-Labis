package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Genes
 *
 * @author Rodrigo Dorado
 */

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class Gene extends NewObjectModel
{
	private String primaryIdentifier;
	private Organism organism                     = null;
	private Ortholog ortholog                     = null;
	private Hcca hcca                             = null;
	private Map<String, Publication> publications = new HashMap<String, Publication>();
	private Map<String, Pathway> pathways         = new HashMap<String, Pathway>();

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

	public void setHcca(Hcca hcca) throws Exception {
		this.hcca = hcca;
	}

	public Hcca getHcca() throws Exception {
		return this.hcca;
	}

	public String getHccaId() throws Exception {
		return this.hcca.getUniqueId();
	}

	public void addPublications(Map<String, Publication> pubs) throws Exception {
		Iterator it = pubs.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry         = (Map.Entry) it.next();
			Publication publication = (Publication) entry.getValue();
			if (!this.publications.containsKey(publication.getUniqueId())) {
				this.publications.put(publication.getUniqueId(), publication);
			}
		}
	}

	public List<String> getPublicationsIds() throws Exception {
		List<String> refIds = new ArrayList<String>();
		Iterator it         = this.publications.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry entry = (Map.Entry) it.next();
		    refIds.add((String) entry.getKey());
		}
		return refIds;
	}

	public void addPathway(Pathway pathway) throws Exception {
		if (!this.pathways.containsKey(pathway.getUniqueId())) {
			this.pathways.put(pathway.getUniqueId(), pathway);
		}
	}

	public List<String> getPathwaysIds() throws Exception {
		List<String> refIds = new ArrayList<String>();
		Iterator it         = this.pathways.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry entry = (Map.Entry) it.next();
		    refIds.add((String) entry.getKey());
		}
		return refIds;
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

		Hcca hcca = getHcca();
		if (hcca != null) {
			score.setReference("hcca", getHccaId());
		}

		if (this.publications.size() > 0) {
			score.setCollection("publications", getPublicationsIds());
		}

		if (this.pathways.size() > 0) {
			score.setCollection("pathways", getPathwaysIds());
		}

        return score;
	}
}