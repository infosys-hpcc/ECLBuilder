package org.hpccsystems.eclBuilder.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hpccsystems.eclBuilder.dao.EClBuilderDao;
import org.hpccsystems.ws.client.HPCCWsClient;
import org.hpccsystems.ws.client.platform.Platform;
import org.hpccsystems.ws.client.platform.WorkunitInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class BuildECLHistoryController extends SelectorComposer<Component> implements EventListener<Event> {

	private static final long serialVersionUID = 1L;
	
	Button openHistory = new Button();
	@Wire
	private Tabpanel historyPanel;

	@Wire
	private Tab eclHistoryTab;
	
	Platform platform;
	
	HPCCWsClient connector;

	String userAction, hpccID, eclBuilderName;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		Grid selectedGrid = new Grid();

		userAction = (String) Executions.getCurrent().getAttribute("userAction");

		hpccID = (String) Executions.getCurrent().getAttribute("hpccConnId");

		eclBuilderName = (String) Executions.getCurrent().getAttribute("BuilderName");
		
		platform = TreeController.getPlatformForCluster();

		connector = platform.getHPCCWSClient();

		Columns cols = new Columns();

		cols.appendChild(new Column("WorkUnitId"));

		cols.appendChild(new Column("Last Updated Date"));

		cols.appendChild(new Column("Action"));

		selectedGrid.appendChild(cols);

		Rows rows = new Rows();

		Row row;

		List<Builder> builders = (List<Builder>) Executions.getCurrent().getAttribute("buildersList");

		for (Builder build : builders) {

			row = new Row();

			row.appendChild(new Label(build.getWuID()));

			Timestamp timestamp = build.getTimestamp();
			
	        // S is the millisecond
	        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");
			row.appendChild(new Label(simpleDateFormat.format(timestamp).toString()));
			
//			row.appendChild(new Label(build.getLastmodifieddate().toString()));

			Button b = new Button("View");
			b.setSclass("btn btn-default");
			b.setIconSclass("glyphicon glyphicon-eye-open");

			row.appendChild(b);

			b.addEventListener(Events.ON_CLICK, event -> openHistoryTab(build.getWuID()));

			rows.appendChild(row);
		}

		selectedGrid.appendChild(rows);
		
//		selectedGrid.setClass("reportDataGrid");
		selectedGrid.setSclass("gridContent");
		historyPanel.setSclass("tab-pane fade in active");

		if (historyPanel.getChildren().size() > 0) {

			historyPanel.removeChild(historyPanel.getChildren().get(0));

		}

		historyPanel.appendChild(selectedGrid);

	}

	// @Listen("onClick=#openHistory")
	public void openHistoryTab(String wwid) {

		System.out.println(wwid);

		int matchedTabcount = (int) eclHistoryTab.getParent().getChildren().stream()
				.filter(comp -> ((Tab) comp).getLabel().equals(wwid)).count();

		if (matchedTabcount > 0) {

			((Tab) eclHistoryTab.getParent().getChildren().stream().filter(comp -> ((Tab) comp).getLabel().equals(wwid))
					.findFirst().get()).setSelected(true);

			return;
		}
		Platform platform;
		try {
			/*HPCCConnection connection = HipieSingleton.getHipie().getHpccManager().getConnections().get(hpccID);
			platform = Platform.get((connection.getIsHttps() ? "https" : "http"), connection.getServerHost(),
					connection.getServerPort(), connection.getUserName(), connection.getPwd());

			HPCCWsClient connector = platform.getHPCCWSClient();*/

			WorkunitInfo wuInfo = connector.getWsWorkunitsClient().getWUInfo(wwid);

			String wuresults = "";

			JSONObject resJson;

			Tabs tabs = new Tabs();

			Tabpanels tabPanels = new Tabpanels();

			Tab tempTab;// = new Tab();

			Tabpanel tempTabPanel;// = new Tabpanel();

			Columns cols;

			Column col = new Column();
			Grid grid;
			Tabpanel tabPanel;

			Tabbox tabboxComp = new Tabbox();

			org.zkoss.zul.Button pivot, download;
			
			Div divPanel;
			
			
			for (int i=0 ; i< connector.getWsWorkunitsClient().getWUInfo(wwid).getResultCount(); i++) {
//				wuresults = connector.getWorkunitResult(wuID, result.getName());
				wuresults = connector.getWsWorkunitsClient().fetchResults(wwid, i, connector.getAvailableClusterGroups()[1], true, 0, 0);
				resJson = XML.toJSONObject(wuresults);
/*
			for (ECLResult result : wuInfo.getResults()) {

				wuresults = connection.getWorkunitResult(wwid, result.getName());*/

				resJson = XML.toJSONObject(wuresults);
				resJson = (JSONObject) resJson.get("Dataset");

				tabPanel = new Tabpanel();

				pivot = new Button("View Pivot");
				download = new Button("Download");

				download.setAttribute("wuId", wwid);
				download.setAttribute("hpccId", hpccID);
//				download.setClass("builderActionButtons");
//				pivot.setClass("builderActionButtons");
				
				pivot.setSclass("btn btn-default");
				pivot.setIconSclass("glyphicon glyphicon-dashboard");
				
				download.setSclass("btn btn-default");
				download.setIconSclass("glyphicon glyphicon-download-alt");
				
				pivot.setStyle("background-color:#66CDAA;");
				download.setStyle("background-color:#FFC0CB;margin-right:2px");

				download.setAttribute("resultName", connector.getWsWorkunitsClient().getWUInfo(wwid).getResults()[i].getName());
				
				divPanel = new Div();
				
				divPanel.setStyle("margin-left: 80%;padding:2px;");
				
				divPanel.appendChild(download);
				divPanel.appendChild(pivot);
				
				tabPanel.appendChild(divPanel);
				download.addEventListener("onClick", event -> {

					Button downloadButton = (Button) event.getTarget();

					String wuId = (String) downloadButton.getAttribute("wuId");

					String resultName = (String) downloadButton.getAttribute("resultName");

					String hpccID = (String) downloadButton.getAttribute("hpccId");

					ECLBuildReportComposer.downloadECLDataReport(wuId, resultName, hpccID);

				});
				
				pivot.setAttribute("wuId",wwid);
				pivot.setAttribute("hpccId", hpccID);
				pivot.setAttribute("resultName", connector.getWsWorkunitsClient().getWUInfo(wwid).getResults()[i].getName());
//				tabPanel.appendChild(pivot);
				pivot.addEventListener("onClick", event -> {
					Button b1 = (Button) event.getTarget();
					HashMap<String, Object> args = new HashMap<String, Object>();
					args.put("wuId", b1.getAttribute("wuId"));
					args.put("resultName", b1.getAttribute("resultName"));
					args.put("hpccId", b1.getAttribute("hpccId"));
					Window win = (Window) Executions.createComponents("/eclBuilder/pivotTable.zul", null, args);
					win.setClosable(true);
					
					Executions.getCurrent().getAttribute("userAction");
					win.doModal();
				});

				cols = new Columns();
				cols.setSizable(true);
				tempTab = new Tab((connector.getWsWorkunitsClient().getWUInfo(wwid).getResults()[i].getName()).toUpperCase());
				tabs.appendChild(tempTab);
				

				List<String> rptCols = getRptColumns(resJson);
				for (String str : getRptColumns(resJson)) {
					col = new Column(str);
					cols.appendChild(col);
				}
				grid = new Grid();
				grid.appendChild(cols);

				grid.setModel(new ListModelList(getRptData(resJson, rptCols)));
				grid.setRowRenderer(new ECLBuildReportRenderer());
				grid.setMold("paging");
//				grid.setClass("reportDataGrid");
				grid.setSclass("gridContent");
				grid.setVflex("1");
				grid.setPagingPosition("top");
				grid.setEmptyMessage("No Data Available");
				grid.setAutopaging(true);
				grid.setEmptyMessage("No Data Available");
				grid.setPageSize(15);
				tabPanel.appendChild(grid);
				tabPanels.appendChild(tabPanel);
			}

			Tab historyCode = new Tab("SOURCE CODE");
			
			tabs.appendChild(historyCode);
			tabs.setSclass("nav nav-pills nav-justified");
			tabs.setStyle("background-color: #00bcd4 !important;color:#fff;");

			Tabpanel tPan = new Tabpanel();

			tabPanels.appendChild(tPan);

			Textbox t = new Textbox();
			
			historyCode.addEventListener("onClick", event -> {
				Clients.evalJavaScript("CodeMirror.fromTextArea(\"" + t.getUuid() + "\",\"" + t.getUuid()
				+ "\", {lineNumbers: true, height: \"650px\", stylesheet: \"css\\codemirror.css\", textWrapping: true})");

			});
			

			t.setMultiline(true);

			t.setRows(22);

			t.setCols(216);

			t.setText(((EClBuilderDao) SpringUtil.getBean("EClBuilderDao")).getECLQueryByWUID(wwid));

			tPan.appendChild(t);

			tabboxComp.appendChild(tabs);

			tabboxComp.appendChild(tabPanels);

			Tab newTab = new Tab(wwid);

			eclHistoryTab.getParent().appendChild(newTab);
			Tabpanel tPanel = new Tabpanel();
			newTab.setSelected(true);
			tPanel.appendChild(tabboxComp);
			historyPanel.getParent().appendChild(tPanel);

		} catch (Exception e) {

		}

	}

	@Override
	public void onEvent(Event arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	private List<String> getRptColumns(JSONObject rptJSONObject) {
		try {
			List<String> rptColumns = new ArrayList<String>();
			JSONArray dummyJSONArray = new JSONArray();

			Object obj;
			if (!rptJSONObject.has("Row")) {
				return rptColumns;
			}
			obj = rptJSONObject.get("Row");

			Iterator<String> iter = obj instanceof JSONArray ? ((JSONObject) ((JSONArray) obj).get(0)).keys()
					: ((JSONObject) obj).keys();

			ArrayList<String[]> listData = new ArrayList<String[]>();

			while (iter.hasNext()) {
				rptColumns.add(iter.next());
			}
			return rptColumns;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private List<String[]> getRptData(JSONObject rptJSONObject, List<String> rptColumns) {
		try {
			String[] tempColArr = new String[0];
			List<String[]> listData = new ArrayList<String[]>();
			// listData.add(rptColumns.toArray(tempColArr));
			JSONObject tempJson;

			JSONArray dummyJSONArray = new JSONArray();
			if (!rptJSONObject.has("Row")) {
				return listData;
			}
			Object rowObj = rptJSONObject.get("Row");

			JSONArray rptJSONArr = rowObj instanceof JSONArray ? ((JSONArray) rowObj)
					: dummyJSONArray.put(0, (JSONObject) rowObj);

			String[] tempStrArr;
			for (int i = 0; i < rptJSONArr.length(); i++) {

				tempJson = (JSONObject) rptJSONArr.get(i);
				tempStrArr = new String[tempJson.length()];
				for (int cnt = 0; cnt < rptColumns.size(); cnt++) {
					tempStrArr[cnt] = tempJson.get(rptColumns.get(cnt)).toString();
				}
				listData.add(tempStrArr);
			}
			return listData;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
