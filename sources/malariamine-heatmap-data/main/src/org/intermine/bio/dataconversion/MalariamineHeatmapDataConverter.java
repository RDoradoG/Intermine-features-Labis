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
//import org.apache.log4j.Logger;
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
public class MalariamineHeatmapDataConverter extends BioFileConverter
{
    //
    private static final String DATASET_TITLE = "Add DataSet.title here";
    private static final String DATA_SOURCE_NAME = "Add DataSource.name here";

    private static Logger LOGGER = null;
    private static final String LOGFILE = "/home/rodrigodorado/Documentos/MyJavaLog.log";

    private Map<String, String> geneItems = new HashMap<String, String>();

    /**
     * Constructor
     * @param writer the ItemWriter used to handle the resultant items
     * @param model the Model
     */
    public MalariamineHeatmapDataConverter(ItemWriter writer, Model model) throws IOException{
        super(writer, model, DATA_SOURCE_NAME, DATASET_TITLE);
        setFileHandler();
    }

    /**
     * 
     *
     * {@inheritDoc}
     */
    public void process(Reader reader) throws Exception {
        //test_randomExpressions(25, 150, 50, 200);
        File currentFile = getCurrentFile();
        String fileName = currentFile.getName();
        int inHead = 0;
        String [] heads = null;
        int end = 0;

        Iterator<?> tsvIter;
        try {
            tsvIter = FormattedTextParser.parseTabDelimitedReader(reader);
        } catch (Exception e) {
            throw new BuildException("cannot parse file: " + getCurrentFile(), e);
        }

        while (tsvIter.hasNext()) {
            String[] line = (String[]) tsvIter.next();
            if(inHead == 0){
                inHead = 1;
                for (int i = 0; i < line.length; i++) {
                    if (StringUtils.isEmpty(line[i])) {
                        break;
                    }
                    end++;
                }
                heads = new String[end];
                System.arraycopy(line, 0, heads, 0, end);
            }else{
                String primaryId = line[0];
                createBioEntity(primaryId);
                for (int i = 1; i < end; i++) {
                    Item score = createItem("ExpressionValues");
                    score.setAttribute("expressionValue", line[i]);
                    score.setAttribute("condition", heads[i]);
                    score.setReference("gene", geneItems.get(primaryId));
                    store(score);
                }
            }
        }
        makeLog("End"); 
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

    protected void setFileHandler() throws IOException{
        LOGGER = Logger.getLogger("MyLog");
        FileHandler filehandler;
        filehandler = new FileHandler(LOGFILE);
        LOGGER.addHandler(filehandler);
        LOGGER.setUseParentHandlers(false);
        SimpleFormatter formatter = new SimpleFormatter();  
        filehandler.setFormatter(formatter);
    }

    public void makeLog(String logger) throws Exception{
        LOGGER.info(logger);
    }

    public void test_randomExpressions(int x, int y, int min, int max) throws Exception{
        String row = "";
        Random rand = new Random();
        for(int j = 0; j < y; j++){
            row = "";
            for(int i = 0; i < x; i++){
                int  n = rand.nextInt(max) + min;
                row = row + "   " + n;
            }
            makeLog(row);
        }
    }
}
