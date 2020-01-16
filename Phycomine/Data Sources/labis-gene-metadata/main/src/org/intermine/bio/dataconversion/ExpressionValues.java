package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. Sao Paulo
 *
 * Described class of Expression Values
 *
 * @author Rodrigo Dorado
 */

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class ExpressionValues extends NewObjectModel
{
	private String expressionValue;
	private ExperimentCondition experimentCondition;
	private Gene gene;
	private ExperimentDescription experimentDescription;
	private ExpressionTypeDiccionary expressionTypeDiccionary;

	public ExpressionValues() throws Exception {
        this.setClassName("ExpressionValues");
    }

    public void setExpressionValue(String expressionValue) throws Exception {
    	this.expressionValue = expressionValue;
    }

    public String getExpressionValue() throws Exception {
    	return this.expressionValue;
    }

	public void setExperimentCondition(ExperimentCondition experimentCondition) throws Exception {
		this.experimentCondition = experimentCondition;
	}

	public ExperimentCondition getExperimentCondition() throws Exception {
		return this.experimentCondition;
	}

	public String getExperimentConditionId() throws Exception {
		return this.experimentCondition.getUniqueId();
	}

	public void setGene(Gene gene) throws Exception {
		this.gene = gene;
	}

	public Gene getGene() throws Exception {
		return this.gene;
	}

	public String getGeneId() throws Exception {
		return this.gene.getUniqueId();
	}

	public void setExperimentDescription(ExperimentDescription experimentDescription) throws Exception {
		this.experimentDescription = experimentDescription;
	}

	public ExperimentDescription getExperimentDescription() throws Exception {
		return this.experimentDescription;
	}

	public String getExperimentDescriptionId() throws Exception {
		return this.experimentDescription.getUniqueId();
	}

	public void setExpressionTypeDiccionary(ExpressionTypeDiccionary expressionTypeDiccionary) throws Exception {
		this.expressionTypeDiccionary = expressionTypeDiccionary;
	}

	public ExpressionTypeDiccionary getExpressionTypeDiccionary() throws Exception {
		return this.expressionTypeDiccionary;
	}

	public String getExpressionTypeDiccionaryId() throws Exception {
		return this.expressionTypeDiccionary.getUniqueId();
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("expressionValue", getExpressionValue());
		score.setReference("condition", getExperimentConditionId());
		score.setReference("gene", getGeneId());
		score.setReference("experiment", getExperimentDescriptionId());
		score.setReference("type", getExpressionTypeDiccionaryId());
        return score;
	}
}
