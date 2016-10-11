package org.hpccsystems.eclBuilder.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hpccsystems.eclBuilder.Constants;
import org.hpccsystems.eclBuilder.dao.EClBuilderDao;
import org.hpccsystems.eclBuilder.entity.User;
import org.hpccsystems.eclBuilder.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Menubar;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.Popup;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class HomeController extends org.hpccsystems.eclBuilder.HomeComposer  {
	private static final String DELETE_CONFIRMATION_DIALOGUE = "deleteConfirmationDialogue";
	private static final String FA_FA_SORT_AMOUNT_DESC = "fa fa-sort-desc";
	private static final String FA_FA_SORT_AMOUNT_ASC = "fa fa-sort-asc";
	private static final String UNABLE_TO_LOAD_DASHBOARD_TEMPLATE = "Unable to load Dashboard template";
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
	private static final String PROMOTE_CONFIRMATION_MESSAGE = "promoteProceedmessage";
	private static final String PROMOTE_CONFIRMATION_HEADER = "promoteProceed";

	@Wire
	private Tabbox homeTabbox;
	@Wire
	private Listbox searchListBox;
	@Wire
	private Textbox searchTextBox;
	@Wire
	private Listhead listHead;
	@Wire
	private Popup searchPopup;
	@Wire
	private Tab homeTab;

	@Wire
	private Button clearFilter;

	@Wire
	private Button aboutBtn;
	
	@Wire
	private Menuitem authorItemDashboard;

	@Wire
	private Menuitem dateItemDashboard;

	@Wire
	private Radiogroup viewSelectRadioGroup;

	@Wire
	private Radio toggleGridView;

	@Wire
	private Radio toggleListView;
	
	@Wire
	private Button signOut;
	
	@Wire
	private Menuitem nameItemDashboard;

//	private DashboardGrid dashboardGrid;

	@Wire
	private Menubar sortMenuBar;
	
	@Wire
	private Grid entityList;

	private User user;
	private static final String NEW_DASHBOARD_URI = "/eclBuilder/newECLBuilder.zul";

//	private List<Dashboard> dashboards;
//	private ListModelList<Dashboard> dashboardModel = new ListModelList<Dashboard>();
	private static final String DASHBOARD_GRID_PROPERTY = "dashboards";
	private static final String GRID_SORT_TYPE = "gridSortType";
	private static final String ASC = "asc";
	private static final String DES = "des";
	private static final String PROJECT_STRING = "projects";
	 
	private List<String> favDashboards;
    private ListModelList<Builder> modelList = new ListModelList<Builder>();
//    ListModelList<Project> projects = new ListModelList<Project>();
    private List<Builder> gridProjects = new ArrayList<Builder>();
//    private ProjectGrid projectGrid;
	
    
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);

		user = null;
		ListModelList<Builder> dummy = new ListModelList<Builder>();
		
		String userID = ""; 
		
		try{
		
		userID = ((User) ((AuthenticationService) SpringUtil.getBean(Constants.AUTHENTICATION_SERVICE))
				.getCurrentUser()).getId();
		
		signOut.setTooltiptext(userID);
		
		}catch(Exception e){
			Executions.sendRedirect("/eclBuilder/login.zul");
			return;
		}
		
//		entityList.setModel(dummy);
		reloadBuilderList(false);
		/*if (user.getPermission().getDashboardPermission().getUiPermission().canViewGrid()
				&& user.getPermission().getDashboardPermission().getUiPermission().canViewList()) {
			viewSelectRadioGroup.setVisible(true);
		} else {
			viewSelectRadioGroup.setVisible(false);
		}*/
		
		homeTabbox.addEventListener("onCompleteUserAction", event -> {reloadBuilderList();});
		aboutBtn.addEventListener(Events.ON_CLICK, event -> {onClickAbout(event);});
		
		homeTabbox.addEventListener("onCreateNewBuilder", event -> openECLBuilderTab(event.getData().toString()));
		
		/*
		 * if (user.getPermission().getDashboardPermission().getUiPermission().
		 * isGridDefaultView()) {
		 */
		/*
		 * } else { toggleGridView.setChecked(false);
		 * toggleListView.setChecked(true); showList(); }
		 */


