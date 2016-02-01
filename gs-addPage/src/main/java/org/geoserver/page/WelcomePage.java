package org.geoserver.page;

import org.apache.wicket.markup.html.basic.Label;
import org.geoserver.web.GeoServerBasePage;

public class WelcomePage extends GeoServerBasePage{
	public WelcomePage() {
		add(new Label("welcome","welcome"));
	}
	
}
