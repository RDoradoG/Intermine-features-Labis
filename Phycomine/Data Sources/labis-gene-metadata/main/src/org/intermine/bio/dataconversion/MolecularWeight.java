package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Molecular Weight
 *
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.lang.*;

public class MolecularWeight
{
    private static final String[] amenoacidNames              = {"A", "C", "D", "E", "F", "G", "H", "I", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "V", "W", "Y"};
    private static final Double[] amenoacidWeights            = {71.03711, 103.00919, 115.02694, 129.04259, 147.06841, 57.02146, 137.05891, 113.08406, 128.09496, 113.08406, 131.04049, 114.04293, 97.05276, 128.05858, 156.10111, 87.03203, 101.04768, 99.06841, 186.07931, 163.06333};
    private Map<String, Double> amenoacidsWeights             = new HashMap<String, Double>();
    private static final String[] nonstandardAminoacidnNames  = {"U", "O"};
    private static final Double[] nonstandardAminoacidWeights = {150.953636, 237.147727};
    private  Map<String, Double> nonstandardAminoacidsWeights = new HashMap<String, Double>();
	private Sequence sequence;
    private String molecularWeight;

	public MolecularWeight() throws Exception {
        this.setMaps(false);
        this.setMaps(true);
        amenoacidsWeights.put("X", this.calculateX());
    }

    public void setSequence(Sequence sequence) throws Exception {
    	this.sequence = sequence;
    }

    public Sequence getSequence() throws Exception {
    	return this.sequence;
    }

    public void setMolecularWeight(String molecularWeight) throws Exception {
    	this.molecularWeight = molecularWeight;
    }

    public String getMolecularWeight() throws Exception {
    	return this.molecularWeight;
    }

    private String getResidues() throws Exception {
    	return this.sequence.getResidues();
    }

    public void calculateWeight() throws Exception {
    	String weight = this.calculateWeightString();
    	this.setMolecularWeight(weight);
    }


    private String calculateWeightString() throws Exception {
    	Integer weightDou = this.calculateWeightDouble();
    	return String.valueOf(weightDou);
    }

    private Integer calculateWeightDouble() throws Exception {
        Double weight = 0.0;
        String seq    = this.getResidues();
    	for (int i = 0; i < seq.length(); i++){
            char a   = seq.charAt(i);
            Double w = this.getWeightofAminoacid(a);
            weight   += w;
		}
		return (int) Math.round(weight);
    }

    private Double getWeightofAminoacid(char aminoacid) throws Exception {
    	String a = String.valueOf(aminoacid);
    	if (amenoacidsWeights.containsKey(a)) {
    		return amenoacidsWeights.get(a);
    	}

		if (nonstandardAminoacidsWeights.containsKey(a)) {
    		return nonstandardAminoacidsWeights.get(a);
    	}

    	return 0.0;
    }

    private void setMaps(Boolean nonStandard) throws Exception {
        if (nonStandard) {
	        for (int i = 0; i < this.amenoacidNames.length; i++) {
	        	nonstandardAminoacidsWeights.put(this.amenoacidNames[i], this.amenoacidWeights[i]);
	        }
        } else {
        	for (int i = 0; i < this.nonstandardAminoacidnNames.length; i++) {
	        	amenoacidsWeights.put(this.nonstandardAminoacidnNames[i], this.nonstandardAminoacidWeights[i]);
	        }
        }
    }

    private Double calculateX() throws Exception {
    	Double sum = 0.0;
    	for (int i = 0; i < amenoacidWeights.length; i++) {
    		sum += amenoacidWeights[i];
    	}
    	return sum / amenoacidWeights.length;
    }
}


