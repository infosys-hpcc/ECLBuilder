package org.hpccsystems.eclBuilder.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class DragAndDropController extends SelectorComposer<Component>  implements EventListener<Event> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Wire
	private org.zkoss.zul.Listbox left;
	
	@Wire
	private org.zkoss.zul.Listbox right;
	
	@Wire
	private Window joinWindow;
	
	@Wire
	private Button submitButton;
	
	@Wire
	private Button cancelJoin;
	
	LinkedList<String> lList = new LinkedList<String>();
	
//	LinkedHashMap<String, String> joinList = new LinkedHashMap<String, String>();
	
	List<String> joinList = new ArrayList<String>();
		
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		List<String> selectedFiles;
		
		String logicalFilesForBuilder = (String) Executions.getCurrent().getArg().get("selectedFiles");
		
		selectedFiles = Arrays.asList(logicalFilesForBuilder.split(",")); 
		
		left.setModel(new ListModelList<>(selectedFiles));
		
		left.setItemRenderer(new ListitemRenderer<String>() {

			@Override
			public void render(Listitem item, String label, int arg2) throws Exception {
				// TODO Auto-generated method stub
				Listcell cell = new Listcell();
				cell.setLabel(returnFileName(label));
				item.setDraggable("true");
				item.appendChild(cell);
			}
		});
		////System.out.println("Page loaded");
//		getSelf().getFellows().toArray()[0].getClass()
	}

	@Listen("onClick = #validateButton")
	public void onClickValidate(){
		if(validateJoinData()){
			Clients.showNotification("Validation success");
		}else{
			Clients.showNotification("Validation fails");
		}
	}
