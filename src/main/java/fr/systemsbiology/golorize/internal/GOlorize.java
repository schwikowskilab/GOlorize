/*
 * #%L
 * Cyni Implementation (cyni-impl)
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2006 - 2013 The Cytoscape Consortium
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */
package fr.systemsbiology.golorize.internal;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.io.File;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;



import javax.swing.*;

import fr.systemsbiology.golorize.internal.BiNGO.*;
import fr.systemsbiology.golorize.internal.ui.*;
import fr.systemsbiology.golorize.internal.ontology.*;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.task.DynamicTaskFactoryProvisioner;
import org.cytoscape.property.CyProperty;
import org.cytoscape.task.NetworkViewTaskFactory;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.PanelTaskManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.ColumnCreatedEvent;
import org.cytoscape.model.events.ColumnCreatedListener;
import org.cytoscape.model.events.ColumnDeletedEvent;
import org.cytoscape.model.events.ColumnDeletedListener;
import org.cytoscape.model.events.ColumnNameChangedListener;
import org.cytoscape.model.events.ColumnNameChangedEvent;
import org.cytoscape.model.events.TableAddedEvent;
import org.cytoscape.model.events.TableAddedListener;
import org.cytoscape.model.events.TableAboutToBeDeletedEvent;
import org.cytoscape.model.events.TableAboutToBeDeletedListener;
import org.cytoscape.model.events.NetworkAddedEvent;
import org.cytoscape.model.events.NetworkAddedListener;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.view.vizmap.VisualMappingFunctionFactory;
import org.cytoscape.view.model.VisualLexicon;
import org.cytoscape.view.presentation.RenderingEngineManager;
import org.cytoscape.view.presentation.property.values.CyColumnIdentifierFactory;





public class GOlorize extends JFrame {
	private final static long serialVersionUID = 1202339874277105L;
	private TaskFactory currentCyni = null;

	// Dialog components
	private JPanel mainPanel; // The main content pane
	private JPanel buttonBox; // Our action buttons (Save Settings, Cancel, Execute, Done)
	private JComboBox algorithmSelector; // Which algorithm we're using
	private JComboBox tableSelector; // Which algorithm we're using
	private JPanel algorithmPanel; // The panel this algorithm uses
	private JButton executeButton;

	private CyApplicationManager appMgr;
	private boolean initialized;
	private CyNetworkViewManager viewMgr;
	private VisualMappingManager vmMgr;
	private CyRootNetworkManager rootNetMgr;
	private String executeButtonName;
	private String selectPanelName;
	private final CySwingAppAdapter adapter;
	private final OpenBrowser openBrowserService;
	private final SynchronousTaskManager<?> syncTaskManager;
	private final CyServiceRegistrar serviceRegistrar;
	
	private StartPanel startPanel;
	private LayoutPanel layoutPanel;
	private int resultPanelCount = 0;
	
	private final JTabbedPane jTabbedPane;
    private JComboBox genesLinked;
    private JComboBox genesLinkedView;
    private JFrame bingoWindow = new JFrame("BiNGO Settings");
    private Map<CyNetworkView,PieChartInfo> network_Options = new HashMap<CyNetworkView,PieChartInfo>();
    private HashMap goTerm_Annotation = new HashMap();
    
    private int[][] automaticColorArray = { { 0, 0, 255 }, { 255, 102, 102 }, { 153, 255, 153 }, { 255, 255, 0 },
			{ 204, 0, 204 }, { 153, 153, 153 }, { 255, 153, 51 }, { 0, 255, 204 }, { 102, 102, 0 }, { 0, 153, 0 },
			{ 51, 51, 51 }, { 153, 153, 255 }, { 255, 0, 0 }, { 102, 0, 153 }, { 255, 204, 255 }, { 255, 255, 153 },
			{ 0, 102, 102 }, { 255, 255, 255 }, { 255, 102, 255 }, { 0, 0, 0 } };
	private boolean[] automaticColorUsed = new boolean[getAutomaticColorArray().length];
	private HashMap termAutomaticlyColored = new HashMap();

	private Color layoutColor = new java.awt.Color(51, 255, 51);
	private Color noLayoutColor = Color.WHITE;
	private boolean bingoLaunched;

	private CyNetworkView networkView;
	private CustomChartListener customChartListener;

	private HashMap goColor = new HashMap();
	
