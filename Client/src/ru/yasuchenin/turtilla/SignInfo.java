package ru.yasuchenin.turtilla;

import java.io.Serializable;

@SuppressWarnings({ "serial", "deprecation" })
class SignInfo implements Serializable {

	private Long id;
	private String sign;
	private String virus;
	private String description;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the sign
	 */
	public String getSign() {
		return sign;
	}

	/**
	 * @param sign
	 *            the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}

	/**
	 * @return the virus
	 */
	public String getVirus() {
		return virus;
	}

	/**
	 * @param virus
	 *            the virus to set
	 */
	public void setVirus(String virus) {
		this.virus = virus;
	}

	/**
	 * @return the descr
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param descr
	 *            the descr to set
	 */
	public void setDescription(String descr) {
		this.description = descr;
	}

	public boolean check() {
		if (sign.length() == 0 || virus.length() == 0
				|| description.length() == 0)
			return false;
		return true;
	}
}