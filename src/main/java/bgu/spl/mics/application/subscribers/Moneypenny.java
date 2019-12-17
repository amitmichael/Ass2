package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.Squad;
import javafx.util.Pair;

import java.util.List;
import java.util.concurrent.TimeoutException;

import static javafx.scene.input.KeyCode.T;

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
		subscribeToExcuteMission();
		subscribedToGetAgentNamesEvent();
	}


	private void subscribedToGetAgentNamesEvent() {
		Callback back1 = new Callback() {
			@Override
			public void call(Object c)  {
				if (c instanceof GetAgentNamesEvent) {
					GetAgentNamesEvent event = (GetAgentNamesEvent) c;
					MessageBrokerImpl.getInstance().complete(event, Squad.getInstance().getAgentsNames(event.getSerial()));

				} else {
					logM.log.warning("call is not of type GetAgentNamesEvent");
				}
			}
		};
		subscribeEvent(GetAgentNamesEvent.class, back1);

	}

	private void subscribeToExcuteMission() {
		Callback back1 = new Callback() {
			@Override
			public void call(Object c) throws TimeoutException, InterruptedException {
				if (c instanceof ExcuteMission) {
					ExcuteMission event = (ExcuteMission) c;
					Squad.getInstance().sendAgents(event.getserials(),event.getDuration());
					logM.log.info("Send agents to Mission");
					MessageBrokerImpl.getInstance().complete( event, serialNumber);
				} else {
					logM.log.warning("call is not of type ExcuteMission");
				}
			}
		};
		subscribeEvent(ExcuteMission.class,back1);
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
	public String getSerialNumber(){
		return serialNumber;
	}
}


