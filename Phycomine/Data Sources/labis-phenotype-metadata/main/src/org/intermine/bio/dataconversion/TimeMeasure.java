package org.intermine.bio.dataconversion;

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

/**
 * <h1>Described class of Time Measures</h1>
 * <p>
 * Labis - IQ, USP. São Paulo
 *
 * @author Rodrigo Dorado
 * @version 1.0
 * @since   2019-12-09
 */
public class TimeMeasure extends NewObjectModel
{
	private String timeMeasure;
	private String rate;
	private String root;

	public TimeMeasure() throws Exception {
        this.setClassName("TimeMeasure");
    }

	public void setTimeMeasure(String timeMeasure) throws Exception {
		this.timeMeasure = timeMeasure;
	}

	public String getTimeMeasure() throws Exception {
		return this.timeMeasure;
	}

	public void setRate(String rate) throws Exception {
		this.rate = rate;
	}

	public String getRate() throws Exception {
		return this.rate;
	}

	public void setRoot(String root) throws Exception {
		this.root = root;
	}

	public String getRoot() throws Exception {
		return this.root;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("timeMeasure", getTimeMeasure());
		score.setAttribute("rate", getRate());
		score.setAttribute("root", getRoot());
        return score;
	}

}