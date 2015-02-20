package org.openflexo.toolbox;

/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.File;
import java.io.IOException;
import java.util.List;

import name.pachler.nio.file.ClosedWatchServiceException;
import name.pachler.nio.file.FileSystems;
import name.pachler.nio.file.Path;
import name.pachler.nio.file.Paths;
import name.pachler.nio.file.StandardWatchEventKind;
import name.pachler.nio.file.WatchEvent;
import name.pachler.nio.file.WatchKey;
import name.pachler.nio.file.WatchService;
import name.pachler.nio.file.ext.ExtendedWatchEventKind;

/**
 * Example to watch a directory (or tree) for changes to files.
 */

public class WatchDirUsingJPathWatch {

	static void usage() {
		System.err.println("usage: java WatchDirUsingJPathWatch [-r] dir");
		System.exit(-1);
	}

	public static void main(String[] args) throws IOException {
		// parse arguments
		if (args.length == 0 || args.length > 2)
			usage();
		boolean recursive = false;
		int dirArg = 0;
		if (args[0].equals("-r")) {
			if (args.length < 2)
				usage();
			recursive = true;
			dirArg++;
		}

		File f;

		WatchService watchService = FileSystems.getDefault().newWatchService();

		// register directory and process its events
		Path watchedPath = Paths.get(args[dirArg]);
		System.out.println("watchedPath=" + watchedPath);

		WatchKey key = null;
		try {
			key = watchedPath.register(watchService, StandardWatchEventKind.ENTRY_CREATE, StandardWatchEventKind.ENTRY_DELETE,
					StandardWatchEventKind.ENTRY_MODIFY/*, ExtendedWatchEventKind.ENTRY_RENAME_FROM,
														ExtendedWatchEventKind.ENTRY_RENAME_TO*/);
		} catch (UnsupportedOperationException uox) {
			System.err.println("filehing not supported!");
			// handle this error here
		} catch (IOException iox) {
			System.err.println("I/Ors");
			// handle this error here
		}

		for (;;) {
			// take() will block until a file has been created/deleted
			WatchKey signalledKey;
			try {
				signalledKey = watchService.take();
			} catch (InterruptedException ix) {
				// we'll ignore being interrupted
				continue;
			} catch (ClosedWatchServiceException cwse) {
				// other thread closed watch service
				System.out.println("watchice closed, terminating.");
				break;
			}

			// get list of events from key
			List<WatchEvent<?>> list = signalledKey.pollEvents();

			// VERY IMPORTANT! call reset() AFTER pollEvents() to allow the
			// key to be reported again by the watch service
			signalledKey.reset();

			// we'll simply print what has happened; real applications
			// will do something more sensible here
			for (WatchEvent e : list) {
				String message = "";
				if (e.kind() == StandardWatchEventKind.ENTRY_CREATE) {
					Path context = (Path) e.context();
					message = context.toString() + " created";
				} else if (e.kind() == StandardWatchEventKind.ENTRY_DELETE) {
					Path context = (Path) e.context();
					message = context.toString() + " deleted";
				} else if (e.kind() == StandardWatchEventKind.ENTRY_MODIFY) {
					Path context = (Path) e.context();
					message = context.toString() + " modified";
				} else if (e.kind() == ExtendedWatchEventKind.ENTRY_RENAME_FROM) {
					Path context = (Path) e.context();
					message = context.toString() + " renamed from";
				} else if (e.kind() == ExtendedWatchEventKind.ENTRY_RENAME_TO) {
					Path context = (Path) e.context();
					message = context.toString() + " renamed to";
				} else if (e.kind() == StandardWatchEventKind.OVERFLOW) {
					message = "OVERFLOW: more changes happened than we could retreive";
				}
				System.out.println(message);
			}
		}
	}
}
