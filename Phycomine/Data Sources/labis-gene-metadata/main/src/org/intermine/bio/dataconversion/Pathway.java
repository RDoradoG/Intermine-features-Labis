package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Pathways
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

public class Pathway extends NewObjectModel
{
	private String name;
	private String identifier;
	private String description;
	private PathwayCategory category = null;

	public Pathway() throws Exception {
        this.setClassName("Pathway");
    }

	public void setIdentifier(String identifier) throws Exception {
		this.identifier = identifier;
	}

	public String getIdentifier() throws Exception {
		return this.identifier;
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

	public void setCategory(PathwayCategory category) throws Exception {
		this.category = category;
	}

	public PathwayCategory getCategory() throws Exception {
		return this.category;
	}

	public String getCategoryId() throws Exception {
		return this.category.getUniqueId();
	}

	public Item savePosibilityEmpty(Item score, String id, String value) throws ObjectStoreException, Exception {
		if (StringUtils.isBlank(value)) {
			score.setAttributeToEmptyString(id);
		} else {
			score.setAttribute(id, value);
		}
		return score;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("identifier", getIdentifier());
		score.setAttribute("name", getName());
		score = this.savePosibilityEmpty(score, "description", getDescription());
		if (this.category != null) {
			score.setReference("category", getCategoryId());
		}
        return score;
	}
}

