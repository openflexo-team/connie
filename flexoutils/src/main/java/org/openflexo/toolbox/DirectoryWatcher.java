/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Flexo-foundation, a component of the software infrastructure 
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implements a timer task which recursively watch a directory looking for changes on file system
 * 
 * @author sylvain
 * 
 */
public abstract class DirectoryWatcher extends TimerTask {

	protected static final Logger logger = Logger.getLogger(DirectoryWatcher.class.getPackage().getName());

	private final NodeDirectoryWatcher rootDirectoryWatcher;

	protected static class NodeDirectoryWatcher {

		private final DirectoryWatcher watcher;
		private final File directory;
		private final Map<File, Long> lastModified = new HashMap<File, Long>();
		private final Map<File, NodeDirectoryWatcher> subNodes = new HashMap<File, NodeDirectoryWatcher>();
		private final Map<File, Integer> checksums = new HashMap<File, Integer>();

		private NodeDirectoryWatcher(File directory, DirectoryWatcher watcher, boolean notifyAdding) {
			// System.out.println("Init NodeDirectoryWatcher on " + directory);
			this.directory = directory;
			this.watcher = watcher;
			for (File f : directory.listFiles()) {
				lastModified.put(f, f.lastModified());
				recordChecksumForFile(f, false);
				if (f.isDirectory()) {
					subNodes.put(f, new NodeDirectoryWatcher(f, watcher, notifyAdding));
				}
				if (notifyAdding) {
					watcher.fileAdded(f);
				}
			}
		}

