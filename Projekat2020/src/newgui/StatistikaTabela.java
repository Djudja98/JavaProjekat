package newgui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import narod.CovjekStatistika;
import narod.Stanovnik.Pol;
import simulacija.Simulacija;

import static simulacija.Simulacija.listaZarazenih;
import static simulacija.Simulacija.listaOporavljenih;

import javax.swing.JTable;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class StatistikaTabela extends JFrame {
	// DAO BOG DA NIKAD VISE NE PISEM OVAKO NESTO
	private final static String STATISTIKA_PUTANJA = "statistika";
	int brojZarazenih;
	int brojOporavljenih;
	int brojMuskihZarazenih;
	int brojZenskihZarazenih;
	int brojMuskihOporavljenih;
	int brojZenskihOporavljenih;
	int brojOdraslihZarazenih;
	int brojOdraslihOporavljenih;
	int brojStarihZarazenih;
	int brojStarihOporavljenih;
	int brojDjeceZarazenih;
	int brojDjeceOporavljenih;

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table_1;
	private JScrollPane scrollPane_1;
	private JTable table_2;
	private JScrollPane scrollPane_2;
	private JButton prezuzmiCSVButton;
	private JTable table_3;

	public StatistikaTabela() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		izracunajStatistiku();
		File statDir = new File(STATISTIKA_PUTANJA);
		statDir.mkdir();
		
		String[] kolone = {"broj zarazenih","broj oporavljenih"};
		String[][] podaci = {{Integer.toString(brojZarazenih),Integer.toString(brojOporavljenih)}};
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(44, 39, 785, 63);
		contentPane.add(scrollPane);
		
		table_3 = new JTable(podaci, kolone);
		table_3.setRowHeight(40);
		
		scrollPane.setViewportView(table_3);
		
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(44, 146, 785, 103);
		contentPane.add(scrollPane_1);
		
		String[] kolone2 = {"","muski", "zenski"};
		String[][] podaci2 = {{"zarazenih",Integer.toString(brojMuskihZarazenih), Integer.toString(brojZenskihZarazenih)},
							  {"oporavljenih",Integer.toString(brojMuskihOporavljenih),Integer.toString(brojZenskihOporavljenih)}};
		table_1 = new JTable(podaci2,kolone2);
		table_1.setColumnSelectionAllowed(true);
		table_1.setRowHeight(40);
		scrollPane_1.setViewportView(table_1);
		
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(44, 305, 785, 103);
		contentPane.add(scrollPane_2);
		
		String[] kolone3 = {"", "odrasli","stari","djeca"};
		String[][] podaci3 = {{"zarazeno",Integer.toString(brojOdraslihZarazenih),Integer.toString(brojStarihZarazenih),Integer.toString(brojDjeceZarazenih)},
				{"oporavljeno",Integer.toString(brojOdraslihOporavljenih),Integer.toString(brojStarihOporavljenih),Integer.toString(brojDjeceOporavljenih)}};
		table_2 = new JTable(podaci3, kolone3);
		table_2.setRowHeight(40);
		scrollPane_2.setViewportView(table_2);
		
		prezuzmiCSVButton = new JButton("Preuzmi CSV");
		prezuzmiCSVButton.addActionListener(e -> upisiCSVStatistike());
		prezuzmiCSVButton.setBounds(374, 461, 134, 44);
		contentPane.add(prezuzmiCSVButton);
	}
	
	public static int prebroj(List<CovjekStatistika> lista, Predicate<CovjekStatistika> predicate) {
		return lista.stream().filter(predicate).mapToInt(i -> 1).sum();
	}
	
	public void izracunajStatistiku() {
		brojZarazenih = listaZarazenih.size();
		brojOporavljenih = listaOporavljenih.size();
		brojMuskihZarazenih = prebroj(Simulacija.listaZarazenih, covjek -> covjek.pol == Pol.MUSKI);
		brojMuskihOporavljenih = prebroj(Simulacija.listaOporavljenih, covjek -> covjek.pol == Pol.MUSKI);
		brojZenskihZarazenih = prebroj(Simulacija.listaZarazenih, covjek -> covjek.pol == Pol.ZENSKI);
		brojZenskihOporavljenih = prebroj(Simulacija.listaOporavljenih, covjek -> covjek.pol == Pol.ZENSKI);
		brojOdraslihZarazenih = prebroj(Simulacija.listaZarazenih, covjek -> covjek.godine >= 18 && covjek.godine < 65);
		brojOdraslihOporavljenih = prebroj(Simulacija.listaOporavljenih, covjek -> covjek.godine >= 18 && covjek.godine < 65);
		brojStarihZarazenih = prebroj(Simulacija.listaZarazenih, covjek -> covjek.godine >= 65);
		brojStarihOporavljenih = prebroj(Simulacija.listaOporavljenih, covjek -> covjek.godine >= 65);
		brojDjeceZarazenih = prebroj(Simulacija.listaZarazenih, covjek -> covjek.godine < 18);
		brojDjeceOporavljenih = prebroj(Simulacija.listaOporavljenih, covjek -> covjek.godine < 18);
	}
	
	// glupo ali najbrze za uraditi
	public void upisiCSVStatistike() {
		String putanjaMuski = STATISTIKA_PUTANJA + File.separator + "muski.csv";
		String putanjaZenski = STATISTIKA_PUTANJA + File.separator + "zenski.csv";
		String putanjaOdrasli = STATISTIKA_PUTANJA + File.separator + "odrasli.csv";
		String putanjaStari = STATISTIKA_PUTANJA + File.separator + "stari.csv";
		String putanjaDjeca = STATISTIKA_PUTANJA + File.separator + "djeca.csv";
		
		String muskiStat = brojMuskihZarazenih + "," + brojMuskihOporavljenih;
		String zenskiStat = brojZenskihZarazenih + "," + brojZenskihOporavljenih;
		String odrasliStat = brojOdraslihZarazenih + ","+ brojOdraslihOporavljenih;
		String stariStat = brojStarihZarazenih +"," + brojStarihOporavljenih;
		String djecaStat = brojDjeceZarazenih + ","+ brojDjeceOporavljenih;
		upisiCSV(new File(putanjaMuski), muskiStat);
		upisiCSV(new File(putanjaZenski), zenskiStat);
		upisiCSV(new File(putanjaOdrasli), odrasliStat);
		upisiCSV(new File(putanjaStari), stariStat);
		upisiCSV(new File(putanjaDjeca), djecaStat);
	}
	
	public void upisiCSV(File file, String tekst) { 
		PrintWriter outMuski = null;
		try {
			outMuski = new PrintWriter(new FileWriter(file));
			outMuski.println("zarazeno,oporavljeno");
			outMuski.println(tekst);
		} catch (IOException e) {
			Logger.getLogger(StatistikaTabela.class.getName()).log(Level.SEVERE,e.toString());
		}finally {
			outMuski.close();
		}
	}
	
}

