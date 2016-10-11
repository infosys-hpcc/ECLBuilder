package org.hpccsystems.eclBuilder.controller;

import java.util.List;

import org.hpccsystems.eclBuilder.Constants;
import org.hpccsystems.eclBuilder.dao.EClBuilderDao;
import org.hpccsystems.eclBuilder.entity.User;
import org.hpccsystems.eclBuilder.service.AuthenticationService;
import org.hpccsystems.ws.client.HPCCWsClient;
import org.hpccsystems.ws.client.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vlayout;
import org.zkoss.zul.Window;

@VariableResolver(org.zkoss.zkplus.spring.DelegatingVariableResolver.class)
public class NewECLBuilderController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(NewECLBuilderController.class);

    
    @Wire
    private Textbox eclBuilderName;

    final ListModelList<String> connectionsModel = new ListModelList<String>();
    final ListModelList<String> thorClusterModel = new ListModelList<String>();
    final ListModelList<String> roxieClusterModel = new ListModelList<String>();

    @Wire
    private Radiogroup datasource;

    @Wire
    private Vlayout hpccContainer;

    @Wire
    private Combobox connectionList;

    @Wire
    private Combobox thorCluster;
    @Wire
    private Combobox roxieCluster;

    @Wire
    private Textbox gcIdDashboard;
    
    String dsType;

    @Wire
    private Include searchInclude;

    @Wire
    private Button searchPopbtnDashboard;

    private String userAction = "Create";
    
    private String builderName;
    @Wire
    private Window createNewECLBuilderWindow;
    
    Platform platform ;
	
	HPCCWsClient connector;
	
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        dsType = (String) Executions.getCurrent().getAttribute("dashboardType");

		platform = TreeController.getPlatformForCluster();

        connector = platform.getHPCCWSClient();
        
        userAction = (String) Executions.getCurrent().getAttribute("userAction");
        builderName = (String) Executions.getCurrent().getAttribute("builderName");

        String[] clusters = connector.getAvailableClusterGroups();
    }


    @Listen("onClick = #continueBtn")
    public void createDashboardTab() {
    	
    	Tabbox homeTabbox = new Tabbox();
    	for(Component comp : createNewECLBuilderWindow.getParent().getChildren()){
    		if(comp instanceof Div && comp.getId().equals("homeTabboxDiv")){
    			if(null != comp.getFirstChild() && comp.getFirstChild().getId().equals("homeTabbox")){
    				homeTabbox = (Tabbox) comp.getFirstChild();
    			}
    		}
    	}
    	
    	Executions.getCurrent().setAttribute("hpccConnID","");//, Executions.getCurrent().getParameter("dashboardType"));
    	Executions.getCurrent().setAttribute("clonedBuilderName",eclBuilderName.getText());
    	if(null == userAction){
    		userAction = "create";
    	}
        Executions.getCurrent().setAttribute("userAction",  userAction);
        createNewECLBuilderWindow.detach();
        if(userAction.equals("clone") || userAction.equals("edit")){
        	try {
            	String clonedBuilderName = eclBuilderName.getText();
            	String userID = ((User) ((AuthenticationService) SpringUtil.getBean(Constants.AUTHENTICATION_SERVICE))
    					.getCurrentUser()).getId();
            	List<Builder> eclBuilders;
        		
        		eclBuilders = ((EClBuilderDao)SpringUtil.getBean("EClBuilderDao")).getECLBuilder(userID, builderName, "");
        		
            	if(eclBuilders.size() == 0){
            		return;
            	}
            	Builder selectedBuilder = eclBuilders.get(0);
                Tab tab = new Tab();
                tab.setClosable(true);
                tab.setLabel(clonedBuilderName.toUpperCase());
                Tabpanel tabpanel = new Tabpanel();
                Include include = new Include();
//                include.setId("eclBuilderInclude");
            	Executions.getCurrent().setAttribute("hpccConnId",Executions.getCurrent().getAttribute("hpccConnID"));//, Executions.getCurrent().getParameter("dashboardType"));
            	Executions.getCurrent().setAttribute("BuilderName",eclBuilderName.getText());
                include.setDynamicProperty("dashboardType",  Executions.getCurrent().getAttribute("dashboardType"));
                include.setDynamicProperty("selectedBuilder",  selectedBuilder);
                include.setDynamicProperty("userAction",  "clone");
                include.setSrc("/eclBuilder/BuildECL.zul");
                
                tabpanel.appendChild(include);
                
                homeTabbox.getTabs().appendChild(tab);
                homeTabbox.getTabpanels().appendChild(tabpanel);
                tab.setSelected(true);
              
        		} catch (Exception e1) {
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}
        	return;
        }
        
        try {

                boolean fileExists;

                fileExists = validateECLBuilderName(eclBuilderName.getValue());

                if (fileExists) {
                    Clients.showNotification(
                            Labels.getLabel("eclBuilderAlreadyExists1")
                                    .concat(eclBuilderName.getText().concat(Labels.getLabel("eclBuilderAlreadyExists2"))),
                            Clients.NOTIFICATION_TYPE_ERROR, eclBuilderName, Constants.POSITION_END_CENTER, 3000);
                    return;
                }
               
            Events.postEvent("onCreateNewBuilder", homeTabbox, eclBuilderName.getRawValue());
            Events.postEvent(Events.ON_CLOSE, getSelf(), null);

        } catch (WrongValueException e) {
            Clients.showNotification(Labels.getLabel("validDashboard"), Clients.NOTIFICATION_TYPE_ERROR,
                    eclBuilderName, Constants.POSITION_END_AFTER, 3000);
            LOGGER.error(Constants.EXCEPTION, e);
            return;
        } catch (Exception e) {
            LOGGER.error(Constants.EXCEPTION, e);
            Clients.showNotification(Labels.getLabel("unableToCreateComposition"), Clients.NOTIFICATION_TYPE_ERROR, eclBuilderName,
                    Constants.POSITION_END_AFTER, 3000);
           return;
        }
    }


    private boolean validateECLBuilderName(String builderName){
    	List<Builder> buildersList;
		try {
			
			String userID = ((User) ((AuthenticationService) SpringUtil.getBean(Constants.AUTHENTICATION_SERVICE))
					.getCurrentUser()).getId();

			
			buildersList = ((EClBuilderDao) SpringUtil.getBean("EClBuilderDao")).getECLBuilders(userID);
		
    	return buildersList.stream().filter(e -> e.getName().equals(builderName)).count() > 0 ?  true : false;
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		}
    }


    @Listen("onClick = #closeProjectDialog")
    public void onClose() {

        Events.postEvent(Events.ON_CLOSE, this.getSelf(), null);
    }

}
