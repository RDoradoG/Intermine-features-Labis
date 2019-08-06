package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Gene Families Dataset
 *
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class GeneFamilyDataset extends NewObjectModel
{
	private String name;


    public GeneFamilyDataset() throws Exception {
        this.setClassName("GeneFamilyDataset");
    }

    public String getName() throws Exception {
        return this.name;
    }

    public void setName(String name) throws Exception {
        this.name = name;
    }

    public Item save(Item score) throws ObjectStoreException, Exception {
        score.setAttribute("name", getName());
        return score;
    }
}