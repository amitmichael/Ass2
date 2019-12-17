package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.Squad;
import java.util.concurrent.TimeoutException;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {
	private String serialNumber;
	private LogManager logM = LogManager.getInstance();
	private Squad squad = Squad.getInstance();


	public Moneypenny(String serialNumber) {
		super("MoneyPenny" + serialNumber);
		this.serialNumber = serialNumber;
	}

	@Override
	protected synchronized void initialize() {
		logM.log.info("Subscriber " + this.getName() + " initialization");
		MessageBrokerImpl.getInstance().register(this);
		subscribeToAgentsAvailableEvent();
	}

	private void subscribeToAgentsAvailableEvent(){
		Callback back = new Callback() {
			@Override
			public void call(Object c) throws InterruptedException {
				if (c instanceof AgentsAvailableEvent) {
					AgentsAvailableEvent event = (AgentsAvailableEvent) c;

						logM.log.info("squad is trying to execute agents");
							Boolean result = squad.getAgents(event.getserials());
							MessageBrokerImpl.getInstance().complete(event, result.toString());

					/*} catch ( InterruptedException e) {
						MessageBrokerImpl.getInstance().complete(event,"Agents didnt executed");
						logM.log.warning("sendAgents reached timeout");
					}*/
					}
				else {
					logM.log.warning("call is not of type AgentAvilableEvent");
				}
			}
		};
		subscribeEvent(AgentsAvailableEvent.class,back);

	}
}


