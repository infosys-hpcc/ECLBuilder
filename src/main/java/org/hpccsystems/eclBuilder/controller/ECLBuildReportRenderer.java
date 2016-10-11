package org.hpccsystems.eclBuilder.controller;

import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

 
public class ECLBuildReportRenderer implements RowRenderer {
	
    public void render(final Row row, final java.lang.Object data) {
        String[] ary = (String[]) data;
        for(int i=0; i < ary.length; i++){
        	new Label(ary[i]).setParent(row);
        }
    }

	@Override
	public void render(Row row, Object data, int arg2) throws Exception {
		String[] ary = (String[]) data;
        for(int i=0; i < ary.length; i++){
        	new Label(ary[i]).setParent(row);
        }
		
	}
}