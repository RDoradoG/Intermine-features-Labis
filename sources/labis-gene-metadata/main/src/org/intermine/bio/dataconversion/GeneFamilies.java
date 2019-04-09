package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Gene Families
 * 
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class GeneFamilies extends NewObjectModel
{
	private String name;
    private TypeGeneFamily typeGeneFamily;
    private String linkTag;

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

	public Item save(Item score) throws ObjectStoreException, Exception {
        score.setAttribute("name", getName()); 
	    String link = setLink();
        if (link.isEmpty()) {
        	score.setAttributeToEmptyString("dbLink");
        } else {
	        score.setAttribute("dbLink", link);
        }
        score.setReference("type", getTypeId());
        return score;
	}

}