package org.intermine.bio.dataconversion;

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

/**
 * <h1>Described class of Phenotypes</h1>
 * <p>
 * Labis - IQ, USP. São Paulo
 *
 * @author Rodrigo Dorado
 * @version 1.0
 * @since   2019-12-09
 */
public class Phenotype extends NewObjectModel
{
	private String phenotype;
	private MeasureType measureType;

	public Phenotype() throws Exception {
        this.setClassName("Phenotype");
    }

	public void setPhenotype(String phenotype) throws Exception {
		this.phenotype = phenotype;
	}

	public String getPhenotype() throws Exception {
		return this.phenotype;
	}

	public void setMeasureType(MeasureType measureType) throws Exception {
		this.measureType = measureType;
	}

	public MeasureType getMeasureType() throws Exception {
		return this.measureType;
	}

	public String getMeasureTypenId() throws Exception {
		return this.measureType.getUniqueId();
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("phenotype", getPhenotype());
		score.setReference("measureType", getMeasureTypenId());
        return score;
	}
}