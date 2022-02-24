package newgui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import simulacija.Simulacija;
import simulacija.Watcher;
import narod.Stanovnik;

public class MainGUI extends JFrame{
	
	private static final long serialVersionUID = 1L;
	public static final Color defaultTextFieldColor = Color.DARK_GRAY;
	private static final int BLOCK_SIZE = 26;
	private static final int SPACING = 2;
	private static final String serijalizacijaPutanja = "serijalizacija.ser";
	private static final String statistikaPutanja = "bolnicaStatistika.txt";
	private static final String endFilePutanja = "SIM-JavaKov-20-";
	
	public static JTextField[][] textFieldMatrica;
	private static int MAP_SIZE;
	private Simulacija simulacija;
	public static Watcher watcher = new Watcher(statistikaPutanja);

	private JPanel contentPane;
	private static JTextArea textArea;
	private static Font font = new Font("Tahoma", Font.PLAIN, 10);
	private JButton posaljiAmbulanteButton;
	private JButton startButton;
	private JButton zaustaviSimulacijuButton;
	private JButton pokreniPonovoButton;
	public static JLabel brojZarazenihLabel;
	public static JLabel brojOporavljenihLabel;
	private JButton btnNewButton_2;

	public MainGUI(int mapSize,Simulacija simulacija) {
		this.simulacija = simulacija;
		MAP_SIZE = mapSize;
		textFieldMatrica = new JTextField[MAP_SIZE][MAP_SIZE];
		setTitle("Projekat");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200,830);
		setResizable(false);
		setLocationRelativeTo(null);
		contentPane = new JPanel(true);
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		initMatrica();
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(830, 2, 354, 410);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setFont(new Font("Arial", Font.PLAIN, 14));
		textArea.setBackground(UIManager.getColor("TextArea.background"));
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		posaljiAmbulanteButton = new JButton("POSALJI A. VOZILO");
		posaljiAmbulanteButton.addActionListener(e -> posaljiAmbulanteClicked());
		posaljiAmbulanteButton.setBounds(830, 481, 171, 31);
		contentPane.add(posaljiAmbulanteButton);
		
		startButton = new JButton("OMOGUCI KRETANJE");
		startButton.addActionListener(e -> {
			Simulacija.startTime = System.currentTimeMillis();
			Simulacija.startSimulacija();
			startButton.setEnabled(false);
			pokreniPonovoButton.setEnabled(false);
		});
		startButton.setBounds(830, 435, 171, 31);
		contentPane.add(startButton);
		
		brojZarazenihLabel = new JLabel("");
		brojZarazenihLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		brojZarazenihLabel.setBounds(1022, 435, 149, 31);
		brojZarazenihLabel.setText("BrojZarazenih: 0");
		contentPane.add(brojZarazenihLabel);
		
		brojOporavljenihLabel = new JLabel("");
		brojOporavljenihLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		brojOporavljenihLabel.setBounds(1022, 481, 149, 31);
		brojOporavljenihLabel.setText("BrojOporavljenih: 0");
		contentPane.add(brojOporavljenihLabel);
		
		JButton btnNewButton = new JButton("STANJE AMBULANTI");
		btnNewButton.addActionListener(e -> prikaziStatusAmbulanti());
		btnNewButton.setBounds(830, 522, 171, 31);
		contentPane.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("STATISTIKA");
		btnNewButton_1.addActionListener(e -> statistikaClicled());
		btnNewButton_1.setBounds(830, 563, 171, 31);
		contentPane.add(btnNewButton_1);
		
		btnNewButton_2 = new JButton("ZAVRSI SIMULACIJU");
		btnNewButton_2.addActionListener(e -> krajSimulacija());
		btnNewButton_2.setBounds(830, 686, 171, 31);
		contentPane.add(btnNewButton_2);
		
