package com.slayerhistory.localstorage;

import com.google.inject.Inject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import static net.runelite.client.RuneLite.RUNELITE_DIR;
import net.runelite.http.api.RuneLiteAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SlayerHistoryLocalStorage
{
	private static final File SLAYER_HISTORY_FOLDER = new File(RUNELITE_DIR, "slayer-history");
	private static final String SLAYER_HISTORY_FILE = "tasks.log";
	private static final Logger log = LoggerFactory.getLogger(SlayerHistoryLocalStorage.class);
	private File logFile;
	private String accountFolderName;

	@Inject
	public SlayerHistoryLocalStorage()
	{
		SLAYER_HISTORY_FOLDER.mkdir();
	}

	public boolean setAccountFolderName(final String folderName)
	{
		if (folderName.equalsIgnoreCase(this.accountFolderName))
		{
			return false;
		}

		logFile = new File(SLAYER_HISTORY_FOLDER, folderName);
		logFile.mkdir();
		this.accountFolderName = folderName;
		return true;
	}

	private File getFile()
	{
		return new File(logFile, SLAYER_HISTORY_FILE);
	}

	public synchronized ArrayList<SlayerHistoryRecord> loadSlayerHistoryRecords()
	{
		final File file = getFile();
		final ArrayList<SlayerHistoryRecord> data = new ArrayList<>();

		try (final BufferedReader br = new BufferedReader(new FileReader(file)))
		{
			String line;
			while ((line = br.readLine()) != null)
			{
				if (line.length() > 0)
				{
					final SlayerHistoryRecord r = RuneLiteAPI.GSON.fromJson(line, SlayerHistoryRecord.class);
					data.add(r);
				}
			}
		}
		catch (FileNotFoundException e)
		{
			log.debug("File not found: {}", file.getName());
		}
		catch (IOException e)
		{
			log.warn("IOException for file {}: {}", file.getName(), e.getMessage());
		}

		return data;
	}

	public synchronized void addSlayerHistoryRecord(SlayerHistoryRecord record)
	{
		final File slayerHistoryFile = getFile();
		final String dataAsString = RuneLiteAPI.GSON.toJson(record);
		try
		{
			final BufferedWriter file = new BufferedWriter(new FileWriter(String.valueOf(slayerHistoryFile), true));
			file.append(dataAsString);
			file.newLine();
			file.close();
			log.debug("Added new task: {}", dataAsString);
		}
		catch (IOException e)
		{
			log.warn("Error writing slayer task data to file {}: {}", slayerHistoryFile.getName(), e.getMessage());
		}
	}
}