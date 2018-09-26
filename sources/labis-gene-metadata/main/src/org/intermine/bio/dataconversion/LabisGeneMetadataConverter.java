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
public class LabisGeneMetadataConverter extends BioFileConverter
{
    private static final String DATASET_TITLE                         = "LabisGeneMetadata";
    private static final String DATA_SOURCE_NAME                      = "labis-gene-metadata";
    private static final String LOGFILE                               = "/home/rdorado/logs/MyJavaLog.log";

    private ErrorLog loggable;

    private static Logger LOGGER                                      = null;

    private Map<String, Gene> geneItems                               = new HashMap<String, Gene>();
    private Map<String, ExperimentDescription> experimentItems        = new HashMap<String, ExperimentDescription>();
    private Map<String, ExpressionTypeDiccionary> typeDiccionaryItmes = new HashMap<String, ExpressionTypeDiccionary>();

    private Map<String, ExperimentConditions> verifyConditionItems    = new HashMap<String, ExperimentConditions>();
    private Map<String, Publication> pubItems                         = new HashMap<String, Publication>();
    private Map<String, String> familiesItems                         = new HashMap<String, String>();
    private Map<String, GeneFamilies> newFamiliesItems                = new HashMap<String, GeneFamilies>();
    private Map<String, String> familiesGeneItems                     = new HashMap<String, String>();
    private Map<String, TypeGeneFamily> geneFamiliesType              = new HashMap<String, TypeGeneFamily>();

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public LabisGeneMetadataConverter(ItemWriter writer, Model model) throws IOException {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
        loggable = new ErrorLog(LOGFILE);
    }

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

        Iterator<?> tsvIter;
        try {
            tsvIter = FormattedTextParser.parseTabDelimitedReader(reader);
        } catch (Exception e) {
            throw new BuildException("cannot parse file: " + getCurrentFile(), e);
        }

        int opt = getOptionFileName(fileName);

        switch(opt) {
            case 1:
                ExperimentDescriptionExecute(tsvIter);
            break;
            case 2:
                ExpressionTypeDiccionaryExecute(tsvIter);
            break;
            case 3:
                ExperimentDescriptionColumnsExecute(tsvIter);
            break;
            case 4:
                ExpressionValuesExecute(tsvIter);
            break;
            case 5:
                geneFamiliesTypeExecute(tsvIter);
            break;
            case 6:
                geneFamilyExcute(tsvIter);
            break;
            default:
                loggable.makeErrorLog("Name of the file invalid.");
            break;
        }

