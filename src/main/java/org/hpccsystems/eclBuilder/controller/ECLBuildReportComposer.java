package org.hpccsystems.eclBuilder.controller;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hpcc.HIPIE.utils.HPCCConnection;
import org.hpccsystems.eclBuilder.Constants;
import org.hpccsystems.ws.client.HPCCWsClient;
import org.json.CDL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

public class ECLBuildReportComposer extends GenericForwardComposer {
	private static final long serialVersionUID = 1L;
	private Tabbox TabboxComp;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		Tabs tabs = new Tabs();
		
		Tabpanels tabPanels = new Tabpanels();
		
		ListModelList<ECLBuilderReportData> reportData = (ListModelList<ECLBuilderReportData>) Executions.getCurrent().getAttribute("reportDetails");
		
		String hpccId = (String) Executions.getCurrent().getAttribute(Constants.HPCC_CONNNECTION);
		
		Tab tempTab;// = new Tab();
		
		Tabpanel tempTabPanel;// = new Tabpanel();
		
		Columns cols;
		
		Column col = new Column();
		Grid grid;
		org.zkoss.zul.Button pivot, download;
		Div divPanel;
		Tabpanel tabPanel;
		for(ECLBuilderReportData buildObj : reportData){
			cols = new Columns();
			cols.setSizable(true);
			tempTab = new Tab(buildObj.getBuilderReportName().toUpperCase());
			tabs.appendChild(tempTab);
			tabs.setSclass("nav nav-pills nav-justified");
			tabs.setStyle("background-color: #00bcd4 !important;color:#fff !important;");
			tempTab.setSclass("nav-link active");
			tempTab.setStyle("color:#fff !important;");
			tabPanel = new Tabpanel();
			
			pivot = new Button("View Pivot");
			pivot.setSclass("btn btn-default");
			pivot.setIconSclass("glyphicon glyphicon-dashboard");
			
			download = new Button("Download");
			download.setSclass("btn btn-default");
			download.setIconSclass("glyphicon glyphicon-download-alt");
			pivot.setStyle("background-color:#66CDAA;");
			download.setStyle("background-color:#FFC0CB;margin-right:2px");
			
			
//			download.setClass("builderActionButtons");
//			pivot.setClass("builderActionButtons");

			
			divPanel = new Div();
			
			divPanel.setStyle("margin-left: 80%;padding:2px;");
			
			divPanel.appendChild(download);
			divPanel.appendChild(pivot);
			
			
			download.setAttribute("wuId", buildObj.getWuId());
			download.setAttribute("hpccId", hpccId);
			download.setAttribute("resultName", buildObj.getBuilderReportName());
			tabPanel.appendChild(divPanel);
			download.addEventListener("onClick", event -> {
				
				Button downloadButton = (Button) event.getTarget();
				
				String wuId = (String) downloadButton.getAttribute("wuId");
				
				String resultName = (String) downloadButton.getAttribute("resultName");
				
				String hpccID = (String) downloadButton.getAttribute("hpccId");
				
				downloadECLDataReport(wuId, resultName, hpccID);
				
			});

				
				
			pivot.setAttribute("wuId", buildObj.getWuId());
			pivot.setAttribute("resultName", buildObj.getBuilderReportName());
			pivot.addEventListener("onClick", event -> {
				Button b1 = (Button) event.getTarget();
				HashMap<String, Object> args = new HashMap<String, Object>();
				args.put("wuId", b1.getAttribute("wuId"));
				args.put("resultName", b1.getAttribute("resultName"));
				args.put("hpccId", hpccId);
				args.put("platform", TreeController.getPlatformForCluster());
				Window win = (Window) Executions.createComponents("/eclBuilder/pivotTable.zul", null, args);
				win.setClosable(true);
				
				Executions.getCurrent().getAttribute("userAction");
				win.doModal();
			});
			for(String str : buildObj.getRptColumns()){
				col = new Column(str);
				cols.appendChild(col);
			}
			grid = new Grid();
			grid.appendChild(cols);
			grid.setModel(new ListModelList(buildObj.getListData()));
			grid.setRowRenderer(new ECLBuildReportRenderer());
			grid.setMold("paging");
			grid.setVflex("1");
			grid.setPagingPosition("top");
//			grid.setClass("reportDataGrid");
			grid.setSclass("gridContent");
			grid.setAutopaging(true);
			grid.setPageSize(15);
			grid.setEmptyMessage("No Data Available");
//			tabPanel.setHeight("60%");
			Vlayout vlay = new Vlayout();
			vlay.setVflex("1");
			vlay.appendChild(grid);
			tabPanel.appendChild(vlay);
			tabPanels.appendChild(tabPanel);
		}
		TabboxComp.appendChild(tabs);
		TabboxComp.appendChild(tabPanels);

	}
	
	public static void downloadECLDataReport(String wuId, String resultName, String hpccID) throws Exception {

	        int rowid = 0;
	        HPCCWsClient connector = TreeController.getPlatformForCluster().getWsClient();
	        
	        JSONArray jsonArrO = new JSONArray();
	        
	        JSONObject resJsonRs = new JSONObject();
	        
	        for (int i=0 ; i< connector.getWsWorkunitsClient().getWUInfo(wuId).getResultCount(); i++) {
	            
	            if(resultName.equals(connector.getWsWorkunitsClient().getWUInfo(wuId).getResults()[i].getName())){
	                String wuresults = connector.getWsWorkunitsClient().fetchResults(wuId, i, connector.getAvailableClusterGroups()[1], true, 0, 1000);
	                resJsonRs = XML.toJSONObject(wuresults);
//	                jsonArrO = (JSONArray) ((JSONObject) resJsonRs.get("Dataset")).get("Row");
	                break;
	            }
	        }    
	        if (jsonArrO != null) {
	            
	            jsonArrO = (JSONArray)(((org.json.JSONObject) resJsonRs.get("Dataset")).get("Row"));
	            String content = CDL.toString(jsonArrO);

			Filedownload.save(content, "text", "newFile" + (new Random()).nextInt() + ".csv");
		}
	}
	
	public static void download(net.sf.json.JSONArray jsonArray, String tempfileName) throws IOException{
		
		int rowid = 0;
		
		File dir = new File(new java.io.File(".").getCanonicalPath());

		File f1 = new File(dir.getAbsolutePath() + "/" + "temp.xls");

		FileOutputStream out = new FileOutputStream(f1);

		XSSFWorkbook workbook = new XSSFWorkbook();
		
		XSSFSheet spreadsheet = workbook.createSheet(tempfileName);
		
		XSSFRow row = spreadsheet.createRow(rowid++);

		Set<String> keys = ((net.sf.json.JSONObject)jsonArray.get(0)).keySet();
		
		Iterator<String> iter1 = keys.iterator();

		int cellid = 0;
		while (iter1.hasNext()) {
			Cell cell = row.createCell(cellid++);
			cell.setCellValue((iter1.next()));
		}

		net.sf.json.JSONArray a = jsonArray;
		net.sf.json.JSONObject objNew;
		ListIterator<?> listIter = ((net.sf.json.JSONArray)a).listIterator();
		List<Object> listObj = new ArrayList<Object>();
		while (listIter.hasNext()) {
			row = spreadsheet.createRow(rowid++);
			objNew = (net.sf.json.JSONObject) listIter.next();
			iter1 = keys.iterator();
			cellid = 0;
			String sss = Constants.EMPTY_STR;
			String key = Constants.EMPTY_STR;
			while (iter1.hasNext()) {
				key = iter1.next();
				listObj.add(objNew.get(key));
				Cell cell = row.createCell(cellid++);
				try {
					Object c = (objNew.get(key));
					if (c instanceof Boolean) {
						if ((Boolean) c) {
							sss = Constants.TRUE_STR;
						} else {
							sss = Constants.FALSE_STR;
						}
					} else {
						sss = (null != c) ? c.toString() : Constants.EMPTY_STR;
					}

					cell.setCellValue(sss);
				} catch (Exception e) {
					System.err.println(Constants.ERROR_STR + e.getMessage() + sss);
				}
			}

		}

		row = workbook.getSheetAt(0).getRow(0);

		for (int colNum = 0; colNum < row.getLastCellNum(); colNum++) {
			workbook.getSheetAt(0).autoSizeColumn(colNum);
		}

		workbook.write(out);
		
		out.close();

		Desktop.getDesktop().open(f1);
	}

	public void onClick$refreshBtn(Event e) {
//		inboxGrid.setModel(new ListMode	lList(getUpdatedData()));
	}

}
