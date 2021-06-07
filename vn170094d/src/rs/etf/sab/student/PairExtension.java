package rs.etf.sab.student;

import java.math.BigDecimal;

import rs.etf.sab.operations.PackageOperations.Pair;

public class PairExtension implements Pair<Integer, BigDecimal>{

	Integer firstParam;
	BigDecimal secondParam;
	
	
	public PairExtension(Integer firstParam, BigDecimal secondParam) {
		super();
		this.firstParam = firstParam;
		this.secondParam = secondParam;
	}

	@Override
	public Integer getFirstParam() {
		// TODO Auto-generated method stub
		return firstParam;
	}

	@Override
	public BigDecimal getSecondParam() {
		// TODO Auto-generated method stub
		return secondParam;
	}

}
