package aula8.servers.rest;

import aula8.api.java.Result;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response.Status;

public class RestResource {
	/**
	 * Given a Result<T>, either returns the value, or throws the JAX-WS Exception
	 * matching the error code...
	 */
	protected <T> T fromJavaResult(Result<T> result) {
		if (result.isOK())
			return result.value();

		throw new WebApplicationException(statusCodeFrom(result));
	}

	/**
	 * Translates a Result<T> to a HTTP Status code
	 */
	private static Status statusCodeFrom(Result<?> result) {
		return switch (result.error()) {
			case CONFLICT -> Status.CONFLICT;
			case NOT_FOUND -> Status.NOT_FOUND;
			case FORBIDDEN -> Status.FORBIDDEN;
			case TIMEOUT, BAD_REQUEST -> Status.BAD_REQUEST;
			case NOT_IMPLEMENTED -> Status.NOT_IMPLEMENTED;
			case INTERNAL_ERROR -> Status.INTERNAL_SERVER_ERROR;
			case OK -> result.value() == null ? Status.NO_CONTENT : Status.OK;
			default -> Status.INTERNAL_SERVER_ERROR;
		};
	}
}