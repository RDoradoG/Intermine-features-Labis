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
public class ExperimentConditions extends NewObjectModel
{
	private String name;
	private String description;
	private String instrument;
	private String strategy;
	private String source;
	private String selection;
	private String layout;

	public ExperimentConditions() throws Exception {
        this.setClassName("ExperimentConditions");
    }

    public void setName(String name) throws Exception {
    	this.name = name;
    }

    public String getName() throws Exception {
    	return this.name;
    }

	public void setDescription(String description) throws Exception {
		this.description = description;
	}

	public String getDescription() throws Exception {
		return this.description;
	}

	public void setInstrument(String instrument) throws Exception {
		this.instrument = instrument;
	}

	public String getInstrument() throws Exception {
		return this.instrument;
	}

	public void setStrategy(String strategy) throws Exception {
		this.strategy = strategy;
	}

	public String getStrategy() throws Exception {
		return this.strategy;
	}

	public void setSource(String source) throws Exception {
		this.source = source;
	}

	public String getSource() throws Exception {
		return this.source;
	}

	public void setSelection(String selection) throws Exception {
		this.selection = selection;
	}

	public String getSelection() throws Exception {
		return this.selection;
	}

	public void setLayout(String layout) throws Exception {
		this.layout = layout;
	}

	public String getLayout() throws Exception {
		return this.layout;
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("name", getName());
		score.setAttribute("description", getDescription());
		score.setAttribute("instrument", getInstrument());
		score.setAttribute("strategy", getStrategy());
		score.setAttribute("source", getSource());
		score.setAttribute("selection", getSelection());
		score.setAttribute("layout", getLayout());
        return score;
	}
}