/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
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

import java.util.HashSet;

/**
 * @deprecated this class doesn't specify any context for the reserved keywords !
 */
@Deprecated
public class ReservedKeyword {

	private static final HashSet<String> reservedSet = new HashSet<>();

	public static boolean contains(String word) {
		if (word == null) {
			return false;
		}
		return reservedSet.contains(word.toLowerCase());
	}

	public static final String[] reserved = new String[] { "WOAction", "WOActionResults", "WOAdaptor", "WOAdminAction", "WOApplication",
			"WOAssociation", "WOComponent", "WOContext", "WOCookie", "WODOMParserException", "WODirectAction", "WODisplayGroup",
			"WODynamicElement", "WOElement", "WOEvent", "WOHTTPConnection", "WOMailDelivery", "WOMessage", "WOMultipartIterator",
			"WOPageNotFoundException", "WORedirect", "WORequest", "WORequestHandler", "WOResourceManager", "WOResponse", "WOSession",
			"WOSessionStore", "WOStatisticsStore", "WOStopWatch", "WOTimer", "WOWebServiceRegistrar", "WOWebServiceUtilities",
			"_CookieParser", "_WODocumentType", "_WOMessageHelper", "_WORunLoop", "_WOSimpleApplication", "EOAccessArrayFaultHandler",
			"EOAccessDeferredFaultHandler", "EOAccessFaultHandler", "EOAccessGenericFaultHandler", "EOAdaptor", "EOAdaptorChannel",
			"EOAdaptorContext", "EOAdaptorOpComparator", "EOAdaptorOperation", "EOAttribute", "EOAttributeNameComparator", "EODatabase",
			"EODatabaseChannel", "EODatabaseContext", "EODatabaseDataSource", "EODatabaseOperation", "EOEntity", "EOEntityClassDescription",
			"EOGeneralAdaptorException", "EOJoin", "EOModel", "EOModelGroup", "EOModelPrivate", "EOObjectNotAvailableException",
			"EOProperty", "EOPropertyListEncoding", "EOQualifierSQLGeneration", "EORelationship", "EORelationshipComparator",
			"EOSQLExpression", "EOSQLExpressionFactory", "EOSQLQualifier", "EOSchemaGeneration", "EOSchemaSynchronization",
			"EOStoredProcedure", "EOSynchronizationFactory", "EOUtilities", "_EOCheapCopyHandler", "_EODBCtxEntityInfo",
			"_EOExpressionArray", "_EOPrivate", "_EOStringUtil", "EOAccountEvent", "EOAggregateEvent", "EOAndQualifier",
			"EOArrayDataSource", "EOClassDescription", "EOCooperatingObjectStore", "EOCustomObject", "EODataSource", "EODeferredFaulting",
			"EODelayedObserver", "EODelayedObserverQueue", "EODetailDataSource", "EOEditingContext", "EOEnterpriseObject", "EOEvent",
			"EOEventCenter", "EOFaultHandler", "EOFaulting", "EOFetchSpecification", "EOGenericRecord", "EOGlobalID",
			"EOKeyComparisonQualifier", "EOKeyGlobalID", "EOKeyValueArchiver", "EOKeyValueArchiving", "EOKeyValueCoding",
			"EOKeyValueCodingAdditions", "EOKeyValueQualifier", "EOKeyValueUnarchiver", "EONotQualifier", "EOObjectStore",
			"EOObjectStoreCoordinator", "EOObserverCenter", "EOObserverProxy", "EOObserving", "EOOrQualifier", "EOQualifier",
			"EOQualifierEvaluation", "EOQualifierVariable", "EOQualifierVisitor", "EORelationshipManipulation", "EOSharedEditingContext",
			"EOSortOrdering", "EOTemporaryGlobalID", "EOValidation", "_EOCheapCopyArray", "_EOCheapCopyMutableArray",
			"_EOEventDurationComparator", "_EOFlatMutableDictionary", "_EOIntegralKeyGlobalID", "_EOKnownSelector",
			"_EOMutableDefaultValueDictionary", "_EOMutableKnownKeyDictionary", "_EOPrivateMemento", "_EOVectorKeyGlobalID",
			"_EOWeakReference", "NSArray", "NSBundle", "NSCoder", "NSCoding", "NSComparator", "NSData", "NSDelayedCallbackCenter",
			"NSDictionary", "NSDisposable", "NSDisposableRegistry", "NSForwardException", "NSKeyValueCoding", "NSKeyValueCodingAdditions",
			"NSLock", "NSLocking", "NSLog", "NSMultiReaderLock", "NSMutableArray", "NSMutableData", "NSMutableDictionary", "NSMutableRange",
			"NSMutableSet", "NSNotification", "NSNotificationCenter", "NSNumberFormatter", "NSPathUtilities", "NSProperties",
			"NSPropertyListSerialization", "NSRange", "NSRecursiveLock", "NSSelector", "NSSet", "NSSocketUtilities", "NSTimeZone",
			"NSTimestamp", "NSTimestampFormatter", "NSUndoManager", "NSValidation", "_NSArrayUtilities", "_NSBase64",
			"_NSCollectionEnumerator", "_NSCollectionPrimitives", "_NSCollectionReaderWriterLock", "_NSDelegate", "_NSDictionaryUtilities",
			"_NSFoundationCollection", "_NSGregorianCalendar", "_NSIntegerDictionary", "_NSIntegerKeyDictionary", "_NSJavaArrayEnumerator",
			"_NSJavaArrayWrapper", "_NSMutableIntegerDictionary", "_NSMutableIntegerKeyDictionary", "_NSObjectStreamClass",
			"_NSReadReentrantReaderWriterLock", "_NSReflectionUtilities", "_NSSerialClassReflector", "_NSSerialFieldDesc",
			"_NSStreamingOutputData", "_NSStringUtilities", "_NSThreadsafeMutableArray", "_NSThreadsafeMutableDictionary",
			"_NSThreadsafeMutableSet", "_NSThreadsafeWrapper", "_NSUtilities", "_NSUtilitiesExtra", "_NSWeakMutableArray",
			"_NSWeakMutableCollection", "_NSWeakMutableSet", "_NSWeakValueMutableDictionary", "__NSLocalTimeZone", "AbstractMethodError",
			"ArithmeticException", "ArrayIndexOutOfBoundsException", "ArrayStoreException", "AssertionError", "AssertionStatusDirectives",
			"Boolean", "Byte", "CharSequence", "Class", "ClassCastException", "ClassCircularityError", "ClassFormatError", "ClassLoader",
			"ClassNotFoundException", "CloneNotSupportedException", "Cloneable", "Comparable", "Compiler", "Double", "Error", "Exception",
			"ExceptionInInitializerError", "Float", "FloatingDecimal", "IllegalAccessError", "IllegalAccessException",
			"IllegalArgumentException", "IllegalMonitorStateException", "IllegalStateException", "IllegalThreadStateException",
			"IncompatibleClassChangeError", "IndexOutOfBoundsException", "InheritableThreadLocal", "InstantiationError",
			"InstantiationException", "Integer", "InternalError", "InterruptedException", "LinkageError", "Long", "Math",
			"NegativeArraySizeException", "NoClassDefFoundError", "NoSuchFieldError", "NoSuchFieldException", "NoSuchMethodError",
			"NoSuchMethodException", "NullPointerException", "Number", "NumberFormatException", "Object", "OutOfMemoryError", "Package",
			"Process", "Runnable", "Runtime", "RuntimeException", "RuntimePermission", "SecurityException", "SecurityManager", "Short",
			"Shutdown", "StackOverflowError", "StackTraceElement", "StrictMath", "String", "StringBuffer", "StringCoding",
			"StringIndexOutOfBoundsException", "System", "Thread", "ThreadDeath", "ThreadGroup", "ThreadLocal", "Throwable", "UnknownError",
			"UnsatisfiedLinkError", "UnsupportedClassVersionError", "UnsupportedOperationException", "VerifyError", "VirtualMachineError",
			"Void", "Application", "Session", "DirectAction", "abstract", "continue", "for", "new", "switch", "assert", "default", "goto",
			"package", "synchronized", "boolean", "do", "if", "private", "this", "break", "double", "implements", "protected", "throw",
			"byte", "else", "import", "public", "throws", "case", "enum", "instanceof", "return", "transient", "catch", "extends", "int",
			"short", "try", "char", "final", "interface", "static", "void", "class", "finally", "long", "strictfp", "volatile", "const",
			"float", "native", "super", "while", "AbstractCollection", "AbstractList", "AbstractMap", "AbstractSequentialList",
			"AbstractSet", "ArrayList", "Arrays", "BitSet", "Calendar", "Collection", "Collections", "Comparator",
			"ConcurrentModificationException", "Currency", "Date", "Dictionary", "EmptyStackException", "Enumeration", "EventListener",
			"EventListenerProxy", "EventObject", "GregorianCalendar", "HashMap", "HashSet", "Hashtable", "IdentityHashMap", "Iterator",
			"LinkedHashMap", "LinkedHashSet", "LinkedList", "List", "ListIterator", "ListResourceBundle", "Locale", "Map",
			"MissingResourceException", "NoSuchElementException", "Observable", "Observer", "Properties", "PropertyPermission",
			"PropertyResourceBundle", "Random", "RandomAccess", "ResourceBundle", "ResourceBundleEnumeration", "Set", "SimpleTimeZone",
			"SortedMap", "SortedSet", "Stack", "StringTokenizer", "TimeZone", "Timer", "TimerTask", "TooManyListenersException", "TreeMap",
			"TreeSet", "Vector", "WeakHashMap", "WDLComponent" };

	static {
		for (int i = 0; i < reserved.length; i++) {
			reservedSet.add(reserved[i].toLowerCase());
		}
	}
}
