package org.intermine.bio.dataconversion;

import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.Random;
import java.util.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.ant.BuildException;
import org.intermine.dataconversion.ItemWriter;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.util.FormattedTextParser;
import org.intermine.xml.full.Item;

/**
 * 
 * @author
 */
public class LabisPhenotypeMetadataConverter extends BioFileConverter
{

    private static final String DATASET_TITLE                                = "LabisPhenotypeMetadata";
    private static final String DATA_SOURCE_NAME                             = "labis-phenotype-metadata";
    private static final String LOGFILE                                      = "/home/rdorado/logs/MyJavaLogPhenotype.log"; //Change to another file that already exists
    private static final String NA_VALUE                                     = "NA";
    private ErrorLog loggable;
    private Iterator<?> tsvIter;
    private static Logger LOGGER                                             = null;
    private Map<String, ExpressionTypeDiccionary> expressionTypeDiccionaries = new HashMap<String, ExpressionTypeDiccionary>();
    private Map<String, TimeMeasure> timeMeasures                            = new HashMap<String, TimeMeasure>();
    private Map<String, MeasureType> measureTypes                            = new HashMap<String, MeasureType>();
    private Map<String, Measure> measures                                    = new HashMap<String, Measure>();
    private Map<String, Phenotype> phenotypes                                = new HashMap<String, Phenotype>();
    private Map<String, Organism> organisms                                  = new HashMap<String, Organism>();
    private Map<String, Publication> pubItems                                = new HashMap<String, Publication>();
    private Map<String, ExperimentDescription> experimentDescriptions        = new HashMap<String, ExperimentDescription>();
    private Map<String, ExperimentCondition> experimentConditions            = new HashMap<String, ExperimentCondition>();
    private String[] prefixFileName                                          = {"A-ExpressionTypeDiccionary",
                                                                                "B-Time-Measures",
                                                                                "C-Measures-Types",
                                                                                "D-Measures",
                                                                                "E-Phenotypes",
                                                                                "F-Organisms",
                                                                                "G-ExperimentDescription",
                                                                                "H-ExperimentDescriptionColumns-",
                                                                                "I-PhenotypesValues-"};

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public LabisPhenotypeMetadataConverter(ItemWriter writer, Model model) throws IOException {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
        loggable = new ErrorLog(LOGFILE);
    }

    /**
     * Set tge organisms inside the meta data.
     * @param taxonIds the taxon ids of the organisms
     */
    /*public void setMetadataLocalOrganisms(String taxonIds) throws Exception {
        String[] aTaxonIds = taxonIds.split(",");
        for (int i = 0; i < aTaxonIds.length; i++) {
            setLocalOrganism(aTaxonIds[i]);
        }
    }*/

    /**
     * Set tge organisms inside the meta data.
     * @param taxonIds the taxon ids of the organisms
     */
    /*public void setMetadataExternalOrganisms(String taxonIds) throws Exception {
        String[] aTaxonIds = taxonIds.split(",");
        for (int i = 0; i < aTaxonIds.length; i++) {
            setExternalOrganism(aTaxonIds[i]);
        }
    }*/

    /*public void setLocalOrganism(String taxonId) throws Exception {
        if (!organisms.containsKey(taxonId)) {
            Organism organism = new Organism(taxonId);
            Item score        = createItem(organism.getClassName());
            score             = organism.save(score);
            store(score);
            organism.setUniqueId(score.getIdentifier());
            organisms.put(taxonId, organism);
        }
    }*/

