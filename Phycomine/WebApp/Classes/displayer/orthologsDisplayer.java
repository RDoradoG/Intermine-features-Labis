package org.phycomine.web.displayer;

/**
 * Labis - IQ, USP. São Paulo
 *
 * Functionallity of Othologs Displayer
 *
 * @author Rodrigo Dorado
 */

import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import org.intermine.api.InterMineAPI;
import org.intermine.api.query.PathQueryExecutor;
import org.intermine.api.profile.Profile;
import org.intermine.api.results.ResultElement;
import org.intermine.api.results.ExportResultsIterator;
import org.intermine.web.logic.config.ReportDisplayerConfig;
import org.intermine.web.logic.results.ReportObject;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.web.displayer.ReportDisplayer;
import org.intermine.model.bio.Gene;
import org.intermine.model.bio.Ortholog;
import org.intermine.model.bio.Organism;
import org.intermine.metadata.Model;
import org.intermine.pathquery.PathQuery;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;

public class orthologsDisplayer extends ReportDisplayer
{

	public orthologsDisplayer(ReportDisplayerConfig config, InterMineAPI im) {
        super(config, im);
    }

    @Override
    public void display(HttpServletRequest request, ReportObject reportObject) {

        HttpSession session        = request.getSession();
        final InterMineAPI im      = SessionMethods.getInterMineAPI(session);
        ObjectStore os             = im.getObjectStore();
        Model model                = im.getModel();
        Profile profile            = SessionMethods.getProfile(session);
        PathQueryExecutor executor = im.getPathQueryExecutor(profile);
        Gene gene                  = (Gene) reportObject.getObject();
        Ortholog ortholog          = gene.getOrtholog();
        String idGene              = gene.getPrimaryIdentifier();
        String geneHTML            = "";
        String ortholog_name       = "";
        Integer size               = 0;
        if (ortholog != null) {
            ortholog_name = ortholog.getName();
            Set<Gene> allGenes     = ortholog.getGene();
            size                   = allGenes.size();
            Iterator gene_iterator = allGenes.iterator();

            while (gene_iterator.hasNext()) {
                Gene actual_gene  = (Gene) gene_iterator.next();
                Organism organism = actual_gene.getOrganism();
                geneHTML          = geneHTML + "<tr>";
                geneHTML          = addColumnWithLink(geneHTML, actual_gene.getPrimaryIdentifier(), actual_gene.getId());
                geneHTML          = addColumnWithLink(geneHTML, actual_gene.getSecondaryIdentifier(), actual_gene.getId());
                geneHTML          = addColumn(geneHTML, actual_gene.getSymbol());
                geneHTML          = addColumn(geneHTML, actual_gene.getName());
                geneHTML          = addColumnWithLink(geneHTML, organism.getShortName(), organism.getId());
                geneHTML          = geneHTML + "</tr>";
            }
        }

        String displayOff = "style=\"display: none;\"";
        String displayOn  = "";

        request.setAttribute("ortholog", ortholog_name);
        request.setAttribute("number_rows", Integer.toString(size));
        request.setAttribute("actual_gene_id", gene.getId());
        request.setAttribute("genes_html", geneHTML);
        if (size > 20) {
        	request.setAttribute("displayOff", displayOn);
        } else {
        	request.setAttribute("displayOff", displayOff);
        }
    }

    private String addColumn(String buffer, String value) {
    	if (value == null) {
    		return buffer + "<td></td>";
    	}
    	return buffer + "<td>" + value + "</td>";
    }

    private String addColumnWithLink(String buffer, String value, Integer objectId) {
    	if (value == null) {
    		return buffer + "<td></td>";
    	}
    	return buffer + "<td><a href=\"report.do?id=" + Integer.toString(objectId) + "\">" + value + "</a></td>";
    }
}