        loggable.makeLog("End (" + fileName + ")");
    }

    private int getOptionFileName(String fileName) {
        if (fileName.indexOf("A-ExperimentDescription-") == 0) {
            return 1;
        }
        if (fileName.indexOf("B-ExpressionTypeDiccionary-") == 0) {
            return 2;
        }
        if (fileName.indexOf("C-ExperimentDescriptionColumns-") == 0) {
            return 3;
        }
        if (fileName.indexOf("D-ExpressionValues-") == 0) {
            return 4;
        }
        if (fileName.indexOf("gene_families_type") == 0) {
            return 5;
        }
        if (fileName.indexOf("gene_family") == 0) {
            return 6;
        }
        return 0;
    }

    public void saveGeneFamiliesType(String type, String dbLink) throws ObjectStoreException, Exception {
        if (!geneFamiliesType.containsKey(type)) {
            TypeGeneFamily typeGeneFamily = new TypeGeneFamily();
            typeGeneFamily.setName(type);
            typeGeneFamily.setDbLink(dbLink);
            Item score = createItem(typeGeneFamily.getClassName());
            score      = typeGeneFamily.save(score);
            store(score);
            typeGeneFamily.setUniqueId(score.getIdentifier());
            geneFamiliesType.put(type, typeGeneFamily);
        }
    }

    public void saveFamilyGene(String family, String gene, String description) throws ObjectStoreException, Exception {
        GeneFamiliesGenes geneFamiliesGenes = new GeneFamiliesGenes();
        geneFamiliesGenes.setDescription(description);
        geneFamiliesGenes.setGeneFamily(newFamiliesItems.get(family));
        geneFamiliesGenes.setGene(geneItems.get(gene));
        Item score = createItem(geneFamiliesGenes.getClassName());
        score      = geneFamiliesGenes.save(score);
        store(score);
        geneFamiliesGenes.setUniqueId(score.getIdentifier());
    }

    private void saveFamily(String family, String type) throws ObjectStoreException, Exception {
        if (!newFamiliesItems.containsKey(family)) {
            GeneFamilies geneFamilies     = new GeneFamilies();
            TypeGeneFamily typeGeneFamily = geneFamiliesType.get(type);
            geneFamilies.setName(family);
            geneFamilies.setType(geneFamiliesType.get(type));
            Item score = createItem(geneFamilies.getClassName());
            score      = geneFamilies.save(score);
            store(score);
            geneFamilies.setUniqueId(score.getIdentifier());
            newFamiliesItems.put(family, geneFamilies);
        }
    }

    private void saveExpressionValues(String expressionValue, String condition, String primaryIdGenes, String primaryIdExperiment, String typeDiccionary) throws ObjectStoreException, Exception {
        ExpressionValues expressionValues = new ExpressionValues();
        expressionValues.setExpressionValue(expressionValue);
        expressionValues.setGene(geneItems.get(primaryIdGenes));
        if (verifyCondition(condition)) {
            expressionValues.setExperimentConditions(verifyConditionItems.get(condition));
        }
        if (verifyExperiment(primaryIdExperiment)) {
            expressionValues.setExperimentDescription(experimentItems.get(primaryIdExperiment));
        }
        if (verifyTypeDicionary(typeDiccionary)) {
            expressionValues.setExpressionTypeDiccionary(typeDiccionaryItmes.get(typeDiccionary));
        }
        Item score = createItem(expressionValues.getClassName());
        score      = expressionValues.save(score);
        store(score);
        expressionValues.setUniqueId(score.getIdentifier());
    }

    private void saveExperimentDescription(String name, String Decription, Publication PubId) throws ObjectStoreException, Exception {
        ExperimentDescription experimentDescription = new ExperimentDescription();
        experimentDescription.setName(name);
        experimentDescription.setDescription(Decription);
        experimentDescription.setPublication(PubId);
        Item score = createItem(experimentDescription.getClassName());
        score      = experimentDescription.save(score);
        store(score);
        experimentDescription.setUniqueId(score.getIdentifier());
        experimentItems.put(name, experimentDescription);
    }

    private void saveExpressionType(String name) throws ObjectStoreException, Exception {
        ExpressionTypeDiccionary expressionTypeDiccionary = new ExpressionTypeDiccionary();
        expressionTypeDiccionary.setName(name);
        Item score = createItem(expressionTypeDiccionary.getClassName());
        score      = expressionTypeDiccionary.save(score);
        store(score);
        expressionTypeDiccionary.setUniqueId(score.getIdentifier());
        typeDiccionaryItmes.put(name, expressionTypeDiccionary);
    }

    private void saveExperimentDescriptionColumns(  String name, String description,
                                                    String instrument, String strategy,
                                                    String source, String selection,
                                                    String layout, String primaryIdExperiment) throws ObjectStoreException, Exception {
        ExperimentConditions experimentConditions = new ExperimentConditions();
        experimentConditions.setName(name);
        experimentConditions.setDescription(description);
        experimentConditions.setInstrument(instrument);
        experimentConditions.setStrategy(strategy);
        experimentConditions.setSource(source);
        experimentConditions.setSelection(selection);
        experimentConditions.setLayout(layout);
        Item score = createItem(experimentConditions.getClassName());
        score      = experimentConditions.save(score);
        store(score);
        experimentConditions.setUniqueId(score.getIdentifier());
        verifyConditionItems.put(name, experimentConditions);
    }

    private void geneFamilyExcute(Iterator tsvIter) throws ObjectStoreException, Exception {
        while (tsvIter.hasNext()) {
            String[] line      = (String[]) tsvIter.next();
            String gene        = (line.length > 0) ? line[0] : "-";
            String type        = (line.length > 1) ? line[1] : "-";
            String family      = (line.length > 2) ? line[2] : "-";
            String description = (line.length > 3) ? line[3] : "-";
            String[] families  = family.split(",");
            if (verifyBioEntity(gene)) {
                for( int i = 0; i < families.length; i++) {
                    saveFamily(families[i], type);
                    //createBioEntity(gene);
                    saveFamilyGene(families[i], gene, description);
                }
            }
        }
    }

    private void geneFamiliesTypeExecute(Iterator tsvIter) throws Exception {
       if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            }
        }

        while (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            String type   = (line.length > 0) ? line[0] : "";
            String dbLink = (line.length > 1) ? line[1] : "";
            if (StringUtils.isBlank(type) == false) {
                saveGeneFamiliesType(type, dbLink);
            }
        }
    }


    private void ExperimentDescriptionColumnsExecute(Iterator tsvIter) throws Exception {
        int end = 0;
        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            } else {
                if (!StringUtils.isBlank(line[0])) {
                    end++;
                }
            }
        }

        if (end > 0) {
            while (tsvIter.hasNext()) {
                String[] line               = (String[]) tsvIter.next();
                String primaryIdExperiment  = (line.length > 0) ? line[0] : "-";
                String Name                 = (line.length > 1) ? line[1] : "-";
                String description          = (line.length > 2) ? line[2] : "-";
                String instrument           = (line.length > 3) ? line[3] : "-";
                String strategy             = (line.length > 4) ? line[4] : "-";
                String source               = (line.length > 5) ? line[5] : "-";
                String selection            = (line.length > 6) ? line[6] : "-";
                String layout               = (line.length > 7) ? line[7] : "-";
                if (StringUtils.isBlank(Name) == false) {
                    saveExperimentDescriptionColumns(Name, description, instrument, strategy, source, selection, layout, primaryIdExperiment);
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void ExpressionTypeDiccionaryExecute(Iterator tsvIter) throws Exception {
       int end = 0;

        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            } else {
                if (!StringUtils.isBlank(line[0])) {
                    end++;
                }
            }
        }

        if (end > 0) {
            while (tsvIter.hasNext()) {
                String[] line = (String[]) tsvIter.next();
                String name   = (line.length > 0) ? line[0] : "";
                if (StringUtils.isBlank(name) == false) {
                    saveExpressionType(name);
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void ExperimentDescriptionExecute(Iterator tsvIter) throws Exception {
        int end = 0;

        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                loggable.makeErrorLog("No data.");
            } else {
                if (!StringUtils.isBlank(line[0])) {
                    end++;
                }
            }
        }

        if (end > 0) {
            while (tsvIter.hasNext()) {
                String[] line        = (String[]) tsvIter.next();
                String name          = (line.length > 0) ? line[0] : "-";
                String Description   = (line.length > 1) ? line[1] : "-";
                String PubId         = (line.length > 2) ? line[2] : "-";
                Publication pubMedId = createPublication(PubId);
                if (StringUtils.isBlank(name) == false) {
                    saveExperimentDescription(name, Description, pubMedId);
                }
            }
        } else {
            loggable.makeErrorLog("File Empty of data.");
        }
    }

    private void ExpressionValuesExecute(Iterator tsvIter) throws Exception {
        String [] heads = null;
        int end         = 0;

        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
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

        while (tsvIter.hasNext()) {
            String[] line              = (String[]) tsvIter.next();
            int sizeLine               = line.length;
            String primaryIdGenes      = line[0];
            String primaryIdExperiment = line[1];
            String typeDiccionary      = line[2];
            createBioEntity(primaryIdGenes);
            for (int i = 3; i < end; i++) {
                if (i >= sizeLine) {
                    break;
                }
                saveExpressionValues(line[i], heads[i], primaryIdGenes, primaryIdExperiment, typeDiccionary);
            }
        }
    }

    private void createBioEntity(String primaryId) throws ObjectStoreException, Exception {
        if (!geneItems.containsKey(primaryId)) {
            Gene gene = new Gene();
            gene.setPrimaryIdentifier(primaryId);
            Item score = createItem(gene.getClassName());
            score      = gene.save(score);
            store(score);
            gene.setUniqueId(score.getIdentifier());
            geneItems.put(primaryId, gene);
        }
    }

    private Publication createPublication(String PubId) throws ObjectStoreException, Exception {
        Publication publication = null;
        if (!pubItems.containsKey(PubId)) {
            publication = new Publication();
            publication.setPubMedId(PubId);
            Item score = createItem(publication.getClassName());
            score      = publication.save(score);
            store(score);
            publication.setUniqueId(score.getIdentifier());
            pubItems.put(PubId, publication);
        } else {
            publication = pubItems.get(PubId);;
        }
        return publication;
    }

    private boolean verifyExperiment(String name) throws ObjectStoreException, Exception {
        return experimentItems.containsKey(name);
    }

    private boolean verifyTypeDicionary(String name) throws ObjectStoreException, Exception {
        return typeDiccionaryItmes.containsKey(name);
    }

    private boolean verifyCondition(String name) throws ObjectStoreException, Exception {
        return verifyConditionItems.containsKey(name);
    }

    private Boolean verifyBioEntity(String primaryId) throws Exception {
        if (!geneItems.containsKey(primaryId)) {
            loggable.makeErrorLog("Gene " + primaryId + " does not exists.");
            return false;
        }
        return true;
    }
}