//		homeTabbox.addEventListener(Dashboard.EVENTS.ON_SAVE_DASHBOARD,
//				event -> addDashboardToGrid((DashboardConfig) event.getData()));

//		homeTabbox.addEventListener(EVENTS.ON_CHANGE_LABEL, this::updateLabel);
		
//		CompositionService compositionService = (CompositionService) SpringUtil.getBean(Constants.COMPOSITION_SERVICE);

//		generateGridHeader();
//        gridProjects.addAll(projects);
//        modelList.addAll(projects);
        /*projectGrid = (ProjectGrid) thumbnailLayout.getAttribute(Constants.PROJECT);
        projectGrid.setProjects(gridProjects);
        BindUtils.postNotifyChange(null, null, projectGrid, PROJECT_STRING);
        gridSortDesc(dateItem, CompositionUtil.SORT_BY_DATE_DES);*/
//        entityList.setModel(modelList);
//        entityList.setRowRenderer(new CompositionRowRenderer(thumbnailLayout));
        
//        Column column = (Column) entityList.getColumns().getChildren().listIterator().next();
//        column.sort(true, true);
	}

	/*private List<Dashboard> getCompositions() throws CompositionServiceException {
		return ((CompositionService) SpringUtil.getBean(Constants.COMPOSITION_SERVICE)).getDashboards(
				((AuthenticationService) SpringUtil.getBean(Constants.AUTHENTICATION_SERVICE)).getCurrentUser());
	}
*/
	/*private void searchboxRenderer(Listitem item, Object dashboard, int i) {
		Dashboard dash = (Dashboard) dashboard;
		item.setLabel(dash.getLabel() + " by " + dash.getAuthor());
	}*/

	/*private void updateLabel(Event event) {
		Dashboard dashboard = (Dashboard) event.getData();
		List<Dashboard> tmpDashboards = dashboardGrid.getDashboards();
		// Returning when dashboard is not present
		if (!dashboardModel.contains(dashboard) || !(tmpDashboards.contains(dashboard))) {
			return;
		}
		dashboardModel.set(dashboardModel.indexOf(dashboard), dashboard);
		int tmpIndex = tmpDashboards.indexOf(dashboard);
		tmpDashboards.remove(tmpIndex);
		tmpDashboards.add(tmpIndex, dashboard);
		dashboardGrid.setDashboards(tmpDashboards);
		BindUtils.postNotifyChange(null, null, dashboardGrid, DASHBOARD_GRID_PROPERTY);
		gridSortDesc(dateItemDashboard, CompositionUtil.SORT_BY_DATE_DES);
	}*/

	private void showList() {
		sortMenuBar.setVisible(false);
		// refreshSortIcons();
		// clearSelections();
	}
	
	@Listen("onClick = #signOut")
	public void signOut(){
		((AuthenticationService) SpringUtil.getBean(Constants.AUTHENTICATION_SERVICE)).logout(new Object());
		Executions.sendRedirect("/eclBuilder/login.zul");
	}
	
	@Listen("onSelect = #homeTab")
	public void reloadBuilderList() {
		reloadBuilderList(true);
	}
	public void reloadBuilderList(boolean doRefresh) {
		try {
			ListModelList<Builder> dummy = new ListModelList<Builder>();

			String userID = "";

			try {

				userID = ((User) ((AuthenticationService) SpringUtil
						.getBean(Constants.AUTHENTICATION_SERVICE)).getCurrentUser()).getId();
			} catch (Exception e) {
				Executions.forward("/eclBuilder/home.zul");
				return;
			}

			if (userID == null) {
				Executions.forward("/eclBuilder/home.zul");
			}
			dummy.addAll(((EClBuilderDao) SpringUtil.getBean("EClBuilderDao")).getECLBuilders(userID));

			Columns cols = new Columns();
			
			Rows rows = new Rows();
			List<Component> test = new ArrayList<Component>();
			test.addAll(entityList.getChildren());
			
			
			for (Component comp : test) {
				if (comp instanceof Rows || comp instanceof Columns) {
					if(comp instanceof Rows){
						rows = (Rows) comp;
						clearChildren(rows);
					}else{
						cols = (Columns) comp;
					}
				}
			}
			if (cols.getChildren().size() == 0) {
				Column col = new Column(Labels.getLabel("builderName"));
				cols.appendChild(col);
				col = new Column(Labels.getLabel("byDate"));
				cols.appendChild(col);
				col = new Column(Labels.getLabel("builderActions"));
				cols.appendChild(col);
			} 
			
			
			Row row;
			Label lab1;
			Button button1;
			Div div;
			for (Builder build : dummy) {
				row = new Row();
				row.setAttribute("builderName", build.getName());
				row.appendChild(new Label(build.getName()));
				
				Timestamp timestamp = build.getTimestamp();
	 			
		        // S is the millisecond
		        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");

				row.appendChild(new Label(simpleDateFormat.format(timestamp).toString()));
				// row.appendChild(new Label(build.getAuthor()));

				div = new Div();
				button1 = new Button();
				button1.setClass("editButton");
				button1.addEventListener("onClick", event -> {
					editECLBuilder(event);
				});
				button1.setIconSclass("z-icon-edit");
				button1.setZclass("img-btn");
				button1.setStyle("color:orange;cursor:pointer");
				button1.setTooltiptext("Edit Builder");
				button1.setTooltip(Labels.getLabel("editbuilder"));
				div.appendChild(button1);
				button1 = new Button();
				button1.setClass("cloneButton");
				button1.setIconSclass("z-icon-copy");

				button1.addEventListener("onClick", event -> {
					cloneECLBuilder(event);
				});
				button1.setZclass("img-btn");
				button1.setStyle("color:blue;cursor:pointer");
				button1.setTooltiptext("Clone Builder");
				button1.setTooltip(Labels.getLabel("cloneDasboard"));
				div.appendChild(button1);
				button1 = new Button();
				button1.setClass("delButton");
				button1.setTooltiptext("Delete Builder");
				button1.addEventListener("onClick", event -> {
					deleteECLBuilder(event);
				});
				button1.setIconSclass("z-icon-trash-o");
				button1.setZclass("img-btn");
				button1.setStyle("color:red;cursor:pointer");
				button1.setTooltip(Labels.getLabel("deleteTitle"));
				div.appendChild(button1);
				row.appendChild(div);
				rows.appendChild(row);
			}
			entityList.appendChild(cols);
			entityList.appendChild(rows);
			if(doRefresh){
			entityList.invalidate();
			entityList.renderAll();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void generateGridHeader() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return;

    }

	public void cloneECLBuilder(Event e) {

		Executions.getCurrent().setAttribute("userAction", "clone");
		Executions.getCurrent().setAttribute("builderName", (String) ((Row)(e.getTarget().getParent().getParent())).getAttribute("builderName"));

		String builderName = (String) ((Row)(e.getTarget().getParent().getParent())).getAttribute("builderName");
		try {
			
			List<Builder> eclBuilders;
			
			String userID = ((User) ((AuthenticationService) SpringUtil.getBean(Constants.AUTHENTICATION_SERVICE))
					.getCurrentUser()).getId();

			eclBuilders = ((EClBuilderDao) SpringUtil.getBean("EClBuilderDao")).getECLBuilder(userID, builderName, "");

			if (eclBuilders.size() == 0) {
				return;
			}
//			include.setId("eclBuilderInclude");
			Executions.getCurrent().setAttribute("hpccConnId", Executions.getCurrent().getAttribute("hpccConnID"));
			Executions.getCurrent().setAttribute("builderName", Executions.getCurrent().getAttribute("builderName"));
			Executions.getCurrent().setAttribute("userAction", "clone");
			
			HashMap<String, String> map = new HashMap<>();
			map.put("userActionIsClone", "true");
			map.put("builderName", builderName);
			
			Window window = (Window) Executions.createComponents(NEW_DASHBOARD_URI, getSelf(), map);
			window.setTitle("Clone ECL Builder");
			window.doModal();
		} catch (Exception e1) { 
				 // TODO Auto-generated catch block 
				 e1.printStackTrace(); }
	}

//	@Listen("onClick = .editButton")
	public void editECLBuilder(Event e) {

		try {
			String builderName = (String) ((Row)(e.getTarget().getParent().getParent())).getAttribute("builderName");
			for(Component currentTab : homeTabbox.getTabs().getChildren()){
				if(((Tab)currentTab).getLabel().equals(builderName.toUpperCase())){
					homeTabbox.setSelectedTab((Tab)currentTab);
					return;
				}
			}

			String userID = ((User) ((AuthenticationService) SpringUtil.getBean(Constants.AUTHENTICATION_SERVICE))
					.getCurrentUser()).getId();
			List<Builder> eclBuilders;

			eclBuilders = ((EClBuilderDao) SpringUtil.getBean("EClBuilderDao")).getECLBuilder(userID, builderName, "");

			if (eclBuilders.size() == 0) {
				return;
			}
			Builder selectedBuilder = eclBuilders.get(0);
			Tab tab = new Tab();
			tab.setClosable(true);
			tab.addEventListener(Events.ON_CLOSE, event -> {
				reloadBuilderList();
//				generateGridHeader();
			});
			tab.setLabel(builderName.toUpperCase());
			Tabpanel tabpanel = new Tabpanel();
			Include include = new Include();
			// include.setId("eclBuilderInclude");
			Executions.getCurrent().setAttribute("hpccConnId", selectedBuilder.getHpccId());// ,
			Executions.getCurrent().setAttribute("userAction","edit");																										// Executions.getCurrent().getParameter("dashboardType"));
        	Executions.getCurrent().setAttribute("BuilderName", selectedBuilder.getName());
            include.setDynamicProperty("dashboardType",  Executions.getCurrent().getAttribute("dashboardType"));
            include.setDynamicProperty("selectedBuilder",  selectedBuilder);
            include.setDynamicProperty("hpccConnId",  selectedBuilder.getHpccId());
            include.setDynamicProperty("userAction",  "edit");
            include.setSrc("/eclBuilder/BuildECL.zul");
            
//			("updateHomeGrid", include, listener)
			tabpanel.appendChild(include);
			homeTabbox.getTabs().appendChild(tab);
			homeTabbox.getTabpanels().appendChild(tabpanel);

			tab.setSelected(true);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Listen("onClick = .delButton")
	public void deleteECLBuilder(Event e) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String builderName = (String) ((Row)(e.getTarget().getParent().getParent())).getAttribute("builderName");
		String userID = ((User) ((AuthenticationService) SpringUtil.getBean(Constants.AUTHENTICATION_SERVICE))
				.getCurrentUser()).getId();
		try {
			((EClBuilderDao) SpringUtil.getBean("EClBuilderDao")).deleteECLBuilder(userID, builderName);
			reloadBuilderList();
//			generateGridHeader();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}


	@SuppressWarnings("unchecked")
	private void createDashboardTab(Builder builder) {
		Tab tab = new Tab();
		tab.setClosable(true);
		Tabpanel tabpanel = new Tabpanel();

		tab.setLabel(builder.getName());
		tab.setAttribute(Constants.ECL_BUILDER, builder);

		tab.addEventListener(Events.ON_CLOSE, event -> {
			reloadBuilderList();
//			generateGridHeader();
		});

		tab.setClosable(true);
		Include include = new Include();
		// include.setId("eclBuilderInclude");
		Executions.getCurrent().setAttribute("hpccConnId", Executions.getCurrent().getAttribute("hpccConnID"));// ,
																												// Executions.getCurrent().getParameter("dashboardType"));
		Executions.getCurrent().setAttribute("BuilderName", builder.getName());
		include.setDynamicProperty("dashboardType", Executions.getCurrent().getAttribute("dashboardType"));
		include.setDynamicProperty("hpccConnID", Executions.getCurrent().getAttribute("hpccConnID"));
		include.setDynamicProperty("userAction", "create");
		
		include.setSrc("/eclBuilder/BuildECL.zul");
//		tabpanel.addEventListener("onCompleteUserAction", event -> {
//			reloadBuilderList();
//		});
		tabpanel.appendChild(include);
		homeTabbox.getTabs().appendChild(tab);
		homeTabbox.getTabpanels().appendChild(tabpanel);

		tab.setSelected(true);
	}

	@Listen("onClick = #newEclBuilder, #dataShaper")
	public void createDashboard(Event event) {
		try {
			if (getSelf().query("#createNewDashboardWindow") != null) {
				LOGGER.error(
						"Cannot create more than one component with the same id, must be an error due to user action(Clicking same button more than once)");
				return;
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Creating new Dashbaord");
			}

			HashMap<String, String> map = new HashMap<>();
			map.put("userActionIsClone", "false");
			Window window = (Window) Executions.createComponents(NEW_DASHBOARD_URI, getSelf(), map);
			window.setTitle("New ECL Builder");
			window.doModal();
		} catch (Exception ex) {
			LOGGER.error(Constants.EXCEPTION, ex);
			Clients.showNotification(UNABLE_TO_LOAD_DASHBOARD_TEMPLATE, Clients.NOTIFICATION_TYPE_ERROR, this.getSelf(),
					Constants.POSITION_TOP_CENTER, 5000, true);
			return;
		}
	}

	private void openECLBuilderTab(String builderName) {
		Executions.getCurrent().setAttribute("dashboardType", Executions.getCurrent().getAttribute("dashboardType"));
		Builder builder = new Builder();
		builder.setName(builderName);

		createDashboardTab(builder);
	}



	private void deleteOtherSortTypes(Menuitem item, String toClear) {
		if (item != nameItemDashboard) {
			nameItemDashboard.setAttribute(toClear, null);
		}
		if (item != authorItemDashboard) {
			authorItemDashboard.setAttribute(toClear, null);
		}
		if (item != dateItemDashboard) {
			dateItemDashboard.setAttribute(toClear, null);
		}
	}


	private void setSortIcon(String sort, Menuitem item) {
		if (sort == null) {
			item.setSclass("hiddenIcon");
		} else if (ASC.equals(sort)) {
			item.setSclass("");
			item.setIconSclass(FA_FA_SORT_AMOUNT_ASC);
		} else {
			item.setSclass("");
			item.setIconSclass(FA_FA_SORT_AMOUNT_DESC);
		}
	}

	private void showErrorNotification(String message) {
		Clients.showNotification(message, Clients.NOTIFICATION_TYPE_ERROR, getSelf(), Constants.POSITION_TOP_CENTER,
				5000, true);
	}

	public void onClickAbout(Event event){
		Div div = new Div();
		div.setSclass("alert alert-info");
		Label msgLabel = new Label("About ECLBuilder");
		msgLabel.setStyle("font-size:12px;");
		msgLabel.setParent(div);
		div.setStyle("align:center;");
//		event.getTarget().getParent().appendChild(div);
	}
	public void clearChildren(Component comp){
		if(comp.getChildren().size() ==0){
			return;
		}
		List<Component> chil = new ArrayList<Component>();
		chil.addAll(comp.getChildren());
		for(Component com : chil){
			comp.removeChild(com);
		}
	}
	
	@Listen("onClick=#clusterConfig")
	public void onClickPreferences(){
//		HashMap<String, String> map = new HashMap<>();
//		map.put("userActionIsClone", "false");
		Window window = (Window) Executions.createComponents("/eclBuilder/hpccClusterConfigPreferences.zul", getSelf(), null);
		window.doModal();
	}
}
