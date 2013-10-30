import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
public class CalculatorExecutionPanel extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JButton btn1 = new JButton("1");
    JButton btn2 = new JButton("2");
    JButton btn3 = new JButton("3");
    JButton btn_plus = new JButton("+");
    
    JButton btn4 = new JButton("4");
    JButton btn5 = new JButton("5");
    JButton btn6 = new JButton("6");
    JButton btn_minus = new JButton("-");
    
    JButton btn7 = new JButton("7");
    JButton btn8 = new JButton("8");
    JButton btn9 = new JButton("9");
    JButton btn_multiply = new JButton("*");
    
    JButton btn0 = new JButton("0");
    JButton btn_clr = new JButton("CLR");
    JButton btn_divide = new JButton("/");
    JButton btn_equal = new JButton("=");
    JButton btn_plugin = new JButton("PLG");

    TextField txt=new TextField(15);
    
    String str_number = "";
    int operation = 0;
    double int_number1 = 0;
    double int_number2 = 0;
    double result = 0;
	private PluginListingPanel plugins;
	private StatusPanel status;

	public CalculatorExecutionPanel(PluginListingPanel plugins, StatusPanel status) {
		 JFrame frame = new JFrame("Calculator");
		 	this.plugins = plugins;
		 	this.status = status;
	        frame.setSize(500,500);
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setResizable(false);
	        frame.setVisible(true);
	        
	        frame.setLayout(new BorderLayout());
	        
	        JPanel NumberPanel = new JPanel();
	        JPanel LabelPanel = new JPanel();
	  	        
	        NumberPanel.setLayout(new GridLayout(4,4));
	        LabelPanel.setLayout(new FlowLayout());
	        NumberPanel.add(btn7);
	        btn7.addActionListener(this);
	        NumberPanel.add(btn8);
	        btn8.addActionListener(this);
	        NumberPanel.add(btn9);
	        btn9.addActionListener(this);
	        NumberPanel.add(btn_plus);
	        btn_plus.addActionListener(this);
	        
	        NumberPanel.add(btn4);
	        btn4.addActionListener(this);
	        NumberPanel.add(btn5);
	        btn5.addActionListener(this);
	        NumberPanel.add(btn6);
	        btn6.addActionListener(this);
	        NumberPanel.add(btn_minus);
	        btn_minus.addActionListener(this);
	        
	        NumberPanel.add(btn1);
	        btn1.addActionListener(this);
	        NumberPanel.add(btn2);
	        btn2.addActionListener(this);
	        NumberPanel.add(btn3);
	        btn3.addActionListener(this);
	        
	        NumberPanel.add(btn_multiply);
	        btn_multiply.addActionListener(this);
	        
	        
	        NumberPanel.add(btn_clr);
	        btn_clr.addActionListener(this);
	        NumberPanel.add(btn0);
	        btn0.addActionListener(this);
	        NumberPanel.add(btn_plugin);
	        btn_plugin.addActionListener(this);
	        NumberPanel.add(btn_divide);
	        btn_divide.addActionListener(this);
	        
	        LabelPanel.add(new JLabel("NUMBER : "));
	        LabelPanel.add(txt);
	        LabelPanel.add(btn_equal);
	        btn_equal.addActionListener(this);
	        
	        txt.setEditable(false);
	        frame.add(plugins, BorderLayout.WEST);
	        frame.add(NumberPanel,BorderLayout.CENTER);
	        frame.add(LabelPanel,BorderLayout.NORTH);
	        frame.add(status, BorderLayout.SOUTH);
	        
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btn1) {
	           txt.setText("1");
	           str_number+=txt.getText();
	           txt.setText(str_number); 
	           this.status.updateText("1 Pressed");}
	    else if(e.getSource()==btn2) {
	           txt.setText("2");
	           str_number+=txt.getText();
	           txt.setText(str_number); 
	           }
	    else if(e.getSource()==btn3) {
	           txt.setText("3");
	           str_number+=txt.getText();
	           txt.setText(str_number); }
	    else if(e.getSource()==btn4) {
	           txt.setText("4");
	           str_number+=txt.getText();
	           txt.setText(str_number); }
	    else if(e.getSource()==btn5) {
	           txt.setText("5");
	           str_number+=txt.getText();
	           txt.setText(str_number); }
	    else if(e.getSource()==btn6) {
	           txt.setText("6");
	           str_number+=txt.getText();
	           txt.setText(str_number); }
	    else if(e.getSource()==btn7) {
	           txt.setText("7");
	           str_number+=txt.getText();
	           txt.setText(str_number); }
	    else if(e.getSource()==btn8) {
	           txt.setText("8");
	           str_number+=txt.getText();
	           txt.setText(str_number); }
	    else if(e.getSource()==btn9) {
	           txt.setText("9");
	           str_number+=txt.getText();
	           txt.setText(str_number); }
	    else if(e.getSource()==btn0) {
	           txt.setText("0");
	           str_number+=txt.getText();
	           txt.setText(str_number); }
	     else if(e.getSource()==btn_plus) {
	            if(operation==0 & str_number!="") {
	            int_number1=Integer.parseInt(str_number);
	            txt.setText("+");
	            str_number+=txt.getText();
	            txt.setText(str_number);
	            operation=1;
	            }
	            else { txt.setText(str_number); }
	            }
	     else if(e.getSource()==btn_minus) {
	         if(operation==0 & str_number!="") {
	            int_number1=Integer.parseInt(str_number);
	             txt.setText("-");
	             str_number+=txt.getText();
	             txt.setText(str_number);
	             operation=2;
	             }
	             else { txt.setText(str_number); }
	             }
	     else if(e.getSource()==btn_multiply) {
	         if(operation==0 & str_number!="") {
	            int_number1=Integer.parseInt(str_number);
	             txt.setText("*");
	             str_number+=txt.getText();
	             txt.setText(str_number);
	             operation=3;
	             }
	             else { txt.setText(str_number); }
	             }
	     else if(e.getSource()==btn_divide) {
	         if(operation==0 & str_number!="") {
	            int_number1=Integer.parseInt(str_number);
	             txt.setText("/");
	             str_number+=txt.getText();
	             txt.setText(str_number);
	             operation=4;
	             }
	             else { txt.setText(str_number); }
	             }
	     else if(e.getSource()==btn_plugin) {
	    	 if(this.plugins.getCurrentPlugin() == null){
	    		 this.status.txt.setText("Error:  Must select a plugin");
	    	 }
	    	 else if(operation==0 & str_number!="") {
	        	 this.status.updateText("Plugin Pressed");
	            int_number1=Integer.parseInt(str_number);
	             txt.setText(this.plugins.getCurrentPlugin().getPluginSymbol());
	             str_number+=txt.getText();
	             txt.setText(str_number);
	             operation=5;
	             }
	          else { txt.setText(str_number); }
	             }
	     else if(e.getSource()==btn_equal) {
	         if(operation!=0 & str_number!="") {
	               txt.setText("=");
	               str_number+=txt.getText();
	               txt.setText(str_number);
	             switch(operation) {
	             case 1: {
	                 String[] args = str_number.split("\\+");
	                 int_number2=Integer.parseInt(args[1].replace("=",""));
	                 result=int_number1+int_number2;
	                 txt.setText(str_number+Double.toString(result));
	                 this.status.updateText("Operation Successful");
	                 break;
	             }
	             case 2: {
	                 String[] args = null;
	                 args = str_number.split("\\-");
	                 int_number2=Integer.parseInt(args[1].replace("=",""));
	                 result=int_number1-int_number2;
	                 txt.setText(str_number+Double.toString(result));
	                 this.status.updateText("Operation Successful");
	                 break;
	             }
	             case 3: {
	                 String[] args = str_number.split("\\*");
	                 int_number2=Integer.parseInt(args[1].replace("=",""));
	                 result=int_number1*int_number2;
	                 txt.setText(str_number+Double.toString(result));
	                 this.status.updateText("Operation Successful");
	                 break;
	             }
	             case 4: {
	                 String[] args =  str_number.split("\\/");
	                 int_number2=Integer.parseInt(args[1].replace("=",""));
	                 result=int_number1/int_number2;
	                 txt.setText(str_number+Double.toString(result));
	                 this.status.updateText("Operation Successful");
	                 break;
	             }
	             case 5: {
	            	 String[] args = str_number.split(this.plugins.getCurrentPlugin().splitSymbol());
	                 int_number2=Integer.parseInt(args[1].replace("=",""));
	                 result = this.plugins.getCurrentPlugin().calculate(int_number1, int_number2);
	                 txt.setText(str_number+Double.toString(result));
	                 this.status.updateText("Operation Successful");
	                 break;
	             }
	             }
	             btn0.setEnabled(false);
	             btn1.setEnabled(false);
	             btn2.setEnabled(false);
	             btn3.setEnabled(false);
	             btn4.setEnabled(false);
	             btn5.setEnabled(false);
	             btn6.setEnabled(false);
	             btn7.setEnabled(false);
	             btn8.setEnabled(false);
	             btn9.setEnabled(false);
	             btn_plus.setEnabled(false);
	             btn_minus.setEnabled(false);
	             btn_multiply.setEnabled(false);
	             btn_divide.setEnabled(false);
	             btn_equal.setEnabled(false);
	             btn_plugin.setEnabled(false);
	         }
	         else { txt.setText("ERROR"); }
	     }
	     else if(e.getSource()==btn_clr) {
	         txt.setText("");
	         str_number = "";
	         operation = 0;
	         int_number1 = 0;
	         int_number2 = 0;
	         result = 0;
	         btn0.setEnabled(true);
	        btn1.setEnabled(true);
	        btn2.setEnabled(true);
	        btn3.setEnabled(true);
	        btn4.setEnabled(true);
	        btn5.setEnabled(true);
	        btn6.setEnabled(true);
	        btn7.setEnabled(true);
	        btn8.setEnabled(true);
	        btn9.setEnabled(true);
	        btn_plus.setEnabled(true);
	        btn_minus.setEnabled(true);
	        btn_multiply.setEnabled(true);
	        btn_divide.setEnabled(true);
	        btn_equal.setEnabled(true);
	        btn_plugin.setEnabled(true);
	     }
	}

}
