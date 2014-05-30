/*
 * (c) Copyright 2013-2014 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.openflexo.xml;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class testXMLModel implements IXMLModel, IXMLMetaModel {

	protected static final Logger logger = Logger.getLogger(testXMLModel.class.getPackage().getName());

	private Map<String, testXMLIndiv> listIndiv;

	private Map<String, Type> listType;
	
	private testXMLIndiv root;
	
	public testXMLModel(){
		listIndiv = new HashMap<String, testXMLIndiv>();
		listType = new HashMap<String, Type>();
		root = null;
	}
	
	@Override
	public Object addNewIndividual(Type aType) {
		logger.info("CREATE a NEW Individual");
		testXMLIndiv indiv = new testXMLIndiv();
		listIndiv.put(indiv.getUUID(), indiv);
		return indiv;
	}

	@Override
	public void setRoot(IXMLIndividual<?, ?> anIndividual) {

		logger.info("ROOT element is " + anIndividual.toString());
		root = (testXMLIndiv) anIndividual;

	}

	@Override
	public IXMLIndividual<?, ?> getRoot() {
		return root;
	}

	@Override
	public void setNamespace(String uri, String prefix) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getNamespacePrefix() {
		return "ATEST";
	}

	@Override
	public String getNamespaceURI() {
		return "http://a.test.org/";
	}

	@Override
	public Type getTypeFromURI(String string) {
		return String.class;
	}

	@Override
	public Type createNewType(String uri, String localName, String qName) {
		return Object.class;
	}

}
