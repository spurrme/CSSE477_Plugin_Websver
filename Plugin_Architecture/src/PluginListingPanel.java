import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;


public class PluginListingPanel extends JPanel implements ActionListener{

	private static final long serialVersionUID = 1L;
	private ArrayList<PluginFunction> plugins;
	private PluginFunction currentPlugin;
	private StatusPanel status;

	public PluginListingPanel(StatusPanel status) {
		this.add(new JLabel("Plugins\n"));
		this.plugins  = new ArrayList<PluginFunction>();
		this.status = status;
		this.setLayout(new GridLayout(this.plugins.size(), 1));
		getPlugins();
		for (int i = 0; i < plugins.size(); i++) {
			PluginFunction currentPlugin = plugins.get(i);
			JButton current = new JButton(currentPlugin.getPluginName());
			this.add(current);
			current.addActionListener(this);
		}
		if(this.plugins.size() == 0){
			this.add(new JButton("No Plugins Found"));
		}
	}

	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < plugins.size(); i++) {
			if (plugins.get(i).getPluginName() == e.getActionCommand()){
				this.setCurrentPlugin(plugins.get(i));
				this.status.updateText("Plugin: " + this.currentPlugin.getPluginName() + " set as current Plugin");
			}
		}
	}
	protected void getPlugins() {
		File dir = new File(System.getProperty("user.dir") + File.separator + "\\plugins");
		ClassLoader cl = new PluginClassLoader(dir);
		if (dir.exists() && dir.isDirectory()) {
			String[] files = dir.list();
			for (int i=0; i<files.length; i++) {
				try {
					if (! files[i].endsWith(".class"))
						continue;

					Class<?> c = cl.loadClass(files[i].substring(0, files[i].indexOf(".")));
					Class<?>[] intf = c.getInterfaces();
					for (int j=0; j<intf.length; j++) {
						if (intf[j].getName().equals("PluginFunction")) {
							PluginFunction pf = (PluginFunction) c.newInstance();
							plugins.add(pf);
							continue;
						}
					}
				} catch (Exception ex) {
					System.err.println("File " + files[i] + " does not contain a valid PluginFunction class.");
				}
			}
		}
	}

	public void setCurrentPlugin(PluginFunction currentPlugin) {
		this.currentPlugin = currentPlugin;
	}

	public PluginFunction getCurrentPlugin() {
		return currentPlugin;
	}


}
