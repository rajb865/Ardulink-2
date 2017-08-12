package org.ardulink.camel;

import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.ardulink.core.messages.events.api.FromDeviceMessageEvent;
import org.ardulink.core.messages.events.api.FromDeviceMessageListener;

public class ArdulinkConsumer extends DefaultConsumer {

	private final FromDeviceMessageListener listener = new FromDeviceMessageListener() {
		@Override
		public void fromDeviceMessageReceived(FromDeviceMessageEvent e) {
			Exchange exchange = getEndpoint().createExchange(
					ExchangePattern.InOnly);
			Message message = exchange.getIn();

			message.setBody(e.getFromDeviceMessage());
			try {
				getProcessor().process(exchange);
			} catch (Exception ex) {
				ex.printStackTrace();
				getExceptionHandler().handleException(
						"Failed to process notification", ex);
			}
		}
	};

	public ArdulinkConsumer(ArdulinkEndpoint endpoint, Processor processor) {
		super(endpoint, processor);
	}

	@Override
	protected void doStart() throws Exception {
		ArdulinkEndpoint ardulinkEndpoint = (ArdulinkEndpoint) getEndpoint();
		ardulinkEndpoint.getLinkMessageAdapter().addInMessageListener(
				this.listener);
		super.doStart();
	}

	@Override
	protected void doStop() throws Exception {
		ArdulinkEndpoint ardulinkEndpoint = (ArdulinkEndpoint) getEndpoint();
		ardulinkEndpoint.getLinkMessageAdapter().removeInMessageListener(
				this.listener);
		super.doStop();
	}

}