		private Integer recordChecksumForFile(File f, boolean force) {
			if (!f.exists()) {
				return null;
			}
			Integer checksum = checksums.get(f);
			if (checksum == null || force) {
				if (f.isDirectory()) {
					StringBuffer sb = new StringBuffer();
					for (File child : f.listFiles()) {
						sb.append(child.getName());
					}
					checksum = sb.toString().hashCode();
					// System.out.println("For file " + f + " checksum=" + checksum);
					checksums.put(f, checksum);

				}
				else {
					try {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("Computing checksum for " + f);
						}
						checksum = new String(FileUtils.getBytes(f, 32)).hashCode();
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("For file " + f + " checksum=" + checksum);
						}
					} catch (IOException e) {
						checksum = -1;
					}
					checksums.put(f, checksum);
				}
			}
			return checksum;
		}

		private boolean watch() {

			Set<File> checkedFiles = new HashSet<File>();

			List<File> modifiedFiles = new ArrayList<File>();
			List<File> addedFiles = new ArrayList<File>();
			List<File> deletedFiles = new ArrayList<File>();

			if (directory == null || !directory.exists()) {
				return false;
			}

			// scan the files and check for modification/addition
			for (File f : directory.listFiles()) {
				Long current = lastModified.get(f);
				checkedFiles.add(f);
				if (current == null) {
					// new file
					lastModified.put(f, f.lastModified());
					// watcher.fileAdded(f);
					addedFiles.add(f);
				}
				else if (current.longValue() != f.lastModified()) {
					// modified file
					lastModified.put(f, f.lastModified());
					// watcher.fileModified(f);
					modifiedFiles.add(f);
				}
			}

			// now check for deleted files
			Set<File> ref = new HashMap<File, Long>(lastModified).keySet();
			ref.removeAll(checkedFiles);
			Iterator<File> it = ref.iterator();
			while (it.hasNext()) {
				File deletedFile = it.next();
				// watcher.fileDeleted(deletedFile);
				deletedFiles.add(deletedFile);
			}

			// We try to detect renamed files

			/*if (addedFiles.size() > 0 && deletedFiles.size() > 0) {
				// List<File> filesToRemoveFromDeletedFiles = new ArrayList<File>();
				Iterator<File> iterator = deletedFiles.iterator();
				while (iterator.hasNext()) {
					File f1 = iterator.next();
					File renamedFile = null;
					boolean renameDetected = false;
					for (File f2 : addedFiles) {
						if (possiblyRename(f1, f2)) {
							renameDetected = true;
							renamedFile = f2;
							break;
						}
					}
					if (renameDetected) {
						watcher.fileRenamed(f1, renamedFile);
						addedFiles.remove(renamedFile);
						iterator.remove();
						deletedFiles.remove(f1);
						// filesToRemoveFromDeletedFiles.add(f1);
					}
				}
				// deletedFiles.removeAll(filesToRemoveFromDeletedFiles);
			}*/

			List<File> renamedFiles = new ArrayList<File>();
			List<File> derivedRenamedFiles = new ArrayList<File>();

			/*if (addedFiles.size() > 0 || deletedFiles.size() > 0) {
				System.out.println("addedFiles=" + addedFiles);
				System.out.println("deletedFiles=" + deletedFiles);
			}*/

			detectRenamedFiles(addedFiles, deletedFiles, renamedFiles, derivedRenamedFiles);

			for (File f : addedFiles) {
				recordChecksumForFile(f, true);
				if (f.isDirectory()) {
					subNodes.put(f, new NodeDirectoryWatcher(f, watcher, true));
				}
				watcher.fileAdded(f);
			}
			for (File deletedFile : deletedFiles) {
				checksums.remove(deletedFile);
				lastModified.remove(deletedFile);
				if (subNodes.get(deletedFile) != null) {
					subNodes.get(deletedFile).delete(true);
				}
				subNodes.remove(deletedFile);
				watcher.fileDeleted(deletedFile);
			}
			for (File f : modifiedFiles) {
				recordChecksumForFile(f, true);
				watcher.fileModified(f);
			}

			for (File k : subNodes.keySet()) {
				NodeDirectoryWatcher w = subNodes.get(k);
				// System.out.println("now watch for " + w.directory);
				if (!w.watch()) {
					subNodes.remove(k);
				}
			}

			return true;
		}

		private void detectRenamedFiles(List<File> addedFiles, List<File> deletedFiles, List<File> renamedFiles,
				List<File> derivedRenamedFiles) {

			RenamedMatch match = detectNextRenamedFiles(addedFiles, deletedFiles);
			while (match != null) {

				checksums.remove(match.oldFile);
				lastModified.remove(match.oldFile);
				if (subNodes.get(match.oldFile) != null) {
					subNodes.get(match.oldFile).delete(false);
				}
				subNodes.remove(match.oldFile);

				recordChecksumForFile(match.renamedFile, true);
				if (match.renamedFile.isDirectory()) {
					subNodes.put(match.renamedFile, new NodeDirectoryWatcher(match.renamedFile, watcher, false));
				}

				watcher.fileRenamed(match.oldFile, match.renamedFile);

				deletedFiles.remove(match.oldFile);
				addedFiles.remove(match.renamedFile);

				match = detectNextRenamedFiles(addedFiles, deletedFiles);
				/*if (renamedFile.isDirectory()) {
				NodeDirectoryWatcher ndw1 = subNodes.get(f1);
				NodeDirectoryWatcher ndw2 = subNodes.get(renamedFile);
				System.out.println("ndw1=" + ndw1 + " files=" + ndw1.subNodes.keySet());
				System.out.println("ndw2=" + ndw2 + " files=" + ndw2.subNodes.keySet());
				}*/
			}
		}

		private RenamedMatch detectNextRenamedFiles(List<File> addedFiles, List<File> deletedFiles) {
			if (addedFiles.size() > 0 && deletedFiles.size() > 0) {
				for (File f1 : deletedFiles) {
					for (File f2 : addedFiles) {
						if (f2.isDirectory()) {
							if (possiblyRename(f1, f2)) {
								return new RenamedMatch(f1, f2);
							}
						}
					}
				}
				for (File f1 : deletedFiles) {
					for (File f2 : addedFiles) {
						if (!f2.isDirectory()) {
							if (possiblyRename(f1, f2)) {
								return new RenamedMatch(f1, f2);
							}
						}
					}
				}
			}
			return null;
		}

		private class RenamedMatch {
			public RenamedMatch(File oldFile, File renamedFile) {
				super();
				this.oldFile = oldFile;
				this.renamedFile = renamedFile;
			}

			File oldFile;
			File renamedFile;

		}

		private boolean possiblyRename(File f1, File f2) {

			recordChecksumForFile(f2, true);
			String extension1 = f1.getName().lastIndexOf(".") > -1 ? f1.getName().substring(f1.getName().lastIndexOf(".")) : null;
			String extension2 = f2.getName().lastIndexOf(".") > -1 ? f2.getName().substring(f2.getName().lastIndexOf(".")) : null;
			if ((extension1 == null && extension2 == null) || (extension1 != null && extension1.equals(extension2))) {
				Integer checksum1 = checksums.get(f1);
				Integer checksum2 = checksums.get(f2);
				if (checksum1 == null) {
					return false;
				}
				return checksum1.equals(checksum2);
			}
			return false;
		}

		private void delete(boolean notify) {
			if (notify) {
				for (File f : lastModified.keySet()) {
					watcher.fileDeleted(f);
				}
			}
			for (NodeDirectoryWatcher w : subNodes.values()) {
				w.delete(notify);
			}
		}

	}

	public DirectoryWatcher(File directory) {
		super();
		rootDirectoryWatcher = new NodeDirectoryWatcher(directory, this, false);
		logger.info("Started DirectoryWatcher on " + directory + " ...");
		status = Status.INIT;
	}

	protected boolean isRunning = false;

	public boolean DEBUG = false;

	public NodeDirectoryWatcher getRootDirectoryWatcher() {
		return rootDirectoryWatcher;
	}

	public File getDirectory() {
		return rootDirectoryWatcher.directory;
	}

	public final void runNow() {
		isRunning = false;
		run();
		isRunning = false;
	}

	@Override
	public final void run() {

		if (isWaitingCurrentExecution) {
			return;
		}
		if (waitNextWatchingRequested) {
			waitNextWatchingDone = true;
		}
		if (isRunning) {
			return;
		}

		isRunning = true;
		if (status == Status.INIT) {
			status = Status.FIRST_RUN;
		}
		else if (status == Status.IDLE) {
			status = Status.RUNNING;
		}
		try {
			performRun();
		} catch (Exception e) {
			logger.warning("Unexpected exception in DirectoryWatcher " + e);
			e.printStackTrace();
		}
		if (waitNextWatchingRequested) {
			waitNextWatchingRequested = false;
		}
		isRunning = false;
		status = Status.IDLE;

	}

	protected void performRun() {
		// logger.info("********** START performWatching NOW " + rootDirectoryWatcher.directory);
		rootDirectoryWatcher.watch();
		// logger.info("********** FINISHED performWatching");
	}

	public boolean isRunning() {
		return isRunning;
	}

	protected abstract void fileModified(File file);

	protected abstract void fileAdded(File file);

	protected abstract void fileDeleted(File file);

	protected abstract void fileRenamed(File oldFile, File renamedFile);

	public static void main(String[] args) {
		TimerTask task = new DirectoryWatcher(new File("/Users/sylvain/Temp")) {
			@Override
			protected void fileModified(File file) {
				System.out.println("File MODIFIED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
			}

			@Override
			protected void fileAdded(File file) {
				System.out.println("File ADDED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
			}

			@Override
			protected void fileDeleted(File file) {
				System.out.println("File DELETED " + file.getName() + " in " + file.getParentFile().getAbsolutePath());
			}

			@Override
			protected void fileRenamed(File oldFile, File renamedFile) {
				System.out.println("File RENAMED from " + oldFile.getName() + " to " + renamedFile.getName() + " in "
						+ renamedFile.getParentFile().getAbsolutePath());
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, new Date(), 1000);

	}

	boolean waitNextWatchingRequested = false;
	boolean waitNextWatchingDone = false;

	public Status status = null;

	public enum Status {
		INIT, FIRST_RUN, RUNNING, IDLE
	}

	/**
	 * Wait for the next watching to be performed
	 */
	public void waitNextWatching() {
		waitNextWatchingRequested = true;
		waitNextWatchingDone = false;
		while (isRunning || !waitNextWatchingDone) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	private boolean isWaitingCurrentExecution = false;

	/**
	 * Wait for the currnt execution to be performed
	 */
	public void waitCurrentExecution() {
		isWaitingCurrentExecution = true;
		while (isRunning) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				isRunning = false;
			}
		}
		isWaitingCurrentExecution = false;
	}

	@Override
	public String toString() {
		return super.toString() + ":" + getDirectory();
	}

}
