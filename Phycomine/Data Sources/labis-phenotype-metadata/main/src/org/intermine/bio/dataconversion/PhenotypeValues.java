package org.intermine.bio.dataconversion;

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

/**
 * <h1>Described class of Phenotype values</h1>
 * <p>
 * Labis - IQ, USP. São Paulo
 *
 * @author Rodrigo Dorado
 * @version 1.0
 * @since   2019-12-09
 */
public class PhenotypeValues extends NewObjectModel
{
	private String time;
	private String phenotypeValue;

	private String phenotypeValueSD;

	private TimeMeasure timeMeasures;
	private Phenotype phenotype;
	private Measure measure;

	private ExperimentCondition experimentCondition;
	private ExperimentDescription experimentDescription;
	private ExpressionTypeDiccionary expressionTypeDiccionary; 

	private Organism organism;

	public PhenotypeValues() throws Exception {
        this.setClassName("PhenotypeValues");
    }

	public void setTime(String time) throws Exception {
		this.time = time;
	}

	public String getTime() throws Exception {
		return this.time;
	}

	public void setPhenotypeValue(String phenotypeValue) throws Exception {
		this.phenotypeValue = phenotypeValue;
	}

	public String getPhenotypeValue() throws Exception {
		return this.phenotypeValue;
	}

	public void setPhenotypeValueSD(String phenotypeValueSD) throws Exception {
		this.phenotypeValueSD = phenotypeValueSD;
	}

	public String getPhenotypeValueSD() throws Exception {
		return this.phenotypeValueSD;
	}

	public void setTimeMeasure(TimeMeasure timeMeasures) throws Exception {
		this.timeMeasures = timeMeasures;
	}

	public TimeMeasure getTimeMeasure() throws Exception {
		return this.timeMeasures;
	}

	public String getTimeMeasureId() throws Exception {
		return this.timeMeasures.getUniqueId();
	}

	public void setPhenotype(Phenotype phenotype) throws Exception {
		this.phenotype = phenotype;
	}

	public Phenotype getPhenotype() throws Exception {
		return this.phenotype;
	}

	public String getPhenotypeId() throws Exception {
		return this.phenotype.getUniqueId();
	}

	public void setMeasure(Measure measure) throws Exception {
		this.measure = measure;
	}

	public Measure getMeasure() throws Exception {
		return this.measure;
	}

	public String getMeasureId() throws Exception {
		return this.measure.getUniqueId();
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

	public void setExperimentCondition(ExperimentCondition experimentCondition) throws Exception {
		this.experimentCondition = experimentCondition;
	}

	public ExperimentCondition getExperimentCondition() throws Exception {
		return this.experimentCondition;
	}

	public String getExperimentConditionId() throws Exception {
		return this.experimentCondition.getUniqueId();
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
		score.setAttribute("phenotypeValue", getPhenotypeValue());
		score.setAttribute("phenotypeValueSD", getPhenotypeValueSD());
		score.setAttribute("time", getTime());
		score.setReference("timeMeasure", getTimeMeasureId());
		score.setReference("phenotype", getPhenotypeId());
		score.setReference("measure", getMeasureId());
		score.setReference("condition", getExperimentConditionId());
		score.setReference("experiment", getExperimentDescriptionId());
		score.setReference("type", getExpressionTypeDiccionaryId());
		score.setReference("organism", getOrganismId());
        return score;
	}
}