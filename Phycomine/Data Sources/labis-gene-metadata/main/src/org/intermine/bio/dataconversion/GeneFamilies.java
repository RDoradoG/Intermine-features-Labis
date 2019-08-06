package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Gene Families
 *
 * @author Rodrigo Dorado
 */

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class GeneFamilies extends NewObjectModel
{
	private String name;
    private TypeGeneFamily typeGeneFamily;
    private String linkTag;
	private GeneFamilyDataset dataset;
	private String annotation;
	private Map<String, Gene> genes = new HashMap<String, Gene>();

    public GeneFamilies() throws Exception {
        this.setClassName("GeneFamilies");
        setLinkTag("<a href=\"{{LINK_REPLACE}}\">Link</a>");
    }

	public String getName() throws Exception {
		return this.name;
	}

	public void setName(String name) throws Exception {
		this.name = name;
	}

	public TypeGeneFamily getType() throws Exception {
		return this.typeGeneFamily;
	}

	public String getTypeId() throws Exception {
		return this.typeGeneFamily.getUniqueId();
	}

	public void setType(TypeGeneFamily typeGeneFamily) throws Exception {
		this.typeGeneFamily = typeGeneFamily;
	}

	private String setLink() throws Exception {
		String dbLink = getType().getDbLink();
		if (dbLink.isEmpty()) {
			return "";
		}
		dbLink = dbLink + getName();
		return  getLinkTag().replace("{{LINK_REPLACE}}", dbLink);
	}

	private String getLinkTag() throws Exception {
		return this.linkTag;
	}

	private void setLinkTag(String linkTag) throws Exception {
		this.linkTag = linkTag;
	}

	public GeneFamilyDataset getDataset() throws Exception {
		return this.dataset;
	}

	public void setDataset(GeneFamilyDataset dataset) throws Exception {
		this.dataset = dataset;
	}

	public String getDatasetId() throws Exception {
		return this.dataset.getUniqueId();
	}

	public Boolean isNullDataset() throws Exception {
		return (this.dataset == null);
	}

	public String getAnnotation() throws Exception {
		return this.annotation;
	}

	public void setAnnotation(String annotation) throws Exception {
		this.annotation = annotation;
	}

	public Item saveAttribute(Item score, String key, String value) throws Exception {
		if (value.isEmpty()) {
        	score.setAttributeToEmptyString(key);
        } else {
	        score.setAttribute(key, value);
        }
        return score;
	}

	public void addGene(Gene gene) throws Exception {
		if (!this.genes.containsKey(gene.getUniqueId())) {
			this.genes.put(gene.getUniqueId(), gene);
		}
	}

	public Map<String, Gene> getGenes() throws Exception {
		return this.genes;
	}

	public Integer sizeGenes() throws Exception {
		return this.genes.size();
	}

	public List<String> getGenesIds() throws Exception {
		List<String> refIds = new ArrayList<String>();
		Iterator it         = this.genes.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry entry = (Map.Entry) it.next();
		    refIds.add((String) entry.getKey());
		}
		return refIds;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
        score.setAttribute("name", getName());
		String link = setLink();
		score       = saveAttribute(score, "dbLink", link);
		score       = saveAttribute(score, "annotation", annotation);
	    if (sizeGenes() > 0) {
	    	score.setCollection("gene", getGenesIds());
	    }
        score.setReference("type", getTypeId());
        if (!isNullDataset()) {
        	score.setReference("dataset", getDatasetId());
        }
        return score;
	}

}