package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Genomic Proteins
 * 
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class GenomicProtein extends NewObjectModel
{
	private String primaryIdentifier;
	private String molecularWeight;
	private String length;
	private Sequence sequence;
	private Organism organism;
	private Map<String, Gene> genes         = new HashMap<String, Gene>();
	private MolecularWeight sequenceWeight = new MolecularWeight();

	public GenomicProtein() throws Exception {
        this.setClassName("GenomicProtein");
    }

    public void setPrimaryIdentifier(String primaryIdentifier) throws Exception {
    	this.primaryIdentifier = primaryIdentifier;
    }

    public String getPrimaryIdentifier() throws Exception {
    	return this.primaryIdentifier;
    }

    public void setMolecularWeight(String molecularWeight) throws Exception {
    	this.molecularWeight = molecularWeight;
    }

	public String getMolecularWeight() throws Exception {
		return this.molecularWeight;
	}

	public void setMolecularWeight() throws Exception {
		this.sequenceWeight.calculateWeight();
		String weight = this.sequenceWeight.getMolecularWeight();
		this.setMolecularWeight(weight);
    }

	public void setLength(String length) throws Exception {
		this.length = length;
	}

	public void setLength() throws Exception {
		Sequence sequence = getSequence();
		this.setLength(sequence.getLength());
	}

	public String getLength() throws Exception {
		return this.length;
	}

	public void setSequence(Sequence sequence) throws Exception {
		this.sequence = sequence;
		this.sequenceWeight.setSequence(sequence);
	}

	public Sequence getSequence() throws Exception {
		return this.sequence;
	}

	public String getSequenceId() throws Exception {
		return this.sequence.getUniqueId();
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

	public void addGene(Gene gene) throws Exception {
		if (!genes.containsKey(gene.getUniqueId())) {
			genes.put(gene.getUniqueId(), gene);
		}
	}

	public Map<String, Gene> getGenes() throws Exception {
		return this.genes;
	}

	public List<String> getGenesIds() throws Exception {
		List<String> refIds = new ArrayList<String>();
		Iterator it = this.genes.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry e = (Map.Entry) it.next();
		    refIds.add((String) e.getKey());
		}
		return refIds;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("primaryIdentifier", getPrimaryIdentifier());
		score.setAttribute("molecularWeight", getMolecularWeight());
        score.setAttribute("length", getLength());
        score.setReference("sequence", getSequenceId());
        score.setReference("organism", getOrganismId());
        score.setCollection("genes", getGenesIds());
        return score;
	}
}