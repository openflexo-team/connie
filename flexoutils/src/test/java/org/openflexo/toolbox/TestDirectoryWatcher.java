/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexoutils, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.toolbox;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.Vector;

import static org.junit.Assert.*;

@Ignore
@RunWith(OrderedRunner.class)
public class TestDirectoryWatcher {

	static DirectoryWatcher watcher;
	static File tempDirectory;
	static File file1, file2, file3, file4, file5, file6;

	static File directory1, directory2;

	static Vector<DirectoryWatcherEvent> events;

	/*@AfterClass
	public static void tearDown() {
		System.out.println("Deleting " + tempDirectory);
		FileUtils.recursiveDeleteFile(tempDirectory);
	}*/

	private static void checkEventFile(File file, DirectoryWatcherEvent event) throws IOException {
		assertNotNull(event.getFile());
		assertEquals(file.getCanonicalPath(), event.getFile().getCanonicalPath());
	}

	@BeforeClass
	public static void setup() throws IOException {
		File parentTempDirectory = File.createTempFile("test", "directoryWatcher").getParentFile();
		tempDirectory = new File(parentTempDirectory, "TestDirectoryWatcher");
		if (tempDirectory.exists()) {
			FileUtils.recursiveDeleteFile(tempDirectory);
		}

		tempDirectory.mkdirs();
		System.out.println("Created " + tempDirectory);
		file1 = new File(tempDirectory, "file1");
		FileUtils.saveToFile(file1, "File1Contents");

		events = new Vector<DirectoryWatcherEvent>();

		watcher = new DirectoryWatcher(tempDirectory) {
			@Override
			protected void fileModified(File file) {
				System.out.println("File MODIFIED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
				events.add(new FileModifiedEvent(file));
			}

			@Override
			protected void fileAdded(File file) {
				System.out.println("File ADDED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
				events.add(new FileAddedEvent(file));
			}

			@Override
			protected void fileDeleted(File file) {
				System.out.println("File DELETED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
				events.add(new FileDeletedEvent(file));
			}

			@Override
			protected void fileRenamed(File oldFile, File renamedFile) {
				System.out.println("File RENAMED from " + oldFile.getName() + " to " + renamedFile.getName() + " in "
						+ renamedFile.getParentFile().getAbsolutePath());
				events.add(new FileRenamedEvent(oldFile, renamedFile));
			}
		};

		Timer timer = new Timer();
		timer.schedule(watcher, new Date(), 500);

		System.out.println("DirectoryWatcher is now initialized, starting tests...");

	}

	static class DirectoryWatcherEvent {
		private final File f;

		DirectoryWatcherEvent(File f) {
			this.f = f;
		}

		public File getFile() {
			return f;
		}
	}

	static class FileAddedEvent extends DirectoryWatcherEvent {
		public FileAddedEvent(File f) {
			super(f);
		}
	}

	static class FileModifiedEvent extends DirectoryWatcherEvent {
		public FileModifiedEvent(File f) {
			super(f);
		}
	}

	static class FileDeletedEvent extends DirectoryWatcherEvent {
		public FileDeletedEvent(File f) {
			super(f);
		}
	}

	static class FileRenamedEvent extends DirectoryWatcherEvent {
		private final File oldFile;

		public FileRenamedEvent(File from, File to) {
			super(to);
			oldFile = from;
		}

		public File getOldFile() {
			return oldFile;
		}
	}

	protected void log(String step) {
		System.err.println("\n******************************************************************************\n" + step
				+ "\n******************************************************************************\n");
	}

	@Test
	@TestOrder(1)
	public void testCreateBasicFile() throws IOException, InterruptedException {
		log("testCreateBasicFile");
		watcher.waitNextWatching();
		file2 = new File(tempDirectory, "file2");
		FileUtils.saveToFile(file2, "File2Contents");
		watcher.waitNextWatching();
		// Thread.sleep(1000);
		assertTrue(events.lastElement() instanceof FileAddedEvent);
		FileAddedEvent event = (FileAddedEvent) events.lastElement();
		checkEventFile(file2, event);
		assertEquals(1, events.size());
	}

	@Test
	@TestOrder(2)
	public void testDeleteBasicFile() throws IOException, InterruptedException {
		log("testDeleteBasicFile");
		watcher.waitNextWatching();
		file2.delete();
		watcher.waitNextWatching();
		// Thread.sleep(1000);
		assertTrue(events.lastElement() instanceof FileDeletedEvent);
		FileDeletedEvent event = (FileDeletedEvent) events.lastElement();
		checkEventFile(file2, event);
		assertEquals(2, events.size());
	}

	@Test
	@TestOrder(3)
	public void testModifyBasicFile() throws IOException, InterruptedException {
		log("testModifyBasicFile");
		watcher.waitNextWatching();
		FileUtils.saveToFile(file1, "NewFile1Contents");
		watcher.waitNextWatching();
		// Thread.sleep(1000);
		assertTrue(events.lastElement() instanceof FileModifiedEvent);
		FileModifiedEvent event = (FileModifiedEvent) events.lastElement();
		checkEventFile(file1, event);
		assertEquals(3, events.size());
	}

	@Test
	@TestOrder(4)
	public void testRenameBasicFile() throws IOException, InterruptedException {
		log("testRenameBasicFile");
		watcher.waitNextWatching();
		File renamedFile = new File(file1.getParentFile(), "file1-renamed");
		FileUtils.rename(file1, renamedFile);
		watcher.waitNextWatching();
		// Thread.sleep(1000);
		assertTrue(events.lastElement() instanceof FileRenamedEvent);
		FileRenamedEvent event = (FileRenamedEvent) events.lastElement();
		checkEventFile(renamedFile, event);
		assertEquals(4, events.size());
	}

	@Test
	@TestOrder(5)
	public void testCreateDirectoriesAndFiles() throws IOException, InterruptedException {
		log("testCreateDirectoriesAndFiles");
		watcher.waitNextWatching();
		directory1 = new File(tempDirectory, "directory1");
		directory1.mkdirs();
		directory2 = new File(directory1, "directory2");
		directory2.mkdirs();
		file3 = new File(directory1, "file3");
		FileUtils.saveToFile(file3, "File3Contents");
		file4 = new File(directory2, "file4");
		FileUtils.saveToFile(file4, "File4Contents");
		file5 = new File(directory2, "file5");
		FileUtils.saveToFile(file5, "File5Contents");

		watcher.waitNextWatching();

		assertEquals(9, events.size());
		assertTrue(events.get(events.size() - 1) instanceof FileAddedEvent);
		assertTrue(events.get(events.size() - 2) instanceof FileAddedEvent);
		assertTrue(events.get(events.size() - 3) instanceof FileAddedEvent);
		assertTrue(events.get(events.size() - 4) instanceof FileAddedEvent);
		assertTrue(events.get(events.size() - 5) instanceof FileAddedEvent);

	}

	@Test
	@TestOrder(6)
	public void testCreateBasicFileInDirectory() throws IOException, InterruptedException {
		log("testCreateBasicFileInDirectory");
		watcher.waitNextWatching();
		file6 = new File(directory2, "file6");
		FileUtils.saveToFile(file6, "File6Contents");
		watcher.waitNextWatching();
		// Thread.sleep(1000);
		assertTrue(events.lastElement() instanceof FileAddedEvent);
		FileAddedEvent event = (FileAddedEvent) events.lastElement();
		checkEventFile(file6, event);
		assertTrue(events.get(events.size() - 2) instanceof FileModifiedEvent);
		FileModifiedEvent event2 = (FileModifiedEvent) events.get(events.size() - 2);
		checkEventFile(directory2, event2);
		assertEquals(11, events.size());
	}

	@Test
	@TestOrder(7)
	public void testDeleteBasicFileInDirectory() throws IOException, InterruptedException {
		log("testDeleteBasicFileInDirectory");
		watcher.waitNextWatching();
		file6.delete();
		watcher.waitNextWatching();
		// Thread.sleep(1000);
		assertTrue(events.lastElement() instanceof FileDeletedEvent);
		FileDeletedEvent event = (FileDeletedEvent) events.lastElement();
		checkEventFile(file6, event);
		assertTrue(events.get(events.size() - 2) instanceof FileModifiedEvent);
		FileModifiedEvent event2 = (FileModifiedEvent) events.get(events.size() - 2);
		checkEventFile(directory2, event2);
		assertEquals(13, events.size());
	}

	@Test
	@TestOrder(8)
	public void testModifyBasicFileInDirectory() throws IOException, InterruptedException {
		log("testModifyBasicFile");
		watcher.waitNextWatching();
		FileUtils.saveToFile(file5, "NewFile5Contents");
		watcher.waitNextWatching();
		// Thread.sleep(1000);
		assertTrue(events.lastElement() instanceof FileModifiedEvent);
		FileModifiedEvent event = (FileModifiedEvent) events.lastElement();
		checkEventFile(file5, event);
		assertEquals(14, events.size());
	}

	@Test
	@TestOrder(9)
	public void testRenameBasicFileInDirectory() throws IOException, InterruptedException {
		log("testRenameBasicFileInDirectory");
		watcher.waitNextWatching();
		File renamedFile = new File(file5.getParentFile(), "file5-renamed");
		FileUtils.rename(file5, renamedFile);
		watcher.waitNextWatching();
		// Thread.sleep(1000);
		assertTrue(events.lastElement() instanceof FileRenamedEvent);
		FileRenamedEvent event = (FileRenamedEvent) events.lastElement();
		checkEventFile(renamedFile, event);
		assertTrue(events.get(events.size() - 2) instanceof FileModifiedEvent);
		FileModifiedEvent event2 = (FileModifiedEvent) events.get(events.size() - 2);
		checkEventFile(directory2, event2);
		assertEquals(16, events.size());
	}

	@Test
	@TestOrder(10)
	public void testRenameDirectory1() throws IOException, InterruptedException {
		log("testRenameDirectory1");
		watcher.waitNextWatching();
		File renamedDirectory = new File(directory1, "Directory2-renamed");
		FileUtils.rename(directory2, renamedDirectory);
		watcher.waitNextWatching();
		// Thread.sleep(1000);
		assertTrue(events.lastElement() instanceof FileRenamedEvent);
		FileRenamedEvent event = (FileRenamedEvent) events.lastElement();
		checkEventFile(renamedDirectory, event);
		assertTrue(events.get(events.size() - 2) instanceof FileModifiedEvent);
		FileModifiedEvent event2 = (FileModifiedEvent) events.get(events.size() - 2);
		checkEventFile(directory1, event2);
		assertEquals(18, events.size());
	}

}
