/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-swing, a component of the software infrastructure 
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

import java.beans.PropertyChangeSupport;
import java.lang.reflect.TypeVariable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Utility class used to reflect all loaded java classes in {@link ClassLoader}<br>
 * 
 * This class is instantiated only once
 * 
 * @author sylvain
 * 
 */
public class LoadedClassesInfo implements HasPropertyChangeSupport {

	private static final Logger LOGGER = Logger.getLogger(LoadedClassesInfo.class.getPackage().getName());

	private static LoadedClassesInfo INSTANCE;

	public static LoadedClassesInfo getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LoadedClassesInfo();
		}
		return INSTANCE;
	}

	private ClassLoader appLoader = ClassLoader.getSystemClassLoader();
	private ClassLoader currentLoader = LoadedClassesInfo.class.getClassLoader();
	private ClassLoader[] loaders = new ClassLoader[] { appLoader, currentLoader };

	private Hashtable<Package, PackageInfo> packages;
	private List<PackageInfo> packageList;
	private boolean needsReordering = true;
	private Hashtable<String, List<ClassInfo>> registeredClassesForName;
	private Set<String> availableClassNames;
	private List<LoadedClassesInfo> instances;

	private List<IgnoredPattern> ignoredPatterns;

	private final PropertyChangeSupport pcSupport;

	private LoadedClassesInfo() {
		pcSupport = new PropertyChangeSupport(this);

		LOGGER.info("Starting loading classes in LoadedClassesInfo");
		appLoader = ClassLoader.getSystemClassLoader();
		currentLoader = LoadedClassesInfo.class.getClassLoader();
		if (appLoader != currentLoader) {
			loaders = new ClassLoader[] { appLoader, currentLoader };
		}
		else {
			loaders = new ClassLoader[] { appLoader };
		}
		// instance = new LoadedClassesInfo();

		init();

		LOGGER.info("Finished loading classes in LoadedClassesInfo");

	}

	/*public LoadedClassesInfo(Class aClass) {
		this();
		if (aClass != null) {
			ClassInfo ci = registerClass(aClass);
			// setFilteredPackageName("*");
			setFilteredClassName(aClass.getName());
			// instance.setFilteredPackageName(aClass.getPackage().getName());
			setSelectedClassInfo(ci);
		}
	}*/

	/**
	 * Statically initialize this class with {@link ClassLoader}s
	 */
	private void init() {

		ignoredPatterns = new ArrayList<>();

		ignoredPatterns.add(new IgnoredPattern("com.*"));
		ignoredPatterns.add(new IgnoredPattern("org.apache.*"));
		ignoredPatterns.add(new IgnoredPattern("org.eclipse.*"));
		ignoredPatterns.add(new IgnoredPattern("org.jdesktop.*"));
		ignoredPatterns.add(new IgnoredPattern("javassist.*"));
		ignoredPatterns.add(new IgnoredPattern("org.jdom2.*"));

		instances = new ArrayList<>();
		registeredClassesForName = new Hashtable<>();

		primitives = new PackageInfo();
		registerClass(Boolean.TYPE);
		registerClass(Character.TYPE);
		registerClass(Byte.TYPE);
		registerClass(Short.TYPE);
		registerClass(Integer.TYPE);
		registerClass(Long.TYPE);
		registerClass(Float.TYPE);
		registerClass(Double.TYPE);

		packages = new Hashtable<Package, PackageInfo>() {
			@Override
			public synchronized PackageInfo put(Package key, PackageInfo value) {
				PackageInfo returned = super.put(key, value);
				needsReordering = true;
				for (LoadedClassesInfo instance : instances) {
					instance.pcSupport.firePropertyChange("packages", null, null);
				}
				return returned;
			};
		};
		for (Package p : Package.getPackages()) {
			registerPackage(p);
		}

		registerClass(String.class);
		registerClass(Date.class);

	}

	/**
	 * Explicitly search in all loaded classes if
	 */
	public List<Class<?>> search(String partialClassName) {
		// LOGGER.info("*************** Searching class " + partialClassName);
		List<Class<?>> foundClasses = new ArrayList<>();
		try {
			Class<?> foundClass = Class.forName(partialClassName);
			foundClasses.add(foundClass);
			LOGGER.info("Found class " + foundClass);
		} catch (ClassNotFoundException e) {
		}
		for (PackageInfo packageInfo : getPackages()) {
			try {
				Class<?> foundClass = Class.forName(packageInfo.getPackageName() + "." + partialClassName);
				foundClasses.add(foundClass);
				LOGGER.info("Found class " + foundClass);
			} catch (ClassNotFoundException e) {
			}
		}
		for (String s : getAvailableClassNames()) {
			if (s.contains(partialClassName) && !isFiltered(s)) {
				Class<?> foundClass = loadClass(s);
				if (foundClass != null && !foundClasses.contains(foundClass)) {
					foundClasses.add(foundClass);
				}
			}
		}
		// LOGGER.info("*************** Returning: " + foundClasses);
		return foundClasses;
	}

	/**
	 * Names of all the classes that can be loaded (not only the ones already loaded), scanned on first use
	 */
	private synchronized Set<String> getAvailableClassNames() {
		if (availableClassNames == null) {
			LOGGER.info("Starting scanning available classes in LoadedClassesInfo");
			availableClassNames = ClassScope.getAvailableClassNames(loaders);
			LOGGER.info("Finished scanning " + availableClassNames.size() + " available classes in LoadedClassesInfo");
		}
		return availableClassNames;
	}

	/**
	 * Load (without initializing it) the class named as supplied, using the known class loaders. Return null when not loadable.
	 */
	private Class<?> loadClass(String className) {
		for (ClassLoader loader : loaders) {
			try {
				return Class.forName(className, false, loader);
			} catch (ClassNotFoundException | LinkageError e) {
				// Try next loader
			}
		}
		return null;
	}

	private boolean isFiltered(Class<?> aClass) {
		return isFiltered(aClass.getName());
	}

	private boolean isFiltered(String className) {
		for (IgnoredPattern ignoredPattern : ignoredPatterns) {
			if (ignoredPattern.match(className)) {
				return true;
			}
		}
		return false;
	}

	private boolean isFiltered(PackageInfo packageInfo) {
		for (IgnoredPattern ignoredPattern : ignoredPatterns) {
			if (ignoredPattern.match(packageInfo.packageName)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

	public List<PackageInfo> getPackages() {
		if (needsReordering) {
			packageList = new ArrayList<>();
			for (Package p : packages.keySet()) {
				packageList.add(packages.get(p));
			}
			Collections.sort(packageList, new Comparator<PackageInfo>() {
				@Override
				public int compare(PackageInfo o1, PackageInfo o2) {
					return Collator.getInstance().compare(o1.packageName, o2.packageName);
				}
			});
			needsReordering = false;
		}
		return packageList;
	}

	public PackageInfo registerPackage(Package p) {
		PackageInfo returned = packages.get(p);
		if (returned == null) {
			packages.put(p, returned = new PackageInfo(p));
		}
		return returned;
	}

	public ClassInfo getClass(Class<?> c) {
		return registerClass(c);
	}

	private PackageInfo primitives;

	public List<ClassInfo> getPrimitives() {
		return primitives.getClasses();
	}

	public ClassInfo registerClass(Class<?> c) {
		try {
			return doRegisterClass(c);
		} catch (LinkageError e) {
			// A class whose dependencies are not on the classpath (optional libraries) cannot be introspected: ignore it
			LOGGER.fine("Ignored class " + c + ": " + e);
			return null;
		}
	}

	private ClassInfo doRegisterClass(Class<?> c) {
		if (c == null) {
			// LOGGER.warning("Null class " + c);
			return null;
		}

		// Ignoring classes from gradle as they cause NoClassDefException
		if (c.getName().startsWith("worker.org.gradle")) {
			System.out.println("Ignored class: " + c);
			return null;
		}

		PackageInfo p = null;

		if (c.isPrimitive()) {
			p = primitives;
		}
		else if (c.getPackage() == null) {
			LOGGER.warning("No package for class " + c);
			Thread.dumpStack();
			return null;
		}
		else {
			p = registerPackage(c.getPackage());
		}

		LOGGER.fine("Register class " + c);
		if (!c.isMemberClass() && !c.isAnonymousClass() && !c.isLocalClass()) {
			ClassInfo returned = p.classes.get(c);
			if (returned == null) {
				p.classes.put(c, returned = new ClassInfo(c));
				LOGGER.fine("Store " + returned + " in package " + p.packageName);
			}
			return returned;
		}
		else if (c.isMemberClass()) {
			// System.out.println("Member class: "+c+" of "+c.getDeclaringClass());
			ClassInfo parentClass = doRegisterClass(c.getEnclosingClass());
			if (parentClass != null) {
				ClassInfo returned = parentClass.declareMember(c);
				return returned;
			}
			return null;
		}
		else {
			// System.out.println("Ignored class: "+c);
			return null;
		}

	}

	public Hashtable<String, List<ClassInfo>> getRegisteredClassesForName() {
		return registeredClassesForName;
	}

	public class PackageInfo implements HasPropertyChangeSupport {
		private String packageName;
		private final Hashtable<Class<?>, ClassInfo> classes = new Hashtable<Class<?>, ClassInfo>() {
			@Override
			public synchronized ClassInfo put(Class<?> key, ClassInfo value) {
				ClassInfo returned = super.put(key, value);
				needsReordering = true;
				pcSupport.firePropertyChange("classes", null, null);
				return returned;
			};
		};
		private List<ClassInfo> classesList;
		private boolean needsReordering = true;
		private final PropertyChangeSupport pcSupport;

		private PackageInfo() {
			pcSupport = new PropertyChangeSupport(this);
			packageName = "primitives";
		}

		public PackageInfo(Package aPackage) {
			pcSupport = new PropertyChangeSupport(this);
			packageName = aPackage.getName();
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		public String getPackageName() {
			return packageName;
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}

		public List<ClassInfo> getClasses() {
			if (needsReordering) {
				classesList = new ArrayList<>();
				for (Class<?> c : classes.keySet()) {
					classesList.add(classes.get(c));
				}
				Collections.sort(classesList, new Comparator<ClassInfo>() {
					@Override
					public int compare(ClassInfo o1, ClassInfo o2) {
						return Collator.getInstance().compare(o1.className, o2.className);
					}
				});
				needsReordering = false;
			}
			return classesList;
		}

		public boolean isFiltered() {
			return LoadedClassesInfo.this.isFiltered(this);

			/*if (loadedClassesInfo.getFilteredPackageName() == null || StringUtils.isEmpty(loadedClassesInfo.getFilteredPackageName())) {
				return false;
			}
			if (packageName.startsWith(loadedClassesInfo.getFilteredPackageName())) {
				return false;
			}
			String patternString = loadedClassesInfo.getFilteredPackageName();
			if (patternString.startsWith("*")) {
				patternString = "." + loadedClassesInfo.getFilteredPackageName();
			}
			try {
				Pattern pattern = Pattern.compile(patternString);
				Matcher matcher = pattern.matcher(packageName);
				return !matcher.find();
			} catch (PatternSyntaxException e) {
				LOGGER.warning("PatternSyntaxException: " + patternString);
				return false;
			}*/
		}

	}

	public class ClassInfo implements HasPropertyChangeSupport {
		private final Class<?> clazz;
		public String className;
		public String displayableName;
		public String packageName;
		public String fullQualifiedName;

		private final Hashtable<Class<?>, ClassInfo> memberClasses = new Hashtable<Class<?>, ClassInfo>() {
			@Override
			public synchronized ClassInfo put(Class<?> key, ClassInfo value) {
				ClassInfo returned = super.put(key, value);
				needsReordering = true;
				pcSupport.firePropertyChange("memberClasses", null, null);
				return returned;
			};
		};
		private List<ClassInfo> memberClassesList;
		private boolean needsReordering = true;
		private final PropertyChangeSupport pcSupport;

		public ClassInfo(Class<?> aClass) {
			pcSupport = new PropertyChangeSupport(this);
			List<ClassInfo> listOfClassesWithThatName = registeredClassesForName.get(aClass.getSimpleName());
			if (listOfClassesWithThatName == null) {
				registeredClassesForName.put(aClass.getSimpleName(), listOfClassesWithThatName = new ArrayList<>());
			}
			listOfClassesWithThatName.add(this);
			className = aClass.getSimpleName();
			displayableName = className;
			if (aClass.getTypeParameters() != null && aClass.getTypeParameters().length > 0) {
				StringBuffer tp = new StringBuffer();
				tp.append("<");
				boolean isFirst = true;
				for (TypeVariable<?> t : aClass.getTypeParameters()) {
					tp.append(isFirst ? t.getName() : "," + t.getName());
					isFirst = false;
				}
				tp.append(">");
				displayableName = displayableName + tp.toString();
			}
			if (aClass.isPrimitive()) {
				packageName = "primitives";
			}
			else {
				packageName = aClass.getPackage().getName();
			}
			fullQualifiedName = aClass.getName();
			clazz = aClass;
			LOGGER.fine("Instanciate new ClassInfo for " + aClass);
		}

		public Class<?> getClazz() {
			return clazz;
		}

		@Override
		public PropertyChangeSupport getPropertyChangeSupport() {
			return pcSupport;
		}

		@Override
		public String getDeletedProperty() {
			return null;
		}

		protected ClassInfo declareMember(Class<?> c) {
			ClassInfo returned = memberClasses.get(c);
			if (returned == null) {
				memberClasses.put(c, returned = new ClassInfo(c));
				needsReordering = true;
				LOGGER.fine(toString() + ": declare member: " + returned);
			}
			return returned;
		}

		public List<ClassInfo> getMemberClasses() {
			if (needsReordering) {
				memberClassesList = new ArrayList<>();
				for (Class<?> c : memberClasses.keySet()) {
					memberClassesList.add(memberClasses.get(c));
				}
				Collections.sort(memberClassesList, new Comparator<ClassInfo>() {
					@Override
					public int compare(ClassInfo o1, ClassInfo o2) {
						return Collator.getInstance().compare(o1.className, o2.className);
					}
				});
				needsReordering = false;
			}
			return memberClassesList;
		}

		public Class<?> getRepresentedClass() {
			return clazz;
		}

		@Override
		public String toString() {
			return "ClassInfo[" + clazz.getName() + "]";
		}

	}

	public List<IgnoredPattern> getIgnoredPatterns() {
		return ignoredPatterns;
	}

	public IgnoredPattern addIgnoredPattern() {
		IgnoredPattern returned = new IgnoredPattern("<filter>");
		ignoredPatterns.add(returned);
		getPropertyChangeSupport().firePropertyChange("ignoredPatterns", null, returned);
		return returned;
	}

	public void removeIgnoredPattern(IgnoredPattern patternToRemove) {
		ignoredPatterns.remove(patternToRemove);
		getPropertyChangeSupport().firePropertyChange("ignoredPatterns", patternToRemove, null);
	}

	public static class IgnoredPattern {
		private String patternString;
		private Pattern pattern;

		public IgnoredPattern(String patternString) {
			setPatternString(patternString);
		}

		public String getPatternString() {
			return patternString;
		}

		public void setPatternString(String patternString) {
			this.patternString = patternString;
			pattern = Pattern.compile(patternString);
		}

		public boolean match(String s) {
			Matcher matcher = pattern.matcher(s);
			return matcher.find();
		}
	}

}
