package trab1.api.soap;

import jakarta.xml.ws.WebFault;

@WebFault
public class UsersException extends Exception {
	private static final long serialVersionUID = 1L;
}
