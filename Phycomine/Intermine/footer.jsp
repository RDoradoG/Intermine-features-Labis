<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- footer.jsp -->
<br/>
<br/>
<br/>

<div class="body" align="center" style="clear:both">
    <!-- contact -->
    <c:if test="${pageName != 'contact'}">
        <div id="contactFormDivButton">
            <im:vspacer height="11" />
            <div class="contactButton">
                <a href="#" onclick="showContactForm();return false">
                    <b><fmt:message key="feedback.title"/></b>
                </a>
            </div>
        </div>
        <div id="contactFormDiv" style="display:none;">
            <im:vspacer height="11" />
            <tiles:get name="contactForm" />
        </div>
    </c:if>
    <br/>

    <div class="funding-footer footer">
      <!-- funding -->
      <fmt:message key="funding" />
      <c:set var="credits"><fmt:message key="credits" /></c:set>

      <c:if test="${!fn:startsWith(credits,'???')}">
           ${credits}
        </c:if>
    </div>
    <br>
    <div id="promo-footer">
      <!-- powered -->
      <div class="powered-footer footer">
        <p>Powered by</p>
        <a target="new" href="http://intermine.org" title="InterMine">
          <img src="images/icons/intermine-footer-logo.png" alt="InterMine logo" />
        </a>
      </div>
        <div class="mobile-app">
            <p>Have you tried our InterMine mobile apps?</p>
            <div>
            <a href='https://play.google.com/store/apps/details?id=org.intermine.app&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1' target="_blank">
              <img class="googleplay" alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' />
            </a>
            <a href="https://itunes.apple.com/us/app/intermine-gene-search/id1271710231?mt=8" target="_blank">
              <img src="images/apple-app-store.svg" class="ios">
            </a>
          </div>
        </div>
        <div class="cite-footer footer">
          <strong>Cite us:</strong>
              <cite>${WEB_PROPERTIES['project.citation']}</cite>
        </div>
    </div>

</div>
<!-- /footer.jsp -->

