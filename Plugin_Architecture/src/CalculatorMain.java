
public class CalculatorMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		StatusPanel status = new StatusPanel();
		PluginListingPanel plugin = new PluginListingPanel(status);
		new CalculatorExecutionPanel(plugin, status);
	}

}