//	@Listen("onClick = .removeButton")
	public void onClickRemove(DropEvent e){
		String joinName = (String) e.getTarget().getParent().getParent().getAttribute("JoinName");
		List<Listitem> children1 = new ArrayList<Listitem>();
		children1.addAll(getChildrenonly(left));
		for(Listitem its : children1){
			if(its.getValue().equals(joinName)){
				left.removeChild(its);
			}
		}
	}
	
	@Listen("onClick=#cancelJoin")
	public void closeJoinWindow(){
		joinWindow.detach();
	}
	private String returnFileName(String fullPath){
		List<String> parts = Arrays.asList(fullPath.split("::"));
		return parts.size() > 0 ? parts.get(parts.size() - 1) : "";
		
	}
	@Listen("onClick=#submitButton")
	public void updateJoinFiles(Event e) throws JSONException{
		
		String joinString = "";
		
		String joinNames = "";
		
		JSONArray joinDtls = new JSONArray();
		
		org.json.JSONObject joinJson;
		
		for(Object obj : getChildrenonly(right)){
			Listitem item = (Listitem) obj;
			
			joinJson = new org.json.JSONObject();

			String joinName = ((Textbox) (item).getChildren().get(3).getChildren().get(0)).getText();
			
			String query = ((Textbox) (item).getChildren().get(2).getChildren().get(0)).getText();
			
			String leftName = ((Listcell) (item).getChildren().get(0)).getLabel();
			
			String rightName = ((Listcell) (item).getChildren().get(1)).getLabel();
			
			String joinType = ((Combobox) (item).getChildren().get(4).getChildren().get(0)).getValue();
			
			if(StringUtils.isEmpty(joinName) || StringUtils.isEmpty(query) || StringUtils.isEmpty(leftName) || StringUtils.isEmpty(rightName) || StringUtils.isEmpty(joinType)){
				Clients.showNotification("Join Codition is not complete!!!");
				return;
			}
			
			joinString += "\n" + joinName + " := Join(" + leftName + ", " + rightName + ", " + query + ", " + joinType +");";
			
			joinString += "\n" + "OUTPUT(" + joinName + " , NAMED(\'" + joinName + "\'));";
			
			joinNames += ((joinNames.length() > 0 ? "," : "") + joinName);
			
			joinJson.put("joinName", joinName);
			
			joinJson.put("leftDs", leftName);
			
			joinJson.put("rightDs", rightName);
			
			joinDtls.put(joinJson);
		}
		
		JSONObject json = new JSONObject();
		json.put("joinString", joinString);
		json.put("joinNames", joinNames);

		json.put("joins", joinDtls);
		
		
		System.out.println(joinString);
		
		Events.postEvent("onAddJoins", joinWindow.getParent(), json);
		closeJoinWindow();
		
	}
	@Listen("onDrop = #right")
	public void onDropEventProcess(DropEvent e){
/*		String targetName = ((Listcell)((Listitem)e.getTarget()).getChildren().get(0)).getLabel();
		if(targetName.startsWith("Join")){
			updateRemoveButtonStatus(targetName);
		}
*/		System.out.print(e.getTarget() + "\t" + e.getTarget());
		String draggedName = ((Listcell)((Listitem)e.getDragged()).getChildren().get(0)).getLabel();
		if(draggedName.startsWith("Join")){
			enableORDisableRemoveButton(draggedName, false);
		}
		Listcell cell = new Listcell(draggedName);
		cell.setZclass("left");
		Listitem it = new Listitem();
		it.setClass("leftMove");
		it.setDroppable("true");
		cell.setDroppable("true");
		String newJoinName = "Join" + (getChildrenonly(right).size() + 1);
		
		cell.addEventListener("onDrop", event -> {onDropEventProcess1(event);} );
		it.appendChild(cell);
		Listcell rightCell = new Listcell();
		rightCell.setDroppable("true");
		it.appendChild(rightCell);
		rightCell.addEventListener("onDrop", event -> {onDropEventProcess1(event);} );
		Listcell txtCell = new Listcell();
		Textbox tbox = new Textbox();
		tbox.setCols(15);
		tbox.setRows(3);
		tbox.setStyle("overflow:auto;");
		txtCell.appendChild(tbox);
		it.appendChild(txtCell);

		org.zkoss.zul.Textbox joinNameTxt = new org.zkoss.zul.Textbox();
		joinNameTxt.setValue(newJoinName);
		txtCell = new Listcell();
		joinNameTxt.addEventListener("onChange", event -> {changeJoinNameInLeft(event);});
		txtCell.appendChild(joinNameTxt);
		it.appendChild(txtCell);
		it.setValue(newJoinName);
		
		ListModelList<String> joinTypesList = new ListModelList<>();
		joinTypesList.add("INNER");
		joinTypesList.add("LEFT OUTER");
		joinTypesList.add("RIGHT OUTER");
		joinTypesList.add("FULL OUTER");
		joinTypesList.add("LEFT ONLY");
		joinTypesList.add("RIGHT ONLY");
		joinTypesList.add("FULL ONLY");
		
		org.zkoss.zul.Combobox joinTypeCombo = new Combobox();
		joinTypeCombo.setStyle("border-radius: 4px;padding: 1px;");
		joinTypeCombo.setModel(joinTypesList);
		joinTypeCombo.setAutodrop(true);
//		joinTypeCombo.setSelectedIndex(0);
//		b.addEventListener("onClick", event -> {removeListItem(event);});
		txtCell = new Listcell();
		txtCell.appendChild(joinTypeCombo);
//		txtCell.setWidth("215px");
		it.appendChild(txtCell);
		
		org.zkoss.zul.Button b = new org.zkoss.zul.Button("Remove");
		b.setClass("removeButton");
		b.setSclass("btn btn-default");
		b.setIconSclass("glyphicon glyphicon-remove");
		b.addEventListener("onClick", event -> {removeListItem(event);});
		txtCell = new Listcell();
		txtCell.appendChild(b);
//		txtCell.setWidth("100px");
		it.appendChild(txtCell);
		
		
		

		right.appendChild(it);
		it.setAttribute("JoinName", newJoinName);
		Listitem ita = new Listitem();
		
//		<listitem draggable="true" droppable="true" onDrop="move(event.dragged)">
//        <listcell src="/widgets/effects/drag_n_drop/img/document.png" label="ZK JSP" />
        ita.setDraggable("true");
        ita.setDroppable("true");
        ita.addEventListener("onDrop", event -> onDropEventProcess((DropEvent)event));
//		ita.setValue(newJoinName);
		txtCell = new Listcell();
		txtCell.setLabel(newJoinName);
		ita.appendChild(txtCell);
		ita.setValue(newJoinName);
		joinList.add(newJoinName);
		
		ListModelList<Object> currenLeftModel = new ListModelList<Object>((ListModelList<Object>) left.getModel());
		
		currenLeftModel.add(newJoinName);
		
		left.setModel(currenLeftModel);
		
		left.invalidate();
		
		left.renderAll();
//		left.appendChild(ita);
//		Listcell cell = new Listcell(
//				e.getDragged()
//				));
	}
	
	public void updateRemoveButtonStatus(String name){
		List<String> strNames = new ArrayList<String>();
		for(Component ita : getChildrenonly(right)){
			if(null != ita.getChildren().get(0)){
				strNames.add(((Listcell)ita.getChildren().get(0)).getValue());
			}
			if(null != ita.getChildren().get(1)){
				strNames.add(((Listcell)ita.getChildren().get(1)).getValue());
			}
		}
		if(!strNames.contains(name)){
			enableORDisableRemoveButton(name, true);
		}
	}
	
	public void onDropEventProcess1(Event e){
		////System.out.println("I am in left event");
		DropEvent dropEve = (DropEvent) e;
		
		String draggedName = ((Listcell)dropEve.getDragged().getChildren().get(0)).getLabel();
		
		if(joinList.contains(draggedName)){
//			dropEve.get
			String targetJoin = ((Listitem)dropEve.getTarget().getParent()).getValue();
			
			if(joinList.indexOf(targetJoin) < joinList.indexOf(draggedName)){
				Clients.showNotification("Error : Cyclic Joins formed which is not allowed in ECL");
				return ;
			}
//			int srcIndex = joinList.
		}
		
		String targetName = ((Listcell)e.getTarget()).getLabel();
		if(targetName.startsWith("Join")){
			updateRemoveButtonStatus(targetName);
		}

		
		if(((Listitem)dropEve.getTarget().getParent()).getAttribute("JoinName").equals(draggedName)){
			Clients.showNotification("Invalid Action, JoinResult can not be part of same Join",  dropEve.getTarget().getParent(), true);
			return;
		}
		((Listcell)dropEve.getTarget()).setLabel(draggedName);
		if(draggedName.startsWith("Join")){
			enableORDisableRemoveButton(draggedName, false);
		}
	}
	
	public void changeJoinNameInLeft(Event e){
		InputEvent IE = (org.zkoss.zk.ui.event.InputEvent)e;
		
		
		if(!IE.getValue().matches("[A-Za-z_]+[_0-9]*") || joinList.contains(IE.getValue())){

			((Textbox)IE.getTarget()).setText(IE.getPreviousValue().toString());
			Clients.showNotification("Invalid name given for the Join - " + IE.getPreviousValue());
			return;
		}
		for(Object litem : getChildrenonly(left)){
			Listitem listItem = (Listitem) litem;
			if(listItem.getChildren().size() > 0 && ((Listcell)listItem.getChildren().get(0)).getLabel().equals(IE.getValue().toString())){
				((Textbox)IE.getTarget()).setText(IE.getPreviousValue().toString());
				Clients.showNotification("Invalid name given for the Join - " + IE.getPreviousValue());
				return;
			}
		}

		IE.getTarget().getParent().getParent().setAttribute("JoinName", IE.getValue());
		
		int removeIndex = joinList.indexOf(IE.getPreviousValue().toString());
		 
		joinList.add(removeIndex, IE.getValue().toString());
		
		joinList.remove(removeIndex + 1);
		
		for(Object lItem : getChildrenonly(right)){
			Listitem listItem = (Listitem) lItem;
			for(Object lCell : listItem.getChildren()){
				Listcell listCell = (Listcell) lCell;
				listCell.setLabel(listCell.getLabel().equals(IE.getPreviousValue().toString()) ? IE.getValue().toString() : listCell.getLabel());
			}
			
		}
		
//		Listitem lItem = (Listitem) getChildrenonly(left).stream().filter(comp -> ((Listitem)comp).getLabel().equals(IE.getPreviousValue())).findFirst().get();
		/*if(lItem != null){
			lItem.setValue(IE.getValue());
			((Listcell)lItem.getChildren().get(0)).setLabel(IE.getValue());
		}*/
		
		ListModelList<Object> leftItems =new ListModelList<Object>();
		
//		leftItems.addAll((ListModelList<Object>)left.getModel());
		
		for(int i = 0; i< left.getListModel().getSize(); i++){
			if(left.getListModel().getElementAt(i).equals(IE.getPreviousValue())){
				leftItems.add(IE.getValue());
			}else{
				leftItems.add(left.getListModel().getElementAt(i));
			}
		}
		
		left.setModel(leftItems);
		////System.out.println(e.getName());
	}

	public void removeListItem(Event e){
		Listitem its = (Listitem) e.getTarget().getParent().getParent();
		if(((Listcell)its.getChildren().get(0)).getLabel().toString().startsWith("Join")){
			enableORDisableRemoveButton(((Listcell)its.getChildren().get(0)).getLabel(), true);					
		}
		
		if(((Listcell)its.getChildren().get(1)).getLabel().toString().startsWith("Join")){
			enableORDisableRemoveButton(((Listcell)its.getChildren().get(1)).getLabel(), true);					
		}

		right.removeChild(e.getTarget().getParent().getParent());
		String joinName = (String) e.getTarget().getParent().getParent().getAttribute("JoinName");
		List<Listitem> children1 = new ArrayList<Listitem>();
		children1.addAll(getChildrenonly(left));
		for(Listitem its1 : children1){
			if(((Listcell)its1.getChildren().get(0)).getLabel().equals(joinName)){
				left.removeChild(its1);
			}
		}

	}
	public void enableORDisableRemoveButton(String joinName, boolean enableDisable){
		List<Listitem> children1 = new ArrayList<Listitem>();
		children1.addAll(getChildrenonly(right));
		for(Listitem its : children1){
			if(its.getAttribute("JoinName").equals(joinName)){
				((Button)(its.getChildren().stream().filter(comp -> comp.getChildren().size() > 0 && comp.getChildren().get(0) instanceof Button).findFirst().get()).getChildren().get(0)).setDisabled(!enableDisable);
			}
		}
	}
	
	public boolean isEmpty(String str){
		return (null != str && !"".equals(str)) ? false : true;
	}
	public boolean validateJoinData(){
		for(Component ita : getChildrenonly(right)){
			if(isEmpty(((Listcell)((Listitem)ita).getChildren().get(0)).getLabel()) ||
					isEmpty(((Listcell)((Listitem)ita).getChildren().get(1)).getLabel()) ||
							isEmpty(((Textbox)((Listcell)((Listitem)ita).getChildren().get(2)).getChildren().get(0)).getText())){
				return false;
			}
		}
		return true;
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
	@Override
	public void onEvent(Event arg0) throws Exception {
		// TODO Auto-generated method stub
		//System.out.println(arg0.getName());
	}
}
