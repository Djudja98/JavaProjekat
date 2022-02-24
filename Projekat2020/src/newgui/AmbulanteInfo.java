package newgui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import mapa.objekti.Ambulanta;
import simulacija.Simulacija;

import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.util.Random;

import static simulacija.Simulacija.mapa;

import java.awt.event.ActionEvent;

public class AmbulanteInfo extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public AmbulanteInfo() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 342);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(10, 10, 416, 241);
		contentPane.add(textArea);
		
		JButton btnNewButton = new JButton("KREIRAJ NOVU");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean imaPunaAmbulanta = false;
				for(Ambulanta a:Simulacija.listaAmbulante) {
					imaPunaAmbulanta = imaPunaAmbulanta || a.isFull();
				}
				if(imaPunaAmbulanta) {
					int x, y;
					do {
						x = (new Random().nextInt(Simulacija.mapa.getMapSize())) + 1;
						if(x!=0 && x!=mapa.getMapSize()-1) {
							y = new Random().nextBoolean()? 0 : (mapa.getMapSize()-1);
						} else {
							y = (new Random().nextInt(mapa.getMapSize())) + 1;
						}
					}while(! Simulacija.dodajAmbulantu(x, y));
					Ambulanta a = Simulacija.listaAmbulante.get(Simulacija.listaAmbulante.size()-1);
					new Thread(a).start();
				}
			}
		});
		btnNewButton.setEnabled(true);
		btnNewButton.setBounds(153, 268, 128, 27);
		contentPane.add(btnNewButton);
		
		for(Ambulanta a: Simulacija.listaAmbulante) {
			textArea.append("Ambulanta "+ a.getID() + " slobodno mjesta: " + a.getBrojSlobodnihMjesta());
			textArea.append("\n");
		}
	}
}
