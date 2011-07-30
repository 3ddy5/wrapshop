package de.jsf;


public class Wrap {
	private int wrapid;
	private String zutat1;
	private String zutat2;
	private String zutat3;
	private String zutat4;
	
	public Wrap() {
	}

	public Wrap(String zutat1, String zutat2, String zutat3, String zutat4) {
		this.zutat1 = zutat1;
		this.zutat2 = zutat2;
		this.zutat3 = zutat3;
		this.zutat4 = zutat4;
	}

	public int getWrapId() {
		return wrapid;
	}

	public void setWrapId(int wrapid) {
		this.wrapid = wrapid;
	}

	public String getZutat1() {
		return zutat1;
	}

	public void setZutat1(String zutat1) {
		this.zutat1 = zutat1;
	}

	public String getZutat2() {
		return zutat2;
	}

	public void setZutat2(String zutat2) {
		this.zutat2 = zutat2;
	}
	
	public String getZutat3() {
		return zutat3;
	}

	public void setZutat3(String zutat3) {
		this.zutat3 = zutat3;
	}
	
	public String getZutat4() {
		return zutat4;
	}

	public void setZutat4(String zutat4) {
		this.zutat4 = zutat4;
	}


	public String toString() {
		return "(" + wrapid + ") " + zutat1 + " " + zutat2 + " " + zutat3 +" " + zutat4;
	}
}