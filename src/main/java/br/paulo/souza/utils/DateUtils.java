package br.paulo.souza.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {

	public static String getDataDiferencaDias(Integer qtdDias) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, qtdDias);		
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		return format.format(cal);
	}
	
}
