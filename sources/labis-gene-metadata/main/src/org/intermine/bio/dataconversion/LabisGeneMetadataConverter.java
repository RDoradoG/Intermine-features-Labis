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
    private static final String DATASET_TITLE    = "Add DataSet.title here";
    private static final String DATA_SOURCE_NAME = "Add DataSource.name here";
    private static final String LOGFILE          = "/home/rodrigodorado/Documentos/MyJavaLog.log";

    private static Logger LOGGER                 = null;

    private Map<String, String> geneItems        = new HashMap<String, String>();
    private Map<String, String> experimentItems  = new HashMap<String, String>();


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
        String TablesNames = "";
        Iterator<?> tsvIter;

        makeLog("Start (" + fileName + ")");
        try {
            tsvIter = FormattedTextParser.parseTabDelimitedReader(reader);
        } catch (Exception e) {
            throw new BuildException("cannot parse file: " + getCurrentFile(), e);
        }

        if (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if (line.length < 1) {
                makeErrorLog(fileName, "Option not defined.");
            } else {
                if (line[0].equals("tables")) {
                    if (line.length > 1) {
                        TablesNames = line[1];
                    } else {
                        makeErrorLog(fileName, "Option not defined.");
                    }
                } else {
                    makeErrorLog(fileName, "Option not defined.");
                }
            }
        } else {
           makeErrorLog(fileName, "File Empty.");
        }

        if (TablesNames.equals("ExpressionValues")) {
            ExpressionValuesExecute(tsvIter, fileName);
        } else {
            if (TablesNames.equals("ExperimentDescription")) {
                ExperimentDescriptionExecute(tsvIter, fileName);
            } else {
                makeErrorLog(fileName, "Table Option incorrect.");
            }
        }
        makeLog("End (" + fileName + ")");
    }

    private void saveExpressionValues(String expressionValue, String condition, String primaryIdGenes, String primaryIdExperiment) throws Exception {
        Item score = createItem("ExpressionValues");
        score.setAttribute("expressionValue", expressionValue);
        score.setAttribute("condition", condition);
        score.setReference("gene", geneItems.get(primaryIdGenes));
        score.setReference("experiment", experimentItems.get(primaryIdExperiment));
        store(score);
    }

    private void saveExperimentDescription(String name, String Decription) throws ObjectStoreException {
        Item score = createItem("ExperimentDescription");
        score.setAttribute("name", name);
        score.setAttribute("description", Decription);
        //score.setReference("publication", "");
        store(score);
        experimentItems.put(name, score.getIdentifier());
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
            String primaryIdGenes      = line[0];
            String primaryIdExperiment = line[1];
            createBioEntity(primaryIdGenes);
            createExpEntity(primaryIdExperiment);
            for (int i = 2; i < end; i++) {
                saveExpressionValues(line[i], heads[i], primaryIdGenes, primaryIdExperiment);
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

    private void createExpEntity(String name) throws ObjectStoreException {
        if (!experimentItems.containsKey(name)) {
            saveExperimentDescription(name, "");
        }
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

}
