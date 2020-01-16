package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. Sao Paulo
 *
 * Described class of Experiment Conditions
 *
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class ExperimentCondition extends NewObjectModel
{
	private String name;
	private String description;
	private String instrument;
	private String strategy;
	private String source;
	private String selection;
	private String layout;
	private String srrNumber;

	public ExperimentCondition() throws Exception {
        this.setClassName("ExperimentConditions");
    }

    public void setName(String name) throws Exception {
    	this.name = name;
    }

    public String getName() throws Exception {
    	return this.name;
    }

	public void setDescription(String description) throws Exception {
		this.description = description;
	}

	public String getDescription() throws Exception {
		return this.description;
	}

	public void setInstrument(String instrument) throws Exception {
		this.instrument = instrument;
	}

	public String getInstrument() throws Exception {
		return this.instrument;
	}

	public void setStrategy(String strategy) throws Exception {
		this.strategy = strategy;
	}

	public String getStrategy() throws Exception {
		return this.strategy;
	}

	public void setSource(String source) throws Exception {
		this.source = source;
	}

	public String getSource() throws Exception {
		return this.source;
	}

	public void setSelection(String selection) throws Exception {
		this.selection = selection;
	}

	public String getSelection() throws Exception {
		return this.selection;
	}

	public void setLayout(String layout) throws Exception {
		this.layout = layout;
	}

	public String getLayout() throws Exception {
		return this.layout;
	}

	public void setSrrNumber(String srrNumber) throws Exception {
		this.srrNumber = srrNumber;
	}

	public String getSrrNumber() throws Exception {
		return this.srrNumber;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("name", getName());
		score.setAttribute("description", getDescription());
		score.setAttribute("instrument", getInstrument());
		score.setAttribute("strategy", getStrategy());
		score.setAttribute("source", getSource());
		score.setAttribute("selection", getSelection());
		score.setAttribute("layout", getLayout());
		score.setAttribute("srrNumber", getSrrNumber());
        return score;
	}
}