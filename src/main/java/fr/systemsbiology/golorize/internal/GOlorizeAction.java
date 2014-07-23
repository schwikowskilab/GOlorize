package fr.systemsbiology.golorize.internal;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFrame;

import org.cytoscape.app.swing.CySwingAppAdapter;
import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.CyNetworkTableManager;
import org.cytoscape.model.CyTableManager;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.util.swing.OpenBrowser;
import org.cytoscape.view.layout.CyLayoutAlgorithmManager;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.view.vizmap.VisualMappingManager;
import org.cytoscape.work.SynchronousTaskManager;
import org.cytoscape.work.swing.PanelTaskManager;


public class GOlorizeAction extends AbstractCyAction {

	private static final String CURRENT_WORKING_DIRECTORY = "user.dir";
	private static final String MENU_NAME = "GOlorize";
	private static final String MENU_CATEGORY = "Apps";
	private static final String WINDOW_TITLE = "GOlorize";
	private final CySwingAppAdapter adapter;
	private final OpenBrowser openBrowserService;
	private final SynchronousTaskManager<?> syncTaskManager;
	private String bingoDir;
	private static final long serialVersionUID = 4190390703299860130L;
	private CyNetworkFactory netFactory;
	private CyNetworkViewFactory viewFactory;
	private CyNetworkManager netMgr;
	private CyNetworkViewManager viewMgr;
	private VisualMappingManager vmMgr;
	private CyNetworkTableManager netTableMgr;
	private CyRootNetworkManager rootNetMgr;
	private CyTableManager tableManager;
	private CySwingApplication desktop;
	private CyLayoutAlgorithmManager layoutManager;
	private CyApplicationManager appMgr;
	private PanelTaskManager taskManager;
	private CyServiceRegistrar serviceRegistrar;

	// The constructor sets the text that should appear on the menu item.
	public GOlorizeAction(
			final CyRootNetworkManager rootNetMgr,
			final CyNetworkViewManager networkViewManager,
            final CyApplicationManager appMgr,
            final VisualMappingManager vmMgr,
            final CyServiceRegistrar serviceRegistrar,
            final CySwingAppAdapter adapter, 
            final OpenBrowser openBrowserService, 
            final SynchronousTaskManager<?> syncTaskManager) {
		super(MENU_NAME);
		this.adapter = adapter;
		this.openBrowserService = openBrowserService;
		this.syncTaskManager = syncTaskManager;
		setPreferredMenu(MENU_CATEGORY);
		this.appMgr = appMgr;
		this.viewMgr = networkViewManager;
		this.rootNetMgr = rootNetMgr;
		this.vmMgr = vmMgr;
		this.serviceRegistrar = serviceRegistrar;
		
		//String cwd = System.getProperty(CURRENT_WORKING_DIRECTORY);
		String cwd =   System.getProperty("user.home");

		bingoDir = new File(cwd, adapter.getCoreProperties().DEFAULT_PROPS_CONFIG_DIR+"/3/apps/installed").toString();
	}

	/**
	 * This method opens the bingo settingspanel upon selection of the menu
	 * item and opens the settingspanel for bingo.
	 * 
	 * @param event
	 *            event triggered when bingo menu item clicked.
	 */
	public void actionPerformed(ActionEvent event) {
		//final JFrame window = new JFrame(WINDOW_TITLE);
		final GOlorize golPanel = new GOlorize(rootNetMgr,viewMgr,appMgr,vmMgr,(CySwingAppAdapter)adapter, openBrowserService, syncTaskManager);
		//window.getContentPane().add(golPanel);
		//window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		//window.pack();
		golPanel.setTitle(WINDOW_TITLE);

		// Cytoscape Main Window
		final JFrame desktop = adapter.getCySwingApplication().getJFrame();
		golPanel.setLocationRelativeTo(desktop);
		golPanel.setVisible(true);
	}
}
