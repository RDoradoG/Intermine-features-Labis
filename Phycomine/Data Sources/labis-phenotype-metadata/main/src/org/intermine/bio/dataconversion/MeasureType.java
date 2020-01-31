package org.intermine.bio.dataconversion;

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

/**
 * <h1>Described class of Measure Types</h1>
 * <p>
 * Labis - IQ, USP. São Paulo
 *
 * @author Rodrigo Dorado
 * @version 1.0
 * @since   2019-12-09
 */
public class MeasureType extends NewObjectModel
{
	private String measureType;

	public MeasureType() throws Exception {
        this.setClassName("MeasureType");
    }

	public void setMeasureType(String measureType) throws Exception {
		this.measureType = measureType;
	}

	public String getMeasureType() throws Exception {
		return this.measureType;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("measureType", getMeasureType());
        return score;
	}
}