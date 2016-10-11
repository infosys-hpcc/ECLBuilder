package org.hpccsystems.eclBuilder.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Window;

public class DedupController extends SelectorComposer<Component> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Wire
	private Listbox colsToDedup;
	
	@Wire
	private Listbox datasetsForDedup;
	
	@Wire
	private Window dedupWindow;
	
	@Wire
	private Radiogroup isDistribute;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		// TODO Auto-generated method stub
		super.doAfterCompose(comp);
		
//		List<String> selectedFiles;
		
		String eclBuilderSortDatasets = "";
		String eclBuilderSortColumns = ""; 
		
		Clients.showNotification("Please select the dataset and the columns for Dedup");
		
//		selectedFiles = Arrays.asList(eclBuilderSortDatasets.split(",")); 
//		selectedFiles = Arrays.asList(eclBuilderSortColumns.split(","));
		
		JSONObject jsonDatasetFields = (JSONObject) Executions.getCurrent().getArg().get("datasetFields");
		
		JSONArray dsFields = (JSONArray) jsonDatasetFields.get("datasets");
		
		HashMap<String, String> fields = new HashMap<String, String>();
		
//		String dsNames = "";
		
		for (int i = 0; i < dsFields.length(); i++) {
			String key = ((JSONObject) dsFields.get(i)).keys().next().toString();
			String value = ((JSONObject) dsFields.get(i)).get(key).toString();

			value = value.endsWith(",") ? value.substring(0, value.length() - 1) : value;

			fields.put(key, value);

			if (eclBuilderSortDatasets.length() > 0) {

				eclBuilderSortDatasets += "," + key;

			} else {

				eclBuilderSortDatasets += key;

			}
		}
		
		
		eclBuilderSortColumns = ((Map.Entry<String, String>)fields.entrySet().iterator().next()).getValue();
		
//		colsToSort.setModel(new ListModelList<>(Arrays.asList(eclBuilderSortColumns.split(","))));
		
		colsToDedup.setItemRenderer(new ListitemRenderer<String>() {

			@Override
			public void render(Listitem item, String label, int arg2) throws Exception {
				// TODO Auto-generated method stub
				Listcell cell = new Listcell();
				Checkbox checkCell = new Checkbox(label);
				cell.appendChild(checkCell);
				item.appendChild(cell);
			}
		});
		
		datasetsForDedup.setModel(new ListModelList<>(Arrays.asList(eclBuilderSortDatasets.split(","))));
		
		datasetsForDedup.setItemRenderer(new ListitemRenderer<String>() {

			@Override
			public void render(Listitem item, String label, int arg2) throws Exception {
				// TODO Auto-generated method stub
				Listcell cell = new Listcell();
				Radio radioCell = new Radio(label);
				radioCell.setName("datasetRadio");
				cell.appendChild(radioCell);
				item.appendChild(cell);
				
				radioCell.addEventListener(Events.ON_CHECK, new EventListener<Event>() {

					@Override
					public void onEvent(Event event) throws Exception {
						// TODO Auto-generated method stub
						for(Component c : event.getTarget().getParent().getParent().getParent().getChildren()){
							if(null != c &&  c instanceof Listitem){
								for(Component c1 : c.getChildren()){
									if(null != c1 && c1 instanceof Listcell){
										for(Component c2 : c1.getChildren()){
											if(null != c2 && c2 instanceof Radio){
												((Radio)c2).setChecked(false);
											}
										}
									}
								}
							}
						}
						((Radio)(event.getTarget())).setChecked(true);
						
						String selLabel = (((Radio) (event.getTarget())).getLabel());
						colsToDedup.setModel(new ListModelList<>(
								Arrays.asList(fields.get(selLabel).split(","))));
						
						colsToDedup.invalidate();
					}
				});
			}
		});
	}
	
	@Listen("onClick=#cancelDedup")
	public void closeDedupWindow(){
		dedupWindow.detach();
	}
	
	@Listen("onClick=#submitButton")
	public void sortCols(Event e){
		
		String cols = "";
		String dedupString="";
		String ds="";
		
		for(Object obj : getChildrenonly(datasetsForDedup)){
			Listitem item = (Listitem) obj;
			for(Component chk : item.getChildren().get(0).getChildren())
			{
				if(chk instanceof Radio && ((Radio) chk).isChecked()){
					ds = ds.isEmpty() ? ((Radio)chk).getLabel() : ds+","+((Radio)chk).getLabel();
				}
			}
			
		}
		for(Object obj : getChildrenonly(colsToDedup)){
			Listitem item = (Listitem) obj;
			for(Component chk : item.getChildren().get(0).getChildren())
			{
				if(chk instanceof Checkbox && ((Checkbox) chk).isChecked()){
					cols = cols.isEmpty() ? ((Checkbox)chk).getLabel() : cols+","+((Checkbox)chk).getLabel();
				}
			}
			
		}
			
			if(StringUtils.isEmpty(cols) || StringUtils.isEmpty(ds)){
				Clients.showNotification("Dedup Codition is not complete!!!");
				return;
			}
			
			if(isDistribute.getSelectedItem().getId().equals("isDistributeTrue")){
				dedupString += "\n\n" + ds + "_DedupRecs := DEDUP(SORT(DISTRIBUTE(" + ds + ",HASH32("+ cols + "))," + cols + ",LOCAL)," + cols + ",LOCAL);";
			}
			else{
				dedupString += "\n\n" + ds + "_DedupRecs := DEDUP(SORT(" + ds + "," + cols +"),"+ cols +");";
			}
			
			
			dedupString += "\n\n" + "OUTPUT(" + ds + "_DedupRecs" + " , NAMED(\'" + ds + "_DedupRecs" + "\'));";
			
			System.out.println(dedupString);
			Events.postEvent("onAddDedup", dedupWindow.getParent(), dedupString);
			closeDedupWindow();
	}
	
	public List<Listitem> getChildrenonly(org.zkoss.zul.Listbox box){
		List<Listitem> listItems = new ArrayList<Listitem>();
		for(Object obj : box.getChildren()){
			if(obj instanceof Listitem){
				listItems.add((Listitem)obj);
			}
		}
		return listItems;
	}
}
