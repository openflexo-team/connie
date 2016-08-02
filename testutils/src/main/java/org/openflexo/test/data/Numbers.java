/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
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

package org.openflexo.test.data;

public class Numbers {

	private byte byteP;
	private short shortP;
	private int intP;
	private long longP;
	private float floatP;
	private double doubleP;

	private Byte byteO;
	private Short shortO;
	private Integer intO;
	private Long longO;
	private Float floatO;
	private Double doubleO;

	public Numbers() {
		super();
		this.byteP = 1;
		this.shortP = 2;
		this.intP = 3;
		this.longP = 4;
		this.floatP = 5.0f;
		this.doubleP = 6.0;
		this.byteO = 7;
		this.shortO = 8;
		this.intO = 9;
		this.longO = Long.valueOf(10);
		this.floatO = 11.0f;
		this.doubleO = 12.0;
	}

	public byte getByteP() {
		return byteP;
	}

	public void setByteP(byte byteP) {
		this.byteP = byteP;
	}

	public short getShortP() {
		return shortP;
	}

	public void setShortP(short shortP) {
		this.shortP = shortP;
	}

	public int getIntP() {
		return intP;
	}

	public void setIntP(int intP) {
		this.intP = intP;
	}

	public long getLongP() {
		return longP;
	}

	public void setLongP(long longP) {
		this.longP = longP;
	}

	public float getFloatP() {
		return floatP;
	}

	public void setFloatP(float floatP) {
		this.floatP = floatP;
	}

	public double getDoubleP() {
		return doubleP;
	}

	public void setDoubleP(double doubleP) {
		this.doubleP = doubleP;
	}

	public Byte getByteO() {
		return byteO;
	}

	public void setByteO(Byte byteO) {
		this.byteO = byteO;
	}

	public Short getShortO() {
		return shortO;
	}

	public void setShortO(Short shortO) {
		this.shortO = shortO;
	}

	public Integer getIntO() {
		return intO;
	}

	public void setIntO(Integer intO) {
		this.intO = intO;
	}

	public Long getLongO() {
		return longO;
	}

	public void setLongO(Long longO) {
		this.longO = longO;
	}

	public Float getFloatO() {
		return floatO;
	}

	public void setFloatO(Float floatO) {
		this.floatO = floatO;
	}

	public Double getDoubleO() {
		return doubleO;
	}

	public void setDoubleO(Double doubleO) {
		this.doubleO = doubleO;
	}
}
