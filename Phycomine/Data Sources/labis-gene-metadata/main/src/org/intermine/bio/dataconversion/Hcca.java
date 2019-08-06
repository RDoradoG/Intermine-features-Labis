package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Gene Network Clusters
 *
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class Hcca extends NewObjectModel
{

	private String cluster;

	public Hcca(String cluster) throws Exception {
        this.setClassName("Hcca");
        this.setCluster(cluster);
    }

    public void setCluster(String cluster) throws Exception {
    	this.cluster = cluster;
    }

    public String getCluster() throws Exception {
    	return this.cluster;
    }

    public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("cluster", this.getCluster());
        return score;
	}
}