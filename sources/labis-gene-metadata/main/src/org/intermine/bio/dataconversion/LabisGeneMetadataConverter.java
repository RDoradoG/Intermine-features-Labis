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
    //
    private static final String DATASET_TITLE       = "Add DataSet.title here";
    private static final String DATA_SOURCE_NAME    = "Add DataSource.name here";
    private static final String LOGFILE             = "/home/rodrigodorado/Documents/MyJavaLog.log";
    
    private static Logger LOGGER                    = null;
    
    private Map<String, String> geneItems           = new HashMap<String, String>();
    private Map<String, String> experimentItems     = new HashMap<String, String>();
    private Map<String, String> typeDiccionaryItmes = new HashMap<String, String>();

    private String publication_id = "2055463";
    private String publication_identifier = "";


    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public LabisGeneMetadataConverter(ItemWriter writer, Model model) throws IOException {
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
        setFileHandler();
    }

    /**
     *
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {


        File currentFile   = getCurrentFile();
        String fileName    = currentFile.getName();

        makeLog("Start (" + fileName + ")");

        Iterator<?> tsvIter;
        try {
            tsvIter = FormattedTextParser.parseTabDelimitedReader(reader);
        } catch (Exception e) {
            throw new BuildException("cannot parse file: " + getCurrentFile(), e);
        }

        int opt = getOptionFileName(fileName);

        switch(opt) {
            case 1:
                createPublication();
                ExperimentDescriptionExecute(tsvIter, fileName);
            break;
            case 2:
                ExpressionTypeDiccionaryExecute(tsvIter, fileName);
            break;
            case 3:
                ExpressionValuesExecute(tsvIter, fileName);
            break;
            case 4:
                ExperimentDescriptionColumnsExecute(tsvIter, fileName);
            break;
            default:
                makeErrorLog(fileName, "Table Option incorrect.");
            break;
        }

        makeLog("End (" + fileName + ")");
    }

    private int getOptionFileName(String fileName) {
        if (fileName.indexOf("A-ExperimentDescription-") == 0) {
            return 1;
        }
        if (fileName.indexOf("B-ExpressionTypeDiccionary-") == 0) {
            return 2;
        }
        if (fileName.indexOf("C-ExpressionValues-") == 0) {
            return 3;
        }
        if (fileName.indexOf("D-ExperimentDescriptionColumns-") == 0) {
            return 4;
        }
        return 0;
    }

    private void saveExpressionValues(String expressionValue, String condition, String primaryIdGenes, String primaryIdExperiment, String typeDiccionary) throws Exception {
        Item score = createItem("ExpressionValues");
        score.setAttribute("expressionValue", expressionValue);
        score.setAttribute("condition", condition);
        score.setReference("gene", geneItems.get(primaryIdGenes));
        if (verifyExperiment(primaryIdExperiment)) {
            score.setReference("experiment", experimentItems.get(primaryIdExperiment));
        }
        if (verifyTypeDicionary(typeDiccionary)) {
            score.setReference("type", typeDiccionaryItmes.get(typeDiccionary));
        }
        store(score);
    }

    private void saveExperimentDescription(String name, String Decription) throws ObjectStoreException {
        Item score = createItem("ExperimentDescription");
        score.setAttribute("name", name);
        score.setAttribute("description", Decription);
        score.setReference("publication", publication_identifier);
        store(score);
        experimentItems.put(name, score.getIdentifier());
    }

    private void saveExpressionType(String name) throws ObjectStoreException {
        Item score = createItem("ExpressionTypeDiccionary");
        score.setAttribute("name", name);
        store(score);
        typeDiccionaryItmes.put(name, score.getIdentifier());
    }

    private void saveExperimentDescriptionColumns(String columnName, String description, String primaryIdExperiment) throws ObjectStoreException {
        Item score = createItem("ExperimentDescriptionColumns");
        score.setAttribute("columnName", columnName);
        score.setAttribute("description", description);
        if (verifyExperiment(primaryIdExperiment)) {
            score.setReference("experiment", experimentItems.get(primaryIdExperiment));
        }
        store(score);
    }

    private void ExperimentDescriptionColumnsExecute(Iterator tsvIter, String fileName) throws Exception {
        int end         = 0;

        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                makeErrorLog(fileName, "No data.");
            } else {
                if (!StringUtils.isBlank(line[0])) {
                    end++;
                }
            }
        }

        if (end > 0) {
            while (tsvIter.hasNext()) {
                String[] line              = (String[]) tsvIter.next();
                String primaryIdExperiment = (line.length > 0) ? line[0] : "";
                String Name                = (line.length > 1) ? line[1] : "";
                String description         = (line.length > 2) ? line[2] : "";
                if (StringUtils.isBlank(Name) == false) {
                    saveExperimentDescriptionColumns(Name, description, primaryIdExperiment);
                }
            }
        } else {
            makeErrorLog(fileName, "File Empty of data.");
        }
    }

    private void ExpressionTypeDiccionaryExecute(Iterator tsvIter, String fileName) throws Exception {
       int end = 0;

        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                makeErrorLog(fileName, "No data.");
            } else {
                if (!StringUtils.isBlank(line[0])) {
                    end++;
                }
            }
        }

        if (end > 0) {
            while (tsvIter.hasNext()) {
                String[] line      = (String[]) tsvIter.next();
                String name        = (line.length > 0) ? line[0] : "";
                if (StringUtils.isBlank(name) == false) {
                    saveExpressionType(name);
                }
            }
        } else {
            makeErrorLog(fileName, "File Empty of data.");
        }
    }

    private void ExperimentDescriptionExecute(Iterator tsvIter, String fileName) throws Exception {
        int end = 0;

        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                makeErrorLog(fileName, "No data.");
            } else {
                if (!StringUtils.isBlank(line[0])) {
                    end++;
                }
            }
        }

        if (end > 0) {
            while (tsvIter.hasNext()) {
                String[] line      = (String[]) tsvIter.next();
                String name        = (line.length > 0) ? line[0] : "";
                String Description = (line.length > 1) ? line[1] : "";
                if (StringUtils.isBlank(name) == false) {
                    saveExperimentDescription(name, Description);
                }
            }
        } else {
            makeErrorLog(fileName, "File Empty of data.");
        }
    }

    private void ExpressionValuesExecute(Iterator tsvIter, String fileName) throws Exception {
        String [] heads = null;
        int end         = 0;

        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                makeErrorLog(fileName, "No data.");
            } else {
                for (int i = 0; i < line.length; i++) {
                    if (StringUtils.isBlank(line[i])) {
                        break;
                    }
                    end++;
                }
                heads = new String[end];
                System.arraycopy(line, 0, heads, 0, end);;
            }
        } else {
           makeErrorLog(fileName, "File Empty of data.");
        }

        while (tsvIter.hasNext()) {
            String[] line              = (String[]) tsvIter.next();
            int sizeLine = line.length;
            String primaryIdGenes      = line[0];
            String primaryIdExperiment = line[1];
            String typeDiccionary      = line[2];
            createBioEntity(primaryIdGenes);
            //createExpEntity(primaryIdExperiment);
            for (int i = 3; i < end; i++) {
                if (i >= sizeLine) {
                    break;
                }
                saveExpressionValues(line[i], heads[i], primaryIdGenes, primaryIdExperiment, typeDiccionary);
            }
        }
    }

    private void createBioEntity(String primaryId) throws ObjectStoreException {
        Item bioentity = null;
        if (!geneItems.containsKey(primaryId)) {
            bioentity = createItem("Gene");
            bioentity.setAttribute("primaryIdentifier", primaryId);
            store(bioentity);
            geneItems.put(primaryId, bioentity.getIdentifier());
        }
    }

    private boolean verifyExperiment(String name) throws ObjectStoreException {
        return experimentItems.containsKey(name);
    }

    private boolean verifyTypeDicionary(String name) throws ObjectStoreException {
        return typeDiccionaryItmes.containsKey(name);
    }

    protected void setFileHandler() throws IOException{
        LOGGER                    = Logger.getLogger("MyLog");
        FileHandler filehandler;
        filehandler               = new FileHandler(LOGFILE);
        LOGGER.addHandler(filehandler);
        LOGGER.setUseParentHandlers(false);
        SimpleFormatter formatter = new SimpleFormatter();
        filehandler.setFormatter(formatter);
    }

    private void makeErrorLog(String FileName, String Error) throws Exception {
        makeLog("ERROR - file(" + FileName + "): " + Error);
    }

    public void makeLog(String logger) throws Exception{
        LOGGER.info(logger);
    }

    private void createPublication() throws ObjectStoreException {
        Item pub               = null;
        pub                    = createItem("Publication");
        pub.setAttribute("pubMedId", publication_id);
        store(pub);
        publication_identifier = pub.getIdentifier();
    }
}