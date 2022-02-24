package simulacija;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.logging.Level;
import java.util.logging.Logger;

import mapa.objekti.Ambulanta;
import newgui.MainGUI;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class Watcher implements Runnable{
	
	private String fajlPutanja;
	
	public Watcher(String putanja) {
		fajlPutanja = putanja;
	}
	
	@SuppressWarnings("unchecked")
	public void run() {
		try {
			WatchService watcher = FileSystems.getDefault().newWatchService();
			Path fajl = Paths.get(".");
			fajl.register(watcher, ENTRY_MODIFY);
			while(true) {
				WatchKey key = null;
				try {
					key = watcher.take();
				}catch (InterruptedException e) {
					Logger.getLogger(Watcher.class.getName()).log(Level.SEVERE,e.toString());
				}
				
				for (WatchEvent<?> event : key.pollEvents()) {
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path fileName = ev.context();
					if(fileName.toString().trim().endsWith(fajlPutanja)) {
						int hospitalizovano=Ambulanta.getBrojBolesnika();
						int oporavljeni=Ambulanta.getBrojOporavljenih();
						MainGUI.brojZarazenihLabel.setText("BrojZarazenih: " + hospitalizovano);
						MainGUI.brojOporavljenihLabel.setText("BrojOporavljenih: " + oporavljeni);
					}
				}
				boolean valid = key.reset();
				if (!valid) {
					break;
				}
			}
		} catch (IOException e) {
			Logger.getLogger(Watcher.class.getName()).log(Level.SEVERE,e.toString());
		}
	}

}
