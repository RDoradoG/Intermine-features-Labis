package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Error Logs
 * 
 * @author Rodrigo Dorado
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

public class ErrorLog
{
	private String LOGFILE;
    private static Logger LOGGER = null;
    private String actualFileName;

    public ErrorLog(String logFile) throws IOException {
    	this.LOGFILE = logFile;
    	setFileHandler();
    }

    public void setActualFile(String filename) throws Exception {
    	this.actualFileName = filename;
    }

    public String getActualFile() throws Exception {
    	return this.actualFileName;
    }

    protected void setFileHandler() throws IOException{
        LOGGER                    = Logger.getLogger("MyLog");
        FileHandler filehandler;
        filehandler               = new FileHandler(this.LOGFILE);
        LOGGER.addHandler(filehandler);
        LOGGER.setUseParentHandlers(false);
        SimpleFormatter formatter = new SimpleFormatter();
        filehandler.setFormatter(formatter);
    }

    public void makeErrorLog(String Error) throws Exception {
    	String filename = getActualFile();
        makeLog("ERROR - file(" + filename + "): " + Error);
    }

    public void makeLog(String logger) throws Exception{
        LOGGER.info(logger);
    }
}