		zaustaviSimulacijuButton = new JButton("ZAUSTAVI SIMULACIJU");
		zaustaviSimulacijuButton.addActionListener(e -> zaustaviSimulaciju());
		zaustaviSimulacijuButton.setBounds(830, 604, 171, 31);
		contentPane.add(zaustaviSimulacijuButton);
		
		pokreniPonovoButton = new JButton("POKRENI PONOVO");
		pokreniPonovoButton.addActionListener(e -> pokreniPonovoSimulaciju());
		pokreniPonovoButton.setBounds(830, 645, 171, 31);
		contentPane.add(pokreniPonovoButton);
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		setVisible(true);
		
		// ove funkcije bi bolje bilo u ovoj klasi ispisati ali nemam vremena sad za to
		simulacija.postaviAmbulante();
		simulacija.postaviKontrolnePunktove(simulacija.brojKontPunktova);
		simulacija.postaviKuce(simulacija.brojKuca);
		simulacija.rasporediStanovnikePoKucama();
		Thread watcherThread = new Thread(watcher);
		watcherThread.setDaemon(true);
		watcherThread.start();
	}
	
	public static void updateTextField(int xStaro, int yStaro, int xNovo, int yNovo, String tekst, Color color) {
		textFieldMatrica[xStaro][yStaro].setBackground(defaultTextFieldColor);
		textFieldMatrica[xStaro][yStaro].setText("");
		textFieldMatrica[xNovo][yNovo].setBackground(color);
		textFieldMatrica[xNovo][yNovo].setText(tekst);
	}
	
	public static void setTextField(int x, int y,String text, Color color) {
		textFieldMatrica[x][y].setBackground(color);
		textFieldMatrica[x][y].setText(text);
	}
	
	public static void ispisNaTextArea(String tekst) {
		textArea.append(tekst);
		textArea.append("\n");
	}
	
	public void initMatrica() { 
		for(int i=0; i<MAP_SIZE; i++) {
			for(int j=0; j<MAP_SIZE; j++) {
				textFieldMatrica[i][j] = new JTextField();
				textFieldMatrica[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				textFieldMatrica[i][j].setEditable(false);
				textFieldMatrica[i][j].setFont(font);
				textFieldMatrica[i][j].setBackground(defaultTextFieldColor);
				textFieldMatrica[i][j].setBounds(i * BLOCK_SIZE + SPACING , (MAP_SIZE -j) * BLOCK_SIZE + SPACING , BLOCK_SIZE - 2*SPACING, BLOCK_SIZE - 2*SPACING);
				contentPane.add(textFieldMatrica[i][j]);
			}
		}
	}
	
	public void posaljiAmbulanteClicked() {
		Simulacija.sistemZaNadzor.posaljiAmbulantnaVozila();
	}
	
	public void prikaziStatusAmbulanti() {
		AmbulanteInfo ai = new AmbulanteInfo();
		ai.setVisible(true);
	}
	
	public void statistikaClicled() {
		StatistikaTabela tabela = new StatistikaTabela();
		tabela.setVisible(true);
	}
	
	public void krajSimulacija() {
		String datum = new SimpleDateFormat("HHmmss_ddMMyyyy").format(Calendar.getInstance().getTime());
		String putanja = endFilePutanja + datum + ".txt";
		long vrijemeTrajanja = new Date().getTime() - Simulacija.startTime;
		int brojStanovnika = Simulacija.listaStanovnika.size();
		int brojOdraslih = prebroj(Simulacija.listaStanovnika, covjek -> covjek.getGodine() >= 18 && covjek.getGodine() < 65);
		int brojStarih = prebroj(Simulacija.listaStanovnika, covjek -> covjek.getGodine() >= 65);
		int brojDjece = prebroj(Simulacija.listaStanovnika, covjek -> covjek.getGodine() < 18);
		int brojKreiranihKuca = Simulacija.listaKuca.size();
		int brojKreiranihVozila = simulacija.brojAmbulVozila;
		int brojKreiranihAmbulanti = simulacija.brojAmbulanti;
		int brojKreiranihKontrolnihPunktova = Simulacija.listaKP.size();
		vrijemeTrajanja/=1000;
		long temp = vrijemeTrajanja;
		vrijemeTrajanja = (temp/3600) + ((temp%3600)/60) + ((temp%3600)%60);
		try(PrintWriter out = new PrintWriter(new FileWriter(new File(putanja)))){
			out.println("Vrijeme trajanja simulacije: "+ vrijemeTrajanja);
			out.println("Broj kreiranih stanovnika: "+ brojStanovnika);
			out.println("Broj odraslih: "+ brojOdraslih);
			out.println("Broj starih: "+ brojStarih);
			out.println("Broj djece: "+ brojDjece);
			out.println("Broj kuca: "+ brojKreiranihKuca);
			out.println("Broj ambulantnih vozila: "+ brojKreiranihVozila);
			out.println("Broj ambulanti: "+ brojKreiranihAmbulanti);
			out.println("Broj kontrolnih punktova: "+ brojKreiranihKontrolnihPunktova);
			StatistikaTabela statistika = new StatistikaTabela();
			out.println("STATISTIKA");
			out.println("Ukupno zarazenih: "+ statistika.brojZarazenih);
			out.println("Ukupno oporavljenih: "+ statistika.brojOporavljenih);
			out.println("Zarazeno odraslih: "+ statistika.brojOdraslihZarazenih);
			out.println("Oporavljeno odraslih: "+statistika.brojOdraslihOporavljenih);
			out.println("Zarazeno starih: "+ statistika.brojStarihZarazenih);
			out.println("Oporavljeno starih: "+statistika.brojStarihOporavljenih);
			out.println("Zarazeno djece: "+ statistika.brojDjeceZarazenih);
			out.println("Oporavljeno djece: "+statistika.brojDjeceOporavljenih);
			out.println("Zarazeno muskih: "+ statistika.brojMuskihZarazenih);
			out.println("Oporavljeno muskih: "+ statistika.brojMuskihOporavljenih);
			out.println("Zarazeno zenskih: "+ statistika.brojZenskihZarazenih);
			out.println("Oporavljeno zenskih: "+statistika.brojZenskihOporavljenih);
		} catch (IOException e) {
			Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE,e.toString());
		}finally {
			System.exit(0);
		}
	}
	
	public void zaustaviSimulaciju() {
		zaustaviSimulacijuButton.setEnabled(false);
		Simulacija.mapa.lockMap();
		try {
			ObjectOutputStream oos= new ObjectOutputStream(new FileOutputStream(new File(serijalizacijaPutanja)));
			oos.writeObject(simulacija);
			oos.close();
		} catch (FileNotFoundException e) {
			Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE,e.toString());
		} catch (IOException e) {
			Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE,e.toString());
		}finally {
			Simulacija.mapa.unlockMap();
		}
		pokreniPonovoButton.setEnabled(true);
		posaljiAmbulanteButton.setEnabled(false);
	}
	
	public void pokreniPonovoSimulaciju() {
		pokreniPonovoButton.setEnabled(false);
		Simulacija.mapa.lockMap();
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(serijalizacijaPutanja)));
			simulacija = (Simulacija)ois.readObject();
			ois.close();
			Files.deleteIfExists(Paths.get(serijalizacijaPutanja));
		}catch(Exception e){
			Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE,e.toString());
		}finally {
			Simulacija.mapa.unlockMap();
		}
		zaustaviSimulacijuButton.setEnabled(true);
		posaljiAmbulanteButton.setEnabled(true);
	}
	
	public static int prebroj(List<Stanovnik> lista, Predicate<Stanovnik> predicate) {
		return lista.stream().filter(predicate).mapToInt(i -> 1).sum();
	}
	
	public static void main(String[] args) {
		PocetniEkran pocetniEkran = new PocetniEkran();
		pocetniEkran.setVisible(true);
	}
}
