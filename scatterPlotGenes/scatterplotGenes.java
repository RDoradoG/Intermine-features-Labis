package org.phycomine.web;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.tiles.actions.TilesAction;
import org.apache.struts.tiles.ComponentContext;

import org.intermine.api.InterMineAPI;
import org.intermine.api.profile.InterMineBag;
import org.intermine.api.profile.Profile;
import org.intermine.api.query.PathQueryExecutor;
import org.intermine.api.results.ExportResultsIterator;
import org.intermine.api.results.ResultElement;
import org.intermine.metadata.Model;
import org.intermine.objectstore.ObjectStore;
import org.intermine.objectstore.ObjectStoreException;
import org.intermine.pathquery.Constraints;
import org.intermine.pathquery.OrderDirection;
import org.intermine.pathquery.PathQuery;
import org.intermine.web.logic.session.SessionMethods;
import org.intermine.webservice.client.core.ServiceFactory;
import org.intermine.webservice.client.services.QueryService;

/**
 * Class that generates heatMap data for a list of genes.
 *
 * @author Rodrigo Dorado
 *
 */
public class scatterplotGenes extends TilesAction
{
    /**
     * {@inheritDoc}
     */
    @Override
    public ActionForward execute(ComponentContext context, ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
    	HttpSession session        = request.getSession();
        final InterMineAPI im      = SessionMethods.getInterMineAPI(session);
        ObjectStore os             = im.getObjectStore();
        InterMineBag bag           = (InterMineBag) request.getAttribute("bag");
        Model model                = im.getModel();
        Profile profile            = SessionMethods.getProfile(session);
        PathQueryExecutor executor = im.getPathQueryExecutor(profile);
        
        String APIKey              = profile.getApiKey();
        String DayToken            = profile.getDayToken();
        String key                 = "";

        try {
            if (APIKey != null && !APIKey.isEmpty()) {
                key = APIKey;
            } else {
                key = DayToken;
            }
            //findExpression(request, model, bag, executor, os, key);
        //} catch (ObjectStoreException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("apiKey", key);

        return null;
    }
}