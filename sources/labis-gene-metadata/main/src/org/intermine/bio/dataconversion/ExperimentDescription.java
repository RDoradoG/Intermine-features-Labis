package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2016 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */
import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

/**
 *
 * @author
 */
public class ExperimentDescription extends NewObjectModel
{
	private String name;
	private String description;
	private Publication publication;

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

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("name", getName());
        score.setAttribute("description", getDescription());
        score.setReference("publication", getPublicationId());
        return score;
	}

}