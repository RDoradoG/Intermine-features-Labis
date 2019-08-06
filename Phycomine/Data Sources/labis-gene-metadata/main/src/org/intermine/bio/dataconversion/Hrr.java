package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Neighborhoods
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

public class Hrr extends NewObjectModel
{

	private Gene gene_a;
    private Gene gene_b;
    private String rank;
    private Boolean saveIt;
    private Hcca hcca                       = null;
    private Map<String, Gene> neighborhoods = new HashMap<String, Gene>();

	public Hrr() throws Exception {
        this.setClassName("Hrr");
    }

    public void setGeneA(Gene gene) throws Exception {
        this.gene_a = gene;
    }

    public Gene getGeneA() throws Exception {
        return this.gene_a;
    }

    public String getGeneAId() throws Exception {
        return this.gene_a.getUniqueId();
    }

    public void setGeneB(Gene gene) throws Exception {
        this.gene_b = gene;
    }

    public Gene getGeneB() throws Exception {
        return this.gene_b;
    }

    public String getGeneBId() throws Exception {
        return this.gene_b.getUniqueId();
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

    public Boolean hccaNull() throws Exception {
        return (this.hcca == null);
    }

    public void setRank(String rank) throws Exception {
    	this.rank = rank;
    }

    public String getRank() throws Exception {
    	return this.rank;
    }

    public void setSaveIt(Boolean saveIt) throws Exception {
        this.saveIt = saveIt;
    }

    public Boolean getSaveIt() throws Exception {
        return this.saveIt;
    }

    public void addNeighborhood(Gene gene) throws Exception {
        if (!this.neighborhoods.containsKey(gene.getUniqueId())) {
            this.neighborhoods.put(gene.getUniqueId(), gene);
        }
    }

    public Map<String, Gene> getNeighborhoods() throws Exception {
        return this.neighborhoods;
    }

    public List<String> getNeighborhoodsIds() throws Exception {
        List<String> refIds = new ArrayList<String>();
        Iterator it         = this.neighborhoods.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry e = (Map.Entry) it.next();
            refIds.add((String) e.getKey());
        }
        return refIds;
    }

    public Item save(Item score) throws ObjectStoreException, Exception {
        score.setReference("gene_a", getGeneAId());
        score.setReference("gene_b", getGeneBId());
        if (!hccaNull()) {
            score.setReference("hcca", getHccaId());
        }
		score.setAttribute("rank", this.getRank());
        score.setCollection("neighborhood", getNeighborhoodsIds());
        return score;
	}
}