package bgu.spl.mics.application.publishers;
import bgu.spl.mics.*;

import java.sql.Time;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {
	private int termination;
	private int count;
	private Timer timer;
	private TimerTask task;
	private LogManager logM = LogManager.getInstance();
	private AtomicBoolean running = new AtomicBoolean(false);



	/**
	 *
	 * @param duration the number of ticks before termination
	 */
	public TimeService(int duration) {
		super("TimeService");
		this.termination = duration;
		this.count = 1;
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public synchronized void run() {
				while (running.get()) {
					if (count <= termination) {
						TickBroadcast toSend = new TickBroadcast(System.currentTimeMillis());
						getSimplePublisher().sendBroadcast(toSend);
						logM.log.info("Sending Broadcast msg #" + count + " Time: " + toSend.getTime());
						count++;
					}
					else {
						interrupt();
					}
				}
			}
		};
	}

	@Override
	protected void initialize() { // ???
		// TODO Implement this
	}
	public  void interrupt() {
		running.set(false);
	}

	@Override
	public void run() {
		running.set(true);
		timer.schedule(task, 0, 100);
	}
}

