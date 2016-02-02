package org.geoserver.page;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.StringResourceModel;
import org.geoserver.web.GeoServerBasePage;

public class InfoPage extends GeoServerBasePage{
	public InfoPage() {
		Label content = new Label("detailInfo", new StringResourceModel("InfoPage.detailInfo", this, null));
		content.setEscapeModelStrings(false); 
        add(content);
	}
}
