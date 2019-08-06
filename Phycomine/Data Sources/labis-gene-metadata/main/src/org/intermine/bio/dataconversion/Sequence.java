package org.intermine.bio.dataconversion;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Described class of Sequences
 *
 * @author Rodrigo Dorado
 */

import java.security.MessageDigest;

import org.apache.commons.lang.StringUtils;
import org.intermine.xml.full.Item;
import org.intermine.objectstore.ObjectStoreException;

public class Sequence extends NewObjectModel
{
	private String length;
	private String residues;
	private String md5checksum;

	public Sequence() throws Exception {
        this.setClassName("Sequence");
    }

    public void setResidues(String residues) throws Exception {
    	this.residues = residues;
		calculateMd5(residues);
    }

    public String getResidues() throws Exception {
    	return this.residues;
    }

	public void setLength(String length) throws Exception {
		this.length = length;
	}

	public String getLength() throws Exception {
		return this.length;
	}

	public void setLength() throws Exception {
		String size = Integer.toString(this.residues.length());
		this.setLength(size);
	}

	public void setMd5checksum(String md5checksum) throws Exception {
		this.md5checksum = md5checksum;
	}

	public String getMd5checksum() throws Exception {
		return this.md5checksum;
	}

	public void calculateMd5(String seq) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(seq.getBytes());
        byte[] digest    = md.digest();
        StringBuffer sb  = new StringBuffer();
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        setMd5checksum(sb.toString());
	}

	public Item save(Item score) throws ObjectStoreException, Exception {
		score.setAttribute("residues", getResidues());
        score.setAttribute("length", getLength());
        score.setAttribute("md5checksum", getMd5checksum());
        return score;
	}
}