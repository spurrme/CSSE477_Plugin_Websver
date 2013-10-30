
public class ExponentialPlugin implements PluginFunction {

	public double calculate(double intNumber1, double intNumber2) {
		return Math.pow(intNumber1, intNumber2);
	}

	public String getPluginName() {
		return "Exponential";
	}

	public String getPluginSymbol() {
		return "^";
	}

	public String splitSymbol() {
		return "\\^";
	}

}
