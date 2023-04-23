package pl.lodz.p.it.domain.exceptions;

import java.time.DateTimeException;

public class DateException extends DateTimeException {

	public DateException () {
		super("Invalid date");
	}

}