    /**
     * 
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {
        File currentFile = getCurrentFile();
        String fileName  = currentFile.getName();

        loggable.setActualFile(fileName);
        loggable.makeLog("Start (" + fileName + ")");

        try {
            this.tsvIter = FormattedTextParser.parseTabDelimitedReader(reader);
        } catch (Exception e) {
            throw new BuildException("cannot parse file: " + getCurrentFile(), e);
        }

        int opt = getOptionFileName(fileName);

        switch(opt) {
            case 1:
                ExpressionTypeDiccionaryExecute();
            break;

            case 2:
                TimeMeasureExecute();
            break;

            case 3:
                MeasureTypesExecute();
            break;

            case 4:
                MeasureExecute();
            break;

            case 5:
                PhenotypesExecute();
            break;

            case 6:
                OrganismsExecute();
            break;

            case 7:
                ExperimentDescriptionExecute();
            break;

            case 8:
                ExperimentDescriptionColumnsExecute();
            break;

            case 9:
                PhenotypesValuesExecute();
            break;

            default:
                loggable.makeErrorLog("Name of the file invalid.");
            break;
        }

        loggable.makeLog("End (" + fileName + ")");
    }

    /**
     * Get the type of the file, what information is inside the file, this depends of the initial letter of the file.
     * @param  fileName The name of the file
     * @return          The option of the type of file.
     */
    private int getOptionFileName(String fileName) {
        for (int i = 0; i < this.prefixFileName.length; i++) {
            if (fileName.indexOf(this.prefixFileName[i]) == 0) {
                return i + 1;
            }
        }
        return 0;
    }

    /**
     * Watch if the file has headers.
     * @return Boolean, true if it has header.
     * @throws Exception    
     */
    private Boolean watchHeader() throws Exception {
        int end = 0;
        if (this.tsvIter.hasNext()) {
            String[] line = (String[]) this.tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
                return false;
            } else {
                if (!StringUtils.isBlank(line[0])) {
                    return true;
                }
            }
        }

