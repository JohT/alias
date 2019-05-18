package org.alias.axon.serializer.example.messaging.axon;

import org.alias.axon.serializer.example.messaging.boundary.command.CommandGatewayBoundaryService;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.gateway.CommandGateway;

public class CommandGatewayBoundaryServiceAdapter implements CommandGatewayBoundaryService {

	private final CommandGateway commandGateway;

	public CommandGatewayBoundaryServiceAdapter(CommandGateway commandGateway) {
		this.commandGateway = commandGateway;
	}

	@Override
	public <R> R sendAndWait(Object command) throws IllegalStateException {
		try {
			return commandGateway.sendAndWait(command);
		} catch (CommandExecutionException e) {
			throw ExceptionCause.of(e).unwrapped();
		}
	}

	@Override
	public String toString() {
		return "CommandGatewayBoundaryServiceAdapter [commandGateway=" + commandGateway + "]";
	}
}