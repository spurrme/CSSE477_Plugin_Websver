
public class ConcatinateNumbersPlugin implements PluginFunction {

	public double calculate(double intNumber1, double intNumber2) {
		return intNumber1*10 + intNumber2;
	}

	public String getPluginName() {
		return "Concatinate Numbers";
	}

	public String getPluginSymbol() {
		return "C";
	}

	public String splitSymbol() {
		return this.getPluginSymbol();
	}

}