        return false;
    }

    private String getColumn(int i, String[] line, String defaultValue) throws Exception {
        return (line.length > i) ? line[i] : defaultValue;
    }

    /**
     * Create several publications.
     * @param  pubIds               The id of the publications.
     * @return                      All the publications created.
     * @throws ObjectStoreException 
     * @throws Exception            
     */
    private Publication[] createPublications(String[] pubIds) throws ObjectStoreException, Exception {
        Publication[] publications = new Publication[pubIds.length];
        for (int i = 0; i < pubIds.length; i++) {
            publications[i] = createPublication(pubIds[i]);
        }
        return publications;
    }

    //---- Readers -----//

    /**
     * Get the expression types of the file.
     * @throws Exception
     */
    private void ExpressionTypeDiccionaryExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line = (String[]) this.tsvIter.next();
                String name   = getColumn(0, line, "");
                if (!StringUtils.isBlank(name)) {
                    saveExpressionType(name);
                } else {
                    loggable.makeErrorLog("The experiment type is empty.");
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void TimeMeasureExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line = (String[]) this.tsvIter.next();
                String measure = getColumn(0, line, "");
                String rate = getColumn(1, line, "");
                String root = getColumn(2, line, "");
                if (StringUtils.isBlank(root)) {
                    root = "0";
                }
                if (!StringUtils.isBlank(measure) && !StringUtils.isBlank(rate)) {
                    saveTimeMeasure(measure, rate, root);
                } else {
                    loggable.makeErrorLog("The time measure or the rate are empty.");
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void MeasureTypesExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line = (String[]) this.tsvIter.next();
                String measureType = getColumn(0, line, "");
                if (!StringUtils.isBlank(measureType)) {
                    saveMeasureType(measureType);
                } else {
                    loggable.makeErrorLog("The measure Type is empty.");
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void MeasureExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line = (String[]) this.tsvIter.next();
                String measure = getColumn(0, line, "");
                String type = getColumn(1, line, "");
                String rate = getColumn(2, line, "");
                String root = getColumn(3, line, "");
                if (StringUtils.isBlank(root)) {
                    root = "0";
                }
                if (!StringUtils.isBlank(measure) && !StringUtils.isBlank(type) && !StringUtils.isBlank(rate)) {
                    saveMeasure(measure, type, rate, root);
                } else {
                    loggable.makeErrorLog("The measure or/and Type or/and rate are empty.");
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void PhenotypesExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line = (String[]) this.tsvIter.next();
                String phenotype = getColumn(0, line, "");
                String type = getColumn(1, line, "");
                if (!StringUtils.isBlank(phenotype) && !StringUtils.isBlank(type)) {
                    savePhenotype(phenotype, type);
                } else {
                    loggable.makeErrorLog("The phenotype or/and Type are empty.");
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void OrganismsExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line = (String[]) this.tsvIter.next();
                String taxonId = getColumn(0, line, "");
                if (!StringUtils.isBlank(taxonId)) {
                    saveOrganism(taxonId);
                } else {
                    loggable.makeErrorLog("The taxon Id is empty.");
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    /**
     * Get the experiments information of the file.
     * @throws Exception
     */
    private void ExperimentDescriptionExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line          = (String[]) this.tsvIter.next();
                String name            = getColumn(0, line, "");
                String pubText         = getColumn(1, line, "");
                String Accession       = getColumn(2, line, "-");
                String experimentTitle = getColumn(3, line, "-");
                String Description     = getColumn(4, line, "-");
                if (!StringUtils.isBlank(name)) {
                    String[] pubIds;
                    if (StringUtils.isBlank(pubText)) {
                        pubIds = new String[0];
                    } else {
                        pubIds = pubText.split(",");
                    }
                    Publication[] pubMedId   = createPublications(pubIds);
                    saveExperimentDescription(name, pubMedId, Accession, experimentTitle, Description);
                } else {
                    loggable.makeErrorLog("Experiment without name");
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    /**
     * Get all the information of the conditions of the file been reading.
     * @throws Exception
     */
    private void ExperimentDescriptionColumnsExecute() throws Exception {
        if (watchHeader()) {
            while (this.tsvIter.hasNext()) {
                String[] line      = (String[]) this.tsvIter.next();
                String experiment  = getColumn(0, line, "");
                String name        = getColumn(1, line, "");
                String description = getColumn(2, line, "-");
                String instrument  = getColumn(3, line, "-");
                String strategy    = getColumn(4, line, "-");
                String source      = getColumn(5, line, "-");
                String selection   = getColumn(6, line, "-");
                String layout      = getColumn(7, line, "-");
                String srrNumber   = getColumn(8, line, "-");
                if (!StringUtils.isBlank(name) && !StringUtils.isBlank(experiment)) {
                    saveExperimentDescriptionColumns(name, description, instrument, strategy, source, selection, layout, experiment, srrNumber);
                } else {
                    loggable.makeErrorLog("The experiment name or the name of the condition are empty: experiment: " + experiment + "; condition: " + name);
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void PhenotypesValuesExecute() throws Exception {
        String [] heads = null;
        int end         = 0;

        if (this.tsvIter.hasNext()) {
            String[] line = (String[]) this.tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            } else {
                for (int i = 0; i < line.length; i++) {
                    if (StringUtils.isBlank(line[i])) {
                        break;
                    }
                    end++;
                }
                heads = new String[end];
                System.arraycopy(line, 0, heads, 0, end);
            }
        } else {
           loggable.makeErrorLog("File Empty of data.");
        }

        while (this.tsvIter.hasNext()) {
            String[] line      = (String[]) this.tsvIter.next();
            int sizeLine       = line.length;
            String taxonId     = getColumn(0, line, "");
            String phenotype   = getColumn(1, line, "");
            String experiment  = getColumn(2, line, "");
            String type        = getColumn(3, line, "");
            String time        = getColumn(4, line, "");
            String timeMeasure = getColumn(5, line, "");
            String measure     = getColumn(6, line, "");
            if (!StringUtils.isBlank(taxonId) && !StringUtils.isBlank(phenotype)  && !StringUtils.isBlank(experiment)  && !StringUtils.isBlank(type)  && !StringUtils.isBlank(time)  && !StringUtils.isBlank(timeMeasure) && !StringUtils.isBlank(measure)) {
                for (int i = 7; i < end; i = i + 2) {
                    if (i >= sizeLine) {
                        break;
                    }
                    if ((i + 1) >= sizeLine) {
                        loggable.makeErrorLog("Is an incomplete line, the data does not have SD");
                        break;
                    }
                    SavePhenotypesValues(line[i], line[i + 1], heads[i], taxonId, phenotype, experiment, type, time, timeMeasure, measure);
                }
            } else {
                loggable.makeErrorLog("Is an incomplete line");
            }
        }
    }

    //---- Savers -----//

    /**
     * Save a type of experiment.
     * @param  name                 The type of experiment
     * @throws ObjectStoreException
     * @throws Exception
     */
    private void saveExpressionType(String name) throws ObjectStoreException, Exception {
        if (!expressionTypeDiccionaries.containsKey(name)) {
            ExpressionTypeDiccionary expressionTypeDiccionary = new ExpressionTypeDiccionary();
            expressionTypeDiccionary.setName(name);
            Item score = createItem(expressionTypeDiccionary.getClassName());
            score      = expressionTypeDiccionary.save(score);
            store(score);
            expressionTypeDiccionary.setUniqueId(score.getIdentifier());
            expressionTypeDiccionaries.put(name, expressionTypeDiccionary);
        } else {
            loggable.makeErrorLog("The experiment type: " + name + " already exists.");
        }
    }

    private void saveTimeMeasure(String measure, String rate, String root) throws ObjectStoreException, Exception {
        if (!timeMeasures.containsKey(measure)) {
            TimeMeasure timeMeasure = new TimeMeasure();
            timeMeasure.setTimeMeasure(measure);
            timeMeasure.setRate(rate);
            timeMeasure.setRoot(root);
            Item score = createItem(timeMeasure.getClassName());
            score      = timeMeasure.save(score);
            store(score);
            timeMeasure.setUniqueId(score.getIdentifier());
            timeMeasures.put(measure, timeMeasure);
        } else {
            loggable.makeErrorLog("The time measure: " + measure + " already exists.");
        }
    }

    private void saveMeasureType(String type) throws ObjectStoreException, Exception {
        if (!measureTypes.containsKey(type)) {
            MeasureType measureType = new MeasureType();
            measureType.setMeasureType(type);
            Item score = createItem(measureType.getClassName());
            score      = measureType.save(score);
            store(score);
            measureType.setUniqueId(score.getIdentifier());
            measureTypes.put(type, measureType);
        } else {
            loggable.makeErrorLog("The measure type: " + type + " already exists.");
        }
    }

    private void saveMeasure(String name, String type, String rate, String root) throws ObjectStoreException, Exception {
        if (!measures.containsKey(name)) {
            Measure measure = new Measure();
            measure.setMeasure(name);
            measure.setRate(rate);
            measure.setRoot(root);
            MeasureType measureType = measureTypes.get(type);
            if (measureType != null) {
                measure.setMeasureType(measureType);
                Item score = createItem(measure.getClassName());
                score      = measure.save(score);
                store(score);
                measure.setUniqueId(score.getIdentifier());
                measures.put(name, measure);
            }
        } else {
            loggable.makeErrorLog("The measure: " + name + " already exists.");
        }
    }

    private void savePhenotype(String name, String type) throws ObjectStoreException, Exception {
        if (!phenotypes.containsKey(name)) {
            Phenotype phenotype = new Phenotype();
            phenotype.setPhenotype(name);
            MeasureType measureType = verifyMeasureType(type);
            if (measureType != null) {
                phenotype.setMeasureType(measureType);
                Item score = createItem(phenotype.getClassName());
                score      = phenotype.save(score);
                store(score);
                phenotype.setUniqueId(score.getIdentifier());
                phenotypes.put(name, phenotype);
            }
        } else {
            loggable.makeErrorLog("The phenotype: " + name + " already exists.");
        }
    }

    private void saveOrganism(String taxonId) throws ObjectStoreException, Exception {
        if (!organisms.containsKey(taxonId)) {
            Organism organism = new Organism();
            organism.setTaxonId(taxonId);
            Item score = createItem(organism.getClassName());
            score      = organism.save(score);
            store(score);
            organism.setUniqueId(score.getIdentifier());
            organisms.put(taxonId, organism);
        } else {
            loggable.makeErrorLog("The taxon id: " + taxonId + " already exists.");
        }
    }

    /**
     * Create a new publication if not exists.
     * @param  pubId                The id of the publication.
     * @return                      The publication created.
     * @throws ObjectStoreException 
     * @throws Exception            
     */
    private Publication createPublication(String pubId) throws ObjectStoreException, Exception {
        if (!pubItems.containsKey(pubId)) {
            Publication publication = new Publication();
            publication.setPubMedId(pubId);
            Item score  = createItem(publication.getClassName());
            score       = publication.save(score);
            store(score);
            publication.setUniqueId(score.getIdentifier());
            pubItems.put(pubId, publication);
            return publication;
        } else {
            return pubItems.get(pubId);
        }
    }

    /**
     * Save a experiment.
     * @param  name                 The name of the experiment.
     * @param  pubMedIds            The id of the publication associated to the experiment.
     * @param  accession            The accession number.
     * @param  experimentTitle      The title of the experiment.
     * @param  description          The description of the experiment.
     * @throws ObjectStoreException
     * @throws Exception
     */
    private void saveExperimentDescription(String name, Publication[] pubMedIds, String accession, String experimentTitle, String description) throws ObjectStoreException, Exception {
        if (!experimentDescriptions.containsKey(name)) {
            ExperimentDescription experimentDescription = new ExperimentDescription();
            experimentDescription.setName(name);
            experimentDescription.setDescription(description);
            experimentDescription.setExperimentTitle(experimentTitle);
            experimentDescription.setAccession(accession);
            for(int i = 0; i < pubMedIds.length; i++) {
                experimentDescription.addPublication(pubMedIds[i]);
            }
            Item score = createItem(experimentDescription.getClassName());
            score      = experimentDescription.save(score);
            store(score);
            experimentDescription.setUniqueId(score.getIdentifier());
            experimentDescriptions.put(name, experimentDescription);
        } else {
            loggable.makeErrorLog("The experiment: " + name + " already exists.");
        }
    }

    /**
     * Save a new condition of a experiment.
     * @param  name                 The name of the condition.
     * @param  description          The description of the condition.
     * @param  instrument           The instrument used in the condition.
     * @param  strategy             The strategy used in the condition.
     * @param  source               The sources of the condition.
     * @param  selection            The selection of the condition.
     * @param  layout               The layout of the condition.
     * @param  primaryIdExperiment  The id of the experiment associated.
     * @param  srrNumber            The SRR number associated to the condition.
     * @throws ObjectStoreException
     * @throws Exception
     */
    private void saveExperimentDescriptionColumns(String name, String description, String instrument, String strategy, String source, String selection, String layout, String primaryIdExperiment, String srrNumber) throws ObjectStoreException, Exception {
        if (!experimentConditions.containsKey(name)) {
            ExperimentCondition experimentCondition = new ExperimentCondition();
            experimentCondition.setName(name);
            experimentCondition.setDescription(description);
            experimentCondition.setInstrument(instrument);
            experimentCondition.setStrategy(strategy);
            experimentCondition.setSource(source);
            experimentCondition.setSelection(selection);
            experimentCondition.setLayout(layout);
            experimentCondition.setSrrNumber(srrNumber);
            Item score = createItem(experimentCondition.getClassName());
            score      = experimentCondition.save(score);
            store(score);
            experimentCondition.setUniqueId(score.getIdentifier());
            experimentConditions.put(name, experimentCondition);
        } else {
            loggable.makeErrorLog("The condition: " + name + " already exists.");
        }
    }

    private void SavePhenotypesValues(String value, String valueSD, String condition, String taxonId, String phenotypeName, String experiment, String type, String time, String measureTime, String measureUsed) throws ObjectStoreException, Exception {
        if (!value.equals(NA_VALUE)) {
            PhenotypeValues phenotypeValue                     = new PhenotypeValues();
            phenotypeValue.setPhenotypeValue(value);
            phenotypeValue.setPhenotypeValueSD(valueSD);
            //phenotypeValue.setTime(Float.parseFloat(time));
            phenotypeValue.setTime(time);
            ExperimentCondition experimentCondition            = verifyExperimentCondition(condition);
            Organism organism                                  = verifyOrganism(taxonId);
            Phenotype phenotype                                = verifyPhenotype(phenotypeName);
            ExperimentDescription experimentDescription        = verifyExperimentDescription(experiment);
            ExpressionTypeDiccionary expressionTypeDiccionarie = verifyExpressionTypeDiccionary(type);
            TimeMeasure timeMeasure                            = verifyTimeMeasure(measureTime);
            Measure measure                                    = verifyMeasure(measureUsed);
            if (experimentCondition != null && organism != null && phenotype != null && experimentDescription != null && expressionTypeDiccionarie != null && timeMeasure != null && measure != null) {
                phenotypeValue.setExperimentCondition(experimentCondition);
                phenotypeValue.setOrganism(organism);
                phenotypeValue.setPhenotype(phenotype);
                phenotypeValue.setExperimentDescription(experimentDescription);
                phenotypeValue.setExpressionTypeDiccionary(expressionTypeDiccionarie);
                phenotypeValue.setTimeMeasure(timeMeasure);
                phenotypeValue.setMeasure(measure);
                Item score = createItem(phenotypeValue.getClassName());
                score      = phenotypeValue.save(score);
                store(score);
            } else {
                loggable.makeErrorLog("Phenotype value incomplete.");
            }
        } else {
            loggable.makeErrorLog("NA value does not enter to the data base.");
        }
    }

    // --- get objects --- //
    
    private MeasureType verifyMeasureType(String type) throws Exception {
        if (measureTypes.containsKey(type)) {
            return measureTypes.get(type);
        } else {
            loggable.makeErrorLog("The measure type: " + type + " does not exist.");
            return null;
        }
    }

    private ExperimentCondition verifyExperimentCondition(String name) throws Exception {
        if (experimentConditions.containsKey(name)) {
            return experimentConditions.get(name);
        } else {
            loggable.makeErrorLog("The condition: " + name + " does not exist.");
            return null;
        }
    }

    private Organism verifyOrganism(String taxonId) throws Exception {
        if (organisms.containsKey(taxonId)) {
            return organisms.get(taxonId);
        } else {
            loggable.makeErrorLog("The organism: " + taxonId + " does not exist.");
            return null;
        }
    }

    private Phenotype verifyPhenotype(String name) throws Exception {
        if (phenotypes.containsKey(name)) {
            return phenotypes.get(name);
        } else {
            loggable.makeErrorLog("The phenotype: " + name + " does not exist.");
            return null;
        }
    }

    private ExperimentDescription verifyExperimentDescription(String name) throws Exception {
        if (experimentDescriptions.containsKey(name)) {
            return experimentDescriptions.get(name);
        } else {
            loggable.makeErrorLog("The experiment: " + name + " does not exist.");
            return null;
        }
    }

    private ExpressionTypeDiccionary verifyExpressionTypeDiccionary(String name) throws Exception {
        if (expressionTypeDiccionaries.containsKey(name)) {
            return expressionTypeDiccionaries.get(name);
        } else {
            loggable.makeErrorLog("The expression type: " + name + " does not exist.");
            return null;
        }
    }

    private TimeMeasure verifyTimeMeasure(String measure) throws Exception {
        if (timeMeasures.containsKey(measure)) {
            return timeMeasures.get(measure);
        } else {
            loggable.makeErrorLog("The time measures: " + measure + " does not exist.");
            return null;
        }
    }

    private Measure verifyMeasure(String measure) throws Exception {
        if (measures.containsKey(measure)) {
            return measures.get(measure);
        } else {
            loggable.makeErrorLog("The measures: " + measure + " does not exist.");
            return null;
        }
    }
}
