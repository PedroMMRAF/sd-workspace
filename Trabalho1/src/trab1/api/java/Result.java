package trab1.api.java;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

/**
 * 
 * Represents the result of an operation, either wrapping a result of the given
 * type, or an error.
 * 
 * @param <T> type of the result value associated with success
 */
public interface Result<T> {
	enum ErrorCode {
		OK, CONFLICT, NOT_FOUND, BAD_REQUEST, FORBIDDEN, INTERNAL_ERROR, NOT_IMPLEMENTED, TIMEOUT;

		public static ErrorCode fromStatusCode(int statusCode) {
			return switch (statusCode) {
				case 200, 204 -> OK;
				case 400 -> BAD_REQUEST;
				case 403 -> FORBIDDEN;
				case 404 -> NOT_FOUND;
				case 409 -> CONFLICT;
				case 405 -> NOT_IMPLEMENTED;
				case 408 -> TIMEOUT;
				case 500 -> INTERNAL_ERROR;
				default -> INTERNAL_ERROR;
			};
		}
	};

	boolean isOK();

	T value();

	ErrorCode error();

	static <T> Result<T> ok(T result) {
		return new OkResult<>(result);
	}

	static <T> Result<T> ok() {
		return new OkResult<>(null);
	}

	static <T> Result<T> error(ErrorCode error) {
		return new ErrorResult<>(error);
	}

	static <T> Result<T> error(int error) {
		return new ErrorResult<>(ErrorCode.fromStatusCode(error));
	}

	static <T> Result<T> fromResponse(Response r, Class<T> cls) {
		if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity())
			return Result.ok(r.readEntity(cls));

		return Result.error(r.getStatus());
	}

	static <T> Result<T> fromResponse(Response r, GenericType<T> cls) {
		if (r.getStatus() == Response.Status.OK.getStatusCode() && r.hasEntity())
			return Result.ok(r.readEntity(cls));

		return Result.error(r.getStatus());
	}

	static Result<Void> fromResponse(Response r) {
		if (r.getStatus() == Response.Status.NO_CONTENT.getStatusCode())
			return Result.ok();

		return Result.error(r.getStatus());
	}
}

class OkResult<T> implements Result<T> {
	final T result;

	OkResult(T result) {
		this.result = result;
	}

	@Override
	public boolean isOK() {
		return true;
	}

	@Override
	public T value() {
		return result;
	}

	@Override
	public ErrorCode error() {
		return ErrorCode.OK;
	}

	public String toString() {
		return "(OK, " + value() + ")";
	}
}

class ErrorResult<T> implements Result<T> {
	final ErrorCode error;

	ErrorResult(ErrorCode error) {
		this.error = error;
	}

	@Override
	public boolean isOK() {
		return false;
	}

	@Override
	public T value() {
		throw new RuntimeException("Attempting to extract the value of an Error: " + error());
	}

	@Override
	public ErrorCode error() {
		return error;
	}

	public String toString() {
		return "(" + error() + ")";
	}
}
