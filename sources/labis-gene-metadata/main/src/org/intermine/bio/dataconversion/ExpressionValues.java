package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2016 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */
import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

/**
 *
 * @author
 */
public class ExpressionValues extends NewObjectModel
{
	private String expressionValue;
	private ExperimentConditions experimentConditions;
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

	public void setExperimentConditions(ExperimentConditions experimentConditions) throws Exception {
		this.experimentConditions = experimentConditions;
	}

	public ExperimentConditions getExperimentConditions() throws Exception {
		return this.experimentConditions;
	}

	public String getExperimentConditionsId() throws Exception {
		return this.experimentConditions.getUniqueId();
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
		score.setReference("condition", getExperimentConditionsId());
		score.setReference("gene", getGeneId());
		score.setReference("experiment", getExperimentDescriptionId());
		score.setReference("type", getExpressionTypeDiccionaryId());
        return score;
	}

}
