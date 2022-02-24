package newgui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import simulacija.NedovoljnoOdraslihException;
import simulacija.Simulacija;

import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class PocetniEkran extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private static final int MIN_MAP_SIZE = 15;
	
	public Simulacija simulacija;
	private int mapSize;
	private JPanel contentPane;
	private JTextField brojOdraslihField;
	private JTextField brojStarihField;
	private JTextField brojDjeceField;
	private JTextField brojKucaField;
	private JTextField brojKPField;
	private JTextField brojAVField;
	private JLabel pogresanUnosLabel;
	private JButton startButton;
	private JLabel mapSizeLabel;

	public PocetniEkran() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 450);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setLocationRelativeTo(null);
		setTitle("Pocetni ekran");
		
		mapSize = generisiMapSize();
		
		brojOdraslihField = new JTextField();
		brojOdraslihField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		brojOdraslihField.setText("0");
		brojOdraslihField.setToolTipText("");
		brojOdraslihField.setBounds(24, 50, 154, 32);
		contentPane.add(brojOdraslihField);
		brojOdraslihField.setColumns(10);
		
		brojStarihField = new JTextField();
		brojStarihField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		brojStarihField.setText("0");
		brojStarihField.setBounds(24, 129, 154, 32);
		contentPane.add(brojStarihField);
		brojStarihField.setColumns(10);
		
		brojDjeceField = new JTextField();
		brojDjeceField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		brojDjeceField.setText("0");
		brojDjeceField.setBounds(24, 207, 154, 32);
		contentPane.add(brojDjeceField);
		brojDjeceField.setColumns(10);
		
		brojKucaField = new JTextField();
		brojKucaField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		brojKucaField.setText("0");
		brojKucaField.setBounds(245, 50, 154, 32);
		contentPane.add(brojKucaField);
		brojKucaField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("brojOdraslih");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel.setBounds(24, 10, 115, 27);
		contentPane.add(lblNewLabel);
		
		JLabel lblBroj = new JLabel("brojStarih");
		lblBroj.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblBroj.setBounds(24, 92, 115, 27);
		contentPane.add(lblBroj);
		
		JLabel lblBrojdjece = new JLabel("brojDjece");
		lblBrojdjece.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblBrojdjece.setBounds(24, 170, 115, 27);
		contentPane.add(lblBrojdjece);
		
		JLabel lblBrojkuca = new JLabel("brojKuca");
		lblBrojkuca.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblBrojkuca.setBounds(249, 10, 115, 27);
		contentPane.add(lblBrojkuca);
		
		brojKPField = new JTextField();
		brojKPField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		brojKPField.setText("0");
		brojKPField.setColumns(10);
		brojKPField.setBounds(245, 129, 154, 32);
		contentPane.add(brojKPField);
		
		brojAVField = new JTextField();
		brojAVField.setFont(new Font("Tahoma", Font.PLAIN, 13));
		brojAVField.setText("0");
		brojAVField.setColumns(10);
		brojAVField.setBounds(245, 207, 154, 32);
		contentPane.add(brojAVField);
		
		JLabel lblNewLabel_3_1 = new JLabel("brojKontrolnihPunktova");
		lblNewLabel_3_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_3_1.setBounds(245, 92, 136, 27);
		contentPane.add(lblNewLabel_3_1);
		
		JLabel lblNewLabel_3_2 = new JLabel("brojAmbulantnihVozila");
		lblNewLabel_3_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		lblNewLabel_3_2.setBounds(245, 170, 136, 27);
		contentPane.add(lblNewLabel_3_2);
		
		startButton = new JButton("START");
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int brojOdraslih = Integer.parseInt(brojOdraslihField.getText());
				int brojStarih = Integer.parseInt(brojStarihField.getText());
				int brojDjece = Integer.parseInt(brojDjeceField.getText());
				int brojKuca = Integer.parseInt(brojKucaField.getText());
				int brojKP = Integer.parseInt(brojKPField.getText());
				int brojAV = Integer.parseInt(brojAVField.getText());
				if(provjeriInput(mapSize,brojOdraslih, brojStarih, brojDjece, brojKuca, brojKP,brojAV)) {
					try {
						simulacija = new Simulacija(mapSize,brojOdraslih,brojStarih,brojDjece,brojKuca,brojKP,brojAV);
						MainGUI mainGUI= new MainGUI(mapSize, simulacija);
						mainGUI.setVisible(true);
						dispose();
					} catch (NedovoljnoOdraslihException e1) {
						Logger.getLogger(PocetniEkran.class.getName()).log(Level.SEVERE,e.toString());
					}
				}
				else {
					pogresanUnosLabel.setVisible(true);
					pogresanUnosLabel.setText("LOSI ULAZNI PODACI");
				}
				
			}
		});

		startButton.setBounds(167, 336, 97, 32);
		contentPane.add(startButton);
		
		pogresanUnosLabel = new JLabel("New label");
		pogresanUnosLabel.setHorizontalAlignment(SwingConstants.CENTER);
		pogresanUnosLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		pogresanUnosLabel.setBounds(117, 294, 199, 32);
		pogresanUnosLabel.setVisible(false);
		contentPane.add(pogresanUnosLabel);
		
		mapSizeLabel = new JLabel("");
		mapSizeLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		mapSizeLabel.setBounds(24, 336, 88, 23);
		contentPane.add(mapSizeLabel);
		mapSizeLabel.setText("Map size: "+ mapSize);
	}
	
	public boolean provjeriInput(int mSize, int brojOdraslih, int brojStarih, int brojDjece, int brojKuca, int brojKP, int brojAV) {
		int brojLjudi = brojOdraslih + brojDjece + brojStarih;
		if((brojLjudi < 1 || brojLjudi > 2*mSize)
				|| ((brojOdraslih + brojStarih) < 1)
				|| (brojKuca < 1 || brojKuca > mSize/2)
				|| (brojKP < 1 || brojKP > mSize/3)){
			return false; //
		}
		return true;
	}
	
	public int generisiMapSize() {
		return new Random().nextInt(MIN_MAP_SIZE + 1) + MIN_MAP_SIZE;
	}
}
