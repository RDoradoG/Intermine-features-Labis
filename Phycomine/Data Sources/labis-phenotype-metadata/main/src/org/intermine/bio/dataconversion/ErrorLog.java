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
 * <h1>The Class that control the Error logs.</h1>
 * <p>
 * Labis - IQ, USP. São Paulo
 *
 * @author Rodrigo Dorado
 * @version 1.0
 * @since   2019-08-31
 */
public class ErrorLog
{
    /**
     * The log file.
     */
	private String LOGFILE;
    /**
     * The Logger that handle the error log.
     */
    private static Logger LOGGER = null;
    /**
     * The file that is been reading.
     */
    private String actualFileName;

    /**
     * Constructor of the class
     * @param  logFile     The name of the log file.
     * @return             
     * @throws IOException 
     */
    public ErrorLog(String logFile) throws IOException {
    	this.LOGFILE = logFile;
    	setFileHandler();
    }

    /**
     * Set the the file that is been reading.
     * @param  filename  The name of the file.
     * @throws Exception 
     */
    public void setActualFile(String filename) throws Exception {
    	this.actualFileName = filename;
    }

    /**
     * Get the actual file.
     * @return The actual file, String.
     * @throws Exception
     */
    public String getActualFile() throws Exception {
    	return this.actualFileName;
    }

    /**
     * Set the file handler.
     * @throws IOException
     */
    protected void setFileHandler() throws IOException{
        LOGGER                    = Logger.getLogger("MyLog");
        FileHandler filehandler;
        filehandler               = new FileHandler(this.LOGFILE);
        LOGGER.addHandler(filehandler);
        LOGGER.setUseParentHandlers(false);
        SimpleFormatter formatter = new SimpleFormatter();
        filehandler.setFormatter(formatter);
    }

    /**
     * Write a error ionside the log file.
     * @param  Error     The error message.
     * @throws Exception
     */
    public void makeErrorLog(String Error) throws Exception {
    	String filename = getActualFile();
        makeLog("ERROR - file(" + filename + "): " + Error);
    }

    /**
     * Write a message inside the log file.
     * @param  logger    The message to be write.
     * @throws Exception
     */
    public void makeLog(String logger) throws Exception{
        LOGGER.info(logger);
    }
}