	private static final Dimension DEF_COMBOBOX_SIZE = new Dimension(3000, 30);

	
	public GOlorize(final CyRootNetworkManager rootNetMgr,
						final CyNetworkViewManager networkViewManager,
	                    final CyApplicationManager appMgr,
	                    final VisualMappingManager vmMgr,
	                    final CyServiceRegistrar serviceRegistrar,
	                    final CySwingAppAdapter adapter, 
	                    final OpenBrowser openBrowserService, 
	                    final SynchronousTaskManager<?> syncTaskManager,
	                    final CustomChartListener customChartManager)
	{
		
		this.appMgr = appMgr;
		this.viewMgr = networkViewManager;
		this.rootNetMgr = rootNetMgr;
		this.vmMgr = vmMgr;
		this.adapter = adapter;
		this.openBrowserService = openBrowserService;
		this.syncTaskManager = syncTaskManager;
		this.serviceRegistrar = serviceRegistrar;
		this.customChartListener = customChartManager;
		
		this.jTabbedPane=new javax.swing.JTabbedPane(JTabbedPane.TOP);
	
		initComponents();
		
		addLayoutPanel();
		
	}

	private void initComponents() { 
        
        JPanel northPanel = new JPanel();      
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize(); 
      

        getJTabbedPane().setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
      
        JButton launchBiNGOButton = new JButton("Start BiNGO");
        northPanel.add(launchBiNGOButton);
        launchBiNGOButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent ev){
                            startBiNGO();
                        }
        });
        
        genesLinked = new JComboBox();
        getGenesLinked().addItem("All nodes in view");
        getGenesLinked().addItem("Selected nodes only");
        getGenesLinked().setSelectedItem("All current view nodes");
        northPanel.add(new JLabel("Apply coloring"));
        northPanel.add(getGenesLinked());
        
        /*genesLinkedView = new JComboBox(); 
        getGenesLinkedView().addItem("No coloring");
        getGenesLinkedView().addItem("Small pie size");
        getGenesLinkedView().addItem("Default pie size");
        getGenesLinkedView().addItem("Large pie size");
        getGenesLinkedView().setSelectedItem("Default pie size");
        northPanel.add(new JLabel("Coloring effect"));
        northPanel.add(getGenesLinkedView());
        getGenesLinkedView().addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent ev){
                Component comp = getJTabbedPane().getSelectedComponent();
                if (comp instanceof ResultPanel)
                    ((ResultPanel)comp).getDisplayPieChart().doIt(((ResultPanel)comp).displayPieChartListener.APPLY_CHANGE,
                            true,appMgr.getCurrentNetworkView());
                else if (comp instanceof StartPanel){
                    Component comp2 = getStartPanel().jTabbedPane.getSelectedComponent();
                    if (comp2 instanceof StartPanelPanel)
                        ((StartPanelPanel)comp2).getDisplayPieChart().doIt(((StartPanelPanel)comp2).getDisplayPieChart().
                                APPLY_CHANGE,true,appMgr.getCurrentNetworkView());
                    else {
                        ResultAndStartPanel all = getStartPanel().tabAll; 
                        all.getDisplayPieChart().doIt(all.getDisplayPieChart().
                                    APPLY_CHANGE,true,appMgr.getCurrentNetworkView());
                    }
                }
                else {
                    ResultAndStartPanel all = getStartPanel().tabAll; 
                    all.getDisplayPieChart().doIt(all.getDisplayPieChart().
                                APPLY_CHANGE,true,appMgr.getCurrentNetworkView());
                }
                
            }
        });*/
        
        getContentPane().add(northPanel,java.awt.BorderLayout.NORTH);
        
        getContentPane().add(getJTabbedPane(), java.awt.BorderLayout.CENTER);
       
        
        this.startPanel=new StartPanel(this,adapter);//startPanel.validate();this.validate();
        //pack();  
        getJTabbedPane().addTab("Selected", getStartPanel());//pack();   
        
       
        
        pack();   



        //this.setLocation(25,screenSize.height-450);
        //this.setSize(screenSize.width-50,400);
        this.addWindowListener(new WindowAdapter(){

            public void windowClosing(WindowEvent e){
                if (getBingoWindow()!=null)
                    getBingoWindow().dispose();
                if (getResultPanelCount()!=0){
                	GOlorize goBin=(GOlorize)e.getComponent();
                    //goBin.getResultPanelAt(1).displayPieChartListener.resetAll(goBin);

                }
            }
        });
        
        this.setLocation(25,screenSize.height-450);
        //this.setSize(screenSize.width-50,400);
        
        this.setSize(adapter.getCySwingApplication().getJFrame().getSize().width,400);

        setVisible(true);
        setResizable(true);
        this.validate();
        
        getStartPanel().jTabbedPane.setAlignmentY(getStartPanel().jTabbedPane.CENTER_ALIGNMENT);
        getStartPanel().jTabbedPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        getStartPanel().validate();
        
        
        
       
        
        
        this.repaint();
        
                
                
      } 
    
    void startBiNGO(){
        
    	CyNetworkView currentNetworkview = appMgr.getCurrentNetworkView();
            setBingoLaunched(true);
            
            String tmp = System.getProperty("user.dir") ;
            String bingoDir = new File(tmp,adapter.getCoreProperties().DEFAULT_PROPS_CONFIG_DIR+"/3/").toString() ;
            SettingsPanel settingsPanel = new SettingsPanel(bingoDir,rootNetMgr,viewMgr,appMgr,vmMgr, 
            		(CySwingAppAdapter)adapter, openBrowserService, syncTaskManager,this);
            getBingoWindow().getContentPane().removeAll();
            getBingoWindow().getContentPane().add(settingsPanel);
            
           
            getBingoWindow().addWindowListener(new WindowAdapter(){
                public void windowClosed(WindowEvent e){
                    setBingoLaunched(false);
                }
            });
            
            
            
            getBingoWindow().pack();
            Dimension screenSize =
              Toolkit.getDefaultToolkit().getScreenSize();
            
            getBingoWindow().setLocation(screenSize.width/2 - (getBingoWindow().getWidth()/2),
                           screenSize.height/2 - (getBingoWindow().getHeight()/2));
            getBingoWindow().setVisible(true);
            getBingoWindow().setResizable(true);
            
        
           
    }
    
    public void createResultTab(Map testMap, Map correctionMap, Map mapSmallX, Map mapSmallN, Map mapBigX, Map mapBigN,
			String alphaString, Annotation annotation, Map alias, Ontology ontology, String annotationFile,
			String ontologyFile, String testString, String correctionString, String overUnderString, String dirName,
			String fileName, String clusterVsString, String catString, Set selectedCanonicalNameVector,
			CyNetwork currentNetwork, CyNetworkView currentNetworkview) {

		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		ResultPanel result = new ResultPanel(testMap, correctionMap, mapSmallX, mapSmallN, mapBigX, mapBigN,
				alphaString, annotation, alias, ontology, annotationFile, ontologyFile, testString, correctionString,
				overUnderString, dirName, fileName, clusterVsString, catString, selectedCanonicalNameVector,
				currentNetwork, currentNetworkview, this, adapter);
        
        
        
        
        
        if (getResultPanelCount()!=0)
            result.setTabName(trouveBonNom(result.getTabName()));
         
        getJTabbedPane().addTab(result.getTabName(),result);

        getJTabbedPane().setSelectedIndex(getJTabbedPane().getTabCount()-1);
        
        
        
        result.validate();
        this.validate();
        
        
        resultPanelCount++;
        Iterator it = this.getStartPanelIterator();
        while (it.hasNext()){
            StartPanelPanel spp = (StartPanelPanel)it.next();
            spp.upDateAnnotationComboBox();
        }
        
    }
    private void addLayoutPanel(){
        //if (this == null) System.out.println("oui je me fouts bien de ta gueule");
        this.layoutPanel=new LayoutPanel(this, appMgr);
        getLayoutPanel().initComponents();
        //this.startPanel.jTabbedPane.addTab("Layout",layoutPanel.jPanelDeBase);
        this.getJTabbedPane().addTab("Layout",getLayoutPanel().jPanelDeBase);
        
        
        
        
        
        getLayoutPanel().jPanelDeBase.validate();
        getLayoutPanel().initAdvancedSettings();
        getLayoutPanel().getAdvSettings().validate();
        //layoutPanel.advSettings.setResizable(false);
        getLayoutPanel().getAdvSettings().setVisible(false);
        
        
        this.validate();
        getLayoutPanel().getAdvSettings().setLocation((int)(this.getX()+this.getSize().width/2  -
                getLayoutPanel().getAdvSettings().getSize().width/2),
                (int)(this.getY()+this.getSize().height/2  - getLayoutPanel().getAdvSettings().getSize().height/2));
    }
    
    
    
    private String trouveBonNom(String nom){
        String retour=nom;
        for (int i =1;i<getResultPanelCount()+1;i++){
            if (nom.equals(( (ResultPanel)getResultPanelAt(i) ).getTabName())){
                String aChanger = nom;
                
                if (aChanger.matches(".*[0123456789]$")){
                    String nombre2="";
                    String temp=aChanger;
                    while (temp.matches(".*[0123456789]$")){
                        nombre2 = temp.substring(temp.length()-1)+nombre2;
                        temp = temp.substring(0,temp.length()-1);
                    }
                    
                    int nombre=Integer.parseInt(nombre2)+1;
                    retour=temp+nombre;
                    retour=trouveBonNom(retour);
                }
                else {
                    retour=aChanger+"1";
                    retour=trouveBonNom(retour);
                }

            }
        }
        return retour;
    }
    
     void resetPieSelection(){
        JTable jtable;
        int selectColumn;
        ResultPanel result;
        for (int i =1;i<getResultPanelCount()+1;i++){
            result=(ResultPanel)getResultPanelAt(i);
            jtable=result.jTable1;
            selectColumn=result.SELECT_COLUMN;
            
            for (int j=0;j<jtable.getRowCount();j++){
                jtable.setValueAt(new Boolean(false),j,selectColumn);
            }
        }
        Iterator it= this.getStartPanelIterator();
        while (it.hasNext()){
            StartPanelPanel spp=(StartPanelPanel)it.next();
            jtable=spp.jTable1;
            selectColumn=spp.getSelectColumn();
            for (int j=0;j<jtable.getRowCount();j++){
                jtable.setValueAt(new Boolean(false),j,selectColumn);
            }
            
        }
        
        
    }
    
    
    
    void synchroColor(){
        JTable jtable;
        String goTerm;
        String goTerm2;
        Iterator it;
        ResultPanel result;
        for (int i =1;i<getResultPanelCount()+1;i++){
            result = (ResultPanel)getResultPanelAt(i);
            jtable=result.jTable1;
            
            
            
            
            
            for (int j=0;j<jtable.getRowCount();j++){
                goTerm=(String)jtable.getValueAt(j,result.GO_TERM_COLUMN);
                it = getGoColor().keySet().iterator();
                while (it.hasNext()){
                    goTerm2 = (String)it.next();
                    if (goTerm.equals(goTerm2)){
                        
                        result.goColor.put(goTerm,(Color)this.getGoColor().get(goTerm2));
                        ((JLabel)jtable.getValueAt(j,ResultPanel.DESCRIPTION_COLUMN)).setBackground((Color)this.getGoColor().get(goTerm2));
                    }
                }
            }
            
        }
        
        
    }
    
    void synchroColor(ResultPanel result){
        JTable jtable;
        String goTerm;
        String goTerm2;
        Iterator it;
        
        jtable=result.jTable1;

        for (int j=0;j<jtable.getRowCount();j++){
            goTerm=(String)jtable.getValueAt(j,result.GO_TERM_COLUMN);
            it = getGoColor().keySet().iterator();
            while (it.hasNext()){
                goTerm2 = (String)it.next();
                if (goTerm.equals(goTerm2)){

                    result.goColor.put(goTerm,(Color)this.getGoColor().get(goTerm2));
                    ((JLabel)jtable.getValueAt(j,ResultPanel.DESCRIPTION_COLUMN)).setBackground((Color)this.getGoColor().get(goTerm2));
                }
            }
        }
            
        
        
        
    }
    
    void synchroSelections(){
        ResultPanel result;
        JTable jtable;
        String goTerm;
        int temp;
        
        CyNetworkView currentNetworkView = appMgr.getCurrentNetworkView();
        Set goHashSet=new HashSet();//=((PieChartInfo)this.network_Options.get(currentNetworkView)).goToDisplay;
        PieChartInfo p = (PieChartInfo)this.getNetwork_Options().get(currentNetworkView);
        if (p!=null)
            goHashSet=p.goToDisplay;
        
        
        
        for (int i =1;i<getResultPanelCount()+1;i++){
            
            result = (ResultPanel)getResultPanelAt(i);
            jtable=result.jTable1;
            for (int j=0;j<jtable.getRowCount();j++){
                goTerm=(String)jtable.getValueAt(j,result.getGoTermColumn());
                if (goHashSet.contains(goTerm))
                    jtable.setValueAt(new Boolean(true),j,result.getSelectColumn());
                else
                    jtable.setValueAt(new Boolean(false),j,result.getSelectColumn());
               
            }
            
            //result.getGOlorize().repaint();
        }
        
        Iterator spit = this.getStartPanelIterator();
        getStartPanel().tabAll.unselectAll();
        while (spit.hasNext()){
            ((StartPanelPanel)spit.next()).unselectAll();
        }
        
        Iterator it = goHashSet.iterator();
        
        
        while(it.hasNext()){

            goTerm=(String)it.next();
            spit = this.getStartPanelIterator();
            while (spit.hasNext()){
                StartPanelPanel tab=(StartPanelPanel)spit.next();
                temp = tab.getTermIndex(goTerm);

                if (temp==-1){
                    Annotation ann = (Annotation)(getGoTerm_Annotation().get(goTerm));
                    
                    if (ann!=null)
                        tab.addLine(goTerm,ann,ann.getOntology());

                }
                else {
                    tab.getJTable().setValueAt(new Boolean(true),temp,tab.getSelectColumn());
                    //startPanel.tabAll.getJTable().
                }
            }

        }
            
            
            
        repaint();
        
        
        
        //if (startPanel.tabAll.getTermIndex(""))
        
        
        
    }
    
    void synchroSelections(ResultPanel result){
        
        JTable jtable;
        String goTerm;
        
        CyNetworkView currentNetworkView = appMgr.getCurrentNetworkView();
        
        try {
            Set goHashSet=((PieChartInfo) this.getNetwork_Options().get(currentNetworkView)).goToDisplay;


            jtable=result.jTable1;
            for (int j=0;j<jtable.getRowCount();j++){
                
                goTerm=(String)jtable.getValueAt(j,result.GO_TERM_COLUMN);
                if (goHashSet.contains(goTerm))
                    jtable.setValueAt(new Boolean(true),j,result.SELECT_COLUMN);
                else
                    jtable.setValueAt(new Boolean(false),j,result.SELECT_COLUMN);
                
            }
        }
        catch (Exception e){
            //on s'en fout c'etait juste le premier tab a etre cree
        }
        
    }
    
    
        
    
    void removeTab(ResultPanel result){
        for (int i =1;i<getResultPanelCount()+1;i++){
            if ((ResultPanel)getResultPanelAt(i)==result){
                getJTabbedPane().removeTabAt(i+1);
                break;
            }
            
        }
        resultPanelCount--;
    }
    
    JTable getResultTableAt (int i){
        return ((ResultPanel) getJTabbedPane().getComponentAt(i+1)).jTable1;
    }
    
    ResultPanel getResultPanelAt (int i){
        return (ResultPanel)getJTabbedPane().getComponentAt(i+1);
    }
    
    ResultPanel getResultTabAt (int i){
        return (ResultPanel)getJTabbedPane().getComponentAt(i);
    }
    
    StartPanel getStartPanel(){
        return this.startPanel;
    }
    
    Iterator getStartPanelIterator(){
        return new Iterator() {
            int iiii=-1;
            int ALL =0;
            int BP=1;
            int CC=2;
            int MF=3;
            int OTHER=4;
            StartPanel start = getStartPanel();
            StartPanelPanel[] spp= {start.tabAll,start.tabBioProcess,start.tabCellComponant,start.tabMolFunction,start.tabOther};
            
            public Object next(){
                iiii++;
                return spp[iiii];
                
            }
            public boolean hasNext(){
                return (iiii<4);
            }
            public void remove(){
                
            }
        };
        
        
    }
    
    
    
    Color getNextAutomaticColor(String term){
        for (int i=0;i<this.getAutomaticColorUsed().length;i++){
            if (!getAutomaticColorUsed()[i]){
                this.getTermAutomaticlyColored().put(term,new Integer(i));
                getAutomaticColorUsed()[i]=true;
                return new Color(this.getAutomaticColorArray()[i][0],getAutomaticColorArray()[i][1],getAutomaticColorArray()[i][2]);
                
            }
        }
        return null;
    }
    
    void resetAutomaticColor(){
        for (int i=0;i<getAutomaticColorUsed().length;i++){
            getAutomaticColorUsed()[i]=false;
            
        }
        getTermAutomaticlyColored().clear();
    }
    
    boolean freeAutomaticColor(String term){
        if (getTermAutomaticlyColored().containsKey(term)){
            int numColor = ((Integer)getTermAutomaticlyColored().get(term)).intValue();
            getAutomaticColorUsed()[numColor]=false;
            return true;
        }
        return false;
    }
    
    boolean isAutomaticlyColored(String term){
        return (getTermAutomaticlyColored().containsKey(term));
    }    
    boolean freeAutomaticColor(){
        for (int i=0;i<this.getAutomaticColorUsed().length;i++){
            if (!getAutomaticColorUsed()[i]){
                return true;
            }
        }
        return false;
    }

    public Map<CyNetworkView,PieChartInfo> getNetwork_Options() {
        return network_Options;
    }

    public void setNetwork_Options(Map<CyNetworkView,PieChartInfo> network_Options) {
        this.network_Options = network_Options;
    }

    public JTabbedPane getJTabbedPane() {
        return jTabbedPane;
    }

    public JComboBox getGenesLinked() {
        return genesLinked;
    }

    public JComboBox getGenesLinkedView() {
        return genesLinkedView;
    }

    public boolean isBingoLaunched() {
        return bingoLaunched;
    }

    public LayoutPanel getLayoutPanel() {
        return layoutPanel;
    }

    public int getResultPanelCount() {
        return resultPanelCount;
    }

    public HashMap getGoTerm_Annotation() {
        return goTerm_Annotation;
    }

    public HashMap getGoColor() {
        return goColor;
    }

    public void setGoColor(HashMap goColor) {
        this.goColor = goColor;
    }

    public int[][] getAutomaticColorArray() {
        return automaticColorArray;
    }

    public boolean[] getAutomaticColorUsed() {
        return automaticColorUsed;
    }

    public HashMap getTermAutomaticlyColored() {
        return termAutomaticlyColored;
    }

    public Color getLayoutColor() {
        return layoutColor;
    }

    public Color getNoLayoutColor() {
        return noLayoutColor;
    }

    public JFrame getBingoWindow() {
        return bingoWindow;
    }

    public void setBingoLaunched(boolean bingoLaunched) {
        this.bingoLaunched = bingoLaunched;
    }
    
    
    public VisualMappingManager getVisualMappingManager(){
    	return vmMgr;
    }
    
    public CustomChartListener getCustomChartsListener(){
    	return customChartListener;
    }
    
    public VisualMappingFunctionFactory getPassthroughMapper(){
    	return serviceRegistrar.getService(VisualMappingFunctionFactory.class, "(mapping.type=passthrough)");
    }
    
    public CyColumnIdentifierFactory getColumnIdFactory(){
    	return  serviceRegistrar.getService( CyColumnIdentifierFactory.class);
    }
    public VisualLexicon getVisualLexicon(){
    	return serviceRegistrar.getService(RenderingEngineManager.class).getDefaultVisualLexicon();
    }
    
    public VisualMappingFunctionFactory getVisualMappingFactory(){
    	return serviceRegistrar.getService( VisualMappingFunctionFactory.class, "(mapping.type=discrete)");
    }
}



/*class CyNetworkviewListener implements PropertyChangeListener {
    GOlorize goBin;
    public CyNetworkviewListener(GOlorize goB){
        this.goBin=goB;
    }
   
  
   
   
    public void propertyChange(PropertyChangeEvent event) {
                //String net_id = (String)event.getNewValue();
                //CyNetwork network = Cytoscape.getNetwork(net_id);
        if (!event.getPropertyName().equals(Cytoscape.getDesktop().NETWORK_VIEW_DESTROYED)){
                if (goBin.getNetwork_Options().containsKey(appMgr.getCurrentNetworkView()))
                    goBin.synchroSelections();
                else
                    goBin.resetPieSelection();
                //System.out.println("truc");
        }
        else {
            CyNetworkView cv;
            
            Iterator it=goBin.getNetwork_Options().keySet().iterator();
            
            while (it.hasNext()){
                cv= (CyNetworkView)it.next();
                if (!Cytoscape.getNetworkViewMap().containsValue(cv)){
                    goBin.getNetwork_Options().remove(cv);
                    break;
                }
            }
            goBin.resetPieSelection();
            
            
        }
                
    }
    
}*/
	


	
//}
