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