package com.googlecode.syncnotes2google;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.syncnotes2google.util.FileHelper;

public class IDTable extends Object {

	private static final String ID_TABLE = "IDTable";
  private static IDTable table = null;
	private List<IdPair> ids = new ArrayList<IdPair>();
	private Map<String, IdPair> notes2google = new HashMap<String, IdPair>();
	private Map<String, IdPair> google2notes = new HashMap<String, IdPair>();

	private IDTable() {
		try {
			File file = FileHelper.getFile(ID_TABLE);
			if (!file.exists()) {
				try {
					saveTable();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line;
			while ((line = br.readLine()) != null) {
				String[] ids = line.split(" ");
				if (ids.length == 2) {
					IdPair id = new IdPair(ids[0], ids[1]);
					addIdPair(id);
				} else {
					System.out.println("Bad id mapping: " + line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addIdPair(IdPair id) {
		ids.add(id);
		notes2google.put(id.notesId, id);
		google2notes.put(id.googleId, id);
	}

	private void deleteIdPair(IdPair id) {
		if (id != null) {
			ids.remove(id);
			notes2google.remove(id.notesId);
			google2notes.remove(id.googleId);
		}
	}

	private IdPair getIdPairByGoogle(String id) {
		return google2notes.get(id);
	}

	private IdPair getIdPairByNotes(String id) {
		return notes2google.get(id);
	}

	private static class IdPair {
		private final String notesId;
		private final String googleId;

		public IdPair(String notesId, String googleId) {
			this.notesId = notesId;
			this.googleId = googleId;
		}
	}

	public static IDTable get() {
		if (table == null) {
			table = new IDTable();
		}
		return table;
	}

	public static String getNotesUNID(String gid) {
		IDTable idTable = IDTable.get();
		IdPair id = idTable.getIdPairByGoogle(gid);
		if (id != null) {
			return id.notesId;
		}
		return null;
	}

	public static String getGoogleUID(String notesUNID) {
		IDTable idTable = IDTable.get();
		IdPair id = idTable.getIdPairByNotes(notesUNID);
		if (id != null) {
			return id.googleId;
		}
		return null;
	}

	public static void insert(String notesUNID, String googleUID) {
		IdPair id = new IdPair(notesUNID, googleUID);
		IDTable idTable = IDTable.get();
		idTable.addIdPair(id);
	}

	public static void delete(String id) {
		IDTable idTable = IDTable.get();
		IdPair idPair = idTable.getIdPairByNotes(id);
		if (idPair == null) {
			idPair = idTable.getIdPairByGoogle(id);
		}
		idTable.deleteIdPair(idPair);
	}

	@Override
	protected void finalize() throws Throwable {
		saveTable();
		super.finalize();
	}

	public static void save() throws IOException {
		IDTable idTable = IDTable.get();
		idTable.saveTable();
	}

	private void saveTable() throws IOException {
    File file = FileHelper.getFile(ID_TABLE);
		FileWriter fw = new FileWriter(file);
		for (IdPair id : ids) {
			fw.write(id.notesId + " " + id.googleId + "\n");
		}
		fw.close();
	}
}