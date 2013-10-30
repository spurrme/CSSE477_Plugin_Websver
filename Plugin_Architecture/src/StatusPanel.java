import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class StatusPanel extends JPanel implements ActionListener{
	
	private static final long serialVersionUID = 1L;
	
	TextField txt=new TextField(40);
	
	public StatusPanel(){
		this.add(txt);
		txt.setEditable(false);
	}

	public void actionPerformed(ActionEvent arg0) {
		
	}
	
	public void updateText(String message){
		txt.setText(message);
	}

}
