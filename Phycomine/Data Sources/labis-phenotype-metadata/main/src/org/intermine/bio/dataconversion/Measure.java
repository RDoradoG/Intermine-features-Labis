package org.intermine.bio.dataconversion;

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

/**
 * <h1>Described class of Measures</h1>
 * <p>
 * Labis - IQ, USP. São Paulo
 *
 * @author Rodrigo Dorado
 * @version 1.0
 * @since   2019-12-09
 */
public class Measure extends NewObjectModel
{
	private String measure;
	private String rate;
	private String root;
	private MeasureType measureType;


	public Measure() throws Exception {
        this.setClassName("Measure");
    }

	public void setMeasure(String measure) throws Exception {
		this.measure = measure;
	}

	public String getMeasure() throws Exception {
		return this.measure;
	}

	public void setRate(String rate) throws Exception {
		this.rate = rate;
	}

	public String getRate() throws Exception {
		return this.rate;
	}

	public void setRoot(String root) throws Exception {
		this.root = root;
	}

	public String getRoot() throws Exception {
		return this.root;
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
		score.setAttribute("measure", getMeasure());
		score.setAttribute("rate", getRate());
		score.setAttribute("root", getRoot());
		score.setReference("measureType", getMeasureTypenId());
        return score;
	}
}